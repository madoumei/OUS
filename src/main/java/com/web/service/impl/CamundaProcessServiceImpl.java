package com.web.service.impl;

import com.client.bean.EquipmentGroup;
import com.client.bean.Gate;
import com.client.bean.Visitor;
import com.client.service.EquipmentGroupService;
import com.client.service.Impl.VisitorServiceImpl;
import com.client.service.VisitorService;
import com.config.exception.ErrorEnum;
import com.utils.BeanUtils;
import com.utils.Constant;
import com.utils.SysLog;
import com.utils.UtilTools;
import com.utils.cacheUtils.CacheManager;
import com.utils.httpUtils.HttpClientUtil;
import com.web.bean.*;
import com.web.dao.RoleDao;
import com.web.service.*;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.history.*;
import org.camunda.bpm.engine.impl.persistence.entity.HistoricDetailVariableInstanceUpdateEntity;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Comment;
import org.camunda.bpm.engine.task.IdentityLink;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service("CamundaProcessService")
public class CamundaProcessServiceImpl implements CamundaProcessService {

    @Resource
    private ProcessEngine processEngine;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private AddressService addressService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private EquipmentGroupService equipmentGroupService;

    @Override
    public int createApproveProcess(UserInfo userInfo, Employee employee, Camunda camunda){
        /**
         * 开始邀请工作流
         */
        Map<String, Object> camundaParam = new HashMap<>();
        camundaParam.put("userid", userInfo.getUserid() + "");
        camundaParam.put("employee", employee.getEmpid() + "");
        camundaParam.put("status", 0);
        camundaParam.put("title", camunda.getTitle());
//        camundaParam.put("realPath", request.getSession().getServletContext().getRealPath("/"));
        camundaParam.put("epidemic", userInfo.getEpidemic());


        if(StringUtils.isNotEmpty(camunda.getLeader())){
            camundaParam.put("leader", camunda.getLeader());
        }else {
            List<Employee> leaders = employeeService.getLeaders(employee);
            List<String> leaderIds = new ArrayList<>();
            if (leaders.size() > 0) {
                for (Employee e : leaders) {
                    leaderIds.add(e.getEmpid() + "");
                }
                camundaParam.put("leader", String.join(",", leaderIds));
            }
        }
        if(StringUtils.isEmpty((CharSequence) camundaParam.get("leader"))){
            return ErrorEnum.E_673.getCode();
        }

        List<String> list = camunda.getAllVisitors();
        if (camunda.getBusinessKey().startsWith("a")) {
            Appointment app = appointmentService.getAppointmentbyId(Integer.parseInt(list.get(0)));
            String accessList = app.getExtendValue("access");
            if(accessList != null){
                String[] access = accessList.split(",");
                EquipmentGroup equipmentGroup = new EquipmentGroup();
                equipmentGroup.setUserid(userInfo.getUserid());
                List<String> groupNames = new ArrayList<>();
                for(int i=0;i<access.length;i++){
                    try {
                        equipmentGroup.setEgid(Integer.parseInt(access[i]));
                        groupNames.add(equipmentGroupService.getEquipmentGroupByEgid(equipmentGroup).getEgname());
                    }catch (Exception e){}
                }
                /**
                 * 获取授权门禁组
                 */
                camundaParam.put("area", StringUtils.join(groupNames,","));


                /**
                 * 审批超时时间
                 */

                String endDate = app.getExtendValue(VisitorService.EXTEND_KEY_ENDDATE);
                if (org.apache.commons.lang.StringUtils.isNotEmpty(endDate)) {
                    try {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        format.setTimeZone(TimeZone.getTimeZone("gmt"));
                        Date date = format.parse(endDate);
                        camundaParam.put("outTimer", date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(app.getAppointmentDate());
                    calendar.add(calendar.DATE, app.getQrcodeConf() - 1);
                    calendar.set(Calendar.HOUR_OF_DAY, 24);
                    camundaParam.put("outTimer", calendar.getTime());
                }
            }


            app.getGid();
            Gate searchG = new Gate();
            searchG.setGids(app.getGid()+"");
            searchG.setUserid(app.getUserid());
            List<Gate> gateList = addressService.getGateById(searchG);
            if(gateList.size()>0) {
                camundaParam.put("gname", gateList.get(0).getGname());
            }

            camundaParam.put("vType", app.getvType());
        }else{
            Visitor visitor = visitorService.getVisitorById(Integer.parseInt(list.get(0)));
            String accessList = visitor.getExtendValue("access");
            if(accessList != null){
                String[] access = accessList.split(",");
                EquipmentGroup equipmentGroup = new EquipmentGroup();
                equipmentGroup.setUserid(userInfo.getUserid());
                List<String> groupNames = new ArrayList<>();
                for(int i=0;i<access.length;i++){
                    try {
                        equipmentGroup.setEgid(Integer.parseInt(access[i]));
                        groupNames.add(equipmentGroupService.getEquipmentGroupByEgid(equipmentGroup).getEgname());
                    }catch (Exception e){}
                }
                /**
                 * 获取授权门禁组
                 */
                camundaParam.put("area", StringUtils.join(groupNames,","));
            }

            visitor.getGid();
            Gate searchG = new Gate();
            searchG.setGids(visitor.getGid()+"");
            searchG.setUserid(visitor.getUserid());
            List<Gate> gateList = addressService.getGateById(searchG);
            if(gateList.size()>0) {
                camundaParam.put("gname", gateList.get(0).getGname());
            }

            camundaParam.put("vType", visitor.getvType());

            /**
             * 审批超时时间
             */

            String endDate = visitor.getExtendValue(VisitorService.EXTEND_KEY_ENDDATE);
            if (org.apache.commons.lang.StringUtils.isNotEmpty(endDate)) {
                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    format.setTimeZone(TimeZone.getTimeZone("gmt"));
                    Date date = format.parse(endDate);
                    camundaParam.put("outTimer", date);
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(visitor.getAppointmentDate());
                calendar.add(calendar.DATE, visitor.getQrcodeConf() - 1);
                calendar.set(Calendar.HOUR_OF_DAY, 24);
                camundaParam.put("outTimer", calendar.getTime());
            }

        }
        List<Department> depts = departmentService.getDeptByEmpid(employee.getEmpid(), employee.getUserid());
        if(depts.size() == 0){
            return ErrorEnum.E_672.getCode();
        }
        camundaParam.put("department", depts.get(0).getDeptName());


        RuntimeService runtimeService = processEngine.getRuntimeService();
        try {

            // 先设置登录用户
            IdentityService identityService = processEngine.getIdentityService();
            identityService.setAuthenticatedUserId(employee.getEmpid()+"");

            /**
             * 创建审批流程
             */
            ProcessInstance processInstance = runtimeService
                    .startProcessInstanceByKey("visitorApprove", camunda.getBusinessKey(), camundaParam);

            //记录流程id到业务表

            if (camunda.getBusinessKey().startsWith("a")) {
                List<Appointment> appointments = new ArrayList<Appointment>();
                for(String id:list){
                    Appointment app = appointmentService.getAppointmentbyId(Integer.parseInt(id));
                    if(app != null  ){
                        app.addExtendValue("approveId",processInstance.getId());
                        appointments.add(app);
                        appointmentService.AppointmentReply(app);
                    }
                }
                if(appointments.size() != 0) {
                    appointmentService.batchUpdateAppExtendCol(appointments);
                }

                //自动接受邀请

            }else{
                List<Visitor> visitors = new ArrayList<Visitor>();
                for(String id:list){
                    Visitor visitor = visitorService.getVisitorById(Integer.parseInt(id));
                    if(visitor != null){
                        visitor.addExtendValue("approveId",processInstance.getId());
                        visitors.add(visitor);
                    }
                }
                if(visitors.size()!=0) {
                    visitorService.batchUpdateExtendCol(visitors);
                }
            }

            /**
             * 员工完成员工节点的节点任务
             */
            TaskService taskService = processEngine.getTaskService();
            Task visittask = taskService.createTaskQuery()
                    .processInstanceBusinessKey(camunda.getBusinessKey())
                    .processInstanceId(processInstance.getId())         //流程定义的id
                    .taskAssignee(employee.getEmpid() + "")                   //只查询该任务负责人的任务
                    .singleResult();
            SysLog.info("businessKey: " + camunda.getBusinessKey());
            if(visittask == null){
                SysLog.error("visittask == null businesskey="+camunda.getBusinessKey()+" processid="+processInstance.getId()+" empid="+employee.getEmpid());
                return ErrorEnum.E_2000.getCode();
            }
            Map<String, Object> param = new HashMap<>();
            param.put("status", 1);
            param.put("allVisitors", camunda.getAllVisitors());
            param.put("addVisitors", camunda.getAddVisitors());
            param.put("delVisitors", camunda.getDelVisitors());
//            taskService.setAssignee(visittask.getId(), employee.getEmpid() + "");
            taskService.createComment(visittask.getId(), visittask.getProcessInstanceId(), "发起审批");
            taskService.complete(visittask.getId(), param);

        } catch (Exception e) {
            e.printStackTrace();
            SysLog.error(e);
            throw new ProcessEngineException(e.getMessage());
        }
        return 0;
    }
    /**
     * 获取每个activity的详情参数
     * @param activityInstanceId
     * @return
     */
    @Override
    public Map<String, Object> getHistoricDetailVariableMap(String activityInstanceId) {
        Map<String, Object> resp = new HashMap<>();
        HistoryService historyService = processEngine.getHistoryService();

        List<HistoricDetail> details = historyService.createHistoricDetailQuery()
                .activityInstanceId(activityInstanceId)
                .list();
        for (HistoricDetail detail : details) {
            HistoricDetailVariableInstanceUpdateEntity entity = (HistoricDetailVariableInstanceUpdateEntity) detail;
            System.out.println(entity.getName() + " " + entity.getValue());
            resp.put(entity.getName(), entity.getValue());
        }
        return resp;
    }

    /**
     * 获取每个processInstance的参数
     * @param processInstanceId
     * @return
     */
    @Override
    public Map<String, Object> getHistoricProcessVariableMap(String processInstanceId) {
        Map<String, Object> resp = new HashMap<>();
        HistoryService historyService = processEngine.getHistoryService();

        List<HistoricVariableInstance> details = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
                .list();
        for (HistoricVariableInstance detail : details) {
            resp.put(detail.getName(), detail.getValue());
        }
        return resp;
    }

    @Override
    public ProcessRecord getProcessRecodesByTask(Task task) {
        ProcessRecord pr = new ProcessRecord();

        /**
         * 获取业务流程唯一标识
         */
        HistoryService historyService = processEngine.getHistoryService();
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
        HistoricProcessInstance processInstance = historicProcessInstanceQuery.processInstanceId(task.getProcessInstanceId()).singleResult();
        if (null == processInstance) {
            return pr;
        }
        String businessKey = processInstance.getBusinessKey();

        /**
         * 获取流程历史记录
         */
        HistoricTaskInstance taskInstances = historyService.createHistoricTaskInstanceQuery()
                .processInstanceBusinessKey(businessKey)
                .taskId(task.getId())
                .singleResult();
        if (null != taskInstances) {
            //获取业务流程全局参数
            Map<String, Object> detailVariableMap = getHistoricProcessVariableMap(processInstance.getId());
            pr.setVid(businessKey.substring(1));
            if (null != detailVariableMap.get("aid")) {
                pr.setAid((Integer) detailVariableMap.get("aid"));
            }
            //流程提交人
            Employee employee = employeeService.getEmployee(Integer.parseInt((String) detailVariableMap.get("employee")));
            if (null != employee) {
                pr.setRvwEmpId(employee.getEmpid() + "");
                pr.setRvwEmpName(employee.getEmpName());
            } else {
                //查询候选人
                List<IdentityLink> identityLinksForTask = processEngine.getTaskService().getIdentityLinksForTask(task.getId());
                List<String> empids = new ArrayList<>();
                List<String> empNames = new ArrayList<>();
                for (IdentityLink identityLink : identityLinksForTask) {
                    if (identityLink.getType().equals("candidate")) {
                        Employee candidateEmp = employeeService.getEmployee(Integer.parseInt(identityLink.getUserId()));
                        if (null != candidateEmp) {
                            empids.add(candidateEmp.getEmpid() + "");
                            empNames.add(candidateEmp.getEmpName());
                        }
                    }
                }
                pr.setRvwEmpId(UtilTools.listToString(empids));
                pr.setRvwEmpName(UtilTools.listToString(empNames));
            }
            //获取访客组
            if (businessKey.startsWith("a")) {
                pr.setPType(1);
                //查询邀请访客组
                Appointment at = new Appointment();
                at.setAgroup(businessKey.substring(1));
                at.setUserid(Integer.parseInt((String) detailVariableMap.get("userid")));
                List<Appointment> appointmentList = new ArrayList<>();
                appointmentList = appointmentService.getAppointmnetByAgroup(at);
                if (null != appointmentList && appointmentList.size() > 0) {
                    pr.setAppList(appointmentList);
                    pr.setAppTime(appointmentList.get(0).getAppointmentDate());
                }

            } else if (businessKey.startsWith("v")) {
                /**
                 * 预约
                 */
                pr.setPType(2);
                //查询预约访客组
                Visitor vt = new Visitor();
                vt.setVgroup(Integer.parseInt(businessKey.substring(1)));
                vt.setUserid(Integer.parseInt((String) detailVariableMap.get("userid")));
                List<Visitor> visitorList = visitorService.getVisitorByVgroup(vt.getVgroup()+"");
                if (null != visitorList && visitorList.size() > 0) {
                    List<Appointment> appointmentList = new ArrayList<>();
                    for(Visitor visitor:visitorList){
                        Appointment appointment = new Appointment();
                        appointment.setUserid(visitor.getUserid());
                        appointment.setId(visitor.getVid());
                        appointment.setEmpid(visitor.getEmpid());
                        appointment.setEmpPhone(visitor.getEmpPhone());
                        appointment.setEmpName(visitor.getEmpName());
                        appointment.setName(visitor.getVname());
                        appointment.setPhone(visitor.getVphone());
                        appointment.setPermission(visitor.getPermission());
                        appointment.setStatus(visitor.getStatus());
                        appointment.setAppExtendCol(visitor.getExtendCol());
                        appointment.setVisitDate(visitor.getVisitdate());
                        appointment.setAppointmentDate(visitor.getAppointmentDate());
                        appointment.setVisitType(visitor.getVisitType());
                        appointment.setvType(visitor.getvType());
                        appointment.setRemark(visitor.getRemark());
                        appointment.setCreateTime(visitor.getCreateTime());
                        appointmentList.add(appointment);
                    }
                    pr.setAppList(appointmentList);
                    pr.setAppTime(visitorList.get(0).getAppointmentDate());

                }
            }
            pr.setUserid(Integer.parseInt((String) detailVariableMap.get("userid")));
            pr.setTaskid(task.getId());
            pr.setSubmitTime(taskInstances.getEndTime());
            pr.setProcessInstanceId(processInstance.getId());
            pr.setTaskName(task.getName());
            if(detailVariableMap.get("status") != null) {
                pr.setStatus((Integer) detailVariableMap.get("status"));
            }

        }
        return pr;
    }

    @Override
    public List<ProcessRecord> getHistoryPRByProcessInstanceId(String processInstanceId, int userid) {
        List<ProcessRecord> prlist = new ArrayList<>();
        if (getHistoryTask(processInstanceId, userid, prlist)) {
            return null;
        }
        return prlist;
    }

    @Override
    public List<Employee> getRoleEmpByGroupRoles(String groupValue, String rolesVaule,UserInfo userInfo) {
        List<Employee> empRoleList = new ArrayList<>();
        //获取组、角色用户
        Role ro = new Role();
        ro.setUserid(userInfo.getUserid());
        List<Role> rlist = roleDao.getRoleGroupList(ro);
        for (int i = 0; i < rlist.size(); i++) {
            rlist.get(i).setChildRoleList(roleDao.getRoleList(rlist.get(i)));
        }
        //过滤组
        String groupName = groupValue;
        if (null != rlist && rlist.size()>0){
            List<Role> roleList_1 = rlist.stream().filter(role -> role.getRgName().equals(groupName)).collect(Collectors.toList());
            String finalRolesVaule = rolesVaule;
            List<Role> roleList_2 = roleList_1.get(0).getChildRoleList().stream().filter(role -> role.getRgName().equals(finalRolesVaule)).collect(Collectors.toList());
            if (roleList_2.size()>0){
                Role role = roleList_2.get(0);
                RequestEmp requestEmp = new RequestEmp();
                requestEmp.setRid(role.getRid());
                empRoleList = employeeService.getEmpRoleList(requestEmp);
            }
        }
        groupValue = null;
        rolesVaule = null;
        return empRoleList;
    }

    /**
     * 查找审批过程的task
     * @param processInstanceId
     * @param userid
     * @param prlist
     * @return
     */
    private boolean getHistoryTask(String processInstanceId, int userid, List<ProcessRecord> prlist) {
        TaskService taskService = processEngine.getTaskService();
        HistoryService historyService = processEngine.getHistoryService();
        List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)       //流程实例id
                //.activityType(ActivityTypes.TASK_USER_TASK) //过滤出usertask
                .orderByHistoricActivityInstanceStartTime().asc()         //按发生时间顺序
                .list();

        if (historicTaskInstances.size() > 0) {
            HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();
            //通过实例id查找task历史数据
            int leavel = 1;  //节点等级，默认第一节点；
            for (HistoricTaskInstance taskInstance : historicTaskInstances) {
                    String businessKey = hpi.getBusinessKey();
                    ProcessRecord pr = new ProcessRecord();
                    if (businessKey.startsWith("a")) {
                        List<Appointment> appointmentList = new ArrayList<>();
                        //查询邀请访客组
                        Appointment at = new Appointment();
                        at.setAgroup(businessKey.substring(1));
                        at.setUserid(userid);
                        appointmentList = appointmentService.getAppointmnetByAgroup(at);
                        if (null != appointmentList && appointmentList.size() > 0) {
                            pr.setAppList(appointmentList);
                            pr.setAppTime(appointmentList.get(0).getAppointmentDate());
                            pr.setAid(appointmentList.get(0).getId());
                        }
                        pr.setPType(1);

                    } else if (businessKey.startsWith("v")) {
                        /**
                         * 预约
                         */
                        //查询预约访客组
                        Visitor vt = new Visitor();
                        vt.setVgroup(Integer.parseInt(businessKey.substring(1)));
                        vt.setUserid(userid);
                        List<Visitor> visitorList = visitorService.getVisitorByVgroup(vt.getVgroup()+"");
                        if (null != visitorList && visitorList.size() > 0) {
                            List<Appointment> appointmentList = new ArrayList<>();
                            for(Visitor visitor:visitorList){
                                Appointment appointment = BeanUtils.VisitorToAppointment(visitor);
                                appointmentList.add(appointment);
                            }
                            pr.setAppList(appointmentList);
                            pr.setAppTime(visitorList.get(0).getAppointmentDate());
                            pr.setVid(visitorList.get(0).getVid()+"");
                        }
                        pr.setPType(2);
                    }

                    pr.setRid(taskInstance.getRootProcessInstanceId());
                    pr.setUserid(userid);

                    pr.setTaskid(taskInstance.getId());
//                        System.out.println("任务开始时间：" + taskInstance.getStartTime());
                    pr.setSubmitTime(taskInstance.getStartTime());

                    //节点等级
                    pr.setLevel(leavel);
//                        System.out.println("节点等级：" + leavel);
                    leavel++;

                    /**
                     * 当前审批的审批人
                     */
                    if (StringUtils.isNotBlank(taskInstance.getAssignee())) {
                        Employee emp = employeeService.getEmployee(Integer.parseInt(taskInstance.getAssignee()));
                        if (null != emp){
                            pr.setRvwEmpId(emp.getEmpid() + "");
                            pr.setRvwEmpName(emp.getEmpName());
//                                System.out.println("审批人id：" + emp.getEmpid());
//                                System.out.println("审批人姓名：" + emp.getEmpName());
                        }
                    } else {
                        //查询候选人
                        HistoricIdentityLinkLogQuery historicIdentityLinkLogQuery = historyService.createHistoricIdentityLinkLogQuery();
                        List<HistoricIdentityLinkLog> list = historicIdentityLinkLogQuery.taskId(taskInstance.getId()).list();
                        List<String> empids = new ArrayList<>();
                        List<String> empNames = new ArrayList<>();
                        for (HistoricIdentityLinkLog identityLink : list) {
                            if (identityLink.getType().equals("candidate")) {
                                Employee candidateEmp = employeeService.getEmployee(Integer.parseInt(identityLink.getUserId()));
                                if (null != candidateEmp) {
                                    empids.add(candidateEmp.getEmpid() + "");
                                    empNames.add(candidateEmp.getEmpName());
                                }
                            }
                        }
                        pr.setRvwEmpId(UtilTools.listToString(empids));
                        pr.setRvwEmpName(UtilTools.listToString(empNames));
                    }

                    /**
                     * 当前task的审批意见
                     */
                    List<Comment> comments = taskService.getTaskComments(taskInstance.getId());
                    for (Comment comment : comments) {
                        if (StringUtils.isNotBlank(comment.getFullMessage())) {
                            pr.setRemark(comment.getFullMessage());
//                                System.out.println("审批意见：" + comment.getFullMessage());
                        }
                    }
                    /**
                     * 历史会话实例
                     */

                    if(taskInstance.getDeleteReason() != null && taskInstance.getDeleteReason().equals("completed")) {
                        //节点完成状态
                        List<HistoricDetail> historicDetails = historyService.createHistoricDetailQuery()
                                .activityInstanceId(taskInstance.getActivityInstanceId()).list();

                        //当前task的详细参数
                        for (HistoricDetail detail : historicDetails) {
                            HistoricDetailVariableInstanceUpdateEntity entity = (HistoricDetailVariableInstanceUpdateEntity) detail;
                            if (entity.getName().equals("status")) {
                                pr.setStatus((Integer) entity.getValue());
                            }
                        }
                    }else if(taskInstance.getDeleteReason() != null && taskInstance.getDeleteReason().equals("deleted")){
                        //取消的节点
                        pr.setStatus(5);
                    }else {
                        pr.setStatus(0);
                    }

                    String assignee = taskInstance.getAssignee();
                    if (StringUtils.isNotBlank(assignee)) {
                        Employee employee = employeeService.getEmployee(Integer.parseInt(assignee));
                        if (null != employee) {
                            pr.setRvwEmpId(employee.getEmpid() + "");
                            pr.setRvwEmpName(employee.getEmpName());
                        }
                    }
                    prlist.add(pr);
            }
        } else {
            return true;

        }
        return false;
    }


    /**
     * 查找审批过程的activity
     * @param processInstanceId
     * @param userid
     * @param prlist
     * @return
     */
    private boolean extracted(String processInstanceId, int userid, List<ProcessRecord> prlist) {
        TaskService taskService = processEngine.getTaskService();
        HistoryService historyService = processEngine.getHistoryService();
        List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)       //流程实例id
                //.activityType(ActivityTypes.TASK_USER_TASK) //过滤出usertask
                .orderPartiallyByOccurrence().asc()         //按发生时间顺序
                .list();

        if (historicActivityInstances.size() > 0) {
            HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();
            //通过实例id查找task历史数据
            int leavel = 1;  //节点等级，默认第一节点；
            for (HistoricActivityInstance taskInstance : historicActivityInstances) {
                if (taskInstance.getActivityType().equals("callActivity")) {
                    extracted(taskInstance.getCalledProcessInstanceId(), userid, prlist);
                } else if (taskInstance.getActivityType().equals("userTask")) {
                    String businessKey = hpi.getBusinessKey();
                    ProcessRecord pr = new ProcessRecord();
                    if (businessKey.startsWith("a")) {
                        List<Appointment> appointmentList = new ArrayList<>();
                        //查询邀请访客组
                        Appointment at = new Appointment();
                        at.setAgroup(businessKey.substring(1));
                        at.setUserid(userid);
                        appointmentList = appointmentService.getAppointmnetByAgroup(at);
                        if (null != appointmentList && appointmentList.size() > 0) {
                            pr.setAppList(appointmentList);
                            pr.setAppTime(appointmentList.get(0).getAppointmentDate());
                            pr.setAid(appointmentList.get(0).getId());
                        }
                        pr.setPType(1);

                    } else if (businessKey.startsWith("v")) {
                        /**
                         * 预约
                         */
                        //查询预约访客组
                        Visitor vt = new Visitor();
                        vt.setVgroup(Integer.parseInt(businessKey.substring(1)));
                        vt.setUserid(userid);
                        List<Visitor> visitorList = visitorService.getVisitorByVgroup(vt.getVgroup()+"");
                        if (null != visitorList && visitorList.size() > 0) {
                            List<Appointment> appointmentList = new ArrayList<>();
                            for(Visitor visitor:visitorList){
                                Appointment appointment = BeanUtils.VisitorToAppointment(visitor);
                                appointmentList.add(appointment);
                            }
                            pr.setAppList(appointmentList);
                            pr.setAppTime(visitorList.get(0).getAppointmentDate());
                            pr.setVid(visitorList.get(0).getVid()+"");
                        }
                        pr.setPType(2);
                    }

                    pr.setRid(taskInstance.getRootProcessInstanceId());
                    pr.setUserid(userid);

                    pr.setTaskid(taskInstance.getTaskId());
//                        System.out.println("任务开始时间：" + taskInstance.getStartTime());
                    pr.setSubmitTime(taskInstance.getStartTime());

                    //节点等级
                    pr.setLevel(leavel);
//                        System.out.println("节点等级：" + leavel);
                    leavel++;

                    /**
                     * 当前审批的审批人
                     */
                    if (StringUtils.isNotBlank(taskInstance.getAssignee())) {
                        Employee emp = employeeService.getEmployee(Integer.parseInt(taskInstance.getAssignee()));
                        if (null != emp){
                            pr.setRvwEmpId(emp.getEmpid() + "");
                            pr.setRvwEmpName(emp.getEmpName());
//                                System.out.println("审批人id：" + emp.getEmpid());
//                                System.out.println("审批人姓名：" + emp.getEmpName());
                        }
                    } else {
                        //查询候选人
                        List<IdentityLink> identityLinksForTask = taskService.getIdentityLinksForTask(taskInstance.getTaskId());
                        List<String> empids = new ArrayList<>();
                        List<String> empNames = new ArrayList<>();
                        for (IdentityLink identityLink : identityLinksForTask) {
                            if (identityLink.getType().equals("candidate")) {
                                Employee candidateEmp = employeeService.getEmployee(Integer.parseInt(identityLink.getUserId()));
                                if (null != candidateEmp) {
                                    empids.add(candidateEmp.getEmpid() + "");
                                    empNames.add(candidateEmp.getEmpName());
                                }
                            }
                        }
                        pr.setRvwEmpId(UtilTools.listToString(empids));
                        pr.setRvwEmpName(UtilTools.listToString(empNames));
                    }

                    /**
                     * 当前task的审批意见
                     */
                    List<Comment> comments = taskService.getTaskComments(taskInstance.getTaskId());
                    for (Comment comment : comments) {
                        if (StringUtils.isNotBlank(comment.getFullMessage())) {
                            pr.setRemark(comment.getFullMessage());
//                                System.out.println("审批意见：" + comment.getFullMessage());
                        }
                    }
                    /**
                     * 历史会话实例
                     */
                    List<HistoricTaskInstance> list2 = new ArrayList<>();
                    if (StringUtils.isNotBlank(taskInstance.getTaskId())) {
                        list2 = historyService.createHistoricTaskInstanceQuery()
                                .taskId(taskInstance.getTaskId())
                                .processInstanceBusinessKey(businessKey)
                                .list();
                        //节点完成状态
                        List<HistoricDetail> historicDetails = historyService.createHistoricDetailQuery()
                                .variableUpdates()
                                .activityInstanceId(taskInstance.getId()).list();

                        //当前task的详细参数
                        if (taskInstance.isCanceled()){
                            pr.setStatus(3);
                        }else {
                            for (HistoricDetail detail : historicDetails) {
                                HistoricDetailVariableInstanceUpdateEntity entity = (HistoricDetailVariableInstanceUpdateEntity) detail;
                                if (entity.getName().equals("status")) {
                                    pr.setStatus((Integer) entity.getValue());
                                }
                            }
                        }

                    }
                    String assignee = taskInstance.getAssignee();
                    if (StringUtils.isNotBlank(assignee)) {
                        Employee employee = employeeService.getEmployee(Integer.parseInt(assignee));
                        if (null != employee) {
                            pr.setRvwEmpId(employee.getEmpid() + "");
                            pr.setRvwEmpName(employee.getEmpName());
                        }
                    }
                    prlist.add(pr);
                }
            }
        } else {
            return true;

        }
        return false;
    }



    @Override
    public int completeProcessInstance(String processInstanceId) {
        if(null == processInstanceId){
             return 1;
        }
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).active().singleResult();
        if(task == null){
            return 1;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("status", 1);
        taskService.createComment(task.getId(), task.getProcessInstanceId(), "自动通过");
        taskService.setAssignee(task.getId(), task.getAssignee() + "");
        taskService.complete(task.getId(),param);
        SysLog.info("自动提交会话id" + task.getId());
        return 1;
    }

}
