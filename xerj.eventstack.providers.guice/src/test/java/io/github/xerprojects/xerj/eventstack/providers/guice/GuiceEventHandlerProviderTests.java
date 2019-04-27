package io.github.xerprojects.xerj.eventstack.providers.guice;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.ParameterizedType;
import java.util.stream.StreamSupport;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.util.Types;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.github.xerprojects.xerj.eventstack.Event;
import io.github.xerprojects.xerj.eventstack.EventHandler;
import io.github.xerprojects.xerj.eventstack.providers.guice.entities.TestEvent;
import io.github.xerprojects.xerj.eventstack.providers.guice.entities.TestEventHandler1;
import io.github.xerprojects.xerj.eventstack.providers.guice.entities.TestEventHandler2;
import io.github.xerprojects.xerj.eventstack.providers.guice.entities.guicemodules.AppModule;
import io.github.xerprojects.xerj.eventstack.providers.guice.entities.guicemodules.NullModule;

public class GuiceEventHandlerProviderTests {

	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new AppModule());
		ParameterizedType interfaceType = Types.newParameterizedType(EventHandler.class, Types.subtypeOf(Event.class));
		var t = injector.getInstance(Key.get(Types.setOf(interfaceType)));
	}

    @Nested
    public class Constructor {
        @Test
        public void shouldThrowWhenInjectorArgumentIsNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                new GuiceEventHandlerProvider(null);
            });
        }
    }
			
	@Nested
	public class GetEventHandlersForMethod {
		@Test
		public void shouldProvideEventHandlerFromInjector() {
			var injector = Guice.createInjector(new AppModule());
            var provider = new GuiceEventHandlerProvider(injector);
            
			Iterable<EventHandler<TestEvent>> handlers = provider.getEventHandlersFor(TestEvent.class);
			
			assertTrue(handlers.iterator().hasNext());
			assertTrue(StreamSupport.stream(handlers.spliterator(), false)
				.allMatch(h -> h instanceof TestEventHandler1 || h instanceof TestEventHandler2));
		}
		
		@Test
		public void shouldThrowWhenCommandClassArgumentIsNull() {
			assertThrows(IllegalArgumentException.class, () -> {
				var injector = Guice.createInjector(new AppModule());
				var provider = new GuiceEventHandlerProvider(injector);
				provider.getEventHandlersFor(null);
			});
		}

		@Test
		public void shouldReturnEmptyIterableWhenNoEventHandlersAreFound() {
			var injector = Guice.createInjector(new NullModule());
            var provider = new GuiceEventHandlerProvider(injector);
            
			Iterable<EventHandler<TestEvent>> handlers = provider.getEventHandlersFor(TestEvent.class);
			
			assertTrue(!handlers.iterator().hasNext());
		}
	}
}