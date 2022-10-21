package com.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.bean.MeetingRoom;
import com.web.dao.MeetingRoomDao;
import com.web.service.MeetingRoomService;


@Service("meetingRoomService")
public class MeetingRoomServiceImpl implements MeetingRoomService{
	
	@Autowired
	private MeetingRoomDao meetingRoomDao;

	@Override
	public int addMeetingRoom(MeetingRoom mr) {
		// TODO Auto-generated method stub
		return meetingRoomDao.addMeetingRoom(mr);
	}

	@Override
	public List<MeetingRoom> getMeetingRoomList(int userid) {
		// TODO Auto-generated method stub
		return meetingRoomDao.getMeetingRoomList(userid);
	}

	@Override
	public int updateMeetingRoom(MeetingRoom mr) {
		// TODO Auto-generated method stub
		return meetingRoomDao.updateMeetingRoom(mr);
	}

	@Override
	public int delMeetingRoom(int id) {
		// TODO Auto-generated method stub
		return meetingRoomDao.delMeetingRoom(id);
	}

	@Override
	public MeetingRoom getMeetingRoom(int id) {
		// TODO Auto-generated method stub
		return meetingRoomDao.getMeetingRoom(id);
	}

	@Override
	public List<MeetingRoom> getMeetingRoomListByIdIn(List<Integer> mrids) {
		return meetingRoomDao.getMeetingRoomListByIdIn(mrids);
	}
}
