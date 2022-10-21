package compileflow.stateless.bpm;

import com.alibaba.compileflow.engine.process.preruntime.generator.bean.BeanProvider;
import com.alibaba.compileflow.engine.runtime.instance.ProcessInstance;
import com.client.bean.Visitor;
import com.client.service.VisitorService;
import com.web.service.ResidentVisitorService;
import java.util.HashMap;
import com.alibaba.compileflow.engine.ProcessEngineFactory;
import com.web.bean.ResidentVisitor;
import com.client.bean.Equipment;
import com.client.service.EquipmentService;
import com.web.bean.UserInfo;
import com.web.bean.Appointment;
import java.lang.Boolean;
import com.utils.BeanUtils;
import com.web.bean.Employee;
import com.web.service.AppointmentService;
import java.lang.Integer;
import java.lang.String;
import java.util.Map;
import com.alibaba.compileflow.engine.common.utils.DataType;
import com.web.service.UserService;
import com.alibaba.compileflow.engine.common.utils.ObjectFactory;

public class CheckQrcodeFlow implements ProcessInstance {

    private java.lang.String qrcode = "";
    private java.lang.String devicenumber = "";
    private java.lang.String id = "";
    private com.client.bean.Visitor vt = null;
    private com.web.bean.Appointment app = null;
    private java.lang.String eType = "";
    private com.web.bean.Employee employee = null;
    private com.client.bean.Equipment eq = null;
    private com.web.bean.UserInfo userinfo = null;
    private java.lang.String qrcodeType = "";
    private com.web.bean.ResidentVisitor rv = null;
    private java.lang.Boolean isEnter = null;

