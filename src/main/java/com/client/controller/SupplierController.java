package com.client.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.client.bean.conver.ConverSupplier;
import com.client.bean.dto.QcvSupplier;
import com.client.bean.req.QcvSupplierReq;
import com.client.service.QcvSupplierService;
import com.config.exception.ErrorEnum;
import com.web.bean.AuthToken;
import com.web.bean.RespInfo;
import com.web.service.TokenServer;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/")
@AllArgsConstructor
public class SupplierController {

    private final QcvSupplierService qcvSupplierService;

    private final TokenServer tokenServer;

    @PostMapping("/getSupplierList")
    public RespInfo getSupplier(@RequestBody(required = false) QcvSupplierReq qcvSupplierReq, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_VISITOR.equals(authToken.getAccountRole()))) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        IPage<QcvSupplier> qcvSupplierReqIPage = qcvSupplierService.getSupplier(qcvSupplierReq);
        return new RespInfo(0, "success", qcvSupplierReqIPage);
    }

    @PostMapping("/addSupplier")
    public RespInfo addSupplier(@RequestBody QcvSupplierReq qcvSupplierReq, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        boolean b = qcvSupplierService.saveOrUpdate(ConverSupplier.getQcvSupplier(qcvSupplierReq));
        if (b) {
            return new RespInfo(0, "success");
        } else {
            return new RespInfo(-1, "fail");
        }
    }

    @PostMapping("/delSupplier")
    public RespInfo delSupplier(@RequestBody QcvSupplierReq qcvSupplierReq, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        boolean b =  qcvSupplierService.removeBatchByIds(qcvSupplierReq.getIds());
        // = qcvSupplierService.removeById(ConverSupplier.getQcvSupplier(qcvSupplierReq));
        if (b) {
            return new RespInfo(0, "success");
        } else {
            return new RespInfo(-1, "fail");
        }
    }
}
