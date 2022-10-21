package com.web.service;

import com.web.bean.*;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.task.Task;

import java.util.List;
import java.util.Map;

public interface CamundaProcessService {

    int createApproveProcess(UserInfo userInfo, Employee employee, Camunda camunda);

    /**
     * 获取历史流程中每个activity的定义参数
     * @param activityInstanceId
     * @return
     */
    public Map<String,Object> getHistoricDetailVariableMap(String activityInstanceId);

    /**
     * 获取历史流程中每个定义参数的最新值
     * @param processInstanceId
     * @return
     */
    public Map<String, Object> getHistoricProcessVariableMap(String processInstanceId);

    /**
     * 根据task获取审批记录
     * @param task
     * @return
     */
    ProcessRecord getProcessRecodesByTask(Task task);

    /**
     * 根据流程实例id获取历史审批记录
     * @param ProcessInstanceId
     * @return
     */
    List<ProcessRecord> getHistoryPRByProcessInstanceId(String ProcessInstanceId,int userid);

    List<Employee> getRoleEmpByGroupRoles(String groupValue, String rolesVaule, UserInfo userInfo);

    /**
     * 疫情模式结束，自动完成审批
     * @param processInstanceId
     * @return
     */
    int completeProcessInstance(String processInstanceId);
}
