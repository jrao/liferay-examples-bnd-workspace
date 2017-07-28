package org.jrao.duck.provider;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

@Component(property={EventConstants.EVENT_TOPIC + "=org/jrao/*"})
public class EventHandlerImpl implements EventHandler {

	@Override
	public void handleEvent(Event event) {
		System.out.println("Handling event: " + event.getTopic());
	}

}
