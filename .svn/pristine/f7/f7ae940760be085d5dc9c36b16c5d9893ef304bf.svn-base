package com.web.controller;

import com.client.bean.Gate;
import com.client.bean.RequestVisit;
import com.client.bean.RespVisitor;
import com.client.bean.Visitor;
import com.client.service.EquipmentGroupService;
import com.client.service.VisitorService;
import com.config.activemq.MessageSender;
import com.config.exception.ErrorEnum;
import com.config.qicool.common.persistence.Page;
import com.utils.BeanUtils;
import com.utils.SysLog;
import com.web.bean.*;
import com.web.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.history.*;
import org.camunda.bpm.engine.impl.RepositoryServiceImpl;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.impl.pvm.PvmTransition;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.TransitionImpl;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/**")
@Api(value = "CamundaProcessController", tags = "API_审批流管理", hidden = true)
public class CamundaProcessController {

    @Resource
    private ProcessEngine processEngine;

    @Autowired
    SubAccountService subAccountService;

    @Autowired
    UserService userService;

    @Autowired
    BlacklistService blacklistService;

    @Autowired
    VisitorService visitorService;

    @Autowired
    EquipmentGroupService equipmentGroupService;

    @Autowired
    PersonInfoService personInfoService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    DepartmentService departmentService;

    @Autowired
    MessageSender messageSender;

    @Autowired
    ProcessService processService;

    @Autowired
    StringRedisTemplate strRedisTemplate;

    @Autowired
    VisitProxyService visitProxyService;

    @Autowired
    private CamundaProcessService camundaProcessService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private TokenServer tokenServer;

    @ApiOperation(value = "/deploymentProcessModels 批量部署流程引擎模板", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/deploymentProcessModels")
    @ResponseBody
    public RespInfo deploymentProcessModels(@RequestParam("fileName") MultipartFile[] multipartFiles) {
        if (null != multipartFiles && multipartFiles.length > 0) {
            RepositoryService repositoryService = processEngine.getRepositoryService();
            for (MultipartFile multipartFile : multipartFiles) {
                //获取部署文件名
                String originalFilename = multipartFile.getOriginalFilename();
                try {
                    //获取文件流
                    InputStream inputStream = multipartFile.getInputStream();
                    if (originalFilename.endsWith("bpmn")) {
                        //部署文件
                        Deployment deployment = repositoryService.createDeployment()
                                .name(originalFilename)
                                .addInputStream(originalFilename, inputStream)
                                .deploy();
                    } else {
                        continue;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
            }
        } else {
            return new RespInfo(21, "fail to upload");
        }
        return new RespInfo(0, "success");
    }

    /**
     * 获取未完成审批流程列表
     *
     * @param requestVisit
     * @return
     */
    @ApiOperation(value = "/getNoApproveRecordsByCamunda 获取未审批流程", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getNoApproveRecordsByCamunda")
    @ResponseBody
    public RespInfo getNoApproveRecordsByCamunda(
            @ApiParam(value = "RequestVisit 请求访客Bean", required = true) @Validated @RequestBody RequestVisit requestVisit,
            HttpServletRequest request, BindingResult result) {

        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        Employee employee = employeeService.getEmployee(Integer.parseInt(authToken.getLoginAccountId()));

        if (null == employee) {
            return new RespInfo(55, "no employee");
        }
        TaskService taskService = processEngine.getTaskService();

        int maxResult = requestVisit.getRequestedCount();
        int startIndex = requestVisit.getStartIndex() - 1;
        int currentPage = requestVisit.getStartIndex()/maxResult+1;

        List<String> filtProcessInstanceIds = new ArrayList<String>();
        if (StringUtils.isNotEmpty(requestVisit.getName())) {
            RequestVisit search = new RequestVisit();
            search.setName(requestVisit.getName());
            search.setvType("");
            search.setSigninType("");
            search.setUserid(employee.getUserid());
            List<RespVisitor> visitors = visitorService.searchVisitors(search);

            for(RespVisitor rv:visitors){
                if(StringUtils.isNotEmpty(rv.getExtendValue("approveId"))){
                    filtProcessInstanceIds.add(rv.getExtendValue("approveId"));
                }
            }
            if(filtProcessInstanceIds.size() ==0 ){
                return new RespInfo(255, "no process");
            }
        }

        TaskQuery taskQuery = taskService.createTaskQuery()
                .or().taskAssignee(employee.getEmpid() + "")
                .taskCandidateUser(employee.getEmpid() + "").endOr()
                .orderByTaskCreateTime().desc();
        if(filtProcessInstanceIds.size()>0){
            taskQuery.processInstanceIdIn(filtProcessInstanceIds.toArray(new String[0]));
        }
        int pageCount =taskQuery.list().size();

        List<Task> taskList =taskQuery.listPage(startIndex, maxResult);

        if (taskList == null || taskList.isEmpty()) {
            return new RespInfo(255, "no process");
        } else {
            List<ProcessRecord> processRecords = new ArrayList<>();
            for (Task task : taskList) {
                try {
                    ProcessRecord processRecord = camundaProcessService.getProcessRecodesByTask(task);
                    processRecords.add(processRecord);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }

            Page<ProcessRecord> prPage = new Page<ProcessRecord>(currentPage , requestVisit.getRequestedCount(), pageCount);
            prPage.setList(processRecords);
            int pageTotal = (pageCount + requestVisit.getRequestedCount() - 1) / requestVisit.getRequestedCount();//总页数
            prPage.setLastPage((pageTotal == currentPage ? true : false));
            prPage.setFirstPage(currentPage == 1 ? true : false);
            return new RespInfo(0, "success", prPage);
        }
    }

    /**
     * 获取已经审批完成流程列表
     *
     * @param requestVisit
     * @return
     */
    @ApiOperation(value = "/getApproveRecordsByCamunda 获取已审批流程,包括自己提交的", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getApproveRecordsByCamunda")
    @ResponseBody
    public RespInfo getApproveRecordsByCamunda(
            @ApiParam(value = "RequestVisit 请求访客Bean", required = true) @Validated @RequestBody RequestVisit requestVisit,
            HttpServletRequest request, BindingResult bindingResult) {

        List<ObjectError> allErrors = bindingResult.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        Map<String, List<ProcessRecord>> result = new HashMap<>();

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        Employee employee = employeeService.getEmployee(Integer.parseInt(authToken.getLoginAccountId()));


        if (null == employee) {
            return new RespInfo(55, "no employee");
        }


        HistoryService historyService = processEngine.getHistoryService();


        /**
         * 查询分页数据
         */
        int maxResult = requestVisit.getRequestedCount();
        int startIndex = requestVisit.getStartIndex() - 1;
        int currentPage = requestVisit.getStartIndex()/maxResult+1;
//        List<HistoricActivityInstance> taskInstances = historyService.createNativeHistoricActivityInstanceQuery()
//                .sql(sql).parameter("assignee", employee.getEmpid())
//                .listPage(requestVisit.getStartIndex(), maxResult);

        //已审批
        Set<String> processInstanceIds = new HashSet<String>();
        Set<String> filtProcessInstanceIds = new HashSet<String>();
        List<HistoricProcessInstance> list = null;
        int pageCount = 0;
        //搜索过滤
        if (StringUtils.isNotEmpty(requestVisit.getName())) {
            RequestVisit search = new RequestVisit();
            search.setName(requestVisit.getName());
            search.setvType("");
            search.setSigninType("");
            search.setUserid(employee.getUserid());
            List<RespVisitor> visitors = visitorService.searchVisitors(search);

            for(RespVisitor rv:visitors){
                if(StringUtils.isNotEmpty(rv.getExtendValue("approveId"))){
                    filtProcessInstanceIds.add(rv.getExtendValue("approveId"));
                }
            }

            if(filtProcessInstanceIds.size() ==0 ){
            return new RespInfo(255, "no process");
        }
        }

        /**
         * 审批的
         */
        List<HistoricActivityInstance> list1 = historyService.createHistoricActivityInstanceQuery()
                .taskAssignee(employee.getEmpid() + "").finished()
                .list();
        for (HistoricActivityInstance hai : list1) {
            processInstanceIds.add(hai.getProcessInstanceId());
        }

        /**
         * 作为候选人
         */
       // List<HistoricIdentityLinkLog> candidates = historyService.createHistoricIdentityLinkLogQuery().type("candidate").list();
      //  for (HistoricIdentityLinkLog candidate : candidates) {
      //      processInstanceIds.add(candidate.getRootProcessInstanceId());
      //  }

        //加上已提交的
        /**
         * 查询分页总数
         */
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
        if(processInstanceIds.size()>0){
            historicProcessInstanceQuery.or()
                    .variableValueEquals("employee", employee.getEmpid()+"")
                    .processInstanceIds(processInstanceIds).endOr()
                    .orderByProcessInstanceStartTime().desc();
        }else {
            historicProcessInstanceQuery
                    .variableValueEquals("employee", employee.getEmpid()+"")
                    .orderByProcessInstanceStartTime().desc();
        }


        if(filtProcessInstanceIds.size()>0){
            historicProcessInstanceQuery.processInstanceIds(filtProcessInstanceIds);
        }
        pageCount = historicProcessInstanceQuery.list().size();
        list = historicProcessInstanceQuery.listPage(startIndex, maxResult);


        if (list == null || list.isEmpty()) {
            return new RespInfo(255, "no process");
        } else {

            /**
             * 封装数据
             */
            List<ProcessRecord> processRecords = new ArrayList<>();
            HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery();
            for (HistoricProcessInstance processInstance : list) {
                String businessKey = processInstance.getBusinessKey();
                Map<String, Object> historicDetailVariableMap = camundaProcessService.getHistoricProcessVariableMap(processInstance.getId());

                if(historicDetailVariableMap.size() == 0){
                    continue;
                }

                ProcessRecord pr = new ProcessRecord();
                if (businessKey.startsWith("a")) {
                    //邀请
                    Appointment appointment = new Appointment();
                    appointment.setUserid(employee.getUserid());
                    appointment.setAgroup(businessKey.substring(1));
                    List<Appointment> appointmentList = appointmentService.getAppointmnetByAgroup(appointment);
                    if (null != appointmentList && appointmentList.size() > 0) {
                        pr.setPType(1);
                        pr.setUserid(employee.getUserid());
                        pr.setVid(businessKey.substring(1));
                        pr.setRid(processInstance.getId());
                        pr.setAppList(appointmentList);
                        pr.setAppTime(appointmentList.get(0).getAppointmentDate());
                        pr.setVisitType(appointmentList.get(0).getVisitType());
                        pr.setRvwEmpId(appointmentList.get(0).getEmpid() + "");
                        pr.setRvwEmpName(appointmentList.get(0).getEmpName());
                        pr.setVname(appointmentList.get(0).getName());
                        pr.setProcessInstanceId(processInstance.getId());
                        pr.setSubmitTime(processInstance.getStartTime());
                     }
                } else {
                    //预约
                    Visitor vt = new Visitor();
                    vt.setUserid(employee.getUserid());
                    try {
                        vt.setVgroup(Integer.parseInt(businessKey.substring(1)));
                    }catch (Exception e){
                        continue;
                    }
                    List<Visitor> visitorList = visitorService.getVisitorByVgroup(vt.getVgroup()+"");
                    if (null != visitorList && visitorList.size() > 0) {
                        pr.setPType(2);
                        pr.setUserid(employee.getUserid());
                        pr.setVid(businessKey.substring(1));
                        pr.setRid(processInstance.getId());
                        if (null != visitorList && visitorList.size() > 0) {
                            List<Appointment> appointmentList = new ArrayList<>();
                            for(Visitor visitor:visitorList){
                                Appointment appointment = BeanUtils.VisitorToAppointment(visitor);
//                                appointment.setStatus((Integer) historicDetailVariableMap.get("status"));
                                appointmentList.add(appointment);
                            }
                            pr.setAppList(appointmentList);
                            pr.setAppTime(visitorList.get(0).getAppointmentDate());
                            pr.setVisitType(visitorList.get(0).getVisitType());
                            pr.setRvwEmpId(visitorList.get(0).getEmpid() + "");
                            pr.setRvwEmpName(visitorList.get(0).getEmpName());
                            pr.setVname(visitorList.get(0).getVname());
                        }
                        pr.setSubmitTime(processInstance.getStartTime());
                        pr.setProcessInstanceId(processInstance.getId());

                    }
                }

                if(processInstance.getState().equals(HistoricProcessInstance.STATE_COMPLETED)){
                    if (historicDetailVariableMap.get("status") != null) {
                        pr.setStatus((Integer) historicDetailVariableMap.get("status"));
                    }
                }else if((processInstance.getState().equals(HistoricProcessInstance.STATE_ACTIVE))) {
                    pr.setStatus(0);
                }else{
                    pr.setStatus(5);
                }
                processRecords.add(pr);
            }
//            if (StringUtils.isNotEmpty(requestVisit.getName())) {
//                List<ProcessRecord> processRecordList = new ArrayList<>();
//                String name = requestVisit.getName();
//                for (ProcessRecord p : processRecords) {
//                    if (StringUtils.isNotEmpty(p.getSubEmpName()) && p.getSubEmpName().equals(name)) {
//                        processRecordList.add(p);
//                        continue;
//                    } else if (StringUtils.isNotEmpty(p.getVname()) && p.getVname().equals(name)) {
//                        processRecordList.add(p);
//                        continue;
//                    } else if (StringUtils.isNotEmpty(p.getRvwEmpName()) && p.getRvwEmpName().equals(name)) {
//                        processRecordList.add(p);
//                        continue;
//                    }
//                }
//                return  new RespInfo(0, "success", processRecordList);
//            }

            /**
             * 按提交事件倒序排序
             */
            Page<ProcessRecord> prPage = new Page<ProcessRecord>(currentPage , requestVisit.getRequestedCount(), pageCount);
            prPage.setList(processRecords);
            int pageTotal = (pageCount + requestVisit.getRequestedCount() - 1) / requestVisit.getRequestedCount();//总页数
            prPage.setLastPage((pageTotal == currentPage ? true : false));
            prPage.setFirstPage(currentPage == 1 ? true : false);
            return new RespInfo(0, "success", prPage);
        }
    }

    /**
     * 根据BusinessKey获取记录审批详情
     *
     * @param camunda
     * @return
     */
    @ApiOperation(value = "/getRecordsByCamundaByBusinessKey 根据BusinessKey获取记录审批详情", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getRecordsByCamundaBusinessKey")
    @ResponseBody
    public RespInfo getRecordsByCamundaByBusinessKey(
            @ApiParam(value = "Camunda 工作流Bean", required = true) @Validated @RequestBody Camunda camunda,
            HttpServletRequest request, BindingResult bindingResult) {
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        int pType = camunda.getPType();

        List<ProcessRecord> prlist = new ArrayList<>();
        int userid = 0;
        String processInstanceId = camunda.getProcessInstanceId();
        if(StringUtils.isBlank(processInstanceId)){
            return new RespInfo(ErrorEnum.E_400.getCode(), ErrorEnum.E_400.getMsg()+" processInstanceId");
        }

        /**
         * 邀请
         */
        if (pType == 1) {
            /**
             * 查询访客组
             */
            List<Appointment> appointmentList = new ArrayList<>();
            Appointment app = new Appointment();
            app.setUserid(camunda.getUserid());
            app.setAgroup(camunda.getVid());
            appointmentList = appointmentService.getAppointmnetByAgroup(app);
            if (null == appointmentList || appointmentList.isEmpty()) {
//                System.out.println("邀请记录为null");
                return new RespInfo(672, "no record ");
            }
            userid = appointmentList.get(0).getUserid();
        } else if (pType == 2) {
            /**
             * 预约
             */
            Visitor visitor = visitorService.getVisitorById(Integer.parseInt(camunda.getVid()));
            if (null == visitor) {
                System.out.println("预约记录为null");
                return new RespInfo(672, "no record ");
            }
            userid = visitor.getUserid();
        }

        /**
         * 历史task会话查询
         */
        List<HistoricProcessInstance> processInstances = processEngine.getHistoryService().createHistoricProcessInstanceQuery()
//                    .processInstanceBusinessKey("a" + camunda.getVid())
                .processInstanceId(processInstanceId)
                .list();
        if (null == processInstances) {
//                System.out.println("没有processInstances记录");
            return new RespInfo(672, "no processInstances ");
        }
        List<ProcessRecord> processRecords = new ArrayList<>();
        if (processInstances.size() > 0) {
            for (HistoricProcessInstance processInstance : processInstances) {
//                if ("visitorApprove".equals(processInstance.getProcessDefinitionKey())) {
                    List<ProcessRecord> processRecordList = camundaProcessService.getHistoryPRByProcessInstanceId(processInstance.getId(), userid);
                    processRecords.addAll(processRecordList);
//                }
            }
        }
        /**
         * 封装返回数据
         */
        if (processRecords.size() > 0) {
            return new RespInfo(0, "success", processRecords);
        } else {
            return new RespInfo(255, "no process");
        }
    }

    /**
     * 根据会话taskid完成当前节点审批
     *
     * @param camunda
     * @return
     */
    @ApiOperation(value = "/completeProcessRecordByTaskid 根据会话taskid完成当前节点审批", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/completeProcessRecordByTaskid")
    @ResponseBody
    public RespInfo completeProcessRecordByTaskid(
            @ApiParam(value = "Camunda 工作流Bean", required = true) @Validated @RequestBody Camunda camunda,
            HttpServletRequest request, BindingResult bindingResult) {
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

//        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
//        if (!AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())
//        || !authToken.getLoginAccountId().equals(String.valueOf(camunda.getSubEmpId()))) {
//            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
//        }

        Employee employee = employeeService.getEmployee(camunda.getSubEmpId());
        if (null == employee) {
            return new RespInfo(55, "no employee");
        }
        UserInfo userInfo = userService.getUserInfo(camunda.getUserid());
        if (null == userInfo) {
            return new RespInfo(1, "invalid user");
        }

        // 先设置登录用户
        IdentityService identityService = processEngine.getIdentityService();
        identityService.setAuthenticatedUserId(employee.getEmpid()+"");

        if(camunda.getStatus() == 3){
            //退回
            proback(camunda);
        }else {
            TaskService taskService = processEngine.getTaskService();
            Task task = taskService.createTaskQuery()
                    .taskId(camunda.getTaskid())
                    .or()
                    .taskAssignee(employee.getEmpid() + "")
                    .taskCandidateUser(employee.getEmpid() + "").endOr()
                    .active().singleResult();
            if (task == null) {
                return new RespInfo(255, "no process");
            } else {
                Map<String, Object> param = new HashMap<>();
                param.put("status", camunda.getStatus());
                if (camunda.getAllVisitors() != null) {
                    param.put("allVisitors", camunda.getAllVisitors());
                }
                if (camunda.getAddVisitors() != null) {
                    param.put("addVisitors", camunda.getAddVisitors());
                }
                if (camunda.getDelVisitors() != null) {
                    param.put("delVisitors", camunda.getDelVisitors());
                }
                if(camunda.getRemark() != null) {
                    taskService.createComment(task.getId(), task.getProcessInstanceId(), camunda.getRemark());
                }
                if(StringUtils.isEmpty(task.getAssignee()))
                {
                    taskService.claim(task.getId(), employee.getEmpid() + "");
                }
                //taskService.setAssignee(task.getId(), employee.getEmpid() + "");
//            if(camunda.getLeader() != null) {
//                taskService.setVariable(task.getId(), "leader", camunda.getLeader());
//                taskService.setVariable(task.getId(), "leaders", camunda.getLeader());
//            }
                taskService.complete(task.getId(), param);
                SysLog.info("提交会话id" + task.getId());
            }
        }

            return new RespInfo(0, "success");
    }

    //退回审批
    public void proback(Camunda camunda){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        HistoryService historyService = processEngine.getHistoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //获取当前任务，未办理任务id
        HistoricTaskInstance currTask = historyService.createHistoricTaskInstanceQuery()
                .taskId(camunda.getTaskid())
                .singleResult();
        //获取流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(currTask.getProcessInstanceId())
                .singleResult();
        //获取流程定义
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                .getDeployedProcessDefinition(currTask.getProcessDefinitionId());

        ActivityImpl currActivity = (processDefinitionEntity)
                .findActivity(currTask.getTaskDefinitionKey());
        //清除当前活动出口
        List<PvmTransition> originPvmTransitionList = new ArrayList<PvmTransition>();
        List<PvmTransition> pvmTransitionList = currActivity.getOutgoingTransitions();
        for (PvmTransition pvmTransition : pvmTransitionList) {
            originPvmTransitionList.add(pvmTransition);
        }
        pvmTransitionList.clear();
        //查找上一个user task节点
        List<HistoricActivityInstance> historicActivityInstances = historyService
                .createHistoricActivityInstanceQuery().activityType("userTask")
                .processInstanceId(processInstance.getId())
                .finished()
                .orderByHistoricActivityInstanceEndTime().asc().list();
        if(historicActivityInstances.size() == 0){
            //在父流程中查找
            historicActivityInstances = historyService
                    .createHistoricActivityInstanceQuery().activityType("userTask")
                    .processInstanceId(processInstance.getRootProcessInstanceId())
                    .finished()
                    .orderByHistoricActivityInstanceEndTime().asc().list();


        }

        TransitionImpl transitionImpl = null;
        if (historicActivityInstances.size() > 0) {
            processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                    .getDeployedProcessDefinition(historicActivityInstances.get(0).getProcessDefinitionId());

            ActivityImpl lastActivity = processDefinitionEntity.findActivity(historicActivityInstances.get(0).getActivityId());
            //创建当前任务的新出口
            transitionImpl = currActivity.createOutgoingTransition(lastActivity.getId());
            transitionImpl.setDestination(lastActivity);
        }
        // 完成任务
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .taskDefinitionKey(currTask.getTaskDefinitionKey()).list();
        for (Task task : tasks) {
            Map<String, Object> param = new HashMap<>();
            param.put("status", camunda.getStatus());
            if(camunda.getRemark() != null) {
                taskService.createComment(task.getId(), task.getProcessInstanceId(), camunda.getRemark());
            }
            taskService.complete(task.getId(),param);
            //删除任务记录，但是activity记录还在，可用于撤回的情况
//            historyService.deleteHistoricTaskInstance(task.getId());
        }
        // 恢复方向
        currActivity.getOutgoingTransitions().remove(transitionImpl);
        for (PvmTransition pvmTransition : originPvmTransitionList) {
            pvmTransitionList.add(pvmTransition);
        }

    }

    @ApiOperation(value = "/getProcessInstanceInfo 获取审批信息", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getProcessInstanceInfo")
    @ResponseBody
    public RespInfo getProcessInstanceInfo(
            @ApiParam(value = "Camunda 工作流Bean", required = true) @Validated @RequestBody Camunda camunda,
            HttpServletRequest request, BindingResult bindingResult) {
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        //        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
//        if (!AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())
//        || !authToken.getLoginAccountId().equals(task.getAssignee())) {
//            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
//        }

        Camunda response = new Camunda();
        Map<String, Object> historicDetailVariableMap = camundaProcessService.getHistoricProcessVariableMap(camunda.getProcessInstanceId());

        if(historicDetailVariableMap.size() == 0){
            return new RespInfo(ErrorEnum.E_703.getCode(), ErrorEnum.E_703.getMsg());
        }

        response.setAllVisitors((List<String>) historicDetailVariableMap.get("allVisitors"));
        response.setAddVisitors((List<String>) historicDetailVariableMap.get("addVisitors"));
        response.setDelVisitors((List<String>) historicDetailVariableMap.get("delVisitors"));
        response.setLeader((String) historicDetailVariableMap.get("leader"));
        response.setEmpid((String) historicDetailVariableMap.get("employee"));
        if(historicDetailVariableMap.get("status") != null) {
            response.setStatus((Integer) historicDetailVariableMap.get("status"));
        }
        return new RespInfo(0, "success",response);
    }

    @ApiOperation(value = "/deployeeProcess 部署流程引擎模板", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/deployeeProcess")
    @ResponseBody
    public RespInfo deployeeProcess(@RequestParam(value = "filename") MultipartFile file, HttpServletRequest request) {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        try {
            String filename = file.getOriginalFilename();
            InputStream is = file.getInputStream();
            Deployment deployment = repositoryService.createDeployment()
                    .name(filename.replace(".bpmn",""))
                    .addInputStream(filename, is)
                    .deploy();
            System.out.println(deployment.getDeploymentTime() + " --->" + file.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/addProcessRecord 创建审批", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "    {\n" +
                    "    }\n"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addProcessRecord")
    @ResponseBody
    public RespInfo addProcessRecord(
            @ApiParam(value = "Camunda 工作流Bean", required = true) @Validated @RequestBody Camunda camunda,
            HttpServletRequest request, BindingResult bindingResult) {
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (!AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())
        || !authToken.getLoginAccountId().equals(String.valueOf(camunda.getSubEmpId()))) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        Employee employee = employeeService.getEmployee(Integer.parseInt(authToken.getLoginAccountId()));
        if (null == employee) {
            return new RespInfo(55, "no employee");
        }
        UserInfo userInfo = userService.getUserInfo(employee.getUserid());
        if (null == userInfo) {
            return new RespInfo(1, "invalid user");
        }

        int ret = camundaProcessService.createApproveProcess(userInfo,employee,camunda);
        if(ret == 0) {
            return new RespInfo(ErrorEnum.E_0.getCode(), ErrorEnum.E_0.getMsg());
        }else{
            ErrorEnum errorEnum = ErrorEnum.getByCode(ret);
            return new RespInfo(errorEnum.getCode(),errorEnum.getMsg());
        }
    }


}
