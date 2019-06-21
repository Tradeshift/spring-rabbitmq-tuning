package com.tradeshift.amqp.rabbit.handlers;

import java.util.Objects;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import com.tradeshift.amqp.rabbit.properties.TunedRabbitPropertiesMap;
import com.tradeshift.amqp.resolvers.RabbitBeanNameResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.tradeshift.amqp.rabbit.properties.TunedRabbitProperties;

@Component
public class RabbitTemplateHandler {

    private final ApplicationContext context;
    private final TunedRabbitPropertiesMap rabbitCustomPropertiesMap;

    @Autowired
    public RabbitTemplateHandler(ApplicationContext context, TunedRabbitPropertiesMap rabbitCustomPropertiesMap) {
        this.context = context;
        this.rabbitCustomPropertiesMap = rabbitCustomPropertiesMap;
    }

    public RabbitTemplate getRabbitTemplate(String eventName) {
        TunedRabbitProperties customRabbitProperties = rabbitCustomPropertiesMap.get(eventName);
        if (Objects.isNull(customRabbitProperties)) {
            throw new NoSuchBeanDefinitionException("No bean available for property " + eventName);
        }
        return getRabbitTemplate(customRabbitProperties);
    }

    public RabbitTemplate getRabbitTemplate(TunedRabbitProperties customRabbitProperties) {
        String beanName = RabbitBeanNameResolver.getRabbitTemplateBeanName(customRabbitProperties);
        return (RabbitTemplate) context.getBean(beanName);
    }
}
