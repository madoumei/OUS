package com.client.bean.conver;

import com.client.bean.dto.QcvSupplier;
import com.client.bean.req.QcvSupplierReq;
import org.springframework.beans.BeanUtils;

public class ConverSupplier {
    public static QcvSupplier getQcvSupplier(QcvSupplierReq qcvSupplierReq) {
        QcvSupplier qcvSupplier = new QcvSupplier();
        BeanUtils.copyProperties(qcvSupplierReq, qcvSupplier);
        return qcvSupplier;
    }
}
