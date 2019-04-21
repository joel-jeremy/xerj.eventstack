package io.github.xerprojects.xerj.eventstack;

import static java.util.Collections.EMPTY_LIST;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import io.github.xerprojects.xerj.eventstack.providers.CompositeEventHandlerProvider;

public interface EventDispatcher {
	<TEvent extends Event> CompletableFuture<Void> send(TEvent event);

	static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private static final InternalEventDispatcher NULL_DISPATCHER = new InternalEventDispatcher(
				Builder::nullProvider);
		private final ArrayList<EventHandlerProvider> providers = new ArrayList<>();

		private Builder() {
		}

		public Builder addEventHandlerProvider(EventHandlerProvider provider) {
			if (provider == null) {
				throw new IllegalArgumentException("Provider must not be null.");
			}

			providers.add(provider);
			return this;
		}

		public EventDispatcher build() {

			int providersCount = providers.size();

			if (providersCount == 1) {
				return new InternalEventDispatcher(providers.get(0));
			}

			if (providersCount == 0) {
				return NULL_DISPATCHER;
			}

			return new InternalEventDispatcher(new CompositeEventHandlerProvider(providers));
		}

		private static final class InternalEventDispatcher implements EventDispatcher {

			private static final CompletableFuture<Void> COMPLETED_FUTURE = CompletableFuture.completedFuture(null);

			private final EventHandlerProvider provider;

			public InternalEventDispatcher(EventHandlerProvider provider) {
				if (provider == null) {
					throw new IllegalArgumentException("Provider must not be null.");
				}

				this.provider = provider;
			}

			@SuppressWarnings("unchecked")
			@Override
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
		}

		private static <TEvent extends Event> Iterable<EventHandler<TEvent>> nullProvider(Class<TEvent> eventType) {
			@SuppressWarnings("unchecked")
			Iterable<EventHandler<TEvent>> emptyList = EMPTY_LIST;
			return emptyList;
		}

		@SuppressWarnings("unchecked")
		private static CompletableFuture<Void>[] toArray(List<CompletableFuture<Void>> futures) {
			CompletableFuture<Void>[] futureArray = (CompletableFuture<Void>[])new CompletableFuture[futures.size()];
			return futures.toArray(futureArray);
		}
	}
}
