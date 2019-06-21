package com.tradeshift.amqp.rabbit.properties;

import java.util.HashMap;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.rabbitmq.custom")
public class TunedRabbitPropertiesMap extends HashMap<String, TunedRabbitProperties> {
}
