package org.jrao.duck.command;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.jrao.duck.api.Duck;

import osgi.enroute.debug.api.Debug;

@Component(service=DuckSizeCommand.class, property = { Debug.COMMAND_SCOPE + "=duck",
		Debug.COMMAND_FUNCTION + "=size" }, name="org.jrao.ducksize.command")
public class DuckSizeCommand {
	
	public void size() {
		System.out.println(_duck.getSize());
	}

	@Reference
	void setDuck(Duck duck) {
		_duck = duck;
	}

	private Duck _duck;

}
