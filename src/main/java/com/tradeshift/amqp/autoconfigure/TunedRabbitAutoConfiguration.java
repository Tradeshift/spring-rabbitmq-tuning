package com.tradeshift.amqp.autoconfigure;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultSaslConfig;
import com.tradeshift.amqp.annotation.EnableRabbitRetryAndDlqAspect;
import com.tradeshift.amqp.constants.TunedRabbitConstants;
import com.tradeshift.amqp.rabbit.annotation.TunedRabbitListenerAnnotationBeanPostProcessor;
import com.tradeshift.amqp.rabbit.handlers.RabbitAdminHandler;
import com.tradeshift.amqp.rabbit.handlers.RabbitTemplateHandler;
import com.tradeshift.amqp.rabbit.properties.TunedRabbitProperties;
import com.tradeshift.amqp.rabbit.properties.TunedRabbitPropertiesMap;
import com.tradeshift.amqp.rabbit.retry.QueueRetryComponent;
import com.tradeshift.amqp.resolvers.RabbitBeanNameResolver;
import com.tradeshift.amqp.ssl.TLSContextUtil;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerAnnotationBeanPostProcessor;
import org.springframework.amqp.rabbit.config.RabbitListenerConfigUtils;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.SimpleRoutingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@EnableConfigurationProperties(TunedRabbitPropertiesMap.class)
@Configuration
@ConditionalOnClass({RabbitTemplate.class, Channel.class})
@AutoConfigureBefore(RabbitAutoConfiguration.class)
@Import({TunedRabbitAutoConfiguration.RabbitPostProcessorConfiguration.class})
public class TunedRabbitAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(TunedRabbitAutoConfiguration.class);
    private Set<String> virtualHosts = new HashSet<>();
    private Set<String> portAndHost = new HashSet<>();

    private final ApplicationContext applicationContext;
    private final ConfigurableListableBeanFactory beanFactory;

    @Autowired
    public TunedRabbitAutoConfiguration(ApplicationContext applicationContext, ConfigurableListableBeanFactory beanFactory) {
        this.applicationContext = applicationContext;
        this.beanFactory = beanFactory;
    }

    @ConditionalOnProperty(
            value = "spring.rabbitmq.enable.custom.autoconfiguration",
            havingValue = "true",
            matchIfMissing = true)
    @Configuration
    static class RabbitPostProcessorConfiguration {
        @Bean(name = RabbitListenerConfigUtils.RABBIT_LISTENER_ANNOTATION_PROCESSOR_BEAN_NAME)
        @DependsOn(TunedRabbitConstants.CONNECTION_FACTORY_BEAN_NAME)
        public static RabbitListenerAnnotationBeanPostProcessor rabbitListenerAnnotationProcessor() {
            return new TunedRabbitListenerAnnotationBeanPostProcessor();
        }
    }

    @ConditionalOnProperty(
            value = "spring.rabbitmq.enable.custom.autoconfiguration",
            havingValue = "true",
            matchIfMissing = true)
    @Configuration
    @EnableRabbit
    static class EnableRabbitConfiguration {
        @Bean(name = RabbitListenerConfigUtils.RABBIT_LISTENER_ANNOTATION_PROCESSOR_BEAN_NAME)
        public static RabbitListenerAnnotationBeanPostProcessor rabbitListenerAnnotationProcessor() {
            return new TunedRabbitListenerAnnotationBeanPostProcessor();
        }
    }

    @Bean
    @ConditionalOnProperty(
            value = "spring.rabbitmq.enable.custom.autoconfiguration",
            havingValue = "true",
            matchIfMissing = true)
    @DependsOn(TunedRabbitConstants.CONNECTION_FACTORY_BEAN_NAME)
    public RabbitTemplateHandler rabbitTemplateHandler(TunedRabbitPropertiesMap rabbitCustomPropertiesMap) {
        return new RabbitTemplateHandler(applicationContext, rabbitCustomPropertiesMap);
    }

    @Bean
    @ConditionalOnProperty(
            value = "spring.rabbitmq.enable.custom.autoconfiguration",
            havingValue = "true",
            matchIfMissing = true)
    @DependsOn(TunedRabbitConstants.CONNECTION_FACTORY_BEAN_NAME)
    public RabbitAdminHandler rabbitAdminHandler(TunedRabbitPropertiesMap rabbitCustomPropertiesMap) {
        return new RabbitAdminHandler(applicationContext, rabbitCustomPropertiesMap);
    }

    @Bean("rabbitTemplateHandler")
    @ConditionalOnProperty(
            value = "spring.rabbitmq.enable.custom.autoconfiguration",
            havingValue = "false")
    public RabbitTemplateHandler rabbitTemplateHandlerWithoutAutoConfig(TunedRabbitPropertiesMap rabbitCustomPropertiesMap) {
        return new RabbitTemplateHandler(applicationContext, rabbitCustomPropertiesMap);
    }

    @Bean("rabbitAdminHandler")
    @ConditionalOnProperty(
            value = "spring.rabbitmq.enable.custom.autoconfiguration",
            havingValue = "false")
    public RabbitAdminHandler rabbitAdminHandlerWithoutAutoConfig(TunedRabbitPropertiesMap rabbitCustomPropertiesMap) {
        return new RabbitAdminHandler(applicationContext, rabbitCustomPropertiesMap);
    }

    @Bean
    @DependsOn("rabbitTemplateHandler")
    public QueueRetryComponent queueRetryComponent(TunedRabbitPropertiesMap rabbitCustomPropertiesMap) {
        return new QueueRetryComponent(rabbitTemplateHandler(rabbitCustomPropertiesMap));
    }

    @Bean
    @DependsOn("queueRetryComponent")
    public EnableRabbitRetryAndDlqAspect enableRabbitRetryAndDlqAspect(TunedRabbitPropertiesMap rabbitCustomPropertiesMap) {
        return new EnableRabbitRetryAndDlqAspect(queueRetryComponent(rabbitCustomPropertiesMap), rabbitCustomPropertiesMap);
    }

    @ConditionalOnProperty(
            value = "spring.rabbitmq.enable.custom.autoconfiguration",
            havingValue = "true",
            matchIfMissing = true)
    @Bean
    public MessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @ConditionalOnProperty(
            value = "spring.rabbitmq.enable.custom.autoconfiguration",
            havingValue = "true",
            matchIfMissing = true)
    @Primary
    @Bean(TunedRabbitConstants.CONNECTION_FACTORY_BEAN_NAME)
    @DependsOn("producerJackson2MessageConverter")
    public ConnectionFactory routingConnectionFactory(TunedRabbitPropertiesMap rabbitCustomPropertiesMap) {
        validateSinglePrimaryConnection(rabbitCustomPropertiesMap);

        AtomicReference<ConnectionFactory> defaultConnectionFactory = new AtomicReference<>();

        HashMap<Object, ConnectionFactory> connectionFactoryHashMap = new HashMap<>();
        rabbitCustomPropertiesMap.forEach((eventName, properties) -> {
            properties.setEventName(eventName);
            ConnectionFactory connectionFactory = createRabbitMQArch(properties);

            connectionFactoryHashMap.put(
                    RabbitBeanNameResolver.getConnectionFactoryBeanName(properties.getVirtualHost(), properties.getHost(), properties.getPort()),
                    connectionFactory
            );

            if (properties.isPrimary()) {
                defaultConnectionFactory.set(connectionFactory);
            }
        });

        if (Objects.isNull(defaultConnectionFactory.get())) {
            Optional<ConnectionFactory> first = connectionFactoryHashMap.values().stream().findFirst();
            first.ifPresent(defaultConnectionFactory::set);
        }

        SimpleRoutingConnectionFactory connectionFactory = new SimpleRoutingConnectionFactory();
        connectionFactory.setTargetConnectionFactories(connectionFactoryHashMap);
        connectionFactory.setDefaultTargetConnectionFactory(defaultConnectionFactory.get());
        return connectionFactory;
    }

    private void validateSinglePrimaryConnection(TunedRabbitPropertiesMap rabbitCustomPropertiesMap) {
        long primary = rabbitCustomPropertiesMap.stream()
                .filter(stringQueuePropertiesEntry -> stringQueuePropertiesEntry.getValue().isPrimary())
                .count();

        if (primary > 1) {
            throw new IllegalArgumentException("Only one primary RabbitMQ architecture is allowed!");
        }
    }

    private ConnectionFactory createRabbitMQArch(final TunedRabbitProperties property) {
        final String virtualHost = RabbitBeanNameResolver.treatVirtualHostName(property.getVirtualHost());

        if (!portAndHost.contains(property.getPort() + property.getHost())) {
            applyAutoConfiguration(property);
        } else if (!virtualHosts.contains(virtualHost)) {
            applyAutoConfiguration(property);
        }

        return (CachingConnectionFactory) applicationContext.getBean(RabbitBeanNameResolver
                .getConnectionFactoryBeanName(property.getVirtualHost(), property.getHost(), property.getPort()));
    }

    private void applyAutoConfiguration(final TunedRabbitProperties property) {
        final String virtualHost = RabbitBeanNameResolver.treatVirtualHostName(property.getVirtualHost());
        CachingConnectionFactory connectionsFactoryBean = createConnectionsFactoryBean(property, virtualHost);
        Optional.ofNullable(connectionsFactoryBean).ifPresent(connectionFactory -> {
            String connectionFactoryBeanName = RabbitBeanNameResolver.getConnectionFactoryBeanName(virtualHost, property.getHost(), property.getPort());
            beanFactory.registerSingleton(connectionFactoryBeanName, connectionFactory);
            log.info("ConnectionFactory Bean with name {} was created for the event {} and virtual host {}",
                    connectionFactoryBeanName, property.getEventName(), virtualHost);

            String listenerContainerFactoryBeanName = RabbitBeanNameResolver.getSimpleRabbitListenerContainerFactoryBean(virtualHost, property.getHost(), property.getPort());
            SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactoryBeanDef = createSimpleRabbitListenerContainerFactoryBean(property, connectionFactory);
            beanFactory.registerSingleton(listenerContainerFactoryBeanName, simpleRabbitListenerContainerFactoryBeanDef);
            log.info("SimpleRabbitListenerContainerFactory Bean with name {} was created for the event {} and virtual host {}",
                    listenerContainerFactoryBeanName, property.getEventName(), virtualHost);

            RabbitAdmin beanDefinitionRabbitAdmin = createRabbitAdminBean(connectionFactory);
            String rabbitAdminBeanName = RabbitBeanNameResolver.getRabbitAdminBeanName(virtualHost, property.getHost(), property.getPort());
            beanFactory.registerSingleton(rabbitAdminBeanName, beanDefinitionRabbitAdmin);
            log.info("RabbitAdmin Bean with name {} was created for the event {} and virtual host {}",
                    rabbitAdminBeanName, property.getEventName(), virtualHost);

            RabbitTemplate beanDefinitionRabbitTemplate = createRabbitTemplateBean(connectionFactory, property);
            String rabbitTemplateBeanName = RabbitBeanNameResolver.getRabbitTemplateBeanName(virtualHost, property.getHost(), property.getPort());
            beanFactory.registerSingleton(rabbitTemplateBeanName, beanDefinitionRabbitTemplate);
            log.info("RabbitTemplate Bean with name {} was created for the event {} and virtual host {}",
                    rabbitTemplateBeanName, property.getEventName(), virtualHost);
            virtualHosts.add(virtualHost);
            portAndHost.add(property.getPort() + property.getHost());

            if (property.isAutoCreate() || (property.isAutoCreateOnlyForTest() && isTestProfile())) {
                autoCreateQueues(property, beanDefinitionRabbitAdmin);
            }
        });
    }

    private CachingConnectionFactory createConnectionsFactoryBean(final TunedRabbitProperties property, String virtualHost) {
        try {
            com.rabbitmq.client.ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();
            if (!property.isSslConnection()) {
                factory.setUsername(property.getUsername());
                factory.setPassword(property.getPassword());
            } else {
                factory.setSaslConfig(DefaultSaslConfig.EXTERNAL);
                factory.useSslProtocol(TLSContextUtil.tls12ContextFromPKCS12(property.getTlsKeystoreLocation().getInputStream(),
                        property.getTlsKeystorePassword().toCharArray()));
            }

            factory.setHost(property.getHost());
            factory.setPort(property.getPort());
            factory.setAutomaticRecoveryEnabled(property.isAutomaticRecovery());
            Optional.ofNullable(property.getVirtualHost()).ifPresent(factory::setVirtualHost);

            return new CachingConnectionFactory(factory);
        } catch (Exception e) {
            log.error(String.format("It is not possible create a Connection Factory to Virtual Host %s", virtualHost), e);
            return null;
        }
    }

    private SimpleRabbitListenerContainerFactory createSimpleRabbitListenerContainerFactoryBean(
            final TunedRabbitProperties property, CachingConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = new SimpleRabbitListenerContainerFactory();
        simpleRabbitListenerContainerFactory.setConnectionFactory(connectionFactory);
        simpleRabbitListenerContainerFactory.setConcurrentConsumers(property.getConcurrentConsumers());
        simpleRabbitListenerContainerFactory.setMaxConcurrentConsumers(property.getMaxConcurrentConsumers());
        if (property.isEnableJsonMessageConverter()) {
            simpleRabbitListenerContainerFactory.setMessageConverter(producerJackson2MessageConverter());
        } else {
            simpleRabbitListenerContainerFactory.setMessageConverter(new SimpleMessageConverter());
        }
        return simpleRabbitListenerContainerFactory;
    }

    private RabbitAdmin createRabbitAdminBean(CachingConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    private RabbitTemplate createRabbitTemplateBean(CachingConnectionFactory connectionFactory, final TunedRabbitProperties property) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        if (property.isEnableJsonMessageConverter()) {
            rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        }
        return rabbitTemplate;
    }

    private void autoCreateQueues(TunedRabbitProperties properties, RabbitAdmin rabbitAdmin) {
        Exchange exchange = new TopicExchange(properties.getExchange(), true, false);

        if ("direct".equals(properties.getExchangeType())) {
            exchange = new DirectExchange(properties.getExchange(), true, false);
        } else if ("fanout".equals(properties.getExchangeType())) {
            exchange = new FanoutExchange(properties.getExchange(), true, false);
        }

        final Queue queue = QueueBuilder.durable(properties.getQueue()).build();
        rabbitAdmin.declareExchange(exchange);
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(properties.getQueueRoutingKey()).noargs());

        if (properties.isAutoCreateForRetryDlq()) {
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

    private boolean isTestProfile() {
        return Arrays.asList(applicationContext.getEnvironment().getActiveProfiles()).contains("test");
    }

}
