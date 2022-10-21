package com.web.dao;

import java.util.List;

import com.web.bean.VO.MeetingVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.web.bean.Meeting;


@Mapper
public interface MeetingDao {
	
	public int addMeeting(MeetingVO m);
	
	public int updateMeeting(MeetingVO m);
	
	public int deleteMeeting(int mid);
	
	public List<Meeting> getMeetingByUserid(Meeting m);
	
	public List<Meeting> getMeetingByPhone(Meeting m);
	
	public Meeting getMeetingById(int mid);
	
	public int updateMeetingStatus(Meeting m);

	/**
	 * 根据会议名查询会议
	 *
	 * @param subject
	 * @return
	 */
    List<Meeting> getMeetingBySubject(@Param("subject") String subject);

	/**
	 * 根据会议名和时间查询会议
	 *
	 * @param meetingVO
	 * @return
	 */
	List<Meeting> slectMeetingBySubjectAndTime(MeetingVO meetingVO);
}
