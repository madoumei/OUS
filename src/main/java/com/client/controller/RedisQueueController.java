package com.client.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.client.bean.dto.QcvLogistics;
import com.client.bean.req.QcvLogQueueReq;
import com.client.bean.req.QueueReq;
import com.client.bean.vo.QcvLogQueueVo;
import com.client.service.QcvLogisticsService;
import com.client.service.QcvQueueService;
import com.config.exception.ErrorEnum;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.utils.algorithmUtils.QueueUtil;
import com.web.bean.AuthToken;
import com.web.bean.PassRule;
import com.web.bean.RespInfo;
import com.web.service.TokenServer;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
@AllArgsConstructor
public class RedisQueueController {


    private final RedisTemplate redisTemplate;

    private final QcvLogisticsService qcvLogisticsService;

    private final QcvQueueService qcvQueueService;

    private final TokenServer tokenServer;

    private static Gson gson = new Gson();


    @PostMapping("/addQueue")
    public RespInfo addQueue(@RequestParam(value = "plateNum", required = false) String plateNum) {
        QcvLogistics qcvLogistics = qcvLogisticsService.getLogisticsByPlateNum(plateNum);
        Type type = new TypeToken<List<QcvLogQueueReq>>() {
        }.getType();
        if (null != qcvLogistics) {
            String rName = qcvLogistics.getRname();
            List<String> range = redisTemplate.opsForList().range(rName, 0, -1);
            List<QcvLogQueueReq> qcvLogQueueReqs = new ArrayList<>();
            //当队列不为空时
            if (!range.isEmpty()) {
                qcvLogQueueReqs = gson.fromJson(range.toString(), type);
            }
            if (!qcvLogQueueReqs.isEmpty()) {
                for (QcvLogQueueReq qcvLogQueueReq : qcvLogQueueReqs) {
                    if (qcvLogQueueReq.getSid() == qcvLogistics.getSid()) {
                        return new RespInfo(ErrorEnum.E_1301.getCode(), ErrorEnum.E_1301.getMsg());
                    }
                }
            }
            List<QcvLogistics> qcvLogisticsReqs = qcvLogisticsService.getTodayLogByPstatus(2);
            QcvLogQueueReq logQueueReq = new QcvLogQueueReq();
            logQueueReq.setSid(qcvLogistics.getSid());
            logQueueReq.setPlateNum(plateNum);
            logQueueReq.setQueueNum(range.size() + 1);
            if (!qcvLogisticsReqs.isEmpty()) {
                String qcvLogQueReq = QueueUtil.getAddQueueIndex(qcvLogisticsReqs, qcvLogistics, qcvLogQueueReqs);
                if (StringUtils.isNotEmpty(qcvLogQueReq)) {

                    logQueueReq.setSid(qcvLogistics.getSid());
                    //logQueueReq.setPlateNum(plateNum);
                    //logQueueReq.setQueueNum(range.size() + 1 + "");
                    redisTemplate.opsForList().leftPush(rName, qcvLogQueReq, gson.toJson(logQueueReq));
                } else {
                    redisTemplate.opsForList().rightPush(rName, gson.toJson(logQueueReq));
                }
            } else {
                // byte[] serialize = SerializationUtils.serialize(logQueueReq);
                redisTemplate.opsForList().rightPush(rName, gson.toJson(logQueueReq));
            }

            //车辆入场更新车辆状态

            QcvLogistics qcvLogisticsUpdate = new QcvLogistics();
            qcvLogisticsUpdate.setSid(qcvLogistics.getSid());
            qcvLogisticsUpdate.setPstatus(2);
            qcvLogisticsService.saveOrUpdate(qcvLogisticsUpdate);
        }
        return new RespInfo(0, "success");
    }

    @PostMapping("/getPassRulePage")
    public RespInfo getPassRulePage(@RequestBody QueueReq queueReq, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole()))) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        queueReq.setUserid(authToken.getUserid());
        IPage<PassRule> page = qcvQueueService.getPassRulePageList(queueReq);
        return new RespInfo(0, "success", page);
    }

    @PostMapping("/getQueuePage")
    public RespInfo getQueue(@RequestBody QueueReq queueReq, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole()))) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        queueReq.setUserid(authToken.getUserid());
        IPage<QcvLogQueueVo> page = qcvQueueService.getQueuePageList(queueReq);
        return new RespInfo(0, "success", page);
    }
}
