package com.client.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.client.bean.dto.QcvLogistics;
import com.client.bean.req.QcvLogisticsReq;
import com.client.bean.vo.QcvLogisticsVo;

import java.util.List;


/**
* @author ernest
* @description 针对表【qcv_logistics】的数据库操作Service
* @createDate 2022-10-19 10:14:43
*/
public interface QcvLogisticsService extends IService<QcvLogistics> {

    IPage<QcvLogistics> getQcvLogisticsListByOpenid(QcvLogisticsReq logistics);

    QcvLogisticsVo getLogisticsDetails(QcvLogisticsReq qcvLogisticsReq);

    QcvLogistics getLogisticsByPlateNum(String plateNum);

    List<QcvLogistics> getTodayLogByPstatus(int i);
}
