package com.client.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.client.dao.EgPtRltDao;
import com.client.service.EgPtRltService;
import com.web.bean.EqptGroupPassTimeRlt;
import com.web.bean.EqptRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service("egPtRltServiceImpl")
public class EgPtRltServiceImpl extends ServiceImpl<EgPtRltDao, EqptGroupPassTimeRlt> implements EgPtRltService {
    @Autowired
    private EgPtRltDao egPtRltDao;

    @Override
    public List<EqptGroupPassTimeRlt> getList(int userid){
        LambdaQueryWrapper<EqptGroupPassTimeRlt> lambdaQueryWrapper = Wrappers.lambdaQuery(EqptGroupPassTimeRlt.class);
        lambdaQueryWrapper.eq(EqptGroupPassTimeRlt::getUserid,userid);
        return list(lambdaQueryWrapper);
    }

    @Override
    public List<EqptGroupPassTimeRlt> getList(int userid, int egid){
        LambdaQueryWrapper<EqptGroupPassTimeRlt> lambdaQueryWrapper = Wrappers.lambdaQuery(EqptGroupPassTimeRlt.class);
        lambdaQueryWrapper.eq(EqptGroupPassTimeRlt::getUserid,userid);
        lambdaQueryWrapper.and(
                QueryWrapper -> QueryWrapper.likeRight(EqptGroupPassTimeRlt::getEgids,egid+",")
                        .or().likeLeft(EqptGroupPassTimeRlt::getEgids,","+egid)
                        .or().like(EqptGroupPassTimeRlt::getEgids,","+egid+",")
                        .or().eq(EqptGroupPassTimeRlt::getEgids,egid));
        return list(lambdaQueryWrapper);
    }

    @Override
    public boolean remove(EqptGroupPassTimeRlt rlt){
        LambdaQueryWrapper<EqptGroupPassTimeRlt> lambdaQueryWrapper = Wrappers.lambdaQuery(EqptGroupPassTimeRlt.class);
        lambdaQueryWrapper.eq(EqptGroupPassTimeRlt::getUserid,rlt.getUserid())
        .eq(EqptGroupPassTimeRlt::getId,rlt.getId());
        return remove(lambdaQueryWrapper);
    }
}
