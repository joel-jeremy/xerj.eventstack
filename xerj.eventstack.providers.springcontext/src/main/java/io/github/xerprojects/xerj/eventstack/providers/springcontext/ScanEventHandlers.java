package io.github.xerprojects.xerj.eventstack.providers.springcontext;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import io.github.xerprojects.xerj.eventstack.EventHandler;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ComponentScan(
    includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = EventHandler.class),
    useDefaultFilters = false)
public @interface ScanEventHandlers {
    @AliasFor(value = "basePackageClasses", annotation = ComponentScan.class)
    Class<?>[] value() default {};

    @AliasFor("value")
    Class<?>[] basePackageClasses() default {};
}