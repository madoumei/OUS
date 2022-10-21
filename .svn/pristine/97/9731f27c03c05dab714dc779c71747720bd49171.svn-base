package com.web.controller;

import com.client.bean.EquipmentGroup;
import com.client.bean.RequestVisit;
import com.client.bean.Visitor;
import com.client.service.EquipmentGroupService;
import com.client.service.FileUploadOrDownLoadServer;
import com.client.service.Impl.FileUploadOrDownLoadServerImpl;
import com.client.service.VisitorService;
import com.config.activemq.MessageSender;
import com.config.exception.ErrorEnum;
import com.config.exception.ErrorException;
import com.config.qicool.common.persistence.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.utils.FileUtils.DelFileThread;
import com.utils.FileUtils.MinioTools;
import com.utils.Constant;
import com.utils.UtilTools;
import com.utils.jsonUtils.JacksonJsonUtil;
import com.web.bean.*;
import com.web.service.*;
import com.web.service.impl.AgentInfoExcelDownLoad;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author qcvisit
 * @date 2021-03-03 09:34
 */
@Controller
@RequestMapping(value = "/*")
@Api(value = "ResidentVisitorController", tags = "API_供应商管理", hidden = true)
public class ResidentVisitorController {

    @Autowired
    private ResidentVisitorService residentVisitorService;

    @Autowired
    private TokenServer tokenServer;

    @Autowired
    private FileUploadOrDownLoadServer fileUploadOrDownLoadServer;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private EquipmentGroupService equipmentGroupService;


    @Autowired
    private UserService userService;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private OperateLogService operateLogService;

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private PersonInfoService personInfoService;
    
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ManagerService mgrService;

    @Autowired
    private FastFileStorageClient storageClient;

