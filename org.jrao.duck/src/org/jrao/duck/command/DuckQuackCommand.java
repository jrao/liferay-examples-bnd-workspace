package org.jrao.duck.command;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.jrao.duck.api.Duck;
import osgi.enroute.debug.api.Debug;

/**
 * This is the implementation. It registers the Duck interface and calls it
 * through a Gogo command.
 * 
 */
@Component(service=DuckQuackCommand.class, property = { Debug.COMMAND_SCOPE + "=duck",
		Debug.COMMAND_FUNCTION + "=quack" }, name="org.jrao.duckquack.command")
public class DuckQuackCommand {

	public void quack() {
		_duck.quack();
	}

	@Reference
	void setDuck(Duck duck) {
		_duck = duck;
	}

	private Duck _duck;

}
