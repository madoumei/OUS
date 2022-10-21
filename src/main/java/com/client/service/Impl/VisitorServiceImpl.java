package com.client.service.Impl;


import cn.jpush.api.JPushClient;
import cn.jpush.api.common.APIConnectionException;
import cn.jpush.api.common.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.alibaba.fastjson.JSON;
import com.annotation.ProcessLogger;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.client.bean.*;
import com.client.dao.VisitorDao;
import com.client.service.*;
import com.config.activemq.MessageSender;
import com.config.exception.ErrorEnum;
import com.config.exception.ErrorException;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.event.event.NotifyEvent;
import com.event.event.PassEvent;
import com.event.event.VisitEvent;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utils.*;
import com.utils.cacheUtils.CacheManager;
import com.utils.emailUtils.SendAppointmentEmail;
import com.utils.emailUtils.SendExchangeEmail;
import com.utils.emailUtils.SendHtmlEmail;
import com.utils.empUtils.EmpUtils;
import com.utils.httpUtils.HttpClientUtil;
import com.utils.jsonUtils.JacksonJsonUtil;
import com.utils.jsonUtils.JsonUtil;
import com.utils.visitorUtils.VisitorUtils;
import com.utils.yimei.DateUtil;
import com.web.bean.*;
import com.web.dao.*;
import com.web.service.*;
import com.web.service.impl.PassRuleServiceImpl;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service("visitorService")
public class VisitorServiceImpl extends ServiceImpl<VisitorDao,Visitor>  implements VisitorService {

    private final static Logger logger = LoggerFactory.getLogger(VisitorServiceImpl.class);

    @Autowired
    private VisitorDao visitorDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ConfigureDao configureDao;

    @Autowired
    private SubAccountDao subAccountDao;

    @Autowired
    MeetingDao meetingDao;

    @Autowired
    private AppointmentDao appointmentDao;

    @Autowired
    private EmployeeDao employeeDao;


    @Autowired
    private VisitProxyDao visitProxyDao;

    private RedisTemplate redisTemplate;

    @Autowired
    private OperateLogService operateLogService;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private ExtendVisitorService extendVisitorService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private StringRedisTemplate strRedisTemplate;

    @Autowired
    private EGRelationService eGRelationService;

    @Autowired
    private OpendoorService opendoorService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private EquipmentGroupService equipmentGroupService;

    @Autowired
    private PassService passService;

    @Autowired
    private VisitorTypeService visitorTypeService;

    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    private PersonInfoService personInfoService;

    @Autowired
    private TrackService trackService;

    @Autowired
    private GeofenceService geofenceService;

    @Autowired
    private CamundaProcessService camundaProcessService;

    @Autowired
    private EqptRuleService eqptRuleService;

    @Autowired
    private EgPtRltService egPtRltService;

    @Autowired
    private PassRuleService passRuleService;


    @Inject
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String getEType(String qrcode) {
        return qrcode.substring(19, 21); // 01 员工  02 访客  03 常驻访客
    }

    /**
     * 访客码类型
     * @param qrcode 访客二维码
     * @return
     */
    @Override
    public String getQrcodeType(String qrcode) {
        return qrcode.substring(21, 23); // 23 邀请  25 预约 40 打印贴纸二维码
    }

    /**
     * 是否允许刷邀约码开门
     * @param qrcodeType 访客二维码类型
     */
    @Override
    public void checkAppCodeAccessTask(String qrcodeType) {
         if(!qrcodeType.equals("23")
         && !qrcodeType.equals("25")){
             return;
         }
        if (Constant.AllowedAppCodeAccess == null
                || Constant.AllowedAppCodeAccess.equals("0")) {
            throw new ErrorException(ErrorEnum.E_1127);
        }
    }

