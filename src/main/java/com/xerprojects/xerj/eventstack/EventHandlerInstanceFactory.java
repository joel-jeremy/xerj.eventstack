package com.xerprojects.xerj.eventstack;

import com.xerprojects.xerj.eventstack.Event;
import com.xerprojects.xerj.eventstack.EventHandler;

public interface EventHandlerInstanceFactory<TEvent extends Event> {
	EventHandler<TEvent> getInstance();
}
