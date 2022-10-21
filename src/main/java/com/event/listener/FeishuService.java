package com.event.listener;

import com.alibaba.fastjson.JSON;
import com.config.qicool.common.utils.StringUtils;
import com.event.bean.Litigant;
import com.event.event.NotifyEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utils.SysLog;
import com.utils.cacheUtils.CacheManager;
import com.utils.httpUtils.HttpClientUtil;
import com.utils.jsonUtils.JacksonJsonUtil;
import com.utils.msgUtils.MsgTemplateUtils;
import com.utils.setTokenUtils.FeishuUtils;
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

import java.io.IOException;
import java.util.*;

@Component
public class FeishuService implements SmartApplicationListener {

    public FeishuService() {
        SysLog.info(this.getClass().getName() + "start");
    }

    private final static String PLATEFORM = "feishu";

    @Autowired
    private UserService userService;

    @Autowired
    private OperateLogService operateLogService;

    @Autowired
    private ConfigureDao configureDao;

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
        return 4;
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
        if (userInfo.getFsNotify() == 0) {
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


        Map<String, Object> params = new HashMap<>();
        Map<String, Object> map = new HashMap<String, Object>();

        List<Litigant> receivers = event.getReceivers();
        if (!receivers.isEmpty()) {
            for (Litigant receiver : receivers) {
                if (org.apache.commons.lang3.StringUtils.isNotBlank(receiver.getOpenid())) {
                    params.put("receive_id", receiver.getOpenid());

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
//                                        event.getParams().put(MessageServiceImpl.PARAM_URL,
//                                                "https://applink.feishu.cn/client/web_app/open?appId=" + userInfo.getSecurityID() + "&path=/empClient/#/agent");
//                                    }
//                                }
//                            } else if (ObjectUtils.isNotEmpty(emp)) {
//                                event.getParams().put(MessageServiceImpl.PARAM_URL,
//                                        "https://applink.feishu.cn/client/web_app/open?appId=" + userInfo.getSecurityID() + "&path=/empClient/#/myVisitor");
//                            }
                            if (null != event.getParams().get(MessageServiceImpl.PARAM_URL)) {
                                map.put("url", "https://applink.feishu.cn/client/web_app/open?appId=" + userInfo.getSecurityID() +event.getParams().get(MessageServiceImpl.PARAM_URL));
                            }
                        }
                    }
                }
            }
        }

        content = MsgTemplateUtils.getTemplateMsg(msg.getContent(), event.getParams());
        List<Map<String, String>> parseContext = (List<Map<String, String>>) JSON.parse(content);

        List<List<Map<String, String>>> list1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> list2 = new ArrayList<Map<String, String>>();

        list2 = parseContext;
        list1.add(list2);

        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("title", msg.getTitle());
        map1.put("content", list1);
        map.put("zh_cn", map1);

        params.put("content", JSON.toJSONString(map));
        params.put("msg_type", "post");

        String s = sendNotifyByFeiShu(JSON.toJSONString(params), userInfo);
        if (StringUtils.isNotBlank(s)) {
            if ("0".equalsIgnoreCase(s)) {
                event.setSentWay(event.getSentWay() + ",feishu");
            }
        }
    }

    public String sendNotifyByFeiShu(String content, UserInfo userInfo) {
        ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
        CacheManager cm = CacheManager.getInstance();
        if (null == cm.getToken("FsToken_" + userInfo.getUserid()) || "null".equals(cm.getToken("FsToken_" + userInfo.getUserid()))) {
            FeishuUtils.setFsToken(cm, userInfo);
        }
        String url = "https://open.feishu.cn/open-apis/im/v1/messages?receive_id_type=open_id";
        try {
            String response = HttpClientUtil.postJsonBodyForFsSource(url, 3000, content, "utf-8", cm.getToken("FsToken_" + userInfo.getUserid()));
            JsonNode jsonNode = mapperInstance.readValue(response, JsonNode.class);
            String errcode = jsonNode.get("code").asText();
            if (null != jsonNode && "0".equals(errcode)) {
                return "0";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "1";
    }

    public String test(UserInfo userInfo) {
        if (userInfo.getFsNotify() == 0) {
            return "";
        }
        CacheManager cm = CacheManager.getInstance();
        return FeishuUtils.setFsToken(cm, userInfo);
    }
}
