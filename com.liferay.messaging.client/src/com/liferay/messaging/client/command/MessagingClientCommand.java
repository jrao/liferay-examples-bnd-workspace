package com.liferay.messaging.client.command;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.messaging.client.api.MessagingClient;

import java.io.IOException;

import osgi.enroute.debug.api.Debug;

/**
 * This is the implementation. It registers the MessagingClient interface and
 * calls it through a Gogo command.
 * 
 */
@Component(
	service=MessagingClientCommand.class,
	property = {
		Debug.COMMAND_SCOPE + "=messagingClient",
		Debug.COMMAND_FUNCTION + "=messagingCommand" },
	name="com.liferay.messaging.client.command"
)
public class MessagingClientCommand {

	public void messagingCommand(String message) throws IOException, InvalidSyntaxException {
		_messagingClient.messagingCommand(message);
	}

	@Reference
	void setClient(MessagingClient messagingClient) {
		_messagingClient = messagingClient;
	}

	private MessagingClient _messagingClient;

}
