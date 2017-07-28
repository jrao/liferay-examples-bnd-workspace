package org.jrao.duck.api;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * This is an example enroute bundle that has a component that implements a
 * simple API. 
 */

@ProviderType
public interface Duck {
	
	/**
	 * The interface is a minimal method.
	 */
	public void quack();
	
	public String getSize();

	@ObjectClassDefinition
	@interface Config {
		@AttributeDefinition
		String size() default "medium";
	}

}
