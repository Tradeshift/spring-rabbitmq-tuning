package com.tradeshift.amqp.rabbit.annotation;

import java.lang.reflect.Method;

import org.springframework.amqp.core.AbstractExchange;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.RabbitListenerAnnotationBeanPostProcessor;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.tradeshift.amqp.annotation.TunedRabbitListener;
import com.tradeshift.amqp.rabbit.handlers.RabbitAdminHandler;
import com.tradeshift.amqp.rabbit.properties.TunedRabbitProperties;
import com.tradeshift.amqp.rabbit.properties.TunedRabbitPropertiesMap;
import com.tradeshift.amqp.resolvers.RabbitBeanNameResolver;

public class TunedRabbitListenerAnnotationBeanPostProcessor
        extends RabbitListenerAnnotationBeanPostProcessor
        implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    protected void processAmqpListener(RabbitListener rabbitListener, Method method, Object bean, String beanName) {
        TunedRabbitPropertiesMap tunedRabbitPropertiesMap = applicationContext.getBean(TunedRabbitPropertiesMap.class);
        TunedRabbitProperties tunedRabbitProperties = tunedRabbitPropertiesMap.get(rabbitListener.containerFactory());

        TunedRabbitListener tunedRabbitListener = new TunedRabbitListener(rabbitListener);
        tunedRabbitListener.setContainerFactory(RabbitBeanNameResolver.getSimpleRabbitListenerContainerFactoryBean(tunedRabbitProperties));
        super.processAmqpListener(tunedRabbitListener, method, bean, beanName);
        enhanceBeansWithReferenceToRabbitAdmin(tunedRabbitProperties);
    }

    private void enhanceBeansWithReferenceToRabbitAdmin(TunedRabbitProperties tunedRabbitProperties) {
        RabbitAdmin rabbitAdmin = getRabbitAdminBean(tunedRabbitProperties);

        applicationContext.getBeansOfType(AbstractExchange.class).values().stream()
                .filter(this::isNotProcessed)
                .forEach(exchange -> exchange.setAdminsThatShouldDeclare(rabbitAdmin != null ? rabbitAdmin : this));

        applicationContext.getBeansOfType(Queue.class).values().stream()
                .filter(this::isNotProcessed)
                .forEach(queue -> queue.setAdminsThatShouldDeclare(rabbitAdmin != null ? rabbitAdmin : this));

        applicationContext.getBeansOfType(Binding.class).values().stream()
                .filter(this::isNotProcessed)
                .forEach(binding -> binding.setAdminsThatShouldDeclare(rabbitAdmin != null ? rabbitAdmin : this));
    }

    private RabbitAdmin getRabbitAdminBean(TunedRabbitProperties tunedRabbitProperties) {
        return ((RabbitAdminHandler) applicationContext.getBean("rabbitAdminHandler")).getRabbitAdmin(tunedRabbitProperties);
    }

    private boolean isNotProcessed(Declarable declarable) {
        return declarable.getDeclaringAdmins() == null
                || (declarable.getDeclaringAdmins().stream().noneMatch(item -> item == this)
                && declarable.getDeclaringAdmins().stream().noneMatch(item -> item instanceof RabbitAdmin));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

}
