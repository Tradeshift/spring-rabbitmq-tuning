package com.tradeshift.amqp.autoconfigure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.MockitoAnnotations.initMocks;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractMessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.tradeshift.amqp.rabbit.components.RabbitComponentsFactory;
import com.tradeshift.amqp.rabbit.properties.TunedRabbitProperties;
import com.tradeshift.amqp.rabbit.properties.TunedRabbitPropertiesMap;
import com.tradeshift.amqp.resolvers.RabbitBeanNameResolver;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TunedRabbitAutoConfigurationTest {

    private TunedRabbitAutoConfiguration tradeshiftRabbitAutoConfiguration;

    @Autowired
    private GenericApplicationContext context;

    @Autowired
    private ConfigurableListableBeanFactory beanFactory;
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() {
        initMocks(this);
        tradeshiftRabbitAutoConfiguration = new TunedRabbitAutoConfiguration(context, beanFactory, new RabbitComponentsFactory());
    }

    @Test
    public void should_create_all_beans_for_rabbitmq_architecture() {

        TunedRabbitPropertiesMap rabbitCustomPropertiesMap = new TunedRabbitPropertiesMap();
        TunedRabbitProperties queueProperties = createQueueProperties(true);
        rabbitCustomPropertiesMap.put("some-event", queueProperties);

        tradeshiftRabbitAutoConfiguration.routingConnectionFactory(rabbitCustomPropertiesMap);

        CachingConnectionFactory connectionFactory = (CachingConnectionFactory) context.getBean(RabbitBeanNameResolver.getConnectionFactoryBeanNameForDefaultVirtualHost(queueProperties));
        RabbitTemplate rabbitTemplate = (RabbitTemplate) context.getBean(RabbitBeanNameResolver.getRabbitTemplateBeanNameForDefaultVirtualHost(queueProperties));
        RabbitAdmin rabbitAdmin = (RabbitAdmin) context.getBean(RabbitBeanNameResolver.getRabbitAdminBeanNameForDefaultVirtualHost(queueProperties));
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory =
                (SimpleRabbitListenerContainerFactory) context.getBean(RabbitBeanNameResolver.getSimpleRabbitListenerContainerFactoryBeanForDefaultVirtualHost(queueProperties));

        assertNotNull(connectionFactory);
        assertNotNull(rabbitTemplate);
        assertNotNull(rabbitAdmin);
        assertNotNull(simpleRabbitListenerContainerFactory);

        assertEquals("/", connectionFactory.getVirtualHost());
        assertEquals("localhost", connectionFactory.getHost());
        assertEquals(5672, connectionFactory.getPort());
        assertEquals("guest", connectionFactory.getUsername());

        assertEquals(1, context.getBeansOfType(CachingConnectionFactory.class).size());
        assertEquals(1, context.getBeansOfType(RabbitTemplate.class).size());
        assertEquals(1, context.getBeansOfType(RabbitAdmin.class).size());
        assertEquals(1, context.getBeansOfType(SimpleRabbitListenerContainerFactory.class).size());
    }

    @Test
    public void should_create_all_beans_for_rabbitmq_architecture_using_json_message_converter() throws IllegalAccessException {
        TunedRabbitPropertiesMap rabbitCustomPropertiesMap = new TunedRabbitPropertiesMap();
        TunedRabbitProperties queueProperties = createQueueProperties(true, null, true);
        rabbitCustomPropertiesMap.put("some-event", queueProperties);

        tradeshiftRabbitAutoConfiguration.routingConnectionFactory(rabbitCustomPropertiesMap);

        CachingConnectionFactory connectionFactory = (CachingConnectionFactory) context.getBean(RabbitBeanNameResolver.getConnectionFactoryBeanNameForDefaultVirtualHost(queueProperties));
        RabbitTemplate rabbitTemplate = (RabbitTemplate) context.getBean(RabbitBeanNameResolver.getRabbitTemplateBeanNameForDefaultVirtualHost(queueProperties));
        RabbitAdmin rabbitAdmin = (RabbitAdmin) context.getBean(RabbitBeanNameResolver.getRabbitAdminBeanNameForDefaultVirtualHost(queueProperties));
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory =
                (SimpleRabbitListenerContainerFactory) context.getBean(RabbitBeanNameResolver.getSimpleRabbitListenerContainerFactoryBeanForDefaultVirtualHost(queueProperties));

        assertNotNull(connectionFactory);
        assertNotNull(rabbitTemplate);
        assertNotNull(rabbitAdmin);
        assertNotNull(simpleRabbitListenerContainerFactory);

        assertEquals(Jackson2JsonMessageConverter.class, rabbitTemplate.getMessageConverter().getClass());
        assertEquals(Jackson2JsonMessageConverter.class, getMessageConverter(simpleRabbitListenerContainerFactory).getClass());
    }

    @Test
    public void should_create_all_beans_for_rabbitmq_architecture_using_default_message_converter() throws IllegalAccessException {

        TunedRabbitPropertiesMap rabbitCustomPropertiesMap = new TunedRabbitPropertiesMap();
        TunedRabbitProperties queueProperties = createQueueProperties(true);
        rabbitCustomPropertiesMap.put("some-event", queueProperties);

        tradeshiftRabbitAutoConfiguration.routingConnectionFactory(rabbitCustomPropertiesMap);

        CachingConnectionFactory connectionFactory = (CachingConnectionFactory) context.getBean(RabbitBeanNameResolver.getConnectionFactoryBeanNameForDefaultVirtualHost(queueProperties));
        RabbitTemplate rabbitTemplate = (RabbitTemplate) context.getBean(RabbitBeanNameResolver.getRabbitTemplateBeanNameForDefaultVirtualHost(queueProperties));
        RabbitAdmin rabbitAdmin = (RabbitAdmin) context.getBean(RabbitBeanNameResolver.getRabbitAdminBeanNameForDefaultVirtualHost(queueProperties));
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory =
                (SimpleRabbitListenerContainerFactory) context.getBean(RabbitBeanNameResolver.getSimpleRabbitListenerContainerFactoryBeanForDefaultVirtualHost(queueProperties));

        assertNotNull(connectionFactory);
        assertNotNull(rabbitTemplate);
        assertNotNull(rabbitAdmin);
        assertNotNull(simpleRabbitListenerContainerFactory);

        assertEquals(SimpleMessageConverter.class, rabbitTemplate.getMessageConverter().getClass());
        assertEquals(SimpleMessageConverter.class, getMessageConverter(simpleRabbitListenerContainerFactory).getClass());
    }

    @Test
    public void should_return_an_excp_because_there_are_2_primaries_definitions() {

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Only one primary RabbitMQ architecture is allowed!");

        TunedRabbitPropertiesMap rabbitCustomPropertiesMap = new TunedRabbitPropertiesMap();
        rabbitCustomPropertiesMap.put("some-event", createQueueProperties(true));
        rabbitCustomPropertiesMap.put("some-event2", createQueueProperties(true));

        tradeshiftRabbitAutoConfiguration.routingConnectionFactory(rabbitCustomPropertiesMap);
    }

    @Test
    public void should_create_2_connectionFactories_and_all_other_beans_for_different_hosts_different_port_and_different_virtual_host() {

        String anotherVirtualHost = "test";
        String anotherUsername = "anotherUsername";
        String anotherHost = "anotherHost";
        int anotherPort = 5670;

        TunedRabbitPropertiesMap rabbitCustomPropertiesMap = new TunedRabbitPropertiesMap();
        TunedRabbitProperties queuePropertiesSomeEvent = createQueueProperties(true);
        rabbitCustomPropertiesMap.put("some-event", queuePropertiesSomeEvent);

        TunedRabbitProperties queuePropertiesAnotherEvent = createQueueProperties(false, anotherVirtualHost, anotherUsername, false, anotherHost, anotherPort);
        rabbitCustomPropertiesMap.put("some-event2", queuePropertiesAnotherEvent);

        tradeshiftRabbitAutoConfiguration.routingConnectionFactory(rabbitCustomPropertiesMap);

        CachingConnectionFactory connectionFactoryForDefaultVH = (CachingConnectionFactory) context.getBean(RabbitBeanNameResolver.getConnectionFactoryBeanNameForDefaultVirtualHost(queuePropertiesSomeEvent));
        CachingConnectionFactory connectionFactoryForAnotherVH = (CachingConnectionFactory) context.getBean(RabbitBeanNameResolver.getConnectionFactoryBeanName(queuePropertiesAnotherEvent));

        assertNotNull(connectionFactoryForDefaultVH);
        assertNotNull(context.getBean(RabbitBeanNameResolver.getRabbitTemplateBeanNameForDefaultVirtualHost(queuePropertiesSomeEvent)));
        assertNotNull(context.getBean(RabbitBeanNameResolver.getRabbitAdminBeanNameForDefaultVirtualHost(queuePropertiesSomeEvent)));
        assertNotNull(context.getBean(RabbitBeanNameResolver.getSimpleRabbitListenerContainerFactoryBeanForDefaultVirtualHost(queuePropertiesSomeEvent)));

        assertNotNull(connectionFactoryForAnotherVH);
        assertNotNull(context.getBean(RabbitBeanNameResolver.getRabbitTemplateBeanName(queuePropertiesAnotherEvent)));
        assertNotNull(context.getBean(RabbitBeanNameResolver.getRabbitAdminBeanName(queuePropertiesAnotherEvent)));
        assertNotNull(context.getBean(RabbitBeanNameResolver.getSimpleRabbitListenerContainerFactoryBean(queuePropertiesAnotherEvent)));

        assertEquals("/", connectionFactoryForDefaultVH.getVirtualHost());
        assertEquals("localhost", connectionFactoryForDefaultVH.getHost());
        assertEquals(5672, connectionFactoryForDefaultVH.getPort());
        assertEquals("guest", connectionFactoryForDefaultVH.getUsername());

        assertEquals(anotherVirtualHost, connectionFactoryForAnotherVH.getVirtualHost());
        assertEquals(anotherHost, connectionFactoryForAnotherVH.getHost());
        assertEquals(anotherPort, connectionFactoryForAnotherVH.getPort());
        assertEquals(anotherUsername, connectionFactoryForAnotherVH.getUsername());

        assertEquals(2, context.getBeansOfType(CachingConnectionFactory.class).size());
        assertEquals(2, context.getBeansOfType(RabbitTemplate.class).size());
        assertEquals(2, context.getBeansOfType(RabbitAdmin.class).size());
        assertEquals(2, context.getBeansOfType(SimpleRabbitListenerContainerFactory.class).size());
    }

    @Test
    public void should_create_2_connectionFactories_and_all_other_beans_for_different_hosts_same_port_and_different_virtual_host() {

        String anotherVirtualHost = "test";
        String anotherUsername = "anotherUsername";
        String anotherHost = "anotherHost";
        int samePort = 5672;

        TunedRabbitPropertiesMap rabbitCustomPropertiesMap = new TunedRabbitPropertiesMap();
        TunedRabbitProperties queuePropertiesSomeEvent = createQueueProperties(true, null, "guest", false, "localhost", samePort);
        rabbitCustomPropertiesMap.put("some-event", queuePropertiesSomeEvent);

        TunedRabbitProperties queuePropertiesAnotherEvent = createQueueProperties(false, anotherVirtualHost, anotherUsername, false, anotherHost, samePort);
        rabbitCustomPropertiesMap.put("some-event2", queuePropertiesAnotherEvent);

        tradeshiftRabbitAutoConfiguration.routingConnectionFactory(rabbitCustomPropertiesMap);

        CachingConnectionFactory connectionFactoryForDefaultVH = (CachingConnectionFactory) context.getBean(RabbitBeanNameResolver.getConnectionFactoryBeanNameForDefaultVirtualHost(queuePropertiesSomeEvent));
        CachingConnectionFactory connectionFactoryForAnotherVH = (CachingConnectionFactory) context.getBean(RabbitBeanNameResolver.getConnectionFactoryBeanName(queuePropertiesAnotherEvent));

        assertNotNull(connectionFactoryForDefaultVH);
        assertNotNull(context.getBean(RabbitBeanNameResolver.getRabbitTemplateBeanNameForDefaultVirtualHost(queuePropertiesSomeEvent)));
        assertNotNull(context.getBean(RabbitBeanNameResolver.getRabbitAdminBeanNameForDefaultVirtualHost(queuePropertiesSomeEvent)));
        assertNotNull(context.getBean(RabbitBeanNameResolver.getSimpleRabbitListenerContainerFactoryBeanForDefaultVirtualHost(queuePropertiesSomeEvent)));

        assertNotNull(connectionFactoryForAnotherVH);
        assertNotNull(context.getBean(RabbitBeanNameResolver.getRabbitTemplateBeanName(queuePropertiesAnotherEvent)));
        assertNotNull(context.getBean(RabbitBeanNameResolver.getRabbitAdminBeanName(queuePropertiesAnotherEvent)));
        assertNotNull(context.getBean(RabbitBeanNameResolver.getSimpleRabbitListenerContainerFactoryBean(queuePropertiesAnotherEvent)));

        assertEquals("/", connectionFactoryForDefaultVH.getVirtualHost());
        assertEquals("localhost", connectionFactoryForDefaultVH.getHost());
        assertEquals(samePort, connectionFactoryForDefaultVH.getPort());
        assertEquals("guest", connectionFactoryForDefaultVH.getUsername());

        assertEquals(anotherVirtualHost, connectionFactoryForAnotherVH.getVirtualHost());
        assertEquals(anotherHost, connectionFactoryForAnotherVH.getHost());
        assertEquals(samePort, connectionFactoryForAnotherVH.getPort());
        assertEquals(anotherUsername, connectionFactoryForAnotherVH.getUsername());

        assertEquals(2, context.getBeansOfType(CachingConnectionFactory.class).size());
        assertEquals(2, context.getBeansOfType(RabbitTemplate.class).size());
        assertEquals(2, context.getBeansOfType(RabbitAdmin.class).size());
        assertEquals(2, context.getBeansOfType(SimpleRabbitListenerContainerFactory.class).size());
    }

    @Test
    public void should_create_2_connectionFactories_and_all_other_beans_for_same_hosts_different_port_and_different_virtual_host() {

        String anotherVirtualHost = "test";
        String anotherUsername = "anotherUsername";
        String sameHost = "localhost";

        TunedRabbitPropertiesMap rabbitCustomPropertiesMap = new TunedRabbitPropertiesMap();
        TunedRabbitProperties queuePropertiesSomeEvent = createQueueProperties(true, null, "guest", false, sameHost, 5671);
        rabbitCustomPropertiesMap.put("some-event", queuePropertiesSomeEvent);

        TunedRabbitProperties queuePropertiesAnotherEvent = createQueueProperties(false, anotherVirtualHost, anotherUsername, false, sameHost, 5672);
        rabbitCustomPropertiesMap.put("some-event2", queuePropertiesAnotherEvent);

        tradeshiftRabbitAutoConfiguration.routingConnectionFactory(rabbitCustomPropertiesMap);

        CachingConnectionFactory connectionFactoryForDefaultVH = (CachingConnectionFactory) context.getBean(RabbitBeanNameResolver.getConnectionFactoryBeanNameForDefaultVirtualHost(queuePropertiesSomeEvent));
        CachingConnectionFactory connectionFactoryForAnotherVH = (CachingConnectionFactory) context.getBean(RabbitBeanNameResolver.getConnectionFactoryBeanName(queuePropertiesAnotherEvent));

        assertNotNull(connectionFactoryForDefaultVH);
        assertNotNull(context.getBean(RabbitBeanNameResolver.getRabbitTemplateBeanNameForDefaultVirtualHost(queuePropertiesSomeEvent)));
        assertNotNull(context.getBean(RabbitBeanNameResolver.getRabbitAdminBeanNameForDefaultVirtualHost(queuePropertiesSomeEvent)));
        assertNotNull(context.getBean(RabbitBeanNameResolver.getSimpleRabbitListenerContainerFactoryBeanForDefaultVirtualHost(queuePropertiesSomeEvent)));

        assertNotNull(connectionFactoryForAnotherVH);
        assertNotNull(context.getBean(RabbitBeanNameResolver.getRabbitTemplateBeanName(queuePropertiesAnotherEvent)));
        assertNotNull(context.getBean(RabbitBeanNameResolver.getRabbitAdminBeanName(queuePropertiesAnotherEvent)));
        assertNotNull(context.getBean(RabbitBeanNameResolver.getSimpleRabbitListenerContainerFactoryBean(queuePropertiesAnotherEvent)));

        assertEquals("/", connectionFactoryForDefaultVH.getVirtualHost());
        assertEquals("localhost", connectionFactoryForDefaultVH.getHost());
        assertEquals(5671, connectionFactoryForDefaultVH.getPort());
        assertEquals("guest", connectionFactoryForDefaultVH.getUsername());

        assertEquals(anotherVirtualHost, connectionFactoryForAnotherVH.getVirtualHost());
        assertEquals(sameHost, connectionFactoryForAnotherVH.getHost());
        assertEquals(5672, connectionFactoryForAnotherVH.getPort());
        assertEquals(anotherUsername, connectionFactoryForAnotherVH.getUsername());

        assertEquals(2, context.getBeansOfType(CachingConnectionFactory.class).size());
        assertEquals(2, context.getBeansOfType(RabbitTemplate.class).size());
        assertEquals(2, context.getBeansOfType(RabbitAdmin.class).size());
        assertEquals(2, context.getBeansOfType(SimpleRabbitListenerContainerFactory.class).size());
    }

    @Test
    public void should_create_2_connectionFactories_and_all_other_beans_for_same_hosts_different_port_and_same_virtual_host() {

        String sameVH = "test";
        String sameUsername = "guest";
        String sameHost = "localhost";

        TunedRabbitPropertiesMap rabbitCustomPropertiesMap = new TunedRabbitPropertiesMap();
        TunedRabbitProperties queuePropertiesSomeEvent = createQueueProperties(true, sameVH, sameUsername, false, sameHost, 5671);
        rabbitCustomPropertiesMap.put("some-event", queuePropertiesSomeEvent);

        TunedRabbitProperties queuePropertiesAnotherEvent = createQueueProperties(false, sameVH, sameUsername, false, sameHost, 5672);
        rabbitCustomPropertiesMap.put("some-event2", queuePropertiesAnotherEvent);

        tradeshiftRabbitAutoConfiguration.routingConnectionFactory(rabbitCustomPropertiesMap);

        CachingConnectionFactory connectionFactoryForDefaultVH = (CachingConnectionFactory) context.getBean(RabbitBeanNameResolver.getConnectionFactoryBeanName(queuePropertiesSomeEvent));
        CachingConnectionFactory connectionFactoryForAnotherVH = (CachingConnectionFactory) context.getBean(RabbitBeanNameResolver.getConnectionFactoryBeanName(queuePropertiesAnotherEvent));

        assertNotNull(connectionFactoryForDefaultVH);
        assertNotNull(context.getBean(RabbitBeanNameResolver.getRabbitTemplateBeanName(queuePropertiesSomeEvent)));
        assertNotNull(context.getBean(RabbitBeanNameResolver.getRabbitAdminBeanName(queuePropertiesSomeEvent)));
        assertNotNull(context.getBean(RabbitBeanNameResolver.getSimpleRabbitListenerContainerFactoryBean(queuePropertiesSomeEvent)));

        assertNotNull(connectionFactoryForAnotherVH);
        assertNotNull(context.getBean(RabbitBeanNameResolver.getRabbitTemplateBeanName(queuePropertiesAnotherEvent)));
        assertNotNull(context.getBean(RabbitBeanNameResolver.getRabbitAdminBeanName(queuePropertiesAnotherEvent)));
        assertNotNull(context.getBean(RabbitBeanNameResolver.getSimpleRabbitListenerContainerFactoryBean(queuePropertiesAnotherEvent)));

        assertEquals(sameVH, connectionFactoryForDefaultVH.getVirtualHost());
        assertEquals(sameHost, connectionFactoryForDefaultVH.getHost());
        assertEquals(5671, connectionFactoryForDefaultVH.getPort());
        assertEquals(sameUsername, connectionFactoryForDefaultVH.getUsername());

        assertEquals(sameVH, connectionFactoryForAnotherVH.getVirtualHost());
        assertEquals(sameHost, connectionFactoryForAnotherVH.getHost());
        assertEquals(5672, connectionFactoryForAnotherVH.getPort());
        assertEquals(sameUsername, connectionFactoryForAnotherVH.getUsername());

        assertEquals(2, context.getBeansOfType(CachingConnectionFactory.class).size());
        assertEquals(2, context.getBeansOfType(RabbitTemplate.class).size());
        assertEquals(2, context.getBeansOfType(RabbitAdmin.class).size());
        assertEquals(2, context.getBeansOfType(SimpleRabbitListenerContainerFactory.class).size());
    }

    @Test
    public void should_create_2_connectionFactories_and_all_other_beans_for_different_hosts_same_port_and_same_virtual_host() {

        String sameVH = "test";
        String sameUsername = "guest";
        String diffHost = "anotherHost";

        TunedRabbitPropertiesMap rabbitCustomPropertiesMap = new TunedRabbitPropertiesMap();
        TunedRabbitProperties queuePropertiesSomeEvent = createQueueProperties(true, sameVH, sameUsername, false, "localhost", 5672);
        rabbitCustomPropertiesMap.put("some-event", queuePropertiesSomeEvent);

        TunedRabbitProperties queuePropertiesAnotherEvent = createQueueProperties(false, sameVH, sameUsername, false, diffHost, 5672);
        rabbitCustomPropertiesMap.put("some-event2", queuePropertiesAnotherEvent);

        tradeshiftRabbitAutoConfiguration.routingConnectionFactory(rabbitCustomPropertiesMap);

        CachingConnectionFactory connectionFactoryForDefaultVH = (CachingConnectionFactory) context.getBean(RabbitBeanNameResolver.getConnectionFactoryBeanName(queuePropertiesSomeEvent));
        CachingConnectionFactory connectionFactoryForAnotherVH = (CachingConnectionFactory) context.getBean(RabbitBeanNameResolver.getConnectionFactoryBeanName(queuePropertiesAnotherEvent));

        assertNotNull(connectionFactoryForDefaultVH);
        assertNotNull(context.getBean(RabbitBeanNameResolver.getRabbitTemplateBeanName(queuePropertiesSomeEvent)));
        assertNotNull(context.getBean(RabbitBeanNameResolver.getRabbitAdminBeanName(queuePropertiesSomeEvent)));
        assertNotNull(context.getBean(RabbitBeanNameResolver.getSimpleRabbitListenerContainerFactoryBean(queuePropertiesSomeEvent)));

        assertNotNull(connectionFactoryForAnotherVH);
        assertNotNull(context.getBean(RabbitBeanNameResolver.getRabbitTemplateBeanName(queuePropertiesAnotherEvent)));
        assertNotNull(context.getBean(RabbitBeanNameResolver.getRabbitAdminBeanName(queuePropertiesAnotherEvent)));
        assertNotNull(context.getBean(RabbitBeanNameResolver.getSimpleRabbitListenerContainerFactoryBean(queuePropertiesAnotherEvent)));

        assertEquals(sameVH, connectionFactoryForDefaultVH.getVirtualHost());
        assertEquals("localhost", connectionFactoryForDefaultVH.getHost());
        assertEquals(5672, connectionFactoryForDefaultVH.getPort());
        assertEquals(sameUsername, connectionFactoryForDefaultVH.getUsername());

        assertEquals(sameVH, connectionFactoryForAnotherVH.getVirtualHost());
        assertEquals(diffHost, connectionFactoryForAnotherVH.getHost());
        assertEquals(5672, connectionFactoryForAnotherVH.getPort());
        assertEquals(sameUsername, connectionFactoryForAnotherVH.getUsername());

        assertEquals(2, context.getBeansOfType(CachingConnectionFactory.class).size());
        assertEquals(2, context.getBeansOfType(RabbitTemplate.class).size());
        assertEquals(2, context.getBeansOfType(RabbitAdmin.class).size());
        assertEquals(2, context.getBeansOfType(SimpleRabbitListenerContainerFactory.class).size());
    }

    @Test
    public void should_create_2_connectionFactories_and_all_other_beans_for_same_host_same_port_and_different_virtual_host() {

        String anotherVirtualHost = "test";
        String anotherUsername = "anotherUsername";

        TunedRabbitPropertiesMap rabbitCustomPropertiesMap = new TunedRabbitPropertiesMap();

        TunedRabbitProperties queuePropertiesSomeEvent = createQueueProperties(true);
        rabbitCustomPropertiesMap.put("some-event", queuePropertiesSomeEvent);

        TunedRabbitProperties queuePropertiesAnotherEvent = createQueueProperties(false, anotherVirtualHost, anotherUsername, false);
        rabbitCustomPropertiesMap.put("some-event2", queuePropertiesAnotherEvent);

        tradeshiftRabbitAutoConfiguration.routingConnectionFactory(rabbitCustomPropertiesMap);

        CachingConnectionFactory connectionFactoryForDefaultVH = (CachingConnectionFactory) context.getBean(RabbitBeanNameResolver.getConnectionFactoryBeanNameForDefaultVirtualHost(queuePropertiesSomeEvent));
        CachingConnectionFactory connectionFactoryForAnotherVH = (CachingConnectionFactory) context.getBean(RabbitBeanNameResolver.getConnectionFactoryBeanName(queuePropertiesAnotherEvent));

        assertNotNull(connectionFactoryForDefaultVH);
        assertNotNull(context.getBean(RabbitBeanNameResolver.getRabbitTemplateBeanNameForDefaultVirtualHost(queuePropertiesSomeEvent)));
        assertNotNull(context.getBean(RabbitBeanNameResolver.getRabbitAdminBeanNameForDefaultVirtualHost(queuePropertiesSomeEvent)));
        assertNotNull(context.getBean(RabbitBeanNameResolver.getSimpleRabbitListenerContainerFactoryBeanForDefaultVirtualHost(queuePropertiesSomeEvent)));

        assertNotNull(connectionFactoryForAnotherVH);
        assertNotNull(context.getBean(RabbitBeanNameResolver.getRabbitTemplateBeanName(queuePropertiesAnotherEvent)));
        assertNotNull(context.getBean(RabbitBeanNameResolver.getRabbitAdminBeanName(queuePropertiesAnotherEvent)));
        assertNotNull(context.getBean(RabbitBeanNameResolver.getSimpleRabbitListenerContainerFactoryBean(queuePropertiesAnotherEvent)));

        assertEquals("/", connectionFactoryForDefaultVH.getVirtualHost());
        assertEquals("localhost", connectionFactoryForDefaultVH.getHost());
        assertEquals(5672, connectionFactoryForDefaultVH.getPort());
        assertEquals("guest", connectionFactoryForDefaultVH.getUsername());

        assertEquals(anotherVirtualHost, connectionFactoryForAnotherVH.getVirtualHost());
        assertEquals("localhost", connectionFactoryForAnotherVH.getHost());
        assertEquals(5672, connectionFactoryForAnotherVH.getPort());
        assertEquals(anotherUsername, connectionFactoryForAnotherVH.getUsername());

        assertEquals(2, context.getBeansOfType(CachingConnectionFactory.class).size());
        assertEquals(2, context.getBeansOfType(RabbitTemplate.class).size());
        assertEquals(2, context.getBeansOfType(RabbitAdmin.class).size());
        assertEquals(2, context.getBeansOfType(SimpleRabbitListenerContainerFactory.class).size());
    }

    @Test
    public void should_create_2_connectionFactories_and_all_other_beans_for_different_hosts_different_ports_and_same_virtual_host() {

        String sameVH = "test";
        String sameUsername = "guest";
        String anotherHost = "anotherHost";
        int anotherPort = 5671;

        TunedRabbitPropertiesMap rabbitCustomPropertiesMap = new TunedRabbitPropertiesMap();

        TunedRabbitProperties queuePropertiesSomeEvent = createQueueProperties(false, sameVH, sameUsername, false, "localhost", 5672);
        rabbitCustomPropertiesMap.put("some-event", queuePropertiesSomeEvent);

        TunedRabbitProperties queuePropertiesAnotherEvent = createQueueProperties(false, sameVH, sameUsername, false, anotherHost, anotherPort);
        rabbitCustomPropertiesMap.put("some-event2", queuePropertiesAnotherEvent);

        tradeshiftRabbitAutoConfiguration.routingConnectionFactory(rabbitCustomPropertiesMap);

        CachingConnectionFactory connectionFactoryForDefaultVH = (CachingConnectionFactory) context.getBean(RabbitBeanNameResolver.getConnectionFactoryBeanName(queuePropertiesSomeEvent));
        CachingConnectionFactory connectionFactoryForAnotherVH = (CachingConnectionFactory) context.getBean(RabbitBeanNameResolver.getConnectionFactoryBeanName(queuePropertiesAnotherEvent));

        assertNotNull(connectionFactoryForDefaultVH);
        assertNotNull(context.getBean(RabbitBeanNameResolver.getRabbitTemplateBeanName(queuePropertiesSomeEvent)));
        assertNotNull(context.getBean(RabbitBeanNameResolver.getRabbitAdminBeanName(queuePropertiesSomeEvent)));
        assertNotNull(context.getBean(RabbitBeanNameResolver.getSimpleRabbitListenerContainerFactoryBean(queuePropertiesSomeEvent)));

        assertNotNull(connectionFactoryForAnotherVH);
        assertNotNull(context.getBean(RabbitBeanNameResolver.getRabbitTemplateBeanName(queuePropertiesAnotherEvent)));
        assertNotNull(context.getBean(RabbitBeanNameResolver.getRabbitAdminBeanName(queuePropertiesAnotherEvent)));
        assertNotNull(context.getBean(RabbitBeanNameResolver.getSimpleRabbitListenerContainerFactoryBean(queuePropertiesAnotherEvent)));

        assertEquals(sameVH, connectionFactoryForDefaultVH.getVirtualHost());
        assertEquals("localhost", connectionFactoryForDefaultVH.getHost());
        assertEquals(5672, connectionFactoryForDefaultVH.getPort());
        assertEquals("guest", connectionFactoryForDefaultVH.getUsername());

        assertEquals(sameVH, connectionFactoryForAnotherVH.getVirtualHost());
        assertEquals(anotherHost, connectionFactoryForAnotherVH.getHost());
        assertEquals(anotherPort, connectionFactoryForAnotherVH.getPort());
        assertEquals(sameUsername, connectionFactoryForAnotherVH.getUsername());

        assertEquals(2, context.getBeansOfType(CachingConnectionFactory.class).size());
        assertEquals(2, context.getBeansOfType(RabbitTemplate.class).size());
        assertEquals(2, context.getBeansOfType(RabbitAdmin.class).size());
        assertEquals(2, context.getBeansOfType(SimpleRabbitListenerContainerFactory.class).size());
    }

    @Test
    public void should_create_only_one_connectionFactory_and_all_other_beans_for_same_host_same_port_and_same_virtual_host() {

        TunedRabbitPropertiesMap rabbitCustomPropertiesMap = new TunedRabbitPropertiesMap();
        TunedRabbitProperties queuePropertiesSomeEvent = createQueueProperties(true);
        rabbitCustomPropertiesMap.put("some-event", queuePropertiesSomeEvent);

        TunedRabbitProperties queuePropertiesAnotherEvent = createQueueProperties(false);
        rabbitCustomPropertiesMap.put("some-event2", queuePropertiesAnotherEvent);

        tradeshiftRabbitAutoConfiguration.routingConnectionFactory(rabbitCustomPropertiesMap);

        CachingConnectionFactory connectionFactoryForDefaultVH = (CachingConnectionFactory) context.getBean(RabbitBeanNameResolver.getConnectionFactoryBeanNameForDefaultVirtualHost(queuePropertiesSomeEvent));

        assertNotNull(connectionFactoryForDefaultVH);
        assertNotNull(context.getBean(RabbitBeanNameResolver.getRabbitTemplateBeanNameForDefaultVirtualHost(queuePropertiesSomeEvent)));
        assertNotNull(context.getBean(RabbitBeanNameResolver.getRabbitAdminBeanNameForDefaultVirtualHost(queuePropertiesSomeEvent)));
        assertNotNull(context.getBean(RabbitBeanNameResolver.getSimpleRabbitListenerContainerFactoryBeanForDefaultVirtualHost(queuePropertiesSomeEvent)));

        assertEquals("/", connectionFactoryForDefaultVH.getVirtualHost());
        assertEquals("localhost", connectionFactoryForDefaultVH.getHost());
        assertEquals(5672, connectionFactoryForDefaultVH.getPort());
        assertEquals("guest", connectionFactoryForDefaultVH.getUsername());

        assertEquals(1, context.getBeansOfType(CachingConnectionFactory.class).size());
        assertEquals(1, context.getBeansOfType(RabbitTemplate.class).size());
        assertEquals(1, context.getBeansOfType(RabbitAdmin.class).size());
        assertEquals(1, context.getBeansOfType(SimpleRabbitListenerContainerFactory.class).size());
    }

    private TunedRabbitProperties createQueueProperties(boolean primary) {
        return createQueueProperties(primary, null);
    }

    private TunedRabbitProperties createQueueProperties(boolean primary, String virtualHost) {
        return createQueueProperties(primary, virtualHost, false);
    }

    private TunedRabbitProperties createQueueProperties(boolean primary, String virtualHost, boolean jsonMessageConverter) {
        return createQueueProperties(primary, virtualHost, "guest", jsonMessageConverter);
    }

    private TunedRabbitProperties createQueueProperties(boolean primary, String virtualHost, String username, boolean jsonMessageConverter) {
        return createQueueProperties(primary, virtualHost, username, jsonMessageConverter, "localhost", 5672);
    }

    private TunedRabbitProperties createQueueProperties(boolean primary, String virtualHost, String username, boolean jsonMessageConverter, String host, int port) {
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
        queueProperties.setHost(host);
        queueProperties.setPort(port);
        queueProperties.setSslConnection(false);
        queueProperties.setEnableJsonMessageConverter(jsonMessageConverter);

        return queueProperties;
    }

    private AbstractMessageConverter getMessageConverter(SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory) throws IllegalAccessException {
        Field[] fields = simpleRabbitListenerContainerFactory.getClass().getSuperclass().getDeclaredFields();
        for (Field field : fields) {
            if ("messageConverter".equals(field.getName())) {
                field.setAccessible(true);
                return (AbstractMessageConverter) field.get(simpleRabbitListenerContainerFactory);
            }
        }

        return null;
    }

}
