package com.web.service.impl;

import com.annotation.ProcessLogger;
import com.client.bean.Visitor;
import com.client.service.VisitorService;
import com.event.bean.Litigant;
import com.event.event.NotifyEvent;
import com.utils.AESUtil;
import com.utils.BeanUtils;
import com.utils.Constant;
import com.utils.SysLog;
import com.web.bean.*;
import com.web.dao.AppointmentDao;
import com.web.service.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("messageService")
public class MessageServiceImpl implements MessageService {
    public static final String PARAM_APPTIME = "time";//预约时间
    public static final String PARAM_APPTIME_EN = "time_e";//预约时间
    public static final String PARAM_VISITTIME = "vtime";//签到时间
    public static final String PARAM_VISITTIME_EN = "vtime_e";//签到时间
    public static final String PARAM_ENDTIME = "endtime";//结束时间
    public static final String PARAM_ENDTIME_EN = "endtime_e";//结束时间
    public static final String PARAM_VISITTYPE = "vtype";//拜访事由
    public static final String PARAM_VISITKIND = "vkind";//访客类型
    public static final String PARAM_VISITPHONE = "vphone";//访客手机
    public static final String PARAM_EMPNAME = "emp";//员工姓名
    public static final String PARAM_VNAME = "visitor";//访客姓名
    public static final String PARAM_COMPANY = "company";//公司名
    public static final String PARAM_URL = "url";
    public static final String PARAM_REMARK = "remark";
    public static final String PARAM_VCOMPANY = "vcompany";//访客公司名


    private static final String SENDWAY_APP = "wechat,wechatbus,feishu,dingtalk,wechatapplet";
    private static final String SENDWAY_NOT_APP = "sms,email";
    @Autowired
    UserService userService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    VisitProxyService visitProxyService;

    @Autowired
    AppointmentDao appointmentDao;

    @Autowired
    PersonInfoService personInfoService;

    @Autowired
    SubAccountService subAccountService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void notifyEmployee(String businessKey, String msgType, int empid) {
        SysLog.info("send message " + msgType + " to " + empid);
    }

    @Async
    @ProcessLogger("访客预约通知被访人授权")
    @Override
    public void sendAppoinmentPermissionNotify(Visitor visitor) {

        if (visitor == null) {
            return;
        }

        UserInfo userInfo = userService.getUserInfo(visitor.getUserid());
        if (userInfo == null) {
            return;
        }

        Employee emp = employeeService.getEmployee(visitor.getEmpid());
        if (emp == null) {
            return;
        }


        List<Litigant> litigantList = new ArrayList<>();
        litigantList.add(getLitigant(emp));
        litigantList.add(getLitigant(visitor));
        NotifyEvent event = new NotifyEvent(userService, NotifyEvent.EVENTTYPE_ADD_APPOINTMENT_PERMISSION, litigantList);
        Map<String, String> params = getEventParams(visitor, emp, userInfo);
        event.setParams(params);
        event.setUserId(userInfo.getUserid());
        String vid = AESUtil.encode(visitor.getVid() + "", Constant.AES_KEY);

        EmpVisitProxy vp = new EmpVisitProxy();
        vp.setEmpid(emp.getEmpid());
        vp.setUserid(emp.getUserid());
        vp = visitProxyService.getProxyInfoByEid(vp);
        if (null != vp) {
            Date pdate = new Date();
            Date sdate = vp.getStartDate();
            Date edate = vp.getEndDate();

            if (vp.getProxyStatus() == 1 && pdate.getTime() >= sdate.getTime() && pdate.getTime() <= edate.getTime()) {
                Employee empProxy = employeeService.getEmployee(vp.getProxyId());

                if (null != empProxy) {
                    //短信
                    litigantList.clear();
                    litigantList.add(getLitigant(empProxy));
                    event.setReceivers(litigantList);

                    params.put(PARAM_URL, Constant.FASTDFS_URL + "e.html?v=" + vid);
                    event.setSendWay(SENDWAY_NOT_APP);
                    applicationEventPublisher.publishEvent(event);

                    //飞书需要特殊处理
//                    params.put("url", "https://applink.feishu.cn/client/web_app/open?appId=" + userInfo.getSecurityID() + "&path=/empClient/#/agent");
                    //params.put("url", Constant.FASTDFS_URL + "&path=/empClient/#/agent");
                    params.put(PARAM_URL, Constant.FASTDFS_URL + "empClient/?path=agent");
                    event.setSendWay(SENDWAY_APP);
                    applicationEventPublisher.publishEvent(event);
                }
            }
        }


        litigantList.clear();
        litigantList.add(getLitigant(emp));
        //litigantList.add(getLitigant(visitor));
        event.setReceivers(litigantList);

        //短信类
        params.put(PARAM_URL, Constant.FASTDFS_URL + "e.html?v=" + vid);
        event.setSendWay(SENDWAY_NOT_APP);
        applicationEventPublisher.publishEvent(event);

        //app
        params.put(PARAM_URL, Constant.FASTDFS_URL + "empClient/?path=myVisitor");

        event.setSendWay(SENDWAY_APP);
        applicationEventPublisher.publishEvent(event);
    }

