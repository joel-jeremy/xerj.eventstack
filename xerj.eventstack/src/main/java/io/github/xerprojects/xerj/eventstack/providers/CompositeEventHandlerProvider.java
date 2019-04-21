package io.github.xerprojects.xerj.eventstack.providers;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import io.github.xerprojects.xerj.eventstack.Event;
import io.github.xerprojects.xerj.eventstack.EventHandler;
import io.github.xerprojects.xerj.eventstack.EventHandlerProvider;

public class CompositeEventHandlerProvider implements EventHandlerProvider {

	private final ArrayList<EventHandlerProvider> providers = new ArrayList<>();

	public CompositeEventHandlerProvider(Iterable<EventHandlerProvider> providers) {
		
		if (providers == null) {
			throw new IllegalArgumentException("Providers must not be null.");
		}

		if (!providers.iterator().hasNext()) {
			throw new IllegalArgumentException("Providers must not be empty.");
		}
		
		providers.forEach(this.providers::add);
	}
	
	@Override
	public <TEvent extends Event> Iterable<EventHandler<TEvent>> getEventHandlersFor(
			Class<TEvent> eventType) {
		return providers.parallelStream()
			.map(p -> p.getEventHandlersFor(eventType))
			.flatMap(p -> StreamSupport.stream(p.spliterator(), true))
			.collect(Collectors.toList());
	}

}
