package com.config.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.jms.*;

/**
 * 消费者监听类
 *
 * @author Mafly
 */
@Component
public class ConsumerMessageListener implements MessageListener {

    private Logger log = Logger.getLogger(ConsumerMessageListener.class);

    @Override
    public void onMessage(Message arg0) {
        // 监听发送到消息队列的文本消息，作强制转换。
        TextMessage textMessage = (TextMessage) arg0;
        try {
            System.out.println("接收到的消息内容是：" + textMessage.getText());

            // TODO: 你喜欢的任何事情...

        } catch (JMSException e) {
            log.error("", e);
        }

    }

    public static void main(String[] args) {
        ConnectionFactory connectionFactory;//连接工厂
        Connection connection = null;//连接

        Session session;//会话 接受或者发送消息的线程
        Topic topic;//消息的目的地

        //实例化连接工厂(连接到ActiveMQ服务器)
        connectionFactory = new ActiveMQConnectionFactory("tcp://47.94.156.170:61616?wireFormat.maxInactivityDuration=0");

        try {
            //通过连接工厂获取连接
            connection = connectionFactory.createConnection();
            connection.setClientID("consumerClient1");
            //创建session
            session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
            //生产者将消息发送到MyTopic，所以消费者要到MyTopic去取
            topic = session.createTopic("face_topic");
            //创建消息消费者
            TopicSubscriber consumer = session.createDurableSubscriber(topic, "face_topic");

            //启动连接
            connection.start();

            Message message = consumer.receive();
            while (message != null) {
                TextMessage txtMsg = (TextMessage) message;
                System.out.println("收到消 息：" + txtMsg.getText());
                //没这句有错
                message = consumer.receive(1000L);
            }
            session.commit();
            session.close();
            connection.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

}
