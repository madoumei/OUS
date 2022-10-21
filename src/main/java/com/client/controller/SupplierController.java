package com.client.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.client.bean.conver.ConverSupplier;
import com.client.bean.dto.QcvSupplier;
import com.client.bean.req.QcvSupplierReq;
import com.client.service.QcvSupplierService;
import com.web.bean.RespInfo;
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

    @PostMapping("/getSupplierList")
    public RespInfo getSupplier(@RequestBody(required = false) QcvSupplierReq qcvSupplierReq, HttpServletRequest request) {

        IPage<QcvSupplier> qcvSupplierReqIPage = qcvSupplierService.getSupplier(qcvSupplierReq);
        return new RespInfo(0, "success", qcvSupplierReqIPage);
    }

    @PostMapping("/addSupplier")
    public RespInfo addSupplier(@RequestBody QcvSupplierReq qcvSupplierReq) {
        boolean b = qcvSupplierService.saveOrUpdate(ConverSupplier.getQcvSupplier(qcvSupplierReq));
        if (b) {
            return new RespInfo(0, "success");
        } else {
            return new RespInfo(-1, "fail");
        }
    }

    @PostMapping("/delSupplier")
    public RespInfo delSupplier(@RequestBody QcvSupplierReq qcvSupplierReq) {
        boolean b = qcvSupplierService.removeById(ConverSupplier.getQcvSupplier(qcvSupplierReq));
        if (b) {
            return new RespInfo(0, "success");
        } else {
            return new RespInfo(-1, "fail");
        }
    }
}
