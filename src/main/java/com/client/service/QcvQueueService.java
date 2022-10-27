package com.client.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.client.bean.dto.QcvQueue;
import com.client.bean.req.QueueReq;
import com.client.bean.vo.QcvLogQueueVo;
import com.web.bean.PassRule;

public interface QcvQueueService {

    IPage<PassRule> getPassRulePageList(QueueReq queueReq);

    IPage<QcvLogQueueVo> getQueuePageList(QueueReq queueReq);
}
