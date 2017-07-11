package com.liferay.messaging.client.provider;

import com.liferay.messaging.DestinationConfiguration;
import com.liferay.messaging.DestinationNames;
import com.liferay.messaging.Message;
import com.liferay.messaging.MessageBuilder;
import com.liferay.messaging.MessageBuilderFactory;
import com.liferay.messaging.MessageBus;
import com.liferay.messaging.MessageListener;
import com.liferay.messaging.MessageListenerException;
import com.liferay.messaging.client.api.MessagingClient;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;

/**
 * This is the implementation. It registers a MessagingClient service.
 */
@Component(immediate=true, name="com.liferay.messaging.client")
public class MessagingClientImpl implements MessagingClient {
	
	public MessagingClientImpl() {
		_bundle = FrameworkUtil.getBundle(this.getClass());
		_bundleContext = _bundle.getBundleContext();
		_messageBusTracker = new ServiceTracker<>(
			_bundleContext, MessageBus.class, null);
		_messageBuilderFactoryTracker = new ServiceTracker<>(
			_bundleContext, MessageBuilderFactory.class, null);
		_timeout = 1000;
	}

	@Activate
	void activate(Map<String, Object> map) {
		String bundleSymbolicName = _bundle.getSymbolicName();
		System.out.println("Activating " + bundleSymbolicName + "!");
	}

	@Deactivate
	void deactivate(Map<String, Object> map) {
		String bundleSymbolicName = _bundle.getSymbolicName();
		System.out.println("Deactivating " + bundleSymbolicName + "!");
	}

	@Override
	public void messagingCommand(String command) {
		System.out.println("Command entered: " + command);

		command = command.toLowerCase();

		MessageBus messageBus = null;

		switch (command) {
		case "getdestinationcount":
			messageBus = getMessageBus();
			
			System.out.println("Destination count: " + messageBus.getDestinationCount());

			break;
		case "all":
			// Add destination
			String destinationName = "parallelDestination";
			
			DestinationConfiguration parallelDestinationConfiguration =
				DestinationConfiguration
					.createParallelDestinationConfiguration(destinationName);
			
			_bundleContext.registerService(DestinationConfiguration.class, parallelDestinationConfiguration, null);
			
			messageBus = getMessageBus();
			
			System.out.println("Destination count: " + messageBus.getDestinationCount());

			// Add destination listener
			MessageListener messageListener = new MessageListener() {

				@Override
				public void receive(Message message) throws MessageListenerException {
					System.out.println("Received message: " + message);
				}
				
			};
			
			Dictionary<String, Object> properties = new Hashtable<String, Object>();
			properties.put("destination.name", destinationName);

			_bundleContext.registerService(MessageListener.class, messageListener, properties);
			
			System.out.println("messageBus.hasDestination(" +
				destinationName + "): " +
				messageBus.hasDestination(destinationName));

			// Create message
			Message message = new Message();
			message.setPayload("payload1");

			message.put("property1", "value1");
			message.put("property2", "value2");
			
			// Send message
			getMessageBus().sendMessage(destinationName, message);

			// Create message via message builder
			MessageBuilderFactory messageBuilderFactory = getMessageBuilderFactory();			
			
			MessageBuilder messageBuilder =
				messageBuilderFactory.create(destinationName);
			
			messageBuilder.setPayload("payload2");
			
			messageBuilder.put("property3", "value3");
			messageBuilder.put("property4", "value4");

			// Send message via message builder
			messageBuilder.send();
			
			// Add third destination
			String destinationName3 = "parallelDestination3";
			
			DestinationConfiguration parallelDestinationConfiguration3 =
				DestinationConfiguration
					.createParallelDestinationConfiguration(destinationName3);
			
			_bundleContext.registerService(DestinationConfiguration.class, parallelDestinationConfiguration3, null);
			
			messageBus = getMessageBus();
			
			System.out.println("Destination count: " + messageBus.getDestinationCount());

			// Add third message listener
			MessageListener messageListener3 = new MessageListener() {

				@Override
				public void receive(Message message) throws MessageListenerException {
					System.out.println("Destination 3 received message: " + message);
					
					// Create response message
					MessageBuilder responseMessageBuilder = getMessageBuilderFactory().createResponse(message);
					
					responseMessageBuilder.setPayload(message);
					
					responseMessageBuilder.send();
				}
				
			};
			
			Dictionary<String, Object> properties3 = new Hashtable<String, Object>();
			properties3.put("destination.name", destinationName3);

			_bundleContext.registerService(MessageListener.class, messageListener3, properties3);
			
			// Add fourth message listener, this one for the default response destination
			MessageListener messageListener4 = new MessageListener() {

				@Override
				public void receive(Message message) throws MessageListenerException {
					System.out.println("Default response destination received message: " + message);
				}
				
			};
			
			Dictionary<String, Object> properties4 = new Hashtable<String, Object>();
			properties4.put("destination.name", DestinationNames.MESSAGE_BUS_DEFAULT_RESPONSE);

			_bundleContext.registerService(MessageListener.class, messageListener4, properties4);
			
			// Create third message
			Message message3 = new Message();
			message3.setPayload("payload3");

			message3.put("property5", "value5");
			message3.put("property6", "value6");
			
			message3.setResponseDestinationName(DestinationNames.MESSAGE_BUS_DEFAULT_RESPONSE);
	
			// Send synchronous message
			Object response = getMessageBus().sendSynchronousMessage(destinationName3, message3);
			System.out.println("response: " + response);
			
			// Create fourth message via message builder
			messageBuilderFactory = getMessageBuilderFactory();
			
			MessageBuilder messageBuilder4 = messageBuilderFactory.create(destinationName3);
			
			messageBuilder4.setPayload("payload4");
			
			messageBuilder4.put("property7", "value7");
			messageBuilder4.put("property8", "value8");
			
			messageBuilder4.setResponseDestinationName(DestinationNames.MESSAGE_BUS_DEFAULT_RESPONSE);
			
			// Send synchronous message
			Object response4 = messageBuilder4.sendSynchronous();
			System.out.println("response: " + response4);
			
			break;
		default:
			System.out.println("Invalid command!");
		}
	}
	
	private MessageBus getMessageBus() {
		try {
			_messageBusTracker.open();
				
			MessageBus messageBus = _messageBusTracker.waitForService(_timeout);

			if (messageBus == null) {
				throw new RuntimeException();
			}

			return messageBus;
		}
		catch (InterruptedException ie) {
			throw new RuntimeException(ie);
		}
	}
	
	private MessageBuilderFactory getMessageBuilderFactory() {
		try {
			_messageBuilderFactoryTracker.open();
				
			MessageBuilderFactory messageBuilderFactory =
				_messageBuilderFactoryTracker.waitForService(_timeout);

			if (messageBuilderFactory == null) {
				throw new RuntimeException();
			}

			return messageBuilderFactory;
		}
		catch (InterruptedException ie) {
			throw new RuntimeException(ie);
		}
	}
	
	private Bundle _bundle;
	private BundleContext _bundleContext;
	private ServiceTracker<MessageBus, MessageBus> _messageBusTracker;
	private ServiceTracker<MessageBuilderFactory, MessageBuilderFactory> _messageBuilderFactoryTracker;
	private long _timeout;

}
