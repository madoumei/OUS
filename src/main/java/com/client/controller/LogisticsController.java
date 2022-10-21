package com.client.controller;

import com.client.bean.Logistics;
import com.client.bean.ReqLogistics;
import com.client.service.LogisticsService;
import com.config.exception.ErrorEnum;
import com.config.qicool.common.persistence.Page;
import com.utils.UtilTools;
import com.web.bean.*;
import com.web.service.*;
import com.web.service.impl.AgentInfoExcelDownLoad;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author qcvisit
 * @date 2021-03-03 09:34
 */
@Controller
@RequestMapping(value = "/*")
@Api(value = "LogisticsController", tags = "API_物流管理", hidden = true)
public class LogisticsController {

    @Autowired
    private LogisticsService logisticsService;

    @Autowired
    private PersonInfoService personInfoService;

    @Autowired
    ManagerService mgrService;

    @Autowired
    private TokenServer tokenServer;

    @Autowired
    private UserService userService;

    @Autowired
    private VisitorTypeService visitorTypeService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private OperateLogService operateLogService;


    @ApiOperation(value = "物流管理_添加物流记录 /addLogisticsInfo", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":1221692693,\n" +
                    "    \"sname\":\"测试\",\n" +
                    "    \"smobile\":\"1322233221\",\n" +
                    "    \"scardid\":\"321222321312312\",\n" +
                    "    \"dname\":\"测试1\",\n" +
                    "    \"dmobile\":\"13222332213\",\n" +
                    "    \"dcardid\":\"3212223213123121\",\n" +
                    "    \"driverExtend\":\"xxxxxxxxxxx\",\n" +
                    "    \"logExtend\":\"xxxxxxxx\",\n" +
                    "    \"plateNum\":\"xxxxx\",\n" +
                    "    \"vehicleExtend\":\"xxxxxxxxxx\",\n" +
                    "    \"goodsExtend\":\"xxxxxxxxxx\",\n" +
                    "    \"photoInfo\":\"xxxxxxxxx\",\n" +
                    "    \"logType\":\"提货\",\n" +
                    "    \"appointmentDate\":13243243242342,\n" +
                    "    \"memberInfo\":\"xxxxxxxxx\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/addLogisticsInfo")
    @ResponseBody
    public RespInfo addLogisticsInfo(@ApiParam(value = "Logistics 物流记录Bean", required = true) @Validated @RequestBody Logistics lo,
                                     HttpServletRequest request, BindingResult result) {

        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        lo.setLogNum(String.valueOf(System.currentTimeMillis()));
        logisticsService.addLogisticsInfo(lo);
        List<Integer> stList = new ArrayList<Integer>();
        stList.add(3);

        Manager mgr = new Manager();
        mgr.setStList(stList);
        mgr.setUserid(lo.getUserid());
        List<Manager> mgrList = mgrService.getManagerList(mgr);
        UserInfo userinfo = userService.getUserInfo(lo.getUserid());
        if (mgrList.size() > 0 && lo.getPstatus() == 0) {
            for (int i = 0; i < mgrList.size(); i++) {
                logisticsService.sendLogisticsSms(lo, userinfo, mgrList.get(i));
            }
        } else {
            VisitorType visType = new VisitorType();
            visType.setUserid(lo.getUserid());
            //visType.setvType(lo.getLogType());
            visType.setCategory(1);

            //通过tid查询用户访客类型，以及有效期周期
            List<VisitorType> vtList = visitorTypeService.getVisitorType(visType);
            if (lo.getPstatus() == 1) {
                if (vtList.size() > 0 && StringUtils.isNotBlank(vtList.get(0).getQid())) {
                    logisticsService.sendLogisticsSms(lo, userinfo, null);
                }
            }
        }

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "物流管理_更新物流数据 /updateLogisticsInfo", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateLogisticsInfo")
    @ResponseBody
    public RespInfo updateLogisticsInfo(@RequestBody Logistics lo, HttpServletRequest request) {
        Logistics lgs = logisticsService.getLogisticsById(lo);
        logisticsService.updateLogisticsInfo(lo);
        if (lo.getPstatus() != 0 && lgs.getPstatus() != lo.getPstatus()) {
            UserInfo userinfo = userService.getUserInfo(lo.getUserid());
            lo.setDmobile(lgs.getDmobile());
            //lo.setSmobile(lgs.getSmobile());
            lo.setAppointmentDate(lgs.getAppointmentDate());

            VisitorType visType = new VisitorType();
            visType.setUserid(lgs.getUserid());
            //visType.setvType(lgs.getLogType());
            visType.setCategory(1);

            //通过tid查询用户访客类型，以及有效期周期
            List<VisitorType> vtList = visitorTypeService.getVisitorType(visType);
            if (lo.getPstatus() == 1) {
                if (vtList.size() > 0 && StringUtils.isNotBlank(vtList.get(0).getQid())) {
                    logisticsService.sendLogisticsSms(lo, userinfo, null);
                }
            } else {
                logisticsService.sendLogisticsSms(lo, userinfo, null);
            }
        }

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/getLogisticsInfo 获取物流数据", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"psList\":[\n" +
                    "        3,\n" +
                    "        2,\n" +
                    "        1,\n" +
                    "        0\n" +
                    "    ],\n" +
                    "    \"startIndex\":1,\n" +
                    "    \"requestedCount\":10,\n" +
                    "    \"startDate\":\"2021-03-19\",\n" +
                    "    \"endDate\":\"2021-03-19\",\n" +
                    "    \"sname\":\"\",\n" +
                    "    \"logType\":\"\",\n" +
                    "    \"cardid\":\"\",\n" +
                    "    \"mobile\":\"\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getLogisticsInfo")
    @ResponseBody
    public RespInfo getLogisticsInfo(
            @ApiParam(value = "ReqLogistics 请求物流Bean", required = true) @RequestBody @Validated ReqLogistics rl,
            HttpServletRequest request, BindingResult result) {
//        int userid = rl.getUserid();
//        String token = request.getHeader("X-COOLVISIT-TOKEN");
//        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
//        if (authToken.getUserid() != rl.getUserid()) {
//            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
//        }


//        if (token.indexOf("wx-") != -1 || token.indexOf("dingding-") != -1) {
//            Person p = null;
//            if (StringUtils.isEmpty(rl.getMobile())) {
//                List<Person> plist = personInfoService.getVisitPersonByCardid(rl.getCardid());
//                if (plist.size() > 0) {
//                    p = plist.get(0);
//                } else {
//                    p = personInfoService.getInvitePersonByOpenid(rl.getOpenid());
//                }
//            } else {
//                p = personInfoService.getVisitPersonByPhone(rl.getMobile());
//            }
//            token = token.substring(token.indexOf("-") + 1);
//            if (null == p || !p.getPopenid().equals(token)) {
//                return new RespInfo(1, "invalid user");
//            }
//        } else {
//            boolean v = UtilTools.validateUser(request.getHeader("X-COOLVISIT-TOKEN"), String.valueOf(userid));
//            if (!v) {
//                String account = token.substring(0, token.indexOf("-"));
//                Manager mgr = mgrService.getManagerByAccount(account);
//                if (null == mgr || mgr.getUserid() != userid) {
//                    return new RespInfo(1, "invalid user");
//                }
//            }
//        }

        Page<Logistics> npage = new Page<Logistics>(rl.getStartIndex() / rl.getRequestedCount() + 1, rl.getRequestedCount(), 0);
        rl.setPage(npage);
        List<Logistics> ln = logisticsService.getLogisticsInfo(rl);
        npage.setList(ln);
        return new RespInfo(0, "success", npage);
    }

    //
//	@RequestMapping(method = RequestMethod.POST, value = "/checkLogistics")
//	@ResponseBody
//	public RespInfo checkLogistics(@RequestBody ReqLogistics rl, HttpServletRequest request)  {
//		    List<Logistics> ln=logisticsService.getLogisticsInfo(rl);
//		    if(ln.size()>0){
//		    	return new RespInfo(0,"success",true);
//		    }
//			return new RespInfo(0,"success",false);
//		}
//
    @ApiOperation(value = "物流管理_导出物流记录 /ExportLogisticsList", httpMethod = "GET")
    @RequestMapping(method = RequestMethod.GET, value = "/ExportLogisticsList")
    public void ExportLogisticsList(HttpServletRequest req, HttpServletResponse response) {
        String ctoken = req.getParameter("token");
        
        if(StringUtils.isNotBlank(ctoken)) {
        	ctoken=UtilTools.getTokenByPcode(redisTemplate, ctoken + "_pcode", "ValidationPcode");
        }
        
        if(StringUtils.isBlank(ctoken)){
        	System.out.println("token is null");
        	return;
        }

        int userid = 0;
        String account = "";
        try {
            if (StringUtils.isNotBlank(req.getParameter("userid"))) {
                userid = Integer.parseInt(req.getParameter("userid"));
//				boolean v=UtilTools.validateUser(ctoken, String.valueOf(userid));
//				if(!v){
//					  response.getWriter().write("{\"status\":27,\"reason\":\"invalid token\"}");
//				}
            } else {
                account = req.getParameter("account");
//				boolean v=UtilTools.validateUser(ctoken, account);
//				if(!v){
//					  response.getWriter().write("{\"status\":27,\"reason\":\"invalid token\"}");
//				}
                Manager mgr = mgrService.getManagerByAccount(account);
                userid = mgr.getUserid();
            }


            HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
            if (hashOperations.hasKey(ctoken, "id")) {
                Long expireDate = (long) hashOperations.get(ctoken, "id");
                Date currentDate = new Date();
                if (expireDate < currentDate.getTime()) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setHeader("Cache-Control", "no-cache");
                    try {
                        response.getWriter().write("{\"status\":29,\"reason\":\"token expired\"}");
                    } catch (IOException e) {
                        //throw new ApplicationException("IOException in populateWithJSON", e);
                    }
                }
            } else {
                response.setContentType("application/json;charset=UTF-8");
                response.setHeader("Cache-Control", "no-cache");
                try {
                    response.getWriter().write("{\"status\":27,\"reason\":\"invalid token\"}");
                } catch (IOException e) {
                    //throw new ApplicationException("IOException in populateWithJSON", e);
                }
            }
            String startDate = req.getParameter("startDate");
            String endDate = req.getParameter("endDate");
            String sname = req.getParameter("sname");
            String logType = req.getParameter("logType");
            String cardid = req.getParameter("cardid");
            String mobile = req.getParameter("mobile");
            String pstatus = req.getParameter("pstatus");

            //TODO 检查权限

            ReqLogistics rl = new ReqLogistics();
            rl.setUserid(userid);
            rl.setSname(sname);
            rl.setLogType(logType);
            rl.setMobile(mobile);
            rl.setCardid(cardid);
            rl.setStartDate(startDate);
            rl.setEndDate(endDate);
            List<Integer> psList = new ArrayList<Integer>();
            for (int i = 0; i < pstatus.length(); i++) {
                psList.add(Integer.parseInt(pstatus.substring(i, i + 1)));
            }
            rl.setPsList(psList);

            List<Logistics> logList = logisticsService.getLogisticsInfo(rl);
            UserInfo userinfo = userService.getBaseUserInfo(userid);
            String filename = userinfo.getCompany() + "物流记录_" + startDate + "-" + endDate;
            ExcelModel excel = new ExcelModel();
            excel.setSheetName(filename);
            ExcelDownLoad download = new AgentInfoExcelDownLoad();
            ExcelModel em = download.createDownLoadExcel(logList, excel);
            download.downLoad(filename + ".xls", em, req, response);

            //添加日志
            OperateLog.addExLog(operateLogService,mgrService,req, ctoken, userinfo,
                    OperateLog.MODULE_VISITOR, logList.size(), "导出"+filename+":"+rl.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
