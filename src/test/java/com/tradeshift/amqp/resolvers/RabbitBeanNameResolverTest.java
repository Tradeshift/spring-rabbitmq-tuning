package com.tradeshift.amqp.resolvers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RabbitBeanNameResolverTest {

    @Test
    public void should_return_the_correct_name_for_connection_factory() {
        assertEquals("connectionFactoryTradeshiftLocalhost5672",
                RabbitBeanNameResolver.getConnectionFactoryBeanName("tradeshift", "localhost", 5672));
    }

    @Test
    public void should_return_the_correct_name_for_rabbit_admin() {
        assertEquals("rabbitAdminTradeshiftLocalhost5672",
                RabbitBeanNameResolver.getRabbitAdminBeanName("tradeshift", "localhost", 5672));
    }

    @Test
    public void should_return_the_correct_name_for_rabbit_template() {
        assertEquals("rabbitTemplateTradeshiftLocalhost5672",
                RabbitBeanNameResolver.getRabbitTemplateBeanName("tradeshift", "localhost", 5672));
    }

    @Test
    public void should_return_the_correct_name_for_listener_container_factory() {
        assertEquals("containerFactoryTradeshiftLocalhost5672",
                RabbitBeanNameResolver.getSimpleRabbitListenerContainerFactoryBean("tradeshift", "localhost", 5672));
    }

    @Test
    public void should_return_the_correct_name_for_default_connection_factory() {
        assertEquals("connectionFactoryDefaultLocalhost5671",
                RabbitBeanNameResolver.getConnectionFactoryBeanNameForDefaultVirtualHost("localhost", 5671));
    }

    @Test
    public void should_return_the_correct_name_for_default_rabbit_admin() {
        assertEquals("rabbitAdminDefaultLocalhost5671",
                RabbitBeanNameResolver.getRabbitAdminBeanNameForDefaultVirtualHost("localhost", 5671));
    }

    @Test
    public void should_return_the_correct_name_for_default_rabbit_template() {
        assertEquals("rabbitTemplateDefaultLocalhost5671",
                RabbitBeanNameResolver.getRabbitTemplateBeanNameForDefaultVirtualHost("localhost", 5671));
    }

    @Test
    public void should_return_the_correct_name_for_default_listener_container_factory() {
        assertEquals("containerFactoryDefaultLocalhost5671",
                RabbitBeanNameResolver.getSimpleRabbitListenerContainerFactoryBeanForDefaultVirtualHost("localhost", 5671));
    }
}
