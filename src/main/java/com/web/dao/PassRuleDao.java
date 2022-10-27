package com.web.dao;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.web.bean.PassConfig;
import com.web.bean.PassRule;


@Mapper
public interface PassRuleDao extends BaseMapper<PassRule> {
    public int addPassRule(PassRule pr);

    public int updatePassRule(PassRule pr);

    public List<PassRule> getPassRuleList(PassRule pr);

    public List<PassRule> getPassRule(PassRule pr);

    public int deletePassRule(PassRule pr);

    public int addPassConfig(PassConfig pc);

    public int updatePassConfig(PassConfig pc);

    public int deletePassConfig(PassConfig pc);

    public List<PassConfig> getPassConfig(PassConfig pc);

    public PassConfig getPassConfigByCid(PassConfig pc);

    List<PassRule> getPassRuleListByDaysOffTranslation(PassRule pr);

    PassRule getPassRuleByRname(PassRule passRule);
}
