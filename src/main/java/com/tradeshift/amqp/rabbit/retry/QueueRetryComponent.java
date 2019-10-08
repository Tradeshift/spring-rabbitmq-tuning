package com.tradeshift.amqp.rabbit.retry;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tradeshift.amqp.annotation.EnableRabbitRetryAndDlqAspect;
import com.tradeshift.amqp.log.TunedLogger;
import com.tradeshift.amqp.rabbit.handlers.RabbitTemplateHandler;
import com.tradeshift.amqp.rabbit.properties.TunedRabbitProperties;

@Component
public class QueueRetryComponent {

    private static final TunedLogger log = TunedLogger.init(QueueRetryComponent.class);

    private static final String X_DEATH = "x-death";

    private final RabbitTemplateHandler rabbitTemplateHandler;

    @Autowired
    public QueueRetryComponent(RabbitTemplateHandler rabbitTemplateHandler) {
        this.rabbitTemplateHandler = rabbitTemplateHandler;
    }

    public void sendToRetryOrDlq(final Message message, final TunedRabbitProperties properties) {
        final Integer qtyRetry = countDeath(message);

        if (qtyRetry > properties.getMaxRetriesAttempts()) {
            sendToDlq(message, properties);
        } else {
            sendToRetry(message, properties, qtyRetry);
        }
    }

    public void sendToRetry(final Message message, final TunedRabbitProperties properties, final Integer qtdRetry) {
        message.getMessageProperties()
                .setExpiration(String.valueOf(calculateTtl(properties.getTtlRetryMessage(), qtdRetry, properties.getTtlMultiply())));
        rabbitTemplateHandler.getRabbitTemplate(properties).send(properties.getExchange(), properties.getQueueRetry(), message);
        log.info(properties, "M=sendToRetry, Message={}", message);
    }

    public void sendToDlq(final Message message, final TunedRabbitProperties properties) {
        message.getMessageProperties().getHeaders().remove(X_DEATH);
        rabbitTemplateHandler.getRabbitTemplate(properties).send(properties.getExchange(), properties.getQueueDlq(), message);
        log.info(properties, "M=sendToDlq, Message={}", message);
    }

    public int countDeath(final Message message) {
        int count = 0;
        final Map<String, Object> headers = message.getMessageProperties().getHeaders();
        if (headers.containsKey(X_DEATH)) {
            count = Integer.parseInt(getXDeath(headers).get("count").toString());
        }
        return ++count;
    }

    protected Map getXDeath(final Map<String, Object> headers) {
        final List list = (List) Collections.singletonList(headers.get(X_DEATH)).get(0);
        return (Map) list.get(0);
    }

    public int calculateTtl(Integer ttlRetry, Integer qtdRetry, Integer ttlMultiply) {
        final AtomicInteger expiration = new AtomicInteger(ttlRetry);
        if (!ttlMultiply.equals(0) && qtdRetry > 1) {
            IntStream.range(1, qtdRetry).forEach(value -> expiration.set(expiration.get() * ttlMultiply));
        }
        return expiration.get();
    }

}
