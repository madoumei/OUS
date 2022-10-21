package com.event.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.config.qicool.common.utils.StringUtils;
import com.event.bean.Litigant;
import com.event.event.NotifyEvent;
import com.utils.Constant;
import com.utils.Encodes;
import com.utils.SysLog;
import com.utils.UtilTools;
import com.utils.httpUtils.HttpClientUtil;
import com.utils.msgUtils.MsgTemplateUtils;
import com.web.bean.EmpVisitProxy;
import com.web.bean.Employee;
import com.web.bean.UserInfo;
import com.web.dao.UserDao;
import com.web.service.EmployeeService;
import com.web.service.MsgTemplateService;
import com.web.service.UserService;
import com.web.service.VisitProxyService;
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
public class DingtalkService implements SmartApplicationListener {
    public DingtalkService() {
        SysLog.info(this.getClass().getName() + "start");
    }

    private final static String PLATEFORM = "dingtalk";

    @Autowired
    private UserService userService;

    @Autowired
    private MsgTemplateService msgTemplateService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private VisitProxyService visitProxyService;

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
        return 3;
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

    private void onNotifyEvent(NotifyEvent event) {
        UserInfo userInfo = userService.getUserInfo(event.getUserId());
        if (null == userInfo) {
            SysLog.error("NotifyEvent can't find UserInfo id=" + event.getUserId());
            return;
        }
        if (userInfo.getDdnotify() == 0) {
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
        String DDlinkStr = "corpid=" + userInfo.getDdcorpid() + "&container_type=work_platform&app_id=0_" + userInfo.getDdagentid() + "&redirect_type=jump&redirect_url=";
        Map<String, Object> map = new HashMap<>();
        List<Litigant> receivers = event.getReceivers();
        if (!receivers.isEmpty()) {
            for (Litigant receiver : receivers) {
                if (org.apache.commons.lang3.StringUtils.isNotBlank(receiver.getOpenid())) {
                    map.put("userid_list", receiver.getOpenid());

                    if ("员工".equalsIgnoreCase(receiver.getType())) {
                        List<Employee> empListByOpenid = employeeService.getEmpListByOpenid(receiver.getOpenid());
                        if (!empListByOpenid.isEmpty()) {
                            Employee emp = empListByOpenid.get(0);
                            EmpVisitProxy vp = new EmpVisitProxy();
                            vp.setEmpid(emp.getEmpid());
                            vp.setUserid(emp.getUserid());
                            EmpVisitProxy proxy = visitProxyService.getProxyInfoByEid(vp);
                            if (ObjectUtils.isNotEmpty(proxy)) {
                                Date pdate = new Date();
                                Date sdate = vp.getStartDate();
                                Date edate = vp.getEndDate();
                                if (vp.getProxyStatus() == 1 && pdate.getTime() >= sdate.getTime() && pdate.getTime() <= edate.getTime()) {
                                    Employee empProxy = employeeService.getEmployee(vp.getProxyId());
                                    if (ObjectUtils.isNotEmpty(empProxy)) {
                                        event.getParams().put(MessageServiceImpl.PARAM_URL,
                                                Constant.DDPAGELINK_URL + DDlinkStr + Encodes.urlEncode(event.getParams().get(MessageServiceImpl.PARAM_URL) + "=agent"));
                                    }
                                }
                            } else if (ObjectUtils.isNotEmpty(emp)) {
                                event.getParams().put(MessageServiceImpl.PARAM_URL,
                                        Constant.DDPAGELINK_URL + DDlinkStr + Encodes.urlEncode(event.getParams().get(MessageServiceImpl.PARAM_URL) + "=myVisitor"));
                            }
                        }
                    }
                }
            }
        }

        content = MsgTemplateUtils.getTemplateMsg(msg.getContent(), event.getParams());

        Map<String, Object> markdown = new HashMap<String, Object>();
        markdown.put("text", content);
        markdown.put("title", msg.getTitle());
        map.put("agent_id", userInfo.getDdagentid());
        map.put("to_all_user", false);


        Map<String, Object> msgParam = new HashMap<String, Object>();
        msgParam.put("msgtype", "markdown");
        msgParam.put("markdown", markdown);

        map.put("msg", msgParam);

        String jsonString = JSON.toJSONString(map);
        jsonString = jsonString.replaceAll("\\\\n", "\\n");
        String reponse = sendNotifyByDingtalk(jsonString, userInfo);
        JSONObject jsonObject = JSON.parseObject(reponse);
        Object errcode = jsonObject.get("errcode");
        if (ObjectUtils.isNotEmpty(errcode)) {
            int errcodeInt = (int) errcode;
            if (0 == errcodeInt) {
                event.setSentWay(event.getSentWay() + ",dingtalk");
            }
        }
    }

    public String sendNotifyByDingtalk(String content, UserInfo userInfo) {

        String access_token = UtilTools.getDDAccToken(userInfo.getUserid(), userInfo.getDdAppid(), userInfo.getDdAppSccessSecret());

        String response = HttpClientUtil.postJsonBodySourceWechat(Constant.DDNOTIFY_URL + "?access_token=" + access_token, 5000, content, "UTF-8");
        return response;
    }


    public String test(UserInfo userInfo) {
        if (userInfo.getDdnotify() == 0) {
            return "";
        }
        String result = UtilTools.checkDDAccToken(userInfo.getUserid(), userInfo.getDdAppid(), userInfo.getDdAppSccessSecret());
        return result;
    }
}
