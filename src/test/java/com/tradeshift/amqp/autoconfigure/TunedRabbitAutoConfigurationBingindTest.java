package com.tradeshift.amqp.autoconfigure;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.tradeshift.amqp.rabbit.components.RabbitComponentsFactory;
import com.tradeshift.amqp.rabbit.properties.TunedRabbitProperties;
import com.tradeshift.amqp.rabbit.properties.TunedRabbitPropertiesMap;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TunedRabbitAutoConfigurationBingindTest {

	private TunedRabbitAutoConfiguration tradeshiftRabbitAutoConfiguration;
	
    @Autowired
    private GenericApplicationContext context;

    @Autowired
    private ConfigurableListableBeanFactory beanFactory;
    
    @SpyBean
    private RabbitComponentsFactory rabbitComponentsFactory;

	private RabbitAdmin rabbitAdmin;
    
    @Before
    public void setup() {
        initMocks(this);
        tradeshiftRabbitAutoConfiguration = spy(new TunedRabbitAutoConfiguration(context, beanFactory));
        
        when(tradeshiftRabbitAutoConfiguration.rabbitComponentsFactory()).thenReturn(rabbitComponentsFactory);
        
        rabbitAdmin = mock(RabbitAdmin.class);
    	doReturn(rabbitAdmin).when(rabbitComponentsFactory).createRabbitAdminBean(any());
    }
    
    @Test
    public void should_create_binding_for_one_event() {
    	
    	TunedRabbitPropertiesMap rabbitCustomPropertiesMap = new TunedRabbitPropertiesMap();
    	TunedRabbitProperties eventProperties = createQueuePropertiesWithAutoCreate(true);
    	rabbitCustomPropertiesMap.put("some-event", eventProperties);

    	tradeshiftRabbitAutoConfiguration.routingConnectionFactory(rabbitCustomPropertiesMap);

    	// Validate the single exchange
    	ArgumentCaptor<Exchange> exchangeArgumentCaptor = ArgumentCaptor.forClass(Exchange.class);
    	verify(rabbitAdmin).declareExchange(exchangeArgumentCaptor.capture());
    	assertThat(exchangeArgumentCaptor.getValue().getName(), is("ex.test"));

    	// Validate all the queues
    	ArgumentCaptor<Queue> queueArgumentCaptor = ArgumentCaptor.forClass(Queue.class);
    	verify(rabbitAdmin, times(3)).declareQueue(queueArgumentCaptor.capture());
    	List<String> queuesNames = queueArgumentCaptor.getAllValues().stream()
    			.map(Queue::getName)
    			.collect(toList());
    	assertThat(queuesNames, hasItems("queue.test", "queue.test.dlq", "queue.test.retry"));
    }
    
    @Test
    public void should_not_create_binding_for_retry_and_dlq_when_disabled() {
    	
    	TunedRabbitPropertiesMap rabbitCustomPropertiesMap = new TunedRabbitPropertiesMap();
    	TunedRabbitProperties eventProperties = createQueuePropertiesWithAutoCreate(true);
    	eventProperties.setAutoCreateForRetryDlq(false);
    	rabbitCustomPropertiesMap.put("some-event", eventProperties);

    	tradeshiftRabbitAutoConfiguration.routingConnectionFactory(rabbitCustomPropertiesMap);

    	// Validate the single exchange
    	ArgumentCaptor<Exchange> exchangeArgumentCaptor = ArgumentCaptor.forClass(Exchange.class);
    	verify(rabbitAdmin).declareExchange(exchangeArgumentCaptor.capture());
    	assertThat(exchangeArgumentCaptor.getValue().getName(), is("ex.test"));

    	// Validate all the queues
    	ArgumentCaptor<Queue> queueArgumentCaptor = ArgumentCaptor.forClass(Queue.class);
    	verify(rabbitAdmin).declareQueue(queueArgumentCaptor.capture());
    	assertThat(queueArgumentCaptor.getValue().getName(), is("queue.test"));
    }

    @Test
    public void should_create_binding_for_events_with_same_host_port_and_virtualhost() {

    	TunedRabbitPropertiesMap rabbitCustomPropertiesMap = new TunedRabbitPropertiesMap();
    	TunedRabbitProperties eventProperties = createQueuePropertiesWithAutoCreate(true);
    	rabbitCustomPropertiesMap.put("some-event", eventProperties);

    	eventProperties = createQueuePropertiesWithAutoCreate(false);
    	eventProperties.setQueue("queue2.test");
    	eventProperties.setExchange("exchange2.test");
    	rabbitCustomPropertiesMap.put("some-event2", eventProperties);

    	tradeshiftRabbitAutoConfiguration.routingConnectionFactory(rabbitCustomPropertiesMap);

    	// Validate the two exchanges
    	ArgumentCaptor<Exchange> exchangeArgumentCaptor = ArgumentCaptor.forClass(Exchange.class);
    	verify(rabbitAdmin, times(2)).declareExchange(exchangeArgumentCaptor.capture());
    	List<String> exchangesNames = exchangeArgumentCaptor.getAllValues().stream()
    			.map(Exchange::getName)
    			.collect(toList());
    	assertThat(exchangesNames, hasItems("ex.test", "exchange2.test"));

    	ArgumentCaptor<Queue> queueArgumentCaptor = ArgumentCaptor.forClass(Queue.class);
    	verify(rabbitAdmin, times(6)).declareQueue(queueArgumentCaptor.capture());
    	List<String> queuesNames = queueArgumentCaptor.getAllValues().stream()
    			.map(Queue::getName)
    			.collect(toList());
    	assertThat(queuesNames, hasItems("queue.test", "queue.test.dlq", "queue.test.retry",
    			"queue2.test", "queue2.test.dlq", "queue2.test.retry"));
    }
    
    private TunedRabbitProperties createQueuePropertiesWithAutoCreate(boolean primary) {
    	return createQueuePropertiesWithAutoCreate(primary, "localhost", 5672, null);
    }

    private TunedRabbitProperties createQueuePropertiesWithAutoCreate(boolean primary, String host, int port, String virtualHost) {
    	TunedRabbitProperties queueProperties = new TunedRabbitProperties();
    	queueProperties.setQueue("queue.test");
    	queueProperties.setExchange("ex.test");
    	queueProperties.setExchangeType("topic");
    	queueProperties.setMaxRetriesAttempts(5);
    	queueProperties.setQueueRoutingKey("routing.key.test");
    	queueProperties.setTtlRetryMessage(3000);
    	queueProperties.setPrimary(primary);
    	queueProperties.setUsername("guest");
    	queueProperties.setPassword("guest");
    	queueProperties.setHost(host);
    	queueProperties.setPort(port);
    	queueProperties.setVirtualHost(virtualHost);
    	queueProperties.setSslConnection(false);
    	queueProperties.setEnableJsonMessageConverter(false);
    	queueProperties.setAutoCreate(true);

    	return queueProperties;
    }
}
