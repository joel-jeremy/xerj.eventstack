package io.github.xerprojects.xerj.eventstack.providers.springcontext;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ResolvableType;

import io.github.xerprojects.xerj.eventstack.Event;
import io.github.xerprojects.xerj.eventstack.EventHandler;
import io.github.xerprojects.xerj.eventstack.EventHandlerProvider;

public class SpringContextEventHandlerProvider implements EventHandlerProvider {

    private final ApplicationContext appContext;

    public SpringContextEventHandlerProvider(ApplicationContext appContext) {
        if (appContext == null) {
            throw new IllegalArgumentException("Application context must not be null.");
        }

        this.appContext = appContext;
    }

    @Override
    public <TEvent extends Event> Iterable<EventHandler<TEvent>> getEventHandlersFor(Class<TEvent> eventType) {
        if (eventType == null) {
            throw new IllegalArgumentException("Event type must not be null.");
        }

        String[] handlerBeanNames = appContext.getBeanNamesForType(
            ResolvableType.forClassWithGenerics(EventHandler.class, eventType));

        ArrayList<EventHandler<TEvent>> handlerInstances = new ArrayList<>(handlerBeanNames.length);

        for (String handlerBeanName : handlerBeanNames) {
            @SuppressWarnings("unchecked")
            EventHandler<TEvent> handler = (EventHandler<TEvent>)appContext.getBean(handlerBeanName, EventHandler.class);
            handlerInstances.add(handler);
        }

        return handlerInstances;
    }    
}