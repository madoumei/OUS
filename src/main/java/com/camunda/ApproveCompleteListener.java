package com.camunda;

import com.client.bean.Visitor;
import com.client.service.VisitorService;
import com.event.event.NotifyEvent;
import com.utils.BeanUtils;
import com.utils.SysLog;
import com.web.bean.Appointment;
import com.web.bean.Employee;
import com.web.bean.UserInfo;
import com.web.service.AppointmentService;
import com.web.service.EmployeeService;
import com.web.service.MessageService;
import com.web.service.UserService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ApproveCompleteListener implements ExecutionListener {
    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private MessageService messageService;

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        SysLog.info("ApproveCompleteListener notify");
        int status = (int) execution.getVariable("status");//0：审批中，1：同意，2：拒绝，3：撤回
        int userid = Integer.parseInt((String) execution.getVariable("userid"));
        int empid = Integer.parseInt((String) execution.getVariable("employee"));
        UserInfo userinfo = userService.getUserInfo(userid);
        String businessKey = execution.getBusinessKey();
        Set<String> variableNames = execution.getVariableNames();
        List<String> allVisitors = (List<String>) execution.getVariable("allVisitors");
        List<String> delVisitors = (List<String>) execution.getVariable("delVisitors");
        Employee employee = employeeService.getEmployee(empid);
        int index = 0;
        if(status == 2){
            //取消
            if (businessKey.startsWith("a")) {
                for(String id:allVisitors){
                    Appointment app = appointmentService.getAppointmentbyId(Integer.parseInt(id));
                    if(app != null && app.getPermission() == 4 && app.getStatus() != 4){
                        app.setPermission(3);
                        appointmentService.updateAppointmentPermission(app);
                    }
                    if(index++==0){
                        //通知员工审批被拒绝
                        messageService.sendCommonNotifyEvent(BeanUtils.appointmentToVisitor(app), NotifyEvent.EVENTTYPE_APPROVE_REJECT);
                    }
                }


            }else{
                for(String id:allVisitors){
                    Visitor visitor = visitorService.getVisitorById(Integer.parseInt(id));
                    if(visitor != null && visitor.getPermission() == 4){
                        visitor.setPermission(3);
                        visitorService.updatePermission(visitor);
                    }
                    if(index++==0){
                        //通知员工审批被拒绝
                        messageService.sendCommonNotifyEvent(visitor, NotifyEvent.EVENTTYPE_APPROVE_REJECT);
                    }
                }

            }

        }else if(status == 1){
            //确定
            if (businessKey.startsWith("a")) {
                if(null != delVisitors) {
                    for (String id : delVisitors) {
                        Appointment app = appointmentService.getAppointmentbyId(Integer.parseInt(id));
                        if (app != null && app.getPermission() == 4 && app.getStatus() != 4) {
                            app.setPermission(3);
                            appointmentService.updateAppointmentPermission(app);
                        }
                    }
                }
                if(null != allVisitors) {
                    for (String id : allVisitors) {
                        Appointment app = appointmentService.getAppointmentbyId(Integer.parseInt(id));
                        if (app != null && app.getPermission() == 4 && app.getStatus() != 4) {
                            app.setPermission(1);
                            appointmentService.updateAppointmentPermission(app);
                        }
                        if(index++==0){
                            //通知员工审批通过
                            messageService.sendCommonNotifyEvent(BeanUtils.appointmentToVisitor(app), NotifyEvent.EVENTTYPE_APPROVE_FINISH);
                        }
                    }
                }
            }else{
                if(null != delVisitors) {
                    for (String id : delVisitors) {
                        Visitor visitor = visitorService.getVisitorById(Integer.parseInt(id));
                        if (visitor != null && visitor.getPermission() == 4) {
                            visitor.setPermission(3);
                            visitorService.updatePermission(visitor);
                        }
                    }
                }

                if(null != allVisitors) {
                    for (String id : allVisitors) {
                        Visitor visitor = visitorService.getVisitorById(Integer.parseInt(id));
                        if (visitor != null && visitor.getPermission() == 4) {
                            visitor.setPermission(1);
                            visitorService.updatePermission(visitor);
                        }
                        if(index++==0){
                            //通知员工审批通过
                            messageService.sendCommonNotifyEvent(visitor, NotifyEvent.EVENTTYPE_APPROVE_FINISH);
                        }
                    }
                }

            }
        }



    }
}
