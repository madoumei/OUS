package com.event.listener;

import com.config.qicool.common.utils.StringUtils;
import com.event.event.NotifyEvent;
import com.utils.SysLog;
import com.web.bean.UserInfo;
import com.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;

@Deprecated
public class EmailDefaultService implements SmartApplicationListener {
    @Autowired
    protected UserService userService;

    public EmailDefaultService() {
        SysLog.info(this.getClass().getName()+" start");
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return eventType == NotifyEvent.class;
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return true;
    }

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        try {
            if (applicationEvent instanceof NotifyEvent) {
                NotifyEvent event = (NotifyEvent) applicationEvent;
                onNotifyEvent(event);
            }
        }catch (Exception e){
            SysLog.error(e);
        }

    }

    /**
     * 预约访客事件
     * @param notifyEvent
     */
    private void onNotifyEvent(NotifyEvent notifyEvent){
        UserInfo userInfo = userService.getUserInfo(notifyEvent.getUserId());
        if(null == userInfo){
            SysLog.error("NotifyEvent can't find UserInfo id="+notifyEvent.getUserId());
            return;
        }
        if(userInfo.getEmailType() == 0){
            return;
        }
        //是否为顺序发送
        if(userInfo.getNotifyType()==1 && StringUtils.isNotEmpty(notifyEvent.getSentWay())) {
            return;
        }

        if(notifyEvent.getReceivers().size()==0){
            return;
        }
        SysLog.info("email get event "+notifyEvent.getEventType()+" "+notifyEvent.getReceivers().get(0).getName()+" send:"+notifyEvent.getSendWay());

    }

    protected void send(){

    }
}
