package com.stone.commons.tools.jms.activemq.with.usrmsg;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQMapMessage;

public class Publisher implements MessageListener{
	private ActiveMQConnectionFactory factory;
	private String brokerURL = "tcp://172.16.10.23:61616"/* + 
			"?jms.optimizeAcknowledge=true" + 
			"&jms.optimizeAcknowledgeTimeOut=30000" + 
			"&jms.redeliveryPolicy.maximumRedeliveries=6"*/;
	private Connection connection;
	private Session session;
	private MessageProducer producer;
	private List<MessageConsumer> consumers;
	private Destination[] destinations;
	
	public Publisher() throws JMSException {
		factory = new ActiveMQConnectionFactory(brokerURL);
		factory.setWatchTopicAdvisories(true);
		connection = factory.createConnection();
		try {
			connection.start();
		} catch (JMSException jmse) {
			connection.close();
			throw jmse;
		}
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		producer = session.createProducer(null);
		consumers = new ArrayList<MessageConsumer>();
	}
	
	protected void setTopics(String[] stocks) throws JMSException {
		destinations = new Destination[stocks.length];
		for(int i = 0; i < stocks.length; i++) {
			destinations[i] = session.createQueue("STOCKS." + stocks[i]);
			
			MessageConsumer consumer = session.createConsumer(session.createQueue("STOCKS.callback." + stocks[i]));
			consumer.setMessageListener(this);
			consumers.add(consumer);
		}
	}
	
	protected void sendMessage(String[] stocks) throws JMSException {
		for(int i = 0; i < stocks.length; i++) {
			Message message = createStockMessage(stocks[i], session);
			System.out.println("Sending: " + ((ActiveMQMapMessage)message).getContentMap() + " on destination: " + destinations[i]);
			producer.send(destinations[i], message);
		}
	}
	
	protected Message createStockMessage(String stock, Session session) throws JMSException {
		MapMessage message = session.createMapMessage();
		message.setString("stock", stock);
		message.setString("callBackUrl", "STOCKS.callback." + stock);
		message.setDouble("price", 1.00);
		message.setDouble("offer", 0.01);
		message.setBoolean("up", true);
		
		return message;
	}
	
	public void close() throws JMSException {
		if (connection != null) {
			connection.close();
		}
	}
	
	public static void main(String[] args) throws JMSException, Exception {
		String[] topics = new String[]{"ordercreate", "finish"};
		Publisher publisher = new Publisher();
		publisher.setTopics(topics);
		
		for(int i = 0; i < 10; i++) {
			publisher.sendMessage(topics);
			System.out.println("Publisher '" + i + " price messages");
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		TimeUnit.MINUTES.sleep(9);
		
		// Close all resources
		publisher.close();
	}

	@Override
	public void onMessage(Message message) {
		try {
			MapMessage map = (MapMessage)message;
			String stock = map.getString("stock");
			String callBackUrl = map.getString("callBackUrl");
			double price = map.getDouble("price");
			double offer = map.getDouble("offer");
			boolean up = map.getBoolean("up");
			DecimalFormat df = new DecimalFormat( "#,###,###,##0.00" );
			System.out.println(stock + "[callback]\t" + df.format(price) + "\t" + df.format(offer) + "\t" + (up?"up\t":"down\t") + callBackUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
