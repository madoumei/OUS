package com.utils.visitorUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.client.bean.Visitor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utils.SysLog;
import com.utils.httpUtils.HttpClientUtil;
import com.utils.jsonUtils.JacksonJsonUtil;
import org.apache.commons.lang3.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 访客工具类
 */
public class VisitorUtils {

    /**
     * 对接第三方系统发送预约审批
     *
     * @param visitors
     * @return
     */
    public static String sendApprovalButt(Visitor visitors,String url) throws JsonProcessingException {
        //String stringify = JsonUtil.stringify(visitors);
        Map<String, Object> params = new HashMap<>();
        params.put("vid", visitors.getVid());
        params.put("company", visitors.getCompany());
        params.put("name", visitors.getVname());
        params.put("phone", visitors.getVphone());
        params.put("email", visitors.getVemail());
        params.put("empPhone", visitors.getEmpPhone());
        params.put("empName", visitors.getEmpName());
//        params.put("empid", visitors.getEmpid());
        params.put("appointmentDate", visitors.getAppointmentDate());
        params.put("visitType", visitors.getVisitType());
//        params.put("userid", visitors.getUserid());
//        params.put("gid", visitors.getGid());
//        params.put("tid", visitors.getTid());
        params.put("vType", visitors.getvType());
        params.put("remark", visitors.getRemark());
        params.put("extendCol", visitors.getExtendCol());
        params.put("memberName", visitors.getMemberName());
        params.put("peopleCount", visitors.getPeopleCount());
//        params.put("clientNo", visitors.getClientNo());
        String s = HttpClientUtil.postJsonBodySourceNoReplace(url, 5000, params, "utf-8");
        if (ObjectUtils.isNotEmpty(s)) {
            JSONObject jsonObject = JSON.parseObject(s);
            SysLog.info("sendApprovalButt response "+s);
            return "1";
        } else {
            return "0";
        }
    }
}
