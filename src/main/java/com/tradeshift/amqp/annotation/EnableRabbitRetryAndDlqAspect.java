package com.tradeshift.amqp.annotation;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import com.tradeshift.amqp.rabbit.properties.TunedRabbitProperties;
import com.tradeshift.amqp.rabbit.retry.QueueRetryComponent;
import com.tradeshift.amqp.rabbit.properties.TunedRabbitPropertiesMap;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
public class EnableRabbitRetryAndDlqAspect {

    private static final Logger log = LoggerFactory.getLogger(EnableRabbitRetryAndDlqAspect.class);

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
        String queueProperty = annotation.event();
        List<Class> exceptions = Arrays.asList(annotation.exceptions());
        Message message = (Message) joinPoint.getArgs()[0];
        try {
            joinPoint.proceed();
        } catch (Exception e) {
            log.info("The listener [{}.{}] threw an exception: {}", method.getDeclaringClass().getSimpleName(), method.getName(), e.getMessage());
            if (exceptions.contains(Exception.class) || exceptions.contains(e.getClass())) {
                TunedRabbitProperties properties = rabbitCustomPropertiesMap.get(queueProperty);
                if (Objects.isNull(properties)) {
                    throw new NoSuchBeanDefinitionException(String.format("Any bean with name %s was found", queueProperty));
                }
                queueRetryComponent.sendToRetryOrDlq(message, properties);
            }
        }
    }

    private Method getMethod(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }
}
