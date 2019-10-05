package com.tradeshift.amqp.rabbit.handlers;

import static org.junit.Assert.assertNotNull;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.tradeshift.amqp.autoconfigure.TunedRabbitAutoConfiguration;
import com.tradeshift.amqp.rabbit.components.RabbitComponentsFactory;
import com.tradeshift.amqp.rabbit.properties.TunedRabbitProperties;
import com.tradeshift.amqp.rabbit.properties.TunedRabbitPropertiesMap;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RabbitTemplateHandlerTest {

    private TunedRabbitAutoConfiguration tradeshiftRabbitAutoConfiguration;

    @Spy
    private AnnotationConfigApplicationContext spyContext;

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
        tradeshiftRabbitAutoConfiguration = new TunedRabbitAutoConfiguration(context, beanFactory, new RabbitComponentsFactory());
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

    @Test
    public void should_return_exception_when_we_have_more_than_2_templates_with_autoconfig_disabled_and_without_the_correct_properties() {
        expectedException.expect(BeanDefinitionValidationException.class);
        expectedException.expectMessage("There are more than 1 RabbitTemplate available. You need to specify the name of the RabbitTemplate that we will use for this event");

        disableAutoConfigurationInSpyContext();
        Map<String, RabbitTemplate> beansOfType = new HashMap<>();
        beansOfType.put("rt1", new RabbitTemplate());
        beansOfType.put("rt2", new RabbitTemplate());

        Mockito.doReturn(new RabbitTemplate()).when(spyContext).getBean(Mockito.anyString());
        Mockito.doReturn(beansOfType).when(spyContext).getBeansOfType(RabbitTemplate.class);

        TunedRabbitPropertiesMap rabbitCustomPropertiesMap = new TunedRabbitPropertiesMap();
        rabbitCustomPropertiesMap.put("some-event", createQueueProperties(true, null));
        rabbitCustomPropertiesMap.put("some-event2", createQueueProperties(false, "test"));

        rabbitTemplateHandler = new RabbitTemplateHandler(spyContext, rabbitCustomPropertiesMap);

        assertNotNull(rabbitTemplateHandler.getRabbitTemplate("some-event"));
        assertNotNull(rabbitTemplateHandler.getRabbitTemplate("some-event2"));
    }

    @Test
    public void should_return_the_single_bean_when_we_have_the_autoconfig_disabled_and_without_the_correct_properties() {
        disableAutoConfigurationInSpyContext();
        Map<String, RabbitTemplate> beansOfType = new HashMap<>();
        beansOfType.put("rt1", new RabbitTemplate());

        Mockito.doReturn(new RabbitTemplate()).when(spyContext).getBean(Mockito.anyString());
        Mockito.doReturn(beansOfType).when(spyContext).getBeansOfType(RabbitTemplate.class);

        TunedRabbitPropertiesMap rabbitCustomPropertiesMap = new TunedRabbitPropertiesMap();
        rabbitCustomPropertiesMap.put("some-event", createQueueProperties(true, null));

        rabbitTemplateHandler = new RabbitTemplateHandler(spyContext, rabbitCustomPropertiesMap);

        assertNotNull(rabbitTemplateHandler.getRabbitTemplate("some-event"));
    }

    @Test
    public void should_return_exception_when_we_have_the_autoconfig_disabled_and_without_any_rabbit_template_bean_configured() {
        expectedException.expect(NoSuchBeanDefinitionException.class);
        expectedException.expectMessage("No RabbitTemplate bean available. Are you sure that you want to disable the autoconfiguration?");

        disableAutoConfigurationInSpyContext();

        Mockito.doReturn(new RabbitTemplate()).when(spyContext).getBean(Mockito.anyString());
        Mockito.doReturn(new HashMap<>()).when(spyContext).getBeansOfType(RabbitTemplate.class);

        Mockito.doReturn(new RabbitTemplate()).when(spyContext).getBean(Mockito.anyString());

        TunedRabbitPropertiesMap rabbitCustomPropertiesMap = new TunedRabbitPropertiesMap();
        rabbitCustomPropertiesMap.put("some-event", createQueueProperties(true, null));

        rabbitTemplateHandler = new RabbitTemplateHandler(spyContext, rabbitCustomPropertiesMap);

        assertNotNull(rabbitTemplateHandler.getRabbitTemplate("some-event"));
    }

    @Test
    public void should_return_the_correct_rabbit_template_bean_when_we_have_more_than_2_templates_with_autoconfig_disabled_and_with_the_correct_properties() {
        disableAutoConfigurationInSpyContext();

        String rabbitTemplateBean1 = "rt1";
        String rabbitTemplateBean2 = "rt2";

        Map<String, RabbitTemplate> beansOfType = new HashMap<>();
        beansOfType.put(rabbitTemplateBean1, new RabbitTemplate());
        beansOfType.put(rabbitTemplateBean2, new RabbitTemplate());

        Mockito.doReturn(new RabbitTemplate()).when(spyContext).getBean(rabbitTemplateBean1);
        Mockito.doReturn(new RabbitTemplate()).when(spyContext).getBean(rabbitTemplateBean2);
        Mockito.doReturn(beansOfType).when(spyContext).getBeansOfType(RabbitTemplate.class);

        TunedRabbitPropertiesMap rabbitCustomPropertiesMap = new TunedRabbitPropertiesMap();
        rabbitCustomPropertiesMap.put("some-event", createQueueProperties(true, null, rabbitTemplateBean1));
        rabbitCustomPropertiesMap.put("some-event2", createQueueProperties(false, "test", rabbitTemplateBean2));

        rabbitTemplateHandler = new RabbitTemplateHandler(spyContext, rabbitCustomPropertiesMap);

        assertNotNull(rabbitTemplateHandler.getRabbitTemplate("some-event"));
        assertNotNull(rabbitTemplateHandler.getRabbitTemplate("some-event2"));
    }

    private TunedRabbitProperties createQueueProperties(boolean primary, String virtualHost, String rabbitTemplateName) {
        return createQueueProperties(primary, virtualHost, "guest", "localhost", 5672, rabbitTemplateName);
    }

    private TunedRabbitProperties createQueueProperties(boolean primary, String virtualHost) {
        return createQueueProperties(primary, virtualHost, "guest", "localhost", 5672, null);
    }

    private void disableAutoConfigurationInSpyContext(){
        MockEnvironment mockEnvironment = new MockEnvironment();
        mockEnvironment.setProperty("spring.rabbitmq.enable.custom.autoconfiguration", "false");
        spyContext.setEnvironment(mockEnvironment);
    }

    private TunedRabbitProperties createQueueProperties(boolean primary, String virtualHost, String username, String host, int port, String rabbitTemplateName) {
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
        Optional.ofNullable(rabbitTemplateName).ifPresent(queueProperties::setRabbitTemplateBeanName);

        return queueProperties;
    }

}
