package com.web.controller;

import com.client.bean.OpendoorInfo;
import com.client.bean.RespVisitor;
import com.client.bean.Visitor;
import com.client.service.EquipmentGroupService;
import com.client.service.OpendoorService;
import com.client.service.VisitorService;
import com.config.qicool.common.utils.StringUtils;
import com.utils.AESUtil;
import com.utils.Constant;
import com.utils.FileUtils.DelFileThread;
import com.utils.SysLog;
import com.web.bean.*;
import com.web.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
@Component
public class AutoGetRtxInfo {

    @Autowired
    private UserService userService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private EquipmentGroupService equipmentGroupService;

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private PersonInfoService personInfoService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OperateLogService operateLogService;

    @Autowired
    private OpendoorService opendoorService;


    private int userid = 2147483647;


    /**
     * 自动同步员工
     */
    @Scheduled(cron = "30 33 23 * * ?")
    public void autoGetEmployee() {
        if(!Constant.IS_MASTER.equals("1")){
            return;
        }
        SysLog.info("开始自动同步员工");
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
            }else if(StringUtils.isNotEmpty(userInfo.getSecurityID())
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
        SysLog.info("自动同步员工完成");
    }


    /**
     * 定时任务：定时删除x个月以前的数据
     */
    @Scheduled(cron = "30 40 23 * * ?")
    public void autoDelExpireResource() {
        if(!Constant.IS_MASTER.equals("1")){
            return;
        }
        //获取所有用户数据
        ReqUserInfo reqinfo = new ReqUserInfo();
        List<UserInfo> userInfoList = userService.getAllUserinfo(reqinfo);
        for(UserInfo user:userInfoList) {
            if(user.getDataKeepTime()==0){
                continue;
            }
            int userid = user.getUserid();
            int x=0-user.getDataKeepTime();//保留数据时间
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.MONTH, x);
            String expireDate = dateFormat.format(calendar.getTime());
            SysLog.info("开始删除"+user.getUserPrintInfo()+expireDate+"之前数据");

            List<Visitor> expireVisitorList = visitorService.getVisitorByexpireDate(userid, expireDate);
            List<String> expireVphotos = null;
            List<String> expireAppointmentPhoto = null;
            // TODO: 2020/5/7 批量删除访客记录/访客头像资源文件
            if (expireVisitorList.size() > 0) {
                expireVphotos = expireVisitorList.stream()
                        .filter(visitor -> StringUtils.isNotEmpty(visitor.getVphoto()))
                        .map(Visitor::getVphoto).collect(Collectors.toList());

                List<Integer> expireVid = expireVisitorList.stream().map(visitor -> visitor.getVid()).collect(Collectors.toList());
                int v_resultCount = 0;
                if (expireVid.size() > 0) {
                    v_resultCount = visitorService.deleteVisitByVids(expireVid);
                }
                SysLog.info("删除"+user.getUserPrintInfo()+expireDate+"之前预约数据,共删除" + v_resultCount + "条访客记录");

                //添加删除日志
                OperateLog log = new OperateLog();
                log.setUserid(userid)
                .setOptRole(OperateLog.ROLE_AUTO)
                .setObjId("")
                .setObjName("签到数据")
                .setoTime(new Date())
                .setOptEvent("删除")
                .setOptModule(OperateLog.MODULE_VISITOR)
                .setOptDesc("删除"+expireDate+"之前预约数据,共删除" + v_resultCount + "条访客记录");
                operateLogService.addLog(log);
            }

            // TODO: 2020/5/7 批量删除访客邀请记录
            List<Appointment> appointmentList = appointmentService.getAppointmentByexpireDate(userid, expireDate);
            if (appointmentList.size() > 0) {
                expireAppointmentPhoto = appointmentList.stream()
                        .filter(appointment -> StringUtils.isNotEmpty(appointment.getPhotoUrl()) && appointment.getId() > 0)
                        .map(appointment -> appointment.getPhotoUrl())
                        .collect(Collectors.toList());


                List<Integer> appointmentIdList = appointmentList.stream().filter(appointment -> appointment.getId() > 0)
                        .map(appointment -> appointment.getId()).collect(Collectors.toList());

                int a_resultCount = 0;
                if (appointmentIdList.size() > 0) {
                    a_resultCount = appointmentService.deleteAppointmentByids(appointmentIdList);
                }
                SysLog.info("删除"+user.getUserPrintInfo()+expireDate+"之前邀请数据,共删除" + a_resultCount + "条访客预约记录");

                //添加删除日志
                OperateLog log = new OperateLog();
                log.setUserid(userid)
                .setOptRole(OperateLog.ROLE_AUTO)
                .setObjId("")
                .setObjName("邀请数据")
                .setoTime(new Date())
                .setOptEvent("删除")
                .setOptModule(OperateLog.MODULE_VISITOR)
                .setOptDesc("删除"+expireDate+"之前预约数据,共删除" + a_resultCount + "条访客记录");
                operateLogService.addLog(log);
            }

            //删除照片文件
            new Thread(new DelFileThread(expireVphotos,personInfoService,expireVisitorList)).start();
            new Thread(new DelFileThread(expireAppointmentPhoto,personInfoService)).start();
        }

    }

