package io.github.xerprojects.xerj.eventstack.providers.guice;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

import io.github.xerprojects.xerj.eventstack.Event;
import io.github.xerprojects.xerj.eventstack.EventHandler;
import io.github.xerprojects.xerj.eventstack.EventHandlerProvider;

public class GuiceEventHandlerProvider implements EventHandlerProvider {

    private final Injector injector;

    public GuiceEventHandlerProvider(Injector injector) {
        if (injector == null) {
            throw new IllegalArgumentException("Injector must not be null.");
        }
        
        this.injector = injector;
    }

    @Override
    public <TEvent extends Event> Iterable<EventHandler<TEvent>> getEventHandlersFor(Class<TEvent> eventType) {
        if (eventType == null) {
            throw new IllegalArgumentException("Event type must not be null.");
        }

        @SuppressWarnings("unchecked")
        TypeLiteral<EventHandler<TEvent>> eventHandlerType = (TypeLiteral<EventHandler<TEvent>>)TypeLiteral.get(
            Types.newParameterizedType(EventHandler.class, eventType));
        
        List<Binding<EventHandler<TEvent>>> bindings = injector.findBindingsByType(eventHandlerType);

        if (bindings.isEmpty()) {
            return new ArrayList<>(0);
        }

        return bindings.stream().map(b -> b.getProvider().get()).collect(Collectors.toList());
    }
    
}