    public Map<String, Object> execute(Map<String, Object> _pContext) throws Exception {
        qrcode = (String)DataType.transfer(_pContext.get("qrcode"), String.class);
        devicenumber = (String)DataType.transfer(_pContext.get("devicenumber"), String.class);
        id = (String)DataType.transfer(_pContext.get("id"), String.class);
        Map<String, Object> _pResult = new HashMap<>();
        //AutoTaskNode: 检查二维码有效期
        ((VisitorService)BeanProvider.getBean("visitorService")).checkQrcodeExpiredDateTask(qrcode);
        //DecisionNode: 二维码类型判断
        visitorServiceGetEType();
        if (eType.equals("01")) {
            //员工二维码
            //AutoTaskNode: 检查员工有效性
            employee = ((VisitorService)BeanProvider.getBean("visitorService")).checkEmployeeTask((String)DataType.transfer(qrcode.substring(44), String.class));
            //AutoTaskNode: 检查员工门禁权限
            eq = ((VisitorService)BeanProvider.getBean("visitorService")).checkEquipmentTask((String)DataType.transfer(employee.getEgids(), String.class), devicenumber);
        } else if (eType.equals("02")) {
            //访客
            //DecisionNode: 预约方式
            visitorServiceGetQrcodeType();
            if (qrcodeType.equals("25")) {
                //预约码
                //AutoTaskNode: 获取预约访客
                vt = ((VisitorService)BeanProvider.getBean("visitorService")).checkVisitorPermissionTask(id);
            } else if (qrcodeType.equals("40")) {
                //签到二维码
                //AutoTaskNode: 获取签到访客
                vt = ((VisitorService)BeanProvider.getBean("visitorService")).checkVisitorPermissionTask(id);
            } else if (qrcodeType.equals("23")) {
                //邀请码
                //AutoTaskNode: 获取邀请访客
                app = ((AppointmentService)BeanProvider.getBean("appointmentService")).checkAppointmentPermissionTask(id);
                //AutoTaskNode: appToVt
                vt = ((BeanUtils)ObjectFactory.getInstance("com.utils.BeanUtils")).appointmentToVisitor(app);
            }
            //AutoTaskNode: getUserInfo
            userinfo = ((UserService)BeanProvider.getBean("userService")).getUserInfoByUserId((Integer)DataType.transfer(vt.getUserid(), Integer.class));
            //AutoTaskNode: 检查访客时间
            ((VisitorService)BeanProvider.getBean("visitorService")).checkVisitorAccessTimeTask(userinfo);
            //AutoTaskNode: 检查访客门禁权限
            eq = ((VisitorService)BeanProvider.getBean("visitorService")).checkEquipmentTask((String)DataType.transfer(vt.getEgids(), String.class), devicenumber);
            //AutoTaskNode: 检查二维码进出权限
            ((VisitorService)BeanProvider.getBean("visitorService")).checkAccessTypeTask(userinfo, (String)DataType.transfer(vt.getvType(), String.class), "1");
            //AutoTaskNode: 检查是否允许签到码进出
            ((VisitorService)BeanProvider.getBean("visitorService")).checkAppCodeAccessTask(qrcodeType);
            //DecisionNode: 门禁方向判断
            equipmentServiceIsEnterRouter();
            if (isEnter == true) {
                //进门
                //AutoTaskNode: 是否能未签到进门
                ((VisitorService)BeanProvider.getBean("visitorService")).checkSigninStatusTask(vt);
                //AutoTaskNode: 检查多次进出权限
                ((VisitorService)BeanProvider.getBean("visitorService")).checkTimesTask(userinfo, (String)DataType.transfer(vt.getvType(), String.class), (String)DataType.transfer(vt.getVphone(), String.class));
                //AutoTaskNode: 是否已经结束拜访
                ((VisitorService)BeanProvider.getBean("visitorService")).checkLeaveStatusTask(vt);
                //AutoTaskNode: 是否已经签离
                ((VisitorService)BeanProvider.getBean("visitorService")).checkSignoutStatusTask(vt);
                //SubBpmNode: 签到
                {
                    Map<String, Object> _subBpmContext = new HashMap<>();
                    _subBpmContext.put("vtParam", vt);
                    _subBpmContext.put("userinfo", userinfo);
                    ProcessEngineFactory.getProcessEngine().start("bpm.autoCheckin", _subBpmContext);
                }
                //AutoTaskNode: 通知senselink访客通行事件
                ((VisitorService)BeanProvider.getBean("visitorService")).sendVisitorPassMsgTask(userinfo, eq, vt);
            } else {
                //出门
                //SubBpmNode: 签出
                {
                    Map<String, Object> _subBpmContext = new HashMap<>();
                    _subBpmContext.put("vt", vt);
                    _subBpmContext.put("eq", eq);
                    _subBpmContext.put("userinfo", userinfo);
                    ProcessEngineFactory.getProcessEngine().start("bpm.autoCheckout", _subBpmContext);
                }
                //AutoTaskNode: 通知senselink访客通行事件
            }
        } else if (eType.equals("03")) {
            //供应商
            //AutoTaskNode: 检查供应商有效性
            rv = ((ResidentVisitorService)BeanProvider.getBean("residentVisitorService")).checkResidentVisitorTask((String)DataType.transfer(qrcode.substring(44), String.class));
            //AutoTaskNode: 检查供应商门禁权限
            eq = ((VisitorService)BeanProvider.getBean("visitorService")).checkEquipmentTask((String)DataType.transfer(rv.getEgids(), String.class), devicenumber);
            //AutoTaskNode: 检查供应商签到
            ((BeanUtils)ObjectFactory.getInstance("com.utils.BeanUtils")).doNothing();
        }
        _pResult.put("vt", vt);
        _pResult.put("app", app);
        _pResult.put("eType", eType);
        _pResult.put("employee", employee);
        _pResult.put("eq", eq);
        _pResult.put("userinfo", userinfo);
        _pResult.put("qrcodeType", qrcodeType);
        _pResult.put("rv", rv);
        return _pResult;
    }

    private void visitorServiceGetEType() {
        eType = ((VisitorService)BeanProvider.getBean("visitorService")).getEType(qrcode);
    }

    private void visitorServiceGetQrcodeType() {
        qrcodeType = ((VisitorService)BeanProvider.getBean("visitorService")).getQrcodeType(qrcode);
    }

    private void equipmentServiceIsEnterRouter() {
        isEnter = ((EquipmentService)BeanProvider.getBean("equipmentService")).isEnterRouter(eq);
    }

}