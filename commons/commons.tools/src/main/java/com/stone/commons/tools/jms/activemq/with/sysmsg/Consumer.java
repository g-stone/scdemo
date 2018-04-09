package com.stone.commons.tools.jms.activemq.with.sysmsg;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Consumer {
	private ActiveMQConnectionFactory factory;
	private String brokerURL = "tcp://172.16.10.23:61616"/* + 
			"?jms.optimizeAcknowledge=true" + 
			"&jms.optimizeAcknowledgeTimeOut=30000" + 
			"&jms.redeliveryPolicy.maximumRedeliveries=6"*/;
	private Connection connection;
	private Session session;
	private MessageListener messageListener;
	private List<MessageConsumer> consumers;
	private List<Destination> destinations;
	
	public Consumer() throws JMSException {
		factory = new ActiveMQConnectionFactory(brokerURL);
		connection = factory.createConnection();
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		consumers = new ArrayList<MessageConsumer>();
		destinations = new ArrayList<Destination>();
	}
	
	public Session getSession() {
		return session;
	}
	
	public void setMessageListener(MessageListener listener){
		messageListener = listener;
	}
	
	public void addMessageConsumer(MessageConsumer consumer){
		try {
			consumer.setMessageListener(messageListener);
			consumers.add(consumer);
			System.out.println("Add ok cosumer " + consumer);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public void addDestination(Destination destination){
		try {
			MessageConsumer messageConsumer = getSession().createConsumer(destination);
			destinations.add(destination);
			addMessageConsumer(messageConsumer);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public void lisTopic(String topic){
		try {
			Destination destination = getSession().createQueue("STOCKS." + topic);
			addDestination(destination);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception{
		String[] topics = new String[]{"ordercreate", "finish"};
		Consumer csl = new Consumer();
		csl.setMessageListener(new JmsMessageListener());
		
		for(String topic:topics){
			csl.lisTopic(topic);
		}
		
		TimeUnit.MINUTES.sleep(9);
	}
}
