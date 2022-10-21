package com.web.controller;

import com.config.exception.ErrorEnum;
import com.config.qicool.common.persistence.Page;
import com.utils.UtilTools;
import com.web.bean.*;
import com.web.service.*;
import com.web.service.impl.AgentInfoExcelDownLoad;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * @author qcvisit
 * @date 2021-03-03 09:34
 */
@Controller
@RequestMapping(value = "/*")
@Api(value = "OptController", tags = "API_操作管理", hidden = true)
public class OptController {

    @Autowired
    private OperateLogService operateLogService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private TokenServer tokenServer;


    @ApiOperation(value = "/getLogList 获取操作日志", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"requestedCount\":10,\n" +
                    "    \"startIndex\":1,\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"startDate\":\"2021-03-19\",\n" +
                    "    \"endDate\":\"2021-03-19\",\n" +
                    "    \"optName\":\"\",\n" +
                    "    \"optModule\":\"\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getLogList")
    @ResponseBody
    public RespInfo getLogList(@ApiParam(value = "ReqOperateLog 日志请求类Bean", required = true) @Validated @RequestBody  ReqOperateLog rol,
                               HttpServletRequest request, BindingResult result) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))
                || authToken.getUserid() != rol.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }


        Page<OperateLog> rpage = new Page<OperateLog>(rol.getStartIndex() / rol.getRequestedCount() + 1, rol.getRequestedCount(), 0);
        rol.setPage(rpage);
        List<OperateLog> olist = operateLogService.getLogList(rol);
        rpage.setList(olist);

        return new RespInfo(0, "success", rpage);
    }

    @ApiOperation(value = "/ExportLogList 导出操作日志", httpMethod = "GET")
    @RequestMapping(method = RequestMethod.GET, value = "/ExportLogList")
    public void ExportLogList(HttpServletRequest req, HttpServletResponse response) {
        String ctoken = req.getParameter("token");
        
        if(StringUtils.isNotBlank(ctoken)) {
        	ctoken=UtilTools.getTokenByPcode(redisTemplate, ctoken + "_pcode", "ValidationPcode");
        }
        
        if(StringUtils.isBlank(ctoken)){
        	System.out.println("token is null");
        	return;
        }
        
        int userid = 0;
        try {
            userid = Integer.parseInt(req.getParameter("userid"));
            String gid = "-1";//如果设为-1，子管理员不检查gid

            ReqOperateLog rol = new ReqOperateLog();
            rol.setUserid(userid);
            rol.setStartDate(req.getParameter("startDate"));
            rol.setEndDate(req.getParameter("endDate"));
            rol.setOptName(req.getParameter("optName"));
            rol.setOptModule(req.getParameter("optModule"));

            //检查权限
            if(tokenServer.checkUserAuthorityForExport(managerService,redisTemplate,response, ctoken, userid, gid)) return;

            List<OperateLog> olist = operateLogService.getLogList(rol);

            UserInfo userinfo = userService.getBaseUserInfo(userid);
            String filename = userinfo.getCompany() + "操作日志记录_" + req.getParameter("startDate") + "-" + req.getParameter("endDate");

            HSSFWorkbook wb = new HSSFWorkbook();
            ExcelDownLoad download = new AgentInfoExcelDownLoad();

            wb = download.createDownLoadLogExcel(olist, wb);

            ServletOutputStream out = response.getOutputStream();
            response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(filename + ".xls", "UTF-8"));
            wb.write(out);
            out.flush();
            out.close();

            //添加日志
            OperateLog.addExLog(operateLogService,managerService,req, ctoken, userinfo,
                    OperateLog.MODULE_OTHER, olist.size(), "导出"+filename+":"+rol.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
