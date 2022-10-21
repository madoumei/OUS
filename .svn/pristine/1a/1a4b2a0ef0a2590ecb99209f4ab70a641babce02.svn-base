package com.web.controller;

import com.client.bean.Gate;
import com.client.bean.RequestVisit;
import com.client.bean.Visitor;
import com.client.service.VisitorService;
import com.config.exception.ErrorEnum;
import com.web.bean.AuthToken;
import com.web.bean.RespInfo;
import com.web.bean.UserInfo;
import com.web.bean.VisitorType;
import com.web.service.AddressService;
import com.web.service.TokenServer;
import com.web.service.UserService;
import com.web.service.VisitorTypeService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/*")
public class ApprovalController {

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private TokenServer tokenServer;

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate strRedisTemplate;

    @Autowired
    private VisitorTypeService visitorTypeService;

    @Autowired
    private AddressService addressService;

    /**
     * 审批回调
     *
     * @param req
     * @param request
     * @return
     */
    @ApiOperation(value = "/approvalCallback 审批回调", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"vid\":430438,\n" +
                    "    \"type\":\"p\",\n" +
                    "    \"vType\":\"普通访客#Normal Visitor\",\n" +
                    "    \"tid\":221,\n" +
                    "    \"gataName\":1,\n" +
                    "    \"remark\":\"稍后我就来了\",\n" +
                    "    \"vtExtendCol\":\"{\\\"access\\\":\\\"\\\",\\\"remark\\\":\\\"稍后我就来了\\\"}\"\n" +
                    "}"
    )
    @PostMapping("/approvalCallback")
    public RespInfo updateApprovalCallback(@RequestBody RequestVisit req,
                                           HttpServletRequest request) {

        // AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        /**AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
         if (!AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())
         && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())) {
         return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
         }*/
        int userid = req.getUserid();
//        if (authToken.getUserid() != userid) {
//            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
//        }

        String permissionName = "manager";
        //管理员：校验是否是所属userid
        //子账号用于第三方调用
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (!AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }


        try {
            int vid = req.getVid();
            Visitor v = visitorService.getVisitorById(vid);
            if (null == v) {
                return new RespInfo(ErrorEnum.E_057.getCode(), ErrorEnum.E_057.getMsg());
            }
            /**if (v.getPermission() != 0) {
             return new RespInfo(ErrorEnum.E_070.getCode(), ErrorEnum.E_070.getMsg());
             }*/
            UserInfo userinfo = userService.getUserInfo(v.getUserid());
            if (userinfo == null) {
                return new RespInfo(ErrorEnum.E_001.getCode(), ErrorEnum.E_001.getMsg());
            }

            v.setPermissionName(permissionName);

            VisitorType visitorType = new VisitorType();
            visitorType.setUserid(userinfo.getUserid());
            visitorType.setvType(req.getvType());
            if (org.apache.commons.lang.StringUtils.isNotBlank(req.getCategory())) {
                if ("物流".equalsIgnoreCase(req.getCategory())) {
                    visitorType.setCategory(1);
                } else if ("访客".equalsIgnoreCase(req.getCategory())) {
                    visitorType.setCategory(2);
                }
            }

            List<VisitorType> visitorTypeList = visitorTypeService.getVisitorType(visitorType);
            if (!visitorTypeList.isEmpty()) {
                v.setTid(visitorTypeList.get(0).getTid());
            }else{
                return new RespInfo(104,"visitor type is null");
            }
            //v.setTid(req.getTid());
            v.setvType(req.getvType());
            if (com.config.qicool.common.utils.StringUtils.isNotEmpty(req.getVtExtendCol())) {
                v.setExtendCol(req.getVtExtendCol());
            }
            v.setArea(req.getArea());
            v.setMeetingPoint(req.getMeetingPoint());
            /**if(com.config.qicool.common.utils.StringUtils.isNotEmpty(req.getGid())) {
             v.setGid(req.getGid() + "");
             }*/
            //根据门岗名查询门岗详情
            String gateName = req.getGateName();
            Gate gate = new Gate();
            gate.setGname(gateName);
            gate.setUserid(userinfo.getUserid());
            Gate gateByName = addressService.getGateByName(gate);
            if (ObjectUtils.isNotEmpty(gateByName)) {
                v.setGid(gateByName.getGid() + "");
            }else{
                return new RespInfo(125,"gata is null");
            }
            v.setRemark(req.getRemark());
            v.addExtendValue("tid", v.getTid() + "");
            v.addExtendValue("gid", v.getGid());
            visitorService.addExtendSetting(userinfo, v);


            if ("p".equals(req.getType())) {
                v.setPermission(VisitorService.PERMISSION_ACCEPT);
                visitorService.updateGroupPermission(v);
            } else {
                v.setPermission(VisitorService.PERMISSION_REJECT);
                visitorService.updateGroupPermission(v);
            }

            ValueOperations<String, String> valueOperations = strRedisTemplate.opsForValue();
            if (com.config.qicool.common.utils.StringUtils.isNotBlank(valueOperations.get("dfvid_" + v.getVid()))) {
                strRedisTemplate.delete("dfvid_" + v.getVid());
            }

            return new RespInfo(0, "success");
        } finally {
        }
    }
}
