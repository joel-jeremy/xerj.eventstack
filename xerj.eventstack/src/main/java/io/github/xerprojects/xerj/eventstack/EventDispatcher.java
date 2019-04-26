package io.github.xerprojects.xerj.eventstack;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class EventDispatcher {
	
	private static final CompletableFuture<Void> COMPLETED_FUTURE = CompletableFuture.completedFuture(null);

	private final EventHandlerProvider provider;

	public EventDispatcher(EventHandlerProvider provider) {
		if (provider == null) {
			throw new IllegalArgumentException("Provider must not be null.");
		}

		this.provider = provider;
	}

	@SuppressWarnings("unchecked")
	public <TEvent extends Event> CompletableFuture<Void> send(TEvent event) {
		if (event == null) {
			throw new IllegalArgumentException("Event must not be null.");
		}

		Class<TEvent> actualCommandType = (Class<TEvent>) event.getClass();

		Iterable<EventHandler<TEvent>> resolvedHandlers = provider.getEventHandlersFor(actualCommandType);

		if (!resolvedHandlers.iterator().hasNext()) {
			// No handlers, so just complete the operation.
			return COMPLETED_FUTURE;
		}
		
		List<CompletableFuture<Void>> futures = StreamSupport.stream(resolvedHandlers.spliterator(), true)
			.map(h -> h.handle(event))
			.collect(Collectors.toList());

		return CompletableFuture.allOf(toArray(futures));
	}

	@SuppressWarnings("unchecked")
	private static CompletableFuture<Void>[] toArray(List<CompletableFuture<Void>> futures) {
		CompletableFuture<Void>[] futureArray = (CompletableFuture<Void>[])new CompletableFuture[futures.size()];
		return futures.toArray(futureArray);
	}
}
