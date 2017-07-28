package org.jrao.duck.provider;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.metatype.annotations.Designate;

import org.jrao.duck.api.Duck;

/**
 * This is the implementation. It registers a Duck service.
 */
@Designate(ocd=Duck.Config.class)
@Component(immediate=true, name="org.jrao.duck")
public class DuckImpl implements Duck {

	@Activate
	void activate(Duck.Config config) {
		System.out.println("Duck activated! Duck size: " + config.size());
	}

	@Deactivate
	void deactivate(Map<String, Object> map) {
		System.out.println("Duck deactivated!");
	}
	
	@Override
	public void quack() {
		System.out.println("quack!");
		
		Event event = new Event("org/jrao/quack", new HashMap<String, Object>());

		_eventAdmin.postEvent(event);
	}
	
	@Override
	public String getSize() {
		Configuration config = null;
		try {
			config = _configAdmin.getConfiguration("org.jrao.duck");
		}
		catch (IOException e) {
			String size = defaultSize();
			return size;
		}
		
		Dictionary<String, Object> properties = config.getProperties();
		
		if (properties == null || properties.isEmpty()) {
			String size = defaultSize();
			return size;
		}
		
		String size = (String) properties.get("size");
		
		if (size == null || size.isEmpty()) {
			size = defaultSize();
		}

		return size;
	}
	
	private String defaultSize() {
		Method method = null;
		try {
			method = Duck.Config.class.getDeclaredMethod("size");
		}
		catch (NoSuchMethodException nsme) {
			nsme.printStackTrace();
		}
		catch (SecurityException se) {
			se.printStackTrace();
		}

		String size = (String) method.getDefaultValue();
		return size;
	}

	@Reference
	void bindEventAdmin(EventAdmin eventAdmin) {
		_eventAdmin = eventAdmin;
	}

	private EventAdmin _eventAdmin;
	
	@Reference
	private ConfigurationAdmin _configAdmin;

}