//    static class DelVisitorFileThread<T> implements Runnable {
//        private List<T> object;
//
//        public DelVisitorFileThread(List<T> object) {
//            this.object = object;
//        }
//
//        @Override
//        public void run() {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            System.out.println(dateFormat.format(new Date()) + " 开始删除图像资源。");
//            for (Object obj : object) {
//                if (obj == null) {
//                    return;
//                } else {
//                    try {
//                        // TODO: 2020/4/24 删除文件操作
//                        String fastdfsUrl = Constant.FASTDFS_URL;
//                        //String fastdfsUrl = "http://www.coolvisit.top/";
//                        String avatar = String.valueOf(obj);
//                        String group = avatar.substring(fastdfsUrl.length()).split("/", 2)[0];
//                        String storagePath = avatar.substring(fastdfsUrl.length()).split("/", 2)[1];
//                        FileUtils.delete_file(group, storagePath);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            System.out.println(dateFormat.format(new Date()) + " 图像资源删除完成。");
//        }
//    }
    
    
    /**
     * 发送满意度调查
     * 自动在拜访结束的第二天9点（多天为最后一天），推送满意度问卷（短信）
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void autoSendSatisfactionQuestionnaire(){
        if(!Constant.IS_MASTER.equals("1")){
            return;
        }
        ReqUserInfo reqinfo = new ReqUserInfo();
        List<UserInfo> userInfoList = userService.getAllUserinfo(reqinfo);
        for(UserInfo user:userInfoList) {
            UserInfo ui = userService.getUserInfo(user.getUserid());
            if (null == ui || ui.getSatisfactionQuestionnaire().equals("0")) {
                //关闭了开关
                continue;
            } else {
                //已经迁离
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date = sdf.format(new Date());
                Map<String, Object> reqMap = new HashMap<String, Object>() {
                    {
                        put("date", date);
                        put("userid", ui.getUserid());
                    }
                };
                List<Visitor> respVisitors = visitorService.searchLeaveTimeVisitByCondition(reqMap);
                if (null != respVisitors && respVisitors.size() > 0) {
                    for (Visitor visitor : respVisitors) {

                        //发送满意度调查链接
                        StringBuffer msg = new StringBuffer();
                        msg.append("尊敬的先生/女士，感谢莅临" + ui.getCardText() + "，我们非常渴望您能从中抽取一点时间填写此链接中的意见反馈，"
                                + "这将促进我们今后的接待工作更加细致，服务水准不断提升。");
                        msg.append(Constant.FASTDFS_URL + "Satisfaction.html");
                        String secid = AESUtil.encode(visitor.getVid() + "", Constant.AES_KEY);
                        msg.append("?secid=" + secid + " 。戴口罩，勤洗手，疫情当前，众志成城。");

                        Map<String, String> params = new HashMap<String, String>() {
                            {
                                put("message", msg.toString());
                                put("phone", visitor.getVphone());
                            }
                        };
                        String response = visitorService.sendSmsReply(params, ui,3);
                        OperateLog.addSMSLog(operateLogService,ui.getUserid(), response, 3,visitor.getVphone(),visitor.getVname(),params.get("message"));

                    }
                }
            }
        }
    }

    /**
     * 访客系统通行时刻表设置提醒
     */
