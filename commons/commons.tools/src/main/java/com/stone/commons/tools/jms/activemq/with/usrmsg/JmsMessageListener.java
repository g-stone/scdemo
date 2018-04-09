package com.stone.commons.tools.jms.activemq.with.usrmsg;

import java.text.DecimalFormat;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;

public class JmsMessageListener implements MessageListener {
	private MessageProducer producer;
	private Map<String, Destination> destinations;
	
	public JmsMessageListener withProducer(MessageProducer producer){
		this.producer = producer;
		
		return this;
	}
	public JmsMessageListener withDestination(Map<String, Destination> destinations){
		this.destinations = destinations;
		
		return this;
	}
	
	public void onMessage(Message message) {
		try {
			MapMessage map = (MapMessage)message;
			String stock = map.getString("stock");
			String callBackUrl = map.getString("callBackUrl");
			double price = map.getDouble("price");
			double offer = map.getDouble("offer");
			boolean up = map.getBoolean("up");
			DecimalFormat df = new DecimalFormat( "#,###,###,##0.00" );
			System.out.println(stock + "\t" + df.format(price) + "\t" + df.format(offer) + "\t" + (up ? "up" : "down"));
			
			if(callBackUrl != null && !callBackUrl.trim().equals("") && producer != null && destinations != null){
				String topic = callBackUrl.substring(callBackUrl.lastIndexOf(".") + 1);
				Destination destination = destinations.get(topic);
				
				producer.send(destination, message);
				System.out.println("" + destination);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
