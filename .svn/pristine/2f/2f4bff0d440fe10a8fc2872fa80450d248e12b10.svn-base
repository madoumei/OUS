package com.config.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class ConsumerMessage{
	
	public static void main(String[] args) {
		 ConnectionFactory connectionFactory;//连接工厂
	     Connection connection = null;//连接

	     Session session;//会话 接受或者发送消息的线程
	     Topic topic;//消息的目的地

	     //实例化连接工厂(连接到ActiveMQ服务器)
	     connectionFactory = new ActiveMQConnectionFactory("failover:(tcp://47.94.156.170:61616?wireFormat.maxInactivityDuration=0)?maxReconnectDelay=10000");

	     try {
	         //通过连接工厂获取连接
	         connection = connectionFactory.createConnection();
	         connection.setClientID("consumerClient2");
	         //启动连接
	         connection.start();
	         //创建session
	         session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
	         //生产者将消息发送到MyTopic，所以消费者要到MyTopic去取
	         topic = session.createTopic("face_topic");
	         //创建消息消费者
	         TopicSubscriber consumer = session.createDurableSubscriber(topic, "face_topic");
	         consumer.setMessageListener(new MessageListener(){//有事务限制  
	             @Override  
	             public void onMessage(Message message) {  
	                 try {  
	                     TextMessage textMessage=(TextMessage)message;  
	                     System.out.println(textMessage.getText());  
	                 } catch (JMSException e1) {  
	                     e1.printStackTrace();  
	                 }  
	                 try {  
	                     session.commit();  
	                 } catch (JMSException e) {  
	                     e.printStackTrace();  
	                 }  
	             }  
	         });  
	     } catch (JMSException e) {
	         e.printStackTrace();
	     }

	 }
}
