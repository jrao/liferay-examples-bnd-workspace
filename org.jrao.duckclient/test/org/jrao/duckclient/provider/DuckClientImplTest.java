package org.jrao.duckclient.provider;

import org.junit.Test;

import org.jrao.duckclient.api.DuckClient;

/*
 * Example JUNit test case
 * 
 */

public class DuckClientImplTest {

	/*
	 * Example test method
	 */

	@Test
	public void simple() {
		DuckClient duckClient = new DuckClientImpl();
		
		duckClient.quack();
	}

}
