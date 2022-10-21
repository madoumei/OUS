package com.client.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.client.bean.Equipment;
import com.client.bean.RequestVisit;
import com.client.bean.Track;
import com.web.bean.EqptGroupPassTimeRlt;

import java.util.Collection;
import java.util.List;

public interface EgPtRltService extends IService<EqptGroupPassTimeRlt> {

    public List<EqptGroupPassTimeRlt> getList(int userid);

    List<EqptGroupPassTimeRlt> getList(int userid, int egid);

    boolean remove(EqptGroupPassTimeRlt rlt);
}
