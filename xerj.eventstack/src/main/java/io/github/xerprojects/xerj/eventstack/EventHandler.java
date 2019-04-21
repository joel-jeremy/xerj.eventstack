package io.github.xerprojects.xerj.eventstack;

import java.util.concurrent.CompletableFuture;

public interface EventHandler<TEvent extends Event> {
	CompletableFuture<Void> handle(TEvent event);
}
