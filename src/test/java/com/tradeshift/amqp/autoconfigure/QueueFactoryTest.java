package com.tradeshift.amqp.autoconfigure;

import org.junit.Test;
import org.junit.Before;
import org.mockito.Mockito;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import com.tradeshift.amqp.rabbit.components.QueueFactory;
import com.tradeshift.amqp.rabbit.properties.TunedRabbitProperties;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

public class QueueFactoryTest {

    private TunedRabbitProperties queueProperties;

    @Before
    public void setUp(){
        queueProperties = new TunedRabbitProperties();
        queueProperties.setQueue("queue.test");
        queueProperties.setExchange("ex.test");
        queueProperties.setMaxRetriesAttempts(5);
        queueProperties.setQueueRoutingKey("routing.key.test");
        queueProperties.setTtlRetryMessage(3000);
        queueProperties.setPrimary(true);
        queueProperties.setVirtualHost("virtualHost");
        queueProperties.setUsername("username");
        queueProperties.setPassword("guest");
        queueProperties.setHost("host");
        queueProperties.setPort(12345);
        queueProperties.setSslConnection(false);
    }

    @Test
    public void should_validate_a_fanout_exchange_creation_without_dlq(){

        queueProperties.setExchangeType("fanout");

        RabbitAdmin rabbitAdminMock = Mockito.mock(RabbitAdmin.class);

        QueueFactory queueFactory = new QueueFactory(queueProperties, rabbitAdminMock);
        queueFactory.create();

        verify(rabbitAdminMock, times(1)).declareExchange(any(FanoutExchange.class));
        verify(rabbitAdminMock, times(1)).declareQueue(any(Queue.class));
        verify(rabbitAdminMock, times(1)).declareBinding(any(Binding.class));
    }

    @Test
    public void should_validate_a_topic_exchange_creation_with_dlq(){

        queueProperties.setExchangeType("topic");

        RabbitAdmin rabbitAdminMock = Mockito.mock(RabbitAdmin.class);

        QueueFactory queueFactory = new QueueFactory(queueProperties, rabbitAdminMock);
        queueFactory.create();

        verify(rabbitAdminMock, times(1)).declareExchange(any(TopicExchange.class));
        verify(rabbitAdminMock, times(3)).declareQueue(any(Queue.class));
        verify(rabbitAdminMock, times(3)).declareBinding(any(Binding.class));
    }

    @Test
    public void should_validate_a_direct_exchange_creation_with_dlq(){

        queueProperties.setExchangeType("direct");

        RabbitAdmin rabbitAdminMock = Mockito.mock(RabbitAdmin.class);

        QueueFactory queueFactory = new QueueFactory(queueProperties, rabbitAdminMock);
        queueFactory.create();

        verify(rabbitAdminMock, times(1)).declareExchange(any(DirectExchange.class));
        verify(rabbitAdminMock, times(3)).declareQueue(any(Queue.class));
        verify(rabbitAdminMock, times(3)).declareBinding(any(Binding.class));
    }

}