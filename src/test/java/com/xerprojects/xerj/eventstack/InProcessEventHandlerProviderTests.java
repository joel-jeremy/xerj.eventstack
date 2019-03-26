package com.xerprojects.xerj.eventstack;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.xerprojects.xerj.eventstack.entities.TestEvent;
import com.xerprojects.xerj.eventstack.entities.TestEventHandler;
import com.xerprojects.xerj.eventstack.providers.InProcessEventHandlerProvider;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class InProcessEventHandlerProviderTests {
	
	@Nested
	public class Constructor {
		@Test
		public void shouldThrowWhenConfigConsumerArgumentIsNull() {
			assertThrows(IllegalArgumentException.class, () -> {
				@SuppressWarnings("unused")
				var provider = new InProcessEventHandlerProvider(null);
			});
		}
	}
	
	@Nested
	public class InProcessConfigTests {
		@Nested
		public class RegisterEventHandlerMethod {

			@Test
			public void shouldThrowWhenEventTypeArgumentIsNull() {
				assertThrows(IllegalArgumentException.class, () -> {
					new InProcessEventHandlerProvider(config -> 
						config.registerEventHandler(null, () -> new TestEventHandler()));
				});
			}
			
			@Test
			public void shouldThrowWhenEventHandlerInstanceFactoryArgumentIsNull() {
				assertThrows(IllegalArgumentException.class, () -> {
					new InProcessEventHandlerProvider(config -> 
						config.registerEventHandler(TestEvent.class, null));
				});
			}
			
			@Test
			public void shouldSupportMultipleHandlersForAnEvent() {
				new InProcessEventHandlerProvider(config -> 
					config.registerEventHandler(TestEvent.class, () -> new TestEventHandler())
						.registerEventHandler(TestEvent.class, () -> new TestEventHandler()));
			}
		}
	}
	
	@Nested
	public class GetEventHandlersForMethod {
		
		@Test
		public void shouldProvideRegisteredEventHandlers() {
			
			var testEventHandler1 = new TestEventHandler();
			var testEventHandler2 = new TestEventHandler();
			
			var provider = new InProcessEventHandlerProvider(config ->
				config.registerEventHandler(TestEvent.class, () -> testEventHandler1)
					.registerEventHandler(TestEvent.class, () -> testEventHandler2));
			
			Iterable<EventHandler<TestEvent>> resolvedHandlers = provider.getEventHandlersFor(TestEvent.class);			
			
			assertTrue(StreamSupport.stream(resolvedHandlers.spliterator(), false)
				.anyMatch(h -> h == testEventHandler1 || h == testEventHandler2));
		}
	
	}
}
