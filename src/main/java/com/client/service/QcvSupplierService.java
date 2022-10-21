package com.client.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.client.bean.dto.QcvSupplier;
import com.client.bean.req.QcvSupplierReq;

/**
* @author ernest
* @description 针对表【qcv_supplier】的数据库操作Service
* @createDate 2022-10-20 18:04:40
*/
public interface QcvSupplierService extends IService<QcvSupplier> {

    IPage<QcvSupplier> getSupplier(QcvSupplierReq qcvSupplierReq);
}