    @ApiOperation(value = "/getProject 获取供用商项目", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"account\":\"\"\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getProject")
    @ResponseBody
    public RespInfo getProject(
            @ApiParam(value = "ResidentProject 常驻供应商项目Bean", required = true) @Validated @RequestBody ResidentProject rp,
            HttpServletRequest request, BindingResult result) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUPP_COMPANY.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != rp.getUserid() ) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        if(AuthToken.ROLE_SUPP_COMPANY.equals(authToken.getAccountRole())){
            rp.setAccount(authToken.getLoginAccountId());
        }
        List<ResidentProject> rplist = residentVisitorService.getProject(rp);
        return new RespInfo(0, "success", rplist);
    }


    @ApiOperation(value = "/getAllResidentCompany 获取所有供用商公司", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getAllResidentCompany")
    @ResponseBody
    public RespInfo getAllResidentCompany(
            @ApiParam(value = "ResidentVisitor 常驻供应商项目Bean", required = true) @Validated @RequestBody ResidentVisitor rv,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUPP_COMPANY.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != rv.getUserid() ) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        List<ResidentVisitor> rplist = residentVisitorService.getAllResidentCompany(rv);
        return new RespInfo(0, "success", rplist);
    }

    /**
     * 常驻访客进出统计
     *
     * @param rvr
     * @return
     */
    @ApiOperation(value = "/getRvReport 获取供应商进出统计", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getRvReport")
    @ResponseBody
    public RespInfo getRvReport(
            @ApiParam(value = "RvReport 常驻供应商清单Bean", required = true) @Validated @RequestBody RvReport rvr,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != rvr.getUserid() ) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        Page<RvReport> rvpage = new Page<RvReport>(rvr.getStartIndex() / rvr.getRequestedCount() + 1, rvr.getRequestedCount(), 0);
        rvr.setPage(rvpage);
        List<RvReport> rvlist = residentVisitorService.getRvReport(rvr);
        rvpage.setList(rvlist);
        return new RespInfo(0, "success", rvpage);
    }

    /**
     * 常驻访客进出记录
     * @param rvr
     * @return
     */
    @ApiOperation(value = "/getRvOpenInfo 获取常驻供应商进出记录", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"requestedCount\":10,\n" +
                    "    \"startIndex\":1,\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"startDate\":\"2021-03-19\",\n" +
                    "    \"endDate\":\"2021-03-19\",\n" +
                    "    \"company\":\"\",\n" +
                    "    \"pName\":\"\",\n" +
                    "    \"name\":\"\"\n" +
                    "}"
    )
    @RequestMapping(method=RequestMethod.POST, value = "/getRvOpenInfo")
    @ResponseBody
    public RespInfo getRvOpenInfo(@ApiParam(value = "RvReport 常驻供应商清单Bean", required = true) @Validated  @RequestBody  RvReport rvr,HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_GATE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != rvr.getUserid() ) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        Page<RvReport> rvpage = new Page<RvReport>(rvr.getStartIndex() / rvr.getRequestedCount() + 1, rvr.getRequestedCount(), 0);
        rvr.setPage(rvpage);
        List<RvReport> rvlist = residentVisitorService.getRvOpenInfo(rvr);
        rvpage.setList(rvlist);
        return new RespInfo(0, "success",rvpage);
    }

    @ApiOperation(value = "/getResidentVisitor 查询常驻供应商", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"pid\":0,\n" +
                    "    \"requestedCount\":10,\n" +
                    "    \"startIndex\":1,\n" +
                    "    \"account\":\"\",\n" +
                    "    \"userid\":2147483647\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getResidentVisitor")
    @ResponseBody
    public RespInfo getResidentVisitor(
            @ApiParam(value = "ResidentVisitor 常住访客Bean", required = true) @Validated   @RequestBody ResidentVisitor rv,
            HttpServletRequest request, BindingResult result) {

        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUPP_COMPANY.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != rv.getUserid() ) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        if(AuthToken.ROLE_SUPP_COMPANY.equals(authToken.getAccountRole())){
            Manager mgr = mgrService.getManagerByAccount(authToken.getLoginAccountId());
            if(mgr == null){
                return new RespInfo(0, "success");
            }
            rv.setCompany(mgr.getCompany());
        }

        Page<ResidentVisitor> rpage = new Page<ResidentVisitor>(rv.getStartIndex() / rv.getRequestedCount() + 1, rv.getRequestedCount(), 0);
        rv.setPage(rpage);
        if(rv.getPid()!=0) {
            //获取项目名称，通过项目名称获取搜索项目
            ResidentProject searchPro = new ResidentProject();
            searchPro.setUserid(rv.getUserid());
            searchPro.setPid(rv.getPid());
            List<ResidentProject> projects = residentVisitorService.getProject(searchPro);
            if(projects.size()==1){
                rv.setpName(projects.get(0).getpName());
            }
        }
        List<ResidentVisitor> rplist = residentVisitorService.getResidentVisitor(rv);

        for (ResidentVisitor residentVisitor : rplist) {
//            Map<String, List<String>> fileMap = fileUploadOrDownLoadServer.showAllFilePathByEmpId(residentVisitor, request);
//            if (null != fileMap) {
//                Set<String> keys = fileMap.keySet();
//                for (String key : keys) {
//                    List<String> value = fileMap.get(key);
//                    String[] filenames = value.toArray(new String[value.size()]);
//                    residentVisitor.setFileNames(filenames);
//                }
//            }
        	
            //minio
            List<String> filenames =MinioTools.getFileList(Constant.BUCKET_NAME, "/residentVisitor/"+residentVisitor.getRid().substring(1));
            //minio
            residentVisitor.setFileNames(filenames.toArray(new String[]{}));
        }
  
        rpage.setList(rplist);

        return new RespInfo(0, "success", rpage);
    }

    @ApiOperation(value = "/updateResidentVisitor 修改常驻供应商", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"rid\":\"C0301160724100\",\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"pid\":140,\n" +
                    "    \"company\":\"123456\",\n" +
                    "    \"name\":\"贺薪屹\",\n" +
                    "    \"age\":\"\",\n" +
                    "    \"sex\":0,\n" +
                    "    \"leader\":\"12313213\",\n" +
                    "    \"phone\":\"13915951676\",\n" +
                    "    \"area\":\"\",\n" +
                    "    \"startDate\":\"2020-01-01\",\n" +
                    "    \"endDate\":\"2020-01-01\",\n" +
                    "    \"remark\":\"\",\n" +
                    "    \"avatar\":\"\",\n" +
                    "    \"job\":\"\",\n" +
                    "    \"cardid\":\"320981199401234495\",\n" +
                    "    \"department\":\"\",\n" +
                    "    \"egids\":\"1390\",\n" +
                    "    \"account\":null,\n" +
                    "    \"plateNum\":null\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/updateResidentVisitor")
    @ResponseBody
    public RespInfo updateResidentVisitor(
            @ApiParam(value = "ResidentVisitor 常住访客Bean", required = true) @Validated   @RequestBody ResidentVisitor rv,
            HttpServletRequest request, BindingResult result) {

        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUPP_COMPANY.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != rv.getUserid() ) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        if (checkComProRel(rv, authToken)) return new RespInfo(ErrorEnum.E_301.getCode(), ErrorEnum.E_301.getMsg());
        if(rv.getPid()!=0){
            ResidentProject searchParam = new ResidentProject();
            searchParam.setPid(rv.getPid());
            searchParam.setUserid(rv.getUserid());
            List<ResidentProject> projects = residentVisitorService.getProject(searchParam);
            if(projects.size()==1){
                rv.setpName(projects.get(0).getpName());
            }
        }
        rv.setRid(rv.getRid().substring(1));
        residentVisitorService.updateResidentVisitor(rv);

        //修改授权状态，需要重新授权
        rv.setUserid(authToken.getUserid());
        rv.setRstatus(0);
        residentVisitorService.updateRvPermission(rv);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("key", "resident_update");
        map.put("company_id", rv.getUserid());
        map.put("resident_id", "C"+rv.getRid());
        messageSender.updateFaceLib(map);

        //添加日志
        Manager manager = null;
        UserInfo userInfo = userService.getUserInfo(rv.getUserid());
        if (StringUtils.isNotEmpty(rv.getLoginAccount())) {
            String optName = "";
            String optId = "";
            String optRole = "";
            manager = managerService.getManagerByAccount(rv.getLoginAccount());
            if (null != manager) {
                optId = manager.getAccount();
                optName = manager.getSname();
                optRole = String.valueOf(manager.getsType());
            } else {
                if (null != userInfo) {
                    optId = userInfo.getEmail();
                    optName = userInfo.getUsername();
                    optRole = "0";
                }
            }
            OperateLog log = new OperateLog();
            String ipAddr = request.getHeader("X-Forwarded-For");
            log.setUserid(rv.getUserid());
            log.setOptId(optId);
            log.setOptName(optName);
            log.setOptRole(optRole);
            log.setIpAddr(ipAddr);
            log.setObjId(rv.getRid().substring(1));
            log.setObjName(rv.getName());
            log.setoTime(new Date());
            log.setOptEvent("修改");
            log.setObjName("");
            log.setOptClient("0");
            log.setOptModule("3");
            log.setOptDesc("成功,修改供应商: " + rv.getName());
            operateLogService.addLog(log);

        }

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/delResidentVisitor 删除常驻供应商", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"userid\":2147483647,\n" +
                    "    \"rids\":[\n" +
                    "        \"C1120172357008\"\n" +
                    "    ]\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/delResidentVisitor")
    @ResponseBody
    public RespInfo delResidentVisitor(@ApiParam(value = "ResidentVisitor 常住访客Bean", required = true) @Validated   @RequestBody ResidentVisitor rv,
                                       HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUPP_COMPANY.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != rv.getUserid() ) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        ResidentVisitor rvr = new ResidentVisitor();
        StringBuffer stringBuffer = new StringBuffer();
        List<String> attachmentList=new ArrayList<>();
        List<String> residentVisitors = new ArrayList<>();
        
        for (int i = 0; i < rv.getRids().size(); i++) {
            rv.getRids().set(i, rv.getRids().get(i).substring(1));

            rvr.setUserid(rv.getUserid());
            rvr.setRid(rv.getRids().get(i));
            rvr = residentVisitorService.getResidentVisitorByRid(rvr);
            stringBuffer.append(rvr.getName());
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("key", "resident_delete");
            map.put("company_id", rvr.getUserid());
            map.put("resident_id", rvr.getRid());
            messageSender.updateFaceLib(map);

            // TODO: 2020/4/24 删除供应商图像文件
            if (StringUtils.isNotBlank(rvr.getAvatar())) {
                residentVisitors.add(rvr.getAvatar());
            }
            List<String> filenames =MinioTools.getObjectList(Constant.BUCKET_NAME, "/residentVisitor/"+rvr.getName() + "_" + rvr.getRid().substring(1));
            attachmentList.addAll(filenames);
        }
        if(residentVisitors.size()>0) {
            new Thread(new DelFileThread(residentVisitors, personInfoService)).start();
        }
        if(attachmentList.size()>0) {
        	new Thread(new DelFileThread(residentVisitors,attachmentList, personInfoService)).start();
        }
        
        // TODO: 2020/4/10 添加删除常驻供应商记录
        if (StringUtils.isNotEmpty(rv.getLoginAccount())) {
            Manager manager = null;
            String optId = "";
            String optName = "";
            String optRole = "";
            manager = managerService.getManagerByAccount(rv.getLoginAccount());
            if (null != manager) {
                optId = manager.getAccount();
                optName = manager.getSname();
                optRole = String.valueOf(manager.getsType());
            } else {
                UserInfo userInfo = userService.getUserInfo(rv.getUserid());
                optId = userInfo.getEmail();
                optName = userInfo.getUsername();
                optRole = "0";
            }
            OperateLog log = new OperateLog();
            String ipAddr = request.getHeader("X-Forwarded-For");
            log.setUserid(rv.getUserid());
            log.setOptId(optId);
            log.setOptName(optName);
            log.setOptRole(optRole);
            log.setIpAddr(ipAddr);
            log.setObjId("");
            log.setObjName(rvr.getName());
            log.setoTime(new Date());
            log.setOptEvent("删除");
            log.setObjName(rvr.getpName());
            log.setOptClient("0");
            log.setOptModule("3");
            log.setOptDesc("成功,删除供应商: " + stringBuffer.toString());
            operateLogService.addLog(log);
        }
        residentVisitorService.delResidentVisitor(rv);
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/getResidentVisitorByCompany 根据公司名称获取常驻访客", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"company\":\"\",\n" +
                    "    \"requestedCount\":10,\n" +
                    "    \"startIndex\":1,\n" +
                    "    \"userid\":2147483647\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getResidentVisitorByCompany")
    @ResponseBody
    public RespInfo getResidentVisitorByCompany(
            @ApiParam(value = "ResidentVisitor 常住访客Bean", required = true) @Validated   @RequestBody ResidentVisitor rv,
            HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != rv.getUserid() ) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }


        Page<ResidentVisitor> rpage = new Page<ResidentVisitor>(rv.getStartIndex() / rv.getRequestedCount() + 1, rv.getRequestedCount(), 0);
        rv.setPage(rpage);
        List<ResidentVisitor> rplist = residentVisitorService.getResidentVisitorByCompany(rv);
        rpage.setList(rplist);

        return new RespInfo(0, "success", rpage);
    }

    @ApiOperation(value = "/getResidentVisitorByName 根据姓名获取常驻访客", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"name\":\"1\",\n" +
                    "    \"requestedCount\":10,\n" +
                    "    \"startIndex\":1,\n" +
                    "    \"userid\":2147483647\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/getResidentVisitorByName")
    @ResponseBody
    public RespInfo getResidentVisitorByName(@ApiParam(value = "ResidentVisitor 常住访客Bean", required = true) @Validated   @RequestBody ResidentVisitor rv,
                                             HttpServletRequest request, BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        if (!allErrors.isEmpty()) {
            throw new HttpMessageNotReadableException(allErrors.toString());
        }

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUPP_COMPANY.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                ||authToken.getUserid() != rv.getUserid() ) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        if(AuthToken.ROLE_SUPP_COMPANY.equals(authToken.getAccountRole())){
            Manager mgr = mgrService.getManagerByAccount(authToken.getLoginAccountId());
            if(mgr == null){
                return new RespInfo(0, "success");
            }
            rv.setCompany(mgr.getCompany());
        }

        Page<ResidentVisitor> rpage = new Page<ResidentVisitor>(rv.getStartIndex() / rv.getRequestedCount() + 1, rv.getRequestedCount(), 0);
        rv.setPage(rpage);
        List<ResidentVisitor> rplist = residentVisitorService.getResidentVisitorByName(rv);
        rpage.setList(rplist);

        return new RespInfo(0, "success", rpage);
    }

    @ApiOperation(value = "/ResidentVisitorSignIn 常驻访客签到", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "    \"cardId\":\"320222198202263694\",\n" +
                    "    \"extendCol\":[\n" +
                    "        \"access=1390,1266\"\n" +
                    "    ],\n" +
                    "    \"gid\":\"1,2,3\",\n" +
                    "    \"name\":\"冯明\",\n" +
                    "    \"peopleCount\":1,\n" +
                    "    \"phone\":\"13813946371\",\n" +
                    "    \"photoUrl\":\"http:\\/\\/master.coolvisit.top\\/group1\\/M00\\/00\\/01\\/rBEABGCLwO6AV-LQAALhDasm9mY966.jpg\",\n" +
                    "    \"signInGate\":\"1号楼\",\n" +
                    "    \"signInOpName\":\"1\",\n" +
                    "    \"vcompany\":\"来访客\",\n" +
                    "    \"visitType\":\"供应商\",\n" +
                    "    \"userid\":2147483647\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/ResidentVisitorSignIn")
    @ResponseBody
    public RespInfo ResidentVisitorSignIn(@RequestBody RequestVisit req, HttpServletRequest request) throws JsonProcessingException {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_MACHINE.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_GATE.equals(authToken.getAccountRole()))
                ||req.getUserid()!=authToken.getUserid()
        ){
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        String name = req.getName();
        String visitType = req.getVisitType();

        List<String> extcol = req.getExtendCol();
        String strext = "";
        if (null != extcol && extcol.size() > 0) {
            Map<String, String> map = new HashMap<String, String>();
            for (int a = 0; a < extcol.size(); a++) {
                String[] col = extcol.get(a).split("=");
                map.put(col[0], col[1]);
            }
            ObjectMapper mapper = JacksonJsonUtil.getMapperInstance(false);
            strext = mapper.writeValueAsString(map);
        }
        String cardid = req.getCardId();
        String photoUrl = req.getPhotoUrl();
        int peopleCount = req.getPeopleCount();

        UserInfo ui = userService.getBaseUserInfo(req.getUserid());
        ResidentVisitor srv = new ResidentVisitor();
        srv.setUserid(ui.getUserid());
        ResidentVisitor rv = null;
        if(StringUtils.isNotEmpty(cardid)){
            srv.setCardid(cardid);
            rv = residentVisitorService.getResidentVisitorByCardId(srv);
        }
        if(StringUtils.isNotEmpty(req.getPhone())){
            srv.setPhone(req.getPhone());
            rv = residentVisitorService.getResidentVisitorByPhone(srv);
        }

        if(rv == null){
            return new RespInfo(ErrorEnum.E_057.getCode(), ErrorEnum.E_057.getMsg());
        }

        try {
            residentVisitorService.checkResidentVisitorTask(rv.getRid());
        }catch (ErrorException e){
            ErrorException errorException = (ErrorException)e.getCause();
            return new RespInfo(errorException.getErrorEnum().getCode(), errorException.getErrorEnum().getMsg());
        }

        Visitor vt = new Visitor();
        vt.setVname(name);
        vt.setVisitType(visitType);
        vt.setExtendCol(strext);
        vt.setCardId(cardid);
        vt.setVphoto(photoUrl);
        vt.setPeopleCount(peopleCount);
        vt.setSigninType(3);
        vt.setVisitdate(new Date());
        vt.setUserid(req.getUserid());
        vt.setCompany(ui.getCompany());
        vt.setSignInOpName(req.getSignInOpName());
        vt.setSignInGate(req.getSignInGate());
        vt.setVcompany(rv.getCompany());
        vt.setGid(req.getGid());
        if (StringUtils.isEmpty(req.getvType())){
            vt.setvType("供应商#supplier");
        }else {
            vt.setvType(req.getvType());
        }
        visitorService.addVisitor(vt);

        return new RespInfo(0, "success",vt);
    }
    
    /**
     * 下载指定的文件
     *
     * @return
     */
    @RequestMapping(value = "/downloadResidentFile", method = RequestMethod.GET)
    public void dawnloadFile(HttpServletRequest request, HttpServletResponse response,
                             @RequestParam("rid") String rid,
                             @RequestParam("fileNames") String fileNames,
                             @RequestParam("userid") String userid) {
        //token 验证
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        String ctoken = request.getParameter("token");

        boolean isValid = true;
        if (null == ctoken || "".equals(ctoken)) {
            response.setContentType("application/json;charset=UTF-8");
            response.setHeader("Cache-Control", "no-cache");
            try {
                response.getWriter().write("{\"status\":27,\"reason\":\"invalid token\"}");
            } catch (IOException e) {
                //throw new ApplicationException("IOException in populateWithJSON", e);
            }
        } else if (hashOperations.hasKey(ctoken, "id")) {
            Long expireDate = (long) hashOperations.get(ctoken, "id");
            Date currentDate = new Date();
            if (expireDate < currentDate.getTime()) {
                isValid = false;
                response.setContentType("application/json;charset=UTF-8");
                response.setHeader("Cache-Control", "no-cache");
                try {
                    response.getWriter().write("{\"status\":29,\"reason\":\"token expired\"}");
                } catch (IOException e) {
                    //throw new ApplicationException("IOException in populateWithJSON", e);
                }
            }

            ResidentVisitor residentVisitor = new ResidentVisitor();
            residentVisitor.setRid(rid.substring(1));
            residentVisitor.setUserid(Integer.parseInt(userid));
            ResidentVisitor rv = residentVisitorService.getResidentVisitorByRid(residentVisitor);
            //获取需要下载的文件名称
            if (null != rv) {
                fileUploadOrDownLoadServer.downLoadFile(response, rv, fileNames);
            }
        }
    }

    @ApiOperation(value = "/addResidentVisitor 添加常驻供应商",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "}")
    @RequestMapping(method = RequestMethod.POST, value = "/addResidentVisitor")
    @ResponseBody
    public RespInfo addResidentVisitor(@RequestBody ResidentVisitor rv, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUPP_COMPANY.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole())
        )
                ||authToken.getUserid() != rv.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        if (checkComProRel(rv, authToken)) return new RespInfo(ErrorEnum.E_301.getCode(), ErrorEnum.E_301.getMsg());


        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSSS");
        String dateStr = dateFormat.format(date);
        rv.setRid(dateStr);
        int vid = residentVisitorService.addResidentVisitor(rv);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("key", "resident_update");
        map.put("company_id", rv.getUserid());
        map.put("resident_id", "C"+rv.getRid());
        messageSender.updateFaceLib(map);

        // TODO: 2020/4/7 添加日志log
        if (StringUtils.isNotEmpty(rv.getLoginAccount())) {
            String optId = "";
            String optName = "";
            String optRole = "";
            Manager manager = managerService.getManagerByAccount(rv.getLoginAccount());
            if (null != manager) {
                optId = manager.getAccount();
                optName = manager.getSname();
                optRole = String.valueOf(manager.getsType());
            } else {
                UserInfo userInfo = userService.getUserInfo(rv.getUserid());
                optId = userInfo.getEmail();
                optName = userInfo.getUsername();
                optRole = "0";
            }
            OperateLog log = new OperateLog();
            String ipAddr = request.getHeader("X-Forwarded-For");
            log.setUserid(rv.getUserid());
            log.setOptId(optId);
            log.setOptName(optName);
            log.setOptRole(optRole);
            log.setIpAddr(ipAddr);
            log.setObjId(String.valueOf(vid));
            log.setoTime(new Date());
            log.setOptEvent("增加");
            log.setObjName("");
            log.setOptClient("0");
            log.setOptModule("3");
            log.setOptDesc("成功,添加白名单: " + rv.getName());
            operateLogService.addLog(log);
        }
        return new RespInfo(0, "success", "C" + rv.getRid());
    }

    /**
     * 检查项目与公司是否关联
     * @param rv
     * @param authToken
     * @return
     */
    private boolean checkComProRel(@RequestBody ResidentVisitor rv, AuthToken authToken) {
        //检查项目和公司是否关联
        Manager mr = new Manager();
        mr.setCompany(rv.getCompany());
        mr.setUserid(authToken.getUserid());
        Manager manager = managerService.getManagerByCompany(mr);
        if (manager != null) {
            ResidentProject rp = new ResidentProject();
            rp.setAccount(manager.getAccount());
            rp.setUserid(authToken.getUserid());
            rp.setPid(rv.getPid());
            List<ResidentProject> rplist = residentVisitorService.getProject(rp);
            if (rplist.size() == 0) {
                return true;
            }
            rv.setAccount(manager.getAccount());
        }else{
            rv.setAccount("");
        }

        if(AuthToken.ROLE_SUPP_COMPANY.equals(authToken.getAccountRole())){
            if(manager == null || !manager.getAccount().equals(authToken.getLoginAccountId())){
                return true;
            }
        }
        return false;
    }

    /**
     * 供应商通行统计
     * @param req
     * @param response
     */
    @ApiOperation(value = "/ExportgetRvrList 导出供应商通行记录", httpMethod = "GET")
    @RequestMapping(method = RequestMethod.GET, value = "/ExportgetRvrList")
    public void ExportgetRvrList(HttpServletRequest req,HttpServletResponse response) {
        String ctoken=req.getParameter("token");

        if(StringUtils.isNotBlank(ctoken)) {
            ctoken= UtilTools.getTokenByPcode(redisTemplate, ctoken + "_pcode", "ValidationPcode");
        }

        if(StringUtils.isBlank(ctoken)){
            System.out.println("token is null");
            return;
        }

        int userid=0;
        try {
            userid=Integer.parseInt(req.getParameter("userid"));
//            boolean v=UtilTools.validateUser(ctoken, String.valueOf(userid));
//            if(!v){
//                response.getWriter().write("{\"status\":27,\"reason\":\"invalid token\"}");
//            }

            HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
            if(hashOperations.hasKey(ctoken, "id")){
                Long  expireDate = (long) hashOperations.get(ctoken, "id");
                Date currentDate  = new Date();
                if(expireDate<currentDate.getTime())
                {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setHeader("Cache-Control", "no-cache");
                    try {
                        response.getWriter().write("{\"status\":29,\"reason\":\"token expired\"}");
                    } catch (IOException e) {
                        //throw new ApplicationException("IOException in populateWithJSON", e);
                    }
                }
            }else{
                response.setContentType("application/json;charset=UTF-8");
                response.setHeader("Cache-Control", "no-cache");
                try {
                    response.getWriter().write("{\"status\":27,\"reason\":\"invalid token\"}");
                } catch (IOException e) {
                    //throw new ApplicationException("IOException in populateWithJSON", e);
                }
            }
            String startDate=req.getParameter("startDate");
            String endDate=req.getParameter("endDate");
            String name=req.getParameter("name");
            String company=req.getParameter("company");
            String pName=req.getParameter("pName");

            RvReport rvr=new RvReport();
            rvr.setUserid(userid);
            rvr.setStartDate(startDate);
            rvr.setEndDate(endDate);
            rvr.setpName(pName);
            rvr.setCompany(company);

            List<RvReport> rvlist = residentVisitorService.getRvReport(rvr);

            UserInfo userinfo=userService.getBaseUserInfo(userid);
            String filename=userinfo.getCompany()+"供应商通行统计_"+startDate+"-"+endDate;
            ExcelModel excel = new ExcelModel();
            excel.setSheetName(filename);
            ExcelDownLoad download = new AgentInfoExcelDownLoad();
            ExcelModel  em=download.createDownLoadRvrExcel(rvlist, excel);
            download.downLoad(filename+".xls", em,req, response);

            //添加日志
            OperateLog.addExLog(operateLogService,managerService,req, ctoken, userinfo,
                    OperateLog.MODULE_RESIDENT, rvlist.size(), "导出"+filename+":"+rvr.toString());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 常驻访客进出记录
     * @param req
     * @param response
     */
    @ApiOperation(value = "/ExportRvOdList 供应商进出记录", httpMethod = "GET")
    @RequestMapping(method = RequestMethod.GET, value = "/ExportRvOdList")
    public void ExportRvOdList(HttpServletRequest req,HttpServletResponse response) {
        String ctoken=req.getParameter("token");

        if(StringUtils.isNotBlank(ctoken)) {
            ctoken=UtilTools.getTokenByPcode(redisTemplate, ctoken + "_pcode", "ValidationPcode");
        }

        if(StringUtils.isBlank(ctoken)){
            System.out.println("token is null");
            return;
        }

        int userid=0;
        try {
            userid=Integer.parseInt(req.getParameter("userid"));
//            boolean v=UtilTools.validateUser(ctoken, String.valueOf(userid));
//            if(!v){
//                response.getWriter().write("{\"status\":27,\"reason\":\"invalid token\"}");
//            }

            HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
            if(hashOperations.hasKey(ctoken, "id")){
                Long  expireDate = (long) hashOperations.get(ctoken, "id");
                Date currentDate  = new Date();
                if(expireDate<currentDate.getTime())
                {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setHeader("Cache-Control", "no-cache");
                    try {
                        response.getWriter().write("{\"status\":29,\"reason\":\"token expired\"}");
                    } catch (IOException e) {
                        //throw new ApplicationException("IOException in populateWithJSON", e);
                    }
                }
            }else{
                response.setContentType("application/json;charset=UTF-8");
                response.setHeader("Cache-Control", "no-cache");
                try {
                    response.getWriter().write("{\"status\":27,\"reason\":\"invalid token\"}");
                } catch (IOException e) {
                    //throw new ApplicationException("IOException in populateWithJSON", e);
                }
            }
            String startDate=req.getParameter("startDate");
            String endDate=req.getParameter("endDate");
            String name=req.getParameter("name");
            String company=req.getParameter("company");
            String pName=req.getParameter("pName");

            RvReport rvr=new RvReport();
            rvr.setUserid(userid);
            rvr.setStartDate(startDate);
            rvr.setEndDate(endDate);
            rvr.setName(name);
            rvr.setpName(pName);
            rvr.setCompany(company);

            List<RvReport> rvlist = residentVisitorService.getRvOpenInfo(rvr);

            UserInfo userinfo=userService.getBaseUserInfo(userid);
            String filename=userinfo.getCompany()+"外协人员进出记录_"+startDate+"-"+endDate;
            ExcelModel excel = new ExcelModel();
            excel.setSheetName(filename);
            ExcelDownLoad download = new AgentInfoExcelDownLoad();
            ExcelModel  em=download.createDownLoadRvExcel(rvlist, excel);
            download.downLoad(filename+".xls", em,req, response);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "/addProject 添加项目",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "}")
    @RequestMapping(method = RequestMethod.POST, value = "/addProject")
    @ResponseBody
    public RespInfo addProject(@RequestBody ResidentProject rp, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole())
        )
                ||authToken.getUserid() != rp.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        List<ResidentProject> rpt = residentVisitorService.getProjectByName(rp);
        if (rpt.size() > 0) {
            return new RespInfo(33, "project name already exist");
        }

        residentVisitorService.addProject(rp);
        String optName = "";
        String optRole = "";
        String optId = "";
        if (StringUtils.isNotEmpty(rp.getLoginAccount())) {
            Manager manager =  managerService.getManagerByAccount(rp.getLoginAccount());
            if (null != manager) {
                optId = manager.getAccount();
                optName = manager.getSname();
                optRole = String.valueOf(manager.getsType());
            } else {
                UserInfo userInfo = userService.getUserInfo(rp.getUserid());
                optId = userInfo.getEmail();
                optName = userInfo.getUsername();
                optRole = "0";
            }
            OperateLog log = new OperateLog();
            String ipAddr = request.getHeader("X-Forwarded-For");
            log.setUserid(rp.getUserid());
            log.setOptId(optId);
            log.setOptName(optName);
            log.setOptRole(optRole);
            log.setIpAddr(ipAddr);
            log.setObjId("");
            log.setObjName("新项目");
            log.setoTime(new Date());
            log.setOptEvent("增加");
            log.setOptClient("0");
            log.setOptModule("3");
            log.setOptDesc("成功，添加项目: " + rp.getpName());
            operateLogService.addLog(log);
        }

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/updateProject 更新项目",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "}")
    @RequestMapping(method = RequestMethod.POST, value = "/updateProject")
    @ResponseBody
    public RespInfo updateProject(@RequestBody ResidentProject rp, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole())
        )
                ||authToken.getUserid() != rp.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        List<ResidentProject> rpt = residentVisitorService.getProjectByName(rp);
        if (rpt.size() == 0 || rpt.get(0).getPid() == rp.getPid()) {
            ResidentProject searchRpt = new ResidentProject();
            searchRpt.setPid(rp.getPid());
            searchRpt.setUserid(rp.getUserid());
            List<ResidentProject> project = residentVisitorService.getProject(searchRpt);
            if(project.size()==0){
                return new RespInfo(ErrorEnum.E_1201.getCode(), ErrorEnum.E_1201.getMsg());
            }
            String preName = project.get(0).getpName();
            residentVisitorService.updateProject(rp);
            //修改原有关联项目
            ResidentVisitor searchRes = new ResidentVisitor();
            searchRes.setUserid(rp.getUserid());
            searchRes.setpName(preName);
            List<ResidentVisitor> residents = residentVisitorService.getResidentVisitor(searchRes);
            for(ResidentVisitor residentVisitor:residents){
                if(residentVisitor.getPid()==0||residentVisitor.getPid()==rp.getPid()) {
                    residentVisitor.setRid(residentVisitor.getRid().replaceFirst("C",""));
                    residentVisitor.setpName(rp.getpName());
                    residentVisitorService.updateResidentVisitor(residentVisitor);
                }
            }
        } else {
            return new RespInfo(33, "project name already exist");
        }
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/delProject 删除项目",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "}")
    @RequestMapping(method = RequestMethod.POST, value = "/delProject")
    @ResponseBody
    public RespInfo delProject(@RequestBody ResidentProject rp, HttpServletRequest request) {

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole())
        )
                ||authToken.getUserid() != rp.getUserid()) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        int i = residentVisitorService.delProject(rp);
        if (i > 0) {
            ResidentVisitor rv = new ResidentVisitor();
            rv.setPid(rp.getPid());
            rv.setUserid(rp.getUserid());
            residentVisitorService.delRVisitorByPid(rv);
            List<ResidentVisitor> rvlist = residentVisitorService.getResidentVisitor(rv);
            Map<String, Object> map = new HashMap<String, Object>();
            for (int a = 0; a < rvlist.size(); a++) {
                map.put("key", "resident_delete");
                map.put("company_id", rv.getUserid());
                map.put("resident_id", rvlist.get(a).getRid());
                messageSender.updateFaceLib(map);
            }
        }

        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/updateRvPermission 修改入驻供应商授权状态",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{" +
                    "}")
    @RequestMapping(method = RequestMethod.POST, value = "/updateRvPermission")
    @ResponseBody
    public RespInfo updateRvPermission(@RequestBody ResidentVisitor rv, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole())
        ) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }
        rv.setUserid(authToken.getUserid());
        rv.setRid(rv.getRid().substring(1));
        residentVisitorService.updateRvPermission(rv);
        if (rv.getRstatus() == 1) {
            rv = residentVisitorService.getResidentVisitorByRid(rv);
            UserInfo userinfo = userService.getUserInfo(rv.getUserid());
            visitorService.sendSmsToResidentVisitor(userinfo, rv);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("key", "resident_update");
            map.put("company_id", rv.getUserid());
            map.put("resident_id", "C"+rv.getRid());
            messageSender.updateFaceLib(map);
        }

        if (rv.getRstatus() == 1) {
            String optId = "";
            Manager manager = null;
            String optName = "";
            String optRole = "";
            if (StringUtils.isNotEmpty(rv.getLoginAccount())) {
                manager = managerService.getManagerByAccount(rv.getLoginAccount());
                if (null != manager) {
                    optId = manager.getAccount();
                    optName = manager.getSname();
                    optRole = String.valueOf(manager.getsType());
                } else {
                    UserInfo userInfo = userService.getUserInfo(rv.getUserid());
                    optId = userInfo.getEmail();
                    optName = userInfo.getUsername();
                    optRole = "0";
                }
                OperateLog log = new OperateLog();
                String ipAddr = request.getHeader("X-Forwarded-For");
                log.setUserid(rv.getUserid());
                log.setOptId(optId);
                log.setOptName(optName);
                log.setOptRole(optRole);
                log.setIpAddr(ipAddr);
                log.setObjId(rv.getRid().substring(1));
                log.setoTime(new Date());
                log.setOptEvent("修改");//事件，修改、增加、删除、登录
                log.setObjName(rv.getName());
                log.setOptClient("0");
                log.setOptModule("3");
                switch (rv.getRstatus()) {
                    case 0:
                        log.setOptDesc("成功,更新了供应商: " + rv.getName());
                        break;
                    case 1:
                        log.setOptDesc("成功,审批了供应商: " + rv.getName() + ",状态:通过");
                        break;
                    case 2:
                        log.setOptDesc("成功,审批了供应商: " + rv.getName() + ",状态:退回");
                        break;
                }
                operateLogService.addLog(log);
            }
        }

        return new RespInfo(0, "success");
    }

    /**
     * 批量下载常住访客多人附件，分文件夹打包
     */
    @ApiOperation(value = "/downloadResidentAllFile 下载常驻供应商所有文件", httpMethod = "GET")
    @RequestMapping(value = "/downloadResidentAllFile", method = RequestMethod.GET)
    public void downloadResidentAllFile(HttpServletRequest request, HttpServletResponse response,
                                        @RequestParam("rids") String rids,
                                        @RequestParam("token") String token,
                                        @RequestParam("userid") String userid) {
        //TODO 检查权限
        //token 验证
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();

        boolean isValid = true;
        if (null == token || "".equals(token)) {
            response.setContentType("application/json;charset=UTF-8");
            response.setHeader("Cache-Control", "no-cache");
            try {
                response.getWriter().write("{\"status\":27,\"reason\":\"invalid token\"}");
            } catch (IOException e) {
                //throw new ApplicationException("IOException in populateWithJSON", e);
            }
        } else if (hashOperations.hasKey(token, "id")) {
            Long expireDate = (long) hashOperations.get(token, "id");
            Date currentDate = new Date();
            if (expireDate < currentDate.getTime()) {
                isValid = false;
                response.setContentType("application/json;charset=UTF-8");
                response.setHeader("Cache-Control", "no-cache");
                try {
                    response.getWriter().write("{\"status\":29,\"reason\":\"token expired\"}");
                    //获取需要下载的文件名称
                } catch (IOException e) {
                    //throw new ApplicationException("IOException in populateWithJSON", e);
                }
            }
        }
        fileUploadOrDownLoadServer.downloadAllFile(response, rids);
    }
    
    
    @ApiOperation(value = "/delResidentFile 删除供应商附件", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n"
    )
    @RequestMapping(value = "/delResidentFile", method = RequestMethod.POST)
    @ResponseBody
    public RespInfo delResidentFile(@RequestBody ResidentVisitor residentVisitor, HttpServletRequest request) {
        if (com.config.qicool.common.utils.StringUtils.isNotEmpty(residentVisitor.getRid())) {
            String rid = residentVisitor.getRid().substring(1);
            residentVisitor.setRid(rid);
            ResidentVisitor rv = residentVisitorService.getResidentVisitorByRid(residentVisitor);

            if (null != rv) {
                AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
                if ((!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                        && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                        && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole()))
                        ||authToken.getUserid() != rv.getUserid()) {
                    return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
                }

                String[] fileNames = residentVisitor.getFileNames();
                //执行删除文件操作
                for (int i=0;i<fileNames.length;i++) {
//                    File file = new File(FileUploadOrDownLoadServerImpl.rootPath + "/" + rid + "/" + fileName);
//                    if (file.isFile()) {
//                        file.delete();
//                    }
                	fileNames[i]="residentVisitor/"+rid+"/" + fileNames[i];
                }
                
                
                MinioTools.removeObjects(Constant.BUCKET_NAME, Arrays.asList(fileNames));
            }
        }
        return new RespInfo(0, "success");
    }

    @ApiOperation(value = "/uploadRvFace 批量导入供应商照片", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/uploadRvFace")
    @ResponseBody
    public RespInfo uploadRvFace(@RequestBody RequestEmp rep, HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_SUPP_COMPANY.equals(authToken.getAccountRole())
                && !AuthToken.ROLE_HSE.equals(authToken.getAccountRole())
        ) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        String code = this.traverseFolderForRv("/opt/facePicture/visitor",authToken.getUserid());

        if (!"1".equals(code)) {
            return new RespInfo(2100, "file folder is not found");
        }

        return new RespInfo(0, "success");
    }

    public String traverseFolderForRv(String path,int userid) {
        File file = new File(path);
        if (file.exists()) {
            List<ResidentVisitor> rvList = new ArrayList<ResidentVisitor>();
            File[] files = file.listFiles();
            if (files.length == 0) {
                System.out.println("文件夹是空的!");
                return "-1";
            } else {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        System.out.println("文件夹:" + file2.getAbsolutePath());
                        traverseFolderForRv(file2.getAbsolutePath(),userid);
                    } else {
                        String filename = file2.getName();
                        String result = UtilTools.fileToImage(file2,storageClient);
                        if (result.equals("")) {
                            continue;
                        } else {
                            System.out.println("文件名:" + filename.substring(0, filename.indexOf(".")));
                            System.out.println("Url:" + result);
                            ResidentVisitor rv = new ResidentVisitor();
                            rv.setPhone(filename.substring(0, filename.indexOf(".")));
                            rv.setAvatar(result);
                            rv.setUserid(userid);
                            rvList.add(rv);
                        }
                    }
                }
                if (!rvList.isEmpty()) {
                    residentVisitorService.batchUpdateRvAvatar(rvList);
                }

                file.renameTo(new File("/opt/facePicture/visitor" + System.currentTimeMillis()));

                /**
                 * 下发人脸
                 */
                for(ResidentVisitor residentVisitor:rvList) {
                    ResidentVisitor rv = residentVisitorService.getResidentVisitorByPhone(residentVisitor);
                    if(rv != null && rv.getRstatus() == 1) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("key", "resident_update");
                        map.put("company_id", rv.getUserid());
                        map.put("resident_id", "C" + rv.getRid());
                        messageSender.updateFaceLib(map);
                    }
                }
                return "1";
            }
        } else {
            System.out.println("文件不存在!");
            return "-2";
        }
    }
}
