package com.web.service;

import com.annotation.ProcessLogger;
import com.client.bean.Visitor;
import com.event.bean.Litigant;
import com.web.bean.AccessControl;
import com.web.bean.ReqAC;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface MessageService {
    void notifyEmployee(String businessKey,String tempId,int empid);

    /**
     * 给被访人发送访客预约通知,等待授权
     * @param vt
     * @return
     */
    void sendAppoinmentPermissionNotify(Visitor vt);

    /**
     * 补填授权
     * @param vt
     * @return
     */
    void sendSupplementPermissionNotifyEvent(Visitor vt);


    /**
     * 访客预约通知
     * @param visitor
     * @return
     */
    void sendCommonNotifyEvent(Visitor visitor, String eventType);

    @Async
    @ProcessLogger("通知审批人审批")
    void sendProcessNotifyEvent(Visitor visitor, List<Litigant> litigantList);

    /**
     * 抄送
     * @param visitor
     * @param eventType
     * @param litigantList
     */
    void sendCCCommonNotifyEvent(Visitor visitor, String eventType,List<Litigant> litigantList);
}
