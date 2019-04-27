package io.github.xerprojects.xerj.eventstack.providers.guice.entities;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import io.github.xerprojects.xerj.eventstack.Event;
import io.github.xerprojects.xerj.eventstack.EventHandler;

public abstract class BaseEventHandler<TEvent extends Event> implements EventHandler<TEvent> {
	private static final CompletableFuture<Void> COMPLETED_FUTURE = CompletableFuture.completedFuture(null); 

	private final ArrayList<TEvent> handledEvents = new ArrayList<>();
	
	@Override
	public CompletableFuture<Void> handle(TEvent event) {
		handledEvents.add(event);
		return COMPLETED_FUTURE;
	}
	
	public boolean hasHandledEvent(Event event) {
		// Instance comparison.
		return handledEvents.stream().anyMatch(e -> e == event);
	}

}
