package io.github.xerprojects.xerj.eventstack.providers.springcontext;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.github.xerprojects.xerj.eventstack.EventHandler;
import io.github.xerprojects.xerj.eventstack.providers.springcontext.entities.springconfigs.BeanConfig;
import io.github.xerprojects.xerj.eventstack.providers.springcontext.entities.springconfigs.NullConfig;
import io.github.xerprojects.xerj.eventstack.providers.springcontext.entities.TestEvent;
import io.github.xerprojects.xerj.eventstack.providers.springcontext.entities.TestEventHandler1;
import io.github.xerprojects.xerj.eventstack.providers.springcontext.entities.TestEventHandler2;

public class SpringContextEventHandlerProviderTests {
    @Nested
    public class Constructor {
        @Test
        public void shouldThrowWhenApplicationContextArgumentIsNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                new SpringContextEventHandlerProvider(null);
            });
        }
    }
			
	@Nested
	public class GetEventHandlersForMethod {
		@Test
		public void shouldProvideEventHandlerFromApplicationContext() {
			var appContext = new AnnotationConfigApplicationContext(BeanConfig.class);
            var provider = new SpringContextEventHandlerProvider(appContext);
            
			Iterable<EventHandler<TestEvent>> handlers = provider.getEventHandlersFor(TestEvent.class);
			
			assertTrue(handlers.iterator().hasNext());
			assertTrue(StreamSupport.stream(handlers.spliterator(), false)
				.allMatch(h -> h instanceof TestEventHandler1 || h instanceof TestEventHandler2));
		}
		
		@Test
		public void shouldThrowWhenCommandClassArgumentIsNull() {
			assertThrows(IllegalArgumentException.class, () -> {
				var appContext = new AnnotationConfigApplicationContext(BeanConfig.class);
				var provider = new SpringContextEventHandlerProvider(appContext);
				provider.getEventHandlersFor(null);
			});
		}

		@Test
		public void shouldReturnEmptyIterableWhenNoEventHandlersAreFound() {
			var appContext = new AnnotationConfigApplicationContext(NullConfig.class);
            var provider = new SpringContextEventHandlerProvider(appContext);
            
			Iterable<EventHandler<TestEvent>> handlers = provider.getEventHandlersFor(TestEvent.class);
			
			assertTrue(!handlers.iterator().hasNext());
		}
	}
}