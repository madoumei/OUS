package com.config.activemq;

import com.google.gson.Gson;

import com.utils.SysLog;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import java.util.Map;

/**
 * ActiveMQ 消息生产类
 *
 * @author Mafly
 */
@Component
public class MessageSender {

    public static final String KEY_VISITOR_WILL_FINISH="visit_will_finish";
    public static final String KEY_AVATAR_UPLOADED="avatar_uploaded";

    @Autowired
    private JmsMessagingTemplate  jmsMessagingTemplate;

    @Autowired
    private Topic topic;

    private Gson gson = new Gson();

    /**
     * 更新人脸库
     */
    public void updateFaceLib(Map<String, Object> map) {
//
//    System.out.println("发送了一条消息。");
//        String destination = "face_topic";
        sendMessage(gson.toJson(map));
    }

    public void updateFaceLib(Map<String, Object> map, String destination) {
//
//    System.out.println("发送了一条消息。");
        sendMessage(gson.toJson(map),destination);
    }

    /**
     * 发送到消息队列
     *
     * @param messgae
     * @param type    类型，0:默认队列 1：删除队列 ...
     */
    public int sendMessage(final String messgae) {
        try {
            SysLog.info(messgae);
          	jmsMessagingTemplate.convertAndSend(topic, messgae);
          } catch (Exception e) {
              e.printStackTrace();
              return -1;
          }
        return 0;
    }

    /**
     * 发送到消息队列
     *
     * @param messgae
     * @param type    类型，0:默认队列 1：删除队列 ...
     */
    public void sendMessage(final String messgae,String destination) {
        try {
            System.out.println(messgae);
          	jmsMessagingTemplate.convertAndSend(new ActiveMQTopic(destination), messgae);
          } catch (Exception e) {
              e.printStackTrace();
          }
    }
}
