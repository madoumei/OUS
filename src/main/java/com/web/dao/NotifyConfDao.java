package com.web.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.web.bean.NotifyConf;


@Mapper
public interface NotifyConfDao {
    public NotifyConf getNotifyConf(String openid);

    public int addNotifyConf(NotifyConf nc);

    public int NotifyConfigure(NotifyConf nc);

    public int delNotifyConf(String openid);
}
