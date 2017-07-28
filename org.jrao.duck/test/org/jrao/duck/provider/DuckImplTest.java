package org.jrao.duck.provider;

import org.junit.Test;

import org.jrao.duck.api.Duck;

/*
 * Example JUNit test case
 * 
 */

public class DuckImplTest {

	/*
	 * Example test method
	 */

	@Test
	public void simple() {
		Duck duck = new DuckImpl();
		
		duck.quack();
	}

}
