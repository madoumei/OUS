package com.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.client.bean.*;
import com.client.service.*;
import com.config.exception.ErrorEnum;
import com.config.qicool.common.persistence.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utils.*;
import com.utils.httpUtils.HttpClientUtil;
import com.utils.jsonUtils.JacksonJsonUtil;
import com.web.bean.*;
import com.web.service.*;
import com.web.service.impl.AgentInfoExcelDownLoad;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/**")
@Api(value = "ChartsController", tags = "API_报表管理", hidden = true)
public class ChartsController {

    @Autowired
    private VisitorService visitorService;

    @Autowired
    ManagerService mgrService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private TokenServer tokenServer;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private VisitorTypeService visitorTypeService;

    @Autowired
    private ExtendVisitorService extendVisitorService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PersonInfoService personInfoService;

    @Autowired
    private OperateLogService operateLogService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private SubAccountService subAccountService;

    /**
     * 报表1，根据条件查询访客记录
     *
     * @param req
     * @param request
     * @return
     */
    @ApiOperation(value = "/SearchVisitByCondition 访客报表,根据条件查询访客记录", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"svcType\":\"0\",\n" +
                    "    \"empName\":\"\",\n" +
                    "    \"empPhone\":\"\",\n" +
                    "    \"name\":\"\",\n" +
                    "    \"phone\":\"\",\n" +
                    "    \"visitType\":\"\",\n" +
                    "    \"vcompany\":\"\",\n" +
                    "    \"date\":\"2021-03-17\",\n" +
                    "    \"endDate\":\"2021-03-17\",\n" +
                    "    \"signInGate\":\"\",\n" +
                    "    \"subaccountId\":0,\n" +
                    "    \"vType\":\"\",\n" +
                    "    \"userid\":2147483647\n" +
                    "    \"gid\":12,24,32\n" +
                    "} 搜索指定时间内的访客记录，如果已经签到以签到时间为准，如果没有签到则以预约时间为准"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/SearchVisitByCondition")
    @ResponseBody
    public RespInfoT<List<RespVisitor>> SearchVisitByCondition(
            @ApiParam(value = "RequestVisit 请求访客Bean", required = true) @Validated @RequestBody RequestVisit req,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);

        if (!AuthToken.ROLE_VISITOR.equals(authToken.getAccountRole()) && authToken.getUserid() != req.getUserid()) {
            return new RespInfoT(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        if(AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())){
            //访问所有
        }
        else if (AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
        ||AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
        ||AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())){
        		Manager mgr = mgrService.getManagerByAccount(authToken.getLoginAccountId());
        		String gids[]=req.getGid().split(",");
        		String mgids[]=mgr.getGid().split(",");
        	    boolean auth=UtilTools.arrayContain(gids, mgids);
        		if(auth) {
                return new RespInfoT(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        		}
        }
        //访客只能查询自己的数据
        else if (AuthToken.ROLE_VISITOR.equals(authToken.getAccountRole())) {
            Person personByOpenid = personInfoService.getVisitPersonByOpenid(authToken.getOpenid());
            if (null == personByOpenid) {
                return new RespInfoT(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
            req.setPhone(personByOpenid.getPmobile());
        }

        //员工权限
        else if (AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())) {
            int empid = Integer.parseInt(authToken.getLoginAccountId());
            Employee emp = employeeService.getEmployee(empid);
            if (null == emp) {
                return new RespInfoT(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
            req.setEmpPhone(emp.getEmpPhone());
            req.setEmpid(emp.getEmpid());
        }

        //入驻企业管理员权限
        else if (AuthToken.ROLE_SUB_COMPANY.equals(authToken.getAccountRole())) {
            SubAccount oldsa = subAccountService.getSubAccountByEmail(authToken.getLoginAccountId());
            if (null == oldsa) {
                return new RespInfoT(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
            req.setSubaccountId(oldsa.getSubAccountiId());
            req.setUserid(oldsa.getUserid());
        }else  {
            return new RespInfoT(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        Map<String, Object> map = new HashMap<String, Object>();

        String startDate = req.getDate();
        String endDate = req.getEndDate();
        String empName = req.getEmpName();
        String empPhone = req.getEmpPhone();
        String name = req.getName();
        String visitType = req.getVisitType();
        String email = req.getEmail();
        String phone = req.getPhone();
        String team = req.getTeam();
        String vcompany = req.getVcompany();
        String signInGate = req.getSignInGate();
        int subaccountid = req.getSubaccountId();
        String company = req.getCompany();
        String vType = req.getvType();
        String gid = req.getGid();
        int svcType = req.getSvcType();
        String plateNum = req.getPlateNum();
        String vtExtendCol = req.getVtExtendCol();

        map.put("userid", String.valueOf(req.getUserid()));
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        map.put("empName", empName);
        map.put("name", name);
        map.put("visitType", visitType);
        map.put("email", email);
        map.put("phone", phone);
        map.put("team", team);
        map.put("signInGate", signInGate);
        map.put("vcompany", vcompany);
        map.put("company", company);
        map.put("vType", vType);
        map.put("subaccountId", String.valueOf(subaccountid));
        map.put("svcType", String.valueOf(svcType));
        map.put("plateNum",plateNum);

        //员工类型账号请求数据约束，只能获取自己的数据
        if(StringUtils.isNotBlank(empPhone)) {
            map.put("empPhone", empPhone);
        }else {
            //没有手机号的情况
            map.put("empid", String.valueOf(req.getEmpid()));
        }

        map.put("gid", gid);

        if(vtExtendCol != null) {
            JSONObject jsonObject = JSON.parseObject(vtExtendCol);
            map.put("vtExtendCol", jsonObject);
        }

        List<RespVisitor> rvlist = visitorService.searchVisitByCondition(map);

        //获取员工部门名称
        List<Department> departments = departmentService.getAllDepartmentList(req.getUserid());
        Map<Integer, String> deptMap = new HashMap<>();
        for (int i = 0; i < departments.size(); i++) {
            Department dept = departments.get(i);
            deptMap.put(dept.getDeptid(), dept.getDeptName());
        }
        rvlist = rvlist.stream().map(respVisitor -> {
            if (StringUtils.isNotBlank(deptMap.get(respVisitor.getEmpdeptid()))) {
                respVisitor.setEmpDeptName(deptMap.get(respVisitor.getEmpdeptid()));
            }
            return respVisitor;
        }).collect(Collectors.toList());

        return new RespInfoT(0, "success", rvlist);
    }

    @ApiOperation(value = "/getAllVisitorLineChart 访客统计图条件查询", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"date\":\"2021-03-18\",\n" +
                    "    \"endDate\":\"2021-03-18\",\n" +
                    "    \"chartStatus\":2,\n" +
                    "    \"signinType\":0\n" +
                    "    \"gid\":12,24,32\n" +
                    "}"
    )
    @RequestMapping(value = "/getAllVisitorLineChart", method = RequestMethod.POST)
    @ResponseBody
    public RespInfo getAllVisitorLineChart(
            @ApiParam(value = "RequestVisit 请求访客Bean", required = true) @Validated @RequestBody RequestVisit visit,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);

        //只有管理员可以访问
        if(AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())){
            //访问所有
            visit.setUserid(authToken.getUserid());
        }
        else if (AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())){
    		Manager mgr = mgrService.getManagerByAccount(authToken.getLoginAccountId());
    		String gids[]=visit.getGid().split(",");
    		String mgids[]=mgr.getGid().split(",");
    	    boolean auth=UtilTools.arrayContain(gids, mgids);
    		if(auth) {
    			return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
    		}
        }else  {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        
        visit.setUserid(authToken.getUserid());
        List<VisitorChart> vclist = visitorService.getAllVisitorLineChart(visit);
        return new RespInfo(0, "success", vclist);

    }


    @ApiOperation(value = "/ExportVisitorList 报表1，导出访客数据", httpMethod = "GET",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "get示例入参：/ExportVisitorList?userid=2147483647&date=2021-03-18&endDate=2021-03-18&subaccountId=0&svcType=0&tid=221&expExtCols=&exportCols=11111001111111111111011&token=${authToken}"
    )
    @RequestMapping(method = RequestMethod.GET, value = "/ExportVisitorList")
    public void ExportVisitorList(HttpServletRequest req, HttpServletResponse response) {
        String ctoken = req.getParameter("token");

        if(StringUtils.isNotBlank(ctoken)) {
        	ctoken=UtilTools.getTokenByPcode(redisTemplate, ctoken + "_pcode", "ValidationPcode");
        }

        if(StringUtils.isBlank(ctoken)){
        	System.out.println("token is null");
        	return;
        }

        int userid = Integer.parseInt(req.getParameter("userid"));
        String exportCols = req.getParameter("exportCols");
        String expExtCols = req.getParameter("expExtCols");
        String startDate = req.getParameter("date");
        String endDate = req.getParameter("endDate");
        String empName = req.getParameter("empName");
        String name = req.getParameter("name");
        String visitType = req.getParameter("visitType");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");
        String team = req.getParameter("team");
        String signInGate = req.getParameter("signInGate");
        String vcompany = req.getParameter("vcompany");
        String tid = req.getParameter("tid");
        String gid = req.getParameter("gid");
        String subaccountid = req.getParameter("subaccountId");
        String vType = req.getParameter("vType");
        String plateNum = req.getParameter("plateNum");
        if (StringUtils.isEmpty(subaccountid)) {
            subaccountid = "0";
        }
        String svcType=req.getParameter("svcType");
        String vtExtendCol=req.getParameter("vtExtendCol");
        try {
            //检查权限
            if(tokenServer.checkUserAuthorityForExport(mgrService,redisTemplate,response, ctoken, userid, gid)) return;

            Map<String, Object> map = new HashMap<String, Object>();

            map.put("userid", String.valueOf(userid));
            map.put("startDate", startDate);
            map.put("endDate", endDate);
            map.put("empName", empName);
            map.put("name", name);
            map.put("visitType", visitType);
            map.put("email", email);
            map.put("phone", phone);
            map.put("team", team);
            map.put("subaccountId", subaccountid);
            map.put("signInGate", signInGate);
            map.put("vcompany", vcompany);
            map.put("gid", gid);
            map.put("svcType", svcType);
            map.put("vType",vType);
            map.put("plateNum",plateNum);
            if(vtExtendCol != null) {
                vtExtendCol = java.net.URLDecoder.decode(vtExtendCol,"UTF-8");
                JSONObject jsonObject = JSON.parseObject(vtExtendCol);
                map.put("vtExtendCol", jsonObject);
            }
            List<RespVisitor> rvlist = visitorService.searchVisitByCondition(map);
            List<ExtendVisitor> evlist = new ArrayList<ExtendVisitor>();

            VisitorType vt = new VisitorType();
            vt.setUserid(userid);
            vt.setTid(Integer.parseInt(tid));
            VisitorType visType = visitorTypeService.getVisitorTypeByTid(vt);

            ExtendVisitor ev = new ExtendVisitor();
            ev.seteType(visType.getvType());
            ev.setUserid(userid);
            evlist = extendVisitorService.getExtendVisitorByType(ev);

            Map<String, String> evmap = new HashMap<String, String>();
            for (int i = 0; i < evlist.size(); i++) {
                evmap.put(evlist.get(i).getFieldName(), evlist.get(i).getDisplayName());
            }

            UserInfo userinfo = userService.getBaseUserInfo(userid);
            String filename = userinfo.getCompany() + "访客记录_" + startDate + "-" + endDate;
            HSSFWorkbook excel = new HSSFWorkbook();

            ExcelDownLoad download = new AgentInfoExcelDownLoad();
            excel = download.createDownLoadExcel(rvlist, excel, exportCols, evmap, expExtCols);
            excel.setSheetName(0,filename);
            ServletOutputStream out = response.getOutputStream();
            response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(filename + ".xls", "UTF-8"));
            excel.write(out);
            out.flush();
            out.close();

            //添加日志
            OperateLog.addExLog(operateLogService,mgrService,req, ctoken, userinfo,
                    OperateLog.MODULE_VISITOR, rvlist.size(), "导出"+filename+":"+map.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @ApiOperation(value = "/getPermissionCode 获取授权码", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "\n" +
                    "}")
    @RequestMapping(method = RequestMethod.POST, value = "/getPermissionCode")
    @ResponseBody
    public RespInfo getPermissionCode(HttpServletRequest req) {
    	String ctoken = req.getHeader("X-COOLVISIT-TOKEN");
        String vcode = UtilTools.produceId(10);
        ValCode vc = new ValCode();
        vc.setVcode(vcode);

        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(vcode + "_pcode", "ValidationPcode", ctoken);
        redisTemplate.expire(vcode + "_pcode", 2, TimeUnit.MINUTES);

        return new RespInfo(0, "success", vc);
    }

    @Deprecated
    @RequestMapping(value="/ExportVisitorListPost",method=RequestMethod.POST)
    public void ExportVisitorListPost(@RequestBody Map<String,String> params, HttpServletRequest req, HttpServletResponse response) {
    	String ctoken = req.getHeader("X-COOLVISIT-TOKEN");
    	int userid = Integer.parseInt(params.get("userid"));
        String exportCols = params.get("exportCols");
        String expExtCols = params.get("expExtCols");
        String startDate = params.get("date");
        String endDate = params.get("endDate");
        String empName = params.get("empName");
        String name = params.get("name");
        String visitType = params.get("visitType");
        String email = params.get("email");
        String phone = params.get("phone");
        String team = params.get("team");
        String signInGate = params.get("signInGate");
        String vcompany = params.get("vcompany");
        String tid = params.get("tid");
        String gid = params.get("gid");

        String subaccountid =(String) params.get("subaccountId");
        if (StringUtils.isEmpty(subaccountid)) {
            subaccountid = "0";
        }
        String svcType=(String) params.get("svcType");

        try {
            //检查权限
            if(tokenServer.checkUserAuthorityForExport(mgrService,redisTemplate,response, ctoken, userid, gid)) return;

            Map<String, Object> map = new HashMap<String, Object>();

            map.put("userid", String.valueOf(userid));
            map.put("startDate", startDate);
            map.put("endDate", endDate);
            map.put("empName", empName);
            map.put("name", name);
            map.put("visitType", visitType);
            map.put("email", email);
            map.put("phone", phone);
            map.put("team", team);
            map.put("subaccountId", subaccountid);
            map.put("signInGate", signInGate);
            map.put("vcompany", vcompany);
            map.put("gid", gid);
            map.put("svcType", svcType);

            List<RespVisitor> rvlist = visitorService.searchVisitByCondition(map);
            List<ExtendVisitor> evlist = new ArrayList<ExtendVisitor>();

            VisitorType vt = new VisitorType();
            vt.setUserid(userid);
            vt.setTid(Integer.parseInt(tid));
            VisitorType visType = visitorTypeService.getVisitorTypeByTid(vt);

            ExtendVisitor ev = new ExtendVisitor();
            ev.seteType(visType.getvType());
            ev.setUserid(userid);
            evlist = extendVisitorService.getExtendVisitorByType(ev);

            Map<String, String> evmap = new HashMap<String, String>();
            for (int i = 0; i < evlist.size(); i++) {
                evmap.put(evlist.get(i).getFieldName(), evlist.get(i).getDisplayName());
            }

            UserInfo userinfo = userService.getBaseUserInfo(userid);
            String filename = userinfo.getCompany() + "访客记录_" + startDate + "-" + endDate;
            HSSFWorkbook excel = new HSSFWorkbook();

            ExcelDownLoad download = new AgentInfoExcelDownLoad();
            excel = download.createDownLoadExcel(rvlist, excel, exportCols, evmap, expExtCols);
            excel.setSheetName(0,filename);
            ServletOutputStream out = response.getOutputStream();
            response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(filename + ".xls", "UTF-8"));
            excel.write(out);
            out.flush();
            out.close();

            //添加日志
            OperateLog.addExLog(operateLogService,mgrService,req, ctoken, userinfo,
                    OperateLog.MODULE_VISITOR, rvlist.size(), "导出"+filename+":"+map.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Deprecated
    @ApiOperation(value = "/newExportVisitorList 报表1，导出访客数据（支持百万级）", httpMethod = "GET",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "GET示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.GET, value = "/newExportVisitorList")
    public void newExportVisitorList(HttpServletRequest req, HttpServletResponse response) {
        String ctoken = req.getParameter("token");
        
        if(StringUtils.isNotBlank(ctoken)) {
        	ctoken=UtilTools.getTokenByPcode(redisTemplate, ctoken + "_pcode", "ValidationPcode");
        }
        
        if(StringUtils.isBlank(ctoken)){
        	System.out.println("token is null");
        	return;
        }
        
        int userid = Integer.parseInt(req.getParameter("userid"));
        String exportCols = req.getParameter("exportCols");
        String gid = req.getParameter("gid");
        try {
            String startDate = req.getParameter("date");
            String endDate = req.getParameter("endDate");
            String name = req.getParameter("name");
            //String floors = req.getParameter("floors");
            String[] floors = req.getParameterValues("floor");
            String company = req.getParameter("company");
            String subaccountid = req.getParameter("subaccountId");
            String vPhone = req.getParameter("vPhone");
            String visitType = req.getParameter("visitType");
            String isBlackList = req.getParameter("isBlackList");
            String bCompany = req.getParameter("bCompany");
            String clientNo = req.getParameter("clientNo");
            String sex = req.getParameter("sex");
            String isTakeCard = req.getParameter("isTakeCard");
            String isWeekendVisitor = req.getParameter("isWeekendVisitor");
            String isHolidayVisitor = req.getParameter("isHolidayVisitor");
            String isSCTimeVisitor = req.getParameter("isSCTimeVisitor");
            String isDaysOffVisitor = req.getParameter("isDaysOffVisitor");
            String category = req.getParameter("category");
            String[] gateName = req.getParameterValues("gateName");
            if (StringUtils.isEmpty(subaccountid)) {
                subaccountid = "0";
            }

            //检查权限
            if(tokenServer.checkUserAuthorityForExport(mgrService,redisTemplate,response, ctoken, userid, gid)) return;

            Map<String, Object> map = new HashMap<String, Object>();

            map.put("userid", String.valueOf(userid));
            map.put("startDate", startDate);
            map.put("endDate", endDate);
//            map.put("floors", floors);
            map.put("company", company);
            map.put("name", name);
            map.put("phone", vPhone);
            map.put("visitType", visitType);
            map.put("isBlackList", isBlackList);
            map.put("bCompany", bCompany);
            map.put("clientNo", clientNo);
            map.put("sex", sex);
            map.put("isTakeCard", isTakeCard);
            map.put("isWeekendVisitor", isWeekendVisitor);
            map.put("isHolidayVisitor", isHolidayVisitor);
            map.put("isSCTimeVisitor", isSCTimeVisitor);
            map.put("isDaysOffVisitor", isDaysOffVisitor);
            map.put("category", String.valueOf(category));
            if (gateName.length > 0 && StringUtils.isNotEmpty(gateName[0])) {
                List<Integer> gidlist = new ArrayList<>();
                for (int i = 0; i < gateName.length; i++) {
                    Gate gate = new Gate();
                    gate.setUserid(userid);
                    gate.setGname(gateName[i]);
                    Gate gateByName = addressService.getGateByName(gate);
                    gidlist.add(Integer.parseInt(gateByName.getGids()));
                }
                map.put("gid", gidlist);
            }
            if (Integer.parseInt(subaccountid) != 0) {
                map.put("subaccountId", subaccountid);
            }
            if (Integer.parseInt(subaccountid) != 0) {
                map.put("subaccountId", subaccountid);
            }


            List<VisitorRecord> vrs = visitorService.newSearchVisitorByCondition1(map);
            if (null != vrs && floors.length > 0 && StringUtils.isNotEmpty(floors[0])) {
                List<VisitorRecord> visitorRecords = new ArrayList<>();
                Iterator<VisitorRecord> it = vrs.iterator();
                while (it.hasNext()) {
                    VisitorRecord visitor = it.next();
                    // T1-11/12/13F&80  T1-11/12/13M&80
                    if (StringUtils.isNotEmpty(visitor.getFloors())) {
                        String respfloors = visitor.getFloors().split("-")[1];
                        respfloors = respfloors.split("&")[0];
                        char c = respfloors.charAt(respfloors.length() - 1);
                        if (!Character.isDigit(c)) {
                            respfloors = respfloors.substring(0, respfloors.length() - 1);
                        }
                        String[] floorsName = respfloors.split("/");
                        List<String> floorslist = Arrays.asList(floorsName);
                        for (String floor : floors) {
                            if (floorslist.contains(floor)) {
                                visitorRecords.add(visitor);
                            }
                        }
                    }
                }
                vrs = visitorRecords;
            }
            UserInfo userinfo = userService.getBaseUserInfo(userid);
            String filename = userinfo.getCompany() + "访客记录_" + startDate + "-" + endDate;
            Gate gate = new Gate();
            gate.setUserid(userinfo.getUserid());
            List<Gate> gateList = addressService.getGateList(gate);
            Map<String, String> gateMap = new HashMap<>();
            for (Gate g : gateList) {
                gateMap.put(String.valueOf(g.getGid()), g.getGname());
            }


            ExcelDownLoad download = new AgentInfoExcelDownLoad();
            SXSSFWorkbook wb = new SXSSFWorkbook();
            //大数据库导出，百万级
            wb = download.createDownLoadExcel(vrs, exportCols, gateMap, wb);

            ServletOutputStream out = response.getOutputStream();
            response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(filename + ".xlsx", "UTF-8"));
            wb.write(out);
            out.flush();
            out.close();

            //添加日志
            OperateLog.addExLog(operateLogService,mgrService,req, ctoken, userinfo,
                    OperateLog.MODULE_VISITOR, vrs.size(), "导出"+filename+":"+map.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @ApiOperation(value = "/SearchVisitByConditionPage 分页条件搜索签到访客", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647\n" +
                    "    \"date\":\"2021-03-18\",\n" +
                    "    \"endDate\":\"2021-03-18\",\n" +
                    "    \"subaccountId\":0,\n" +
                    "    \"startIndex\":0,\n" +
                    "    \"requestedCount\":10,\n" +
                    "    \"reception\":\"\",\n" +
                    "    \"searchType\":0\n" +
                    "    \"gid\":12,24,32\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/SearchVisitByConditionPage")
    @ResponseBody
    public RespInfoT<Page<RespVisitor>> SearchVisitByConditionPage(
            @ApiParam(value = "RequestVisit 请求访客Bean", required = true) @Validated @RequestBody RequestVisit req,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);

        //只有管理员可以访问
        if (AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())) {
            req.setUserid(authToken.getUserid());
        } else if (AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
        ||AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
        ||AuthToken.ROLE_GATE.equals(authToken.getAccountRole()) ){
            Manager mgr = mgrService.getManagerByAccount(authToken.getLoginAccountId());
            String gids[] = req.getGid().split(",");
            String mgids[] = mgr.getGid().split(",");
            boolean auth = UtilTools.arrayContain(gids, mgids);
            if (auth) {
                return new RespInfoT(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
        } else {
            return new RespInfoT(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        req.setUserid(authToken.getUserid());
        Page<RespVisitor> rvpage = new Page<RespVisitor>(req.getStartIndex() / req.getRequestedCount() + 1, req.getRequestedCount(), 0);
        req.setPage(rvpage);
        List<RespVisitor> rvlist = visitorService.searchVisitByConditionPage(req);
        rvpage.setList(rvlist);
        return new RespInfoT(0, "success", rvpage);


    }

    @ApiOperation(value = "/SearchRVisitorByConditionPage 分页条件搜索供应商", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":\"2147483647\",\n" +
                    "    \"gid\":\"1\",\n" +
                    "    \"date\":\"2021-04-08\",\n" +
                    "    \"endDate\":\"2021-04-08\",\n" +
                    "    \"searchType\":0,\n" +
                    "    \"startIndex\":0,\n" +
                    "    \"requestedCount\":2,\n" +
                    "    \"reception\":\"\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/SearchRVisitorByConditionPage")
    @ResponseBody
    public RespInfoT<Page<RespVisitor>> SearchRVisitorByConditionPage(@ApiParam(value = "RequestVisit 请求访客Bean", required = true) @Validated @RequestBody RequestVisit req,
                                                  HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);

        //只有管理员可以访问
        if (AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())) {
            req.setUserid(authToken.getUserid());
        } else if (AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                ||AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
                ||AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                ||AuthToken.ROLE_HSE.equals(authToken.getAccountRole())){
            Manager mgr = mgrService.getManagerByAccount(authToken.getLoginAccountId());
            String gids[] = req.getGid().split(",");
            String mgids[] = mgr.getGid().split(",");
            boolean auth = UtilTools.arrayContain(gids, mgids);
            if (auth) {
                return new RespInfoT(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
        } else {
            return new RespInfoT(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        req.setUserid(authToken.getUserid());
        Page<RespVisitor> rvpage = new Page<RespVisitor>(req.getStartIndex() / req.getRequestedCount() + 1, req.getRequestedCount(), 0);
        req.setPage(rvpage);
        List<RespVisitor> rvlist = visitorService.SearchRVisitorByConditionPage(req);
        rvpage.setList(rvlist);

        return new RespInfoT(0, "success", rvpage);
    }

    @ApiOperation(value = "/SearchAppointmentByConditionPage 分页条件搜索预约访客", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"date\":\"2021-03-18\",\n" +
                    "    \"endDate\":\"2021-03-18\",\n" +
                    "    \"subaccountId\":0,\n" +
                    "    \"startIndex\":0,\n" +
                    "    \"requestedCount\":10,\n" +
                    "    \"reception\":\"\",\n" +
                    "    \"searchType\":0\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/SearchAppointmentByConditionPage")
    @ResponseBody
    public RespInfoT<Page<RespVisitor>> SearchAppointmentByConditionPage(
            @ApiParam(value = "RequestVisit 请求访客Bean", required = true) @Validated @RequestBody RequestVisit req,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        //权限管理
        if (AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())) {
            req.setUserid(authToken.getUserid());
        } else if (AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                ||AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
                ||AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                ||AuthToken.ROLE_HSE.equals(authToken.getAccountRole())){
            Manager mgr = mgrService.getManagerByAccount(authToken.getLoginAccountId());
            String gids[] = req.getGid().split(",");
            String mgids[] = mgr.getGid().split(",");
            boolean auth = UtilTools.arrayContain(gids, mgids);
            if (auth) {
                return new RespInfoT(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
            }
        }else if (AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())) {
            req.setUserid(authToken.getUserid());
            Employee vEmployee = employeeService.getEmployee(Integer.parseInt(authToken.getLoginAccountId()));
            if (null == vEmployee) {
                return new RespInfoT(ErrorEnum.E_610.getCode(),ErrorEnum.E_610.getMsg());
            }
            if(vEmployee.getEmpPhone()!=null){
                req.setEmpPhone(vEmployee.getEmpPhone());
            }else {
                req.setEmpid(Integer.parseInt(authToken.getLoginAccountId()));
            }
        } else {
            return new RespInfoT(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        req.setUserid(authToken.getUserid());
        Page<RespVisitor> rvpage = new Page<RespVisitor>(req.getStartIndex() / req.getRequestedCount() + 1, req.getRequestedCount(), 0);
        req.setPage(rvpage);
        List<RespVisitor> rvlist = visitorService.searchAppByConditionPage(req);
        for (int i = 0; i < rvlist.size(); i++) {
            rvlist.get(i).setEncryption(AESUtil.encode("v"+rvlist.get(i).getVid(), Constant.AES_KEY));
        }
        rvpage.setList(rvlist);
        return new RespInfoT(0, "success", rvpage);

    }

    @ApiOperation(value = "/searchInviteByConditionPage 分页条件搜索邀请访客", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/searchInviteByConditionPage")
    @ResponseBody
    public RespInfoT<Page<RespVisitor>> searchInviteByConditionPage(
            @ApiParam(value = "RequestVisit 请求访客Bean", required = true) @Validated @RequestBody RequestVisit req,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        //权限管理
        if (AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())) {
            req.setUserid(authToken.getUserid());
        } else if (AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                || AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
                || AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                || AuthToken.ROLE_HSE.equals(authToken.getAccountRole())) {
            Manager mgr = mgrService.getManagerByAccount(authToken.getLoginAccountId());
            if(StringUtils.isNotEmpty(req.getGid())) {
                String gids[] = req.getGid().split(",");
                String mgids[] = mgr.getGid().split(",");
                boolean auth = UtilTools.arrayContain(gids, mgids);
                if (auth) {
                    return new RespInfoT(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
                }
            }else{
                req.setGid(mgr.getGid());
            }
        }else if (AuthToken.ROLE_EMPLOYEE.equals(authToken.getAccountRole())) {
            req.setUserid(authToken.getUserid());
            Employee vEmployee = employeeService.getEmployee(Integer.parseInt(authToken.getLoginAccountId()));
            if (null == vEmployee) {
                return new RespInfoT(ErrorEnum.E_610.getCode(),ErrorEnum.E_610.getMsg());
            }
            if(vEmployee.getEmpPhone()!=null){
                req.setEmpPhone(vEmployee.getEmpPhone());
            }else {
                req.setEmpid(Integer.parseInt(authToken.getLoginAccountId()));
            }
        } else {
            return new RespInfoT(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        req.setUserid(authToken.getUserid());
        Page<RespVisitor> rvpage = new Page<RespVisitor>(req.getStartIndex() / req.getRequestedCount() + 1, req.getRequestedCount(), 0);
        req.setPage(rvpage);
        List<RespVisitor> aplist = appointmentService.searchInviteByConditionPage(req);
        for (int i = 0; i < aplist.size(); i++) {
            aplist.get(i).setEncryption(AESUtil.encode(aplist.get(i).getVid()+"", Constant.AES_KEY));
        }
        rvpage.setList(aplist);

        return new RespInfoT(0, "success", rvpage);

    }


    @ApiOperation(value = "/SearchVisitors 分页条件搜索签到访客,space365定制", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"startDate\":\"2021-03-18\",\n" +
                    "    \"endDate\":\"2021-03-18\",\n" +
                    "    \"subaccountId\":0,\n" +
                    "    \"startIndex\":0,\n" +
                    "    \"requestedCount\":10,\n" +
                    "    \"reception\":\"\",\n" +
                    "    \"searchType\":0\n" +
                    "    \"gid\":12,24,32\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/SearchVisitors")
    @ResponseBody
    public RespInfoT<Page<RespVisitor>> SearchVisitors(
            @ApiParam(value = "RequestVisit 请求访客Bean", required = true) @Validated @RequestBody RequestVisit req,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);

        //只有管理员可以访问
        if (AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())) {

        } else if (AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                ||AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                ||AuthToken.ROLE_GATE.equals(authToken.getAccountRole()) ){
            Manager mgr = mgrService.getManagerByAccount(authToken.getLoginAccountId());
            if(StringUtils.isNotEmpty(req.getGid())) {
                String gids[] = req.getGid().split(",");
                String mgids[] = mgr.getGid().split(",");
                boolean auth = UtilTools.arrayContain(gids, mgids);
                if (auth) {
                    return new RespInfoT(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
                }
            }else{
                req.setGid(mgr.getGid());
            }
        } else {
            return new RespInfoT(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        req.setUserid(authToken.getUserid());
        Department dept = new Department();
        dept.setDeptName(req.getName());
        dept.setUserid(req.getUserid());
        dept.setParentId(0);
        Department depart = departmentService.getDepartmentByDeptName(dept);
        if(depart != null){
            req.setEmpdeptid(depart.getDeptid()+"");
        }
        if(req.getStartIndex() != -1){
            Page<RespVisitor> rvpage = new Page<RespVisitor>(req.getStartIndex() / req.getRequestedCount() + 1, req.getRequestedCount(), 0);
            req.setPage(rvpage);
            List<RespVisitor> rvlist = visitorService.searchVisitors(req);
            //获取员工部门名称
            List<Department> departments = departmentService.getAllDepartmentList(req.getUserid());
            Map<Integer, String> deptMap = new HashMap<>();
            for (int i = 0; i < departments.size(); i++) {
                Department dp = departments.get(i);
                deptMap.put(dp.getDeptid(), dp.getDeptName());
            }
            rvlist = rvlist.stream().map(respVisitor -> {
                if (StringUtils.isNotBlank(deptMap.get(respVisitor.getEmpdeptid()))) {
                    respVisitor.setEmpDeptName(deptMap.get(respVisitor.getEmpdeptid()));
                }
                return respVisitor;
            }).collect(Collectors.toList());
            rvpage.setList(rvlist);
            return new RespInfoT(0, "success", rvpage);
        }else{
            List<RespVisitor> rvlist = visitorService.searchVisitors(req);
            //获取员工部门名称
            List<Department> departments = departmentService.getAllDepartmentList(req.getUserid());
            Map<Integer, String> deptMap = new HashMap<>();
            for (int i = 0; i < departments.size(); i++) {
                Department dp = departments.get(i);
                deptMap.put(dp.getDeptid(), dp.getDeptName());
            }
            rvlist = rvlist.stream().map(respVisitor -> {
                if (StringUtils.isNotBlank(deptMap.get(respVisitor.getEmpdeptid()))) {
                    respVisitor.setEmpDeptName(deptMap.get(respVisitor.getEmpdeptid()));
                }
                return respVisitor;
            }).collect(Collectors.toList());
            return new RespInfoT(0, "success", rvlist);
        }
    }


}