    /**
     * 检查二维码有效期
     * @param qrcode
     */
    @Override
    @ProcessLogger("检查二维码有效期")
    public void checkQrcodeExpiredDateTask(String qrcode) {
        if(qrcode.length()<= 10){
            //ic卡
            return;
        }
        if (qrcode.length() < 20) {
            throw new ErrorException(ErrorEnum.E_1112);
        }
        String aes = DigestUtils.sha512Hex(qrcode.substring(16) + "csjm759").substring(8, 23);
        if (!aes.equals(qrcode.substring(1, 16))) {
            throw new ErrorException(ErrorEnum.E_1112);
        }

        String expireddate = qrcode.substring(34, 44);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        try {
            Date date = sdf.parse("20" + expireddate);
            if (date.getTime() < new Date().getTime()) {
                throw new ErrorException(ErrorEnum.E_1128);
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 检查预约访客授权及有效期
     * @param vid
     * @return
     */
    @Override
    public Visitor checkVisitorPermissionTask(String vid) {
        Visitor vt = getVisitorById(Integer.parseInt(vid));
        if(vt == null){
            throw new ErrorException(ErrorEnum.E_703);
        }

        UserInfo ui = userDao.getUserInfo(vt.getUserid());
        checkAppointmentVisitorLimitTask(vt,ui);
        return vt;
    }



    /**
     * 未填写补充信息不可签到
     *
     * @param app 邀请bean
     * @return
     */
    @Override
    public void checkSigninAppointmentQrcodeTask(Appointment app) {
        if(app == null){
            return;
        }
        int userid = app.getUserid();
        UserInfo userInfo = userDao.getUserInfo(userid);
        if (ObjectUtils.isNotEmpty(userInfo)
                && com.config.qicool.common.utils.StringUtils.isNotBlank(userInfo.getAppointmenProcessSwitch())
                && "1".equalsIgnoreCase(userInfo.getAppointmenProcessSwitch())){
            if (6 == app.getStatus() && 1!=app.getPermission()){
                throw new ErrorException(ErrorEnum.E_1129);
            }
        }
    }

    /**
     * 结束拜访拜访后不允许进门
     * @param visitor 签到记录
     * @return
     * @throws IOException
     */
    @Override
    public void checkLeaveStatusTask(Visitor visitor) {
        if(visitor == null){
            throw new ErrorException(ErrorEnum.E_703);
        }
        if(visitor.getLeaveTime()!=null
        &&visitor.getLeaveTime().getTime()>visitor.getVisitdate().getTime()){
            throw new ErrorException(ErrorEnum.E_1126);
        }
    }

    /**
     * 签出后不允许进门
     * @param visitor 签到记录
     * @return
     * @throws IOException
     */
    @Override
    public void checkSignoutStatusTask(Visitor visitor) {
        if(visitor == null){
            throw new ErrorException(ErrorEnum.E_703);
        }
        if(visitor.getSignOutDate()!=null
                &&visitor.getSignOutDate().getTime()>visitor.getVisitdate().getTime()){
            throw new ErrorException(ErrorEnum.E_1203);
        }
    }


    /**
     * 检查签到状态是否有问题，是否允许未签到进门，是否允许一次签到多天进门
     * @param v
     * @throws IOException
     */
    @Override
    public void checkSigninStatusTask(Visitor v) {
        if(v == null){
            throw new ErrorException(ErrorEnum.E_703);
        }

        if(Constant.AccessWithoutSignin == null
                || Constant.AccessWithoutSignin.equals("1")) {
            //允许未签到进门
            return;
        }
        //不允许未签到进门
        if(v==null||v.getVisitdate()==null){
            throw new ErrorException(ErrorEnum.E_1125);
        }

        ExtendVisitor ev = new ExtendVisitor();
        ev.seteType(v.getvType());
        ev.setUserid(v.getUserid());
        List<ExtendVisitor> extendVisitors = extendVisitorService.getExtendVisitorByType(ev);
        String moreDays = "0";
        for (int i = 0; i < extendVisitors.size(); i++) {
            /**
             * 进门次数约束： moreDays:0-当天有效，1-多天有效
             */
            if ("moreDays".equals(extendVisitors.get(i).getFieldName())) {
                moreDays = extendVisitors.get(i).getInputValue();
            }

        }
        //当天有效，检查签到日期是不是当天
        if("0".equals(moreDays)
                && org.apache.commons.lang3.time.DateUtils.truncatedCompareTo(v.getVisitdate(), new Date(), Calendar.DATE) != 0) {
            throw new ErrorException(ErrorEnum.E_1125);
        }
    }


    /**
     * 是否要自动签到
     * @param vt
     * @return
     */
    @Override
    public int autoSigninRouter(Visitor vt) {
        if(vt == null){
            throw new ErrorException(ErrorEnum.E_703);
        }
        if((vt.getVisitdate() == null
                || DateUtils.truncatedCompareTo(vt.getVisitdate(), new Date(), Calendar.DATE)!=0)//当天是否签到
                && Constant.AutoSignin != null
                && Constant.AutoSignin.equals("1")) {
            return 1;
        }
        return 0;
    }


    /**
     * 检查有效性，并返回员工数据
     * @param empid
     * @return
     */
    @Override
    public Employee checkEmployeeTask(String empid){
        int evid = Integer.parseInt(empid);
        Employee emp = employeeService.getEmployee(evid);
        if(emp == null){
            throw new ErrorException(ErrorEnum.E_055);
        }
        //有效期
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        int d1 = Integer.parseInt(emp.getStartDate());
        int d2 = Integer.parseInt(emp.getEndDate());
        int today = Integer.parseInt(sd.format(new Date()));
        if (today < d1 || today > d2) {
            throw new ErrorException(ErrorEnum.E_1113);
        }

        //离职状态
        if(emp.getEmpType() == 4){
            throw new ErrorException(ErrorEnum.E_055);
        }
        return emp;
    }

    @Override
    public Employee checkEmployeeByCardNoTask(String cardNo){
        Employee search = new Employee();
        search.setCardNo(cardNo);
        Employee employee = employeeService.getEmployeeByCardNo(search);
        if(employee != null){
            return checkEmployeeTask(employee.getEmpid()+"");
        }
        throw new ErrorException(ErrorEnum.E_055);
    }

    /**
     * 检查设备有效性，并返回设备信息
     * @param egids
     * @param devicenumber
     * @return
     */
    @ProcessLogger("检查门禁是否授权")
    @Override
    public Equipment checkEquipmentTask(String egids, String devicenumber){
        //检查是否被授权该门禁
        Map<String, String> map = new HashMap<String, String>();
        map.put("deviceQrcode", devicenumber);
        if(StringUtils.isNotEmpty(egids)) {
            map.put("egids", "(" + egids + ")");
        }else{
            throw new ErrorException(ErrorEnum.E_1111);
        }
        Equipment eq = eGRelationService.getEquipmentByDq(map);
        if (null == eq) {
            throw new ErrorException(ErrorEnum.E_1111);
        }
        return eq;
    }

    /**
     * 检查门禁潮汐策略
     * @param eqpt
     * @return
     */
    @ProcessLogger("检查门禁潮汐策略")
    @Override
    public void checkEquipmentRuleTask(Equipment eqpt){
        List<EqptRule> list = eqptRuleService.getList(eqpt.getUserid(), eqpt.getEid());
        if (null == list||list.size()==0) {
            //没有策略直接放行
            return;
        }
        PassRule searchPassRule = new PassRule();
        searchPassRule.setUserid(eqpt.getUserid());
        List<PassRule> passRuleList = passRuleService.getPassRuleList(searchPassRule);
        Date now = new Date();
        for(EqptRule rule:list){
            //是否为工作日最后一天
            if(isLastWorkday(now,passRuleList.get(0))){
                if(!rule.getRname().contains("周五")){
                    continue;
                }
            }else if(isWorkday(now,passRuleList.get(0))){
                //工作日
                if(!rule.getRname().contains("周一至周四")){
                    continue;
                }
            }else if(isLastHoliday(now,passRuleList.get(0))){
                //最后一个放假日
                if(!rule.getRname().contains("周日")){
                    continue;
                }
            }else {
                //节假日
                if(!rule.getRname().contains("周六")){
                    continue;
                }
            }


            if(rule.getStartDate()!=null&&rule.getStartDate().compareTo(now) == 1){
                continue;
            }

            if(rule.getEndDate()!=null&&rule.getEndDate().compareTo(now) == -1){
                continue;
            }

            if(1== PassRuleServiceImpl.Confirm_today(now,rule.getTime())){
                throw new ErrorException(ErrorEnum.E_1130);
            }
        }
    }


    /**
     * 检查通行策略
     * @param eq
     * @param employee
     * @return
     */
    @ProcessLogger("检查通行策略")
    @Override
    public void checkEquipmentPassTimeTask(Equipment eq, Employee employee) {
        List<EquipmentGroup> eglist = eGRelationService.getEGroupByEid(eq);
        String egidsEmp = employee.getEgids();

        if (eglist.size() == 0 || StringUtils.isEmpty(egidsEmp)) {
            return;
        }

        //取设备和人员的交集
        List<EqptGroupPassTimeRlt> list = new ArrayList<EqptGroupPassTimeRlt>();//策略集合
        for (EquipmentGroup eg : eglist) {
            if (String.valueOf("," + egidsEmp + ",").contains("," + eg.getEgid() + ",")) {
                list.addAll(egPtRltService.getList(eq.getUserid(), eg.getEgid()));
            }
        }

        if (list.size() == 0) {
            //没有额外的策略
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        for (EqptGroupPassTimeRlt rlt : list) {
            String[] ridList = rlt.getRids().split(",");
            for (int i = 0; i < ridList.length; i++) {
                PassRule pr = new PassRule();
                pr.setRid(Integer.parseInt(ridList[i]));
                pr.setUserid(rlt.getUserid());
                List<PassRule> passRuleList = passRuleService.getPassRuleList(pr);//设置的通行时刻表
                if (passRuleList.size() == 0) {
                    //不应该存在这个情况
                    continue;
                }

                PassRule passRule = passRuleList.get(0);

                try {
                    if (dateFormat.parse(passRule.getStartDate()).compareTo(now) == 1
                            || dateFormat.parse(passRule.getEndDate()).compareTo(now) == -1) {
                        //不在有效期内
                        continue;
                    }
                } catch (Exception e) {
                    continue;
                }

                //德威定制
                {
                    if(isLastWorkday(now,passRule)){
                        if(1== PassRuleServiceImpl.Confirm_today(now,passRule.getFri())){
                            return;
                        }
                    }else if(isLastHoliday(now,passRule)){
                        if(1== PassRuleServiceImpl.Confirm_today(now,passRule.getSun())){
                            return;
                        }
                    }
                }

                //是否为调的工作日
                List<DaysOffTranslation> offTranslations = passRule.getDaysOffTranslations();
                for (DaysOffTranslation offDay : offTranslations) {
                    if (DateUtils.isSameDay(offDay.getDate(), now)) {
                        if(1== PassRuleServiceImpl.Confirm_today(now,passRule.getDaysOff())){
                            return;
                        }
                    }
                }

                //是否为调休休息日
                List<Holiday> holidays = passRule.gethList();
                for (Holiday holiday : holidays) {
                    if (DateUtils.isSameDay(holiday.getHdate(), now)) {
                        if(1== PassRuleServiceImpl.Confirm_today(now,passRule.getHol())){
                            return;
                        }
                    }
                }

                int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
                String passTime = "";

                switch (weekDay) {
                    case 1:
                        passTime = passRule.getSun();
                        break;
                    case 2:
                        passTime = passRule.getMon();
                        break;
                    case 3:
                        passTime = passRule.getTues();
                        break;
                    case 4:
                        passTime = passRule.getWed();
                        break;
                    case 5:
                        passTime = passRule.getThur();
                        break;
                    case 6:
                        passTime = passRule.getFri();
                        break;
                    case 7:
                        passTime = passRule.getSat();
                        break;
                }


                if(1== PassRuleServiceImpl.Confirm_today(now,passTime)){
                    return;
                }

            }
        }
        throw new ErrorException(ErrorEnum.E_1115);
    }

    //是不是最后一个工作日
    private boolean isLastWorkday(Date date,PassRule passRule){
        if(!isWorkday(date,passRule)){
            return false;
        }
        Calendar calendar =  Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(calendar.DATE,1);
        return isHoliday(calendar.getTime(),passRule);
    }

    //是不是假期最后一天
    private boolean isLastHoliday(Date date,PassRule passRule){
        if(!isHoliday(date,passRule)){
            return false;
        }
        Calendar calendar =  Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(calendar.DATE,1);
        return !isHoliday(calendar.getTime(),passRule);
    }

    //是不是工作日
    private boolean isWorkday(Date date,PassRule passRule){
        //是否为调的工作日
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<DaysOffTranslation> offTranslations = passRule.getDaysOffTranslations();
        for(DaysOffTranslation offDay:offTranslations){
            if(DateUtils.isSameDay(offDay.getDate(),date)){
                return true;
            }
        }

        //是否为调休休息日
        List<Holiday> holidays = passRule.gethList();
        for(Holiday holiday:holidays){
            if(DateUtils.isSameDay(holiday.getHdate(),date)){
                return false;
            }
        }

        //是否为周末
        Calendar calendar =  Calendar.getInstance();
        calendar.setTime(date);
        if(calendar.get(Calendar.DAY_OF_WEEK)==1
                ||calendar.get(Calendar.DAY_OF_WEEK)==7){
            return false;
        }
        return true;
    }

    //是不是假期
    private boolean isHoliday(Date date,PassRule passRule){
        //是否为调的工作日
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<DaysOffTranslation> offTranslations = passRule.getDaysOffTranslations();
        for(DaysOffTranslation offDay:offTranslations){
            if(DateUtils.isSameDay(offDay.getDate(),date)){
                return false;
            }
        }

        //是否为调休休息日
        List<Holiday> holidays = passRule.gethList();
        for(Holiday holiday:holidays){
            if(DateUtils.isSameDay(holiday.getHdate(),date)){
                return true;
            }
        }

        //是否为周末
        Calendar calendar =  Calendar.getInstance();
        calendar.setTime(date);
        if(calendar.get(Calendar.DAY_OF_WEEK)==1
        ||calendar.get(Calendar.DAY_OF_WEEK)==7){
            return true;
        }
        return false;
    }

    /**
     * 是否自动签出
     * @param vt
     * @return 1自动签出 0不自动签出
     */
    @Override
    public int autoSignoutRouter(Visitor vt){
        if ((Constant.AutoSignoutAfterLeave != null&& Constant.AutoSignoutAfterLeave.equals("1")
                //如果已经结束拜访，自动签出
                && null != vt.getLeaveTime())
                && vt.getLeaveTime().getTime()>vt.getVisitdate().getTime()
                ||(Constant.AutoSignout != null && Constant.AutoSignout.equals("1"))) {
            return 1;
        }
        return 0;
    }

    /**
     * 签离
     * @param vt
     */
    @Override
    public void autoSignoutTask(Visitor vt, Equipment equipment) {
        if (vt == null ) {
            throw new ErrorException(ErrorEnum.E_703);
        }

        vt.setSignOutDate(new Date());
        if(equipment != null) {
            vt.setSignOutOpName(equipment.getDeviceName());
        }else{
            vt.setSignOutOpName(vt.getSignOutOpName());
            vt.setSignOutGate(vt.getSignOutGate());
        }
        updateSignOut(vt);
        if (vt.getAppid() != 0) {
            if (strRedisTemplate.hasKey("aid_" + vt.getAppid())) {
                strRedisTemplate.delete("aid_" + vt.getAppid());
            }
        } else {
            if (strRedisTemplate.hasKey("vid_" + vt.getVid())) {
                strRedisTemplate.delete("vid_" + vt.getVid());
            }
        }


    }

    /**
     * 检查是否在管理员设置的访客允许通行时间（工作时间）范围内
     * @param ui
     * @return
     * @throws IOException
     */
    @Override
    public boolean checkVisitorAccessTimeTask(UserInfo ui) {
        /**
         * 开门时间判断
         * 邀请时间-----》员工下班时间
         */
        String time[] = ui.getUpDuty().split(":");
        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        long start = calendar.getTime().getTime();
        long now = new Date().getTime();
        /**
         * 获取企业下班时间
         */
        String time2[] = ui.getOffDuty().split(":");
        int hour2 = Integer.parseInt(time2[0]);
        int minute2 = Integer.parseInt(time2[1]);
        calendar.set(Calendar.HOUR_OF_DAY, hour2);
        calendar.set(Calendar.MINUTE, minute2);
        calendar.set(Calendar.SECOND, 0);
        long end = calendar.getTime().getTime();
        if (now > end || now < start) {
            throw new ErrorException(ErrorEnum.E_1124);
        }
        return false;
    }

    /**
     * 检查邀请函中次数和有效天数的限制,兼顾考虑了访客时间，答题
     * @param vt
     * @param ui
     * @return true 未通过，false 通过
     */
    @Override
    public boolean checkAppointmentVisitorLimitTask(Visitor vt, UserInfo ui) {
        if(vt.getStatus() == 4
        || vt.getPermission() != 1){
            //已拒绝
            throw new ErrorException(ErrorEnum.E_1121);
        }

        //天数
        if (null != vt && vt.getQrcodeType() == 0) {
            String time[] = ui.getUpDuty().split(":");
            int hour = Integer.parseInt(time[0]);
            int minute = Integer.parseInt(time[1]);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(vt.getAppointmentDate());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            long start = calendar.getTime().getTime();
            long now = new Date().getTime();

            //判断是否在邀请时间段拜访
            calendar.add(calendar.DATE, vt.getQrcodeConf() - 1);//把日期往后增加一天.整数往后推,负数往前移动
            Date d1 = calendar.getTime();
            calendar.setTime(d1);
            String time2[] = ui.getOffDuty().split(":");
            int hour2 = Integer.parseInt(time2[0]);
            int minute2 = Integer.parseInt(time2[1]);
            calendar.set(Calendar.HOUR_OF_DAY, hour2);
            calendar.set(Calendar.MINUTE, minute2);
            calendar.set(Calendar.SECOND, 0);
            long end = calendar.getTime().getTime();
            if (now > end || now < start) {
                throw new ErrorException(ErrorEnum.E_1115);
            }

        }
//        else if (null != app && app.getQrcodeType() == 1 && app.getStatus() != 4) {
//            //二维码按次数
//            int opencount = app.getOpenCount();
//
//            String time[] = ui.getUpDuty().split(":");
//            int hour = Integer.parseInt(time[0]);
//            int minute = Integer.parseInt(time[1]);
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(app.getAppointmentDate());
//            calendar.set(Calendar.HOUR_OF_DAY, hour);
//            calendar.set(Calendar.MINUTE, minute);
//            calendar.set(Calendar.SECOND, 0);
//            long start = calendar.getTime().getTime();
//            long now = new Date().getTime();
//            String time2[] = ui.getOffDuty().split(":");
//            int hour2 = Integer.parseInt(time2[0]);
//            int minute2 = Integer.parseInt(time2[1]);
//            calendar.set(Calendar.HOUR_OF_DAY, hour2);
//            calendar.set(Calendar.MINUTE, minute2);
//            calendar.set(Calendar.SECOND, 0);
//            long end = calendar.getTime().getTime();
//
//            if (opencount >= app.getQrcodeConf() || now > end || now < start) {
//                throw new ErrorException(ErrorEnum.E_1114);
//            }
//
//        }
        else if(vt.getQrcodeType() != 0 && vt.getQrcodeType() != 1){
            //qrcodeType 类型错误
            throw new ErrorException(ErrorEnum.E_1122);
        }
        else if(vt.getIsBlackList() == 1){
            //qrcodeType 类型错误
            throw new ErrorException(ErrorEnum.E_066);
        }
        else {
            throw new ErrorException(ErrorEnum.E_1123);
        }
        return false;
    }

    /**
     * 检查是否允许多次进入，检查是否允许扫码进入
     * @param ui
     * @param vType 访客类型
     * @param vphone
     * @return
     * @throws IOException
     */
    @Override
    public void checkTimesTask(UserInfo ui, String vType, String vphone) throws IOException {
        ExtendVisitor ev = new ExtendVisitor();
        ev.seteType(vType);
        ev.setUserid(ui.getUserid());
        List<ExtendVisitor> extendVisitors = extendVisitorService.getExtendVisitorByType(ev);
        String moreTimes = "0";
        for (int i = 0; i < extendVisitors.size(); i++) {
            /**
             * 进门次数约束： moreTimes:0-单次，1-多次
             */
            if ("moreTimes".equals(extendVisitors.get(i).getFieldName())) {
                moreTimes = extendVisitors.get(i).getInputValue();
            }
        }

        //检查是否允许多次通行
        if ("0".equals(moreTimes)) {
            Date d = new Date();
            OpendoorInfo opendoorInfo = new OpendoorInfo();
            opendoorInfo.setUserid(ui.getUserid());
            opendoorInfo.setOpenDate(d);
            opendoorInfo.setMobile(vphone);
            opendoorInfo = opendoorService.getLastRecords(opendoorInfo);
            if (null != opendoorInfo) {
                throw new ErrorException(ErrorEnum.E_1114);
            }
        }
    }

    /**
     *
     * @param ui UserInfo
     * @param vType 访客类型
     * @param accType 0 刷脸，1 扫码，2 车牌
     * @return
     * @throws IOException
     */
    @Override
    public void checkAccessTypeTask(UserInfo ui, String vType, String accType) throws IOException {
        ExtendVisitor ev = new ExtendVisitor();
        ev.seteType(vType);
        ev.setUserid(ui.getUserid());
        List<ExtendVisitor> extendVisitors = extendVisitorService.getExtendVisitorByType(ev);
        String accessType = "";
        for (int i = 0; i < extendVisitors.size(); i++) {
            /**
             * 进门次数约束： accessType:"0，1"   0 刷脸，1 扫码
             */
            if ("accessType".equals(extendVisitors.get(i).getFieldName())) {
                accessType = extendVisitors.get(i).getInputValue();
            }
        }

        //检查是否可以二维码通行
        if (accessType.indexOf(accType) == -1) {
            throw new ErrorException(ErrorEnum.E_1120);
        }
    }

    /**
     * 检查答题状态
     * @param userinfo
     * @param vt
     */
    @Override
    public void checkQuestionnaireTask(UserInfo userinfo, Visitor vt){
        //答题检查
        if (userinfo.getQuestionnaireSwitch() == 1) {
            VisitorAnswer va = new VisitorAnswer();
            if (com.config.qicool.common.utils.StringUtils.isNotBlank(vt.getVphone())) {
                va.setIdentity(vt.getVphone());
            } else if (com.config.qicool.common.utils.StringUtils.isNotBlank(vt.getVemail())) {
                va.setIdentity(vt.getVemail());
            } else {
                va.setIdentity(vt.getCardId());
            }

            va.setUserid(vt.getUserid());
            va = questionnaireService.getAnswerByIdentity(va);

            VisitorType visType = new VisitorType();
            visType.setTid(vt.getTid());
            visType.setUserid(vt.getUserid());

            //通过tid查询用户访客类型，以及有效期周期
            visType = visitorTypeService.getVisitorTypeByTid(visType);

            if (null == visType) {
                SysLog.error("get no visitType by tid：");
                throw new ErrorException(ErrorEnum.E_068);
            }

            if (null == va && StringUtils.isNotBlank(visType.getQid())) {
                throw new ErrorException(ErrorEnum.E_069);
            }

            if (null != va && com.config.qicool.common.utils.StringUtils.isNotBlank(visType.getQid())) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(va.getPassDate());
                cal.add(Calendar.DATE, visType.getPovDays()-1);
                Date endDate = DateUtil.getMaxTimeDateByDate(cal.getTime());
                Date date = new Date();
                if (date.getTime() > endDate.getTime()) {
                    throw new ErrorException(ErrorEnum.E_069);
                }
            }
        }
    }

    /**
     * 单次进出情况通知senselink访客已进入
     * @param ui
     * @param eq
     * @param visitor
     * @return
     * @throws IOException
     */
    @Override
    public void sendVisitorPassMsgTask(UserInfo ui, Equipment eq, Visitor visitor){
        ExtendVisitor ev = new ExtendVisitor();
        ev.seteType(visitor.getvType());
        ev.setUserid(ui.getUserid());
        List<ExtendVisitor> extendVisitors = extendVisitorService.getExtendVisitorByType(ev);
        String moreTimes = "0";
        for (int i = 0; i < extendVisitors.size(); i++) {
            /**
             * 进门次数约束： moreTimes:0-单次，1-多次
             */
            if ("moreTimes".equals(extendVisitors.get(i).getFieldName())) {
                moreTimes = extendVisitors.get(i).getInputValue();
            }
        }
        String vid = null;
        if(visitor.getVid()!=0){
            vid = "v" + visitor.getVid();
        }else{
            vid ="a" + visitor.getAppid();
        }

        //单次进出情况通知senselink
        Map<String, Object> empmap = new HashMap<String, Object>();
        empmap.put("key", "qrcode_pass");
        empmap.put("company_id", ui.getUserid());
        empmap.put("visit_id", vid);
        empmap.put("direction", eq.getEnterStatus());
        empmap.put("pass_type", Integer.parseInt(moreTimes));
        messageSender.updateFaceLib(empmap);

    }

    /**
     * 给预约记录增加扩展信息
     * @param userinfo
     * @param vt
     */
    @Override
    public void addExtendSetting(UserInfo userinfo,Visitor vt){
        //获取扩展信息
        String moreTimes = "0";
        String accessType="";
        String moreDays="1";
        ExtendVisitor ev = new ExtendVisitor();
        if(StringUtils.isNotEmpty(vt.getvType())) {
            ev.seteType(vt.getvType());
        }else{
        //获取默认类型
            List<String> extendTypeList = extendVisitorService.getExtendTypeList(vt.getUserid());
            if(extendTypeList.size()>0) {
                for (String eType : extendTypeList) {
                    if (eType.contains("普通")) {
                        ev.seteType(eType);
                        break;
                    }
                }
                if (StringUtils.isEmpty(ev.geteType())) {
                    ev.seteType(extendTypeList.get(0));
                }
            }
        }
        ev.setUserid(vt.getUserid());
        List<ExtendVisitor> extendVisitors = extendVisitorService.getExtendVisitorByType(ev);
        for(int i=0;i<extendVisitors.size();i++){
            if("moreTimes".equals(extendVisitors.get(i).getFieldName())){
                moreTimes = extendVisitors.get(i).getInputValue();
            }
            if("accessType".equals(extendVisitors.get(i).getFieldName())){
                accessType = extendVisitors.get(i).getInputValue();
            }
            if("moreDays".equals(extendVisitors.get(i).getFieldName())){
                moreDays = extendVisitors.get(i).getInputValue();
            }
        }
        vt.addExtendValue("moreTimes", moreTimes);
        vt.addExtendValue("moreDays", moreDays);
        vt.addExtendValue("accessType", accessType);
    }

    @Override
    public int addVisitor(Visitor vt) {
        // TODO Auto-generated method stub
        return visitorDao.addVisitor(vt);
    }

    /**
     * 只更新头像
     * @param vt
     * @return
     */
    @Override
    public int updateVisitor(Visitor vt) {
        // TODO Auto-generated method stub
        return visitorDao.updateVisitor(vt);
    }

    @Override
    public int updateVGroup(Visitor vt) {
        LambdaUpdateWrapper<Visitor> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Visitor::getVid,vt.getVid())
                .set(Visitor::getVgroup,vt.getVid());
        int ret = visitorDao.update(null,updateWrapper);
        if(ret == 0){
            throw new ErrorException(ErrorEnum.E_063);
        }
        return ret;
    }

    @Override
    public boolean sendMail(UserInfo userinfo, Employee emp, Visitor vt, List<ExtendVisitor> evlist) throws JsonParseException, JsonMappingException, IOException {
        // TODO Auto-generated method stub
        if (userinfo.getEmailType() == 1) {
            SendHtmlEmail she = new SendHtmlEmail();
            return she.send(userinfo, emp, vt, evlist);
        } else if (userinfo.getEmailType() == 2) {
            SendExchangeEmail see = new SendExchangeEmail();
            return see.send(userinfo, emp, vt, evlist);
        } else {
            SendHtmlEmail she = new SendHtmlEmail();
            return she.send(userinfo, emp, vt, evlist);
        }
    }

    @Override
    public Visitor getVisitor(Visitor v) {
        // TODO Auto-generated method stub
        return visitorDao.getVisitor(v);
    }

    @Override
    public List<RespVisitor> getVisitorList(int userid, String visitDate) {
        // TODO Auto-generated method stub
        return visitorDao.getVisitorList(userid, visitDate);
    }

    @Override
    public String sendWeixin(UserInfo userinfo, Employee emp, Visitor vt) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Map<String, String>> maps = new HashMap<String, Map<String, String>>();
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] str = new String[]{"first", "keyword1", "keyword2", "keyword3", "keyword4", "remark"};

        for (int i = 0; i < str.length; i++) {
            if (i == 0) {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("value", "您有一个访客到访。");
                map1.put("color", "#173177");
                maps.put(str[i], map1);
            } else if (i == 1) {
                Map<String, String> map2 = new HashMap<String, String>();
                map2.put("value", vt.getVname());
                map2.put("color", "#173177");
                maps.put(str[i], map2);
            } else if (i == 2) {
                Map<String, String> map3 = new HashMap<String, String>();
                map3.put("value", vt.getVisitType());
                map3.put("color", "#173177");
                maps.put(str[i], map3);
            } else if (i == 3) {
                Map<String, String> map4 = new HashMap<String, String>();
                map4.put("value", vt.getVphone());
                map4.put("color", "#173177");
                maps.put(str[i], map4);
            } else if (i == 4) {
                Map<String, String> map5 = new HashMap<String, String>();
                map5.put("value", time.format(vt.getVisitdate()));
                map5.put("color", "#173177");
                maps.put(str[i], map5);
            } else if (i == 5) {
                Map<String, String> map6 = new HashMap<String, String>();
                map6.put("value", "请接待。");
                map6.put("color", "#173177");
                maps.put(str[i], map6);
            }

        }

        String url = vt.getVphoto();
        String name = vt.getVname();
        try {
            if (null != url) {
                url = URLEncoder.encode(url, "utf-8");
            }
            name = URLEncoder.encode(name, "utf-8");
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        map.put("touser", emp.getOpenid());
//        map.put("url", Constant.FASTDFS_URL + "empClient/reply.html?type=v&picurl=" + url + "&openid=" + emp.getOpenid() + "&vid=" + vt.getVid() + "&name=" + name);
        map.put("template_id", Constant.WeiXin_Notify.get("VisitNotify"));
        map.put("topcolor", "#FF0000");
        map.put("data", maps);

        CacheManager cm = CacheManager.getInstance();
        if (null == cm.getToken("token") || "".equals(cm.getToken("token"))) {
            settoken(cm);
        }

        String result = HttpClientUtil.postJsonBody("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + cm.getToken("token"), 5000, map, "utf-8");
        checkresult(cm, result, map);

        return result;

    }

    @Override
    public String sendVerifyCodeByWeixin(UserInfo userinfo, Visitor vt, Person p) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Map<String, String>> maps = new HashMap<String, Map<String, String>>();
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String[] str = new String[]{"first", "keyword1", "keyword2", "keyword3", "remark"};
        StringBuffer sb = new StringBuffer();
        sb.append("468");
        sb.append("02");
        sb.append("25");
        sb.append("28965874589");
        sb.append("1234561234");
        sb.append(vt.getVid());
        String qrcode = sb.toString();
        String aes = "v" + DigestUtils.sha512Hex(qrcode + "csjm759").substring(8, 23);
        sb.insert(0, aes);

        String verifycode = sb.toString();
        for (int i = 0; i < str.length; i++) {
            if (i == 0) {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("value", "预约拜访确认通知");
                map1.put("color", "#173177");
                maps.put(str[i], map1);
            } else if (i == 1) {
                Map<String, String> map2 = new HashMap<String, String>();
                map2.put("value", vt.getEmpName());
                map2.put("color", "#173177");
                maps.put(str[i], map2);
            } else if (i == 2) {
                Map<String, String> map3 = new HashMap<String, String>();
                map3.put("value", time.format(vt.getAppointmentDate()));
                map3.put("color", "#173177");
                maps.put(str[i], map3);
            } else if (i == 3) {
                Map<String, String> map4 = new HashMap<String, String>();
                map4.put("value", userinfo.getCardText() + "的" + vt.getEmpName() + "已确认您的预约拜访申请");
                map4.put("color", "#173177");
                maps.put(str[i], map4);
            } else if (i == 4) {
                Map<String, String> map5 = new HashMap<String, String>();
                StringBuffer msg = new StringBuffer("请准时赴约，点击详情查看通行二维码！");
                map5.put("value", msg.toString());
                map5.put("color", "#173177");
                maps.put(str[i], map5);
            }

        }

        map.put("touser", p.getPopenid());
        map.put("url", Constant.FASTDFS_URL + "visitorQrcode.html?vid=" + AESUtil.encode("" + vt.getVid(), Constant.AES_KEY));
        map.put("template_id", Constant.WeiXin_Notify.get("AppointSuccess"));
        map.put("topcolor", "#FF0000");
        map.put("data", maps);
        CacheManager cm = CacheManager.getInstance();
        if (null == cm.getToken("token") || "".equals(cm.getToken("token"))) {
            settoken(cm);
        }

        String result = HttpClientUtil.postJsonBodyOther("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + cm.getToken("token"), 5000, map, "utf-8");
        checkresult(cm, result, map);

        return result;

    }

    @Override
    public String sendRefuseVisitByWeixin(ProcessRecord pr, Visitor vt, Person p) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Map<String, String>> maps = new HashMap<String, Map<String, String>>();
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String[] str = new String[]{"first", "keyword1", "keyword2", "remark"};
        for (int i = 0; i < str.length; i++) {
            if (i == 0) {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("value", "非常抱歉，您预约" + vt.getEmpName() + "的拜访已被取消，请联系确认。");
                map1.put("color", "#173177");
                maps.put(str[i], map1);
            } else if (i == 1) {
                Map<String, String> map2 = new HashMap<String, String>();
                map2.put("value", vt.getVisitType());
                map2.put("color", "#173177");
                maps.put(str[i], map2);
            } else if (i == 2) {
                Map<String, String> map3 = new HashMap<String, String>();
                map3.put("value", pr.getRemark());
                map3.put("color", "#173177");
                maps.put(str[i], map3);
            } else if (i == 3) {
                Map<String, String> map4 = new HashMap<String, String>();
                map4.put("value", "");
                map4.put("color", "#173177");
                maps.put(str[i], map4);
            }
        }

        map.put("touser", p.getPopenid());

        map.put("url", "");
        map.put("template_id", Constant.WeiXin_Notify.get("AppointFefuse"));
        map.put("topcolor", "#FF0000");
        map.put("data", maps);


        CacheManager cm = CacheManager.getInstance();
        if (null == cm.getToken("token") || "".equals(cm.getToken("token"))) {
            settoken(cm);
        }

        String result = HttpClientUtil.postJsonBodyOther("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + cm.getToken("token"), 5000, map, "utf-8");
        checkresult(cm, result, map);
        return result;

    }

    @Override
    public String sendAppoinmentNotifyByWeixin(UserInfo userinfo, Visitor vt, Employee emp, Person visitor, int proxy) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Map<String, String>> maps = new HashMap<String, Map<String, String>>();
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] str = new String[]{"first", "keyword1", "keyword2", "keyword3", "keyword4", "keyword5", "remark"};

        for (int i = 0; i < str.length; i++) {
            if (i == 0) {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("value", "您有一个拜访申请需要授权");
                map1.put("color", "#173177");
                maps.put(str[i], map1);
            } else if (i == 1) {
                Map<String, String> map2 = new HashMap<String, String>();
                StringBuffer sb = new StringBuffer();
                if (null != visitor && null != visitor.getPcompany() && !"".equals(visitor.getPcompany())) {
                    sb.append(visitor.getPcompany() + "-");
                }
                if (null != visitor && null != visitor.getPposition() && !"".equals(visitor.getPposition())) {
                    sb.append(visitor.getPposition() + "-");
                }
                if ("商务".equals(vt.getVisitType())) {
                    map2.put("value", sb.append("需要访问贵公司洽谈业务").toString());
                } else if ("面试".equals(vt.getVisitType())) {
                    map2.put("value", sb.append("需要访问贵公司参加面试").toString());
                } else {
                    map2.put("value", vt.getVisitType());
                }
                map2.put("color", "#173177");
                maps.put(str[i], map2);
            } else if (i == 2) {
                Map<String, String> map3 = new HashMap<String, String>();
                map3.put("value", vt.getEmpName());
                map3.put("color", "#173177");
                maps.put(str[i], map3);
            } else if (i == 3) {
                Map<String, String> map4 = new HashMap<String, String>();
                map4.put("value", time.format(vt.getAppointmentDate()));
                map4.put("color", "#173177");
                maps.put(str[i], map4);
            } else if (i == 4) {
                Map<String, String> map5 = new HashMap<String, String>();
                map5.put("value", vt.getVname());
                map5.put("color", "#173177");
                maps.put(str[i], map5);
            } else if (i == 5) {
                Map<String, String> map6 = new HashMap<String, String>();
                map6.put("value", vt.getVphone());
                map6.put("color", "#173177");
                maps.put(str[i], map6);
            } else if (i == 6) {
                Map<String, String> map7 = new HashMap<String, String>();
                map7.put("value", "请点击详情进行访问授权！");
                map7.put("color", "#173177");
                maps.put(str[i], map7);
            }
        }

        map.put("touser", emp.getOpenid());
        if (proxy == 2) {
            map.put("url", Constant.FASTDFS_URL + "empClient/?path=myVisitor");
        } else {
            map.put("url", Constant.FASTDFS_URL + "empClient/?path=agent");
        }
        map.put("template_id", Constant.WeiXin_Notify.get("AppointmentNotify"));
        map.put("topcolor", "#FF0000");
        map.put("data", maps);

        CacheManager cm = CacheManager.getInstance();
        if (null == cm.getToken("token") || "".equals(cm.getToken("token"))) {
            settoken(cm);
        }

        String result = HttpClientUtil.postJsonBody("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + cm.getToken("token"), 5000, map, "utf-8");
        checkresult(cm, result, map);

        return result;

    }

    /**
     * 获取微信token
     * @param cm
     */
    public static void settoken(CacheManager cm) {
        ObjectMapper mapper = JacksonJsonUtil.getMapperInstance(false);
        Map<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "client_credential");
        params.put("appid", Constant.WeiXin_Notify.get("APPID"));
        params.put("secret", Constant.WeiXin_Notify.get("APP_SECRET"));
        String response = HttpClientUtil.invokeGet("https://api.weixin.qq.com/cgi-bin/token", params, "utf-8", 5000);
        try {
            JsonNode rootNode = mapper.readValue(response, JsonNode.class);
            if ("".equals(rootNode.path("errcode").asText())) {
                JsonNode token = rootNode.path("access_token");
                cm.putToken(CacheManager.TOKEN_WEIXIN, token.asText());
                System.out.println(cm.getToken("token"));
            }

        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 获取企业微信token
     * @param cm
     * @param userInfo
     */
    public static void settoken(CacheManager cm, UserInfo userInfo) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String corpid = userInfo.getCorpid();
        String corpsecret = userInfo.getCorpsecret();
        String access_token_url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=" + corpid + "&corpsecret=" + corpsecret;

        String jsonBody = HttpClientUtil.postJsonBodyOther(access_token_url, 1000, null, "UTF-8");
        try {
            ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
            JsonNode jsonNode = objectMapper.readValue(jsonBody, JsonNode.class);
            // 如果请求成功
            JsonNode errcode = jsonNode.get("errcode");
            if (null != errcode && "0".equals(errcode.asText())) {
                cm.putToken("WeChartToken_" + userInfo.getUserid(), jsonNode.get("access_token").asText());
            } else {
                System.out.println(dateFormat.format(new Date()) + " 获取accessToken失败: " + jsonBody);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void setDingTalkToken(CacheManager cm, UserInfo userInfo) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String appid = userInfo.getDdAppid();
        String appSccessSecret = userInfo.getDdAppSccessSecret();
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
            OapiGettokenRequest request = new OapiGettokenRequest();
            request.setAppkey(appid);
            request.setAppsecret(appSccessSecret);
            request.setHttpMethod("GET");
            OapiGettokenResponse response = client.execute(request);
            System.out.println(response.getBody());
            ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
            JsonNode jsonNode = objectMapper.readValue(response.getBody(), JsonNode.class);
            // 如果请求成功
            if (null != jsonNode.get("errcode") && "0".equals(jsonNode.get("errcode").asText())) {
                cm.putToken("dingTalkToken", response.getAccessToken());
            } else {
                System.out.println(dateFormat.format(new Date()) + " 获取accessToken失败: " + response.getBody());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void checkresult(CacheManager cm, UserInfo userInfo, String result, String url, Map<String, Object> map) {
        ObjectMapper mapper = JacksonJsonUtil.getMapperInstance(false);
        try {
            JsonNode rootNode = mapper.readValue(result, JsonNode.class);
            if (!"".equals(rootNode.path("errcode").asText())) {
                String errcode = rootNode.path("errcode").asText();
                if ("40001".equals(errcode) || "42001".equals(errcode) || "42002".equals(errcode) || "40014".equals(errcode)) {
                    settoken(cm, userInfo);
                    url = url.replace("ACCESS_TOKEN", cm.getToken("WeChartToken_" + userInfo.getUserid()));
                    result = HttpClientUtil.postJsonBodyOther(url, 5000, map, "utf-8");
                }
            }
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public List<RespVisitor> searchVisitByCondition(Map<String, Object> conditions) {
        // TODO Auto-generated method stub
        return visitorDao.searchVisitByCondition(conditions);
    }

    @Override
    public String sendTextNotifyByWXBus(Employee emp, String msg) {
        // TODO Auto-generated method stub
        // TODO Auto-generated method stub
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("openid", emp.getOpenid());
//        map.put("text", msg);
//        map.put("url", "");
//
//        return HttpClientUtil.postJsonBody("http://127.0.0.1:80/cpwechat/php/SendWechat.php", 5000, map, "utf-8");


        // TODO Auto-generated method stub
        ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        UserInfo userInfo = userDao.getUserInfo(emp.getUserid());

        Map<String, Object> reqParam = new HashMap<>();
        reqParam.put("touser", emp.getOpenid());
        reqParam.put("agentid", userInfo.getAgentid());
        reqParam.put("msgtype", "markdown");
        Map<String, Object> markdown = new HashMap<>();
        StringBuffer sb = new StringBuffer();
        sb.append("> 审批完毕提醒:  ").append("\r\n")
                .append("> 事　项：<font color=\"info\">审批完毕</font>  ").append("\r\n")
                .append("> 描述信息：" + msg + "  ").append("\r\n");
        markdown.put("content", sb.toString());
        reqParam.put("markdown", markdown);

        CacheManager cm = CacheManager.getInstance();
        if (null == cm.getToken("WeChartToken_" + userInfo.getUserid()) || "null".equals(cm.getToken("WeChartToken_" + userInfo.getUserid()))) {
            settoken(cm, userInfo);
        }
        String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=ACCESS_TOKEN";
        try {
            String reqUrl = url.replace("ACCESS_TOKEN", cm.getToken("WeChartToken_" + userInfo.getUserid()));
            String response = HttpClientUtil.postJsonBodyOther(reqUrl, 50000, reqParam, "UTF-8");
            System.out.println(response);
            JsonNode jsonNode = mapperInstance.readValue(response, JsonNode.class);
            String errcode = jsonNode.get("errcode").asText();
            if (null != jsonNode && "0".equals(errcode)) {
                System.out.println(dateFormat.format(new Date()) + " " + jsonNode.get("errmsg").asText());
                return "0";
            } else if ("40001".equals(errcode) || "42001".equals(errcode) || "42002".equals(errcode) || "40014".equals(errcode)) {
                checkresult(cm, userInfo, response, url, reqParam);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "-1";
    }

    @Override
    public String sendTextNotifyByFeiShu(Employee emp, String msg) {
        ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
        UserInfo userInfo = userDao.getUserInfo(emp.getUserid());

        Map<String, Object> reqParam = new HashMap<String, Object>();
        Map<String, String> map = new HashMap<String, String>();
        reqParam.put("receive_id", emp.getOpenid());
        StringBuffer sb = new StringBuffer();
        sb.append("审批完毕提醒:  ").append("描述信息：" + msg + "  ");
        map.put("text", sb.toString());
        reqParam.put("content", JsonUtil.stringify(map));
        reqParam.put("msg_type", "text");

        CacheManager cm = CacheManager.getInstance();
        if (null == cm.getToken("FsToken_" + userInfo.getUserid()) || "null".equals(cm.getToken("FsToken_" + userInfo.getUserid()))) {
            setFsToken(cm, userInfo);
        }
        String url = "https://open.feishu.cn/open-apis/im/v1/messages?receive_id_type=open_id";
        try {
            String response = HttpClientUtil.postJsonBodyForFs(url, 3000, reqParam, "utf-8", cm.getToken("FsToken_" + userInfo.getUserid()));
            JsonNode jsonNode = mapperInstance.readValue(response, JsonNode.class);
            String errcode = jsonNode.get("code").asText();
            if (null != jsonNode && "0".equals(errcode)) {
                return "0";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "-1";
    }

    @Override
    public String sendMarkdownNotifyByWXBus(Employee emp, Visitor vt) {
        ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
        UserInfo userInfo = userDao.getUserInfo(emp.getUserid());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Map<String, Object> reqParam = new HashMap<>();
        reqParam.put("touser", emp.getOpenid());
        reqParam.put("agentid", userInfo.getAgentid());
        reqParam.put("msgtype", "markdown");
        Map<String, Object> markdown = new HashMap<>();
        StringBuffer sb = new StringBuffer();
//        String msgUrl = "";
        String url = vt.getVphoto();
        String name = vt.getVname();
        try {
            if (null != url) {
                url = URLEncoder.encode(url, "utf-8");
            }
            name = URLEncoder.encode(name, "utf-8");
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
//        if (vt.getSigninType() == 1) {
//            msgUrl = Constant.FASTDFS_URL + "empClient/reply.html?type=a&picurl=" + url + "&openid=" + emp.getOpenid() + "&vid=" + vt.getVid() + "&name=" + name;
//        } else {
//            msgUrl = Constant.FASTDFS_URL + "empClient/reply.html?type=v&picurl=" + url + "&openid=" + emp.getOpenid() + "&vid=" + vt.getVid() + "&name=" + name;
//        }

        sb.append("> 访客到访提醒:  ").append("\r\n")
                .append("> 事　项：<font color=\"info\">访客到访</font>  ").append("\r\n")
                .append("> 描述信息：" + vt.getVname() + "已在公司前台完成来访登记，请准备接待。  ").append("\r\n");
//                .append("> 查看详情，请点击：[查看审批详情](" + msgUrl + ")  ");
        markdown.put("content", sb.toString());
        reqParam.put("markdown", markdown);

        CacheManager cm = CacheManager.getInstance();
        if (null == cm.getToken("WeChartToken_" + userInfo.getUserid()) || "null".equals(cm.getToken("WeChartToken_" + userInfo.getUserid()))) {
            settoken(cm, userInfo);
        }
        try {
            String reqUrl = url.replace("ACCESS_TOKEN", cm.getToken("WeChartToken_" + userInfo.getUserid()));
            String response = HttpClientUtil.postJsonBodyOther(reqUrl, 50000, reqParam, "UTF-8");
            System.out.println(response);
            JsonNode jsonNode = mapperInstance.readValue(response, JsonNode.class);
            String errcode = jsonNode.get("errcode").asText();
            if (null != jsonNode && "0".equals(errcode)) {
                System.out.println(dateFormat.format(new Date()) + " " + jsonNode.get("errmsg").asText());
                return "0";
            } else if ("40001".equals(errcode) || "42001".equals(errcode) || "42002".equals(errcode) || "40014".equals(errcode)) {
                checkresult(cm, userInfo, response, url, reqParam);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "-1";
    }

    @Override
    public String sendAppReplyByDD(UserInfo userinfo, Employee emp, Visitor vt, int type) {
        // TODO Auto-generated method stub
        Map<String, Object> params = new HashMap<String, Object>();
        SimpleDateFormat date = new SimpleDateFormat("MM月dd日 HH时mm分");
        if (null != emp.getDdid() && !"".equals(emp.getDdid())) {
            params.put("userid_list", emp.getDdid());
        } else if (null != emp.getOpenid() && !"".equals(emp.getOpenid())) {
            params.put("userid_list", emp.getOpenid());
        } else {
            return "";
        }
        params.put("agent_id", userinfo.getDdagentid());
        params.put("to_all_user", false);

        Map<String, Object> markdown = new HashMap<String, Object>();
        markdown.put("title", "邀请回执提醒");
        String content = "";
        if (type == 3) {
            content = emp.getEmpName() + "您好，" + vt.getVname() + "接受您的拜访邀请，请在" + date.format(vt.getAppointmentDate()) + "准时接待。";
        } else if (type == 4) {
            content = emp.getEmpName() + "您好，" + vt.getVname() + "暂时无法接受您的邀请，请及时沟通。";
        }
        content = "# 事　项：邀请回执" + "  \n\n  " + "访客姓名：" + vt.getVname() + "  \n\n  " + "描述信息：" + content + "  \n\n  ";
        markdown.put("text", content);

        Map<String, Object> msg = new HashMap<String, Object>();
        msg.put("msgtype", "markdown");
        msg.put("markdown", markdown);

        params.put("msg", msg);

        String access_token = UtilTools.getDDAccToken(userinfo.getUserid(), userinfo.getDdAppid(), userinfo.getDdAppSccessSecret());
        String response = HttpClientUtil.postJsonBodyOther(Constant.DDNOTIFY_URL + "?access_token=" + access_token, 5000, params, "UTF-8");

        return response;
    }


    @Override
    public String sendAppReplyByFeiShu(UserInfo userInfo, Employee emp, Visitor vt, int type) {
        // TODO Auto-generated method stub
        ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
        Map<String, Object> params = new HashMap<String, Object>();
        SimpleDateFormat date = new SimpleDateFormat("MM月dd日 HH时mm分");
        if (null != emp.getOpenid() && !"".equals(emp.getOpenid())) {
            params.put("receive_id", emp.getOpenid());
        } else {
            return "";
        }
        String content = "";
        if (type == 3) {
            content = emp.getEmpName() + "您好，" + vt.getVname() + "接受您的拜访邀请，请在" + date.format(vt.getAppointmentDate()) + "准时接待。";
        } else if (type == 4) {
            content = emp.getEmpName() + "您好，" + vt.getVname() + "暂时无法接受您的邀请，请及时沟通。";
        }
//        content= "邀请回执"+"  \r\n  "+"访客姓名：" + vt.getVname()+"  \r\n  "+"描述信息：" + content;

        Map<String, String> map = new HashMap<String, String>();
        map.put("text", content);
        params.put("content", JsonUtil.stringify(map));
        params.put("msg_type", "text");

        CacheManager cm = CacheManager.getInstance();
        if (null == cm.getToken("FsToken_" + userInfo.getUserid()) || "null".equals(cm.getToken("FsToken_" + userInfo.getUserid()))) {
            setFsToken(cm, userInfo);
        }
        String url = "https://open.feishu.cn/open-apis/im/v1/messages?receive_id_type=open_id";
        try {
            String response = HttpClientUtil.postJsonBodyForFs(url, 3000, params, "utf-8", cm.getToken("FsToken_" + userInfo.getUserid()));
            JsonNode jsonNode = mapperInstance.readValue(response, JsonNode.class);
            String errcode = jsonNode.get("code").asText();
            if (null != jsonNode && "0".equals(errcode)) {
                return "0";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "-1";

    }

    @Override
    public String privateSendWeiXin(String vname, String visitType,
                                    String vphone, String visitDate, String openid, String photo) {
        // TODO Auto-generated method stub
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Map<String, String>> maps = new HashMap<String, Map<String, String>>();
        String[] str = new String[]{"first", "keyword1", "keyword2", "keyword3", "keyword4", "remark"};

        for (int i = 0; i < str.length; i++) {
            if (i == 0) {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("value", "您有一个访客到访。");
                map1.put("color", "#173177");
                maps.put(str[i], map1);
            } else if (i == 1) {
                Map<String, String> map2 = new HashMap<String, String>();
                map2.put("value", vname);
                map2.put("color", "#173177");
                maps.put(str[i], map2);
            } else if (i == 2) {
                Map<String, String> map3 = new HashMap<String, String>();
                map3.put("value", visitType);
                map3.put("color", "#173177");
                maps.put(str[i], map3);
            } else if (i == 3) {
                Map<String, String> map4 = new HashMap<String, String>();
                map4.put("value", vphone);
                map4.put("color", "#173177");
                maps.put(str[i], map4);
            } else if (i == 4) {
                Map<String, String> map5 = new HashMap<String, String>();
                map5.put("value", visitDate);
                map5.put("color", "#173177");
                maps.put(str[i], map5);
            } else if (i == 5) {
                Map<String, String> map6 = new HashMap<String, String>();
                map6.put("value", "请接待。");
                map6.put("color", "#173177");
                maps.put(str[i], map6);
            }

        }

        map.put("touser", openid);
        map.put("url", photo);
        map.put("template_id", Constant.WeiXin_Notify.get("VisitNotify"));
        map.put("topcolor", "#FF0000");
        map.put("data", maps);

        CacheManager cm = CacheManager.getInstance();
        if (null == cm.getToken("token") || "".equals(cm.getToken("token"))) {
            settoken(cm);
        }

        String result = HttpClientUtil.postJsonBody("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + cm.getToken("token"), 5000, map, "utf-8");
        checkresult(cm, result, map);

        return result;

    }

    @Override
    public String sendSMS(UserInfo userinfo, Employee emp, Visitor vt) {
        // TODO Auto-generated method stub
        Map<String, String> params = new HashMap<String, String>();
        params.put("message", vt.getVname() + "已在公司前台完成登记，请准备接待。 ");
        params.put("phone", emp.getEmpPhone());
        String response = UtilTools.sendSmsByYiMei(params, configureDao, userinfo);

        if ("0".equals(response)) {
            userinfo.setSmsCount(userinfo.getSmsCount() + 1);
            userDao.updateSmsCount(userinfo);
        }
        OperateLog.addSMSLog(operateLogService,userinfo.getUserid(), response, 1,emp.getEmpPhone(),emp.getEmpName(),params.get("message"));
        return response;
    }

    @Override
    public int getVisitorCount(int userid) {
        // TODO Auto-generated method stub
        return visitorDao.getVisitorCount(userid);
    }

    @Override
    public List<RespVisitor> getVisitorListByEmpid(int empid) {
        // TODO Auto-generated method stub
        return visitorDao.getVisitorListByEmpid(empid);
    }

    @Override
    public Visitor getVisitorById(int vid) {
        // TODO Auto-generated method stub
        return visitorDao.getVisitorById(vid);
    }

    @Override
    public int updateVisitorSms(Visitor vt) {
        // TODO Auto-generated method stub
        return visitorDao.updateVisitorSms(vt);
    }

    @Override
    public boolean sendAppointmentMail(UserInfo userinfo, Employee emp,
                                       Visitor vt) throws JsonParseException, JsonMappingException, IOException {
        // TODO Auto-generated method stub
        List<ExtendVisitor> evlist = new ArrayList<ExtendVisitor>();
        if (userinfo.getEmailType() == 1) {
            SendHtmlEmail she = new SendHtmlEmail();
            return she.send(userinfo, emp, vt, evlist);
        } else if (userinfo.getEmailType() == 2) {
            SendAppointmentEmail see = new SendAppointmentEmail();
            return see.send(userinfo, emp, vt);
        } else {
            SendHtmlEmail she = new SendHtmlEmail();
            return she.send(userinfo, emp, vt, evlist);
        }
    }

    /**
     * 发送邀请短信
     * @param userinfo
     * @param emp 邀请人
     * @param vt
     * @return
     */
    @Override
    public String sendAppointmentSMS(UserInfo userinfo, Employee emp, Visitor vt) {
        // TODO Auto-generated method stub
        String url = "";
        String tempid = "";
        Map<String, String> params = new HashMap<String, String>();
        if ("面试".equals(vt.getVisitType())) {
//			url=UtilTools.shortUrl(Constant.FASTDFS_URL+"qcvisit/show?id="+AESUtil.encode(vt.getVid()+"", Constant.AES_KEY));
            url = Constant.FASTDFS_URL + "show.html?id=" + AESUtil.encode(vt.getVid() + "", Constant.AES_KEY);
            tempid = "SMS_52475005";
            if (userinfo.getSubAccount() == 1 && emp.getSubaccountId() != 0) {
                SubAccount sa = subAccountDao.getSubAccountById(emp.getSubaccountId());
                String companyName = sa.getCompanyName();
                if (sa.getCompanyName().indexOf("#") != 1) {
                    String name[] = sa.getCompanyName().split("#");
                    companyName = name[0];
                }
                params.put("message", "您好" + vt.getVname() + "！" + companyName + emp.getEmpName() + "诚邀您前来面谈，详情请点击邀请函： " + url);
            } else {
                params.put("message", "您好" + vt.getVname() + "！" + userinfo.getCardText() + emp.getEmpName() + "诚邀您前来面谈，详情请点击邀请函： " + url);
            }
        } else if ("商务".equals(vt.getVisitType())) {
//			url=UtilTools.shortUrl(Constant.FASTDFS_URL+"qcvisit/bus?id="+AESUtil.encode(vt.getVid()+"", Constant.AES_KEY));
            url = Constant.FASTDFS_URL + "bus.html?id=" + AESUtil.encode(vt.getVid() + "", Constant.AES_KEY);
            tempid = "SMS_52485034";
            if (userinfo.getSubAccount() == 1 && emp.getSubaccountId() != 0) {
                SubAccount sa = subAccountDao.getSubAccountById(emp.getSubaccountId());
                String companyName = sa.getCompanyName();
                if (sa.getCompanyName().indexOf("#") != 1) {
                    String name[] = sa.getCompanyName().split("#");
                    companyName = name[0];
                }
                params.put("message", "您好" + vt.getVname() + "！" + companyName + emp.getEmpName() + "诚邀您来访，详情请点击邀请函： " + url);
            } else {
                params.put("message", "您好" + vt.getVname() + "！" + userinfo.getCardText() + emp.getEmpName() + "诚邀您来访，详情请点击邀请函： " + url);
            }
        } else if ("会议".equals(vt.getVisitType()) && vt.getMid() != 0) {
//			url=UtilTools.shortUrl(Constant.FASTDFS_URL+"qcvisit/bus?id="+AESUtil.encode(vt.getVid()+"", Constant.AES_KEY));
            url = Constant.FASTDFS_URL + "bus.html?id=" + AESUtil.encode(vt.getVid() + "", Constant.AES_KEY);
            Meeting m = meetingDao.getMeetingById(vt.getMid());
            tempid = "SMS_52405001";
            if (userinfo.getSubAccount() == 1 && emp.getSubaccountId() != 0) {
                SubAccount sa = subAccountDao.getSubAccountById(emp.getSubaccountId());
                String companyName = sa.getCompanyName();
                if (sa.getCompanyName().indexOf("#") != 1) {
                    String name[] = sa.getCompanyName().split("#");
                    companyName = name[0];
                }
                params.put("message", "您好" + vt.getVname() + "！" + companyName + "诚邀您参加" + m.getName() + "，详情请点击邀请函： " + url);
            } else {
                params.put("message", "您好" + vt.getVname() + "！" + userinfo.getCardText() + "诚邀您参加" + m.getName() + "，详情请点击邀请函： " + url);
            }

        } else {
            url = Constant.FASTDFS_URL + "bus.html?id=" + AESUtil.encode(vt.getVid() + "", Constant.AES_KEY);
            if (userinfo.getSubAccount() == 1 && emp.getSubaccountId() != 0) {
                SubAccount sa = subAccountDao.getSubAccountById(emp.getSubaccountId());
                String companyName = sa.getCompanyName();
                if (sa.getCompanyName().indexOf("#") != 1) {
                    String name[] = sa.getCompanyName().split("#");
                    companyName = name[0];
                }
                params.put("message", "您好" + vt.getVname() + "！" + companyName + emp.getEmpName() + "诚邀您来访，详情请点击邀请函： " + url);
            } else {
                params.put("message", "您好" + vt.getVname() + "！" + userinfo.getCardText() + emp.getEmpName() + "诚邀您来访，详情请点击邀请函：  " + url);
            }
        }

        params.put("phone", vt.getVphone());
        String response = UtilTools.sendSmsByYiMei(params, configureDao, userinfo);

        if ("0".equals(response)) {
            userinfo.setAppSmsCount(userinfo.getAppSmsCount() + 2);
            userDao.updateAppSmsCount(userinfo);
        }
        OperateLog.addSMSLog(operateLogService,userinfo.getUserid(), response, 2,vt.getVphone(),vt.getVname(),params.get("message"));
        return response;
    }




    @Override
    public String sendAppointmentMeetingSMS(UserInfo userinfo,Visitor vt,Integer type) {
        String url = "";
        Map<String, String> params = new HashMap<String, String>();
        url = Constant.FASTDFS_URL + "bus.html?id=" + AESUtil.encode(vt.getVid() + "", Constant.AES_KEY);
        if (1 == type){
            if (userinfo.getSubAccount() == 1) {
                params.put("message", "您好" + vt.getVname() + "！" + userinfo.getCardText() + "诚邀您来访，详情请点击邀请函： " + url);
            } else {
                params.put("message", "您好" + vt.getVname() + "！" + userinfo.getCardText() + "诚邀您来访，详情请点击邀请函：  " + url);
            }
        }else if (2 == type){
            params.put("message", "您好" + vt.getVname() + "！" + userinfo.getCardText() + "已拒绝您预约的"+vt.getRemark()+"会议，请知悉。");
        }

        params.put("phone", vt.getVphone());
        String response = UtilTools.sendSmsByYiMei(params, configureDao, userinfo);
        if ("0".equals(response)) {
            userinfo.setAppSmsCount(userinfo.getAppSmsCount() + 2);
            userDao.updateAppSmsCount(userinfo);
        }
        OperateLog.addSMSLog(operateLogService,userinfo.getUserid(), response, 2,vt.getVphone(),vt.getVname(),params.get("message"));
        return response;
    }

    @Override
    public String sendIvrSMS(UserInfo userinfo, Employee emp, Visitor vt) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("phone", vt.getVphone());
        params.put("message", "尊敬的来宾您好，十分抱歉，" + emp.getEmpName() + "现在不在公司，无法接待，请您提前预约。感谢您此次来访！");

        String response = UtilTools.sendSmsByYiMei(params, configureDao, userinfo);

        if ("0".equals(response)) {
            userinfo.setAppSmsCount(userinfo.getAppSmsCount() + 1);
            userDao.updateAppSmsCount(userinfo);
        }

        OperateLog.addSMSLog(operateLogService,userinfo.getUserid(), response, 1,vt.getVphone(),vt.getVname(),params.get("message"));
        return response;
    }

    @Override
    public int batchSingin(List<Visitor> vlist) {
        // TODO Auto-generated method stub
        return visitorDao.batchSingin(vlist);
    }

    @ProcessLogger("授权")
    @Override
    public int updatePermission(Visitor vt) {
        int ret = visitorDao.updatePermission(vt);
        if(ret == 1){
            onUpdatePermission(vt);
        }

        return ret;
    }

    /**
     * 授权后的事件处理
     * @param vt
     */
    protected void onUpdatePermission(Visitor vt) {
        if( vt.getPermission() == 1){
            //发送通知
           int supplementType = supplementTypeRouter(vt);
           if(supplementType ==2){
               //必填的情况，说明是先发邀请函在授权/审批,邀请函已发过，通知授权成功
               messageService.sendCommonNotifyEvent(vt, NotifyEvent.EVENTTYPE_ACCEPT_APPOINTMENT);
           }else{
               //非必填的情况是先审批再发邀请函
               messageService.sendCommonNotifyEvent(vt,NotifyEvent.EVENTTYPE_SEND_INVITE);
           }
           if( Constant.AccessWithoutSignin.equals("1")) {
               //允许未签到开门，授权后直接下发通行权限
               List<Visitor> list = new ArrayList<Visitor>();
               list.add(vt);
               passService.passAuth(list, PassEvent.Pass_Add);
           }
       }else if(vt.getPermission() == 2){
           messageService.sendCommonNotifyEvent(vt, NotifyEvent.EVENTTYPE_REJECT_APPOINTMENT);
       }else if(vt.getPermission() == 3){
            messageService.sendCommonNotifyEvent(vt,NotifyEvent.EVENTTYPE_VISIT_CANCEL);
        }
    }

    @Override
    public int addVisitorApponintmnet(Visitor vt) {

        int ret = visitorDao.addVisitorApponintmnet(vt);
        if(ret != 1) {
            return ret;
        }
        VisitEvent event = new VisitEvent();
        event.setEventType(VisitEvent.EVENTTYPE_ADD);
        event.setVisitor(vt);
        applicationEventPublisher.publishEvent(event);
        return ret;
    }

    @Override
    public List<RespVisitor> getVisitorAppointmentByPhone(Map<String, Object> conditions) {
        // TODO Auto-generated method stub
        return visitorDao.getVisitorAppointmentByPhone(conditions);
    }

    @Override
    public List<RespVisitor> getVisitorAppointmentList(Visitor vt) {
        // TODO Auto-generated method stub
        return visitorDao.getVisitorAppointmentList(vt);
    }

    @Override
    public String sendInviteSMS(UserInfo userinfo, Visitor vt, Employee emp) {
        // TODO Auto-generated method stub
        String vid = AESUtil.encode(vt.getVid() + "", Constant.AES_KEY);
        SimpleDateFormat time = new SimpleDateFormat("MM月dd日");

        Map<String, String> params = new HashMap<String, String>();
        int smsCount = 0;
        if (userinfo.getPermissionSwitch() == 1 && vt.getPermission() == 0) {
            smsCount = 2;
            params.put("message", "您好，" + vt.getVname() + "预约在" + time.format(vt.getAppointmentDate()) + "拜访你，点击链接授权：" + Constant.FASTDFS_URL + "e.html?v=" + vid);
        } else {
            params.put("message", "您好，" + vt.getVname() + "预约在" + time.format(vt.getAppointmentDate()) + "拜访你，请准时接待。");
            smsCount = 1;
        }
        params.put("phone", emp.getEmpPhone());
        String response = UtilTools.sendSmsByYiMei(params, configureDao, userinfo);

        if ("0".equals(response)) {
            userinfo.setAppSmsCount(userinfo.getAppSmsCount() + smsCount);
            userDao.updateAppSmsCount(userinfo);
        }

        //日志
        OperateLog.addSMSLog(operateLogService,userinfo.getUserid(), response, smsCount,emp.getEmpPhone(),emp.getEmpName(),params.get("message"));

        return response;
    }


    /**
     * 3.9版本后改为发送邀请函连接
     */
    @Override
    public String sendInviteReplySMS(UserInfo userinfo, Visitor vt,
                                     String empName, String type) {
        Map<String, String> params = new HashMap<String, String>();
        int smsCount=2;
        if (type.equals("p")) {
           String url ="bus.html?id=" + AESUtil.encode("v"+vt.getVid() + "", Constant.AES_KEY);

            Employee emp = employeeDao.getEmployee(vt.getEmpid());
            String msgLink = "请点击以下链接查看访客邀请函: ";
            if (userinfo.getSubAccount() == 1 && emp.getSubaccountId() != 0) {
                SubAccount sa = subAccountDao.getSubAccountById(emp.getSubaccountId());
                params.put("message", vt.getVname() + "您好，" + sa.getCompanyName() + "的" + empName + "接受您的拜访预约，"
                        + msgLink + Constant.FASTDFS_URL + url);
//                params.put("message", vt.getVname() + "您好，" + sa.getCompanyName() + "的" + empName + "接受您的拜访预约。");
            } else {
                params.put("message", vt.getVname() + "您好，" + userinfo.getCardText() + "的" + empName + "接受您的拜访预约，"
                        + msgLink + Constant.FASTDFS_URL  + url);
//                params.put("message", vt.getVname() + "您好，" + userinfo.getCardText() + "的" + empName + "接受您的拜访预约。");
            }
        } else {
            params.put("message", vt.getVname() + "您好，" + empName + "暂时无法接受您的预约拜访申请，请及时沟通。");
            smsCount=1;
        }
        params.put("phone", vt.getVphone());
        String response = UtilTools.sendSmsByYiMei(params, configureDao, userinfo);

        if ("0".equals(response)) {
            userinfo.setAppSmsCount(userinfo.getAppSmsCount() + smsCount);
            userDao.updateAppSmsCount(userinfo);
        }

        OperateLog.addSMSLog(operateLogService,userinfo.getUserid(), response, smsCount,vt.getVphone(),vt.getVname(),params.get("message"));
        return response;
    }

    @Override
    public int addApponintmnetVisitor(Visitor vt) {
        // TODO Auto-generated method stub
        return visitorDao.addApponintmnetVisitor(vt);
    }

    @Override
    public int updateVisitorAppointment(Visitor vt) {
        // TODO Auto-generated method stub
        return visitorDao.updateVisitorAppointment(vt);
    }

    @Override
    public int updateSignOut(Visitor vt) {
        //解除权限
        List<RespVisitor> signOutList = checkSignOutRecords(vt);
        List<Visitor> visitorList = new ArrayList<Visitor>();
        for(RespVisitor rv:signOutList){
            visitorList.add(BeanUtils.RespVisitToVisitor(rv));
        }
        passService.passAuth(visitorList, PassEvent.Pass_Del);

        return visitorDao.updateSignOut(vt);
    }

    @Override
    public List<RespVisitor> checkSignOutRecords(Visitor vt) {
        // TODO Auto-generated method stub
        return visitorDao.checkSignOutRecords(vt);
    }

    @Override
    public List<RespVisitor> getVistorAppListByEmpPhone(
            RespVisitor vt) {
        // TODO Auto-generated method stub
        return visitorDao.getVistorAppListByEmpPhone(vt);
    }

    @Override
    public List<RespVisitor> getTempList(Map<String, String> conditions) {
        // TODO Auto-generated method stub
        return visitorDao.getTempList(conditions);
    }

    @Override
    public List<RespVisitor> getAppointmentListByEmpPhone(
            RespVisitor vt) {
        // TODO Auto-generated method stub
        return visitorDao.getAppointmentListByEmpPhone(vt);
    }

    @Override
    public int updateSigninByAppClient(Visitor vt) {
        // TODO Auto-generated method stub
        return visitorDao.updateSigninByAppClient(vt);
    }

    @Override
    public List<RespVisitor> getAppointmentListByVPhone(RespVisitor vt) {
        // TODO Auto-generated method stub
        return visitorDao.getAppointmentListByVPhone(vt);
    }

    @Override
    public List<RespVisitor> searchVisitForApp(Map<String, String> conditions) {
        // TODO Auto-generated method stub
        return visitorDao.searchVisitForApp(conditions);
    }

    @Override
    public Visitor getTodayVisitorById(int vid) {
        // TODO Auto-generated method stub
        return visitorDao.getTodayVisitorById(vid);
    }

    @Override
    public String sendAppointmentReplySMS(UserInfo userinfo, Employee emp,
                                          Visitor vt, int type) {
        SimpleDateFormat date = new SimpleDateFormat("MM月dd日 HH时mm分");

        Map<String, String> params = new HashMap<String, String>();
        if (type == 3) {
            params.put("message", emp.getEmpName() + "您好，" + vt.getVname() + "接受您的邀请，请在" + date.format(vt.getAppointmentDate()) + "准时接待。");
        } else if (type == 4) {
            params.put("message", emp.getEmpName() + "您好，" + vt.getVname() + "暂时无法接受您的邀请，请及时沟通。");
        }

        params.put("phone", emp.getEmpPhone());
        String response = UtilTools.sendSmsByYiMei(params, configureDao, userinfo);

        if ("0".equals(response)) {
            userinfo.setAppSmsCount(userinfo.getAppSmsCount() + 1);
            userDao.updateAppSmsCount(userinfo);
        }

        OperateLog.addSMSLog(operateLogService,userinfo.getUserid(), response, 1,vt.getVphone(),vt.getVname(),params.get("message"));
        return response;
    }

    public static void checkresult(CacheManager cm, String result, Map<String, Object> map) {
        ObjectMapper mapper = JacksonJsonUtil.getMapperInstance(false);
        try {
            JsonNode rootNode = mapper.readValue(result, JsonNode.class);
            if (!"".equals(rootNode.path("errcode").asText())) {
                String errcode = rootNode.path("errcode").asText();
                if ("40001".equals(errcode) || "42001".equals(errcode) || "42002".equals(errcode) || "40014".equals(errcode)) {
                    settoken(cm);
                    result = HttpClientUtil.postJsonBody("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + cm.getToken("token"), 5000, map, "utf-8");
                }
            }
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public List<RespVisitor> getAppointmentListByEmpId(RespVisitor vt) {
        // TODO Auto-generated method stub
        return visitorDao.getAppointmentListByEmpId(vt);
    }

    @Override
    public List<RespVisitor> getVisitorAppointmentByVname(Map<String, Object> conditions) {
        // TODO Auto-generated method stub
        return visitorDao.getVisitorAppointmentByVname(conditions);
    }

    /**
     * permission p需要授权 n 不需要授权
     */
    @Override
    public String sendWeixinByBus(Visitor vt, String type, String permission) {
        // TODO Auto-generated method stub
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("mobile", vt.getEmpPhone());
        map.put("fktid", vt.getUserid());
        map.put("vid", vt.getVid());
        map.put("type", type);
        map.put("vname", vt.getVname());
        map.put("method", permission);

        return HttpClientUtil.postJsonBody(Constant.WECHAT_URL + "wechat/department/sendCorpMessage.php", 5000, map, "utf-8");
    }

    @Override
    public String sendVNet(UserInfo userinfo, Visitor vt) {
        // TODO Auto-generated method stub
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = time.format(vt.getAppointmentDate());
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("fromType", "1");
        map.put("receiverType", "02");
        map.put("receiverPerson", vt.getEmpPhone());
        map.put("serviceID", userinfo.getServiceID());
        map.put("securityID", userinfo.getSecurityID());
        map.put("securityKey", userinfo.getSecurityKey());
        map.put("messageType", "02");

        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("type", "mainTitle");
        if (null == vt.getVisitdate()) {
            map2.put("titleDesc", Encodes.encodeBase64(vt.getEmpName() + "您好，" + vt.getVname() + "预约在" + date + "拜访你，请点击授权访问。"));
        } else {
            map2.put("titleDesc", Encodes.encodeBase64("访客：" + vt.getVname() + "已到公司前台，请接待!"));
        }
        if (null == vt.getVphoto() || "".equals(vt.getVphoto())) {
            map2.put("titlePicUrl", Constant.WECHAT_URL + "wechat/img/defaultHead.png");
        } else {
            map2.put("titlePicUrl", vt.getVphoto());
        }
        map2.put("clickUrl", Constant.FASTDFS_URL + "vlist.html?FromUserTelNum=" + vt.getEmpPhone() + "&type=lft");
        map.put("messageContent", map2);

        return HttpClientUtil.postVnetJsonBody("http://112.4.17.117:10013/VIC_AUTH/pushMessage", 5000, map, "utf-8");
    }

    @Override
    public int updateVisitRemark(Visitor vt) {
        // TODO Auto-generated method stub
        return visitorDao.updateVisitRemark(vt);
    }

    @Override
    public int addGroupVisitor(List<Visitor> vlist) {
        // TODO Auto-generated method stub
        return visitorDao.addGroupVisitor(vlist);
    }

    @Override
    public int updateGroupPermission(Visitor vt) {
        int ret = visitorDao.updateGroupPermission(vt);
        if(ret == 1){
            List<Visitor> vlist = getGroupVistorList(vt.getVid());
            for(Visitor visitor:vlist){
                onUpdatePermission(visitor);
            }
        }
        return ret;
    }

    @Override
    public List<Visitor> getGroupVistorList(int vid) {
        // TODO Auto-generated method stub
        return visitorDao.getGroupVistorList(vid);
    }

    @Override
    public Visitor getTodayVisitorByPhone(String vphone, int userid) {
        // TODO Auto-generated method stub
        return visitorDao.getTodayVisitorByPhone(vphone, userid);
    }

    @Override
    public int updateSignOutByVid(Visitor vt) {
        //解除权限
        List<Visitor> visitorList = new ArrayList<Visitor>();
        visitorList.add(vt);
        passService.passAuth(visitorList, PassEvent.Pass_Del);

        return visitorDao.updateSignOutByVid(vt);
    }

    @Override
    public List<Visitor> getLastestVisitor(Map<String, Object> map) {
        // TODO Auto-generated method stub
        return visitorDao.getLastestVisitor(map);
    }

    @Override
    public List<RespVisitor> searchAppByCondition(Map<String, String> map) {
        // TODO Auto-generated method stub
        return visitorDao.searchAppByCondition(map);
    }

    @Override
    public int batchSignOut(List<Visitor> vlist) {
        //删除权限
        List<Visitor> deleteList = new ArrayList<>();
        for(Visitor v:vlist){
            Visitor del = visitorDao.getVisitorById(v.getVid());
            if (del != null){
                deleteList.add(del);
            }
        }
        passService.passAuth(deleteList, PassEvent.Pass_Del);

        return visitorDao.batchSignOut(vlist);
    }

    @Override
    public String sendVisitorKeyByWX(Visitor vt, Person p) {
        // TODO Auto-generated method stub
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Map<String, String>> maps = new HashMap<String, Map<String, String>>();
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] str = new String[]{"first", "keyword1", "keyword2", "remark"};

        for (int i = 0; i < str.length; i++) {
            if (i == 0) {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("value", vt.getVname() + "您好，您已签到成功");
                map1.put("color", "#173177");
                maps.put(str[i], map1);
            } else if (i == 1) {
                Map<String, String> map2 = new HashMap<String, String>();
                map2.put("value", time.format(vt.getVisitdate()));
                map2.put("color", "#173177");
                maps.put(str[i], map2);
            } else if (i == 2) {
                Map<String, String> map3 = new HashMap<String, String>();
                map3.put("value", vt.getCompany());
                map3.put("color", "#173177");
                maps.put(str[i], map3);
            } else if (i == 3) {
                Map<String, String> map4 = new HashMap<String, String>();
                map4.put("value", "点击详情，获取临时通行钥匙！");
                map4.put("color", "#173177");
                maps.put(str[i], map4);
            }

        }

        map.put("touser", p.getPopenid());
        map.put("url", "");
        map.put("template_id", Constant.WeiXin_Notify.get("SigninSuccess"));
        map.put("topcolor", "#FF0000");
        map.put("data", maps);

        CacheManager cm = CacheManager.getInstance();
        if (null == cm.getToken("token") || "".equals(cm.getToken("token"))) {
            settoken(cm);
        }

        String result = HttpClientUtil.postJsonBody("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + cm.getToken("token"), 5000, map, "utf-8");
        checkresult(cm, result, map);

        return result;
    }

    @Override
    public boolean sendVisitorKey(Person p, List<Equipment> elist, Visitor vt, int time) {
        List<WXOpenDoor> wlist = new ArrayList<WXOpenDoor>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.MINUTE, time);
        String expiretime = sdf.format(nowTime.getTime());
        if (null != p && null != p.getPopenid() && !"".equals(p.getPopenid())) {
            for (int s = 0; s < elist.size(); s++) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("puid", vt.getEmpPhone());
                map.put("pid", elist.get(s).getDeviceCode());
                map.put("expiretime", expiretime);
                map.put("mtype", "1");
                map.put("gid", "gh_2b79266bd71a");
                String response = UtilTools.miaodouWXService(map, "createMKeyService.aspx");
                ObjectMapper mapper = JacksonJsonUtil.getMapperInstance(false);
                JsonNode rootNode;
                try {
                    rootNode = mapper.readValue(response, JsonNode.class);

                    if (rootNode.path("code").asText().equals("0")) {
                        JsonNode result = rootNode.path("msg");
                        WXOpenDoor wxod = new WXOpenDoor();
                        wxod.setKsid(result.path("ksid").asText());
                        wxod.setDevice_id(result.path("device_id").asText());
                        wxod.setKey_secret(result.path("key_secret").asText());
                        wxod.setLock_name(elist.get(s).getDeviceName());
                        wlist.add(wxod);
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if (wlist.size() > 0) {
                redisTemplate.delete(p.getPopenid());
                ListOperations<String, WXOpenDoor> listOperations = redisTemplate.opsForList();
                listOperations.rightPushAll(p.getPopenid(), wlist.toArray(new WXOpenDoor[wlist.size()]));
                redisTemplate.expire(p.getPopenid(), time, TimeUnit.MINUTES);
                return true;
            }

            return false;
        }

        return false;
    }

    @Override
    public PushResult sendJpushNotify(Visitor vt, String content, String type) {
        // TODO Auto-generated method stub
        JPushClient jpushClient = new JPushClient(Constant.OD_MASTER_SECRET, Constant.OD_APP_KEY, 3);

        PushPayload payload = PushPayload.newBuilder().setPlatform(Platform.android_ios())
                .setAudience(Audience.alias(vt.getEmpPhone()))
                .setNotification(Notification.newBuilder()
                        .addPlatformNotification(IosNotification.newBuilder().setAlert(content).addExtra("type", type).build())
                        .addPlatformNotification(AndroidNotification.newBuilder().setAlert(content).addExtra("type", type).build()).build())
                .setOptions(Options.newBuilder()
                        .setApnsProduction(true)
                        .build())
                .build();

        try {
            PushResult result = jpushClient.sendPush(payload);
            logger.info("Got result - " + result);

            return result;
        } catch (APIConnectionException e) {
            logger.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            logger.error("Error response from JPush server. Should review and fix it. ", e);
            logger.info("HTTP Status: " + e.getStatus());
            logger.info("Error Code: " + e.getErrorCode());
            logger.info("Error Message: " + e.getErrorMessage());
            logger.info("Msg ID: " + e.getMsgId());
        }
        return null;
    }

    @Override
    public int updateVisitInfo(Visitor vt) {
        // TODO Auto-generated method stub
        return visitorDao.updateVisitInfo(vt);
    }

    @Override
    public Visitor getVisitorByCardID(String cardId, int userid) {
        // TODO Auto-generated method stub
        return visitorDao.getVisitorByCardID(cardId, userid);
    }

    @Override
    public List<RespVisitor> SearchRVisitorByCondition(Map<String, String> map) {
        // TODO Auto-generated method stub
        return visitorDao.SearchRVisitorByCondition(map);
    }

    @Override
    public Visitor getTodayAppointmentByPhone(Visitor vt) {
        // TODO Auto-generated method stub
        return visitorDao.getTodayAppointmentByPhone(vt);
    }

    @Override
    public String sendVoiceSMS(UserInfo userinfo, Employee emp, Visitor vt) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String sendCancelSMS(UserInfo userinfo, Employee emp, Visitor vt) {
        // TODO Auto-generated method stub
        SimpleDateFormat date = new SimpleDateFormat("MM月dd日 HH时mm分");
        Map<String, String> params = new HashMap<String, String>();
        params.put("message", "很抱歉的通知您，" + emp.getEmpName() + "取消了原定于" + date.format(vt.getAppointmentDate()) + "的会面，请知悉。");
        params.put("phone", vt.getVphone());
        String response = UtilTools.sendSmsByYiMei(params, configureDao, userinfo);

        if ("0".equals(response)) {
            userinfo.setAppSmsCount(userinfo.getAppSmsCount() + 1);
            userDao.updateAppSmsCount(userinfo);
        }

        OperateLog.addSMSLog(operateLogService,userinfo.getUserid(), response, 1,vt.getVphone(),vt.getVname(),params.get("message"));
        return response;
    }

    @Override
    public String sendSmsReply(Map<String, String> params, UserInfo userinfo,int count) {
        String response = UtilTools.sendSmsByYiMei(params, configureDao, userinfo);
        
        if ("0".equals(response)) {
            userinfo.setAppSmsCount(userinfo.getAppSmsCount() + count);
            userDao.updateAppSmsCount(userinfo);
        }
        
        return response;
    }

    @Override
    public String sendSmsCodeByYiMei(Map<String, String> params, UserInfo userinfo) {
        // TODO Auto-generated method stub
        String response = UtilTools.sendSmsCodeByYiMei(params, configureDao, userinfo);
        
        if ("0".equals(response)) {
            userinfo.setAppSmsCount(userinfo.getAppSmsCount() + 1);
            userDao.updateAppSmsCount(userinfo);
        }
        
        OperateLog.addSMSLog(operateLogService, userinfo.getUserid(), response, 1, params.get("phone"), "", params.get("message"));
        return response;
    }


    //    @Override
//    public String sendNotifyByWXBus(Employee emp, Visitor vt) {
//        // TODO Auto-generated method stub
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("openid", emp.getOpenid());
//        map.put("text", vt.getVname() + "已在公司前台完成来访登记，请准备接待。 ");
//
//        String url = vt.getVphoto();
//        String name = vt.getVname();
//        try {
//            if (null != url) {
//                url = URLEncoder.encode(url, "utf-8");
//            }
//            name = URLEncoder.encode(name, "utf-8");
//        } catch (UnsupportedEncodingException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
//
//        if (vt.getSigninType() == 1) {
//            map.put("url", Constant.FASTDFS_URL + "empClient/reply.html?type=a&picurl=" + url + "&openid=" + emp.getOpenid() + "&vid=" + vt.getVid() + "&name=" + name);
//        } else {
//            map.put("url", Constant.FASTDFS_URL + "empClient/reply.html?type=v&picurl=" + url + "&openid=" + emp.getOpenid() + "&vid=" + vt.getVid() + "&name=" + name);
//        }
//
//        return HttpClientUtil.postJsonBody(Constant.FASTDFS_URL + "cpwechat/php/SendWechat.php", 5000, map, "utf-8");
//    }
    @Override
    public String sendNotifyByWXBus(Employee emp, Visitor vt) {
        // TODO Auto-generated method stub
        ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        UserInfo userInfo = userDao.getUserInfo(vt.getUserid());

        Map<String, Object> reqParam = new HashMap<>();
        reqParam.put("touser", emp.getOpenid());
        reqParam.put("agentid", userInfo.getAgentid());
        reqParam.put("msgtype", "markdown");
        Map<String, Object> markdown = new HashMap<>();
        String msg = vt.getVname() + "已在公司前台完成来访登记，请准备接待。";
        StringBuffer sb = new StringBuffer();
//        String msgUrl = "";
//        String vphoto = vt.getVphoto();
//        String name = vt.getVname();
//        try {
//            if (null != vphoto) {
//                vphoto = URLEncoder.encode(vphoto, "utf-8");
//            }
//            name = URLEncoder.encode(name, "utf-8");
//        } catch (UnsupportedEncodingException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
//        if (vt.getSigninType() == 1) {
//            msgUrl = Constant.FASTDFS_URL + "empClient/reply.html?type=a&picurl=" + vphoto + "&openid=" + emp.getOpenid() + "&vid=" + vt.getVid() + "&name=" + name;
//        } else {
//            msgUrl = Constant.FASTDFS_URL + "empClient/reply.html?type=v&picurl=" + vphoto + "&openid=" + emp.getOpenid() + "&vid=" + vt.getVid() + "&name=" + name;
//        }

        sb.append("> 访客到访提醒:  ").append("\r\n")
                .append("> 事　项：<font color=\"info\">访客到访</font>  ").append("\r\n")
                .append("> 访客姓名：" + vt.getVname() + "  ").append("\r\n")
                .append("> 描述信息：" + msg + "  ").append("\r\n");
        markdown.put("content", sb.toString());
        reqParam.put("markdown", markdown);

        CacheManager cm = CacheManager.getInstance();
        if (null == cm.getToken("WeChartToken_" + userInfo.getUserid()) || "null".equals(cm.getToken("WeChartToken_" + userInfo.getUserid()))) {
            settoken(cm, userInfo);
        }
        String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=ACCESS_TOKEN";
        try {
            String reqUrl = url.replace("ACCESS_TOKEN", cm.getToken("WeChartToken_" + userInfo.getUserid()));
            String response = HttpClientUtil.postJsonBodyOther(reqUrl, 50000, reqParam, "UTF-8");
            System.out.println(response);
            JsonNode jsonNode = mapperInstance.readValue(response, JsonNode.class);
            String errcode = jsonNode.get("errcode").asText();
            if (null != jsonNode && "0".equals(errcode)) {
                System.out.println(dateFormat.format(new Date()) + " " + jsonNode.get("errmsg").asText());
                return "0";
            } else if ("40001".equals(errcode) || "42001".equals(errcode) || "42002".equals(errcode) || "40014".equals(errcode)) {
                checkresult(cm, userInfo, response, url, reqParam);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "-1";
    }

    @Override
    public String sendNotifyByDD(Employee emp, Visitor vt) {
        // TODO Auto-generated method stub
        Map<String, Object> map = new HashMap<String, Object>();
        if (null != emp.getDdid() && !"".equals(emp.getDdid())) {
            map.put("userid_list", emp.getDdid());
        } else if (null != emp.getOpenid() && !"".equals(emp.getOpenid())) {
            map.put("userid_list", emp.getOpenid());
        } else {
            return "";
        }
        UserInfo userinfo = userDao.getUserInfo(vt.getUserid());
        map.put("agent_id", userinfo.getDdagentid());
        map.put("to_all_user", false);

//        String url = vt.getVphoto();
        String name = vt.getVname();
        try {
//            if (null != url) {
//                url = URLEncoder.encode(url, "utf-8");
//            }
            name = URLEncoder.encode(name, "utf-8");
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        Map<String, Object> markdown = new HashMap<String, Object>();
        markdown.put("title", "访客到访提醒");
        String content = vt.getVname() + "已在公司前台完成来访登记，请准备接待。 ";
        content = "# 事　项：访客到访" + "  \n\n  " + "访客姓名：" + vt.getVname() + "  \n\n  " + "描述信息：" + content + "  \n\n  ";
        markdown.put("text", content);

        Map<String, Object> msg = new HashMap<String, Object>();
        msg.put("msgtype", "markdown");
        msg.put("markdown", markdown);

        map.put("msg", msg);
        String access_token = UtilTools.getDDAccToken(userinfo.getUserid(), userinfo.getDdAppid(), userinfo.getDdAppSccessSecret());
        String response = HttpClientUtil.postJsonBodyOther(Constant.DDNOTIFY_URL + "?access_token=" + access_token, 5000, map, "UTF-8");
        return response;
    }

    @Override
    public String sendNotifyByFeiShu(Employee emp, Visitor vt) {
        // TODO Auto-generated method stub
        ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("receive_id", emp.getOpenid());
        UserInfo userInfo = userDao.getUserInfo(vt.getUserid());
        String content = vt.getVname() + "已在公司前台完成来访登记，请准备接待。 ";
//        content= "事　项：访客到访"+"  \r\n  "+"访客姓名：" + name+"  \r\n  "+"描述信息：" + content+"  \r\n  ";
        Map<String, String> tmap = new HashMap<String, String>();
        tmap.put("text", content);
        map.put("content", JsonUtil.stringify(tmap));
        map.put("msg_type", "text");

        CacheManager cm = CacheManager.getInstance();
        if (null == cm.getToken("FsToken_" + userInfo.getUserid()) || "null".equals(cm.getToken("FsToken_" + userInfo.getUserid()))) {
            setFsToken(cm, userInfo);
        }
        String url = "https://open.feishu.cn/open-apis/im/v1/messages?receive_id_type=open_id";
        try {
            String response = HttpClientUtil.postJsonBodyForFs(url, 3000, map, "utf-8", cm.getToken("FsToken_" + userInfo.getUserid()));
            JsonNode jsonNode = mapperInstance.readValue(response, JsonNode.class);
            String errcode = jsonNode.get("code").asText();
            if (null != jsonNode && "0".equals(errcode)) {
                return "0";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "-1";
    }

    @Override
    public String sendAppoinmentNotifyByWXBus(Employee emp, Visitor vt, int proxy) {
        SimpleDateFormat time = new SimpleDateFormat("MM月dd日 HH时mm分");
        String msgUrl = "";
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("openid", emp.getOpenid());
//        map.put("text", "你好" + emp.getEmpName() + "，" + vt.getVname() + "预约在" + time.format(vt.getAppointmentDate()) + "拜访你，请在企业微信中授权访问。");
        if (proxy == 1) {
            msgUrl = Constant.FASTDFS_URL + "empClient/?path=agent&pc_slide=true";
        } else {
            msgUrl = Constant.FASTDFS_URL + "empClient/?path=myVisitor&pc_slide=true";
        }
//
//        return HttpClientUtil.postJsonBody(Constant.FASTDFS_URL + "cpwechat/php/SendWechat.php", 5000, map, "utf-8");

        ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        UserInfo userInfo = userDao.getUserInfo(emp.getUserid());

        Map<String, Object> reqParam = new HashMap<>();
        reqParam.put("touser", emp.getOpenid());
        reqParam.put("agentid", userInfo.getAgentid());
        reqParam.put("msgtype", "text");
        Map<String, Object> markdown = new HashMap<>();
        StringBuffer sb = new StringBuffer();
        sb.append(" 访客授权提醒:  ").append("\r\n")
                .append(" 事　项：访客授权  ").append("\r\n")
                .append(" 描述信息：" + "你好" + emp.getEmpName() + "，" + vt.getVname() + "预约在" + time.format(vt.getAppointmentDate()) + "拜访你，请在企业微信中授权访问。" + "  ").append("\r\n")
                .append(" 查看详情，请点击：<a href=\"" + msgUrl + "\">查看审批详情</a>");
        markdown.put("content", sb.toString());
        reqParam.put("text", markdown);

        CacheManager cm = CacheManager.getInstance();
        if (null == cm.getToken("WeChartToken_" + userInfo.getUserid()) || "null".equals(cm.getToken("WeChartToken_" + userInfo.getUserid()))) {
            settoken(cm, userInfo);
        }
        String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=ACCESS_TOKEN";
        try {
            String reqUrl = url.replace("ACCESS_TOKEN", cm.getToken("WeChartToken_" + userInfo.getUserid()));
            String response = HttpClientUtil.postJsonBodyOther(reqUrl, 50000, reqParam, "UTF-8");
            System.out.println(response);
            JsonNode jsonNode = mapperInstance.readValue(response, JsonNode.class);
            String errcode = jsonNode.get("errcode").asText();
            if (null != jsonNode && "0".equals(errcode)) {
                System.out.println(dateFormat.format(new Date()) + " " + jsonNode.get("errmsg").asText());
                return "0";
            } else if ("40001".equals(errcode) || "42001".equals(errcode) || "42002".equals(errcode) || "40014".equals(errcode)) {
                checkresult(cm, userInfo, response, url, reqParam);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "-1";
    }

    @Override
    public String sendAppoinmentNotifyByDD(Employee emp, Visitor vt, int proxy) {
        // TODO Auto-generated method stub
        SimpleDateFormat time = new SimpleDateFormat("MM月dd日 HH时mm分");

        Map<String, Object> params = new HashMap<String, Object>();
        if (null != emp.getDdid() && !"".equals(emp.getDdid())) {
            params.put("userid_list", emp.getDdid());
        } else if (null != emp.getOpenid() && !"".equals(emp.getOpenid())) {
            params.put("userid_list", emp.getOpenid());
        } else {
            return "";
        }

        UserInfo userinfo = userDao.getUserInfo(vt.getUserid());
        params.put("agent_id", userinfo.getDdagentid());
        params.put("to_all_user", false);

        Map<String, Object> markdown = new HashMap<String, Object>();
        markdown.put("title", "访客授权提醒");
        String content = "你好" + emp.getEmpName() + "，" + vt.getVname() + "预约在" + time.format(vt.getAppointmentDate()) + "拜访你，请在钉钉中授权访问。";

        String DDlinkStr = "corpid=" + userinfo.getDdcorpid() + "&container_type=work_platform&app_id=0_" + userinfo.getDdagentid() + "&redirect_type=jump&redirect_url=";

        String msgUrl = "";
        if (proxy == 1) {
            msgUrl = Constant.DDPAGELINK_URL + DDlinkStr + Encodes.urlEncode(Constant.FASTDFS_URL + "empClient/?path=agent");
        } else {
            msgUrl = Constant.DDPAGELINK_URL + DDlinkStr + Encodes.urlEncode(Constant.FASTDFS_URL + "empClient/?path=myVisitor");
        }

        content = "# 事　项：访客授权" + "  \n\n  " + "描述信息：" + content + "  \n\n  " +
                "查看详情，请点击：[查看审批详情](" + msgUrl + ")";
        markdown.put("text", content);

        Map<String, Object> msg = new HashMap<String, Object>();
        msg.put("msgtype", "markdown");
        msg.put("markdown", markdown);

        params.put("msg", msg);

        String access_token = UtilTools.getDDAccToken(userinfo.getUserid(), userinfo.getDdAppid(), userinfo.getDdAppSccessSecret());

        String response = HttpClientUtil.postJsonBodyOther(Constant.DDNOTIFY_URL + "?access_token=" + access_token, 5000, params, "UTF-8");


        return response;
    }


    @Override
    public String sendAppoinmentNotifyByFeiShu(Employee emp, Visitor vt, int proxy) {
        // TODO Auto-generated method stub
        ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
        SimpleDateFormat time = new SimpleDateFormat("MM月dd日 HH时mm分");

        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, Object> map = new HashMap<String, Object>();
        List<List<Map<String, String>>> list1 = new ArrayList<List<Map<String, String>>>();
        List<Map<String, String>> list2 = new ArrayList<Map<String, String>>();

        params.put("receive_id", emp.getOpenid());

        UserInfo userInfo = userDao.getUserInfo(vt.getUserid());

        String content = "你好" + emp.getEmpName() + "，" + vt.getVname() + "预约在" + time.format(vt.getAppointmentDate()) + "拜访你，请在飞书中授权访问。";
        String msgUrl = "";
        if (proxy == 1) {
            msgUrl = "https://applink.feishu.cn/client/web_app/open?appId=" + userInfo.getSecurityID() + "&path=/empClient/#/agent";
        } else {
            msgUrl = "https://applink.feishu.cn/client/web_app/open?appId=" + userInfo.getSecurityID() + "&path=/empClient/#/myVisitor";
        }

        Map<String, String> map3 = new HashMap<String, String>();
        map3.put("tag", "text");
        map3.put("text", content);
        Map<String, String> map4 = new HashMap<String, String>();
        map4.put("tag", "a");
        map4.put("href", msgUrl);
        map4.put("text", "查看详情，请点击：[查看审批详情]");
        list2.add(map3);
        list2.add(map4);
        list1.add(list2);

//        content= "事　项：访客授权"+"  \n\n  "+"描述信息：" + content+"  \n\n  "+
//        "查看详情，请点击：[查看审批详情](" + msgUrl + ")";

        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("title", "访客授权");
        map1.put("content", list1);
        map.put("zh_cn", map1);

        params.put("content", JsonUtil.stringify(map));
        params.put("msg_type", "post");

        CacheManager cm = CacheManager.getInstance();
        if (null == cm.getToken("FsToken_" + userInfo.getUserid()) || "null".equals(cm.getToken("FsToken_" + userInfo.getUserid()))) {
            setFsToken(cm, userInfo);
        }
        String url = "https://open.feishu.cn/open-apis/im/v1/messages?receive_id_type=open_id";
        try {
            String response = HttpClientUtil.postJsonBodyForFs(url, 3000, params, "utf-8", cm.getToken("FsToken_" + userInfo.getUserid()));
            JsonNode jsonNode = mapperInstance.readValue(response, JsonNode.class);
            String errcode = jsonNode.get("code").asText();
            if (null != jsonNode && "0".equals(errcode)) {
                return "0";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "-1";
    }


    @Override
    public String sendAppointmentReplyWXBus(Employee emp, Visitor vt, int type) {
        UserInfo userInfo = userDao.getUserInfo(emp.getUserid());
        // TODO Auto-generated method stub
        ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Map<String, Object> reqParam = new HashMap<>();
        reqParam.put("touser", emp.getOpenid());
        reqParam.put("agentid", userInfo.getAgentid());
        reqParam.put("msgtype", "markdown");
        Map<String, Object> markdown = new HashMap<>();
        String msg = "";
        if (type == 3) {
            msg = emp.getEmpName() + "您好，" + vt.getVname() + "接受您的拜访邀请，请在" + dateFormat.format(vt.getAppointmentDate()) + "准时接待。";
        } else if (type == 4) {
            msg = emp.getEmpName() + "您好，" + vt.getVname() + "暂时无法接受您的邀请，请及时沟通。";
        }

        StringBuffer sb = new StringBuffer();
        sb.append("> 邀请回执提醒:  ").append("\r\n")
                .append("> 事　项：<font color=\"info\">邀请回执</font>  ").append("\r\n")
                .append("> 访客姓名：" + vt.getVname() + "  ").append("\r\n")
                .append("> 描述信息：" + msg + "  ").append("\r\n");
        markdown.put("content", sb.toString());
        reqParam.put("markdown", markdown);

        CacheManager cm = CacheManager.getInstance();
        if (null == cm.getToken("WeChartToken_" + userInfo.getUserid()) || "null".equals(cm.getToken("WeChartToken_" + userInfo.getUserid()))) {
            settoken(cm, userInfo);
        }
        String response = "";
        String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=ACCESS_TOKEN";
        try {
            String reqUrl = url.replace("ACCESS_TOKEN", cm.getToken("WeChartToken_" + userInfo.getUserid()));
            response = HttpClientUtil.postJsonBodyOther(reqUrl, 50000, reqParam, "UTF-8");
            JsonNode jsonNode = mapperInstance.readValue(response, JsonNode.class);
            String errcode = jsonNode.get("errcode").asText();
            if (null != jsonNode && "0".equals(errcode)) {
                System.out.println(dateFormat.format(new Date()) + " " + jsonNode.get("errmsg").asText());
                return "0";
            } else if ("40001".equals(errcode) || "42001".equals(errcode) || "42002".equals(errcode) || "40014".equals(errcode)) {
                checkresult(cm, userInfo, response, url, reqParam);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "-1";
    }

    @Override
    public int batchUpdateVisitorAppointment(Visitor vt) {
        // TODO Auto-generated method stub
        return visitorDao.batchUpdateVisitorAppointment(vt);
    }

    @Override
    public String sendAppointmentReplyWX(Employee emp, Visitor vt, int type) {
        // TODO Auto-generated method stub
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Map<String, String>> maps = new HashMap<String, Map<String, String>>();
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String result = "";
        if (type == 3) {
            String[] str = new String[]{"first", "keyword1", "keyword2", "remark"};

            for (int i = 0; i < str.length; i++) {
                if (i == 0) {
                    Map<String, String> map1 = new HashMap<String, String>();
                    map1.put("value", "用户接受了您的邀请");
                    map1.put("color", "#173177");
                    maps.put(str[i], map1);
                } else if (i == 1) {
                    Map<String, String> map2 = new HashMap<String, String>();
                    map2.put("value", vt.getVname());
                    map2.put("color", "#173177");
                    maps.put(str[i], map2);
                } else if (i == 2) {
                    Map<String, String> map3 = new HashMap<String, String>();
                    map3.put("value", time.format(new Date()));
                    map3.put("color", "#173177");
                    maps.put(str[i], map3);
                } else if (i == 3) {
                    Map<String, String> map6 = new HashMap<String, String>();
                    map6.put("value", "");
                    map6.put("color", "#173177");
                    maps.put(str[i], map6);
                }

            }

            map.put("touser", emp.getOpenid());
            map.put("url", "");
            map.put("template_id", Constant.WeiXin_Notify.get("InviteSuccess"));
            map.put("topcolor", "#FF0000");
            map.put("data", maps);

            CacheManager cm = CacheManager.getInstance();
            if (null == cm.getToken("token") || "".equals(cm.getToken("token"))) {
                settoken(cm);
            }

            result = HttpClientUtil.postJsonBody("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + cm.getToken("token"), 5000, map, "utf-8");
            checkresult(cm, result, map);
        } else if (type == 4) {
            String[] str = new String[]{"first", "keyword1", "keyword2", "keyword3", "remark"};

            for (int i = 0; i < str.length; i++) {
                if (i == 0) {
                    Map<String, String> map1 = new HashMap<String, String>();
                    map1.put("value", "用户暂时无法接受您的邀请");
                    map1.put("color", "#173177");
                    maps.put(str[i], map1);
                } else if (i == 1) {
                    Map<String, String> map2 = new HashMap<String, String>();
                    map2.put("value", vt.getVname());
                    map2.put("color", "#173177");
                    maps.put(str[i], map2);
                } else if (i == 2) {
                    Map<String, String> map3 = new HashMap<String, String>();
                    map3.put("value", "不方便接受");
                    map3.put("color", "#173177");
                    maps.put(str[i], map3);
                } else if (i == 3) {
                    Map<String, String> map3 = new HashMap<String, String>();
                    map3.put("value", time.format(new Date()));
                    map3.put("color", "#173177");
                    maps.put(str[i], map3);
                } else if (i == 4) {
                    Map<String, String> map6 = new HashMap<String, String>();
                    map6.put("value", "");
                    map6.put("color", "#173177");
                    maps.put(str[i], map6);
                }

            }

            map.put("touser", emp.getOpenid());
            map.put("url", "");
            map.put("template_id", Constant.WeiXin_Notify.get("InviteFefuse"));
            map.put("topcolor", "#FF0000");
            map.put("data", maps);

            CacheManager cm = CacheManager.getInstance();
            if (null == cm.getToken("token") || "".equals(cm.getToken("token"))) {
                settoken(cm);
            }

            result = HttpClientUtil.postJsonBody("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + cm.getToken("token"), 5000, map, "utf-8");
            checkresult(cm, result, map);
        }

        return result;
    }

    @Override
    public List<RespVisitor> getVistorProxyListByEmpPhone(RespVisitor vt) {
        // TODO Auto-generated method stub
        return visitorDao.getVistorProxyListByEmpPhone(vt);
    }

    @Override
    public int updateSignPdf(Visitor vt) {
        // TODO Auto-generated method stub
        return visitorDao.updateSignPdf(vt);
    }

    @Override
    public int updateLeaveTime(Visitor vt) {
        // TODO Auto-generated method stub
        return visitorDao.updateLeaveTime(vt);
    }

    @Override
    public void sendAutoNotifyMessage(String key) {
        if (key.indexOf("_") != -1) {
            String[] id = key.split("_");
            if (id.length > 1) {
                if ("dfvid".equals(id[0])) {
                    //预约时设置的定制消息
                    Visitor v = visitorDao.getVisitorById(Integer.parseInt(id[1]));
                    UserInfo userinfo = userDao.getExtendsInfo(v.getUserid());
                    List<Employee> emplist = employeeDao.getSubAccountEmpList(v.getUserid(), v.getSubaccountId());
                    for (int i = 0; i < emplist.size(); i++) {
                        if (v.getEmpid() == emplist.get(i).getEmpid() || emplist.get(i).getEmpType() != 1) {
                            continue;
                        } else {
                            if (StringUtils.isNotBlank(emplist.get(i).getEmpPhone())) {
                                this.sendInviteSMS(userinfo, v, emplist.get(i));
                            }

                            if (StringUtils.isNotBlank(emplist.get(i).getOpenid())) {
                                this.sendDefaultNotifyByWeixin(v, emplist.get(i));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 检查访客轨迹，是否到指定位置
     * @param vid
     */
    @Override
    public int checkTrack(String vid) {
        if(StringUtils.isEmpty(vid)){
            return 0;
        }
        Visitor v = getVisitorById(Integer.parseInt(vid));
        if(v == null || v.getSignOutDate() != null){
            return 0;
        }

        LambdaQueryWrapper<Track> lambdaQueryWrapper = Wrappers.lambdaQuery(Track.class);
        lambdaQueryWrapper.select(Track::getOpenId,Track::getCreateTime,Track::getLatitude,Track::getLongitude);
        lambdaQueryWrapper.eq(Track::getVid,vid)
        .orderByDesc(Track::getCreateTime).last("limit 10");
        List<Track> list = trackService.list(lambdaQueryWrapper);

        //更新超时
        if(list.size()==0){
            return 0;
        }

        String gfid = v.getExtendValue(EXTEND_KEY_GEOFENCE);
        Geofence geofence = geofenceService.getById(Integer.parseInt(gfid));
        Geofence parentGf = geofenceService.getById(geofence.getParentid());
        String parentGfid = gfid;
        if(parentGf != null){
            parentGfid = parentGf.getId()+"";
        }
        //与大园区围栏关系
        if(trackService.isInGeofence(vid,parentGfid)){
            //在园区内
            if(v.getVisitdate()==null){
                v.setVisitdate(list.get(0).getCreateTime());
                updateVisitorAppointment(v);
            }

            //与电子围栏关系
            if(trackService.isInGeofence(v.getVid()+"",v.getExtendValue(EXTEND_KEY_GEOFENCE))){
                if(StringUtils.isEmpty(v.getExtendValue(EXTEND_KEY_ARRIVETIME))){
                    //到达电子围栏
                    v.addExtendValue(EXTEND_KEY_ARRIVETIME,list.get(0).getCreateTime().toString());
                    updateVisitorExtendCol(v);
                }
            } else{
                //在围栏外面
                if(StringUtils.isNotEmpty(v.getExtendValue(EXTEND_KEY_ARRIVETIME))
                        &&v.getLeaveTime() == null){
                    //离开电子围栏
                    v.setLeaveTime(list.get(list.size()-1).getCreateTime());
                    updateLeaveTime(v);
                }
                if(!trackService.isInRoute(v.getVid()+"",v.getExtendValue(EXTEND_KEY_ROUTE))){
                    //偏离轨迹
//                        messageService.sendCommonNotifyEvent(v,NotifyEvent.EVENTTYPE_TRACK_OFFROUTE_TOVISITOR);
                    messageService.sendCommonNotifyEvent(v,NotifyEvent.EVENTTYPE_TRACK_OFFROUTE_TOMANAGER);
                    return ErrorEnum.E_1202.getCode();
                }
            }
        }else{
            //已离开
            if(StringUtils.isNotEmpty(v.getExtendValue(EXTEND_KEY_ARRIVETIME))){
                v.setSignOutDate(list.get(0).getCreateTime());
                updateSignOutByVid(v);
            }
            return 0;
        }

        return 0;
}


    /**
     * 检查访客轨迹，是否超时
     * @param key
     */
    @Override
    public void checkTrackTimeout(String key) {
        if (key.indexOf("_") != -1) {
            String[] id = key.split("_");
            if (id.length > 1) {
                if (!"track".equals(id[0])) {
                    return;
                }
                String vid = id[1];
                SysLog.info("checkTrack id:"+id[1]);

                Visitor v = getVisitorById(Integer.parseInt(vid));
                if(v == null || v.getVisitdate() == null || v.getSignOutDate() != null){
                    return;
                }


                //获取最近30内的轨迹数据
                Calendar current = Calendar.getInstance();// 获取当前日期
                current.setTime(new Date());
                current.add(Calendar.MINUTE, -30);
                LambdaQueryWrapper<Track> lambdaQueryWrapper = Wrappers.lambdaQuery(Track.class);
                lambdaQueryWrapper.select(Track::getOpenId,Track::getCreateTime,Track::getLatitude,Track::getLongitude);
                lambdaQueryWrapper.eq(Track::getVid,vid)
                        .ge(Track::getCreateTime, current.getTime())
                        .orderByDesc(Track::getCreateTime).last("limit 10");
                List<Track> list = trackService.list(lambdaQueryWrapper);

                //更新超时
                if(list.size()==0){
                    return;
                }

                Date now = new Date();
                if(now.getTime()-(list.get(0).getCreateTime().getTime())>5*60*1000){
                    //5分钟未更新通知
                    messageService.sendCommonNotifyEvent(v,NotifyEvent.EVENTTYPE_TRACK_TIMEOUT_TOVISITOR);
                    messageService.sendCommonNotifyEvent(v,NotifyEvent.EVENTTYPE_TRACK_TIMEOUT_TOMANAGER);
                }
            }
        }
    }

    @Override
    public void sendLeaveMessage(String key) {
        String[] id = key.split("_");
        Visitor v=null;
        if (id.length > 1) {
            if ("aid".equals(id[0])) {
                Appointment app = appointmentDao.getAppointmentbyId(Integer.parseInt(id[1]));
                v = BeanUtils.appointmentToVisitor(app);
            } else if ("vid".equals(id[0])) {
                v = visitorDao.getVisitorById(Integer.parseInt(id[1]));
            } else {
                return;
            }

        } else {
            return;
        }

        messageService.sendCommonNotifyEvent(v,NotifyEvent.EVENTTYPE_EMP_NO_LEAVE);
        messageService.sendCommonNotifyEvent(v,NotifyEvent.EVENTTYPE_VISITOR_NO_LEAVE);
    }

    @Override
    public Visitor getVisitorByAppId(Visitor vt) {
        // TODO Auto-generated method stub
        return visitorDao.getVisitorByAppId(vt);
    }

    @Override
    public List<RespVisitor> getExamVisitList(
            Map<String, Object> conditions) {
        // TODO Auto-generated method stub
        return visitorDao.getExamVisitList(conditions);
    }

    @Override
    public Visitor getVisitorByPlateNum(Visitor vt) {
        // TODO Auto-generated method stub
        return visitorDao.getVisitorByPlateNum(vt);
    }

    @Override
    public String sendSmsToResidentVisitor(UserInfo userinfo, ResidentVisitor rv) {
        // TODO Auto-generated method stub
        Map<String, String> params = new HashMap<String, String>();
        params.put("message", rv.getName() + "您好，您的供应商身份已通过" + userinfo.getCardText() + "审核，"
                + "服务期限为" + rv.getStartDate() + "到" + rv.getEndDate() + "，请点击链接通过培训答题：" + Constant.FASTDFS_URL +
                "visitorQrcode.html?rid=" + AESUtil.encode(rv.getRid().substring(1), Constant.AES_KEY));
        params.put("phone", rv.getPhone());
        String response = UtilTools.sendSmsByYiMei(params, configureDao, userinfo);

        if ("0".equals(response)) {
            userinfo.setAppSmsCount(userinfo.getAppSmsCount() + 2);
            userDao.updateAppSmsCount(userinfo);
        }

        OperateLog.addSMSLog(operateLogService,userinfo.getUserid(), response, 2,rv.getPhone(),rv.getName(),params.get("message"));
        return response;
    }


    @Override
    public String sendDefaultNotifyByWeixin(Visitor vt, Employee emp) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Map<String, String>> maps = new HashMap<String, Map<String, String>>();
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] str = new String[]{"first", "keyword1", "keyword2", "keyword3", "keyword4", "keyword5", "remark"};

        for (int i = 0; i < str.length; i++) {
            if (i == 0) {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("value", "您有一个拜访申请需要授权");
                map1.put("color", "#173177");
                maps.put(str[i], map1);
            } else if (i == 1) {
                Map<String, String> map2 = new HashMap<String, String>();
                map2.put("value", vt.getVname());
                map2.put("color", "#173177");
                maps.put(str[i], map2);
            } else if (i == 2) {
                Map<String, String> map3 = new HashMap<String, String>();
                map3.put("value", vt.getVisitType());
                map3.put("color", "#173177");
                maps.put(str[i], map3);
            } else if (i == 3) {
                Map<String, String> map4 = new HashMap<String, String>();

                if ("商务".equals(vt.getVisitType())) {
                    map4.put("value", "需要访问贵公司洽谈业务");
                } else if ("面试".equals(vt.getVisitType())) {
                    map4.put("value", "需要访问贵公司参加面试");
                } else {
                    map4.put("value", vt.getVisitType());
                }
                map4.put("color", "#173177");
                maps.put(str[i], map4);
            } else if (i == 4) {
                Map<String, String> map5 = new HashMap<String, String>();
                map5.put("value", time.format(vt.getAppointmentDate()));
                map5.put("color", "#173177");
                maps.put(str[i], map5);
            } else if (i == 5) {
                Map<String, String> map6 = new HashMap<String, String>();
                map6.put("value", time.format(new Date(vt.getAppointmentDate().getTime() + 60 * 60 * 1000)));
                map6.put("color", "#173177");
                maps.put(str[i], map6);
            } else if (i == 6) {
                Map<String, String> map6 = new HashMap<String, String>();
                map6.put("value", "请点击详情进行访问授权！");
                map6.put("color", "#173177");
                maps.put(str[i], map6);
            }

        }

        String vid = AESUtil.encode(vt.getVid() + "", Constant.AES_KEY);
        map.put("touser", emp.getOpenid());
        map.put("url", Constant.FASTDFS_URL + "empClient/Proxypage.html?v=" + vid);
        map.put("template_id", Constant.WeiXin_Notify.get("AppointmentNotify"));
        map.put("topcolor", "#FF0000");
        map.put("data", maps);


        CacheManager cm = CacheManager.getInstance();
        if (null == cm.getToken("token") || "".equals(cm.getToken("token"))) {
            settoken(cm);
        }

        String result = HttpClientUtil.postJsonBody("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + cm.getToken("token"), 5000, map, "utf-8");
        checkresult(cm, result, map);

        return result;

    }

    @Override
    public List<Visitor> getVisitorByexpireDate(int userid, String expireDate) {
        return visitorDao.getVisitorByexpireDate(userid, expireDate);
    }

    @Override
    public int deleteVisitByVids(List<Integer> expireVid) {
        return visitorDao.deleteVisitByVids(expireVid);
    }

    @Override
    public List<VisitorChart> getSignInVisitorByDept(RequestVisit visit) {
        return visitorDao.getSignInVisitorByDept(visit);
    }

    @Override
    public int updateCardNo(Visitor vt) {
        // TODO Auto-generated method stub
        return visitorDao.updateCardNo(vt);
    }


    @Override
    public VisitorChart getNoArrivedVCount(RequestVisit requestVisit) {
        // TODO Auto-generated method stub
        return visitorDao.getNoArrivedVCount(requestVisit);
    }

    @Override
    public List<VisitorChart> getNoArrivedLineChart(RequestVisit requestVisit) {
        // TODO Auto-generated method stub
        return visitorDao.getNoArrivedLineChart(requestVisit);
    }

    @Override
    public List<VisitorChart> getArrivedVisitorChart(RequestVisit requestVisit) {
        // TODO Auto-generated method stub
        return visitorDao.getArrivedVisitorChart(requestVisit);
    }

    @Override
    public List<VisitorChart> getAllArrivedVisitorChart(RequestVisit requestVisit) {
        // TODO Auto-generated method stub
        return visitorDao.getAllArrivedVisitorChart(requestVisit);
    }

    @Override
    public List<VisitorChart> getAllArrivedVisitorChartSmart(RequestVisit requestVisit) {
        // TODO Auto-generated method stub
        return visitorDao.getAllArrivedVisitorChartSmart(requestVisit);
    }

    /**
     * ifs报表1
     *
     * @param rv
     * @return
     */
    @Override
    public List<VisitorRecord> newSearchVisitorByCondition1(Map<String, Object> rv) {
        return visitorDao.newSearchVisitorByCondition1(rv);
    }

    @Override
    public VisitorChart getVisitSaCountByVphone(RequestVisit requestVisit) {
        // TODO Auto-generated method stub
        return visitorDao.getVisitSaCountByVphone(requestVisit);
    }

    @Override
    public int updateVisitorExtendCol(Visitor vt) {
        // TODO Auto-generated method stub
        return visitorDao.updateVisitorExtendCol(vt);
    }

    @Override
    public List<VisitorChart> getArrivedVCount(RequestVisit rv) {
        return visitorDao.getArrivedVCount(rv);
    }

    @Override
    public String sendCancelVisitByWeixin(Visitor vt, Person person) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH");
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Map<String, String>> maps = new HashMap<String, Map<String, String>>();
        String[] str = new String[]{"thing1", "time2", "thing3", "thing5"};
        for (int i = 0; i < str.length; i++) {
            if (i == 0) {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("value", nameFormat(vt.getEmpName()));
                maps.put(str[i], map1);
            } else if (i == 1) {
                Map<String, String> map2 = new HashMap<String, String>();
                map2.put("value", dateFormat.format(vt.getAppointmentDate()));
                maps.put(str[i], map2);
            } else if (i == 2) {
                Map<String, String> map3 = new HashMap<String, String>();
                map3.put("value", vt.getVisitType());
                maps.put(str[i], map3);
            } else if (i == 3) {
                Map<String, String> map4 = new HashMap<String, String>();
                StringBuffer sb = new StringBuffer("被访人取消了定于" + dateFormat2.format(vt.getAppointmentDate()) + "点的会面,请及时联系");
                map4.put("value", sb.toString());
                maps.put(str[i], map4);
            }
        }

        map.put("touser", person.getPopenid());
        map.put("template_id", Constant.WeiXin_Notify.get("Visitcancel"));
        map.put("data", maps);
        map.put("miniprogram_state", "trial");
        map.put("lang", "zh_CN");

        CacheManager cm = CacheManager.getInstance();
        if (null == cm.getToken("token") || "".equals(cm.getToken("token"))) {
            settoken(cm);
        }

        String result = HttpClientUtil.postJsonBodyOther("https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + cm.getToken("token"), 5000, map, "utf-8");
        checkresult(cm, result, map);

        return result;
    }

    @Override
    public String sendArrivedWXNotify(Employee employee, Visitor vt) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Map<String, String>> maps = new HashMap<String, Map<String, String>>();
        String[] str = new String[]{"name1", "time2", "thing3", "thing7"};
        for (int i = 0; i < str.length; i++) {
            if (i == 0) {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("value", nameFormat(vt.getVname()));
                maps.put(str[i], map1);
            } else if (i == 1) {
                Map<String, String> map2 = new HashMap<String, String>();
                map2.put("value", dateFormat.format(vt.getVisitdate()));
                maps.put(str[i], map2);
            } else if (i == 2) {
                Map<String, String> map3 = new HashMap<String, String>();
                map3.put("value", vt.getVisitType().split("#")[0]);
                maps.put(str[i], map3);
            } else if (i == 3) {
                Map<String, String> map4 = new HashMap<String, String>();
                StringBuffer sb = new StringBuffer("访客已过闸机，请准备接待");
                map4.put("value", sb.toString());
                maps.put(str[i], map4);
            }
        }

        map.put("touser", employee.getOpenid());
        map.put("template_id", Constant.WeiXin_Notify.get("AppointArrived"));
        map.put("data", maps);
        map.put("miniprogram_state", "formal");
        map.put("lang", "zh_CN");

        CacheManager cm = CacheManager.getInstance();
        if (null == cm.getToken("token") || "".equals(cm.getToken("token"))) {
            settoken(cm);
        }

        String result = HttpClientUtil.postJsonBodyOther("https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + cm.getToken("token"), 5000, map, "utf-8");
        checkresult(cm, result, map);

        return result;
    }


    /**
     * 按小程序订阅消息模板，name.data格式要求格式化数据
     * 中文名10个汉字内；纯英文名20个字母内；中文和字母混合按中文名算，10个字内
     * @param name
     * @return
     */
    private String nameFormat(String name){
        if(StringUtils.isEmpty(name)){
            return "";
        }
        name = name.replaceAll("\\d+", "");
        SysLog.info("name="+name+" length="+name.length());
        if(name.length()<=10){
            return name;
        }

        Pattern p = Pattern.compile("[\u4E00-\u9FA5|\\！|\\，|\\。|\\(|\\)|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】]");

        Matcher m = p.matcher(name);

        if (m.find()) {
            //有中文
            return name.substring(0,10);
        }

        //纯英文
        if(name.length()<=20){
            return name;
        }

        return name.substring(0,20);
    }

    @Override
    public List<Visitor> getVListBySubAccountId(int empid, int userid) {
        return visitorDao.getVListBySubAccountId(empid, userid);
    }

    @Override
    public List<RespVisitor> searchVisitByConditionPage(RequestVisit rv) {
        // TODO Auto-generated method stub
        return visitorDao.searchVisitByConditionPage(rv);
    }

    @Override
    public List<RespVisitor> searchAppByConditionPage(RequestVisit requestVisit) {
        // TODO Auto-generated method stub
        return visitorDao.searchAppByConditionPage(requestVisit);
    }

    @Override
    public List<RespVisitor> SearchRVisitorByConditionPage(
            RequestVisit requestVisit) {
        // TODO Auto-generated method stub
        return visitorDao.SearchRVisitorByConditionPage(requestVisit);
    }

    @Override
    public DataStatistics getSignedCount(RequestVisit rv) {
        // TODO Auto-generated method stub
        return visitorDao.getSignedCount(rv);
    }

    @Override
    public DataStatistics getAppointmentCount(RequestVisit rv) {
        // TODO Auto-generated method stub
        return visitorDao.getAppointmentCount(rv);
    }

    @Override
    public DataStatistics getRvSignedCount(RequestVisit rv) {
        // TODO Auto-generated method stub
        return visitorDao.getRvSignedCount(rv);
    }

    @Override
    public List<VisitorChart> getAllVisitorLineChart(RequestVisit rv) {
        // TODO Auto-generated method stub
        return visitorDao.getAllVisitorLineChart(rv);
    }

    @Override
    public List<VisitorChart> getDeptVisitByAppDate(RequestVisit requestVisit) {
        return visitorDao.getDeptVisitByAppDate(requestVisit);
    }


    @Override
    public String sendDMettingAppointmentSMS(UserInfo userinfo, Visitor vt) {
        // TODO Auto-generated method stub
        String url = "";
        String tempid = "";
        Map<String, String> params = new HashMap<String, String>();
        url = Constant.FASTDFS_URL + "bus.html?id=" + AESUtil.encode(vt.getVid() + "", Constant.AES_KEY);
        tempid = "SMS_52405001";
        params.put("message", "您好" + vt.getVname() + "！" + userinfo.getCardText() + "诚邀您参加" + vt.getmName() + "会议，详情请点击邀请函： " + url);

        params.put("phone", vt.getVphone());
        String response = UtilTools.sendSmsByYiMei(params, configureDao, userinfo);

        if ("0".equals(response)) {
            userinfo.setAppSmsCount(userinfo.getAppSmsCount() + 2);
            userDao.updateAppSmsCount(userinfo);
        }

        OperateLog.addSMSLog(operateLogService,userinfo.getUserid(), response, 2,vt.getVphone(),vt.getVname(),params.get("message"));
        return response;
    }

    @Override
    public String sendProcessMarkdownNotifyByWXBus(Employee employee, List<Appointment> appointmentList) {
        ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
        UserInfo userInfo = userDao.getUserInfo(employee.getUserid());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Map<String, Object> reqParam = new HashMap<>();
        reqParam.put("touser", employee.getOpenid());
        reqParam.put("agentid", userInfo.getAgentid());
        reqParam.put("msgtype", "text");
        String msgUrl = Constant.FASTDFS_URL + "cpwechat/coolwechat/approve/agreedetail.html?vid=" + appointmentList.get(0).getAgroup() + "&openid=" + employee.getOpenid() + "&pType=1";
        Map<String, Object> markdown = new HashMap<>();
        StringBuffer sb = new StringBuffer();
        sb.append("您有新审批提醒:  ").append("\r\n")
                .append("事　项：邀请审批  ").append("\r\n")
                .append("发起人：" + appointmentList.get(0).getEmpName() + " ").append("\r\n")
                .append("审批描述：" + appointmentList.get(0).getEmpName() + " 邀请访客 " + appointmentList.get(0).getName() + " 拜访  ").append("\r\n")
                .append("会面时间：" + dateFormat.format(appointmentList.get(0).getAppointmentDate())).append("\r\n")
                .append("查看详情，请点击：<a href=\"" + msgUrl + "\">查看审批详情</a>");
        markdown.put("content", sb.toString());
        reqParam.put("text", markdown);

        CacheManager cm = CacheManager.getInstance();
        if (null == cm.getToken("WeChartToken_" + userInfo.getUserid()) || "null".equals(cm.getToken("WeChartToken_" + userInfo.getUserid()))) {
            settoken(cm, userInfo);
        }
        String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=ACCESS_TOKEN";
        try {
            String s = mapperInstance.writeValueAsString(reqParam);
            String reqUrl = url.replace("ACCESS_TOKEN", cm.getToken("WeChartToken_" + userInfo.getUserid()));
            String response = HttpClientUtil.postJsonBodyOther(reqUrl, 50000, reqParam, "UTF-8");
            System.out.println(response);
            JsonNode jsonNode = mapperInstance.readValue(response, JsonNode.class);
            String errcode = jsonNode.get("errcode").asText();
            if (null != jsonNode && "0".equals(errcode)) {
                System.out.println(dateFormat.format(new Date()) + " " + jsonNode.get("errmsg").asText());
                return "0";
            } else if ("40001".equals(errcode) || "42001".equals(errcode) || "42002".equals(errcode) || "40014".equals(errcode)) {
                checkresult(cm, userInfo, response, url, reqParam);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "-1";
    }

    @Override
    public String sendProcessMarkdownFinishNotifyByWXBus(Employee employee, ProcessRecord pr) {
        ObjectMapper mapperInstance = JacksonJsonUtil.getMapperInstance(false);
        UserInfo userInfo = userDao.getUserInfo(employee.getUserid());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Map<String, Object> reqParam = new HashMap<>();
        reqParam.put("touser", employee.getOpenid());
        reqParam.put("agentid", userInfo.getAgentid());
        reqParam.put("msgtype", "markdown");
        Map<String, Object> markdown = new HashMap<>();
        StringBuffer sb = new StringBuffer();
        String permission = "";
        if (pr.getStatus() == 1) {
            permission = "同意";
        } else {
            permission = "拒绝";
        }
        sb.append("> 审批完成提醒:  ").append("\r\n")
                .append("> 事　项：<font color=\"info\">邀请审批</font>  ").append("\r\n")
                .append("> 发起人：" + pr.getSubEmpName() + "  ").append("\r\n")
                .append("> 审批描述：" + pr.getSubEmpName() + "邀请访客 " + pr.getVname() + " 拜访  ").append("\r\n")
                .append("> 会面时间：<font color=\"warning\">" + dateFormat.format(pr.getAppTime()) + "</font>  ").append("\r\n")
                .append("> 审批状态：" + permission + "  ").append("\r\n")
                .append("> 备注：" + pr.getRemark() + "  ");
        markdown.put("content", sb.toString());
        reqParam.put("markdown", markdown);

        CacheManager cm = CacheManager.getInstance();
        if (null == cm.getToken("WeChartToken_" + userInfo.getUserid()) || "null".equals(cm.getToken("WeChartToken_" + userInfo.getUserid()))) {
            settoken(cm, userInfo);
        }
        String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=ACCESS_TOKEN";
        try {
            String reqUrl = url.replace("ACCESS_TOKEN", cm.getToken("WeChartToken_" + userInfo.getUserid()));
            String response = HttpClientUtil.postJsonBodyOther(reqUrl, 50000, reqParam, "UTF-8");
            JsonNode jsonNode = mapperInstance.readValue(response, JsonNode.class);
            String errcode = jsonNode.get("errcode").asText();
            if (null != jsonNode && "0".equals(errcode)) {
                System.out.println(dateFormat.format(new Date()) + " " + jsonNode.get("errmsg").asText());
                return "0";
            } else if ("40001".equals(errcode) || "42001".equals(errcode) || "42002".equals(errcode) || "40014".equals(errcode)) {
                checkresult(cm, userInfo, response, url, reqParam);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "-1";
    }

    @Override
    public List<VisitorRecord> newSearchVisitorByCondition1Page(ReqVisitorRecord rv) {
        return visitorDao.newSearchVisitorByCondition1Page(rv);
    }

    @Override
    public String sendProcessMarkdownNotifyBydingtalk(Employee employee, List<Appointment> appointmentList, UserInfo userInfo) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        //工作通知入参
        String url = "dingtalk://dingtalkclient/action/openapp?corpid=CORPID" +
                "&container_type=work_platform" +
                "&app_id=0_1083826831" +
                "&redirect_type=jump" +
                "&redirect_url="+ Constant.FASTDFS_URL+"/empClient/?path=approve";
        url.replace("CORPID",userInfo.getDdcorpid());
//        String msgUrl = Constant.FASTDFS_URL +
//                "cpwechat/coolwechat/approve/agreedetail.html?vid=" + appointmentList.get(0).getAgroup() +
//                "&openid=" + employee.getOpenid() + "&pType=1";
        StringBuffer sb = new StringBuffer();
        sb.append("> 事　项：邀请审批  ").append("\n  ")
                .append("> 发起人：" + appointmentList.get(0).getEmpName() + " ").append("  \n  ")
                .append("> 审批描述：" + appointmentList.get(0).getEmpName() + " 邀请访客 " + appointmentList.get(0).getName() + " 拜访  ").append("  \n")
                .append("> 会面时间：" + dateFormat.format(appointmentList.get(0).getAppointmentDate()) + "  ").append("  \n")
                .append("> 查看详情，请点击：[查看审批详情](" + url + ")  ");
        //获取请求的叮叮token
        CacheManager cm = CacheManager.getInstance();
        if (StringUtils.isBlank(cm.getToken("dingTalkToken"))) {
            setDingTalkToken(cm, userInfo);
        }

        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");
            OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
            request.setAgentId((long) Integer.parseInt(userInfo.getDdagentid()));
            request.setUseridList(employee.getDdid());

            OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
            msg.setMsgtype("markdown");

            OapiMessageCorpconversationAsyncsendV2Request.Markdown markdown = new OapiMessageCorpconversationAsyncsendV2Request.Markdown();
            markdown.setTitle("您有新审批提醒:  ");
            markdown.setText(sb.toString());
            msg.setMarkdown(markdown);
            request.setMsg(msg);

            OapiMessageCorpconversationAsyncsendV2Response rsp = client.execute(request, cm.getToken("dingTalkToken"));
            System.out.println(dateFormat.format(new Date()) + " 工作消息通知响应：" + rsp.getBody());
            if ("0".equals(rsp.getErrorCode())) {
                return "0";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "-1";
    }

    @Override
    public void sendProcessNotify(Object... o) {
        ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
        EmpVisitProxy vp = objectMapper.convertValue(o[0], EmpVisitProxy.class);
        Employee empProxy = objectMapper.convertValue(o[1], Employee.class);
        UserInfo userInfo = objectMapper.convertValue(o[2], UserInfo.class);

        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, Appointment.class);
        List<Appointment> appointmentList = objectMapper.convertValue(o[3], javaType);
        Employee emp = objectMapper.convertValue(o[4], Employee.class);

        Date pdate = new Date();
        Date sdate = null;
        Date edate = null;
        if (null != vp) {
            empProxy = employeeDao.getEmployee(vp.getProxyId());
            sdate = vp.getStartDate();
            edate = vp.getEndDate();
        }

        //通知模式开关，默认关闭，打开：一个成功就不在发送
        boolean signleNotify = false;

        //微信通知
        if (StringUtils.isNotBlank(emp.getOpenid()) && userInfo.getMsgNotify() == 1) {

        }

        //企业微信通知
        if (StringUtils.isNotBlank(emp.getOpenid()) && userInfo.getWxBusNotify() == 1) {
            if (!signleNotify || userInfo.getNotifyType() == 0) {
                this.sendProcessMarkdownNotifyByWXBus(emp, appointmentList);
                if (null != vp && null != empProxy && vp.getProxyStatus() == 1 && pdate.getTime() >= sdate.getTime() && pdate.getTime() <= edate.getTime()) {
                    this.sendProcessMarkdownNotifyByWXBus(empProxy, appointmentList);
                }
            }
        }

        //叮叮通知
        if (StringUtils.isNotBlank(emp.getDdid()) && userInfo.getDdnotify() == 1) {
            if (!signleNotify || userInfo.getNotifyType() == 0) {
                this.sendProcessMarkdownNotifyBydingtalk(emp, appointmentList, userInfo);
                if (null != vp && null != empProxy && vp.getProxyStatus() == 1 && pdate.getTime() >= sdate.getTime() && pdate.getTime() <= edate.getTime()) {
                    this.sendProcessMarkdownNotifyBydingtalk(empProxy, appointmentList, userInfo);
                }
            }
        }
    }

    @Override
    public void sendProcessFinishNotify(Object... o) {
        ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
        EmpVisitProxy vp = objectMapper.convertValue(o[0], EmpVisitProxy.class);
        Employee empProxy = objectMapper.convertValue(o[1], Employee.class);
        UserInfo userinfo = objectMapper.convertValue(o[2], UserInfo.class);
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, Appointment.class);
        List<Appointment> appointmentList = objectMapper.convertValue(o[3], javaType);
        Employee emp = objectMapper.convertValue(o[4], Employee.class);
        Visitor vt = objectMapper.convertValue(o[4], Visitor.class);
        ProcessRecord processRecord = objectMapper.convertValue(o[5], ProcessRecord.class);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date pdate = new Date();
        Date sdate = null;
        Date edate = null;
        if (null != vp) {
            empProxy = employeeDao.getEmployee(vp.getProxyId());
            sdate = vp.getStartDate();
            edate = vp.getEndDate();
        }

        //通知模式开关，默认关闭，打开：一个成功就不在发送
        boolean signleNotify = false;

        //微信通知
        if (StringUtils.isNotBlank(emp.getOpenid()) && userinfo.getMsgNotify() == 1) {
            if (!signleNotify || userinfo.getNotifyType() == 0) {

                if (emp.getOpenid().length() != 28) {
                    // 发送企业微信审批通知
                    sendProcessMarkdownFinishNotifyByWXBus(emp, processRecord);
                    if (null != vp && null != empProxy && vp.getProxyStatus() == 1 && pdate.getTime() >= sdate.getTime() && pdate.getTime() <= edate.getTime()) {
                        this.sendProcessMarkdownNotifyByWXBus(empProxy, appointmentList);
                    }
                } else {
                    //发送常规微信通知
                    String msg = "员工(" + vt.getEmpName() + ")的邀请审批已经处理完毕！";
                    String response = sendTextNotifyByWXBus(emp, msg);
                    System.out.println(dateFormat.format(new Date()) + response);
                }
                if (userinfo.getNotifyType() == 1) {
                    signleNotify = true;
                }
            }
        }

        //企业微信通知
        if (StringUtils.isNotBlank(emp.getOpenid()) && userinfo.getWxBusNotify() == 1) {
            if (!signleNotify || userinfo.getNotifyType() == 0) {
                this.sendProcessMarkdownNotifyByWXBus(emp, appointmentList);
                if (null != vp && null != empProxy && vp.getProxyStatus() == 1 && pdate.getTime() >= sdate.getTime() && pdate.getTime() <= edate.getTime()) {
                    this.sendProcessMarkdownNotifyByWXBus(empProxy, appointmentList);
                }
            }
            if (userinfo.getNotifyType() == 1) {
                signleNotify = true;
            }
        }

        //叮叮通知
        if (StringUtils.isNotBlank(emp.getDdid()) && userinfo.getDdnotify() == 1) {
            if (!signleNotify || userinfo.getNotifyType() == 0) {
                this.sendProcessMarkdownNotifyBydingtalk(emp, appointmentList, userinfo);
                if (null != vp && null != empProxy && vp.getProxyStatus() == 1 && pdate.getTime() >= sdate.getTime() && pdate.getTime() <= edate.getTime()) {
                    this.sendProcessMarkdownNotifyBydingtalk(empProxy, appointmentList, userinfo);
                }
            }
            if (userinfo.getNotifyType() == 1) {
                signleNotify = true;
            }
        }
    }

    /**
     * 获取飞书token
     * @param cm
     * @param userInfo
     */
    public static void setFsToken(CacheManager cm, UserInfo userInfo) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String access_token_url = "https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal/";

        String corpid = userInfo.getSecurityID();
        String corpsecret = userInfo.getSecurityKey();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("app_id", corpid);
        map.put("app_secret", corpsecret);

        String jsonBody = HttpClientUtil.postJsonBodyOther(access_token_url, 3000, map, "UTF-8");
        try {
            ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
            JsonNode jsonNode = objectMapper.readValue(jsonBody, JsonNode.class);
            // 如果请求成功
            JsonNode errcode = jsonNode.get("code");
            if (null != errcode && "0".equals(errcode.asText())) {
                cm.putToken("FsToken_" + userInfo.getUserid(), jsonNode.get("tenant_access_token").asText());
            } else {
                System.out.println(dateFormat.format(new Date()) + " 获取accessToken失败: " + jsonBody);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int finishQuestionnaire(Visitor v) {
        // TODO Auto-generated method stub
        return visitorDao.finishQuestionnaire(v);
    }

    @Override
    public List<Visitor> searchLeaveTimeVisitByCondition(Map<String, Object> reqMap) {
        return visitorDao.searchLeaveTimeVisitByCondition(reqMap);
    }

    @Override
    public List<Visitor> getScoreChartByVisitor(RequestVisit requestVisit) {
        return visitorDao.getScoreChartByVisitor(requestVisit);
    }

    @Override
    public String sendCancelSupplementSMS(UserInfo userinfo, Employee emp, Visitor vt) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("message",vt.getVname()+"您好，"+emp.getEmpName()+"暂时无法与您会面，请及时沟通。");
        params.put("phone", vt.getVphone());
        String response = UtilTools.sendSmsByYiMei(params, configureDao, userinfo);

        if ("0".equals(response)) {
            userinfo.setAppSmsCount(userinfo.getAppSmsCount() + 2);
            userDao.updateAppSmsCount(userinfo);
        }
        OperateLog.addSMSLog(operateLogService,userinfo.getUserid(), response, 2,vt.getVphone(),vt.getVname(),params.get("message"));
        return response;
    }

    /**
     * 更新补填信息
     * @param visitor
     */
    @Override
    public void updateSupplementTask(Visitor visitor) {
        LambdaUpdateWrapper<Visitor> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Visitor::getVid,visitor.getVid())
                .set(StringUtils.isNotBlank(visitor.getExtendCol()),Visitor::getExtendCol,visitor.getExtendCol())
                .set(StringUtils.isNotBlank(visitor.getCardId()),Visitor::getCardId,visitor.getCardId())
                .set(StringUtils.isNotBlank(visitor.getVname()),Visitor::getVname,visitor.getVname())
                .set(StringUtils.isNotBlank(visitor.getVphoto()),Visitor::getVphoto,visitor.getVphoto())
                .set(StringUtils.isNotBlank(visitor.getPlateNum()),Visitor::getPlateNum,visitor.getPlateNum())
                .set(StringUtils.isNotBlank(visitor.getRemark()),Visitor::getRemark,visitor.getRemark())
                .set(StringUtils.isNotBlank(visitor.getCompany()),Visitor::getCompany,visitor.getCompany())
                .set(StringUtils.isNotBlank(visitor.getVemail()),Visitor::getVemail,visitor.getVemail())
                .set(StringUtils.isNotBlank(visitor.getHealthDeclaration()),Visitor::getHealthDeclaration,visitor.getHealthDeclaration());
        int ret = visitorDao.update(null,updateWrapper);
        if(ret == 0){
            throw new ErrorException(ErrorEnum.E_057);
        }
    }

    /**
     * 更新status
     * @param visitor
     */
    @Override
    public void updateStatusByVidTask(Visitor visitor){
        LambdaUpdateWrapper<Visitor> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Visitor::getVid,visitor.getVid())
                .set(Visitor::getStatus,visitor.getStatus());
        int ret = visitorDao.update(null,updateWrapper);
        if(ret == 0){
            throw new ErrorException(ErrorEnum.E_063);
        }
    }


    @Override
    public void autoPermission(int vid,int tid,String vType) {
        Visitor v = getVisitorById(vid);
        if(v == null){
            return;
        }
        v.setTid(tid);
        v.setvType(vType);
        v.setPermissionName(v.getEmpName());
        v.setPermission(4);
        updatePermission(v);
    }

    @Override
    public void autopproval(int vid) {
        Visitor v = getVisitorById(vid);
        if(v == null){
            return;
        }
        v.setPermission(1);
        updatePermission(v);
    }

    /**
     * 获取访客默认授权门禁
     * @param userinfo
     * @param gids 授权的门岗
     * @param empid
     * @return access
     */
    @Override
    public String getDefaultAccess(UserInfo userinfo, String gids, int empid) {
        String access = "";

        //员工
        Employee employee = employeeService.getEmployee(empid);
        if (employee != null && StringUtils.isNotEmpty(employee.getVegids())) {
            return employee.getVegids();
        }

        //多企业模式，企业默认访客权限
        if (userinfo != null && userinfo.getSubAccount() == 1
                &&employee != null && employee.getSubaccountId() != 0) {
            SubAccount subAccount = subAccountDao.getSubAccountById(employee.getSubaccountId());
            if (subAccount != null && StringUtils.isNotEmpty(subAccount.getVegids())) {
                return subAccount.getVegids();
            }
        }

        //默认门禁
        EquipmentGroup condition = new EquipmentGroup();
        condition.setUserid(userinfo.getUserid());
        condition.setReqEtype("(0,2)");
        condition.setGids(gids);
        List<EquipmentGroup> equipmentGroups = equipmentGroupService.getEquipmentGroupByUserid(condition);
        if(equipmentGroups != null && equipmentGroups.size()>0) {
            //启用状态的门禁组
            equipmentGroups = equipmentGroups.stream().filter(eg -> eg.getStatus() == 1).collect(Collectors.toList());
            for (EquipmentGroup eg : equipmentGroups) {
                if (com.config.qicool.common.utils.StringUtils.isNotBlank(eg.getGids())) {
                    if (com.config.qicool.common.utils.StringUtils.isNotBlank(access)) {
                        access += ",";
                    }
                    access += eg.getEgid();
                }
            }
        }

        return access;
    }

    @ProcessLogger("一次授权子流程")
    @Override
    public void firstPermissionSubBPM(Visitor visitor, UserInfo userinfo){
        if(userinfo.getPermissionSwitch()==1){
            if(StringUtils.isNotEmpty(Constant.CUSTOM_URL)){
                onVisitAppoinmentNotify(visitor);
            }else {
                messageService.sendAppoinmentPermissionNotify(visitor);
            }
        }else{
            Visitor vt = new Visitor();
            vt.setVid(visitor.getVid());
            vt.setVgroup(visitor.getVid());
            vt.setPermission(PERMISSION_SEC);
            updateGroupPermission(vt);
            //不要授权，进入补填流程
            supplementSubBPM(visitor,userinfo);
        }
    }
    /**
     * 补填子流程，由授权触发
     * @param visitor
     */
    @ProcessLogger("补填子流程")
    @Override
    public void supplementSubBPM(Visitor visitor, UserInfo userinfo){
        int supplementType = supplementTypeRouter(visitor);
        if(supplementType == 2){
            //补填必填
            List<Visitor> vlist = getGroupVistorList(visitor.getVid());
            for(Visitor vt:vlist){
                vt.setStatus(6);
                updateStatusByVidTask(vt);
                messageService.sendCommonNotifyEvent(vt,NotifyEvent.EVENTTYPE_SEND_INVITE);
            }
        }else{
            if(supplementType == 1) {
                //可补填非必填
                List<Visitor> vlist = getGroupVistorList(visitor.getVid());
                for (Visitor vt : vlist) {
                    vt.setStatus(6);
                    updateStatusByVidTask(vt);
                }
            }
            //不必填,跳过二次授权流程，直接审批流程
            approvalSubBPM(visitor,true);
        }

    }

    /**
     * 审批子流程
     * @param visitor
     */
    @ProcessLogger("审批子流程流程")
    @Override
    public void approvalSubBPM(Visitor visitor,boolean group){
        UserInfo userinfo = userDao.getUserInfo(visitor.getUserid());
        if(group){

            if (userinfo.getProcessSwitch() == 0) {
                //不要审批，自动完成审批
                List<Visitor> vlist = getGroupVistorList(visitor.getVid());
                for(Visitor vt:vlist){
                    vt.setPermission(1);
                    updatePermission(vt);
                }
            } else {
                List<String> allVisitors = new ArrayList<>();
                List<Visitor> vlist = getGroupVistorList(visitor.getVid());
                for(Visitor vt:vlist){
                    vt.setPermission(4);
                    updatePermission(vt);
                    allVisitors.add(vt.getVid()+"");
                }
//                //TODO 开始camunda审批流程,有前端发起
//                Employee employee = employeeService.getEmployee(visitor.getEmpid());
//                Camunda camunda = new Camunda();
//                camunda.setTitle("访客邀请审批");
//                camunda.setBusinessKey("v"+visitor.getVgroup());
//                //查找领导
//                camunda.setLeader(visitor.getExtendValue("leader"));
//                camunda.setAllVisitors(allVisitors);
//                int ret = camundaProcessService.createApproveProcess(userinfo,employee,camunda);
//                if(ret != 0) {
//                    throw new ErrorException(ErrorEnum.getByCode(ret));
//                }
            }
        }else {
            if (userinfo.getProcessSwitch() == 0) {
                //不要审批，自动完成审批
                visitor.setPermission(1);
                updatePermission(visitor);
            } else {
                SysLog.info("等待被访人提交审批,vid：" + visitor.getVid());
                visitor.setPermission(4);
                updatePermission(visitor);

//                Employee employee = employeeService.getEmployee(visitor.getEmpid());
//                Camunda camunda = new Camunda();
//                camunda.setTitle("访客邀请审批");
//                camunda.setBusinessKey("v"+visitor.getVgroup());
//                //查找领导
//                camunda.setLeader(visitor.getExtendValue("leader"));
//                List<String> allVisitors = new ArrayList<>();
//                allVisitors.add(visitor.getVid()+"");
//                camunda.setAllVisitors(allVisitors);
//                int ret = camundaProcessService.createApproveProcess(userinfo,employee,camunda);
//                if(ret != 0) {
//                    throw new ErrorException(ErrorEnum.getByCode(ret));
//                }
            }
        }

    }

    /**
     * 补填授权子流程
     * @param visitor
     * @param userinfo
     */
    @ProcessLogger("补填授权子流程")
    @Override
    public void supplementPermissonSubBPM(Visitor visitor, UserInfo userinfo){
        if ("1".equalsIgnoreCase(userinfo.getAppointmenProcessSwitch())) {
            //需要二次授权

            //通知被访人
            messageService.sendSupplementPermissionNotifyEvent(visitor);
        } else {
            //不需要二次授权,进入审批流程
            approvalSubBPM(visitor,false);
        }
    }

    /**
     * 补填授权，1：同意，2：拒绝
     * @param visitor
     */
    @Override
    public void completeSupplementTask(Visitor visitor){
        int ret = updatePermission(visitor);
        if(ret == 0){
            throw new ErrorException(ErrorEnum.E_057);
        }
    }



    /**
     * 补填类型
     * @param visitor
     * @return 0不要补填 1要补填不必填 2.补填并必填
     */
    @Override
    public int supplementTypeRouter(Visitor visitor){
        int supplementType = 0;
        ExtendVisitor ev = new ExtendVisitor();
        ev.setUserid(visitor.getUserid());
        ev.seteType(visitor.getvType());
        List<ExtendVisitor> evlist = extendVisitorService.getExtendVisitorByType(ev);
        if(evlist != null) {
            for (ExtendVisitor ex:evlist) {
                if ((ex.getRequired() & 32) == 32) {
                    supplementType= 2;
                    break;
                }
                if ((ex.getIsDisplay() & 32) == 32) {
                    supplementType=1;
                }

            }
        }
        return supplementType;
    }


    @Override
    public List<Visitor> getVisitorByVgroup(String vGroup){
        LambdaQueryWrapper<Visitor> lambdaQueryWrapper = Wrappers.lambdaQuery(Visitor.class);
        lambdaQueryWrapper.eq(Visitor::getVgroup,vGroup);

        return list(lambdaQueryWrapper);
    }

    /**
     * 第三方获取访客数据
     * @param rv
     * @return
     */
    @Override
    public List<RespVisitor> searchVisitors(RequestVisit rv) {
        return visitorDao.searchVisitors(rv);
    }

    /**
     * 通知第三方服务器有访客预约
     */
    private void onVisitAppoinmentNotify(Visitor visitor){
        try {
            VisitorUtils.sendApprovalButt(visitor,Constant.CUSTOM_URL);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int batchUpdateExtendCol(List<Visitor> atlist){
        return visitorDao.batchUpdateExtendCol(atlist);
    }

    @Override
    public int VisitorReply(Visitor vt) {
        return visitorDao.VisitorReply(vt);
    }
}


