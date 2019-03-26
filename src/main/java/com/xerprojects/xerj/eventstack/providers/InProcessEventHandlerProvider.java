package com.xerprojects.xerj.eventstack.providers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.xerprojects.xerj.eventstack.Event;
import com.xerprojects.xerj.eventstack.EventHandler;
import com.xerprojects.xerj.eventstack.EventHandlerInstanceFactory;
import com.xerprojects.xerj.eventstack.EventHandlerProvider;

public class InProcessEventHandlerProvider implements EventHandlerProvider {
	private final HashMap<Class<? extends Event>, List<RegisteredEventHandler<? extends Event>>> eventHandlersByEventType = new HashMap<>();
	
	public InProcessEventHandlerProvider(Consumer<Config> configuration) {
		
		if (configuration == null) {
			throw new IllegalArgumentException("Config must not be null.");
		}
		
		InProcessConfig config = new InProcessConfig();
		configuration.accept(config);
		
		for (RegisteredEventHandler<? extends Event> registered : config) {
			
			List<RegisteredEventHandler<? extends Event>> registeredHandlers =
					eventHandlersByEventType.getOrDefault(registered.getEventType(), new ArrayList<>());
			
			registeredHandlers.add(registered);
			
			eventHandlersByEventType.put(registered.getEventType(), registeredHandlers);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <TEvent extends Event> Iterable<EventHandler<TEvent>> getEventHandlersFor(Class<TEvent> eventType) {
		 List<RegisteredEventHandler<? extends Event>> registeredHandlers = 
				 eventHandlersByEventType.getOrDefault(eventType, new ArrayList<>());
		 
		 if (registeredHandlers.size() == 0) {
			 return new ArrayList<>(0);
		 }
		 
		 return registeredHandlers.parallelStream()
			 .map(r -> ((RegisteredEventHandler<TEvent>)r).getInstance())
			 .collect(Collectors.toUnmodifiableList());
	}
	
	public static interface Config {
		<TEvent extends Event> Config registerEventHandler(
				Class<TEvent> eventType, 
				EventHandlerInstanceFactory<TEvent> instanceFactory);
	}
	
	private static final class InProcessConfig implements Config, Iterable<RegisteredEventHandler<? extends Event>> {
		private final ArrayList<RegisteredEventHandler<? extends Event>> registeredEventHandlers = new ArrayList<>();
		
		@Override
		public <TEvent extends Event> Config registerEventHandler(Class<TEvent> eventType,
				EventHandlerInstanceFactory<TEvent> instanceFactory) {
			
			if (eventType == null) {
				throw new IllegalArgumentException("Event type must not be null.");
			}
			
			if (instanceFactory == null) {
				throw new IllegalArgumentException("Instance provider must not be null.");
			}
			
			registeredEventHandlers.add(new RegisteredEventHandler<>(eventType, instanceFactory));
			return this;
		}
		
		@Override
		public Iterator<RegisteredEventHandler<? extends Event>> iterator() {
			return registeredEventHandlers.iterator();
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
					throw new IllegalStateException("Registered event handler instance provider for " + eventType + " supplied a null instance.");
				}
				
				return instance;
			} catch (Exception e) {
				throw new IllegalStateException("Registered event handler instance provider for " + eventType + " has thrown an exception.");
			}
		}
	}
}
