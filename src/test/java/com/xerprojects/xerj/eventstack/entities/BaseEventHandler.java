package com.xerprojects.xerj.eventstack.entities;

import java.util.ArrayList;

import com.xerprojects.xerj.eventstack.Event;
import com.xerprojects.xerj.eventstack.EventHandler;

public class BaseEventHandler<TEvent extends Event> implements EventHandler<TEvent> {

	private final ArrayList<TEvent> handledEvents = new ArrayList<>();
	
	@Override
	public void handle(TEvent event) {
		handledEvents.add(event);
	}
	
	public boolean hasHandledEvent(Event event) {
		// Instance comparison.
		return handledEvents.stream().anyMatch(e -> e == event);
	}

}
