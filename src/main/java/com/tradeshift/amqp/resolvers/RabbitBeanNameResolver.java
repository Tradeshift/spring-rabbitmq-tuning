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
        return getConnectionFactoryBeanName(null, getHostAndPortKey(customRabbitProperties));
    }

    public static String getConnectionFactoryBeanName(TunedRabbitProperties customRabbitProperties) {
        return getConnectionFactoryBeanName(customRabbitProperties.getVirtualHost(), getHostAndPortKey(customRabbitProperties));
    }

    public static String getConnectionFactoryBeanName(String virtualHost, TunedRabbitProperties customRabbitProperties) {
        return getConnectionFactoryBeanName(virtualHost, getHostAndPortKey(customRabbitProperties));
    }

    private static String getConnectionFactoryBeanName(String virtualHost, String hostAndPort) {
        return getConnectionFactoryBeanName(treatVirtualHostName(virtualHost) + "_" + hostAndPort);
    }

    protected static String getConnectionFactoryBeanName(String virtualHostHostAndPort) {
        return TunedRabbitConstants.PREFIX_CONNECTION_FACTORY_PER_VIRTUAL_HOST + convertSnakeCaseToCamelCase(virtualHostHostAndPort);
    }

    // -------------------------------------- RabbitTemplate --------------------------------------

    public static String getRabbitTemplateBeanNameForDefaultVirtualHost(TunedRabbitProperties customRabbitProperties) {
        return getRabbitTemplateBeanName(null, getHostAndPortKey(customRabbitProperties));
    }

    public static String getRabbitTemplateBeanName(TunedRabbitProperties customRabbitProperties) {
        return getRabbitTemplateBeanName(customRabbitProperties.getVirtualHost(), getHostAndPortKey(customRabbitProperties));
    }

    public static String getRabbitTemplateBeanName(String virtualHost, TunedRabbitProperties customRabbitProperties) {
        return getRabbitTemplateBeanName(virtualHost, getHostAndPortKey(customRabbitProperties));
    }

    private static String getRabbitTemplateBeanName(String virtualHost, String hostAndPort) {
        return getRabbitTemplateBeanName(treatVirtualHostName(virtualHost) + "_" + hostAndPort);
    }

    protected static String getRabbitTemplateBeanName(String virtualHostHostAndPort) {
        return TunedRabbitConstants.PREFIX_RABBIT_TEMPLATE_PER_VIRTUAL_HOST + convertSnakeCaseToCamelCase(virtualHostHostAndPort);
    }

    // -------------------------------------- RabbitAdmin --------------------------------------

    public static String getRabbitAdminBeanNameForDefaultVirtualHost(TunedRabbitProperties customRabbitProperties) {
        return getRabbitAdminBeanName(null, getHostAndPortKey(customRabbitProperties));
    }

    public static String getRabbitAdminBeanName(TunedRabbitProperties customRabbitProperties) {
        return getRabbitAdminBeanName(customRabbitProperties.getVirtualHost(), getHostAndPortKey(customRabbitProperties));
    }

    public static String getRabbitAdminBeanName(String virtualHost, TunedRabbitProperties customRabbitProperties) {
        return getRabbitAdminBeanName(virtualHost, getHostAndPortKey(customRabbitProperties));
    }

    private static String getRabbitAdminBeanName(String virtualHost, String hostAndPort) {
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
        return getSimpleRabbitListenerContainerFactoryBean(null, getHostAndPortKey(customRabbitProperties));
    }

    public static String getSimpleRabbitListenerContainerFactoryBean(TunedRabbitProperties customRabbitProperties) {
        return getSimpleRabbitListenerContainerFactoryBean(customRabbitProperties.getVirtualHost(), getHostAndPortKey(customRabbitProperties));
    }

    public static String getSimpleRabbitListenerContainerFactoryBean(String virtualHost, TunedRabbitProperties customRabbitProperties) {
        return getSimpleRabbitListenerContainerFactoryBean(virtualHost, getHostAndPortKey(customRabbitProperties));
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

    private static String getHostAndPortKey(TunedRabbitProperties properties) {
        if (properties.isClusterMode()) {
            // TODO: improve so that the order of each host:port in the list doesn't generate different key
            return properties.getHosts().replaceAll("[:,]", "");
        }
        return properties.getHost() + properties.getPort();
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
