package com.liferay.messaging.client.api;

import org.osgi.annotation.versioning.ProviderType;

/**
 * This is an example enroute bundle that has a component that implements a
 * simple API. 
 */

@ProviderType
public interface MessagingClient {
	
	/**
	 * The interface is a minimal method.
	 * 
	 * @param message the message to say
	 * @return true if the message could be spoken
	 */
	void messagingCommand(String command);

}
