package com.tradeshift.amqp.rabbit.properties;

import java.util.HashMap;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.rabbitmq.custom")
public class TunedRabbitPropertiesMap extends HashMap<String, TunedRabbitProperties> {

    @PostConstruct
    public void postConstructBean() {
        this.remove("shared");
    }

    public Stream<Entry<String, TunedRabbitProperties>> stream() {
        return entrySet().stream();
    }

}
