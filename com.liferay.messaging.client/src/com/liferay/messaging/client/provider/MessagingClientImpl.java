package com.liferay.messaging.client.provider;

import com.liferay.messaging.Destination;
import com.liferay.messaging.DestinationConfiguration;
import com.liferay.messaging.DestinationEventListener;
import com.liferay.messaging.DestinationNames;
import com.liferay.messaging.Message;
import com.liferay.messaging.MessageBuilder;
import com.liferay.messaging.MessageBuilderFactory;
import com.liferay.messaging.MessageBus;
import com.liferay.messaging.MessageBusEventListener;
import com.liferay.messaging.MessageListener;
import com.liferay.messaging.MessageListenerException;
import com.liferay.messaging.MessageProcessorException;
import com.liferay.messaging.OutboundMessageProcessor;
import com.liferay.messaging.OutboundMessageProcessorFactory;
import com.liferay.messaging.client.api.MessagingClient;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
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
		_configurationAdminTracker = new ServiceTracker<>(
			_bundleContext, ConfigurationAdmin.class, null);
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
	public void messagingCommand(String command) throws IOException, InvalidSyntaxException {
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

			// Create message via message builder
			MessageBuilderFactory messageBuilderFactory = getMessageBuilderFactory();			
			
			MessageBuilder messageBuilder =
				messageBuilderFactory.create(destinationName);
			
			messageBuilder.setPayload("payload");
			
			messageBuilder.put("property1", "value1");
			messageBuilder.put("property2", "value2");

			// Send message via message builder
			messageBuilder.send();
			
			// Create a message bus event listener
			MessageBusEventListener listener = new MessageBusEventListener() {

				@Override
				public void destinationAdded(Destination destination) {
					System.out.println("Destination added!");
				}

				@Override
				public void destinationRemoved(Destination destination) {
					System.out.println("Destination removed!");
				}

			};

			// Register the message bus event listener
			_bundleContext.registerService(
				MessageBusEventListener.class, listener, null);
			
			// Add second destination
			String destinationName2 = "parallelDestination2";
			
			DestinationConfiguration parallelDestinationConfiguration2 =
				DestinationConfiguration
					.createParallelDestinationConfiguration(destinationName2);
			
			_bundleContext.registerService(DestinationConfiguration.class, parallelDestinationConfiguration2, null);
			
			messageBus = getMessageBus();
			
			System.out.println("Destination count: " + messageBus.getDestinationCount());
			
			// Add destination event listener to second destination
			DestinationEventListener destinationEventListener = new DestinationEventListener() {

				@Override
				public void messageListenerRegistered(String destinationName, MessageListener messageListener) {
					System.out.println("Message listener registered with " + destinationName + "!");
				}

				@Override
				public void messageListenerUnregistered(String destinationName, MessageListener messageListener) {
					System.out.println("Message listener unregistered with " + destinationName + "!");
				}
				
			};

			Dictionary<String, Object> destinationEventListenerProperties = new Hashtable<String, Object>();

			destinationEventListenerProperties.put("destination.name", destinationName2);

			_bundleContext.registerService(DestinationEventListener.class, destinationEventListener, destinationEventListenerProperties);

			// Add second message listener
			MessageListener messageListener2 = new MessageListener() {

				@Override
				public void receive(Message message) throws MessageListenerException {
					System.out.println("Received message: " + message);
					System.out.println("Sending response");
					
					// Create response message
					MessageBuilder responseMessageBuilder = getMessageBuilderFactory().createResponse(message);
					
					responseMessageBuilder.setPayload(message);
					
					responseMessageBuilder.send();
				}
				
			};
			
			Dictionary<String, Object> properties2 = new Hashtable<String, Object>();
			properties2.put("destination.name", destinationName2);

			_bundleContext.registerService(MessageListener.class, messageListener2, properties2);
			
			// Add third message listener, this one for the default response destination
			MessageListener messageListener3 = new MessageListener() {

				@Override
				public void receive(Message message) throws MessageListenerException {
					System.out.println("Default response destination received message: " + message);
				}
				
			};
			
			Dictionary<String, Object> properties3 = new Hashtable<String, Object>();
			properties3.put("destination.name", DestinationNames.MESSAGE_BUS_DEFAULT_RESPONSE);

			_bundleContext.registerService(MessageListener.class, messageListener3, properties3);
			
			// Create third message
			Message message3 = new Message();
			message3.setPayload("payload3");

			message3.put("property5", "value5");
			message3.put("property6", "value6");
			
			message3.setResponseDestinationName(DestinationNames.MESSAGE_BUS_DEFAULT_RESPONSE);
	
			// Send synchronous message
			Object response = getMessageBus().sendSynchronousMessage(destinationName2, message3);
			System.out.println("response: " + response);
			
			// Create fourth message via message builder
			messageBuilderFactory = getMessageBuilderFactory();
			
			MessageBuilder messageBuilder4 = messageBuilderFactory.create(destinationName2);
			
			messageBuilder4.setPayload("payload2");
			
			messageBuilder4.put("property3", "value4");
			messageBuilder4.put("property3", "value4");
			
			messageBuilder4.setResponseDestinationName(DestinationNames.MESSAGE_BUS_DEFAULT_RESPONSE);
			
			// Send synchronous message
			Object response4 = messageBuilder4.sendSynchronous();
			System.out.println("response: " + response4);
			
			// Demonstrate use of outbound message processor
			OutboundMessageProcessorFactory ompFactory =
					new OutboundMessageProcessorFactory() {

				@Override
				public OutboundMessageProcessor create() {
					return new OutboundMessageProcessor() {

						@Override
						public void afterSend(Message message) throws MessageProcessorException {
							System.out.println("In afterSend!");
						}

						@Override
						public Message beforeSend(Message message) throws MessageProcessorException {
							System.out.println("In beforeSend!");

							message.put("extraKey", "extraValue");

							return message;
						}

					};
				}

			};

			Dictionary<String, Object> ompFactoryProperties = new Hashtable<String, Object>();

			ompFactoryProperties.put("destination.name", destinationName);

			_bundleContext.registerService(
				OutboundMessageProcessorFactory.class, ompFactory, ompFactoryProperties);
			
			MessageBuilder ompMessageBuilder = getMessageBuilderFactory().create(destinationName);
			
			ompMessageBuilder.setPayload("ompMessagePayload");
			
			ompMessageBuilder.send();
			
			break;
		case "configadmin":
			// Demonstrate configuring message bus properties via configuration admin
			ConfigurationAdmin configurationAdmin = getConfigurationAdmin();
			
			messageBus = getMessageBus();
			
			Configuration[] configurations =
					configurationAdmin.listConfigurations(
							"(service.factoryPid=" + messageBus.getClass().getName() + ")");
			
			if (configurations == null) {
				System.out.println("No configurations found. Adding one!");
				addMessageBusConfig();
			}
			
			configurations =
					configurationAdmin.listConfigurations(
							"(service.factoryPid=" + messageBus.getClass().getName() + ")");
			
			if (configurations == null || configurations.length == 0) {
				throw new RuntimeException();
			}
			
			System.out.println("Number of configurations for " +
			messageBus.getClass().getName() + ": " + configurations.length);
			
			for (Configuration configuration : configurations) {
				System.out.println("configuration: " + configuration);
				Dictionary<String, Object> props = configuration.getProperties();
				System.out.println("configuration properties: " + props);
			}

			break;
		default:
			System.out.println("Invalid command!");
		}
	}
	
	private void addMessageBusConfig() throws IOException {
		ConfigurationAdmin configurationAdmin = getConfigurationAdmin();
		
		MessageBus messageBus = getMessageBus();

		Configuration newConfig =
		configurationAdmin.createFactoryConfiguration(messageBus.getClass().getName());
		
		Dictionary<String, Object> dictionary = new Hashtable<String, Object>();
		
		dictionary.put("synchronousMessageSenderTimeout", 5000);
		
		newConfig.update(dictionary);
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
	
	private ConfigurationAdmin getConfigurationAdmin() {
		try {
			_configurationAdminTracker.open();
				
			ConfigurationAdmin configurationAdmin = _configurationAdminTracker.waitForService(_timeout);

			if (configurationAdmin == null) {
				throw new RuntimeException();
			}

			return configurationAdmin;
		}
		catch (InterruptedException ie) {
			throw new RuntimeException(ie);
		}
	}
	
	private Bundle _bundle;
	private BundleContext _bundleContext;
	private ServiceTracker<MessageBus, MessageBus> _messageBusTracker;
	private ServiceTracker<MessageBuilderFactory, MessageBuilderFactory> _messageBuilderFactoryTracker;
	private ServiceTracker<ConfigurationAdmin, ConfigurationAdmin> _configurationAdminTracker;
	private long _timeout;

}
