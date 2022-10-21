package com.web.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.web.bean.Person;


@Mapper
public interface PersonInfoDao {
		public Person getVisitPersonByPhone(String pmobile);
		
		public Person getInvitePersonByPhone(String pmobile);
		
		public int addVisitPerson(Person p);
		
		public int addInvitePerson(Person p);
		
		public int updateVisitPerson(Person p);
		
		public int updateInvitePerson(Person p);
		
		public Person getVisitPersonByOpenid(String popenid);
		
		public Person getInvitePersonByOpenid(String popenid);
		
		public int delInvitePerson(String pmobile);
		
		public int delVisitPerson(String pmobile);
		
		public int updateVisitPersonPwd(Person p);
		
		public int updatePersonUserid(Person p);
		
		public int updateNickname(Person p);
		
		public int updateAvatar(Person p);
		
		public List<Person> getVisitPersonByCardid(String cardid);
		
		public int delInvitePersonByOpenid(String popenid);
		
		public int updateVisitFace(Person p);
		
		public int updateInviteFace(Person p);
		
		public int updateInviteAvatar(Person p);

	List<Person> getVisitPersonByAvatar(String avatar);

	List<Person> getInvitePersonByAvatar(String avatar);

}
