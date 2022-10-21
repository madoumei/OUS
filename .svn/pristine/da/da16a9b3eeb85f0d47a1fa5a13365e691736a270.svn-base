package com.camunda;

import com.client.bean.Visitor;
import com.client.service.VisitorService;
import com.event.bean.Litigant;
import com.event.event.NotifyEvent;
import com.utils.BeanUtils;
import com.utils.SysLog;
import com.utils.UtilTools;
import com.utils.emailUtils.SendInviteEmail;
import com.web.bean.*;
import com.web.service.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.task.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class Notify implements JavaDelegate {

    @Resource
    private ProcessEngine processEngine;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private ProcessService processService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private VisitProxyService visitProxyService;

    @Autowired
    private CamundaProcessService camundaProcessService;

    @Autowired
    private MessageService messageService;

    private static Logger logger = Logger.getLogger("mylogger2");

    Expression group;

    Expression roles;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int status = (int) execution.getVariable("status");
        String realPath = (String) execution.getVariable("realPath");

        int userid = Integer.parseInt((String) execution.getVariable("userid"));
        UserInfo userinfo = userService.getUserInfo(userid);
        String businessKey = execution.getBusinessKey();
        //审批发起人
        String empid = (String) execution.getVariable("employee");
        Employee employee = employeeService.getEmployee(Integer.parseInt(empid));

        //当前task的审批意见
        HistoryService historyService = processEngine.getHistoryService();
        List<HistoricTaskInstance> taskInstances = historyService.createHistoricTaskInstanceQuery()
                .processInstanceBusinessKey(businessKey).list();
        HistoricTaskInstance taskInstance = taskInstances.get(taskInstances.size() - 1);
        TaskService taskService = processEngine.getTaskService();
        List<Comment> comments = taskService.getTaskComments(taskInstance.getId());

        /**
         * 封装通知数据
         */
        Visitor v = new Visitor();
        v.setSigninType(1);
        Visitor visitor = null;
        if (businessKey.startsWith("a")){
            Appointment appointment = new Appointment();
            appointment.setUserid(userid);
            appointment.setAgroup(businessKey.substring(1));
            List<Appointment> atlist = appointmentService.getAppointmnetByAgroup(appointment);
            if (null != atlist && atlist.size() > 0) {
              visitor = BeanUtils.appointmentToVisitor(atlist.get(0));
            }
        }else if(businessKey.startsWith("v")){
            //预约
            visitor = visitorService.getVisitorById(Integer.parseInt(businessKey.substring(1)));
        }
        Employee toEmployee = new Employee();
        /**
         * 邀请
         */
        String notifyType = (String) execution.getVariable("notifyType");

        switch (notifyType) {
            /**
             * 发送审批通知消息给leader
             */
            case "leadGroup":
                String groupValue = null;
                String rolesVaule = null;
                if (group != null) {
                    groupValue = (String) group.getValue(execution);
                }
                if (roles != null) {
                    rolesVaule = (String) roles.getValue(execution);
                }
                if (groupValue != null && rolesVaule != null) {
                    List<Employee> areaGroupEmp = camundaProcessService.getRoleEmpByGroupRoles(groupValue, rolesVaule, userinfo);
                    group = null;
                    roles = null;
                    List<Litigant> litigantList = new ArrayList<>();
                    if (null != areaGroupEmp && areaGroupEmp.size()>0) {
                        for (Employee toEmp : areaGroupEmp) {
                            litigantList.add(BeanUtils.getLitigant(employee));
                        }
                        messageService.sendCCCommonNotifyEvent(visitor,NotifyEvent.EVENTTYPE_APPROVE_FINISH,litigantList);
                    }
                }
                break;

            /**
             * 发送审批消息给employee
             */
            case "employee":
                messageService.sendCommonNotifyEvent(visitor, NotifyEvent.EVENTTYPE_APPROVE_FINISH);
                break;
            case "visitor":
                /**
                 * 邀请访客
                 */
                if (businessKey.startsWith("a")) {
                    System.out.println("发送邀请函给visitor");
                    Appointment appointment = new Appointment();
                    appointment.setUserid(userid);
                    appointment.setAgroup(businessKey.substring(1));
                    List<Appointment> atlist = appointmentService.getAppointmnetByAgroup(appointment);
                } else if (businessKey.startsWith("v")) {
                    /**
                     *预约
                     */
                }
                break;

            /**
             *访客到访接待：通知被访者
             */
            case "visitorReceive":
                messageService.sendCommonNotifyEvent(visitor, NotifyEvent.EVENTTYPE_CHECK_IN);
                break;
        }

        /**
         * 邀请审批状态更新
         */
//        if (businessKey.startsWith("a")) {
//            Appointment at = new Appointment();
//            at.setUserid(userid);
//            at.setAgroup(businessKey.substring(1));
//            List<Appointment> appointmentList = appointmentService.getAppointmnetByAgroup(at);
//            if (null == appointmentList || appointmentList.isEmpty()) {
//                return;
//            }
//            for (Appointment appointment : appointmentList) {
//                appointment.setPermission(status);
//                appointmentService.updateAppointmentPermission(appointment);
//            }
//        }
    }
}
