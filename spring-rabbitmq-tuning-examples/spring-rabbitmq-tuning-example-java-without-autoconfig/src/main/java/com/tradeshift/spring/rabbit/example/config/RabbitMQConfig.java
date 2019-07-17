package com.tradeshift.spring.rabbit.example.config;

import java.io.IOException;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

import com.rabbitmq.client.ConnectionFactory;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    @Bean
    public RabbitTemplate rabbitTemplate() throws IOException {
        RabbitTemplate template = new RabbitTemplate(getConnectionFactory());
        template.setMessageConverter(producerJackson2MessageConverter());

        RetryTemplate retry = new RetryTemplate();
        retry.setBackOffPolicy(new ExponentialBackOffPolicy());

        template.setRetryTemplate(retry);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() throws IOException {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(getConnectionFactory());
        factory.setMessageConverter(producerJackson2MessageConverter());
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        return factory;
    }

    @Bean
    public CachingConnectionFactory getConnectionFactory() throws IOException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        return new CachingConnectionFactory(factory);
    }

    @Bean
    public MessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