//    @Scheduled(cron = "0 30 8 * * ?")
//    public void autoNotifyTimeTable() {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
//        Map<String, String> dateMap = new HashMap<>();
//        dateMap.put("12-05", "");
//        dateMap.put("12-15", "");
//        dateMap.put("12-25", "");
////        dateMap.put("08-01", "");
//        UserInfo userinfo = userService.getUserInfoWithExt("visitor@coolvisit.com");
//        if (null != userinfo && dateMap.containsKey(dateFormat.format(new Date()))) {
//            Manager manager = new Manager();
//            manager.setUserid(userinfo.getUserid());
//            List<Integer> stList = new ArrayList<>();
//            stList.add(5);
//            manager.setStList(stList);
//            List<Manager> managerList = managerService.getManagerList(manager);
//            List<String> emailList = new ArrayList<>();
//            emailList.add(userinfo.getEmail());
//
//            for (int i = 0; i < managerList.size(); i++) {
//                String regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
//                Pattern p = Pattern.compile(regEx1);
//                Matcher m = p.matcher(managerList.get(i).getAccount());
//                if (m.matches()) {
//                    System.out.println("邮箱校验通过。。。" + managerList.get(i).getAccount());
//                    emailList.add(managerList.get(i).getAccount());
//                }
//            }
//            String msg = "您的访客通行时刻表即将到期，请尽快设置下一年的通行时刻表，您可通过登录访客系统管理员端——点击策略管理——点击通行时刻表设置，谢谢配合。";
//            //获取JavaMailSender bean
//            MimeMessage mailMessage = null;
//            JavaMailSenderImpl mailSender = null;
//            if (userinfo.getEmailType() == 3) {
//                mailMessage = (MimeMessage) UtilTools.getEmailConf("smtp.ym.163.com", "994", "service@coolvisit.com", "ZoneZone8006");
//                userinfo.setEmailAccount("service@coolvisit.com");
//            } else if (StringUtils.isNotBlank(userinfo.getEmailPwd()) && userinfo.getSmtpPort() != 25) {
//                mailMessage = (MimeMessage) UtilTools.getEmailConf(userinfo.getSmtp(), userinfo.getSmtpPort() + "", userinfo.getEmailAccount(), userinfo.getEmailPwd());
//            } else if (StringUtils.isNotBlank(userinfo.getEmailPwd()) && userinfo.getSmtpPort() == 25) {
//                mailSender = UtilTools.getEmailConf(userinfo.getSmtp(), userinfo.getSmtpPort(), userinfo.getEmailAccount(), userinfo.getEmailPwd());
//                mailMessage = mailSender.createMimeMessage();
//            } else {
//                mailMessage = (MimeMessage) UtilTools.getNoAuthEmailConf(userinfo.getSmtp(), userinfo.getSmtpPort(), userinfo.getEmailAccount());
//            }
//            String[] emailArr = emailList.toArray(new String[emailList.size()]);
//            try {
//                MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true, "utf-8");
//                messageHelper.setTo(emailArr);
//                messageHelper.setFrom(userinfo.getEmailAccount());
//                messageHelper.setSubject("访客系统通行时刻表设置提醒");
//                messageHelper.setText(msg, true);
//                if(userinfo.getSmtpPort()==25&&StringUtils.isNotBlank(userinfo.getEmailPwd())){
//                    mailSender.send(mailMessage);
//                }else{
//                    Transport.send(mailMessage);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    @Scheduled(cron = "*/5 * * * * ?")
//    public void  test(){
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        System.out.println(dateFormat.format(new Date())+"定时器执行了。。。。。。");
//    }

    /**
     * 自动签离
     */
    @Scheduled(cron = "10 58 23 * * ?")
    public void autoSignOut() {

        UserInfo ui = userService.selectByName("visitor@coolvisit.com");
        Visitor vt = new Visitor();
        vt.setUserid(ui.getUserid());
        List<RespVisitor> rvList = visitorService.checkSignOutRecords(vt);
        Date d = new Date();
        OpendoorInfo oi = new OpendoorInfo();
        Visitor v = new Visitor();
        for (int i = 0; i < rvList.size(); i++) {
            if (null == rvList.get(i).getSignOutDate()) {
                oi.setOpenDate(d);
                oi.setUserid(ui.getUserid());
                oi.setMobile(rvList.get(i).getVphone());
                oi = opendoorService.getLastRecords(oi);
                if (null == oi) {
                    v.setSignOutDate(new Date());
                    oi = new OpendoorInfo();
                    oi.setMobile("");
                } else {
                    v.setSignOutDate(oi.getOpenDate());
                }
                System.out.println("签离人员：" + rvList.get(i).getVid() + "-" + oi.getMobile());
                v.setVphone(oi.getMobile());
                v.setUserid(ui.getUserid());

                v.setSignOutOpName("");
                v.setSignOutGate("");

                visitorService.updateSignOut(v);

            }
        }
    }
}
