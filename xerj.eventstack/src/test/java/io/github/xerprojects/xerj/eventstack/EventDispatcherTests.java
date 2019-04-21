package io.github.xerprojects.xerj.eventstack;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.github.xerprojects.xerj.eventstack.entities.TestEvent;
import io.github.xerprojects.xerj.eventstack.entities.TestEventHandler;
import io.github.xerprojects.xerj.eventstack.providers.RegistryEventHandlerProvider;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class EventDispatcherTests {
	
	@Nested
	public class BuilderMethod {
		@Test
		public void shouldNeverReturnNull() {
			EventDispatcher.Builder builder = EventDispatcher.builder();
			assertNotNull(builder);
		}
	}
	
	@Nested
	public class BuilderTests {
		@Nested
		public class AddEventHandlerProviderMethod {
			@Test
			public void shouldThrowWhenProviderArgumentIsNull() {
				assertThrows(IllegalArgumentException.class, () -> {
					EventDispatcher.Builder builder = EventDispatcher.builder();
					// Add null.
					builder.addEventHandlerProvider(null);
				});
			}
		}
	}
	
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
			
			EventDispatcher dispatcher = EventDispatcher.builder()
					.addEventHandlerProvider(provider)
					.build();
			
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
			
			EventDispatcher dispatcher = EventDispatcher.builder()
					.addEventHandlerProvider(provider)
					.build();
			
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
