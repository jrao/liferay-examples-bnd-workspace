package org.jrao.duckclient.provider;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import org.jrao.duck.api.Duck;
import org.jrao.duckclient.api.DuckClient;

/**
 * This is the implementation. It registers a Duckclient service.
 */
@Component(immediate=true, name="org.jrao.duckclient")
public class DuckClientImpl implements DuckClient {

	@Activate
	void activate(Map<String, Object> map) {
		System.out.println("Duck client activated!");
	}

	@Deactivate
	void deactivate(Map<String, Object> map) {
		System.out.println("Duck client deactivated!");
	}

	@Override
	public void quack() {
		_duck.quack();
	}
	
	@Reference
	Duck _duck;

}
