package com.web.controller;

import com.config.exception.ErrorEnum;
import com.config.qicool.common.persistence.Page;
import com.config.qicool.common.utils.StringUtils;
import com.utils.SysLog;
import com.web.bean.AuthToken;
import com.web.bean.Meeting;
import com.web.bean.RespInfo;
import com.web.bean.VO.MeetingVO;
import com.web.service.MeetingRoomService;
import com.web.service.MeetingService;
import com.web.service.TokenServer;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping(value = "/*")
public class MeetingController {

    @Autowired
    private MeetingService meetingService;
    @Autowired
    private TokenServer tokenServer;
    @Autowired
    private MeetingRoomService meetingRoomService;

    @ApiOperation(value = "/addMeeting 添加会议", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "")
    @RequestMapping(method = RequestMethod.POST, value = "/addMeeting")
    public RespInfo addMeeting(@RequestBody MeetingVO m, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                || authToken.getUserid() != m.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        m.setStatus(1);
        if (StringUtils.isBlank(m.getStartTime()) || StringUtils.isBlank(m.getEndTime())) {
            return new RespInfo(ErrorEnum.E_106.getCode(), ErrorEnum.E_106.getMsg());
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat dateFormatDate = new SimpleDateFormat("yyyy-MM-dd");
        try {
            //添加会议不能超过当前时间
            Long addDate = dateFormat.parse(m.getStartTime()).getTime();
            Long nowDate = new Date().getTime();
            if ((nowDate - addDate) > 0) {
                return new RespInfo(ErrorEnum.E_107.getCode(), ErrorEnum.E_107.getMsg());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //根据会议名查询所有会议
        List<Meeting> meetingsOld = this.meetingService.getMeetingBySubject(m.getSubject());
        long addTime = 0;
        try {
            addTime = dateFormatDate.parse(m.getStartTime()).getTime();
            if (!meetingsOld.isEmpty()) {
                for (Meeting meeting : meetingsOld) {
                    long oldTime = dateFormatDate.parse(meeting.getStartTime()).getTime();
                    //同一名字的会议在同一天只能有一个
                    if (addTime == oldTime) {
                        return new RespInfo(ErrorEnum.E_108.getCode(), ErrorEnum.E_108.getMsg());
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (StringUtils.isNotBlank(m.getRoomStartTime()) && StringUtils.isNotBlank(m.getRoomEndTime()) && m.getMrid() != 0) {
            //会议室校验
            List<Integer> mrids = new ArrayList<>();

            List<Meeting> meetingBySubjectAndTime = this.meetingService.getMeetingBySubjectAndTime(m);
            if (!meetingBySubjectAndTime.isEmpty()) {
                for (Meeting meeting : meetingBySubjectAndTime) {
                    mrids.add(meeting.getMrid());
                }
            }
            if (mrids.contains(m.getMrid())) {
                return new RespInfo(ErrorEnum.E_115.getCode(), ErrorEnum.E_115.getMsg());
            }
        }
        int i = 0;
        try {
            i = meetingService.addMeeting(m);
            SysLog.info("添加会议成功!" + i);
            return new RespInfo(0, "success", i);
        } catch (Exception e) {
            SysLog.error("添加会议失败:" + e.getMessage());
            return new RespInfo(ErrorEnum.E_105.getCode(), ErrorEnum.E_105.getMsg());
        }
    }

    /**
     * 修改会议
     *
     * @param m
     * @return
     */
    @ApiOperation(value = "/updateMeeting 修改会议", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "")
    @RequestMapping(method = RequestMethod.POST, value = "/updateMeeting")
    public RespInfo updateMeeting(@RequestBody MeetingVO m, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                || authToken.getUserid() != m.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        if (StringUtils.isBlank(m.getStartTime()) || StringUtils.isBlank(m.getEndTime())) {
            return new RespInfo(ErrorEnum.E_106.getCode(), ErrorEnum.E_106.getMsg());
        }
        //根据mid查询会议
        Meeting meetingById = this.meetingService.getMeetingById(m.getMid());
        if (ObjectUtils.isNotEmpty(meetingById)) {
            if (0 == m.getStatus() && 0 == meetingById.getStatus()) {
                return new RespInfo(ErrorEnum.E_109.getCode(), ErrorEnum.E_109.getMsg());
            }
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat dateFormatDate = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Long addDate = dateFormat.parse(m.getStartTime()).getTime();
            Long nowDate = new Date().getTime();
            if ((nowDate - addDate) > 0) {
                return new RespInfo(ErrorEnum.E_107.getCode(), ErrorEnum.E_107.getMsg());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //根据会议名查询所有会议
        List<Meeting> meetingsOld = this.meetingService.getMeetingBySubject(m.getSubject());
        long addTime = 0;
        try {
            addTime = dateFormatDate.parse(m.getStartTime()).getTime();
            if (!meetingsOld.isEmpty()) {
                for (Meeting meeting : meetingsOld) {
                    if (m.getMid() == meeting.getMid()) {
                        continue;
                    }
                    long oldTime = dateFormatDate.parse(meeting.getStartTime()).getTime();
                    //同一名字的会议在同一天只能有一个
                    if (addTime == oldTime) {
                        return new RespInfo(ErrorEnum.E_108.getCode(), ErrorEnum.E_108.getMsg());
                    }
                }
            }
        } catch (ParseException e) {
            SysLog.error("更新会议校验:" + e.getMessage());
            e.printStackTrace();
        }

        if (StringUtils.isNotBlank(m.getRoomStartTime()) && StringUtils.isNotBlank(m.getRoomEndTime()) && m.getMrid() != 0) {
            //会议室校验
            List<Integer> mrids = new ArrayList<>();

            List<Meeting> meetingBySubjectAndTime = this.meetingService.getMeetingBySubjectAndTime(m);
            if (!meetingBySubjectAndTime.isEmpty()) {
                for (Meeting meeting : meetingBySubjectAndTime) {
                    if (meeting.getMid() == m.getMid()) {
                        continue;
                    }
                    mrids.add(meeting.getMrid());
                }
            }
            if (!mrids.isEmpty()) {
                if (mrids.contains(m.getMrid())) {
                    return new RespInfo(ErrorEnum.E_115.getCode(), ErrorEnum.E_115.getMsg());
                }
            }
        }

        int i = 0;
        try {
            i = meetingService.updateMeeting(m);
            SysLog.info("更新会议成功");
        } catch (Exception e) {
            SysLog.error("更新会议失败:" + e.getMessage());
            e.printStackTrace();
        }
        /**
         * 取消
         */
        if (i > 0) {
            if (0 == m.getStatus()) {
                this.meetingService.cancelMeetingSendSms(m.getMid());
            }
            return new RespInfo(0, "success", i);
        } else {
            return new RespInfo(ErrorEnum.E_063.getCode(), ErrorEnum.E_063.getMsg());
        }

    }

    @ApiOperation(value = "/delMeeting 根据Mid物理删除会议", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "")
    @RequestMapping(method = RequestMethod.POST, value = "/delMeeting")
    public RespInfo delMeeting(@RequestBody Meeting m, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                || authToken.getUserid() != m.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        meetingService.deleteMeeting(m.getMid());

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/getMeetingByUserid 根据UserId获取所有会议不分页", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "")
    @RequestMapping(method = RequestMethod.POST, value = "/getMeetingByUserid")
    public RespInfo getMeetingByUserid(@RequestBody Meeting m, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                || authToken.getUserid() != m.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        List<Meeting> mlist = meetingService.getMeetingByUserid(m);
        return new RespInfo(0, "success", mlist);
    }

    @ApiOperation(value = "/getMeetingByPhone 根据手机号获取会议详情", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "")
    @RequestMapping(method = RequestMethod.POST, value = "/getMeetingByPhone")
    public RespInfo getMeetingByPhone(@RequestBody Meeting m, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                || authToken.getUserid() != m.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        List<Meeting> mlist = meetingService.getMeetingByPhone(m);
        return new RespInfo(0, "success", mlist);
    }

    @ApiOperation(value = "/getMeetingById 根据Id获取会议详细信息", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "")
    @RequestMapping(method = RequestMethod.POST, value = "/getMeetingById")
    public RespInfo getMeetingById(@RequestBody Meeting m, HttpServletRequest request) {
        try {
            AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
            if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())) || authToken.getUserid() != m.getUserid()) {
                return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
        } catch (RuntimeException e) {
            //没有token就检查签名
            RespInfo respInfo = tokenServer.checkSign(request);
            if (null != respInfo) {
                return respInfo;
            }
        }
        Meeting meet = meetingService.getMeetingById(m.getMid());

        return new RespInfo(0, "success", meet);
    }

    /**
     * 根据条件查询会议
     *
     * @param meetingVO
     * @return
     */
    @ApiOperation(value = "/getMeeting 分页获取会议列表", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "")
    @PostMapping("/getMeeting")
    public RespInfo getMeetingBySubjectAndTime(@RequestBody MeetingVO meetingVO, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                || authToken.getUserid() != meetingVO.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        Page<Meeting> meetingPage = new Page<Meeting>(meetingVO.getStartIndex() / meetingVO.getRequestedCount() + 1, meetingVO.getRequestedCount(), 0);
        meetingVO.setPage(meetingPage);
        List<Meeting> meetings = this.meetingService.getMeetingBySubjectAndTime(meetingVO);
        meetingPage.setList(meetings);
        return new RespInfo(0, "success", meetingPage);
    }

    @ApiOperation(value = "/updateMeetingStatus 修改会议状态开始或者结束", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "")
    @PostMapping("/updateMeetingStatus")
    public RespInfo updateMeetingStatusByMid(@RequestBody Meeting meeting, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                || authToken.getUserid() != meeting.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        int result = this.meetingService.updateMeetingStatusByMid(meeting);
        return new RespInfo(0, "success", result);
    }

    @ApiOperation(value = "/syncJoinMeeting 同步参会人员", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "")
    @PostMapping("/syncJoinMeeting")
    public RespInfo syncJoinMeeting(@RequestBody MeetingVO meetingVO, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())) || authToken.getUserid() != meetingVO.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        this.meetingService.syncJoinMeeting(meetingVO);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/getMeetingByroomTime 根据会议室编号以及时间获取会议室预约情况", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "")
    @PostMapping("/getMeetingByroomTime")
    public RespInfo getMeetingByroomTime(@RequestBody MeetingVO m, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                || authToken.getUserid() != m.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        List<Meeting> meetings = this.meetingService.getMeetingBySubjectAndTime(m);
        return new RespInfo(0, "success", meetings);
    }
}
