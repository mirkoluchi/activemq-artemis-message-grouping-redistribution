package org.lokrim.poc.artemis;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQDestination;
import org.apache.activemq.artemis.jms.client.ActiveMQTopic;

public class Producer {

	private String url;

	public Producer(String url) {
		this.url = url;
	}

	public List<Integer> produce(int nMessages, boolean groupingEnabled) throws JMSException {
		ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(url);
		Connection connection = cf.createConnection();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		ActiveMQTopic address = ActiveMQDestination.createTopic(Constants.TOPIC);
		MessageProducer producer = session.createProducer(address);
		List<Integer> messageIds = new ArrayList<>();
		for (int i = 0; i < nMessages; i++) {
			Integer id = i;
			Integer groupId = groupingEnabled ? id % 20 : null;
			TextMessage message = session.createTextMessage();
			message.setIntProperty(Constants.PROPERTY_ID, id);
			if (groupId != null) {
				message.setIntProperty(Constants.PROPERTY_GROUPID, groupId);
			}
			System.out.println("[producer] Produced message with id=" + id + " (groupId=" + groupId + ")");
			producer.send(message);
			messageIds.add(id);
		}
		return messageIds;
	}

}
