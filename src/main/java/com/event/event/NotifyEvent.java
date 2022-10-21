package com.event.event;

import com.event.bean.Litigant;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.Map;

/**
 * 通知事件
 */
@Setter
@Getter
@Data
public class NotifyEvent extends ApplicationEvent {
	private static final long serialVersionUID = 3063752514941574968L;
    public static final String EVENTTYPE_ADD_APPOINTMENT = "addAppointment";//通知被访人，有访客预约
    public static final String EVENTTYPE_ADD_APPOINTMENT_PERMISSION = "addAppointmentPermission";//通知被访人，有访客预约,等待授权 url
    public static final String EVENTTYPE_ACCEPT_APPOINTMENT = "acceptAppointment";//通知访客预约已授权
    public static final String EVENTTYPE_REJECT_APPOINTMENT = "rejectAppointment";//通知访客预约被拒绝
    public static final String EVENTTYPE_SEND_INVITE = "sendInvite";//向访客发送邀请函 url

    public static final String EVENTTYPE_VISITOR_ACCEPT = "visitorAccept";//通知被访人访客同意邀请
    public static final String EVENTTYPE_VISITOR_REJECT = "visitorReject";//通知被访人访客拒绝邀请
    public static final String EVENTTYPE_VISITOR_SUPPLEMENT = "visitorSupplement";//通知被访人访客已补填信息
    public static final String EVENTTYPE_SUPPLEMENT_ACCEPT = "supplementAccept";//通知被访人访客已补填信息
    public static final String EVENTTYPE_SUPPLEMENT_REJECT = "supplementReject";//通知被访人访客已补填信息


    public static final String EVENTTYPE_VISIT_CANCEL = "visitCanceled";//通知访客来访被取消

    public static final String EVENTTYPE_CHECK_IN = "visitorCheckin";//通知被访人访客已签到
    public static final String EVENTTYPE_CHECK_OUT = "visitorCheckout";//通知被访人访客已经签出
    public static final String EVENTTYPE_VISITOR_ARRIVED = "visitorArrived";//访客进门提醒，有的访客需要先进门在签到
    public static final String EVENTTYPE_EMP_NO_LEAVE = "empNoLeave";//通知被访人访客离开超时
    public static final String EVENTTYPE_VISITOR_NO_LEAVE = "visitorNoLeave";//通知访客马上离开

    public static final String EVENTTYPE_APPROVE_PROCESS = "approveNotify";//通知审批人有审批需要处理 url
    public static final String EVENTTYPE_APPROVE_FINISH = "approveComplete";//通知审批已完成
    public static final String EVENTTYPE_APPROVE_REJECT = "approveReject";//通知审批被拒绝


    public static final String EVENTTYPE_TRACK_TIMEOUT_TOVISITOR = "tkTimeoutToV";//通知访客地理位置更新超时
    public static final String EVENTTYPE_TRACK_TIMEOUT_TOMANAGER = "tkTimeoutToM";//通知管理员地理位置更新超时
    public static final String EVENTTYPE_TRACK_OFFROUTE_TOVISITOR = "tkOffRouteToV";//通知访客偏离路线
    public static final String EVENTTYPE_TRACK_OFFROUTE_TOMANAGER = "tkOffRouteToM";//通知管理员访客偏离路线

    public static final String EVENTTYPE_CODE = "code";//验证码


    private List<Litigant> receivers;//接收者
    private Map<String, String> params;//消息变量
    private String eventType;//通知事件
    private String sentWay;//已发送的通知
    private String sendWay;//如果指定发送方式，默认其他方式不响应
    private int userId;//

    public NotifyEvent(Object source) {
        super(source);
    }

    public NotifyEvent(Object source, String eventType, List<Litigant> receivers) {
        super(source);
        this.eventType = eventType;
        this.receivers = receivers;
    }

}
