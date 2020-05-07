package com.tradeshift.amqp.resolvers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.tradeshift.amqp.rabbit.properties.TunedRabbitProperties;

public class RabbitBeanNameResolverTest {

	@Test
	public void should_return_the_correct_name_for_default_connection_factory_from_properties() {
		assertEquals("connectionFactoryDefaultLocalhost5672", RabbitBeanNameResolver
				.getConnectionFactoryBeanName(createQueueProperties("localhost", 5672, null)));
	}

	@Test
	public void should_return_the_correct_name_for_connection_factory_from_properties() {
		assertEquals("connectionFactoryTradeshiftLocalhost5672", RabbitBeanNameResolver
				.getConnectionFactoryBeanName(createQueueProperties("localhost", 5672, "tradeshift")));
	}

	@Test
	public void should_return_the_correct_name_for_default_rabbit_admin_from_properties() {
		assertEquals("rabbitAdminDefaultLocalhost5672", RabbitBeanNameResolver
				.getRabbitAdminBeanName(createQueueProperties("localhost", 5672, null)));
	}

	@Test
	public void should_return_the_correct_name_for_rabbit_admin_from_properties() {
		assertEquals("rabbitAdminTradeshiftLocalhost5672", RabbitBeanNameResolver
				.getRabbitAdminBeanName(createQueueProperties("localhost", 5672, "tradeshift")));
	}

	@Test
	public void should_return_the_correct_name_for_default_rabbit_template_from_properties() {
		assertEquals("rabbitTemplateDefaultLocalhost5672", RabbitBeanNameResolver
				.getRabbitTemplateBeanName(createQueueProperties("localhost", 5672, null)));
	}

	@Test
	public void should_return_the_correct_name_for_rabbit_template_from_properties() {
		assertEquals("rabbitTemplateTradeshiftLocalhost5672", RabbitBeanNameResolver
				.getRabbitTemplateBeanName(createQueueProperties("localhost", 5672, "tradeshift")));
	}

	@Test
	public void should_return_the_correct_name_for_default_listener_container_factory_from_properties() {
		assertEquals("containerFactoryDefaultLocalhost5672", RabbitBeanNameResolver
				.getSimpleRabbitListenerContainerFactoryBean(createQueueProperties("localhost", 5672, null)));
	}

	@Test
	public void should_return_the_correct_name_for_listener_container_factory_from_properties() {
		assertEquals("containerFactoryTradeshiftLocalhost5672", RabbitBeanNameResolver
				.getSimpleRabbitListenerContainerFactoryBean(createQueueProperties("localhost", 5672, "tradeshift")));
	}

	private TunedRabbitProperties createQueueProperties(String host, int port, String virtualHost) {
		TunedRabbitProperties queueProperties = new TunedRabbitProperties();
		queueProperties.setHost(host);
		queueProperties.setPort(port);
		queueProperties.setVirtualHost(virtualHost);
		queueProperties.setQueue("some-queue");
		queueProperties.setExchange("some-exchange");
		queueProperties.setExchangeType("topic");
		queueProperties.setMaxRetriesAttempts(5);
		queueProperties.setQueueRoutingKey("routing.key.test");
		queueProperties.setTtlRetryMessage(3000);
		queueProperties.setPrimary(true);
		queueProperties.setUsername("guest");
		queueProperties.setPassword("guest");
		queueProperties.setDefaultRetryDlq(true);
		queueProperties.setSslConnection(false);
		return queueProperties;
	}
}
