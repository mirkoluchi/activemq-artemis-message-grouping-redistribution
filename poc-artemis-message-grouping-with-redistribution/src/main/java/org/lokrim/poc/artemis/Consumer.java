package org.lokrim.poc.artemis;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.artemis.api.core.ActiveMQInterruptedException;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQDestination;
import org.apache.activemq.artemis.jms.client.ActiveMQTopic;

public class Consumer {

	private String name;
	private String url;

	public Consumer(String name, String url) {
		this.name = name;
		this.url = url;
	}

	public void consume(java.util.function.Consumer<Integer> messageIdsCollector)
			throws JMSException, InterruptedException {
		ActiveMQTopic address = ActiveMQDestination.createTopic(Constants.TOPIC);
		try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(url); //
				Connection connection = cf.createConnection(); //
				Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE); //
				MessageConsumer consumer = session.createSharedDurableConsumer(address, Constants.QUEUE);) {
			connection.start();
			while (true) {
				try {
					if (Thread.interrupted()) {
						throw new InterruptedException();
					}
					Message message = consumer.receive();
					if (message instanceof TextMessage) {
						TextMessage textMessage = (TextMessage) message;
						int id = textMessage.getIntProperty(Constants.PROPERTY_ID);
						messageIdsCollector.accept(id);
						Object groupId = textMessage.getObjectProperty(Constants.PROPERTY_GROUPID);

						System.out.println("[" + name + "] Consumed message with id=" + id + " and groupId=" + groupId);

						// Slow down consumption
						Thread.sleep(Constants.TIME_TO_CONSUME_MESSAGE);
					}
				} catch (InterruptedException | ActiveMQInterruptedException e) {
					System.out.println("[" + name + "] Consumer canceled");
					return;
				} catch (javax.jms.IllegalStateException e) {
					if (e.getCause() instanceof ActiveMQInterruptedException) {
						System.out.println("[" + name + "] Consumer canceled");
						return;
					} else {
						throw e;
					}
				}
			}
		}
	}

}
