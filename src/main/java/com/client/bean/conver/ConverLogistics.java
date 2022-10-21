package com.client.bean.conver;

import com.client.bean.dto.QcvLogistics;
import com.client.bean.req.QcvLogisticsInsertReq;
import com.client.bean.req.QcvLogisticsReq;
import com.client.bean.vo.QcvLogisticsVo;
import org.springframework.beans.BeanUtils;

public class ConverLogistics {

    public static QcvLogistics getLogistics(QcvLogisticsInsertReq qcvLogisticsInsertReq) {
        QcvLogistics qcvLogistics = new QcvLogistics();
        BeanUtils.copyProperties(qcvLogisticsInsertReq, qcvLogistics);
        return qcvLogistics;
    }

    public static QcvLogistics getLogistics(QcvLogisticsReq qcvLogisticsInsertReq) {
        QcvLogistics qcvLogistics = new QcvLogistics();
        BeanUtils.copyProperties(qcvLogisticsInsertReq, qcvLogistics);
        return qcvLogistics;
    }

    public static QcvLogisticsVo getLogisticsVo(QcvLogistics qcvLogistics) {
        QcvLogisticsVo qcvLogisticsVo = new QcvLogisticsVo();
        BeanUtils.copyProperties(qcvLogistics, qcvLogisticsVo);
        return qcvLogisticsVo;
    }
}
