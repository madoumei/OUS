package com.config.redis;

import com.client.service.VisitorService;
import com.utils.MessageDelegate;
import com.utils.SysLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;


/**
 * @author bryan
 */
@Component
public class MyRedisKeyExpiredMessageDelegate implements MessageDelegate {

    @Autowired
    private VisitorService visitorService;

    @Override
    public void handleMessage(String message) {
        // TODO Auto-generated method stub
    }

    @Override
    public void handleMessage(Map message) {
        // TODO Auto-generated method stub
    }

    @Override
    public void handleMessage(byte[] message) {
        // TODO Auto-generated method stub
    }

    @Override
    public void handleMessage(Serializable message) {
        // TODO Auto-generated method stub
        String key = message.toString();
        SysLog.info("handleMessage "+key);
        visitorService.sendLeaveMessage(key);
//		visitorService.sendAutoNotifyMessage(key);
        visitorService.checkTrackTimeout(key);
    }

    @Override
    public void handleMessage(Serializable message, String channel) {
        // TODO Auto-generated method stub
    }

}
