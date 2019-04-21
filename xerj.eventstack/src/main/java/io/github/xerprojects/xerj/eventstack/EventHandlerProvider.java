package io.github.xerprojects.xerj.eventstack;

public interface EventHandlerProvider {
	<TEvent extends Event> Iterable<EventHandler<TEvent>> getEventHandlersFor(Class<TEvent> eventType);
}
