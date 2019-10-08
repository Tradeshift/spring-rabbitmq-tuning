package com.tradeshift.amqp.log;

public class TunedRabbitConstants {

    public static final String PREFIX_CONNECTION_FACTORY_PER_VIRTUAL_HOST = "connectionFactory";
    public static final String PREFIX_SIMPLE_RABBIT_LISTENER_CONTAINER_FACTORY_PER_VIRTUAL_HOST = "containerFactory";
    public static final String PREFIX_RABBIT_ADMIN_PER_VIRTUAL_HOST = "rabbitAdmin";
    public static final String PREFIX_RABBIT_TEMPLATE_PER_VIRTUAL_HOST = "rabbitTemplate";

    public static final String DEFAULT_RABBIT_ADMIN_BEAN_NAME = "defaultRabbitAdmin";
    public static final String CONNECTION_FACTORY_BEAN_NAME = "tsRabbitConnectionFactory";

    private TunedRabbitConstants() {
        //Do nothing because this is a private constructor
    }

}
