package com.tradeshift.amqp.rabbit.properties;

import java.util.Objects;
import org.springframework.core.io.Resource;

public class TunedRabbitProperties {
    private boolean clusterMode = false;

    private String hosts = null;

    private String host = "localhost";

    private int port = 5672;

    private String username = "guest";

    private String password = "guest";

    private String virtualHost;

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

    private boolean enableLogs = true;

    public TunedRabbitProperties() {
        // Do nothing because this is a empty constructor
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

    public void setEventName(final String eventName) {
        this.eventName = eventName;
    }

    public Integer getTtlRetryMessage() {
        return ttlRetryMessage;
    }

    public void setTtlRetryMessage(final Integer ttlRetryMessage) {
        this.ttlRetryMessage = ttlRetryMessage;
    }

    public Integer getMaxRetriesAttempts() {
        return maxRetriesAttempts;
    }

    public void setMaxRetriesAttempts(final Integer maxRetriesAttempts) {
        this.maxRetriesAttempts = maxRetriesAttempts;
    }

    public String getQueueRoutingKey() {
        return queueRoutingKey;
    }

    public void setQueueRoutingKey(final String queueRoutingKey) {
        this.queueRoutingKey = queueRoutingKey;
    }

    public String getExchange() {
        return validateSnakeCase(exchange);
    }

    public void setExchange(final String exchange) {
        this.exchange = exchange;
    }

    public String getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(final String exchangeType) {
        this.exchangeType = exchangeType;
    }

    public String getQueue() {
        return validateSnakeCase(queue);
    }

    public void setQueue(final String queue) {
        this.queue = queue;
    }

    public void setQueueRetry(final String queueRetry) {
        this.queueRetry = queueRetry;
    }

    public void setQueueDlq(final String queueDlq) {
        this.queueDlq = queueDlq;
    }

    public boolean isDefaultRetryDlq() {
        return defaultRetryDlq;
    }

    public void setDefaultRetryDlq(final boolean defaultRetryDlq) {
        this.defaultRetryDlq = defaultRetryDlq;
    }

    public boolean isAutoCreate() {
        return autoCreate;
    }

    public void setAutoCreate(final boolean autoCreate) {
        this.autoCreate = autoCreate;
    }

    public boolean isAutomaticRecovery() {
        return automaticRecovery;
    }

    public void setAutomaticRecovery(final boolean automaticRecovery) {
        this.automaticRecovery = automaticRecovery;
    }

    public Integer getConcurrentConsumers() {
        return concurrentConsumers;
    }

    public void setConcurrentConsumers(final Integer concurrentConsumers) {
        this.concurrentConsumers = concurrentConsumers;
    }

    public Integer getMaxConcurrentConsumers() {
        return maxConcurrentConsumers;
    }

    public void setMaxConcurrentConsumers(final Integer maxConcurrentConsumers) {
        this.maxConcurrentConsumers = maxConcurrentConsumers;
    }

    public Resource getTlsKeystoreLocation() {
        return tlsKeystoreLocation;
    }

    public void setTlsKeystoreLocation(final Resource tlsKeystoreLocation) {
        this.tlsKeystoreLocation = tlsKeystoreLocation;
    }

    public String getTlsKeystorePassword() {
        return tlsKeystorePassword;
    }

    public void setTlsKeystorePassword(final String tlsKeystorePassword) {
        this.tlsKeystorePassword = tlsKeystorePassword;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(final boolean primary) {
        this.primary = primary;
    }

    public boolean isSslConnection() {
        return sslConnection;
    }

    public void setSslConnection(final boolean sslConnection) {
        this.sslConnection = sslConnection;
    }

    public boolean isAutoCreateOnlyForTest() {
        return autoCreateOnlyForTest;
    }

    public void setAutoCreateOnlyForTest(final boolean autoCreateOnlyForTest) {
        this.autoCreateOnlyForTest = autoCreateOnlyForTest;
    }

    public Integer getTtlMultiply() {
        return ttlMultiply;
    }

    public void setTtlMultiply(final Integer ttlMultiply) {
        this.ttlMultiply = ttlMultiply;
    }

    public boolean isEnableJsonMessageConverter() {
        return enableJsonMessageConverter;
    }

    public void setEnableJsonMessageConverter(final boolean enableJsonMessageConverter) {
        this.enableJsonMessageConverter = enableJsonMessageConverter;
    }

    public boolean isEnableSnakeCaseForQueuesAndExchangeNames() {
        return enableSnakeCaseForQueuesAndExchangeNames;
    }

    public void setEnableSnakeCaseForQueuesAndExchangeNames(final boolean enableSnakeCaseForQueuesAndExchangeNames) {
        this.enableSnakeCaseForQueuesAndExchangeNames = enableSnakeCaseForQueuesAndExchangeNames;
    }

    public boolean isAutoCreateForRetryDlq() {
        return autoCreateForRetryDlq;
    }

    public void setAutoCreateForRetryDlq(final boolean autoCreateForRetryDlq) {
        this.autoCreateForRetryDlq = autoCreateForRetryDlq;
    }

    public String getRabbitTemplateBeanName() {
        return rabbitTemplateBeanName;
    }

    public void setRabbitTemplateBeanName(final String rabbitTemplateBeanName) {
        this.rabbitTemplateBeanName = rabbitTemplateBeanName;
    }

    public boolean isClusterMode() {
        return clusterMode;
    }

    public void setClusterMode(boolean clusterMode) {
        this.clusterMode = clusterMode;
    }

    public String getHosts() {
        return hosts;
    }

    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(final String virtualHost) {
        this.virtualHost = virtualHost;
    }

    private String validateSnakeCase(final String string) {
        return enableSnakeCaseForQueuesAndExchangeNames ? string.replace('.', '_') : string.replace('_', '.');
    }

    public boolean isEnableLogs() {
        return enableLogs;
    }

    public void setEnableLogs(boolean enableLogs) {
        this.enableLogs = enableLogs;
    }
}
