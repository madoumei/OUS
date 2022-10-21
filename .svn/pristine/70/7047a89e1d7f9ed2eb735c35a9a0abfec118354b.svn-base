package com.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.web.bean.MsgTemplate;
import com.web.dao.MsgTemplateDao;
import com.web.service.MsgTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("MsgTemplateService")
public class MsgTemplateServiceImpl extends ServiceImpl<MsgTemplateDao,MsgTemplate> implements MsgTemplateService {

    @Autowired
    private MsgTemplateDao msgTemplateDao;

    @Override
    public MsgTemplate getTemplate(int userid, String id) {
        return msgTemplateDao.getTemplate(userid,id);
    }

    @Override
    public List<MsgTemplate> getTemplateList(MsgTemplate conf) {
        LambdaQueryWrapper<MsgTemplate> lambdaQueryWrapper = Wrappers.lambdaQuery(MsgTemplate.class);
        lambdaQueryWrapper.eq(StringUtils.isNotEmpty(conf.getType()),MsgTemplate::getType,conf.getType());
        if(StringUtils.isNotEmpty(conf.getTitle()) || StringUtils.isNotEmpty(conf.getContent()) || StringUtils.isNotEmpty(conf.getId())) {
            lambdaQueryWrapper.and(
                    QueryWrapper -> QueryWrapper.like(StringUtils.isNotEmpty(conf.getTitle()), MsgTemplate::getTitle, conf.getTitle())
                            .or().like(StringUtils.isNotEmpty(conf.getContent()), MsgTemplate::getContent, conf.getContent())
                            .or().like(StringUtils.isNotEmpty(conf.getId()), MsgTemplate::getId, conf.getId()));
        }
        return list(lambdaQueryWrapper);
    }

    @Override
    public int addTemplate(MsgTemplate conf) {
        return msgTemplateDao.addTemplate(conf);
    }

    @Override
    public int updateMsgTemplate(MsgTemplate conf) {
        return msgTemplateDao.updateMsgTemplate(conf);
    }

    @Override
    public int delMsgTemplate(MsgTemplate conf) {
        return msgTemplateDao.delMsgTemplate(conf);
    }
}
