package com.config.activemq;

import javax.jms.Topic;

import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ActiveMqConfig {

    @Value("${myTopic}")
    private String myTopic;
 
    @Bean
    public Topic topic(){
        return new ActiveMQTopic(myTopic);
    }

}

