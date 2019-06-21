package com.tradeshift.amqp.rabbit.handlers;

import static org.junit.Assert.assertNotNull;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.tradeshift.amqp.autoconfigure.TunedRabbitAutoConfiguration;
import com.tradeshift.amqp.rabbit.properties.TunedRabbitProperties;
import com.tradeshift.amqp.rabbit.properties.TunedRabbitPropertiesMap;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RabbitTemplateHandlerTest {

    private TunedRabbitAutoConfiguration tradeshiftRabbitAutoConfiguration;

    @Autowired
    private GenericApplicationContext context;

    @Autowired
    private ConfigurableListableBeanFactory beanFactory;

    private RabbitTemplateHandler rabbitTemplateHandler;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() {
        initMocks(this);
        tradeshiftRabbitAutoConfiguration = new TunedRabbitAutoConfiguration(context, beanFactory);
    }

    @Test
    public void should_return_default_rabbit_template() {

        TunedRabbitPropertiesMap rabbitCustomPropertiesMap = new TunedRabbitPropertiesMap();
        rabbitCustomPropertiesMap.put("some-event", createQueueProperties(true, null));

        rabbitTemplateHandler = new RabbitTemplateHandler(context, rabbitCustomPropertiesMap);

        tradeshiftRabbitAutoConfiguration.routingConnectionFactory(rabbitCustomPropertiesMap);

        assertNotNull(rabbitTemplateHandler.getRabbitTemplate("some-event"));
    }

    @Test
    public void should_return_no_such_bean_definition_exception() {
        expectedException.expect(NoSuchBeanDefinitionException.class);
        expectedException.expectMessage("No bean available for property test");

        TunedRabbitPropertiesMap rabbitCustomPropertiesMap = new TunedRabbitPropertiesMap();
        rabbitCustomPropertiesMap.put("some-event", createQueueProperties(true, "test"));

        rabbitTemplateHandler = new RabbitTemplateHandler(context, rabbitCustomPropertiesMap);

        tradeshiftRabbitAutoConfiguration.routingConnectionFactory(rabbitCustomPropertiesMap);

        assertNotNull(rabbitTemplateHandler.getRabbitTemplate("test"));
    }

    @Test
    public void should_return_all_rabbit_templates() {

        TunedRabbitPropertiesMap rabbitCustomPropertiesMap = new TunedRabbitPropertiesMap();
        rabbitCustomPropertiesMap.put("some-event", createQueueProperties(true, null));
        rabbitCustomPropertiesMap.put("some-event2", createQueueProperties(false, "test"));

        rabbitTemplateHandler = new RabbitTemplateHandler(context, rabbitCustomPropertiesMap);

        tradeshiftRabbitAutoConfiguration.routingConnectionFactory(rabbitCustomPropertiesMap);

        assertNotNull(rabbitTemplateHandler.getRabbitTemplate("some-event"));
        assertNotNull(rabbitTemplateHandler.getRabbitTemplate("some-event2"));
    }

    private TunedRabbitProperties createQueueProperties(boolean primary, String virtualHost) {
        return createQueueProperties(primary, virtualHost, "guest", "localhost", 5672);
    }

    private TunedRabbitProperties createQueueProperties(boolean primary, String virtualHost, String username, String host, int port) {
        TunedRabbitProperties queueProperties = new TunedRabbitProperties();
        queueProperties.setQueue("queue.test");
        queueProperties.setExchange("ex.test");
        queueProperties.setExchangeType("topic");
        queueProperties.setMaxRetriesAttempts(5);
        queueProperties.setQueueRoutingKey("routing.key.test");
        queueProperties.setTtlRetryMessage(3000);
        queueProperties.setPrimary(primary);
        queueProperties.setVirtualHost(virtualHost);
        queueProperties.setUsername(username);
        queueProperties.setPassword("guest");
        queueProperties.setSslConnection(false);
        queueProperties.setHost(host);
        queueProperties.setPort(port);

        return queueProperties;
    }

}
