package io.github.xerprojects.xerj.eventstack.providers.springcontext.entities.springconfigs;

import io.github.xerprojects.xerj.eventstack.EventHandler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.xerprojects.xerj.eventstack.providers.springcontext.entities.TestEvent;
import io.github.xerprojects.xerj.eventstack.providers.springcontext.entities.TestEventHandler1;
import io.github.xerprojects.xerj.eventstack.providers.springcontext.entities.TestEventHandler2;

@Configuration
public class BeanConfig {
    @Bean
    public EventHandler<TestEvent> getTestEventHandler1() {
        return new TestEventHandler1();
    }

    @Bean
    public EventHandler<TestEvent> getTestEventHandler2() {
        return new TestEventHandler2();
    }
}