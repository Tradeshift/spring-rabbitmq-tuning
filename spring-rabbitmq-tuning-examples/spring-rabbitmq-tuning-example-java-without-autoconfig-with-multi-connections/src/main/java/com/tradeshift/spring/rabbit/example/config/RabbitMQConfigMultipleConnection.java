package com.tradeshift.spring.rabbit.example.config;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

import com.rabbitmq.client.ConnectionFactory;

@Configuration
@EnableRabbit
public class RabbitMQConfigMultipleConnection {

    @Bean
    @Primary
    public RabbitTemplate rabbitTemplateDefault() {
        RabbitTemplate template = new RabbitTemplate(getConnectionFactoryDefault());
        template.setMessageConverter(producerJackson2MessageConverterDefault());

        RetryTemplate retry = new RetryTemplate();
        retry.setBackOffPolicy(new ExponentialBackOffPolicy());

        template.setRetryTemplate(retry);
        return template;
    }

    @Bean
    @Primary
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactoryDefault() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(getConnectionFactoryDefault());
        factory.setMessageConverter(producerJackson2MessageConverterDefault());
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        return factory;
    }

    @Bean
    @Primary
    public CachingConnectionFactory getConnectionFactoryDefault() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        return new CachingConnectionFactory(factory);
    }

    @Bean
    @Primary
    public MessageConverter producerJackson2MessageConverterDefault() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplateTS() {
        RabbitTemplate template = new RabbitTemplate(getConnectionFactoryTS());
        template.setMessageConverter(producerJackson2MessageConverterTS());

        RetryTemplate retry = new RetryTemplate();
        retry.setBackOffPolicy(new ExponentialBackOffPolicy());

        template.setRetryTemplate(retry);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactoryTS() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(getConnectionFactoryTS());
        factory.setMessageConverter(producerJackson2MessageConverterTS());
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        return factory;
    }

    @Bean
    public CachingConnectionFactory getConnectionFactoryTS() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("tradeshift");
        return new CachingConnectionFactory(factory);
    }

    @Bean
    public MessageConverter producerJackson2MessageConverterTS() {
        return new Jackson2JsonMessageConverter();
    }
}
