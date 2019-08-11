package com.tradeshift.amqp.rabbit.properties;

import com.tradeshift.amqp.autoconfigure.TunedRabbitAutoConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

public class TunedRabbitPropertiesBindHandlerAdvisorTest {

    @Test
    public void should_merge_with_shared_configs() {
        final TunedRabbitPropertiesMap tunedRabbitPropertiesMap = new SpringApplicationBuilder(SampleConfiguration.class)
                .web(WebApplicationType.NONE)
                .run("--spring.jmx.enabled=false",
                        "--spring.rabbitmq.custom.my-event.queue=my-event.queue",
                        "--spring.rabbitmq.custom.my-event.max-retries-attempts=5",
                        "--spring.rabbitmq.custom.second-event.queue=second-event.queue",
                        "--spring.rabbitmq.custom.second-event.max-retries-attempts=6",
                        "--spring.rabbitmq.custom.second-event.host=rabbit.host.internet",
                        "--spring.rabbitmq.custom.second-event.port=5672",
                        "--spring.rabbitmq.custom.shared.host=rabbit.host.intranet",
                        "--spring.rabbitmq.custom.shared.port=2612",
                        "--spring.rabbitmq.custom.shared.auto-create=true"
                )
                .getBean(TunedRabbitPropertiesMap.class);

        final TunedRabbitProperties myEvent = tunedRabbitPropertiesMap.get("my-event");
        Assert.assertEquals("my-event.queue", myEvent.getQueue());
        Assert.assertEquals(Integer.valueOf(5), myEvent.getMaxRetriesAttempts());
        Assert.assertEquals("rabbit.host.intranet", myEvent.getHost());
        Assert.assertEquals(2612, myEvent.getPort());
        Assert.assertTrue(myEvent.isAutoCreate());

        final TunedRabbitProperties secondEvent = tunedRabbitPropertiesMap.get("second-event");
        Assert.assertEquals("second-event.queue", secondEvent.getQueue());
        Assert.assertEquals(Integer.valueOf(6), secondEvent.getMaxRetriesAttempts());
        Assert.assertEquals("rabbit.host.internet", secondEvent.getHost());
        Assert.assertEquals(5672, secondEvent.getPort());
        Assert.assertTrue(secondEvent.isAutoCreate());
    }

    @EnableConfigurationProperties(TunedRabbitPropertiesMap.class)
    @SpringBootApplication(exclude = TunedRabbitAutoConfiguration.class)
    static class SampleConfiguration {
    }

}


