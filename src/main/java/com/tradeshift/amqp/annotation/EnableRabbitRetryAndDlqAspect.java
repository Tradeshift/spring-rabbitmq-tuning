package com.tradeshift.amqp.annotation;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.tradeshift.amqp.log.TunedLogger;
import com.tradeshift.amqp.rabbit.properties.TunedRabbitProperties;
import com.tradeshift.amqp.rabbit.properties.TunedRabbitPropertiesMap;
import com.tradeshift.amqp.rabbit.retry.QueueRetryComponent;

@Aspect
@Configuration
public class EnableRabbitRetryAndDlqAspect {

    private static final TunedLogger log = TunedLogger.init(EnableRabbitRetryAndDlqAspect.class);

    private final QueueRetryComponent queueRetryComponent;
    private final TunedRabbitPropertiesMap rabbitCustomPropertiesMap;

    @Autowired
    public EnableRabbitRetryAndDlqAspect(QueueRetryComponent queueRetryComponent, TunedRabbitPropertiesMap rabbitCustomPropertiesMap) {
        this.queueRetryComponent = queueRetryComponent;
        this.rabbitCustomPropertiesMap = rabbitCustomPropertiesMap;
    }

    @Around("com.tradeshift.amqp.annotation.CommonJoinPointConfig.enableRabbitRetryAndDlqAnnotation()")
    public void validateMessage(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = getMethod(joinPoint);
        EnableRabbitRetryAndDlq annotation = method.getAnnotation(EnableRabbitRetryAndDlq.class);
        TunedRabbitProperties properties = getPropertiesByAnnotationEvent(annotation);
        try {
            joinPoint.proceed();
        } catch (Exception e) {
            handleExceptionUsingEventDefinitions(properties, annotation, e, joinPoint);
        }
    }

    private void handleExceptionUsingEventDefinitions(TunedRabbitProperties properties,
                                                      EnableRabbitRetryAndDlq annotation,
                                                      Exception exceptionThrown,
                                                      ProceedingJoinPoint joinPoint) {
        Method method = getMethod(joinPoint);
        log.info(properties, "The listener [{}.{}] threw an exception: {}",
                method.getDeclaringClass().getSimpleName(),
                method.getName(), exceptionThrown.getMessage());

        if (shouldDiscard(annotation, exceptionThrown)) {
            log.warn(properties, "Exception {} was parametrized to be discarded", exceptionThrown.getClass().getSimpleName());
        } else if (shouldSentDirectToDlq(annotation, exceptionThrown)) {
            sendMessageToDlq(joinPoint, annotation);
        } else if (shouldSentToRetry(annotation, exceptionThrown)) {
            sendMessageToRetry(joinPoint, annotation);
        } else {
            log.error(properties, "Discarding message after exception {}: {}", exceptionThrown.getClass().getSimpleName(), exceptionThrown.getMessage());
        }
    }

    private Method getMethod(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }

    private boolean shouldDiscard(EnableRabbitRetryAndDlq annotation, Exception exceptionThrown) {
        if (annotation.discardWhen().length > 0) {
            return checkIfContainsException(annotation, annotation.discardWhen(), exceptionThrown);
        }
        return false;
    }

    private boolean shouldSentDirectToDlq(EnableRabbitRetryAndDlq annotation, Exception exceptionThrown) {
        if (annotation.directToDlqWhen().length > 0) {
            return checkIfContainsException(annotation, annotation.directToDlqWhen(), exceptionThrown);
        }

        return false;
    }

    private boolean shouldSentToRetry(EnableRabbitRetryAndDlq annotation, Exception exceptionThrown) {
        return checkIfContainsException(annotation, annotation.retryWhen(), exceptionThrown);
    }

    private boolean checkIfContainsException(EnableRabbitRetryAndDlq annotation, Class<?>[] acceptableExceptions, Exception exceptionThrown) {
        if (acceptableExceptions.length == 0) {
            return false;
        }

        List<Class<?>> exceptions = Arrays.asList(acceptableExceptions);
        if (annotation.checkInheritance()) {
            return exceptions.stream()
                    .anyMatch(type -> type.isAssignableFrom(exceptionThrown.getClass()));
        }
        return exceptions.contains(exceptionThrown.getClass());
    }

    private void sendMessageToRetry(ProceedingJoinPoint joinPoint, EnableRabbitRetryAndDlq annotation) {
        TunedRabbitProperties properties = getPropertiesByAnnotationEvent(annotation);
        Message message = (Message) joinPoint.getArgs()[0];
        queueRetryComponent.sendToRetryOrDlq(message, properties);
    }

    private void sendMessageToDlq(ProceedingJoinPoint joinPoint, EnableRabbitRetryAndDlq annotation) {
        TunedRabbitProperties properties = getPropertiesByAnnotationEvent(annotation);
        Message message = (Message) joinPoint.getArgs()[0];
        queueRetryComponent.sendToDlq(message, properties);
    }

    private TunedRabbitProperties getPropertiesByAnnotationEvent(EnableRabbitRetryAndDlq annotation) {
        String queueProperty = annotation.event();
        TunedRabbitProperties properties = rabbitCustomPropertiesMap.get(queueProperty);
        if (Objects.isNull(properties)) {
            throw new NoSuchBeanDefinitionException(String.format("Any bean with name %s was found", queueProperty));
        }
        return properties;
    }

}
