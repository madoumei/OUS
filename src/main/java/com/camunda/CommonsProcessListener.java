package com.camunda;

import com.client.bean.Visitor;
import com.client.service.VisitorService;
import com.event.bean.Litigant;
import com.event.event.NotifyEvent;
import com.utils.BeanUtils;
import com.utils.Constant;
import com.utils.SysLog;
import com.web.bean.*;
import com.web.service.*;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.TransitionImpl;
import org.camunda.bpm.engine.runtime.ProcessInstanceModificationBuilder;
import org.camunda.bpm.engine.task.IdentityLink;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 通知审批人有新的审批需要处理
 * 设置在审批节点的listeners->task listener中
 * Event Type:assignment
 */
@Service
public class CommonsProcessListener implements TaskListener {

    @Resource
    ProcessEngine processEngine;

    @Autowired
    VisitorService visitorService;

    @Autowired
    UserService userService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private CamundaProcessService camundaProcessService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private VisitProxyService visitProxyService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private RuntimeService runtimeService;

    Expression group;
    Expression roles;

    /**
     * camunda 工作流的时间监听通知
     *
     * @param delegateTask
     */
    @Override
    public void notify(DelegateTask delegateTask) {
        SysLog.info("CommonsProcessListener: ");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String businessKey = delegateTask.getExecution().getBusinessKey();
        String userid = (String) delegateTask.getVariable("userid");
        int status = (int) delegateTask.getVariable("status");
        UserInfo userInfo = userService.getUserInfo(Integer.parseInt(userid));
        SysLog.info("businessKey: " + businessKey+" 流程通知审批："+delegateTask.getAssignee()+" proId:"+delegateTask.getProcessInstanceId()+" delTaskId:"+delegateTask.getId()+" "+delegateTask.getName());
        String groupValue = null;
        String rolesVaule = null;

        if (group != null) {
            groupValue = (String) group.getValue(delegateTask);
            this.group = null;
        }

        if (roles != null) {
            rolesVaule = (String) roles.getValue(delegateTask);
            this.roles = null;
        }
        // 组、角色都不为空
        if (groupValue != null && rolesVaule != null) {
            List<Employee> candidateUserLsit = camundaProcessService.getRoleEmpByGroupRoles(groupValue, rolesVaule, userInfo);
            if(candidateUserLsit.size() == 0){
                return;
            }

            //1.0 通知组、角色人员审批
            List<String> candidateUsers = new ArrayList<>();
            if ( candidateUserLsit.size() > 0) {
                for (int i = 0; i < candidateUserLsit.size() ; i++) {
                    //员工代理人查询
                    EmpVisitProxy vp = new EmpVisitProxy();
                    Employee empProxy = new Employee();
                    vp.setEmpid(candidateUserLsit.get(i).getEmpid());
                    vp.setUserid(userInfo.getUserid());
                    vp = visitProxyService.getProxyInfoByEid(vp);
                    Date pdate = new Date();
                    Date sdate = null;
                    Date edate = null;
                    if (null != vp) {
                        empProxy = employeeService.getEmployee(vp.getProxyId());
                        sdate = vp.getStartDate();
                        edate = vp.getEndDate();
                    }

                    //添加候选人
                    candidateUsers.add(String.valueOf(candidateUserLsit.get(i).getEmpid()));
                }
            }


            //添加候选人
            if (candidateUsers.size() > 0) {
                delegateTask.addCandidateUsers(candidateUsers);
                SysLog.info("addCandidateUsers "+candidateUsers.toString());
//                delegateTask.setAssignee(candidateUserLsit.get(0).getEmpid()+"");
            }

            /**
             * 如果已经同一个人审批过则自动跳过
             */
            if(autoComplete(candidateUserLsit,delegateTask)!=0)
            {
                return;
            }

            Visitor visitor = null;
            if (businessKey.startsWith("a")) {
                //1.1.1 根据访客组id(工作流唯一id)获取邀请组记录
                Appointment at = new Appointment();
                at.setUserid(Integer.parseInt(userid));
                at.setAgroup(businessKey.substring(1));
                List<Appointment> appointmentList = appointmentService.getAppointmnetByAgroup(at);
                if (null == appointmentList || appointmentList.isEmpty()) {
                    SysLog.error(dateFormat.format(dateFormat) + " 未查询到邀请记录。businessKey=" + businessKey);
                    return;
                }
                visitor = BeanUtils.appointmentToVisitor(appointmentList.get(0));

            }
            //1.2 预约审批
            if (businessKey.startsWith("v")) {
                /**
                 * 预约
                 */
                List<Visitor> visitorList = visitorService.getVisitorByVgroup(businessKey.substring(1));
                if (visitorList.size() == 0) {
                    SysLog.error(dateFormat.format(dateFormat) + " 未查询到邀请记录。businessKey=" + businessKey);
                    return;
                }
                visitor = visitorList.get(0);
            }

            List<Litigant> litigantList = new ArrayList<Litigant>();
            for(Employee employee:candidateUserLsit){
                litigantList.add(BeanUtils.getLitigant(employee));
            }
            messageService.sendProcessNotifyEvent(visitor,litigantList);
        } else {

            if (status == 0) {
                //第一次不通知发起人
                SysLog.info("end 1 businessKey: " + businessKey+" 流程通知审批："+delegateTask.getAssignee()+" proId:"+delegateTask.getProcessInstanceId()+" delTaskId:"+delegateTask.getId()+" "+delegateTask.getName());

                return;
            }
            List<Litigant> litigantList = new ArrayList<Litigant>();
            List<Employee> leaders = new ArrayList<>();
            try {
                Employee assigee = null;
                if(null != delegateTask.getAssignee()) {
                    assigee = employeeService.getEmployee(Integer.parseInt((String) delegateTask.getAssignee()));
                }
                if (assigee != null) {
                    leaders.add(assigee);
                    litigantList.add(BeanUtils.getLitigant(assigee));
                }
            } catch (Exception e) {

            }

            if (leaders.size() == 0) {
                //候选人
                Set<IdentityLink> candidates = delegateTask.getCandidates();
                if(candidates.size()>0) {
                    for (IdentityLink identityLink : candidates) {
                        Employee employee = employeeService.getEmployee(Integer.parseInt(identityLink.getUserId()));
                        if (employee != null) {
                            leaders.add(employee);
                            litigantList.add(BeanUtils.getLitigant(employee));
                        }
                    }
                }
            }
            if (leaders.size() == 0) {
                SysLog.error("no leader");
                return;
            }

            /**
             * 如果已经同一个人审批过则自动跳过
             */
            if(autoComplete(leaders,delegateTask)==1)
            {
                return;
            }

            for (Employee l : leaders) {
                //员工代理人查询
                EmpVisitProxy vp = new EmpVisitProxy();
                Employee empProxy = new Employee();
                vp.setEmpid(l.getEmpid());
                vp.setUserid(userInfo.getUserid());
                vp = visitProxyService.getProxyInfoByEid(vp);
                Date pdate = new Date();
                Date sdate = null;
                Date edate = null;
                if (null != vp) {
                    empProxy = employeeService.getEmployee(vp.getProxyId());
                    sdate = vp.getStartDate();
                    edate = vp.getEndDate();
                    if (null != vp && null != empProxy && vp.getProxyStatus() == 1 && pdate.getTime() >= sdate.getTime() && pdate.getTime() <= edate.getTime()) {
                        //代理目前无法在待审批列表看见
                        //litigantList.add(BeanUtils.getLitigant(empProxy));
                    }


                }

            }

            Visitor visitor = null;
            if (businessKey.startsWith("a")) {
                //1.1.1 根据访客组id(工作流唯一id)获取邀请组记录
                Appointment at = new Appointment();
                at.setUserid(Integer.parseInt(userid));
                at.setAgroup(businessKey.substring(1));
                List<Appointment> appointmentList = appointmentService.getAppointmnetByAgroup(at);
                if (null == appointmentList || appointmentList.isEmpty()) {
                    SysLog.error(dateFormat.format(dateFormat) + " 未查询到邀请记录。businessKey=" + businessKey);
                    return;
                }
                visitor = BeanUtils.appointmentToVisitor(appointmentList.get(0));

            }
            //1.2 预约审批
            if (businessKey.startsWith("v")) {
                /**
                 * 预约
                 */
                List<Visitor> visitorList = visitorService.getVisitorByVgroup(businessKey.substring(1));
                if (visitorList.size() == 0) {
                    SysLog.error(dateFormat.format(dateFormat) + " 未查询到邀请记录。businessKey=" + businessKey);
                    return;
                }
                visitor = visitorList.get(0);
            }

            if(litigantList.size() == 0){
                SysLog.error("没有审批人 businessKey："+businessKey);
                return;
            }
            messageService.sendProcessNotifyEvent(visitor, litigantList);
            //visitorService.sendProcessNotify(vp, empProxy, userInfo, appointmentList, leader,status,subEmployee);
            SysLog.info("businessKey: " + businessKey + " 通知审批：" + litigantList + " status=" + status);


        }
    }

