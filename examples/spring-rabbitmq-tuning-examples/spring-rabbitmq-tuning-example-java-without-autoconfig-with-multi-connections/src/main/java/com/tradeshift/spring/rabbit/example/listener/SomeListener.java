package com.tradeshift.spring.rabbit.example.listener;

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.tradeshift.amqp.annotation.EnableRabbitRetryAndDlq;

@Component
public class SomeListener {

    private static final Logger log = LoggerFactory.getLogger(SomeListener.class);

    @RabbitListener(containerFactory = "rabbitListenerContainerFactoryTS", queues = "${spring.rabbitmq.custom.some-event.queue}")
    @EnableRabbitRetryAndDlq(event = "some-event")
    public void onMessage(Message message) {
        process(message);
    }

    @RabbitListener(containerFactory = "rabbitListenerContainerFactoryDefault", queues = "${spring.rabbitmq.custom.another-event.queue}")
    @EnableRabbitRetryAndDlq(event = "another-event", retryWhen = { IllegalArgumentException.class, RuntimeException.class })
    public void onMessageAnotherListener(Message message) {
        process(message);
    }

    private void process(Message message) {
        String messageStr = new String(message.getBody(), Charset.defaultCharset());

        if ("dlq".equals(messageStr.replace("\"", ""))) {
            throw new RuntimeException("to dead-letter");
        }

        log.info("message = [{}]", messageStr);
    }
}