    @Async
    @ProcessLogger("访客预约通知被访人授权")
    @Override
    public void sendSupplementPermissionNotifyEvent(Visitor visitor) {

        if (visitor == null) {
            return;
        }

        UserInfo userInfo = userService.getUserInfo(visitor.getUserid());
        if (userInfo == null) {
            return;
        }

        Employee emp = employeeService.getEmployee(visitor.getEmpid());
        if (emp == null) {
            return;
        }


        List<Litigant> litigantList = new ArrayList<>();
        litigantList.add(getLitigant(emp));
        NotifyEvent event = new NotifyEvent(userService, NotifyEvent.EVENTTYPE_VISITOR_SUPPLEMENT, litigantList);
        Map<String, String> params = getEventParams(visitor, emp, userInfo);
        event.setParams(params);
        event.setUserId(userInfo.getUserid());

        EmpVisitProxy vp = new EmpVisitProxy();
        vp.setEmpid(emp.getEmpid());
        vp.setUserid(emp.getUserid());
        vp = visitProxyService.getProxyInfoByEid(vp);
        if (null != vp) {
            Date pdate = new Date();
            Date sdate = vp.getStartDate();
            Date edate = vp.getEndDate();

            if (vp.getProxyStatus() == 1 && pdate.getTime() >= sdate.getTime() && pdate.getTime() <= edate.getTime()) {
                Employee empProxy = employeeService.getEmployee(vp.getProxyId());

                if (null != empProxy) {
                    //短信
                    litigantList.clear();
                    litigantList.add(getLitigant(empProxy));
                    event.setReceivers(litigantList);

                    if (visitor.getSigninType() == 1) {
                        String vid = AESUtil.encode("a" + visitor.getAppid() + "", Constant.AES_KEY);
                        params.put(PARAM_URL, Constant.FASTDFS_URL + "e.html?v=" + vid + "&supp=1");
                    } else {
                        String vid = AESUtil.encode("v" + visitor.getVid() + "", Constant.AES_KEY);
                        params.put(PARAM_URL, Constant.FASTDFS_URL + "e.html?v=" + vid + "&supp=1");
                    }
                    event.setSendWay(SENDWAY_NOT_APP);
                    applicationEventPublisher.publishEvent(event);

                    //飞书需要特殊处理
//                    params.put("url", "https://applink.feishu.cn/client/web_app/open?appId=" + userInfo.getSecurityID() + "&path=/empClient/#/agent");
                    params.put(PARAM_URL, Constant.FASTDFS_URL + "empClient/?path=agent");
                    event.setSendWay(SENDWAY_APP);
                    applicationEventPublisher.publishEvent(event);
                }
            }
        }


        litigantList.clear();
        litigantList.add(getLitigant(emp));
        event.setReceivers(litigantList);

        //短信类
        if (visitor.getSigninType() == 1) {
            String vid = AESUtil.encode("a" + visitor.getAppid() + "", Constant.AES_KEY);
            params.put(PARAM_URL, Constant.FASTDFS_URL + "e.html?v=" + vid + "&supp=1");
        } else {
            String vid = AESUtil.encode("v" + visitor.getVid() + "", Constant.AES_KEY);
            params.put(PARAM_URL, Constant.FASTDFS_URL + "e.html?v=" + vid + "&supp=1");
        }
        event.setSendWay(SENDWAY_NOT_APP);
        applicationEventPublisher.publishEvent(event);

        //app
        params.put(PARAM_URL, Constant.FASTDFS_URL + "empClient/?path=myVisitor");
        event.setSendWay(SENDWAY_APP);
        applicationEventPublisher.publishEvent(event);
    }



