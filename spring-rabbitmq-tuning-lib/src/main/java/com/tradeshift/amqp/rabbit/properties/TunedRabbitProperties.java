package com.tradeshift.amqp.rabbit.properties;

import java.util.Objects;

import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.core.io.Resource;

public class TunedRabbitProperties extends RabbitProperties {

    private String eventName;
    private Integer ttlRetryMessage = 5000;
    private Integer maxRetriesAttempts = 3;
    private Integer ttlMultiply = 0;
    private String queueRoutingKey;
    private String exchange;
    private String exchangeType;
    private String queue;
    private String queueRetry;
    private String queueDlq;
    private boolean defaultRetryDlq = true;
    private boolean autoCreate;
    private boolean autoCreateForRetryDlq = true;
    private boolean automaticRecovery;
    private Integer concurrentConsumers = 1;
    private Integer maxConcurrentConsumers = 1;
    private Resource tlsKeystoreLocation;
    private String tlsKeystorePassword;
    private boolean primary;
    private boolean sslConnection;
    private boolean autoCreateOnlyForTest;
    private boolean enableJsonMessageConverter;
    private boolean enableSnakeCaseForQueuesAndExchangeNames;
    private String rabbitTemplateBeanName;

    public TunedRabbitProperties() {
        //Do nothing because this is a empty constructor
    }

    public String getQueueRetry() {
        return validateSnakeCase(defaultRetryDlq && Objects.isNull(queueRetry) ? this.queue + ".retry" : queueRetry);
    }

    public String getQueueDlq() {
        return validateSnakeCase(defaultRetryDlq && Objects.isNull(queueDlq) ? this.queue + ".dlq" : queueDlq);
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Integer getTtlRetryMessage() {
        return ttlRetryMessage;
    }

    public void setTtlRetryMessage(Integer ttlRetryMessage) {
        this.ttlRetryMessage = ttlRetryMessage;
    }

    public Integer getMaxRetriesAttempts() {
        return maxRetriesAttempts;
    }

    public void setMaxRetriesAttempts(Integer maxRetriesAttempts) {
        this.maxRetriesAttempts = maxRetriesAttempts;
    }

    public String getQueueRoutingKey() {
        return queueRoutingKey;
    }

    public void setQueueRoutingKey(String queueRoutingKey) {
        this.queueRoutingKey = queueRoutingKey;
    }

    public String getExchange() {
        return validateSnakeCase(exchange);
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(String exchangeType) {
        this.exchangeType = exchangeType;
    }

    public String getQueue() {
        return validateSnakeCase(queue);
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public void setQueueRetry(String queueRetry) {
        this.queueRetry = queueRetry;
    }

    public void setQueueDlq(String queueDlq) {
        this.queueDlq = queueDlq;
    }

    public boolean isDefaultRetryDlq() {
        return defaultRetryDlq;
    }

    public void setDefaultRetryDlq(boolean defaultRetryDlq) {
        this.defaultRetryDlq = defaultRetryDlq;
    }

    public boolean isAutoCreate() {
        return autoCreate;
    }

    public void setAutoCreate(boolean autoCreate) {
        this.autoCreate = autoCreate;
    }

    public boolean isAutomaticRecovery() {
        return automaticRecovery;
    }

    public void setAutomaticRecovery(boolean automaticRecovery) {
        this.automaticRecovery = automaticRecovery;
    }

    public Integer getConcurrentConsumers() {
        return concurrentConsumers;
    }

    public void setConcurrentConsumers(Integer concurrentConsumers) {
        this.concurrentConsumers = concurrentConsumers;
    }

    public Integer getMaxConcurrentConsumers() {
        return maxConcurrentConsumers;
    }

    public void setMaxConcurrentConsumers(Integer maxConcurrentConsumers) {
        this.maxConcurrentConsumers = maxConcurrentConsumers;
    }

    public Resource getTlsKeystoreLocation() {
        return tlsKeystoreLocation;
    }

    public void setTlsKeystoreLocation(Resource tlsKeystoreLocation) {
        this.tlsKeystoreLocation = tlsKeystoreLocation;
    }

    public String getTlsKeystorePassword() {
        return tlsKeystorePassword;
    }

    public void setTlsKeystorePassword(String tlsKeystorePassword) {
        this.tlsKeystorePassword = tlsKeystorePassword;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public boolean isSslConnection() {
        return sslConnection;
    }

    public void setSslConnection(boolean sslConnection) {
        this.sslConnection = sslConnection;
    }

    public boolean isAutoCreateOnlyForTest() {
        return autoCreateOnlyForTest;
    }

    public void setAutoCreateOnlyForTest(boolean autoCreateOnlyForTest) {
        this.autoCreateOnlyForTest = autoCreateOnlyForTest;
    }

    public Integer getTtlMultiply() {
        return ttlMultiply;
    }

    public void setTtlMultiply(Integer ttlMultiply) {
        this.ttlMultiply = ttlMultiply;
    }

    public boolean isEnableJsonMessageConverter() {
        return enableJsonMessageConverter;
    }

    public void setEnableJsonMessageConverter(boolean enableJsonMessageConverter) {
        this.enableJsonMessageConverter = enableJsonMessageConverter;
    }

    public boolean isEnableSnakeCaseForQueuesAndExchangeNames() {
        return enableSnakeCaseForQueuesAndExchangeNames;
    }

    public void setEnableSnakeCaseForQueuesAndExchangeNames(boolean enableSnakeCaseForQueuesAndExchangeNames) {
        this.enableSnakeCaseForQueuesAndExchangeNames = enableSnakeCaseForQueuesAndExchangeNames;
    }

    private String validateSnakeCase(String string) {
        return enableSnakeCaseForQueuesAndExchangeNames ? string.replace('.', '_') : string.replace('_', '.');
    }

    public boolean isAutoCreateForRetryDlq() {
        return autoCreateForRetryDlq;
    }

    public void setAutoCreateForRetryDlq(boolean autoCreateForRetryDlq) {
        this.autoCreateForRetryDlq = autoCreateForRetryDlq;
    }

    public String getRabbitTemplateBeanName() {
        return rabbitTemplateBeanName;
    }

    public void setRabbitTemplateBeanName(String rabbitTemplateBeanName) {
        this.rabbitTemplateBeanName = rabbitTemplateBeanName;
    }
}
