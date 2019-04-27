package io.github.xerprojects.xerj.eventstack.providers.guice.entities.guicemodules;

import java.util.List;

import io.github.xerprojects.xerj.eventstack.providers.guice.AbstractEventHandlerModule;
import io.github.xerprojects.xerj.eventstack.providers.guice.entities.TestEvent;
import io.github.xerprojects.xerj.eventstack.providers.guice.entities.TestEventHandler1;
import io.github.xerprojects.xerj.eventstack.providers.guice.entities.TestEventHandler2;

public class AppModule extends AbstractEventHandlerModule {
    @Override
    public void configure() {
       bindEventHandlers(TestEvent.class, List.of(TestEventHandler1.class, TestEventHandler2.class));
    }
}