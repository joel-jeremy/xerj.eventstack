# Table of contents
* [Overview](#overview)
* [Features](#features)
* [Installation](#installation)
* [Getting Started](#getting-started)

# Overview
Simple event handling library!

This project composes of components for implementing the event handling parts of the DDD/CQRS pattern. This library was built with simplicity, modularity and pluggability in mind.

## Features
* Send events to one or many registered event handlers.
* Multiple ways of registering event handlers:
    * Simple registration (no IoC container).
    * IoC container registration
      * achieved by creating implementations of EventHandlerProvider:
        * Spring Context
          
          [![Maven Central](https://img.shields.io/maven-central/v/io.github.xerprojects/xerj.eventstack.providers.springcontext.svg?style=for-the-badge)](https://mvnrepository.com/artifact/io.github.xerprojects/xerj.eventstack.providers.springcontext)
          
        * Guice
          
          [![Maven Central](https://img.shields.io/maven-central/v/io.github.xerprojects/xerj.eventstack.providers.guice.svg?style=for-the-badge)](https://mvnrepository.com/artifact/io.github.xerprojects/xerj.eventstack.providers.guice)
          
                    
    * Attribute registration (Soon!)
      * achieved by marking methods with @EventHandler annotations.

## Installation
* You can simply clone this repository, build the source, reference the jar from your project, and code away!

* XerJ.EventStack is also available in the Maven Central:

    [![Maven Central](https://img.shields.io/maven-central/v/io.github.xerprojects/xerj.eventstack.svg?style=for-the-badge)](https://mvnrepository.com/artifact/io.github.xerprojects/xerj.eventstack)

## Getting Started

### Sample Command and Command Handler

```java
// Example command.
public class ProductRegisteredEvent
{
    private final int productId;
    private final String productName;

    public ProductRegisteredEvent(int productId, String productName) 
    {
        this.productId = productId;
        this.productName = productName;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }
}

// Event handler 1.
public class ProductRegisteredNotificationHandler : EventHandler<ProductRegisteredEvent>
{
    private final NotificationService notificationService;

    public ProductRegisteredNotificationHandler(NotificationService notificationService)
    {
        this.notificationService = notificationService;
    }

    @Override
    public CompletableFuture<Void> handle(ProductRegisteredEvent event)
    {
        return notificationService.notify("Product registered! - " + event.getProductName());
    }
}

... Other event handlers

```
### Event Handler Registration

Before we can dispatch any events, first we need to register our event handlers. There are several ways to do this:

#### 1. Simple Registration (No IoC container)
```java
public static void main(String[] args)
{
    RegistryEventHandlerProvider provider = new RegistryEventHandlerProvider(registry -> {
        registry.registerEventHandler(ProductRegisteredEvent.class, () -> new ProductRegisteredNotificationHandler(
            new EmailNotificationService()
        ));
    });

    EventDispatcher dispatcher = new EventDispatcher(provider);
    
    // Dispatch event.
    CompletableFuture<Void> future = dispatcher.send(new ProductRegisteredEvent(1, "My Product Name"));
}
```

#### 2. Container Registration

Spring Context
```java
public static void main(String[] args)
{ 
    ApplicationContext appContext = new AnnotationConfigApplicationContext(BeanConfigs.class);

    SpringContextEventHandlerPovider provider = new SpringContextEventHandlerPovider(appContext);

    EventDispatcher dispatcher = new EventDispatcher(provider);

    // Dispatch event.
    CompletableFuture<Void> future = dispatcher.send(new ProductRegisteredEvent(1, "My Product Name"));
}
```

Guice
```java
public static void main(String[] args)
{ 
    Injector injector = Guice.createInjector(new AppModule());

    GuiceEventHandlerPovider provider = new GuiceEventHandlerPovider(injector);

    EventDispatcher dispatcher = new EventDispatcher(provider);

    // Dispatch event.
    CompletableFuture<Void> future = dispatcher.send(new ProductRegisteredEvent(1, "My Product Name"));
}
```