    @Async
    @ProcessLogger("通知审批人审批")
    @Override
    public void sendProcessNotifyEvent(Visitor visitor,List<Litigant> litigantList) {

        if (visitor == null) {
            return;
        }

        UserInfo userInfo = userService.getUserInfo(visitor.getUserid());
        if (userInfo == null) {
            return;
        }

        Employee emp = employeeService.getEmployee(visitor.getEmpid());
        if (emp == null) {
            return;
        }

        NotifyEvent event = new NotifyEvent(userService, NotifyEvent.EVENTTYPE_APPROVE_PROCESS, litigantList);
        Map<String, String> params = getEventParams(visitor, emp, userInfo);
        event.setParams(params);
        event.setUserId(userInfo.getUserid());
        event.setReceivers(litigantList);

        //短信类
        if (visitor.getSigninType() == 1) {
            String vid = AESUtil.encode("a" + visitor.getAppid() + "", Constant.AES_KEY);
        } else {
            String vid = AESUtil.encode("v" + visitor.getVid() + "", Constant.AES_KEY);
        }
        event.setSendWay(SENDWAY_NOT_APP);
        applicationEventPublisher.publishEvent(event);

        //app
        params.put(PARAM_URL, Constant.FASTDFS_URL + "empClient/?path=approve");
        event.setSendWay(SENDWAY_APP);
        applicationEventPublisher.publishEvent(event);
    }


    /**
     *
     * @param vtParam
     * @param eventType NotifyEvent.EVENTTYPE_SEND_INVITE EVENTTYPE_ACCEPT_APPOINTMENT
     */
    @ProcessLogger("向访客发送邀请函")
    public void sendInviteNotifyEvent(Visitor vtParam,String eventType) {
        Visitor visitor = null;
        String url = null;
        if (vtParam.getSigninType() == 1) {
            Appointment app = appointmentDao.getAppointmentbyId(vtParam.getAppid());
            if (app == null) {
                return;
            }
            visitor = BeanUtils.appointmentToVisitor(app);
            url = Constant.FASTDFS_URL + "bus.html?id=" + AESUtil.encode(app.getId() + "", Constant.AES_KEY);
        } else {
            visitor = vtParam;
            url = Constant.FASTDFS_URL + "bus.html?id=" + AESUtil.encode("v" + vtParam.getVid() + "", Constant.AES_KEY);
        }

        UserInfo userInfo = userService.getUserInfo(visitor.getUserid());
        if (userInfo == null) {
            return;
        }

        Employee emp = employeeService.getEmployee(visitor.getEmpid());

        List<Litigant> litigantList = new ArrayList<>();
        litigantList.add(getLitigant(visitor));
        NotifyEvent event = new NotifyEvent(userService, eventType, litigantList);
        Map<String, String> params = getEventParams(visitor, emp, userInfo);
        event.setParams(params);
        event.setUserId(userInfo.getUserid());
        event.setReceivers(litigantList);

        //短信类
        params.put(PARAM_URL, url);
        //event.setSendWay(SENDWAY_APP);
        applicationEventPublisher.publishEvent(event);
    }

    /**
     * 通用通知消息，适用于没有跳转链接的访客通知
     *
     * @param visitor   访客数据
     * @param eventType 事件类型 NotifyEvent.EVENTTYPE_xxx
     * @return 0成功 -1失败
     */
    @Async
    @ProcessLogger("发送抄送通知")
    @Override
    public void sendCCCommonNotifyEvent(Visitor visitor, String eventType,List<Litigant> litigantList) {
        if (visitor == null) {
            SysLog.error("参数错误 visitor==null eventType=" + eventType);
            return;
        }

        UserInfo userInfo = userService.getUserInfo(visitor.getUserid());
        if (userInfo == null) {
            SysLog.error("参数错误 userInfo==null eventType=" + eventType);
            return;
        }
        Employee emp = employeeService.getEmployee(visitor.getEmpid());

        NotifyEvent event = new NotifyEvent(userService, eventType, litigantList);
        Map<String, String> params = getEventParams(visitor, emp, userInfo);
        event.setParams(params);
        event.setUserId(userInfo.getUserid());
        event.setReceivers(litigantList);

        SysLog.info("通知事件发送：" + event);
        applicationEventPublisher.publishEvent(event);
        return;
    }


