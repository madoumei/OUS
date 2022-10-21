package com.event.listener;

import com.alibaba.fastjson.JSON;
import com.config.qicool.common.utils.StringUtils;
import com.event.bean.Litigant;
import com.event.event.NotifyEvent;
import com.utils.SysLog;
import com.utils.cacheUtils.CacheManager;
import com.utils.httpUtils.HttpClientUtil;
import com.utils.msgUtils.MsgTemplateUtils;
import com.utils.setTokenUtils.WechatUtils;
import com.web.bean.EmpVisitProxy;
import com.web.bean.Employee;
import com.web.bean.UserInfo;
import com.web.dao.ConfigureDao;
import com.web.dao.UserDao;
import com.web.service.*;
import com.web.service.impl.MessageServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WechatBusService implements SmartApplicationListener {
    private final static String PLATEFORM = "wechatbus";

    @Autowired
    protected UserService userService;

    @Autowired
    protected ConfigureDao configureDao;

    @Autowired
    protected UserDao userDao;

    @Autowired
    protected OperateLogService operateLogService;

    @Autowired
    protected MsgTemplateService msgTemplateService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private VisitProxyService visitProxyService;

    public WechatBusService() {
        SysLog.info(this.getClass().getName() + " start");
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
        return 1;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        try {
            if (applicationEvent instanceof NotifyEvent) {
                NotifyEvent event = (NotifyEvent) applicationEvent;
                onNotifyEvent(event);
            }
        } catch (Exception e) {
            SysLog.error(e);
        }
    }

    /**
     * 预约访客事件
     *
     * @param event
     */
    private void onNotifyEvent(NotifyEvent event) {
        UserInfo userInfo = userService.getUserInfo(event.getUserId());
        if (null == userInfo) {
            SysLog.error("NotifyEvent can't find UserInfo id=" + event.getUserId());
            return;
        }
        if (userInfo.getWxBusNotify() == 0) {
            return;
        }
        //是否为顺序发送
        if (userInfo.getNotifyType() == 1 && StringUtils.isNotEmpty(event.getSentWay())) {
            return;
        }

        //是否指定发送方式
        if (StringUtils.isNotEmpty(event.getSendWay()) && !event.getSendWay().contains(PLATEFORM)) {
            return;
        }

        com.web.bean.MsgTemplate msg = msgTemplateService.getTemplate(0, PLATEFORM + "_" + event.getEventType());
        if (msg == null || 0 == msg.getStatus()) {
            return;
        }

        String content = MsgTemplateUtils.getTemplateMsg(msg.getContent(), event.getParams());
        if (StringUtils.isBlank(content)) {
            //未设置模板，不发送
            return;
        }


        Map<String, Object> map = new HashMap<>();
        List<Litigant> receivers = event.getReceivers();
        if (!receivers.isEmpty()) {
            for (Litigant receiver : receivers) {
                if (org.apache.commons.lang3.StringUtils.isNotBlank(receiver.getOpenid())) {
                    map.put("touser", receiver.getOpenid());

                    if ("员工".equalsIgnoreCase(receiver.getType())) {
                        List<Employee> empListByOpenid = employeeService.getEmpListByOpenid(receiver.getOpenid());
                        if (!empListByOpenid.isEmpty()) {
                            Employee emp = empListByOpenid.get(0);
                            EmpVisitProxy vp = new EmpVisitProxy();
                            vp.setEmpid(emp.getEmpid());
                            vp.setUserid(emp.getUserid());
                            EmpVisitProxy proxy = visitProxyService.getProxyInfoByEid(vp);
//                            if (ObjectUtils.isNotEmpty(proxy)) {
//                                Date pdate = new Date();
//                                Date sdate = vp.getStartDate();
//                                Date edate = vp.getEndDate();
//                                if (vp.getProxyStatus() == 1 && pdate.getTime() >= sdate.getTime() && pdate.getTime() <= edate.getTime()) {
//                                    Employee empProxy = employeeService.getEmployee(vp.getProxyId());
//                                    if (ObjectUtils.isNotEmpty(empProxy)) {
//                                        map.put("url", event.getParams().get(MessageServiceImpl.PARAM_URL) + "=agent&pc_slide=true");
//                                    }
//                                }
//                            } else if (ObjectUtils.isNotEmpty(emp)) {
//                                map.put(MessageServiceImpl.PARAM_URL, event.getParams().get(MessageServiceImpl.PARAM_URL) + "=myVisitor&pc_slide=true");
//                            }
                            if (null != event.getParams().get(MessageServiceImpl.PARAM_URL)) {
                                map.put("url", event.getParams().get(MessageServiceImpl.PARAM_URL)+ "&pc_slide=true");
                            }
                        }
                    }
                }
            }
        }
        content = MsgTemplateUtils.getTemplateMsg(msg.getContent(), event.getParams());
        Map<String, Map<String, String>> parseContext = (Map<String, Map<String, String>>) JSON.parse(content);
        //map.put("url",event.getParams().get(MessageServiceImpl.PARAM_URL)+"&pc_slide=true");
        map.put("agentid", userInfo.getAgentid());
        map.put("msgtype", "text");
        map.put("text", parseContext);
        String jsonString = JSON.toJSONString(map);

        String s = sendNotifyByWeixinBus(jsonString, userInfo);
        if (StringUtils.isNotBlank(s)) {
            if ("0".equalsIgnoreCase(s)) {
                event.setSentWay(event.getSentWay() + ",wechatBus");
            }
        }

    }


    public String sendNotifyByWeixinBus(String content, UserInfo userInfo) {
        CacheManager cm = CacheManager.getInstance();
        if (null == cm.getToken("wechatBusToken_" + userInfo.getUserid()) || "".equals(cm.getToken("wechatBusToken_" + userInfo.getUserid()))) {
            //获取微信通知token
            WechatUtils.settokenBus(cm, userInfo);
        }
        String result = HttpClientUtil.postJsonBodySourceWechat("https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + cm.getToken("wechatBusToken_" + userInfo.getUserid()), 5000, content, "utf-8");
        //校验微信返回结果
        return WechatUtils.checkresultWeixinBus(cm, userInfo, result, "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=ACCESS_TOKEN", content);
    }

    public String test(UserInfo userInfo) {
        if (userInfo.getWxBusNotify() == 0) {
            return "";
        }
        CacheManager cm = CacheManager.getInstance();
        return WechatUtils.settokenBus(cm, userInfo);
    }
}
