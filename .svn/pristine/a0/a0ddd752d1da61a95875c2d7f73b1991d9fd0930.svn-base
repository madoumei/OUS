package com.web.service;

import java.util.List;

import com.web.bean.VO.MeetingVO;
import com.web.bean.Meeting;

public interface MeetingService {
	public int addMeeting(MeetingVO m);
	
	public int updateMeeting(MeetingVO m);
	
	public int deleteMeeting(int mid);
	
	public List<Meeting> getMeetingByUserid(Meeting m);
	
	public List<Meeting> getMeetingByPhone(Meeting m);
	
	public Meeting getMeetingById(int mid);
	
	public int updateMeetingStatus(Meeting m);

	/**
	 * 根据会议名称查询会议
	 *
	 * @param subject
	 * @return
	 */
    List<Meeting> getMeetingBySubject(String subject);

	/**
	 * 根据会议名称和时间查询会议
	 *
	 * @param meetingVO
	 * @return
	 */
	List<Meeting> getMeetingBySubjectAndTime(MeetingVO meetingVO);

	/**
	 * 根据Mid修改会议的状态
	 *
	 * @param meeting
	 * @return
	 */
    int updateMeetingStatusByMid(Meeting meeting);

	/**
	 * 更新参会人员
	 *
	 * @param meetingVO
	 * @return
	 */
	int syncJoinMeeting(MeetingVO meetingVO);

	int cancelMeetingSendSms(int mid);
}
