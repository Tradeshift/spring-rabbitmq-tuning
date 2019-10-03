package com.tradeshift.amqp.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tradeshift.amqp.constants.LogLevel;
import com.tradeshift.amqp.rabbit.properties.TunedRabbitProperties;

public class TunedLogger {

    private final Logger log;

    public TunedLogger(Class<?> tClass) {
        this.log = LoggerFactory.getLogger(tClass);
    }

    public static TunedLogger init(Class<?> tClass) {
        return new TunedLogger(tClass);
    }

    public void info(TunedRabbitProperties properties, String message, Object... params) {
        log(properties, LogLevel.INFO, message, params);
    }

    public void warn(TunedRabbitProperties properties, String message, Object... params) {
        log(properties, LogLevel.WARN, message, params);
    }

    public void error(TunedRabbitProperties properties, String message, Object... params) {
        log(properties, LogLevel.ERROR, message, params);
    }

    public void log(TunedRabbitProperties properties, LogLevel level, String message, Object... params) {
        if (properties.isEnableLogs()) {
            if (LogLevel.INFO.equals(level)) {
                log.info(message, params);
            } else if (LogLevel.WARN.equals(level)) {
                log.warn(message, params);
            } else if (LogLevel.ERROR.equals(level)) {
                log.error(message, params);
            } else if (LogLevel.TRACE.equals(level)) {
                log.trace(message, params);
            } else if (LogLevel.DEBUG.equals(level)) {
                log.debug(message, params);
            }
        }
    }

}
