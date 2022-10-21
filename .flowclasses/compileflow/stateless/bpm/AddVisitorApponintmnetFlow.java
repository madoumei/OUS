package compileflow.stateless.bpm;

import com.alibaba.compileflow.engine.common.utils.DataType;
import com.alibaba.compileflow.engine.runtime.instance.ProcessInstance;
import com.client.service.VisitorService;
import java.util.HashMap;
import java.lang.String;
import java.lang.Integer;
import com.client.bean.Visitor;
import java.util.Map;
import com.alibaba.compileflow.engine.process.preruntime.generator.bean.BeanProvider;
import com.web.service.UserService;
import com.web.service.MessageService;
import com.alibaba.compileflow.engine.ProcessEngineFactory;
import com.alibaba.compileflow.engine.common.utils.ObjectFactory;

public class AddVisitorApponintmnetFlow implements ProcessInstance {

    private java.lang.String employee = "";
    private java.lang.String visitor = "";
    private java.lang.Integer userid = null;
    private java.lang.Integer subid = null;
    private java.lang.Integer vid = null;
    private java.lang.Integer tid = null;
    private java.lang.String vType = "";
    private java.lang.Integer businessKey = null;
    private java.lang.Integer needPermission = null;
    private java.lang.Integer needProcess = null;

    public Map<String, Object> execute(Map<String, Object> _pContext) throws Exception {
        employee = (String)DataType.transfer(_pContext.get("employee"), String.class);
        visitor = (String)DataType.transfer(_pContext.get("visitor"), String.class);
        userid = (Integer)DataType.transfer(_pContext.get("userid"), Integer.class);
        subid = (Integer)DataType.transfer(_pContext.get("subid"), Integer.class);
        vid = (Integer)DataType.transfer(_pContext.get("vid"), Integer.class);
        tid = (Integer)DataType.transfer(_pContext.get("tid"), Integer.class);
        vType = (String)DataType.transfer(_pContext.get("vType"), String.class);
        Map<String, Object> _pResult = new HashMap<>();
        //DecisionNode: 是否要授权
        userServiceNeedPermission();
        if (needPermission==0) {
            //不需要授权
            //AutoTaskNode: 自动授权
            ((VisitorService)BeanProvider.getBean("visitorService")).autoPermission(vid, tid, vType);
            //DecisionNode: 是否要审批
            userServiceNeedProcess();
            if (needProcess==0) {
                //不要审批
                //AutoTaskNode: 自动审批
                ((VisitorService)BeanProvider.getBean("visitorService")).autopproval(vid);
                //AutoTaskNode: 通知被访人有访客预约
                ((MessageService)BeanProvider.getBean("messageService")).sendCommonNotifyEvent((Visitor)DataType.transfer(visitor, Visitor.class), "addAppointment");
            } else {
                //需要审批
                //AutoTaskNode: 通知授权
                ((MessageService)BeanProvider.getBean("messageService")).sendAppoinmentPermissionNotify((Visitor)DataType.transfer(visitor, Visitor.class));
            }
        } else {
            //需要授权
            //AutoTaskNode: 通知授权
            ((MessageService)BeanProvider.getBean("messageService")).sendAppoinmentPermissionNotify((Visitor)DataType.transfer(visitor, Visitor.class));
        }
        _pResult.put("businessKey", businessKey);
        return _pResult;
    }

    private void userServiceNeedPermission() {
        needPermission = ((UserService)BeanProvider.getBean("userService")).needPermission(userid, subid);
    }

    private void userServiceNeedProcess() {
        needProcess = ((UserService)BeanProvider.getBean("userService")).needProcess(userid, subid);
    }

}