package io.github.xerprojects.xerj.eventstack.providers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.github.xerprojects.xerj.eventstack.EventHandler;
import io.github.xerprojects.xerj.eventstack.entities.TestEvent;
import io.github.xerprojects.xerj.eventstack.entities.TestEventHandler;

public class RegistryEventHandlerProviderTests {
	
	@Nested
	public class Constructor {
		@Test
		public void shouldThrowWhenRegistryConfigurationArgumentIsNull() {
			assertThrows(IllegalArgumentException.class, () -> {
				new RegistryEventHandlerProvider(null);
			});
		}
	}
	
	@Nested
	public class RegistryTests {
		@Nested
		public class RegisterEventHandlerMethod {

			@Test
			public void shouldThrowWhenEventTypeArgumentIsNull() {
				assertThrows(IllegalArgumentException.class, () -> {
					new RegistryEventHandlerProvider(registry -> 
						registry.registerEventHandler(null, () -> new TestEventHandler()));
				});
			}
			
			@Test
			public void shouldThrowWhenEventHandlerInstanceFactoryArgumentIsNull() {
				assertThrows(IllegalArgumentException.class, () -> {
					new RegistryEventHandlerProvider(registry -> 
						registry.registerEventHandler(TestEvent.class, null));
				});
			}
			
			@Test
			public void shouldSupportMultipleHandlersForAnEvent() {
				assertDoesNotThrow(() -> {
					new RegistryEventHandlerProvider(registry -> 
						registry.registerEventHandler(TestEvent.class, () -> new TestEventHandler())
							.registerEventHandler(TestEvent.class, () -> new TestEventHandler()));
				});
			}
		}
	}
	
	@Nested
	public class GetEventHandlersForMethod {
		
		@Test
		public void shouldProvideRegisteredEventHandlers() {
			
			var testEventHandler1 = new TestEventHandler();
			var testEventHandler2 = new TestEventHandler();
			
			var provider = new RegistryEventHandlerProvider(registry ->
				registry.registerEventHandler(TestEvent.class, () -> testEventHandler1)
					.registerEventHandler(TestEvent.class, () -> testEventHandler2));
			
			Iterable<EventHandler<TestEvent>> resolvedHandlers = provider.getEventHandlersFor(TestEvent.class);	
			
			assertTrue(resolvedHandlers.iterator().hasNext());			
			assertTrue(StreamSupport.stream(resolvedHandlers.spliterator(), false)
				.allMatch(h -> h == testEventHandler1 || h == testEventHandler2));
		}

		@Test
		public void shouldReturnEmptyIterableIfNoEventHandlersAreRegistered() {
						
			var provider = new RegistryEventHandlerProvider(registry -> {});
			
			Iterable<EventHandler<TestEvent>> resolvedHandlers = provider.getEventHandlersFor(TestEvent.class);	
			
			assertTrue(!resolvedHandlers.iterator().hasNext());
		}	
	}
}
