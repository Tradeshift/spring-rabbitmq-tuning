package com.tradeshift.amqp.rabbit.handlers;

import java.util.Objects;

import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import com.tradeshift.amqp.rabbit.properties.TunedRabbitPropertiesMap;
import com.tradeshift.amqp.resolvers.RabbitBeanNameResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.tradeshift.amqp.rabbit.properties.TunedRabbitProperties;

@Component
public class RabbitAdminHandler {

    private final ApplicationContext context;
    private final TunedRabbitPropertiesMap rabbitCustomPropertiesMap;

    @Autowired
    public RabbitAdminHandler(ApplicationContext context, TunedRabbitPropertiesMap rabbitCustomPropertiesMap) {
        this.context = context;
        this.rabbitCustomPropertiesMap = rabbitCustomPropertiesMap;
    }

    public RabbitAdmin getRabbitAdmin(String eventName) {
        TunedRabbitProperties customRabbitProperties = rabbitCustomPropertiesMap.get(eventName);
        if (Objects.isNull(customRabbitProperties)) {
            throw new NoSuchBeanDefinitionException("No bean available for property " + eventName);
        }
        return getRabbitAdmin(customRabbitProperties);
    }

    public RabbitAdmin getRabbitAdmin(TunedRabbitProperties customRabbitProperties) {
        String beanName = RabbitBeanNameResolver.getRabbitAdminBeanName(customRabbitProperties);
        return (RabbitAdmin) context.getBean(beanName);
    }

    public RabbitAdmin getRabbitAdminByHostAndPortAndVirtualHost(String virtualHostHostAndPort) {
        String beanName = RabbitBeanNameResolver.getRabbitAdminBeanName(virtualHostHostAndPort, false);
        return (RabbitAdmin) context.getBean(beanName);
    }
}