    /**
     * 通用通知消息，抄送
     *
     * @param visitor   访客数据
     * @param eventType 事件类型 NotifyEvent.EVENTTYPE_xxx
     * @return 0成功 -1失败
     */
    @Async
    @ProcessLogger("发送通知")
    @Override
    public void sendCommonNotifyEvent(Visitor visitor, String eventType) {
        if (visitor == null) {
            SysLog.error("参数错误 visitor==null eventType=" + eventType);
            return;
        }

        UserInfo userInfo = userService.getUserInfo(visitor.getUserid());
        if (userInfo == null) {
            SysLog.error("参数错误 userInfo==null eventType=" + eventType);
            return;
        }
        Employee emp = employeeService.getEmployee(visitor.getEmpid());

        List<Litigant> litigantList = new ArrayList<>();
        switch (eventType) {

            case NotifyEvent.EVENTTYPE_ADD_APPOINTMENT://通知被访人，有访客预约
            case NotifyEvent.EVENTTYPE_ADD_APPOINTMENT_PERMISSION://通知被访人，有访客预约,等待授权
            case NotifyEvent.EVENTTYPE_VISITOR_ACCEPT://访客同意邀请
            case NotifyEvent.EVENTTYPE_VISITOR_REJECT://访客拒绝邀请
            case NotifyEvent.EVENTTYPE_CHECK_IN://通知被访人访客已签到
            case NotifyEvent.EVENTTYPE_VISITOR_ARRIVED://访客进门提醒，有的访客需要先进门在签到
            case NotifyEvent.EVENTTYPE_EMP_NO_LEAVE://通知被访人访客离开超时
            case NotifyEvent.EVENTTYPE_APPROVE_PROCESS://通知审批人有审批需要处理
            case NotifyEvent.EVENTTYPE_VISITOR_SUPPLEMENT://访客补填信息
            case NotifyEvent.EVENTTYPE_APPROVE_FINISH://通知审批已完成
            case NotifyEvent.EVENTTYPE_APPROVE_REJECT://通知审批被拒绝
            {
                //发给员工的通知
                if (emp == null) {
                    SysLog.error("emp==null,empid：" + visitor.getEmpid());
                    return;
                }

                EmpVisitProxy vp = new EmpVisitProxy();
                vp.setEmpid(emp.getEmpid());
                vp.setUserid(emp.getUserid());
                vp = visitProxyService.getProxyInfoByEid(vp);
                if (null != vp) {
                    Date pdate = new Date();
                    Date sdate = vp.getStartDate();
                    Date edate = vp.getEndDate();

                    if (vp.getProxyStatus() == 1 && pdate.getTime() >= sdate.getTime() && pdate.getTime() <= edate.getTime()) {
                        Employee empProxy = employeeService.getEmployee(vp.getProxyId());

                        if (null != empProxy) {
                            litigantList.add(getLitigant(empProxy));
                        }
                    }
                }

                litigantList.add(getLitigant(emp));
                break;
            }
            case NotifyEvent.EVENTTYPE_TRACK_TIMEOUT_TOMANAGER://通知管理员获取轨迹失败
            case NotifyEvent.EVENTTYPE_TRACK_OFFROUTE_TOMANAGER://通知管理员偏离路线
            {
                break;
            }

            case NotifyEvent.EVENTTYPE_ACCEPT_APPOINTMENT://通知访客预约已授权
            case NotifyEvent.EVENTTYPE_SEND_INVITE://向访客发送邀请函
            {
                sendInviteNotifyEvent(visitor,eventType);
                break;
            }
            case NotifyEvent.EVENTTYPE_REJECT_APPOINTMENT://通知访客预约被拒绝

            case NotifyEvent.EVENTTYPE_VISIT_CANCEL://通知访客来访被取消
            case NotifyEvent.EVENTTYPE_VISITOR_NO_LEAVE://通知访客马上离开
            case NotifyEvent.EVENTTYPE_SUPPLEMENT_ACCEPT://通知访客补填被授权
            case NotifyEvent.EVENTTYPE_SUPPLEMENT_REJECT://通知访客补填被拒绝
            case NotifyEvent.EVENTTYPE_TRACK_TIMEOUT_TOVISITOR://通知访客获取轨迹失败
            case NotifyEvent.EVENTTYPE_TRACK_OFFROUTE_TOVISITOR://通知访客偏离路线
            {
                //发给访客的通知
                litigantList.add(getLitigant(visitor));
                break;
            }
            default: {
                SysLog.error("未知的通知类型：" + eventType);
            }
        }
        NotifyEvent event = new NotifyEvent(userService, eventType, litigantList);
        Map<String, String> params = getEventParams(visitor, emp, userInfo);
        event.setParams(params);
        event.setUserId(userInfo.getUserid());
        event.setReceivers(litigantList);

        SysLog.info("通知事件发送：" + event);
        applicationEventPublisher.publishEvent(event);
        return;
    }


