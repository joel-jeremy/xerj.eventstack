package io.github.xerprojects.xerj.eventstack;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.github.xerprojects.xerj.eventstack.entities.TestEvent;
import io.github.xerprojects.xerj.eventstack.entities.TestEventHandler;
import io.github.xerprojects.xerj.eventstack.providers.RegistryEventHandlerProvider;

public class EventDispatcherTests {
	
	@Nested
	public class SendMethod {
		@Test
		public void shouldSendEventToRegisteredEventHandlers() {
			var eventHandler1 = new TestEventHandler();
			var eventHandler2 = new TestEventHandler();
			var eventHandler3 = new TestEventHandler();
			
			var provider = new RegistryEventHandlerProvider(registry -> 
					registry.registerEventHandler(TestEvent.class, () -> eventHandler1)
						.registerEventHandler(TestEvent.class, () -> eventHandler2)
						.registerEventHandler(TestEvent.class, () -> eventHandler3));
			
			var dispatcher = new EventDispatcher(provider);
			
			var event = new TestEvent();
			
			dispatcher.send(event).join();
			
			assertTrue(eventHandler1.hasHandledEvent(event));
			assertTrue(eventHandler2.hasHandledEvent(event));
			assertTrue(eventHandler3.hasHandledEvent(event));
		}
		
		@Test
		public void shouldSendEventsToRegisteredEventHandlers() {
			var eventHandler1 = new TestEventHandler();
			var eventHandler2 = new TestEventHandler();
			var eventHandler3 = new TestEventHandler();
			
			var provider = new RegistryEventHandlerProvider(registry -> 
					registry.registerEventHandler(TestEvent.class, () -> eventHandler1)
						.registerEventHandler(TestEvent.class, () -> eventHandler2)
						.registerEventHandler(TestEvent.class, () -> eventHandler3));
			
			var dispatcher = new EventDispatcher(provider);
			
			var event1 = new TestEvent();
			var event2 = new TestEvent();
			var event3 = new TestEvent();
			
			CompletableFuture<Void> c1 = dispatcher.send(event1);
			CompletableFuture<Void> c2 = dispatcher.send(event2);
			CompletableFuture<Void> c3 = dispatcher.send(event3);

			CompletableFuture.allOf(c1, c2, c3).join();
			
			assertTrue(eventHandler1.hasHandledEvent(event1));
			assertTrue(eventHandler2.hasHandledEvent(event1));
			assertTrue(eventHandler3.hasHandledEvent(event1));
			
			assertTrue(eventHandler1.hasHandledEvent(event2));
			assertTrue(eventHandler2.hasHandledEvent(event2));
			assertTrue(eventHandler3.hasHandledEvent(event2));
			
			assertTrue(eventHandler1.hasHandledEvent(event3));
			assertTrue(eventHandler2.hasHandledEvent(event3));
			assertTrue(eventHandler3.hasHandledEvent(event3));
		}
	}
}
