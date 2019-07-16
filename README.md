[![CircleCI](https://circleci.com/gh/Tradeshift/spring-rabbitmq-tuning.svg?style=svg)](https://circleci.com/gh/Tradeshift/spring-rabbitmq-tuning)

# Spring RabbitMQ Tuning

This library makes it easy to configure RabbitMQ for use with Spring.

# Table of Contents
- [Technologies](#technologies)
- [Autoconfiguration](#autoconfiguration)
- [Properties Docs](#properties-docs)
  - [Default Values](#default-values)
- [Queues Strategy](#queues-strategy)
- [Lib Usage](#lib-usage)
  - [Publisher](#publisher)
  - [Listener](#listener)

___

## Technologies
 This lib uses **Java 8** and **Spring Boot 2.1.x.RELEASE**

## Autoconfiguration

Today if we want to create a custom communication (like one using **ssl** for example) with RabbitMQ using Spring AMQP, we need to create a couple of beans like **ConnectionFactory**, **RabbitAdmin**, **RabbitTemplate**, **AbstractRabbitListenerContainerFactory**.
And if you have more than 1 **virtual host** in your RabbitMQ, you need to create this ecosystem for each virtual host and management the **RabbitTemplate** and **RabbitListener**.

All these configurations take a long time to work well and could be a headache.

Thinking in this problem, this library auto-configure all these beans and make the management for you.

The only thing that you need to do is define the configuration in your application.properties / yaml and the magic happens!

Example:
```properties
spring.rabbitmq.custom.some-event.host=localhost
spring.rabbitmq.custom.some-event.port=5672
spring.rabbitmq.custom.some-event.ttlRetryMessage=5000
spring.rabbitmq.custom.some-event.maxRetriesAttempts=5
spring.rabbitmq.custom.some-event.ttlMultiply=2
spring.rabbitmq.custom.some-event.queueRoutingKey=CREATE.PERMISSION.FOR.ATTACHMENT
spring.rabbitmq.custom.some-event.exchange=ex_create_permission_for_attachment
spring.rabbitmq.custom.some-event.exchangeType=direct
spring.rabbitmq.custom.some-event.queue=queue_create_permission_for_attachment
spring.rabbitmq.custom.some-event.autoCreate=true
spring.rabbitmq.custom.some-event.concurrentConsumers=1
spring.rabbitmq.custom.some-event.maxConcurrentConsumers=1
spring.rabbitmq.custom.some-event.virtualHost=tradeshift
spring.rabbitmq.custom.some-event.primary=true
spring.rabbitmq.custom.some-event.sslConnection=true
spring.rabbitmq.custom.some-event.tlsKeystoreLocation=file:///etc/tradeshift/your-service/tls-keystore.pkcs12
spring.rabbitmq.custom.some-event.tlsKeystorePassword=${RABBITMQ_PASS_CERT}

spring.rabbitmq.custom.another-event.host=localhost
spring.rabbitmq.custom.another-event.port=5672
spring.rabbitmq.custom.another-event.ttlRetryMessage=5000
spring.rabbitmq.custom.another-event.maxRetriesAttempts=5
spring.rabbitmq.custom.another-event.queueRoutingKey=TEST.QUEUE
spring.rabbitmq.custom.another-event.exchange=ex_test_1
spring.rabbitmq.custom.another-event.exchangeType=direct
spring.rabbitmq.custom.another-event.queue=queue_test_1
spring.rabbitmq.custom.another-event.autoCreate=true
spring.rabbitmq.custom.another-event.concurrentConsumers=1
spring.rabbitmq.custom.another-event.maxConcurrentConsumers=1
spring.rabbitmq.custom.another-event.username=guest
spring.rabbitmq.custom.another-event.password=${RABBITMQ_PASS}
``` 

## Properties Docs
  - **ttlRetryMessage**
    - Define the time to live between retries  
  - **maxRetriesAttempts**
    - Define the number of max retries (after this the message will go to *DLQ*)
  - **ttlMultiply**
      - Define the multiplier ttl. For example, if the *ttlRetryMessage* is 5000 (5s) and the *maxRetriesAttempts* is 3, the ttl for the first retry is **5s**, for the second retry is **10s** and for the third retry is **20s**.
  - **queueRoutingKey**
    - Define your routing key
  - **exchange**
    - Define your exchange name
  - **exchangeType**
    - Define your exchange type. Values are *direct*, *topic*, *fanout* (For now we don't have support for headers type).
  - **queue**
    - Define your queue name
  - **queueRetry**
    - Define the name of the retry queue. If you don't define, the library will get **queue name** and concatenated with **.retry**.
  - **queueDlq**
    - Define the name of the dlq queue. If you don't define, the library will get **queue name** and concatenated with **.dlq**.
  - **defaultRetryDlq**
    - Define if you want to use the default resolver to create the name of the retry and dlq queues.
  - **autoCreate**
    - This library has the intelligence to auto create all these architecture for you.
  - **autoCreateForRetryDlq**
    - Define if you want to enable the auto to create for retry queue and dlq queue
  - **autoCreateOnlyForTest**
    - Uses the auto create behavior only in test profile
  - **automaticRecovery**
    - Define if you want automatic recovery behavior
  - **concurrentConsumers**
    - Define the number of the concurrent consumers for this container factory
  - **maxConcurrentConsumers**
    - Define the number of the max concurrent consumers for this container factory
  - **tlsKeystoreLocation**
    - Define the location of your tls file (Only use if you want an SSL connection)
  - **tlsKeystorePassword**
    - Define the password of the certificate
  - **sslConnection**
    - Define if you want an SSL connection
  - **virtualHost**
    - Define your virtual host
  - **host**
    - Define your host
  - **port**
    - Define your port
  - **username**
    - Define your username (in case that is not an SSL connection)
  - **password**
    - Define your password (in case that is not an SSL connection)
  - **primary**
    - Define if this architecture will be your primary beans
  - **enableJsonMessageConverter**
    - Define if you want to use the `Jackson2JsonMessageConverter` as the **default** message converter (per container factory)
  - **enableSnakeCaseForQueuesAndExchangeNames**
    - Define if you want to allow snake case in exchange and queues names.
      
##### Default Values
  - ttlRetryMessage = **5000**
  - maxRetriesAttempts = **3**
  - ttlMultiply = **0**
  - defaultRetryDlq = **true** 
    - It means that the name of the retry and dlq will be the name of the queue with suffix ".retry" and ".dlq"
  - concurrentConsumers = **1**
  - maxConcurrentConsumers = **1**
  - host = **localhost**
  - port = **5672**
  - username = **guest**
  - password = **guest**
  - autoCreate = **false**
  - autoCreateForRetryDlq = **true**
  - autoCreateOnlyForTest = **false**
  - sslConnection = **false**
  - automaticRecovery = **false**
  - primary = **false**
    - If you don't define the autoconfigure will get the first beans and set as primary
  - enableJsonMessageConverter = **false**
  - enableSnakeCaseForQueuesAndExchangeNames = **false**
    - With this option as false, every `_` will be replaced for `.` (Only for queues and exchanges names)
    - With this option as true, every `.` will be replaced for `_` (Only for queues and exchanges names)

## Queues Strategy
By default, RabbitMQ does not provide a retry strategy that we can control all the life cycle of the message.

For example, until [RabbitMQ 3.8](https://github.com/rabbitmq/rabbitmq-server/pull/1889), we do not have a property in the header to control the number of retries that Rabbit already did.

RabbitMQ default behavior: 
- If you didn't define the **[time-to-live](https://www.rabbitmq.com/ttl.html)** argument, RabbitMQ will try to requeue your message forever.
- If you defined a ttl but didn't define a **[dlx](https://www.rabbitmq.com/dlx.html)**, after ttl, RabbitMQ will remove your message to the queue and you will lost the message.
- If you defined a ttl and a **[dlx](https://www.rabbitmq.com/dlx.html)**, after the ttl, RabbitMQ will send the message to the [**exchange**](https://medium.com/faun/different-types-of-rabbitmq-exchanges-9fefd740505d) defined in the dlx.
 
![Default RabbitMQ Behavior](https://i.ibb.co/CnY7K4q/Screenshot-2019-05-15-at-10-37-44.png)
 
> So, but if we want to have a growing ttl (in case of instability, for example), and control the number of retries, how can we do this?

#### Default Spring AMQP
With [Spring AMQP default](https://docs.spring.io/spring-boot/docs/2.1.4.RELEASE/reference/htmlsingle/#common-application-properties), you can define retry configurations using the properties below, when `simple` is the default container bean.

But this strategy has a problem. By default, Spring AMQP will lock your queue when it tries to deliver the message in the retry process.

To resolve this has a workaround that is: Define the `concurrency`. But this way we are overload the JVM, and is not the best approach, and we still need to define the beans manually in our `@Configuration` bean for each connection and container.

```properties
spring.rabbitmq.listener.simple.retry.enabled=true
spring.rabbitmq.listener.simple.retry.initial-interval=2000
spring.rabbitmq.listener.simple.retry.max-attempts=5
spring.rabbitmq.listener.simple.retry.multiplier=2
spring.rabbitmq.listener.simple.max-concurrency=5
spring.rabbitmq.listener.simple.concurrency=5
```

#### Tradeshift Spring AMQP Lib

This library creates a strategy using another queue for retries.
With this, we can control the ttl using the `expiration` message property and we can use the `x-death` message property to control the number of retries.

> *But how?*

We are using the **dlx** concept in the **retry queue** to requeue a message in the **main queue**. Doing this, we have access to the `x-death` property and we can define programmatically the message expiration. 


Note: How this lib is an extension of Spring AMQP, if you want to use the default retry strategy, you can still use this lib only for auto configurations of beans.

![Tradeshift RabbitMQ Behavior](https://i.ibb.co/dLZ5dn4/Screenshot-2019-05-15-at-10-40-13.png)

## Lib Usage

We have an example project in this repo that you can see how to use this lib for publishers and listeners.

#### Publisher

This lib has a class called **RabbitTemplateHandler** and uses it is very simple.
We just need to call the **getRabbitTemplate** method and pass the virtual host as a parameter for getting the correct bean of RabbitTemplate.
Once with this bean, you can call the **convertAndSend** method and pass the **exchange** and the **routing key** as parameters.

Note: You can get the exchange and the routing key using the **@Value** annotation.

Example:

```java
@Value("${spring.rabbitmq.custom.some-event.exchange}")
private String exchangeSomeEvent;

@Value("${spring.rabbitmq.custom.some-event.queueRoutingKey}")
private String routingKeySomeEvent;

@Autowired
private final RabbitTemplateHandler rabbitTemplateHandler;

public void sendMessage(final String message) {
    rabbitTemplateHandler.getRabbitTemplate("some-event").convertAndSend(exchangeSomeEvent, routingKeySomeEvent, message);
}
```

#### Listener

To listeners is very simple too. The only thing that you need to do is annotate a method with the **RabbitListener** annotation and pass the name of **containerFactory**.
> How do I know the name of the correct container factory for each virtual host?

You don't need to know, just pass the name of you event and the library will do the magic for you!

This lib also has an option to enable the **retry** and **dlq** strategy, that is recommended to use.

To enable this behavior is very simple, we just need to annotate our method with the **EnableRabbitRetryAndDlq** annotation and pass the nome of the property as an argument.

Note: You also have an option to specify for which exceptions you want to enable the retry and dlq strategy. By default, the value is **Exception.class**, that means that **all exceptions** will be handled. 

Example:
```java
@RabbitListener(containerFactory = "some-event", queues = "${spring.rabbitmq.custom.some-event.queue}")
@EnableRabbitRetryAndDlq(event = "some-event", exceptions = { IllegalArgumentException.class, RuntimeException.class })
public void onMessage(Message message) {
    ...
}
``` 