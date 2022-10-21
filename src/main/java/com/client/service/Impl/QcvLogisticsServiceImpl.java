package com.client.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.client.bean.conver.ConverLogistics;
import com.client.bean.dto.QcvLogistics;
import com.client.bean.req.QcvLogisticsReq;
import com.client.bean.vo.QcvLogisticsVo;
import com.client.dao.QcvLogisticsMapper;
import com.client.service.QcvLogisticsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author ernest
 * @description 针对表【qcv_logistics】的数据库操作Service实现
 * @createDate 2022-10-19 10:14:43
 */
@Service
public class QcvLogisticsServiceImpl extends ServiceImpl<QcvLogisticsMapper, QcvLogistics>
        implements QcvLogisticsService {

    @Override
    public IPage<QcvLogistics> getQcvLogisticsListByOpenid(QcvLogisticsReq logistics) {
        IPage<QcvLogistics> qcvLogisticsIPage = new Page<>(logistics.getPageNum(), logistics.getPageSize());
        LambdaQueryWrapper<QcvLogistics> eq = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(logistics.getOpenid())) {
            eq.eq(QcvLogistics::getOpenid, logistics.getOpenid());
        }
        IPage<QcvLogistics> qcvLogistics = this.baseMapper.selectPage(qcvLogisticsIPage, eq);
        return qcvLogistics;
    }

    @Override
    public QcvLogisticsVo getLogisticsDetails(QcvLogisticsReq qcvLogisticsReq) {
        LambdaQueryWrapper<QcvLogistics> eq = new QueryWrapper<QcvLogistics>().lambda().eq(QcvLogistics::getSid, qcvLogisticsReq.getSid());
        QcvLogistics qcvLogistics = this.baseMapper.selectOne(eq);
        return ConverLogistics.getLogisticsVo(qcvLogistics);
    }

    @Override
    public QcvLogistics getLogisticsByPlateNum(String plateNum) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date startDateFormart = calendar.getTime();
        String startFormat = simpleDateFormat.format(startDateFormart);

        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(today);
        calendarEnd.set(Calendar.HOUR_OF_DAY, 0);
        calendarEnd.set(Calendar.MINUTE, 0);
        calendarEnd.set(Calendar.SECOND, 0);
        Date endDateFormart = calendarEnd.getTime();
        String endDate = simpleDateFormat.format(endDateFormart);
        LambdaQueryWrapper<QcvLogistics> queryWrapper = new LambdaQueryWrapper<QcvLogistics>();
        queryWrapper.between(QcvLogistics::getAppointmentstartdate,startFormat,endDate)
        //queryWrapper.between("appointmentStartDate",startFormat,endDate)
                .eq(QcvLogistics::getPlatenum,plateNum);
        //queryWrapper.eq(QcvLogistics::getPlatenum,plateNum);
        List<QcvLogistics> qcvLogistics = this.baseMapper.selectList(queryWrapper);
        if (!qcvLogistics.isEmpty()){
            if (){

            }
        }
        return null;
    }
}




