package io.github.xerprojects.xerj.eventstack.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import io.github.xerprojects.xerj.eventstack.Event;
import io.github.xerprojects.xerj.eventstack.EventHandler;
import io.github.xerprojects.xerj.eventstack.EventHandlerProvider;

public class RegistryEventHandlerProvider implements EventHandlerProvider {
	private final MapRegistry mapRegistry;
	
	public RegistryEventHandlerProvider(Consumer<Registry> registryConfiguration) {
		
		if (registryConfiguration == null) {
			throw new IllegalArgumentException("Registry configuration must not be null.");
		}
		
		mapRegistry = new MapRegistry();
		registryConfiguration.accept(mapRegistry);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <TEvent extends Event> Iterable<EventHandler<TEvent>> getEventHandlersFor(Class<TEvent> eventType) {
		 List<RegisteredEventHandler<? extends Event>> registeredHandlers = 
		 	mapRegistry.getOrDefault(eventType, new ArrayList<>());
		 
		 if (registeredHandlers.size() == 0) {
			Iterable<EventHandler<TEvent>> emptyList = Collections.EMPTY_LIST;
			return emptyList;
		 }
		 
		 return registeredHandlers.parallelStream()
			 .map(r -> ((RegisteredEventHandler<TEvent>)r).getInstance())
			 .collect(Collectors.toList());
	}
	
	public static interface Registry {
		<TEvent extends Event> Registry registerEventHandler(
				Class<TEvent> eventType, 
				EventHandlerInstanceFactory<TEvent> instanceFactory);
	}

	public static interface EventHandlerInstanceFactory<TEvent extends Event> {
		EventHandler<TEvent> getInstance();
	}
	
	private static final class MapRegistry 
			extends HashMap<Class<? extends Event>, List<RegisteredEventHandler<? extends Event>>> 
			implements Registry {
				
		private static final long serialVersionUID = 549485089986794134L;
		
		@Override
		public <TEvent extends Event> Registry registerEventHandler(
				Class<TEvent> eventType,
				EventHandlerInstanceFactory<TEvent> instanceFactory) {
			
			if (eventType == null) {
				throw new IllegalArgumentException("Event type must not be null.");
			}
			
			if (instanceFactory == null) {
				throw new IllegalArgumentException("Instance factory must not be null.");
			}

			List<RegisteredEventHandler<? extends Event>> handlers = getOrDefault(eventType, new ArrayList<>());
			handlers.add(new RegisteredEventHandler<>(eventType, instanceFactory));
			
			put(eventType, handlers);

			return this;
		}
		
	}
	
	private static final class RegisteredEventHandler<TEvent extends Event> {
		
		private final Class<TEvent> eventType;
		private final EventHandlerInstanceFactory<TEvent> instanceFactory;
		
		public RegisteredEventHandler(Class<TEvent> eventType, EventHandlerInstanceFactory<TEvent> instanceFactory) {
			this.eventType = eventType;
			this.instanceFactory = instanceFactory;
		}
		
		public Class<TEvent> getEventType() {
			return eventType;
		}
		
		public EventHandler<TEvent> getInstance() {
			try {
				EventHandler<TEvent> instance = instanceFactory.getInstance();
				
				if (instance == null) {
					throw new IllegalStateException("Registered event handler instance provider for " + getEventType() 
						+ " supplied a null instance. Please check configuration.");
				}
				
				return instance;
			} catch (Exception e) {
				throw new IllegalStateException("Registered event handler instance provider for " + getEventType() 
					+ " has thrown an exception. Please check configuration.", e);
			}
		}
	}
}
