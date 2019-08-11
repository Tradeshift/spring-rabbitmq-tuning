package com.tradeshift.amqp.rabbit.properties;

import org.springframework.boot.context.properties.ConfigurationPropertiesBindHandlerAdvisor;
import org.springframework.boot.context.properties.bind.BindContext;
import org.springframework.boot.context.properties.bind.BindHandler;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.validation.ValidationBindHandler;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.stereotype.Component;

@Component
public class TunedRabbitPropertiesBindHandlerAdvisor implements ConfigurationPropertiesBindHandlerAdvisor {

    @Override
    public BindHandler apply(final BindHandler bindHandler) {
        return new ValidationBindHandler() {
            @Override
            public <T> Bindable<T> onStart(ConfigurationPropertyName name, Bindable<T> target, BindContext context) {
                ConfigurationPropertyName defaultName = findDefaultName(name);
                if (defaultName != null) {
                    BindResult<T> result = context.getBinder().bind(defaultName, target);
                    if (result.isBound()) {
                        return target.withExistingValue(result.get());
                    }
                }
                return bindHandler.onStart(name, target, context);
            }
        };
    }

    private ConfigurationPropertyName findDefaultName(final ConfigurationPropertyName name) {
        final ConfigurationPropertyName from = ConfigurationPropertyName.of("spring.rabbitmq.custom");
        final ConfigurationPropertyName to = ConfigurationPropertyName.of("spring.rabbitmq.custom.shared");

        if (from.isAncestorOf(name) && name.getNumberOfElements() > from.getNumberOfElements()) {
            ConfigurationPropertyName defaultName = to;
            for (int i = from.getNumberOfElements() + 1; i < name.getNumberOfElements(); i++) {
                defaultName = defaultName.append(name.getElement(i, ConfigurationPropertyName.Form.UNIFORM));
            }
            return defaultName;
        }

        return null;
    }

}
