package org.jrao.duckclient.command;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.jrao.duckclient.api.DuckClient;
import osgi.enroute.debug.api.Debug;

/**
 * This is the implementation. It registers the Duckclient interface and calls it
 * through a Gogo command.
 * 
 */
@Component(
	service=DuckClientCommand.class,
	property = {Debug.COMMAND_SCOPE + "=duckclient", Debug.COMMAND_FUNCTION + "=quack"},
	name="org.jrao.duckclient.command"
)
public class DuckClientCommand {
	public void quack() {
		_duckClient.quack();
	}

	@Reference
	void setDuckClient(DuckClient duckClient) {
		_duckClient = duckClient;
	}

	private DuckClient _duckClient;

}
