package com.liferay.messaging.client.provider;

import org.junit.Test;

import org.osgi.framework.InvalidSyntaxException;

import com.liferay.messaging.client.api.MessagingClient;

import java.io.IOException;

/*
 * Example JUNit test case
 * 
 */

public class ClientImplTest {

	/*
	 * Example test method
	 */

	@Test
	public void simple() throws IOException, InvalidSyntaxException {
		MessagingClient impl = new MessagingClientImpl();
		
		impl.messagingCommand("getDestinationCount");
	}

}
