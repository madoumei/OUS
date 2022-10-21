package com;

import com.client.bean.Equipment;
import com.client.bean.Visitor;
import com.client.service.*;
import com.config.qicool.common.utils.SpringContextHolder;
import com.config.qicool.common.utils.StringUtils;
import com.event.event.NotifyEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utils.*;
import com.utils.jsonUtils.JacksonJsonUtil;
import com.web.bean.*;
import com.web.controller.AutoGetRtxInfo;
import com.web.dao.ConfigureDao;
import com.web.dao.PassRuleDao;
import com.web.service.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.text.SimpleDateFormat;
import java.util.*;


@SpringBootTest
@ActiveProfiles("dev") //指定profiles
class DemoApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private UserService userService;

    @Autowired
    private AutoGetRtxInfo autoGetRtxInfo;


    private Environment environment;

    @Autowired
    private PersonInfoService personInfoService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EquipmentGroupService equipmentGroupService;

    @Autowired
    private OperateLogService operateLogService;

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private MessageService messageService;
    @Autowired
    private TrackService trackService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private ConfigureDao configureDao;

    @Autowired
    private HolidayService holidayService;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PassRuleService passRuleService;

    @Autowired
    private EqptRuleService eqptRuleService;
//    @Test
//    void checkAutoGetRtxInfo(){
//        List<EquipmentGroup> equipmentGroups = equipmentGroupService.getEquipmentGroupListByGid(2147483647, 2);
//        System.out.println("get "+equipmentGroups.size());
//    }

    @Test
    void getSecid() {
        String url = Constant.FASTDFS_URL + "bus.html?id=" + AESUtil.encode( "6864", Constant.AES_KEY);
        System.out.println(url);

    }

    @Test
    void getToken() {
        HashOperations hashOperations = redisTemplate.opsForHash();
        if(hashOperations.get("13813946371", "token") != null){
            String token = (String) hashOperations.get("13813946371", "token");
            System.out.println("token = "+token);
        }
        System.out.println(" success");
    }

    @Test
    void getDefaultAccess() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserid(2147483647);
        userInfo.setSubAccount(1);
        String gids = "1";
        int empid = 1;
        String defaultAccess = visitorService.getDefaultAccess(userInfo, gids, empid);
        System.out.println(defaultAccess);

    }

    @Test
    void getvid() {
        int  aid=Integer.parseInt(AESUtil.decode("745FEEFF7E1AAA18306BEBD7C475029E", Constant.AES_KEY));
        System.out.println(aid);
    }

    @Test
    void getpwd() {
        String base64 = "2979e0bbd0f161457c28637579084560";
        String front = base64.substring(0, 5);
        base64 = front + base64.substring(10);
        String pwd = Encodes.decodeBase64String(base64);
        pwd = pwd.substring(0, 3) + pwd.substring(8);
        String pwd2 = MD5.crypt(pwd);
        System.out.println(pwd);
    }

 @Test
    void autoSync() {

     //检查当前需要同步的企业
     List<UserInfo> allUser = userService.getUserInfoList();
     for(UserInfo userInfo:allUser) {
         RespInfo respInfo = null;
         String empType = "";//同步数据源类型
         if(StringUtils.isNotEmpty(userInfo.getCorpid())
                 && StringUtils.isNotEmpty(userInfo.getCorpsecret())){
             respInfo = employeeService.ImportEmpsByWechart(userInfo);
             empType = "企业微信";
         }
         else if(StringUtils.isNotEmpty(userInfo.getDdagentid())
                 && StringUtils.isNotEmpty(userInfo.getDdAppid())
                 && StringUtils.isNotEmpty(userInfo.getDdAppSccessSecret())){
             respInfo = employeeService.ImportEmpsByDD(userInfo);
             empType = "钉钉";
         }else
             if(StringUtils.isNotEmpty(userInfo.getSecurityID())
                 && StringUtils.isNotEmpty(userInfo.getSecurityKey())){
             respInfo = employeeService.ImportEmpsByFeishu(userInfo);
             empType = "飞书";
         }else{
             continue;
         }

         String result = "0";
         if (respInfo.getStatus() == 0) {
             result = String.valueOf(respInfo.getResult());
         }

         //添加日志
         OperateLog log = new OperateLog();
         log.setUserid(userInfo.getUserid())
                 .setOptRole(OperateLog.ROLE_AUTO)
                 .setObjId("")
                 .setObjName("员工")
                 .setoTime(new Date())
                 .setOptEvent(empType+"人员同步")
                 .setOptModule(OperateLog.MODULE_EMPLOYEE)
                 .setOptDesc("同步员工" + result + "人");
         operateLogService.addLog(log);

     }
     System.out.println("sync success");
    }


    @Test
    void sendMsg() {
        UserInfo userInfo = userService.getUserInfo(2147483647);
        Map<String,String> param = new HashMap<String,String>();
        param.put("phone","11234234234");
        param.put("message","你好");
        String ret = UtilTools.sendSmsByYiMei(param,configureDao,userInfo);
        System.out.println("send msg "+ret);

    }

    @Test
    void checkEquipmentRuleTask() {
        Equipment equipment = new Equipment();
        equipment.setEid(34);
        visitorService.checkEquipmentRuleTask(equipment);

        System.out.println("send msg ");

    }

    @Test
    void getHoliday() {
        ReqHd reqHd = new ReqHd();
        reqHd.setUserid(2147483647);
        List<Holiday> holidayList = holidayService.getHoliday(reqHd);

        PassRule pr = new PassRule();
        pr.setRid(Integer.parseInt("26"));
        pr.setUserid(2147483647);
        List<PassRule> passRuleList = passRuleService.getPassRuleList(pr);

        System.out.println("holidayList "+holidayList.size());

    }

    @Test
    void updateOnlineStatus() {
        Equipment equipment = new Equipment();
        equipment.setUserid(2147483647);
        equipment.setDeviceCode("SP343333");
        equipment.setDeviceName("SP343333");
        equipment.setOnlineStatus(1);
        equipmentService.updateOnlineStatus(equipment);

        System.out.println("holidayList ");

    }

    @Test
    void sendMsg2() {
            String s1="+86-1231231286";
            String s2="1231231286";
            String s3="86+1231231286";
            System.out.println(s2.replaceFirst("^{+}*86{+-}}",""));
            System.out.println(s2.replaceFirst("86",""));
            System.out.println(s3.replaceFirst("^86",""));
            System.out.println(s1.replaceFirst("^+86",""));
//        Appointment app = appointmentService.getAppointmentbyId(2718);
//
//        messageService.sendCommonNotifyEvent(BeanUtils.appointmentToVisitor(app), NotifyEvent.EVENTTYPE_VISITOR_NO_LEAVE);
//

    }

    @Test
    void getQrcode() {
        StringBuffer sb=new StringBuffer();
        sb.append("468");
        sb.append("02");
        sb.append("25");
        sb.append("28965874589");
        SimpleDateFormat sdf=new SimpleDateFormat("yyMMddHHmm");
        String date2=sdf.format(new Date().getTime()+300000L);
        sb.append(date2);
        sb.append("1211");
        String qrcode=sb.toString();
        String aes="v"+ DigestUtils.sha512Hex(qrcode+"csjm759").substring(8, 23);
        sb.insert(0,aes);
        System.out.println("getQrcode "+sb.toString());

    }


    @Test
    void getlist(){
        List<EqptRule> list = eqptRuleService.getList(2147483647, 34);
        for(EqptRule rule:list){
            SysLog.info("rule "+rule.getRname());
        }
    }

    @Test
    void getLock(){
        RedisUtil redisUtil = SpringContextHolder.getBean(RedisUtil.class);
//        redisUtil.releaseLock("lock_ip_" + "0.0.0.0.0.1", "0.0.0.0.0.1");
        boolean lock = redisUtil.getLock("lock_ip_" + "0.0.0.0.0.1", "0.0.0.0.0.1", 60);
        SysLog.info("getlock "+lock);
        while (true){
            try {
                Thread.sleep(10*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            redisUtil.hasKey("lock_ip_" + "0.0.0.0.0.1");
        }
    }


    @Test
    void readLog(){
        SysLog.readTxtFile("/Users/imac7/Downloads/catalina.out");
    }


}
