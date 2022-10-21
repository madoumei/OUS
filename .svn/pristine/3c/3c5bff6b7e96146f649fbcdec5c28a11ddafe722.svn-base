package com.client.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.client.dao.EqptRuleDao;
import com.client.service.EqptRuleService;
import com.web.bean.EqptRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("eqptRuleServiceImpl")
public class EqptRuleServiceImpl extends ServiceImpl<EqptRuleDao, EqptRule> implements EqptRuleService {
    @Autowired
    private EqptRuleDao eqptRuleDao;

    @Override
    public List<EqptRule> getList(int userid){
        LambdaQueryWrapper<EqptRule> lambdaQueryWrapper = Wrappers.lambdaQuery(EqptRule.class);
        lambdaQueryWrapper.eq(EqptRule::getUserid,userid);
        return list(lambdaQueryWrapper);
    }

    @Override
    public List<EqptRule> getList(int userid,int eid){
        LambdaQueryWrapper<EqptRule> lambdaQueryWrapper = Wrappers.lambdaQuery(EqptRule.class);
        lambdaQueryWrapper.eq(EqptRule::getUserid,userid);
        lambdaQueryWrapper.and(
                QueryWrapper -> QueryWrapper.likeRight(EqptRule::getEids,eid+",")
                        .or().likeLeft(EqptRule::getEids,","+eid)
                        .or().like(EqptRule::getEids,","+eid+",")
                        .or().eq(EqptRule::getEids,eid));
        return list(lambdaQueryWrapper);
    }

    @Override
    public boolean remove(EqptRule rlt){
        LambdaQueryWrapper<EqptRule> lambdaQueryWrapper = Wrappers.lambdaQuery(EqptRule.class);
        lambdaQueryWrapper.eq(EqptRule::getUserid,rlt.getUserid())
        .eq(EqptRule::getId,rlt.getId());
        return remove(lambdaQueryWrapper);
    }
}
