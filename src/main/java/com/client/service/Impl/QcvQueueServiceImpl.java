package com.client.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.client.bean.dto.QcvLogistics;
import com.client.bean.req.QcvLogQueueReq;
import com.client.bean.req.QueueReq;
import com.client.bean.vo.QcvLogQueueVo;
import com.client.dao.QcvLogisticsMapper;
import com.client.service.QcvQueueService;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.web.bean.PassRule;
import com.web.dao.PassRuleDao;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


@Service
@AllArgsConstructor
public class QcvQueueServiceImpl implements QcvQueueService {

    private final PassRuleDao passRuleDao;

    private final RedisTemplate redisTemplate;

    private final QcvLogisticsMapper qcvLogisticsMapper;

    private static final Gson gson = new Gson();

    @Override
    public IPage<PassRule> getPassRulePageList(QueueReq queueReq) {
        PassRule passRule = new PassRule();
        passRule.setUserid(queueReq.getUserid());
        IPage<PassRule> passRulePage = new Page<>(queueReq.getPageNum(), queueReq.getPageSize());
        IPage<PassRule> passRuleIPage = passRuleDao.selectPage(passRulePage, null);

        return passRuleIPage;
    }

    @Override
    public IPage<QcvLogQueueVo> getQueuePageList(QueueReq queueReq) {

        int pageNum = queueReq.getPageNum();
        int pageSize = queueReq.getPageSize();
        if (pageNum <= 0) {
            pageNum = 1;
        }
        int start = (pageNum - 1) * pageSize;
        int end = start + pageSize - 1;
        List<String> range = redisTemplate.opsForList().range(queueReq.getQueueName(), start, end);
        Type type = new TypeToken<List<QcvLogQueueReq>>() {
        }.getType();
        List<QcvLogQueueReq> logQueueReqs = gson.fromJson(range.toString(), type);
        List<QcvLogQueueVo> qcvLogQueueVos = new ArrayList<>();
        if (!logQueueReqs.isEmpty()) {
            for (QcvLogQueueReq logQueueReq : logQueueReqs) {
                LambdaQueryWrapper<QcvLogistics> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(QcvLogistics::getSid, logQueueReq.getSid());
                QcvLogistics qcvLogistics = qcvLogisticsMapper.selectOne(queryWrapper);
                if (null != qcvLogistics) {
                    QcvLogQueueVo qcvLogQueueVo = new QcvLogQueueVo();
                    qcvLogQueueVo.setQueueNum(logQueueReq.getQueueNum());
                    qcvLogQueueVo.setPlateNum(logQueueReq.getPlateNum());
                    qcvLogQueueVo.setCompany(qcvLogistics.getCompany());
                    qcvLogQueueVo.setStatus(qcvLogistics.getPstatus());
                    qcvLogQueueVo.setSid(logQueueReq.getSid());
                    qcvLogQueueVos.add(qcvLogQueueVo);
                }
            }
        }
        List<String> total = redisTemplate.opsForList().range(queueReq.getQueueName(), 0, -1);
        IPage<QcvLogQueueVo> iPage = new Page<QcvLogQueueVo>();
        iPage.setRecords(qcvLogQueueVos);
        iPage.setCurrent(start);
        iPage.setSize(pageSize);
        iPage.setTotal(total.size());
        int totalPages =(total.size()+pageSize-1)/pageSize;
        iPage.setPages(totalPages);

        return iPage;
    }
}