    /**
     * 如果候选人曾经之前曾经审批过，自动完成审批
     * @param candidateList
     * @param delegateTask
     * @return
     */
    private int autoComplete(List<Employee> candidateList, DelegateTask delegateTask) {

        HistoryService historyService = processEngine.getHistoryService();
        List<HistoricTaskInstance> taskInstances = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(delegateTask.getProcessInstanceId()).list();
        if (taskInstances.size() == 0) {
            return 0;
        }
        for (HistoricTaskInstance hTask : taskInstances) {
            for (Employee employee : candidateList) {
                if (String.valueOf(employee.getEmpid()).equals(hTask.getAssignee())) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            synchronized(Constant.LOCK_AUTO_COMPLETE) {
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            TaskService taskService = processEngine.getTaskService();
                            Task task = taskService.createTaskQuery().executionId(delegateTask.getExecutionId()).singleResult();
                                if (task == null) {
                                return;
                            }
                            if(org.apache.commons.lang.StringUtils.isEmpty(task.getAssignee()))
                            {
                                taskService.claim(task.getId(), employee.getEmpid() + "");
                            }
                            Map<String, Object> param = new HashMap<>();
                            param.put("status", 1);
                            taskService.createComment(task.getId(), task.getProcessInstanceId(), "自动完成");
                                taskService.complete(task.getId(), param);
                                SysLog.info(employee.getEmpName() + " 已审批，自动完成 2" + task.getName() + " taskid：" + task.getId());
                            }
                        }
                    }).start();
                    SysLog.info("自动跳过审批 "+delegateTask.getName());
                    return 1;
                }
            }

        }
        SysLog.info("未自动跳过 "+delegateTask.getName());
        return 0;
    }
}
