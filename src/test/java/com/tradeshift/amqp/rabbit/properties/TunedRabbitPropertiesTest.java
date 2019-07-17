package com.tradeshift.amqp.rabbit.properties;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TunedRabbitPropertiesTest {

    @Test
    public void should_return_retry_and_dlq_default_names_correctly() {
        String queueName = "queue.test.default";
        TunedRabbitProperties queueProperties = createQueueProperties(queueName, true, false);
        assertEquals(queueName + ".retry", queueProperties.getQueueRetry());
        assertEquals(queueName + ".dlq", queueProperties.getQueueDlq());
    }

    @Test
    public void should_return_retry_and_dlq_names_correctly() {
        String queueName = "queue.test.default";
        String specificRetryName = "queue.retry";
        String specificDlqName = "queue.dlq";
        TunedRabbitProperties queueProperties = createQueueProperties(queueName, false, false);
        queueProperties.setQueueRetry(specificRetryName);
        queueProperties.setQueueDlq(specificDlqName);
        assertEquals(specificRetryName, queueProperties.getQueueRetry());
        assertEquals(specificDlqName, queueProperties.getQueueDlq());
    }

    @Test
    public void should_return_retry_and_dlq_names_correctly_even_with_default_flag_enable() {
        String queueName = "queue.test.default";
        String specificRetryName = "queue.retry";
        String specificDlqName = "queue.dlq";
        TunedRabbitProperties queueProperties = createQueueProperties(queueName, true, false);
        queueProperties.setQueueRetry(specificRetryName);
        queueProperties.setQueueDlq(specificDlqName);
        assertEquals(specificRetryName, queueProperties.getQueueRetry());
        assertEquals(specificDlqName, queueProperties.getQueueDlq());
    }

    @Test
    public void should_return_retry_and_dlq_names_correctly_with_snake_case() {
        String queueName = "queue_test_default";
        String ex = "ex_exhange";
        TunedRabbitProperties queueProperties = createQueueProperties(ex, queueName, true, true);
        assertEquals(queueName + "_retry", queueProperties.getQueueRetry());
        assertEquals(queueName + "_dlq", queueProperties.getQueueDlq());
        assertEquals(ex, queueProperties.getExchange());
    }

    @Test
    public void should_return_retry_and_dlq_names_correctly_without_snake_case() {
        String queueName = "queue_test_default";
        String ex = "ex_exhange";
        TunedRabbitProperties queueProperties = createQueueProperties(ex, queueName, true, false);
        assertEquals(queueName.replace("_", ".") + ".retry", queueProperties.getQueueRetry());
        assertEquals(queueName.replace("_", ".") + ".dlq", queueProperties.getQueueDlq());
        assertEquals(ex.replace("_", "."), queueProperties.getExchange());
    }

    private TunedRabbitProperties createQueueProperties(String queue, boolean defaultRetryDlq, boolean enableSnakeCaseForQueuesAndExchangeNames) {
        return createQueueProperties("ex.test", queue, defaultRetryDlq, enableSnakeCaseForQueuesAndExchangeNames);
    }

    private TunedRabbitProperties createQueueProperties(String exchange, String queue, boolean defaultRetryDlq, boolean enableSnakeCaseForQueuesAndExchangeNames) {
        TunedRabbitProperties queueProperties = new TunedRabbitProperties();
        queueProperties.setQueue(queue);
        queueProperties.setExchange(exchange);
        queueProperties.setExchangeType("topic");
        queueProperties.setMaxRetriesAttempts(5);
        queueProperties.setQueueRoutingKey("routing.key.test");
        queueProperties.setTtlRetryMessage(3000);
        queueProperties.setPrimary(true);
        queueProperties.setVirtualHost("some");
        queueProperties.setUsername("guest");
        queueProperties.setPassword("guest");
        queueProperties.setDefaultRetryDlq(defaultRetryDlq);
        queueProperties.setSslConnection(false);
        queueProperties.setEnableSnakeCaseForQueuesAndExchangeNames(enableSnakeCaseForQueuesAndExchangeNames);

        return queueProperties;
    }

}
