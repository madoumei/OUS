package com.web.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.client.bean.Visitor;
import com.web.bean.MsgTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MsgTemplateDao extends BaseMapper<MsgTemplate> {

	public MsgTemplate getTemplate(@Param("userid") int userid, @Param("id") String id);

	public int addTemplate(MsgTemplate conf);

	public int updateMsgTemplate(MsgTemplate conf);

	public int delMsgTemplate(MsgTemplate conf);
}
