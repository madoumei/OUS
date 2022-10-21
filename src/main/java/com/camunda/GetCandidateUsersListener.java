package com.camunda;

import com.client.service.VisitorService;
import com.utils.SysLog;
import com.web.bean.Appointment;
import com.web.bean.EmpVisitProxy;
import com.web.bean.Employee;
import com.web.bean.UserInfo;
import com.web.service.*;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.*;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 通知审批人有新的审批需要处理
 * 设置在审批节点的listeners->task listener中
 * Event Type:assignment
 */
@Service
public class GetCandidateUsersListener implements JavaDelegate {

    @Resource
    ProcessEngine processEngine;

    @Autowired
    VisitorService visitorService;

    @Autowired
    UserService userService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    private CamundaProcessService camundaProcessService;

    Expression group;
    Expression roles;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        SysLog.info("GetCandidateUsersListener: ");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String businessKey = execution.getBusinessKey();
        String userid = (String) execution.getVariable("userid");
        int status = (int) execution.getVariable("status");
        UserInfo userInfo = userService.getUserInfo(Integer.parseInt(userid));
        String groupValue = null;
        String rolesVaule = null;

        if (group != null) {
            groupValue = (String) group.getValue(execution);
            this.group = null;
        }

        if (roles != null) {
            rolesVaule = (String) roles.getValue(execution);
            this.roles = null;
        }
        // 组、角色都不为空
        if (groupValue != null && rolesVaule != null) {
            List<Employee> candidateUserLsit = camundaProcessService.getRoleEmpByGroupRoles(groupValue, rolesVaule, userInfo);
            if(candidateUserLsit.size() != 0){
                execution.setVariable("hasCdtUsers",true);
                return;
            }

        }
        execution.setVariable("hasCdtUsers",false);

    }
}
