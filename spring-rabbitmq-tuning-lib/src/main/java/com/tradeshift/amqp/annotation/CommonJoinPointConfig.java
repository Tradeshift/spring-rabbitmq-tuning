package com.tradeshift.amqp.annotation;

import org.aspectj.lang.annotation.Pointcut;

public class CommonJoinPointConfig {

    @Pointcut("@annotation(com.tradeshift.amqp.annotation.EnableRabbitRetryAndDlq)")
    public void enableRabbitRetryAndDlqAnnotation() {
        // Do nothing because the whole logic are around the Pointcut annotation.
    }

}
