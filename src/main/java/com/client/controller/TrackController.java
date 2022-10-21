package com.client.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.client.bean.*;
import com.client.dao2.TrackDao;
import com.client.service.DrivingRouteService;
import com.client.service.GeofenceService;
import com.client.service.TrackService;
import com.client.service.VisitorService;
import com.config.exception.ErrorEnum;
import com.utils.Constant;
import com.utils.SysLog;
import com.utils.yimei.DateUtil;
import com.web.bean.*;
import com.web.service.PersonInfoService;
import com.web.service.TokenServer;
import com.web.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 访客轨迹
 */
@Controller
@RequestMapping(value = "/*")
@Api(value = "TrackController", tags = "API_访客轨迹管理", hidden = true)
public class TrackController {

    @Autowired
    private TrackService trackService;

    @Autowired
    private GeofenceService geofenceService;

    @Autowired
    private DrivingRouteService drivingRouteService;

    @Autowired
    private StringRedisTemplate strRedisTemplate;

    @Autowired
    private TokenServer tokenServer;

    @Autowired
    private UserService userService;

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private PersonInfoService personInfoService;

    @ApiOperation(value = "/addTrack 添加访客轨迹", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"vid\":\"123\",\n" +
                    "    \"longitude\":\"1.222\",\n" +
                    "    \"latitude\":\"1.222\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addTrack")
    @ResponseBody
    public RespInfo addTrack(@RequestBody List<Track> trackList , HttpServletRequest request) {
        //TODO 权限检查
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_VISITOR.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole()))) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        if(trackList.size()==0){
            return new RespInfo(ErrorEnum.E_0.getCode(),ErrorEnum.E_0.getMsg());
        }

       //数据清洗
        if(StringUtils.isNotEmpty(trackList.get(0).getVid())) {
            Visitor visitor = visitorService.getVisitorById(Integer.parseInt(trackList.get(0).getVid()));
            if(visitor == null || visitor.getSignOutDate() != null){
                //已签离
                return new RespInfo(ErrorEnum.E_1203.getCode(),ErrorEnum.E_1203.getMsg());
            }
        }
        for(int i=0;i<trackList.size();i++){
            trackList.get(i).setOpenId(authToken.getOpenid());
        }
        trackService.batchSaveTrack(trackList);


        //创建定时任务检查轨迹
        ValueOperations<String, String> valueOperations = strRedisTemplate.opsForValue();
        SysLog.info("添加检查轨迹定时任务："+trackList.get(0).getVid());
        valueOperations.set("track_" + trackList.get(0).getVid(), "", 6, TimeUnit.MINUTES);


        int i = visitorService.checkTrack(trackList.get(0).getVid());
        ErrorEnum errorEnum = ErrorEnum.getByCode(i);
        return new RespInfo(errorEnum.getCode(),errorEnum.getMsg());
    }

    @ApiOperation(value = "/getTrackList 获取某个访客轨迹", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"openId\":\"123\",\n" +
                    "    \"vid\":\"123\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getTrackList")
    @ResponseBody
    public RespInfoT<List<Track>> getTrackList(@RequestBody RequestVisit req, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
        &&!AuthToken.ROLE_VISITOR.equals(authToken.getAccountRole())
                &&!AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())) {
//            return new RespInfoT(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        if(AuthToken.ROLE_VISITOR.equals(authToken.getAccountRole())
        ||AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())){
            req.setOpenId(authToken.getOpenid());
        }
        if(StringUtils.isEmpty(req.getOpenId()) && StringUtils.isNotEmpty(req.getPhone())){
            Person person = personInfoService.getVisitPersonByPhone(req.getPhone());
            if(person==null || StringUtils.isEmpty(person.getPopenid())){
                person = personInfoService.getInvitePersonByPhone(req.getPhone());
            }

            if(person==null){
                return new RespInfoT(ErrorEnum.E_057.getCode(),ErrorEnum.E_057.getMsg());
            }
            req.setOpenId(person.getPopenid());
        }
        LambdaQueryWrapper<Track> lambdaQueryWrapper = Wrappers.lambdaQuery(Track.class);
        lambdaQueryWrapper.select(Track::getOpenId,Track::getCreateTime,Track::getLatitude,Track::getLongitude);
        lambdaQueryWrapper.eq(StringUtils.isNotEmpty(req.getOpenId()),Track::getOpenId,req.getOpenId());
        lambdaQueryWrapper.eq(req.getVid()!=0,Track::getVid,req.getVid()+"");
        //        lambdaQueryWrapper.eq(Track::getUserid,authToken.getUserid());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(StringUtils.isNotEmpty(req.getStartDate())){
            try {
                lambdaQueryWrapper.ge(Track::getCreateTime, dateFormat.parse(req.getStartDate()));
            }catch (Exception e){

            }
        }
        if(StringUtils.isNotEmpty(req.getEndDate())){
            try {
                lambdaQueryWrapper.le(Track::getCreateTime, dateFormat.parse(req.getEndDate()));
            }catch (Exception e){

            }
        }

        List<Track> list = trackService.list(lambdaQueryWrapper);
        return new RespInfoT(ErrorEnum.E_0.getCode(),ErrorEnum.E_0.getMsg(),list);
    }

    @ApiOperation(value = "/getLatestPointList 获取最新访客所在位置", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":\"123\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getLatestPointList")
    @ResponseBody
    public RespInfoT<List<Track>> getLatestPointList(@RequestBody RequestVisit req, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole()))) {
            return new RespInfoT(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        Calendar current = Calendar.getInstance();// 获取当前日期
        current.setTime(new Date());
        current.add(Calendar.MINUTE, -30);
        current.add(Calendar.DAY_OF_MONTH,-10);
        req.setReqDate(current.getTime());

        List<Track> list = trackService.getLatestPointList(req);
        Person person = null;
        for(Track track:list){
            person = personInfoService.getPersonByOpenid(track.getOpenId());
            if(person!=null)
            {
                track.setName(person.getPname());
            }
        }
        return new RespInfoT(ErrorEnum.E_0.getCode(),ErrorEnum.E_0.getMsg(),list);
    }


    @ApiOperation(value = "/addGFPolyline 创建多边形围栏", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"name\":\"厂区1\",\n" +
                    "    \"points\":\"0 0,0 10,10 0\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addGFPolyline")
    @ResponseBody
    public RespInfoT<Geofence> addGFPolyline(@RequestBody Geofence req, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole()))) {
            return new RespInfoT(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        req.setUserid(authToken.getUserid());
        req.setType("polyline");
        int ret = geofenceService.addGFPolyline(req);
        if(ret != 1){
            return new RespInfoT(ErrorEnum.E_505.getCode(),ErrorEnum.E_505.getMsg());
        }

        return new RespInfoT(ErrorEnum.E_0.getCode(),ErrorEnum.E_0.getMsg(),req);
    }


    @ApiOperation(value = "/updateGFPolyline 修改多边形围栏", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"name\":\"厂区1\",\n" +
                    "    \"points\":\"0 0,0 10,10 0\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateGFPolyline")
    @ResponseBody
    public RespInfoT<Geofence> updateGFPolyline(@RequestBody Geofence req, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole()))) {
            return new RespInfoT(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        req.setUserid(authToken.getUserid());
        req.setType("polyline");
        int ret = geofenceService.updateGFPolyline(req);
        if(ret != 1){
            return new RespInfoT(ErrorEnum.E_505.getCode(),ErrorEnum.E_505.getMsg());
        }

        return new RespInfoT(ErrorEnum.E_0.getCode(),ErrorEnum.E_0.getMsg(),req);
    }

    @ApiOperation(value = "/delGeofences 批量删除围栏", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"id\":\"电子围栏id\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/delGeofences")
    @ResponseBody
    public RespInfo delGeofences(@RequestBody List<Geofence> req, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole()))) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        if(req.size()==0){
            return new RespInfo(ErrorEnum.E_0.getCode(),ErrorEnum.E_0.getMsg());
        }
        List<Integer> ids = new ArrayList<>();
        for(Geofence gf:req){
            if(gf.getId()!=0){
                ids.add(gf.getId());
            }
        }
        LambdaQueryWrapper<Geofence> lambdaQueryWrapper = Wrappers.lambdaQuery(Geofence.class);
        lambdaQueryWrapper.in(Geofence::getId,ids);
        lambdaQueryWrapper.eq(Geofence::getUserid,authToken.getUserid());
        if(geofenceService.remove(lambdaQueryWrapper)){
            return new RespInfo(ErrorEnum.E_0.getCode(),ErrorEnum.E_0.getMsg());
        }

        return new RespInfo(ErrorEnum.E_703.getCode(),ErrorEnum.E_703.getMsg());
    }

    @ApiOperation(value = "/getGeofences 获取围栏列表", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"id\":\"电子围栏id\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getGeofences")
    @ResponseBody
    public RespInfoT<List<Geofence>> getGeofences(@RequestBody Geofence req, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
//        if ((!AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
//                && !AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole()))) {
//            return new RespInfoT(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
//        }
        if(authToken.getUserid()==0){
            //访客
            UserInfo userInfo = userService.selectByName(Constant.ACCOUNT);
            authToken.setUserid(userInfo.getUserid());
        }
        LambdaQueryWrapper<Geofence> lambdaQueryWrapper = Wrappers.lambdaQuery(Geofence.class);
        if ((!AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole()))) {
            lambdaQueryWrapper.select(Geofence::getId,Geofence::getName);
        }
        lambdaQueryWrapper.eq(req.getId()!=0,Geofence::getId,req);
        lambdaQueryWrapper.eq(Geofence::getUserid,authToken.getUserid());
        List<Geofence> list = geofenceService.list(lambdaQueryWrapper);

        return new RespInfoT(ErrorEnum.E_0.getCode(),ErrorEnum.E_0.getMsg(),list);
    }


    @ApiOperation(value = "/addDrivingRoute 创建路径", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"name\":\"西门到f4\",\n" +
                    "    \"points\":\"0 0,0 10,10 0\",\n" +
                    "    \"remark\":\"限高4米\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addDrivingRoute")
    @ResponseBody
    public RespInfoT<DrivingRoute> addDrivingRoute(@RequestBody DrivingRoute req, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole()))) {
            return new RespInfoT(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        req.setUserid(authToken.getUserid());
        int ret = drivingRouteService.addRoute(req);
        if(ret != 1){
            return new RespInfoT(ErrorEnum.E_505.getCode(),ErrorEnum.E_505.getMsg());
        }

        return new RespInfoT(ErrorEnum.E_0.getCode(),ErrorEnum.E_0.getMsg(),req);
    }

    @ApiOperation(value = "/updateDrivingRoute 修改路径", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"name\":\"西门到f4\",\n" +
                    "    \"points\":\"0 0,0 10,10 0\",\n" +
                    "    \"remark\":\"限高4米\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateDrivingRoute")
    @ResponseBody
    public RespInfoT<DrivingRoute> updateDrivingRoute(@RequestBody DrivingRoute req, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole()))) {
            return new RespInfoT(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        req.setUserid(authToken.getUserid());
        int ret = drivingRouteService.updateRoute(req);
        if(ret != 1){
            return new RespInfoT(ErrorEnum.E_505.getCode(),ErrorEnum.E_505.getMsg());
        }

        return new RespInfoT(ErrorEnum.E_0.getCode(),ErrorEnum.E_0.getMsg(),req);
    }

    @ApiOperation(value = "/delDrivingRoutes 批量删除围栏", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"id\":\"电子围栏id\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/delDrivingRoutes")
    @ResponseBody
    public RespInfo delDrivingRoutes(@RequestBody List<DrivingRoute> req, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole()))) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        if(req.size()==0){
            return new RespInfo(ErrorEnum.E_0.getCode(),ErrorEnum.E_0.getMsg());
        }
        List<Integer> ids = new ArrayList<>();
        for(DrivingRoute route:req){
            if(route.getId()!=0){
                ids.add(route.getId());
            }
        }
        LambdaQueryWrapper<DrivingRoute> lambdaQueryWrapper = Wrappers.lambdaQuery(DrivingRoute.class);
        lambdaQueryWrapper.in(DrivingRoute::getId,ids);
        lambdaQueryWrapper.eq(DrivingRoute::getUserid,authToken.getUserid());
        if(drivingRouteService.remove(lambdaQueryWrapper)){
            return new RespInfo(ErrorEnum.E_0.getCode(),ErrorEnum.E_0.getMsg());
        }

        return new RespInfo(ErrorEnum.E_703.getCode(),ErrorEnum.E_703.getMsg());
    }


    @ApiOperation(value = "/getRoutes 获取路径列表", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"id\":\"电子围栏id\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getRoutes")
    @ResponseBody
    public RespInfoT<List<DrivingRoute>> getRoutes(@RequestBody Geofence req, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
//        if ((!AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
//                && !AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole()))) {
//            return new RespInfoT(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
//        }
        if(authToken.getUserid()==0){
            //访客
            UserInfo userInfo = userService.selectByName(Constant.ACCOUNT);
            authToken.setUserid(userInfo.getUserid());
        }
        LambdaQueryWrapper<DrivingRoute> lambdaQueryWrapper = Wrappers.lambdaQuery(DrivingRoute.class);
        if ((!AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole()))) {
            lambdaQueryWrapper.select(DrivingRoute::getId,DrivingRoute::getName);
        }
        lambdaQueryWrapper.eq(req.getId()!=0,DrivingRoute::getId,req);
        lambdaQueryWrapper.eq(DrivingRoute::getUserid,authToken.getUserid());
        List<DrivingRoute> list = drivingRouteService.list(lambdaQueryWrapper);

        return new RespInfoT(ErrorEnum.E_0.getCode(),ErrorEnum.E_0.getMsg(),list);
    }

    @ApiOperation(value = "/getDrivingRoutes 获取连接两个电子围栏的路径", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "[" +
                    "{\n" +
                    "    \"id\":\"电子围栏id\",\n" +
                    "}," +
                    "{\n" +
                    "    \"id\":\"电子围栏id\",\n" +
                    "}" +
                    "]"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getDrivingRoutes")
    @ResponseBody
    public RespInfoT<List<DrivingRoute>> getDrivingRoutes(@RequestBody List<Geofence> req, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
//        if ((!AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
//                && !AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole()))) {
//            return new RespInfoT(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
//        }
        if(authToken.getUserid()==0){
            //访客
            UserInfo userInfo = userService.selectByName(Constant.ACCOUNT);
            authToken.setUserid(userInfo.getUserid());
        }
        if(req.size()!=2){
            return new RespInfoT(ErrorEnum.E_505.getCode(), ErrorEnum.E_505.getMsg());
        }

        List<DrivingRoute> list = drivingRouteService.getRoutesByGeofenceId(req.get(0).getId(),req.get(1).getId());

        return new RespInfoT(ErrorEnum.E_0.getCode(),ErrorEnum.E_0.getMsg(),list);
    }


    @ApiOperation(value = "/getVisitorRoutes 获取访客绑定路径", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"phone\":\"访客手机号，管理员访问必填\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getVisitorRoutes")
    @ResponseBody
    public RespInfoT<DrivingRoute> getVisitorRoutes(@RequestBody RequestVisit req, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_VISITOR.equals(authToken.getAccountRole()))) {
            return new RespInfoT(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        if(authToken.getUserid()==0){
            //访客
            UserInfo userInfo = userService.selectByName(Constant.ACCOUNT);
            authToken.setUserid(userInfo.getUserid());
        }
        if(AuthToken.ROLE_VISITOR.equals(authToken.getAccountRole())){
            Person person = personInfoService.getPersonByOpenid(authToken.getOpenid());
            if(person == null){
                return new RespInfoT(ErrorEnum.E_701.getCode(), ErrorEnum.E_701.getMsg());
            }
            req.setPhone(person.getPmobile());
        }

        //有没有预约记录
        LambdaQueryWrapper<Visitor> lambdaQueryWrapper = Wrappers.lambdaQuery(Visitor.class);
        lambdaQueryWrapper.ge(Visitor::getAppointmentDate, DateUtil.getMinTimeDateByDate(new Date()))
                .le(Visitor::getAppointmentDate, DateUtil.getMaxTimeDateByDate(new Date()))
                .isNull(Visitor::getSignOutDate)
                .eq(Visitor::getVphone,req.getPhone())
                .eq(Visitor::getUserid,authToken.getUserid())
                .orderByAsc(Visitor::getAppointmentDate);
        List<Visitor> visitorList = visitorService.list(lambdaQueryWrapper);

        if(visitorList.size()==0){
            return new RespInfoT(ErrorEnum.E_057.getCode(),ErrorEnum.E_057.getMsg());
        }

        Visitor currentVt = null;
        for(Visitor visitor:visitorList){
            String routes = visitor.getExtendValue(VisitorService.EXTEND_KEY_ROUTE);
            if(StringUtils.isNotEmpty(routes)){
                currentVt = visitor;
                break;
            }
        }

        if(currentVt == null){
            return new RespInfoT(ErrorEnum.E_057.getCode(),ErrorEnum.E_057.getMsg());
        }

        DrivingRoute route = drivingRouteService.getById(Integer.parseInt(currentVt.getExtendValue(VisitorService.EXTEND_KEY_ROUTE)));
        if(route == null){
            return new RespInfoT(ErrorEnum.E_057.getCode(),ErrorEnum.E_057.getMsg());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("vid",currentVt.getVid());
        result.put("geofence",currentVt.getExtendValue(VisitorService.EXTEND_KEY_GEOFENCE));
        result.put("route",route);
        return new RespInfoT(ErrorEnum.E_0.getCode(),ErrorEnum.E_0.getMsg(),result);
    }


    @ApiOperation(value = "/bindGeofence 绑定电子围栏", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "\"id\":\"电子围栏id\",\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/bindGeofence")
    @ResponseBody
    public RespInfo bindGeofence(@RequestBody Geofence req, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_VISITOR.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole()))) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }


        Person person = personInfoService.getPersonByOpenid(authToken.getOpenid());
        if(person == null){
            return new RespInfo(ErrorEnum.E_701.getCode(), ErrorEnum.E_701.getMsg());
        }

        if(authToken.getUserid()==0){
            //访客
            UserInfo userInfo = userService.selectByName(Constant.ACCOUNT);
            authToken.setUserid(userInfo.getUserid());
        }
        //有没有预约记录
        LambdaQueryWrapper<Visitor> lambdaQueryWrapper = Wrappers.lambdaQuery(Visitor.class);
        lambdaQueryWrapper.ge(Visitor::getAppointmentDate, DateUtil.getMinTimeDateByDate(new Date()))
                .le(Visitor::getAppointmentDate, DateUtil.getMaxTimeDateByDate(new Date()))
                .isNull(Visitor::getSignOutDate)
                .eq(Visitor::getVphone,person.getPmobile())
                .eq(Visitor::getUserid,authToken.getUserid())
                .orderByAsc(Visitor::getAppointmentDate);
        Visitor visitor = visitorService.getOne(lambdaQueryWrapper);

        if(visitor == null){
           //新增预约
            Visitor vt = new Visitor();
            vt.setUserid(authToken.getUserid());
            vt.setVphone(person.getPmobile());
            vt.setVname(person.getPname());
            vt.setAppointmentDate(new Date());
            visitorService.addVisitorApponintmnet(vt);
            visitor = vt;
        }
        int gateId = 0;//大门电子围栏
        List<DrivingRoute> ids = drivingRouteService.getRoutesByGeofenceId(req.getId(),gateId);
        if(ids.size()>0){
            visitor.addExtendValue(VisitorService.EXTEND_KEY_ROUTE,ids.get(0).getId()+"");
            visitor.addExtendValue(VisitorService.EXTEND_KEY_GEOFENCE,req.getId()+"");
            visitorService.updateVisitorAppointment(visitor);
            Map<String, Object> result = new HashMap<>();
            result.put("vid",visitor.getVid());
            result.put("geofence",visitor.getExtendValue(VisitorService.EXTEND_KEY_GEOFENCE));
            result.put("route",ids.get(0));
            return new RespInfo(ErrorEnum.E_0.getCode(),ErrorEnum.E_0.getMsg(),result);
        }

        return new RespInfo(ErrorEnum.E_1201.getCode(),ErrorEnum.E_1201.getMsg());

    }
}