    protected Map<String, String> getEventParams(Visitor visitor, Employee emp, UserInfo userinfo) {
        SimpleDateFormat time = new SimpleDateFormat("MM月dd日 HH时mm分");
        SimpleDateFormat etime = new SimpleDateFormat("dd/MM HH:mm");
        Map<String, String> params = new HashMap<>();
        if (emp != null) {
            params.put(PARAM_EMPNAME, emp.getEmpName());
        }
        if (visitor != null) {
            params.put(PARAM_VNAME, visitor.getVname());
            if (visitor.getAppointmentDate() != null) {
                params.put(PARAM_APPTIME, time.format(visitor.getAppointmentDate()));
                params.put(PARAM_APPTIME_EN, etime.format(visitor.getAppointmentDate()));
            }

            if (visitor.getVisitdate() != null) {
                params.put(PARAM_VISITTIME, time.format(visitor.getVisitdate()));
                params.put(PARAM_VISITTIME_EN, etime.format(visitor.getVisitdate()));
            }

            if (StringUtils.isNotBlank(visitor.getvType())) {
                params.put(PARAM_VISITKIND, visitor.getvType());
            }
            if (StringUtils.isNotBlank(visitor.getVphone())) {
                params.put(PARAM_VISITPHONE, visitor.getVphone());
            }

            if (StringUtils.isNotBlank(visitor.getVisitType())) {
                params.put(PARAM_VISITTYPE, visitor.getVisitType());
            }

            if (ObjectUtils.isNotEmpty(visitor.getExtendValue(VisitorService.EXTEND_KEY_ENDDATE))) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                format.setTimeZone(TimeZone.getTimeZone("gmt"));
                try {
                    Date endDate = format.parse(visitor.getExtendValue(VisitorService.EXTEND_KEY_ENDDATE));
                    params.put(PARAM_ENDTIME, time.format(endDate));
                    params.put(PARAM_ENDTIME_EN, etime.format(endDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            if(StringUtils.isNotBlank(visitor.getRemark())){
                params.put(PARAM_REMARK,visitor.getRemark());
            }

//            if (0 != visitor.getQrcodeConf()) {
//                Date visitdate = visitor.getAppointmentDate();
//                Calendar calendar = new GregorianCalendar();
//                calendar.setTime(visitdate);
//                calendar.add(calendar.DATE, visitor.getQrcodeConf() - 1);
//                calendar.set(Calendar.HOUR_OF_DAY, 23);
//                calendar.set(Calendar.MINUTE, 59);
//                calendar.set(Calendar.SECOND, 59);
//                calendar.set(Calendar.MILLISECOND, 999);
//                visitdate = calendar.getTime();
//                String format = dateFormat.format(visitdate);
//                params.put("vtime", format);
//            }
            if (userinfo.getSubAccount() == 1 && visitor.getSubaccountId() != 0) {
                SubAccount sa = subAccountService.getSubAccountById(visitor.getSubaccountId());
                params.put(PARAM_COMPANY, sa.getCompanyName());
            } else {
                params.put(PARAM_COMPANY, userinfo.getCardText());
            }
            if (StringUtils.isNotBlank(visitor.getVcompany())) {
                params.put(PARAM_VCOMPANY, visitor.getVcompany());
            }
        }



//        params.put("url", "http://sms");
        return params;
    }

    @ProcessLogger("向访客发送验证码")
    public void sendCodeNotifyEvent(String code,Visitor visitor) {
        List<Litigant> litigantList = new ArrayList<>();
        litigantList.add(getLitigant(visitor));
        NotifyEvent event = new NotifyEvent(userService, NotifyEvent.EVENTTYPE_CODE, litigantList);
        Map<String, String> params = new HashMap<>();
        params.put("code",code);
        event.setParams(params);
        event.setReceivers(litigantList);
        applicationEventPublisher.publishEvent(event);
    }

    /**
     * 获取员工接收者
     *
     * @param employee
     * @return
     */
    private Litigant getLitigant(Employee employee) {
        Litigant litigant = new Litigant();
        litigant.setName(employee.getEmpName());
        litigant.setPhone(employee.getEmpPhone());
        litigant.setEmail(employee.getEmpEmail());
        litigant.setOpenid(employee.getOpenid());
        litigant.setType("员工");
        return litigant;
    }

    /**
     * 获取访客接收者
     *
     * @param visitor
     * @return
     */
    private Litigant getLitigant(Visitor visitor) {
        Litigant litigant = new Litigant();
        litigant.setName(visitor.getVname());
        litigant.setPhone(visitor.getVphone());
        litigant.setEmail(visitor.getVemail());
        litigant.setVtype(visitor.getvType());
        litigant.setType("访客");

        Person visitPersonByPhone = personInfoService.getVisitPersonByPhone(visitor.getVphone());
        if (visitPersonByPhone != null) {
            litigant.setOpenid(visitPersonByPhone.getPopenid());
        }
        return litigant;
    }


}
