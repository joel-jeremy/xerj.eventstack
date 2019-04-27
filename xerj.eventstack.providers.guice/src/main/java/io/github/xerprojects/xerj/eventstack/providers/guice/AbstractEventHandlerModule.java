package io.github.xerprojects.xerj.eventstack.providers.guice;

import java.lang.reflect.ParameterizedType;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Types;

import io.github.xerprojects.xerj.eventstack.Event;
import io.github.xerprojects.xerj.eventstack.EventHandler;

public abstract class AbstractEventHandlerModule extends AbstractModule {

    @SuppressWarnings("unchecked")
    protected <TEvent extends Event> void bindEventHandlers(
            Class<TEvent> eventType, 
            Iterable<Class<? extends EventHandler<TEvent>>> handlerClasses) {

        ParameterizedType eventHandlerType = Types.newParameterizedType(EventHandler.class, eventType);

        Multibinder<EventHandler<TEvent>> mb = (Multibinder<EventHandler<TEvent>>)Multibinder.newSetBinder(
            binder(), 
            Key.get(eventHandlerType));

        for (Class<? extends EventHandler<TEvent>> handlerClass : handlerClasses) {
            mb.addBinding().to(handlerClass);
        }
    }
}