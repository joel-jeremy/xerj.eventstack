package com.xerprojects.xerj.eventstack;

import java.util.ArrayList;
import java.util.stream.Stream;

import com.xerprojects.xerj.eventstack.providers.CompositeEventHandlerProvider;

public interface EventDispatcher {
	<TEvent extends Event> void send(TEvent event);

	static Builder builder() {
		return new Builder();
	}
	
	public static final class Builder {
		public final ArrayList<EventHandlerProvider> providers = new ArrayList<>();
		
		private Builder() { }
		
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
				return new InternalEventDelegator(providers.get(0));
			}
			
			if (providersCount == 0) {
				return InternalEventDelegator.NULL;
			}
			
			return new InternalEventDelegator(
					new CompositeEventHandlerProvider(providers));
		}
		
		private static final class InternalEventDelegator implements EventDispatcher {
			
			static final InternalEventDelegator NULL = new InternalEventDelegator(InternalEventDelegator::nullProvider);

			private final EventHandlerProvider provider;

			public InternalEventDelegator(EventHandlerProvider provider) {
				this.provider = provider;
			}
			
			@Override
			public <TEvent extends Event> void send(TEvent event) {
				
				if (event == null) {
					throw new IllegalArgumentException("Event must not be null.");
				}
				
				@SuppressWarnings("unchecked")
				Class<TEvent> genericClass = (Class<TEvent>) event.getClass();
				Iterable<EventHandler<TEvent>> resolvedHandlers = provider.getEventHandlersFor(genericClass);
				resolvedHandlers.forEach(h -> h.handle(event));
			}
			
			private static <TEvent extends Event> Iterable<EventHandler<TEvent>> nullProvider(
					Class<TEvent> eventType) {
				return new ArrayList<>(0);
			}
		}
		
	}
}
