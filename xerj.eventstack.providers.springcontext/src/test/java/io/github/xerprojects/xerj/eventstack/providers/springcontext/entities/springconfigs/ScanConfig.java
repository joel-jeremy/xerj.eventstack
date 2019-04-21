package io.github.xerprojects.xerj.eventstack.providers.springcontext.entities.springconfigs;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import io.github.xerprojects.xerj.eventstack.EventHandler;
import io.github.xerprojects.xerj.eventstack.providers.springcontext.ScanEventHandlers;
import io.github.xerprojects.xerj.eventstack.providers.springcontext.entities.BaseEventHandler;

@Configuration
@ScanEventHandlers(BaseEventHandler.class)
public class ScanConfig {
    
}