package com.web.service.impl;

import com.config.activemq.MessageSender;
import com.utils.SysLog;
import com.utils.UtilTools;
import com.web.bean.Appointment;
import com.web.bean.Meeting;
import com.web.bean.UserInfo;
import com.web.bean.VO.MeetingVO;
import com.web.dao.ConfigureDao;
import com.web.dao.MeetingDao;
import com.web.service.AppointmentService;
import com.web.service.MeetingService;
import com.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("meetingService")
public class MeetingServiceImpl implements MeetingService {

    @Autowired
    MeetingDao meetingDao;

    @Autowired
    AppointmentService appointmentService;

    @Autowired
    ConfigureDao configureDao;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSender messageSender;

    @Override
    public int addMeeting(MeetingVO m) {
        return this.meetingDao.addMeeting(m);
    }

    /**
     * 修改会议
     *
     * @param m
     * @return
     */
    @Override
    public int updateMeeting(MeetingVO m) {
        return this.meetingDao.updateMeeting(m);
    }

    @Override
    public int deleteMeeting(int mid) {
        // TODO Auto-generated method stub
        return meetingDao.deleteMeeting(mid);
    }

    @Override
    public List<Meeting> getMeetingByUserid(Meeting m) {
        // TODO Auto-generated method stub
        return meetingDao.getMeetingByUserid(m);
    }

    @Override
    public List<Meeting> getMeetingByPhone(Meeting m) {
        // TODO Auto-generated method stub
        return meetingDao.getMeetingByPhone(m);
    }

    @Override
    public Meeting getMeetingById(int mid) {
        return meetingDao.getMeetingById(mid);
    }

    @Override
    public int updateMeetingStatus(Meeting m) {
        // TODO Auto-generated method stub
        return meetingDao.updateMeetingStatus(m);
    }

    /**
     * 根据会议名称查询会议
     *
     * @param subject
     * @return
     */
    @Override
    public List<Meeting> getMeetingBySubject(String subject) {
        return this.meetingDao.getMeetingBySubject(subject);
    }

    /**
     * 根据会议名称和时间查询
     *
     * @param meetingVO
     * @return
     */
    @Override
    public List<Meeting> getMeetingBySubjectAndTime(MeetingVO meetingVO) {
        List<Meeting> meetings = this.meetingDao.slectMeetingBySubjectAndTime(meetingVO);
        return meetings;
    }

    /**
     * 根据Mid修改会议
     *
     * @param meeting
     * @return
     */
    @Override
    public int updateMeetingStatusByMid(Meeting meeting) {
        int i = 0;
        try {
            i = this.meetingDao.updateMeetingStatus(meeting);
            SysLog.info("开始会议或者结束会议:" + meeting.getStatus());
        } catch (Exception e) {
            SysLog.error("开始会议或者结束会议:" + e.getMessage());
            e.printStackTrace();
        }
        if (i > 0) {
            Map<String, Object> params = new HashMap<>();
            params.put("company_id", meeting.getUserid());
            params.put("meeting_id", meeting.getMid());
            //会议开始
            if (2 == meeting.getStatus()) {
                params.put("key", "meeting_open");
                messageSender.updateFaceLib(params);
            } else if (3 == meeting.getStatus()) {
                params.put("key", "meeting_close");
                messageSender.updateFaceLib(params);
            }
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * 取消会议通知参会人员
     *
     * @param mid
     * @return
     */
    public int cancelMeetingSendSms(int mid) {
        List<Appointment> appointmentByMid = appointmentService.getAppointmentByMid(mid);
        Meeting meetingById = this.meetingDao.getMeetingById(mid);
        if (!appointmentByMid.isEmpty()) {
            String response = "0";
            for (Appointment appointment : appointmentByMid) {
                Map<String, String> params = new HashMap<String, String>();
                String phone = appointment.getPhone();
                params.put("phone", phone);
                params.put("message", "您好" + appointment.getName() + "!原定于"
                        + meetingById.getStartTime() + "-" + meetingById.getEndTime()
                        + "在" + meetingById.getAddress() + "召开的"
                        + meetingById.getSubject() + "暂时取消，给您带来的不便敬请原谅，感谢您的支持！");
                UserInfo userinfo = userService.getUserInfoByUserId(meetingById.getUserid());
                response = UtilTools.sendSmsByYiMei(params, configureDao, userinfo);
            }
            SysLog.info("取消会议发送消息成功!");
            return Integer.parseInt(response);
        }
        SysLog.error("取消会议发送消息失败！");
        return -1;
    }

    /**
     * 同步参会人员
     *
     * @param meetingVO
     * @return
     */
    @Override
    public int syncJoinMeeting(MeetingVO meetingVO) {
        Map<String, Object> params = new HashMap<>();
        params.put("key", "meeting_sync");
        params.put("company_id", meetingVO.getUserid());
        params.put("meeting_id", meetingVO.getMid());
        messageSender.updateFaceLib(params);
        return 0;
    }
}
