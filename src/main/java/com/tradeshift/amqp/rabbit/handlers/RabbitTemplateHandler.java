package com.tradeshift.amqp.rabbit.handlers;

import java.util.Map;
import java.util.Objects;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;

import com.tradeshift.amqp.rabbit.properties.TunedRabbitPropertiesMap;
import com.tradeshift.amqp.resolvers.RabbitBeanNameResolver;

import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
        String property = context.getEnvironment().getProperty("spring.rabbitmq.enable.custom.autoconfiguration");
        if (Objects.nonNull(property) && !Boolean.getBoolean(property)) {
            Map<String, RabbitTemplate> beansOfType = context.getBeansOfType(RabbitTemplate.class);
            if (beansOfType.size() == 0) {
                throw new NoSuchBeanDefinitionException("No RabbitTemplate bean available. Are you sure that you want to disable the autoconfiguration?");
            } else if (beansOfType.size() > 1) {
                if (StringUtils.isEmpty(customRabbitProperties.getRabbitTemplateBeanName())) {
                    throw new BeanDefinitionValidationException("There are more than 1 RabbitTemplate available. You need to specify the name of the RabbitTemplate that we will use for this event");
                } else {
                    return (RabbitTemplate) context.getBean(customRabbitProperties.getRabbitTemplateBeanName());
                }
            } else {
                return beansOfType.entrySet().iterator().next().getValue();
            }
        } else {
            String beanName = RabbitBeanNameResolver.getRabbitTemplateBeanName(customRabbitProperties);
            return (RabbitTemplate) context.getBean(beanName);
        }
    }
}
