package com.xerprojects.xerj.eventstack;

public interface EventHandler<TEvent extends Event> {
	void handle(TEvent event);
}
