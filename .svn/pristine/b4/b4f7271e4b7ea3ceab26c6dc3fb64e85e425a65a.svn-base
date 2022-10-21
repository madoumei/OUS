package com.event.event;

import com.client.bean.Equipment;
import com.client.bean.Visitor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 *预约事件
 */
@Setter
@Getter
public class VisitEvent{
    public static final int EVENTTYPE_ADD = 0;
    public static final int EVENTTYPE_STATUS_CHANGE = 1;//status 变更
    public static final int EVENTTYPE_PERMISSION_CHANGE = 2;//授权变更
    public static final int EVENTTYPE_SIGNIN = 3;//签到
    public static final int EVENTTYPE_SIGNOUT = 4;//签离

    private Visitor visitor;
    private Equipment equipment;
    private int eventType;
    private String sentSouce;//已发送的通知

}
