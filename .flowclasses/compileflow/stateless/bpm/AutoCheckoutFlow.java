package compileflow.stateless.bpm;

import com.alibaba.compileflow.engine.common.utils.DataType;
import com.client.bean.Visitor;
import com.client.bean.Equipment;
import com.alibaba.compileflow.engine.runtime.instance.ProcessInstance;
import com.alibaba.compileflow.engine.ProcessEngineFactory;
import java.util.Map;
import java.lang.Integer;
import java.util.HashMap;
import com.alibaba.compileflow.engine.common.utils.ObjectFactory;
import com.alibaba.compileflow.engine.process.preruntime.generator.bean.BeanProvider;
import com.web.bean.UserInfo;
import com.client.service.VisitorService;

public class AutoCheckoutFlow implements ProcessInstance {

    private com.client.bean.Visitor vt = null;
    private com.web.bean.UserInfo userinfo = null;
    private com.client.bean.Equipment eq = null;
    private java.lang.Integer autoSignout = null;

    public Map<String, Object> execute(Map<String, Object> _pContext) throws Exception {
        vt = (Visitor)DataType.transfer(_pContext.get("vt"), Visitor.class);
        userinfo = (UserInfo)DataType.transfer(_pContext.get("userinfo"), UserInfo.class);
        eq = (Equipment)DataType.transfer(_pContext.get("eq"), Equipment.class);
        Map<String, Object> _pResult = new HashMap<>();
        //DecisionNode: 是否结束拜访判断
        visitorServiceAutoSignoutRouter();
        if (autoSignout == 1) {
            //要自动签出
            //AutoTaskNode: 签出
            ((VisitorService)BeanProvider.getBean("visitorService")).autoSignoutTask(vt, eq);
        }
        return _pResult;
    }

    private void visitorServiceAutoSignoutRouter() {
        autoSignout = ((VisitorService)BeanProvider.getBean("visitorService")).autoSignoutRouter(vt);
    }

}