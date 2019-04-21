package io.github.xerprojects.xerj.eventstack.providers.springcontext;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.ResolvableType;

import io.github.xerprojects.xerj.eventstack.EventHandler;
import io.github.xerprojects.xerj.eventstack.providers.springcontext.entities.springconfigs.ScanConfig;
import io.github.xerprojects.xerj.eventstack.providers.springcontext.entities.TestEvent;
import io.github.xerprojects.xerj.eventstack.providers.springcontext.entities.TestEventHandler1;
import io.github.xerprojects.xerj.eventstack.providers.springcontext.entities.TestEventHandler2;

public class ScanEventHandlersTests {
    @Test
    public void shouldDetectEventHandlersInPackage() {
        var appContext = new AnnotationConfigApplicationContext(ScanConfig.class);
        
        String[] handlerBeanNames = appContext.getBeanNamesForType(
            ResolvableType.forClassWithGenerics(EventHandler.class, TestEvent.class));

        ArrayList<EventHandler<?>> handlerInstances = new ArrayList<>(handlerBeanNames.length);

        for (String handlerBeanName : handlerBeanNames) {
            @SuppressWarnings("unchecked")
            EventHandler<?> handler = (EventHandler<?>)appContext.getBean(handlerBeanName, EventHandler.class);
            handlerInstances.add(handler);
        }

        assertTrue(!handlerInstances.isEmpty());
        assertTrue(handlerInstances.stream()
            .allMatch(h -> h instanceof TestEventHandler1 || h instanceof TestEventHandler2));
    }
}