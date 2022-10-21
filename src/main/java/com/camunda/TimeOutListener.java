package com.camunda;

import com.web.bean.Appointment;
import com.web.bean.UserInfo;
import com.web.service.AppointmentService;
import com.web.service.UserService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * camunda工作流超时触发的通知
 * 路径改动需修改对应bpmn图
 */
@Service
public class TimeOutListener implements ExecutionListener {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserService userService;

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("");
        System.out.println(dateFormat.format(new Date())+"超时触发BusinessKey："+execution.getBusinessKey());
        System.out.println(dateFormat.format(new Date())+"超时触发ProcessInstanceID："+execution.getProcessInstance().getId());
        String businessKey = execution.getBusinessKey();
        String userid = (String) execution.getVariable("userid");
        UserInfo userInfo = userService.getUserInfo(Integer.parseInt(userid));
        if (businessKey.startsWith("a")){
            //邀请
            Appointment appointment = new Appointment();
            appointment.setUserid(userInfo.getUserid());
            appointment.setAgroup(businessKey.substring(1));
            List<Appointment> appointments = appointmentService.getAppointmnetByAgroup(appointment);
            if (null != appointments && appointments.size()>0){
                for (Appointment a : appointments) {
                    if (1==a.getPermission() || 2 == a.getPermission()){
                        continue;
                    }
                    a.setPermission(3);
                    appointmentService.updateAppointmentPermission(a);
                }
            }
        }else {
            //预约
        }
    }
}
