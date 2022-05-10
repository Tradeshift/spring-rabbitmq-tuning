package com.tradeshift.amqp.rabbit.retry;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.tradeshift.amqp.rabbit.handlers.RabbitTemplateHandler;
import com.tradeshift.amqp.rabbit.properties.TunedRabbitProperties;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class QueueRetryComponentTest {

    @InjectMocks
    @Spy
    private QueueRetryComponent queueRetryComponent;

    @Mock
    private RabbitTemplateHandler rabbitTemplateHandler;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void should_return_1_death() {
        MessageProperties messageProperties = createMessageProperties(1);
        Message message = new Message("some".getBytes(), messageProperties);
        int countDeath = queueRetryComponent.countDeath(message);
        Assert.assertEquals(1, countDeath);
    }

    @Test
    public void should_return_2_death() {
        MessageProperties messageProperties = createMessageProperties(2);
        Message message = new Message("some".getBytes(), messageProperties);
        int countDeath = queueRetryComponent.countDeath(message);
        Assert.assertEquals(2, countDeath);
    }

    @Test
    public void should_return_3_death() {
        MessageProperties messageProperties = createMessageProperties(3);
        Message message = new Message("some".getBytes(), messageProperties);
        int countDeath = queueRetryComponent.countDeath(message);
        Assert.assertEquals(3, countDeath);
    }

    @Test
    public void should_calculate_the_ttl_based_on_ttl_multiply_with_first_retry() {
        int correctTtl = queueRetryComponent.calculateTtl(5000, 1, 2);
        Assert.assertEquals(5000, correctTtl);
    }

    @Test
    public void should_calculate_the_ttl_based_on_ttl_multiply_with_second_retry() {
        int correctTtl = queueRetryComponent.calculateTtl(5000, 2, 2);
        Assert.assertEquals(10000, correctTtl);
    }

    @Test
    public void should_calculate_the_ttl_based_on_ttl_multiply_with_third_retry() {
        int correctTtl = queueRetryComponent.calculateTtl(5000, 3, 2);
        Assert.assertEquals(20000, correctTtl);
    }

    @Test
    public void should_calculate_the_ttl_based_on_ttl_multiply_with_fifty_second_retry() {
        int correctTtl = queueRetryComponent.calculateTtl(5000, 10, 2);
        Assert.assertEquals(2560000, correctTtl);
    }

    @Test
    public void should_call_send_to_dlq_with_correct_params() {
        when(rabbitTemplateHandler.getRabbitTemplate(Mockito.any(TunedRabbitProperties.class))).thenReturn(rabbitTemplate);
        doNothing().when(rabbitTemplate).send(Mockito.any(), Mockito.any(), Mockito.any(Message.class));

        MessageProperties messageProperties = createMessageProperties(3);
        Message message = new Message("some".getBytes(), messageProperties);
        TunedRabbitProperties queueProperties = createQueueProperties();

        queueRetryComponent.sendToDlq(message, queueProperties);

        verify(rabbitTemplate).send(ArgumentMatchers.eq(queueProperties.getExchange()), ArgumentMatchers.eq(queueProperties.getQueueDlq()), ArgumentMatchers.eq(message));
        Assert.assertFalse(messageProperties.getHeaders().containsKey("x-death"));
    }

    @Test
    public void should_call_send_to_retry_with_correct_params_without_ttl_message() {
        when(rabbitTemplateHandler.getRabbitTemplate(Mockito.any(TunedRabbitProperties.class))).thenReturn(rabbitTemplate);
        doNothing().when(rabbitTemplate).send(Mockito.any(), Mockito.any(), Mockito.any(Message.class));

        int numberOfDeaths = 3;
        MessageProperties messageProperties = createMessageProperties(numberOfDeaths);
        Message message = new Message("some".getBytes(), messageProperties);

        TunedRabbitProperties queueProperties = createQueueProperties(2, 5);

        queueRetryComponent.sendToRetry(message, queueProperties, numberOfDeaths);

        verify(rabbitTemplate).send(ArgumentMatchers.eq(queueProperties.getExchange()), ArgumentMatchers.eq(queueProperties.getQueueRetry()), ArgumentMatchers.eq(message));
        Assert.assertEquals("20000", messageProperties.getExpiration());
    }

    @Test
    public void should_call_send_to_retry_and_sent_to_dlq_based_on_max_retries() {
        when(rabbitTemplateHandler.getRabbitTemplate(Mockito.any(TunedRabbitProperties.class))).thenReturn(rabbitTemplate);
        doNothing().when(rabbitTemplate).send(Mockito.any(), Mockito.any(), Mockito.any(Message.class));

        int maxRetry = 5;
        TunedRabbitProperties queueProperties = createQueueProperties(2, maxRetry);

        IntStream.range(1, maxRetry + 2).forEach(index -> {
            Message message = new Message("some".getBytes(), createMessageProperties(index));
            queueRetryComponent.sendToRetryOrDlq(message, queueProperties);
        });

        verify(queueRetryComponent, times(maxRetry)).sendToRetry(ArgumentMatchers.any(Message.class), ArgumentMatchers.eq(queueProperties), ArgumentMatchers.anyInt());
        verify(queueRetryComponent, times(1)).sendToDlq(ArgumentMatchers.any(Message.class), ArgumentMatchers.eq(queueProperties));
    }

    @Test
    public void should_call_send_to_retry_and_sent_to_dlq_based_on_max_retries_again() {
        when(rabbitTemplateHandler.getRabbitTemplate(Mockito.any(TunedRabbitProperties.class))).thenReturn(rabbitTemplate);
        doNothing().when(rabbitTemplate).send(Mockito.any(), Mockito.any(), Mockito.any(Message.class));

        int maxRetry = 10;
        TunedRabbitProperties queueProperties = createQueueProperties(2, maxRetry);

        IntStream.range(1, maxRetry + 2).forEach(index -> {
            Message message = new Message("some".getBytes(), createMessageProperties(index));
            queueRetryComponent.sendToRetryOrDlq(message, queueProperties);
        });

        verify(queueRetryComponent, times(maxRetry)).sendToRetry(ArgumentMatchers.any(Message.class), ArgumentMatchers.eq(queueProperties), ArgumentMatchers.anyInt());
        verify(queueRetryComponent, times(1)).sendToDlq(ArgumentMatchers.any(Message.class), ArgumentMatchers.eq(queueProperties));
    }

    private MessageProperties createMessageProperties(Integer numberOfDeaths) {
        MessageProperties messageProperties = new MessageProperties();
        HashMap<String, Integer> map = new HashMap<>();
        IntStream.range(0, numberOfDeaths).forEach(value -> map.put("count", value));
        messageProperties.getHeaders().put("x-death", Collections.singletonList(map));
        return messageProperties;
    }

    private TunedRabbitProperties createQueueProperties() {
        return createQueueProperties(0, 1);
    }

    private TunedRabbitProperties createQueueProperties(Integer ttlMultiply, Integer maxRetry) {
        TunedRabbitProperties queueProperties = new TunedRabbitProperties();
        queueProperties.setQueue("queue.test");
        queueProperties.setExchange("ex.test");
        queueProperties.setExchangeType("topic");
        queueProperties.setMaxRetriesAttempts(5);
        queueProperties.setQueueRoutingKey("routing.key.test");
        queueProperties.setTtlRetryMessage(5000);
        queueProperties.setPrimary(true);
        queueProperties.setVirtualHost("vh");
        queueProperties.setUsername("guest");
        queueProperties.setPassword("guest");
        queueProperties.setHost("host");
        queueProperties.setPort(5672);
        queueProperties.setSslConnection(false);
        queueProperties.setTtlMultiply(ttlMultiply);
        queueProperties.setMaxRetriesAttempts(maxRetry);
        return queueProperties;
    }

}
