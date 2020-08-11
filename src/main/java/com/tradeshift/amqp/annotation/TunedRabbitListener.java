package com.tradeshift.amqp.annotation;

import java.lang.annotation.Annotation;

import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

public class TunedRabbitListener implements RabbitListener {

    private String id = "";
    private String containerFactory = "";
    private String[] queues = new String[0];
    private Queue[] queuesToDeclare = new Queue[0];
    private boolean exclusive;
    private String priority = "";
    private String admin = "";
    private QueueBinding[] bindings  = new QueueBinding[0];
    private String group = "";
    private String returnExceptions = "";
    private String errorHandler = "";
    private String concurrency = "";
    private String autoStartup = "";
    private String executor = "";
    private String ackMode = "";
    private String replyPostProcessor = "";


    public TunedRabbitListener(RabbitListener rabbitListener) {
        this.id = rabbitListener.id();
        this.containerFactory = rabbitListener.containerFactory();
        this.queues = rabbitListener.queues();
        this.queuesToDeclare = rabbitListener.queuesToDeclare();
        this.exclusive = rabbitListener.exclusive();
        this.priority = rabbitListener.priority();
        this.admin = rabbitListener.admin();
        this.bindings = rabbitListener.bindings();
        this.group = rabbitListener.group();
        this.returnExceptions = rabbitListener.returnExceptions();
        this.errorHandler = rabbitListener.errorHandler();
        this.concurrency = rabbitListener.concurrency();
        this.autoStartup = rabbitListener.autoStartup();
        this.executor = rabbitListener.executor();
        this.ackMode = rabbitListener.ackMode();
        this.replyPostProcessor = rabbitListener.replyPostProcessor();
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public String containerFactory() {
        return this.containerFactory;
    }

    @Override
    public String[] queues() {
        return this.queues;
    }

    @Override
    public Queue[] queuesToDeclare() {
        return this.queuesToDeclare;
    }

    @Override
    public boolean exclusive() {
        return this.exclusive;
    }

    @Override
    public String priority() {
        return this.priority;
    }

    @Override
    public String admin() {
        return this.admin;
    }

    @Override
    public QueueBinding[] bindings() {
        return this.bindings;
    }

    @Override
    public String group() {
        return this.group;
    }

    @Override
    public String returnExceptions() {
        return this.returnExceptions;
    }

    @Override
    public String errorHandler() {
        return this.errorHandler;
    }

    @Override
    public String concurrency() {
        return this.concurrency;
    }

    @Override
    public String autoStartup() {
        return this.autoStartup;
    }

    @Override
    public String executor() {
        return this.executor;
    }

    @Override
    public String ackMode() {
        return this.ackMode;
    }

    @Override
    public String replyPostProcessor() {
        return this.replyPostProcessor;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return RabbitListener.class;
    }

    public void setContainerFactory(String containerFactory) {
        this.containerFactory = containerFactory;
    }
}
