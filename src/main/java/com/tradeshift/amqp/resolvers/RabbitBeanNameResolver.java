package com.tradeshift.amqp.resolvers;

import java.util.Optional;
import java.util.stream.Stream;

import com.tradeshift.amqp.log.TunedRabbitConstants;
import com.tradeshift.amqp.rabbit.properties.TunedRabbitProperties;

public class RabbitBeanNameResolver {

    private RabbitBeanNameResolver() {
        //Do nothing because this is a private constructor
    }

    // -------------------------------------- ConnectionFactory --------------------------------------

    public static String getConnectionFactoryBeanNameForDefaultVirtualHost(TunedRabbitProperties customRabbitProperties) {
        return getConnectionFactoryBeanName(null, customRabbitProperties.getHost(), customRabbitProperties.getPort());
    }

    public static String getConnectionFactoryBeanNameForDefaultVirtualHost(String host, int port) {
        return getConnectionFactoryBeanName(null, host, port);
    }

    public static String getConnectionFactoryBeanName(TunedRabbitProperties customRabbitProperties) {
        return getConnectionFactoryBeanName(customRabbitProperties.getVirtualHost(), customRabbitProperties.getHost() + customRabbitProperties.getPort());
    }

    public static String getConnectionFactoryBeanName(String virtualHost, String host, int port) {
        return getConnectionFactoryBeanName(virtualHost, host + port);
    }

    public static String getConnectionFactoryBeanName(String virtualHost, String hostAndPort) {
        return getConnectionFactoryBeanName(treatVirtualHostName(virtualHost) + "_" + hostAndPort);
    }

    protected static String getConnectionFactoryBeanName(String virtualHostHostAndPort) {
        return TunedRabbitConstants.PREFIX_CONNECTION_FACTORY_PER_VIRTUAL_HOST + convertSnakeCaseToCamelCase(virtualHostHostAndPort);
    }

    // -------------------------------------- RabbitTemplate --------------------------------------

    public static String getRabbitTemplateBeanNameForDefaultVirtualHost(TunedRabbitProperties customRabbitProperties) {
        return getRabbitTemplateBeanName(null, customRabbitProperties.getHost(), customRabbitProperties.getPort());
    }

    public static String getRabbitTemplateBeanNameForDefaultVirtualHost(String host, int port) {
        return getRabbitTemplateBeanName(null, host, port);
    }

    public static String getRabbitTemplateBeanName(TunedRabbitProperties customRabbitProperties) {
        return getRabbitTemplateBeanName(customRabbitProperties.getVirtualHost(), customRabbitProperties.getHost() + customRabbitProperties.getPort());
    }

    public static String getRabbitTemplateBeanName(String virtualHost, String host, int port) {
        return getRabbitTemplateBeanName(virtualHost, host + port);
    }

    public static String getRabbitTemplateBeanName(String virtualHost, String hostAndPort) {
        return getRabbitTemplateBeanName(treatVirtualHostName(virtualHost) + "_" + hostAndPort);
    }

    protected static String getRabbitTemplateBeanName(String virtualHostHostAndPort) {
        return TunedRabbitConstants.PREFIX_RABBIT_TEMPLATE_PER_VIRTUAL_HOST + convertSnakeCaseToCamelCase(virtualHostHostAndPort);
    }

    // -------------------------------------- RabbitAdmin --------------------------------------

    public static String getRabbitAdminBeanNameForDefaultVirtualHost(TunedRabbitProperties customRabbitProperties) {
        return getRabbitAdminBeanName(null, customRabbitProperties.getHost(), customRabbitProperties.getPort());
    }

    public static String getRabbitAdminBeanNameForDefaultVirtualHost(String host, int port) {
        return getRabbitAdminBeanName(null, host, port);
    }

    public static String getRabbitAdminBeanName(TunedRabbitProperties customRabbitProperties) {
        return getRabbitAdminBeanName(customRabbitProperties.getVirtualHost(), customRabbitProperties.getHost() + customRabbitProperties.getPort());
    }

    public static String getRabbitAdminBeanName(String virtualHost, String host, int port) {
        return getRabbitAdminBeanName(virtualHost, host + port);
    }

    public static String getRabbitAdminBeanName(String virtualHost, String hostAndPort) {
        return getRabbitAdminBeanName(treatVirtualHostName(virtualHost) + "_" + hostAndPort, true);
    }

    public static String getRabbitAdminBeanName(String virtualHostHostAndPort, boolean convertFromSnakeCase) {
        if (convertFromSnakeCase) {
            return TunedRabbitConstants.PREFIX_RABBIT_ADMIN_PER_VIRTUAL_HOST + convertSnakeCaseToCamelCase(virtualHostHostAndPort);
        }

        return TunedRabbitConstants.PREFIX_RABBIT_ADMIN_PER_VIRTUAL_HOST + virtualHostHostAndPort;
    }

    // -------------------------------------- SimpleRabbitListenerContainerFactory --------------------------------------

    public static String getSimpleRabbitListenerContainerFactoryBeanForDefaultVirtualHost(TunedRabbitProperties customRabbitProperties) {
        return getSimpleRabbitListenerContainerFactoryBean(null, customRabbitProperties.getHost(), customRabbitProperties.getPort());
    }

    public static String getSimpleRabbitListenerContainerFactoryBeanForDefaultVirtualHost(String host, int port) {
        return getSimpleRabbitListenerContainerFactoryBean(null, host, port);
    }

    public static String getSimpleRabbitListenerContainerFactoryBean(TunedRabbitProperties customRabbitProperties) {
        return getSimpleRabbitListenerContainerFactoryBean(customRabbitProperties.getVirtualHost(), customRabbitProperties.getHost() + customRabbitProperties.getPort());
    }

    public static String getSimpleRabbitListenerContainerFactoryBean(String virtualHost, String host, int port) {
        return getSimpleRabbitListenerContainerFactoryBean(virtualHost, host + port);
    }

    protected static String getSimpleRabbitListenerContainerFactoryBean(String virtualHost, String hostAndPort) {
        return getSimpleRabbitListenerContainerFactoryBean(treatVirtualHostName(virtualHost) + "_" + hostAndPort);
    }

    protected static String getSimpleRabbitListenerContainerFactoryBean(String virtualHostHostAndPort) {
        return TunedRabbitConstants.PREFIX_SIMPLE_RABBIT_LISTENER_CONTAINER_FACTORY_PER_VIRTUAL_HOST  + convertSnakeCaseToCamelCase(virtualHostHostAndPort);
    }

    // -------------------------------------- Methods --------------------------------------

    public static String treatVirtualHostName(String virtualHost) {
        return Optional.ofNullable(virtualHost).orElse("Default");
    }

    protected static String convertSnakeCaseToCamelCase(final String text) {
        final StringBuilder sb = new StringBuilder();
        Stream.of(text.split("_")).forEach(s -> {
            sb.append(Character.toUpperCase(s.charAt(0)));
            if (s.length() > 1) {
                sb.append(s.substring(1, s.length()).toLowerCase());
            }
        });
        return sb.toString();
    }

}
