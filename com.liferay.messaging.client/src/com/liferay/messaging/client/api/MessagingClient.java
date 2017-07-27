package com.liferay.messaging.client.api;

import java.io.IOException;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.InvalidSyntaxException;

/**
 * This is an example enroute bundle that has a component that implements a
 * simple API. 
 */

@ProviderType
public interface MessagingClient {
	
	/**
	 * The interface is a minimal method.
	 * 
	 * @param command the command to run
	 * @throws InvalidSyntaxException 
	 * @throws IOException 
	 */
	void messagingCommand(String command) throws IOException, InvalidSyntaxException;

}
