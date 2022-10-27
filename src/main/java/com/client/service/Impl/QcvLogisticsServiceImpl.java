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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
        eq.orderByDesc(QcvLogistics::getAppointmentstartdate);
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date today = new Date();
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(today);
        calendarEnd.set(Calendar.HOUR_OF_DAY, 23);
        calendarEnd.set(Calendar.MINUTE, 59);
        calendarEnd.set(Calendar.SECOND, 59);
        Date endDateFormart = calendarEnd.getTime();
        String endDate = simpleDateFormat.format(endDateFormart);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date startDateFormart = calendar.getTime();
        String startDate = simpleDateFormat.format(startDateFormart);
        LambdaQueryWrapper<QcvLogistics> queryWrapper = new LambdaQueryWrapper<QcvLogistics>();
        queryWrapper.between(QcvLogistics::getAppointmentstartdate, startDate, endDate)
                .eq(QcvLogistics::getPlatenum, plateNum);
        List<QcvLogistics> qcvLogistics = this.baseMapper.selectList(queryWrapper);
        List<QcvLogistics> qcvLogisticsListNew = new ArrayList<>();
        List<QcvLogistics> qcvLogisticsListOld = new ArrayList<>();
        QcvLogistics returnQcvlog = null;
        long date = new Date().getTime();

        if (!qcvLogistics.isEmpty()) {

            if (1 < qcvLogistics.size()) {
                for (QcvLogistics qcvLogistic : qcvLogistics) {
                    String appointmentstartdate = qcvLogistic.getAppointmentstartdate();
                    try {
                        Date parse = simpleDateFormat.parse(appointmentstartdate);
                        if (date - parse.getTime() < 0) {
                            qcvLogisticsListNew.add(qcvLogistic);
                        } else {
                            qcvLogisticsListOld.add(qcvLogistic);
                        }
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (1 < qcvLogisticsListNew.size()) {
                    List<QcvLogistics> collect = qcvLogisticsListNew.stream().sorted(Comparator.comparing(QcvLogistics::getAppointmentenddate)).collect(Collectors.toList());
                    returnQcvlog = collect.get(0);
                } else if (1 == qcvLogisticsListNew.size()) {
                    returnQcvlog = qcvLogisticsListNew.get(0);
                }

                if (qcvLogisticsListNew.isEmpty()) {
                    returnQcvlog = qcvLogisticsListOld.get(0);
                }
            }else{
                returnQcvlog = qcvLogistics.get(0);
            }
        }
        return returnQcvlog;
    }

    @Override
    public List<QcvLogistics> getTodayLogByPstatus(int i) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date today = new Date();
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(today);
        calendarEnd.set(Calendar.HOUR_OF_DAY, 23);
        calendarEnd.set(Calendar.MINUTE, 59);
        calendarEnd.set(Calendar.SECOND, 59);
        Date endDateFormart = calendarEnd.getTime();
        String endDate = simpleDateFormat.format(endDateFormart);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date startDateFormart = calendar.getTime();
        String startDate = simpleDateFormat.format(startDateFormart);

        LambdaQueryWrapper<QcvLogistics> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(QcvLogistics::getPstatus, 2)
                .between(QcvLogistics::getAppointmentstartdate, startDate, endDate);
        List<QcvLogistics> qcvLogisticsList = this.baseMapper.selectList(queryWrapper);

        return qcvLogisticsList;
    }
}




