package com.liferay.messaging.client.provider;

import org.junit.Test;

import com.liferay.messaging.client.api.MessagingClient;

/*
 * Example JUNit test case
 * 
 */

public class ClientImplTest {

	/*
	 * Example test method
	 */

	@Test
	public void simple() {
		MessagingClient impl = new MessagingClientImpl();
		
		impl.messagingCommand("getDestinationCount");
	}

}
