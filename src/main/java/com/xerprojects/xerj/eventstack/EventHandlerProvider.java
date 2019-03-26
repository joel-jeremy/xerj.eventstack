package com.xerprojects.xerj.eventstack;

public interface EventHandlerProvider {
	<TEvent extends Event> Iterable<EventHandler<TEvent>> getEventHandlersFor(Class<TEvent> eventType);
}
