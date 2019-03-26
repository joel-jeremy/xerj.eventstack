package com.xerprojects.xerj.eventstack;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.xerprojects.xerj.eventstack.entities.TestEvent;
import com.xerprojects.xerj.eventstack.entities.TestEventHandler;
import com.xerprojects.xerj.eventstack.providers.InProcessEventHandlerProvider;

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
			
			var provider = new InProcessEventHandlerProvider(config -> 
					config.registerEventHandler(TestEvent.class, () -> eventHandler1)
						.registerEventHandler(TestEvent.class, () -> eventHandler2)
						.registerEventHandler(TestEvent.class, () -> eventHandler3));
			
			EventDispatcher delegator = EventDispatcher.builder()
					.addEventHandlerProvider(provider)
					.build();
			
			var event = new TestEvent();
			
			delegator.send(event);
			
			assertTrue(eventHandler1.hasHandledEvent(event));
			assertTrue(eventHandler2.hasHandledEvent(event));
			assertTrue(eventHandler3.hasHandledEvent(event));
		}
		
		@Test
		public void shouldSendEventsToRegisteredEventHandlers() {
			var eventHandler1 = new TestEventHandler();
			var eventHandler2 = new TestEventHandler();
			var eventHandler3 = new TestEventHandler();
			
			var provider = new InProcessEventHandlerProvider(config -> 
					config.registerEventHandler(TestEvent.class, () -> eventHandler1)
						.registerEventHandler(TestEvent.class, () -> eventHandler2)
						.registerEventHandler(TestEvent.class, () -> eventHandler3));
			
			EventDispatcher delegator = EventDispatcher.builder()
					.addEventHandlerProvider(provider)
					.build();
			
			var event1 = new TestEvent();
			var event2 = new TestEvent();
			var event3 = new TestEvent();
			
			delegator.send(event1);
			delegator.send(event2);
			delegator.send(event3);
			
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
