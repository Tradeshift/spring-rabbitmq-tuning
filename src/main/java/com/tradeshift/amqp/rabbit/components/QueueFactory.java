package com.tradeshift.amqp.rabbit.components;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import com.tradeshift.amqp.rabbit.properties.TunedRabbitProperties;

public class QueueFactory {

    private static final String DIRECT = "direct";
    private static final String FANOUT = "fanout";
    private static final String TOPIC = "topic";

    private TunedRabbitProperties properties;
    private RabbitAdmin rabbitAdmin;

    public QueueFactory(TunedRabbitProperties properties, RabbitAdmin rabbitAdmin) {
        this.properties = properties;
        this.rabbitAdmin = rabbitAdmin;
    }

    public void create() {
        Exchange exchange = new TopicExchange(properties.getExchange(), true, false);

        if (isADirectExchange(properties)) {
            exchange = new DirectExchange(properties.getExchange(), true, false);
        } else if (isAFanoutExchange(properties)) {
            exchange = new FanoutExchange(properties.getExchange(), true, false);
        }

        final Queue queue = QueueBuilder.durable(properties.getQueue()).build();
        rabbitAdmin.declareExchange(exchange);
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(properties.getQueueRoutingKey()).noargs());

        if (properties.isAutoCreateForRetryDlq() && isAValidExchange(properties)) {
            final Queue dlq = QueueBuilder.durable(properties.getQueueDlq()).build();
            final Queue retry = QueueBuilder.durable(properties.getQueueRetry())
                    .withArgument("x-dead-letter-exchange", properties.getExchange())
                    .withArgument("x-dead-letter-routing-key", properties.getQueueRoutingKey())
                    .build();
            rabbitAdmin.declareQueue(dlq);
            rabbitAdmin.declareQueue(retry);
            rabbitAdmin.declareBinding(BindingBuilder.bind(retry).to(exchange).with(properties.getQueueRetry()).noargs());
            rabbitAdmin.declareBinding(BindingBuilder.bind(dlq).to(exchange).with(properties.getQueueDlq()).noargs());
        }
    }

    private boolean isAValidExchange(TunedRabbitProperties properties) {
        return isADirectExchange(properties) || isATopicExchange(properties);
    }

    private boolean isATopicExchange(TunedRabbitProperties properties) {
        return TOPIC.equals(properties.getExchangeType());
    }

    private boolean isADirectExchange(TunedRabbitProperties properties) {
        return DIRECT.equals(properties.getExchangeType());
    }

    private boolean isAFanoutExchange(TunedRabbitProperties properties) {
        return FANOUT.equals(properties.getExchangeType());
    }
}
