package com.client.controller;

import com.client.bean.MrRecords;
import com.client.bean.ReqMrRecords;
import com.client.service.MrRecordsService;
import com.config.exception.ErrorEnum;
import com.config.qicool.common.persistence.Page;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utils.UtilTools;
import com.web.bean.*;
import com.web.bean.VO.MeetingRoomVO;
import com.web.bean.VO.MeetingVO;
import com.web.service.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Controller
@RequestMapping(value = "/*")
public class MeetingRoomController {

    @Autowired
    private MeetingRoomService meetingRoomService;

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private TokenServer tokenServer;

    @Autowired
    private MrRecordsService mrRecordsService;

    @Autowired
    private SubAccountService subAccountService;

    @Autowired
    private EmployeeService employeeService;

    @ApiOperation(value = "/addMeetingRoom 添加会议室", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "")
    @RequestMapping(method = RequestMethod.POST, value = "/addMeetingRoom")
    @ResponseBody
    public RespInfo addMeetingRoom(@RequestBody MeetingRoom mr, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                || authToken.getUserid() != mr.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        meetingRoomService.addMeetingRoom(mr);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/getMeetingRoomList 获取所有会议室", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "")
    @RequestMapping(method = RequestMethod.POST, value = "/getMeetingRoomList")
    @ResponseBody
    public RespInfo getMeetingRoomList(@RequestBody MeetingRoom mr, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                || authToken.getUserid() != mr.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        List<MeetingRoom> mrlist = meetingRoomService.getMeetingRoomList(mr.getUserid());

        return new RespInfo(0, "success", mrlist);
    }

    @ApiOperation(value = "/updateMeetingRoom 更新会议室", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "")
    @RequestMapping(method = RequestMethod.POST, value = "/updateMeetingRoom")
    @ResponseBody
    public RespInfo updateMeetingRoom(@RequestBody MeetingRoom mr, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                || authToken.getUserid() != mr.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        meetingRoomService.updateMeetingRoom(mr);

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/delMeetingRoom 删除会议室", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "")
    @RequestMapping(method = RequestMethod.POST, value = "/delMeetingRoom")
    @ResponseBody
    public RespInfo delMeetingRoom(@RequestBody MeetingRoom mr, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                || authToken.getUserid() != mr.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        meetingRoomService.delMeetingRoom(mr.getId());

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/getMeetingRoomListByDate 根据时间获取会议室", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "")
    @RequestMapping(method = RequestMethod.POST, value = "/getMeetingRoomListByDate")
    @ResponseBody
    public RespInfo getMeetingRoomListByDate(@RequestBody MeetingRoomVO mr, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                || authToken.getUserid() != mr.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        List<Integer> mrids = new ArrayList<>();
        MeetingVO meetingVO = new MeetingVO();
        meetingVO.setUserid(authToken.getUserid());
        meetingVO.setRoomStartTime(mr.getRoomStartTime());
        meetingVO.setRoomEndTime(mr.getRoomEndTime());
        //根据时间获取对应的会议
        List<Meeting> meetingBySubjectAndTime = this.meetingService.getMeetingBySubjectAndTime(meetingVO);
        if (!meetingBySubjectAndTime.isEmpty()) {
            for (Meeting meeting : meetingBySubjectAndTime) {
                mrids.add(meeting.getMrid());
            }
        }

        List<MeetingRoom> mrlist = this.meetingRoomService.getMeetingRoomListByIdIn(mrids);
        return new RespInfo(0, "success", mrlist);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/addMrOrder")
    @ResponseBody
    public RespInfo addMrRecords(@RequestBody ReqMrRecords reqmr) throws ParseException {
        SimpleDateFormat time=new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        List<MrRecords> mrlist=mrRecordsService.checkMrRecords(reqmr);
        if(mrlist.size()>0){
            return new RespInfo(93,"meeting room has been reserved");
        }

        Employee emp=employeeService.getEmpInfo(reqmr.getUserid(), reqmr.getEmpPhone()).get(0);
        MeetingRoom mr=meetingRoomService.getMeetingRoom(reqmr.getMrid());
        if(null!=emp&&emp.getSubaccountId()==0) {

            mrRecordsService.addMrRecords(reqmr);
        }else{
            SubAccount sa=subAccountService.getSubAccountById(emp.getSubaccountId());
            if(sa.getBalance()<reqmr.getCost()){
                return new RespInfo(90,"balance not enough");
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(reqmr.getStartDate()));
            long time1 = cal.getTimeInMillis();
            cal.setTime(new Date());
            long time2 = cal.getTimeInMillis();
            long between_days=(time1-time2)/(1000*3600*24);

            int days=Integer.parseInt(String.valueOf(between_days));

            if(days>=0&&days<=mr.getPrematureDays())
            {
                mrRecordsService.addMrRecords(reqmr);
                sa.setBalance(sa.getBalance()-reqmr.getCost());
                subAccountService.updateSABalance(sa);
            }else{
                return new RespInfo(91,"time span is too large");
            }
        }
        return new RespInfo(0,"success");
    }

    @RequestMapping(method = RequestMethod.POST, value = "/cancelMrOrder")
    @ResponseBody
    public RespInfo cancelMrRecords(@RequestBody ReqMrRecords reqmr) throws ParseException {
        MrRecords  record=mrRecordsService.getMrRecord(reqmr.getRid());
        MeetingRoom mr=meetingRoomService.getMeetingRoom(record.getMrid());
        Calendar cal = Calendar.getInstance();
        cal.setTime(record.getStartDate());
        long time1 = cal.getTimeInMillis();
        cal.setTime(new Date());
        long time2 = cal.getTimeInMillis();
        long between_minutes=(time1-time2)/60000;
        if(between_minutes<mr.getLatestTime())
        {
            return new RespInfo(92,"cannot cancel");
        }

        reqmr.setStatus(2);
        int a=mrRecordsService.updateMeetingRoom(reqmr);
        if(a>0){
            Employee emp=employeeService.getEmpInfo(record.getUserid(), record.getEmpPhone()).get(0);
            if(emp.getSubaccountId()!=0){
                SubAccount sa=subAccountService.getSubAccountById(emp.getSubaccountId());
                sa.setBalance(sa.getBalance()+record.getCost());
                subAccountService.updateSABalance(sa);
            }
        }


        return new RespInfo(0,"success");
    }


    @RequestMapping(method = RequestMethod.POST, value = "/getMrRecords")
    @ResponseBody
    public RespInfo getMrRecords(@RequestBody ReqMrRecords reqmr){
        Page rpage = new Page(reqmr.getStartIndex()/reqmr.getRequestedCount()+1, reqmr.getRequestedCount(), 0);
        reqmr.setPage(rpage);
        List<MrRecords> mrlist=	mrRecordsService.getMrRecordsList(reqmr);
        rpage.setList(mrlist);

        return new RespInfo(0,"success",rpage);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/getMrRecordsById")
    @ResponseBody
    public RespInfo getMrRecordsById(@RequestBody ReqMrRecords reqmr){
        List<MrRecords> mrlist=	mrRecordsService.getMrRecordsById(reqmr);
        return new RespInfo(0,"success",mrlist);
    }
}
