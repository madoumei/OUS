package compileflow.stateless.bpm;

import com.alibaba.compileflow.engine.common.utils.DataType;
import com.client.service.VisitorService;
import java.util.Map;
import com.alibaba.compileflow.engine.process.preruntime.generator.bean.BeanProvider;
import java.lang.String;
import com.alibaba.compileflow.engine.ProcessEngineFactory;
import com.web.bean.UserInfo;
import com.web.service.AppointmentService;
import java.util.HashMap;
import com.alibaba.compileflow.engine.runtime.instance.ProcessInstance;
import java.lang.Integer;
import com.client.bean.Visitor;
import com.alibaba.compileflow.engine.common.utils.ObjectFactory;
import com.web.service.MessageService;

public class AutoCheckinFlow implements ProcessInstance {

    private com.web.bean.UserInfo userinfo = null;
    private com.client.bean.Visitor vtParam = null;
    private com.client.bean.Visitor vt = null;
    private java.lang.Integer autoSignin = null;

    public Map<String, Object> execute(Map<String, Object> _pContext) throws Exception {
        userinfo = (UserInfo)DataType.transfer(_pContext.get("userinfo"), UserInfo.class);
        vtParam = (Visitor)DataType.transfer(_pContext.get("vtParam"), Visitor.class);
        Map<String, Object> _pResult = new HashMap<>();
        //DecisionNode: 是否自动签到
        visitorServiceAutoSigninRouter();
        if (autoSignin ==1) {
            //自动签到
            //AutoTaskNode: 生成签到数据
            vt = ((AppointmentService)BeanProvider.getBean("appointmentService")).addSigninTask(vtParam, userinfo);
            //AutoTaskNode: 发送访客签到通知
            ((MessageService)BeanProvider.getBean("messageService")).sendCommonNotifyEvent(vt, "visitorArrived");
        }
        _pResult.put("vt", vt);
        return _pResult;
    }

    private void visitorServiceAutoSigninRouter() {
        autoSignin = ((VisitorService)BeanProvider.getBean("visitorService")).autoSigninRouter(vtParam);
    }

}