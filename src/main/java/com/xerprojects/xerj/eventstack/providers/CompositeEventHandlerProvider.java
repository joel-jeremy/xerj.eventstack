package com.xerprojects.xerj.eventstack.providers;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.xerprojects.xerj.eventstack.Event;
import com.xerprojects.xerj.eventstack.EventHandler;
import com.xerprojects.xerj.eventstack.EventHandlerProvider;

public class CompositeEventHandlerProvider implements EventHandlerProvider {

	private final ArrayList<EventHandlerProvider> providers = new ArrayList<>();

	public CompositeEventHandlerProvider(Iterable<EventHandlerProvider> providers) {
		
		if (providers == null) {
			throw new IllegalArgumentException("Providers must not be null.");
		}
		
		providers.forEach(this.providers::add);

		if (this.providers.size() == 0) {
			throw new IllegalArgumentException("Providers must not be empty.");
		}
	}
	
	@Override
	public <TEvent extends Event> Iterable<EventHandler<TEvent>> getEventHandlersFor(
			Class<TEvent> eventType) {
		return providers.parallelStream()
			.map(p -> p.getEventHandlersFor(eventType))
			.flatMap(p -> StreamSupport.stream(p.spliterator(), true))
			.collect(Collectors.toUnmodifiableList());
	}

}
