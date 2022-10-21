package com.web.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.config.qicool.common.utils.StringUtils;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.web.bean.Person;
import com.web.dao.PersonInfoDao;
import com.web.service.PersonInfoService;

import javax.print.DocFlavor;


@Service("personInfoService")
public class PersonInfoServiceImpl implements PersonInfoService{
	
	@Autowired 
	PersonInfoDao personInfoDao;

	@Autowired
	private StringRedisTemplate strRedisTemplate;

	@Override
	public Person getPersonByOpenid(String openid) {
		Person person = null;
		ValueOperations<String, String> valueOperations = strRedisTemplate.opsForValue();
		String jsonPerson = valueOperations.get("p_"+openid);
		if(StringUtils.isNotEmpty(jsonPerson)){
			person = JSON.parseObject(jsonPerson, Person.class);
			return person;
		}

		person = personInfoDao.getVisitPersonByOpenid(openid);
		if(person != null){
			valueOperations.set("p_"+openid,JSON.toJSONString(person), 8, TimeUnit.HOURS);
			return person;
		}

		person = personInfoDao.getInvitePersonByOpenid(openid);
		if(person != null){
			valueOperations.set("p_"+openid,JSON.toJSONString(person), 8, TimeUnit.HOURS);
			return person;
		}

		return null;
	}

	@Override
	public Person getVisitPersonByPhone(String pmobile) {
		// TODO Auto-generated method stub
		return personInfoDao.getVisitPersonByPhone(pmobile);
	}

	@Override
	public int addVisitPerson(Person p) {
		// TODO Auto-generated method stub
		return personInfoDao.addVisitPerson(p);
	}

	@Override
	public int updateVisitPerson(Person p) {
		// TODO Auto-generated method stub
		return personInfoDao.updateVisitPerson(p);
	}

	@Override
	public Person getVisitPersonByOpenid(String openid) {
		// TODO Auto-generated method stub
		return personInfoDao.getVisitPersonByOpenid(openid);
	}

	@Override
	public Person getInvitePersonByPhone(String pmobile) {
		// TODO Auto-generated method stub
		return personInfoDao.getInvitePersonByPhone(pmobile);
	}

	@Override
	public int addInvitePerson(Person p) {
		// TODO Auto-generated method stub
		return personInfoDao.addInvitePerson(p);
	}

	@Override
	public int updateInvitePerson(Person p) {
		// TODO Auto-generated method stub
		return personInfoDao.updateInvitePerson(p);
	}

	@Override
	public Person getInvitePersonByOpenid(String popenid) {
		// TODO Auto-generated method stub
		return personInfoDao.getInvitePersonByOpenid(popenid);
	}

	@Override
	public int delInvitePerson(String pmobile) {
		// TODO Auto-generated method stub
		return personInfoDao.delInvitePerson(pmobile);
	}

	@Override
	public int updateVisitPersonPwd(Person p) {
		// TODO Auto-generated method stub
		return personInfoDao.updateVisitPersonPwd(p);
	}

	@Override
	public int updatePersonUserid(Person p) {
		// TODO Auto-generated method stub
		return personInfoDao.updatePersonUserid(p);
	}

	@Override
	public int updateNickname(Person p) {
		// TODO Auto-generated method stub
		return personInfoDao.updateNickname(p);
	}

	@Override
	public int updateAvatar(Person p) {
		// TODO Auto-generated method stub
		return personInfoDao.updateAvatar(p);
	}

	@Override
	public List<Person> getVisitPersonByCardid(String cardid) {
		// TODO Auto-generated method stub
		return personInfoDao.getVisitPersonByCardid(cardid);
	}

	@Override
	public int delVisitPerson(String pmobile) {
		// TODO Auto-generated method stub
		return personInfoDao.delVisitPerson(pmobile);
	}

	@Override
	public int delInvitePersonByOpenid(String popenid) {
		// TODO Auto-generated method stub
		return personInfoDao.delInvitePersonByOpenid(popenid);
	}

	@Override
	public int updateVisitFace(Person p) {
		// TODO Auto-generated method stub
		return personInfoDao.updateVisitFace(p);
	}

	@Override
	public int updateInviteFace(Person p) {
		// TODO Auto-generated method stub
		return personInfoDao.updateInviteFace(p);
	}

	@Override
	public int updateInviteAvatar(Person p) {
		// TODO Auto-generated method stub
		return personInfoDao.updateInviteAvatar(p);
	}

	@Override
	public List<Person> getVisitPersonByAvatar(String avatar) {
		return personInfoDao.getVisitPersonByAvatar(avatar);
	}

	@Override
	public List<Person> getInvitePersonByAvatar(String avatar) {
		return personInfoDao.getInvitePersonByAvatar(avatar);
	}
	
	

}
