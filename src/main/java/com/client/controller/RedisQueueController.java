package com.client.controller;

import com.client.bean.dto.QcvLogistics;
import com.client.bean.dto.QcvQueue;
import com.client.service.QcvLogisticsService;
import com.web.bean.RespInfo;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@AllArgsConstructor
public class RedisQueueController {

    private final RedisTemplate redisTemplate;

    private final QcvLogisticsService qcvLogisticsService;

    @PostMapping("/addQueue")
    public RespInfo addQueue(@RequestParam(value = "plateNum",required = false) String plateNum) {
        QcvLogistics qcvLogistics = qcvLogisticsService.getLogisticsByPlateNum(plateNum);
        return new RespInfo(0, "success");
    }

    @PostMapping("/getQueue")
    public RespInfo getQueue() {

    }
}
