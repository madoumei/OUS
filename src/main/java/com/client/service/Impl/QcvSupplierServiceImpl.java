package com.client.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.client.bean.dto.QcvSupplier;
import com.client.bean.req.QcvSupplierReq;
import com.client.dao.QcvSupplierMapper;
import com.client.service.QcvSupplierService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author ernest
 * @description 针对表【qcv_supplier】的数据库操作Service实现
 * @createDate 2022-10-20 18:04:40
 */
@Service
public class QcvSupplierServiceImpl extends ServiceImpl<QcvSupplierMapper, QcvSupplier>
        implements QcvSupplierService {

    @Override
    public IPage<QcvSupplier> getSupplier(QcvSupplierReq qcvSupplierReq) {
        IPage<QcvSupplier> qcvSupplierIPage = new Page<QcvSupplier>(qcvSupplierReq.getPageNum(),qcvSupplierReq.getPageNum());
        LambdaQueryWrapper<QcvSupplier> objectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(qcvSupplierReq.getName())){
            objectLambdaQueryWrapper.eq(QcvSupplier::getName,qcvSupplierReq.getName());
        }
        IPage<QcvSupplier> qcvSupplierIPage1 = this.baseMapper.selectPage(qcvSupplierIPage, objectLambdaQueryWrapper);
        return qcvSupplierIPage1;
    }
}




