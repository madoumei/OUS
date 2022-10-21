package com.client.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.client.bean.conver.ConverLogistics;
import com.client.bean.dto.QcvLogistics;
import com.client.bean.req.QcvLogisticsInsertReq;
import com.client.bean.req.QcvLogisticsReq;
import com.client.bean.vo.QcvLogisticsVo;
import com.client.service.QcvLogisticsService;
import com.config.exception.ErrorEnum;
import com.web.bean.AuthToken;
import com.web.bean.PassRule;
import com.web.bean.RespInfo;
import com.web.service.PassRuleService;
import com.web.service.TokenServer;
import com.web.service.impl.PassRuleServiceImpl;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/")
@AllArgsConstructor
public class QcvLogisticsController {

    private final QcvLogisticsService qcvLogisticsService;

    private final TokenServer tokenServer;

    private final PassRuleService passRuleService;

    @PostMapping("/addLogistics")
    public RespInfo addLogistics(@RequestBody QcvLogisticsInsertReq qcvLogistics, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_VISITOR.equals(authToken.getAccountRole()))) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        int userid = authToken.getUserid();
        qcvLogistics.setUserid(userid);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date parse = simpleDateFormat.parse(qcvLogistics.getAppointmentstartdate());
            int i = PassRuleServiceImpl.Confirm_today(parse, qcvLogistics.getDatejson());
            if (-1 == i) {
                return new RespInfo(-1, "date is not allow");
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        //预约
        qcvLogistics.setPstatus(1);
        boolean b = qcvLogisticsService.saveOrUpdate(ConverLogistics.getLogistics(qcvLogistics));
        if (b) {
            return new RespInfo(0, "success");
        } else {
            return new RespInfo(-1, "fail");
        }
    }

    @PostMapping("/getLogPassRuleList")
    public RespInfo getPassRuleList(@RequestBody PassRule pr, HttpServletRequest request) {
        /** AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
         if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
         && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
         || authToken.getUserid() != pr.getUserid()) {
         return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
         }*/

        pr = passRuleService.getPassRuleByRname(pr);
        if (ObjectUtils.isEmpty(pr)) {
            return new RespInfo(-1, "fail");
        }

        List<PassRule> prList = passRuleService.getPassRuleList(pr);

        if (!prList.isEmpty()) {
            for (PassRule passRule : prList) {
                System.out.println(passRule);
            }
        }


        return new RespInfo(0, "success", prList);
    }

    /**
     * 获取物流信息列表
     *
     * @param qcvLogisticsReq
     * @param request
     * @return
     */
    @PostMapping("/getLogisticsPage")
    public RespInfo getLogisticsPage(@RequestBody(required = false) QcvLogisticsReq qcvLogisticsReq, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_VISITOR.equals(authToken.getAccountRole()))) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        IPage<QcvLogistics> logisticsVos = new Page<>();
        //员工
        if ("7".equalsIgnoreCase(authToken.getAccountRole())) {
            logisticsVos = qcvLogisticsService.getQcvLogisticsListByOpenid(qcvLogisticsReq);
        } else if ("8".equalsIgnoreCase(authToken.getAccountRole())) {
            logisticsVos = qcvLogisticsService.getQcvLogisticsListByOpenid(qcvLogisticsReq);
        }
        return new RespInfo(0, "success", logisticsVos);
    }

    @PostMapping("/getLogisticsDetails")
    public RespInfo getLogisticsDetails(@RequestBody QcvLogisticsReq qcvLogisticsReq, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_VISITOR.equals(authToken.getAccountRole()))) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        QcvLogisticsVo qcvLogisticsVo = qcvLogisticsService.getLogisticsDetails(qcvLogisticsReq);
        return new RespInfo(0, "success", qcvLogisticsVo);
    }
}
