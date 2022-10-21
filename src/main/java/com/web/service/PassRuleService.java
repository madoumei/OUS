package com.web.service;

import java.util.Date;
import java.util.List;

import com.client.bean.RequestVisit;
import com.web.bean.PassConfig;
import com.web.bean.PassRule;

public interface PassRuleService {
	public int addPassRule(PassRule pr);
	
	public int updatePassRule(PassRule pr);
	
	public List<PassRule> getPassRuleList(PassRule pr);
	
	public List<PassRule> getPassRule(PassRule pr);
	
	public int deletePassRule(PassRule pr);
	
	public int addPassConfig(PassConfig pc);
	
	public int updatePassConfig(PassConfig pc);
	
	public int deletePassConfig(PassConfig pc);
	
	public List<PassConfig> getPassConfig(PassConfig pc);

	public int getSendCardStatus(Integer userid,Date date,String[] gName);

	public Boolean isWeekendPass(Date visitDate ,int gid,int userid);

	public Boolean isHolidayPass(Date visitDate ,int gid,int userid);

	public Boolean isDaysOffTranslation(Date visitDate ,int gid,List<String> floors,int userid);

	PassRule getPassRuleByRname(PassRule passRule);
}
