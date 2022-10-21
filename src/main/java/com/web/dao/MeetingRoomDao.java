package com.web.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.web.bean.MeetingRoom;

@Mapper
public interface MeetingRoomDao {
	public int addMeetingRoom(MeetingRoom mr);
	
	public List<MeetingRoom> getMeetingRoomList(int userid);
	
	public int updateMeetingRoom(MeetingRoom mr);
	
	public int delMeetingRoom(int id);
	
	public MeetingRoom getMeetingRoom(int id);

    List<MeetingRoom> getMeetingRoomListByIdIn(List<Integer> mrids);
}
