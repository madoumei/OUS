package com.web.controller;

import com.client.bean.EquipmentGroup;
import com.client.bean.Gate;
import com.client.bean.QuestionnaireDetail;
import com.client.service.EquipmentGroupService;
import com.client.service.FileUploadOrDownLoadServer;
import com.client.service.QuestionnaireService;
import com.config.exception.ErrorEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.northconcepts.datapipeline.core.DataException;
import com.northconcepts.datapipeline.core.DataReader;
import com.northconcepts.datapipeline.core.Record;
import com.northconcepts.datapipeline.csv.CSVReader;
import com.northconcepts.datapipeline.excel.ExcelDocument;
import com.northconcepts.datapipeline.excel.ExcelDocument.ProviderType;
import com.northconcepts.datapipeline.excel.ExcelReader;
import com.utils.Constant;
import com.utils.SysLog;
import com.utils.UtilTools;
import com.utils.FileUtils.MinioTools;
import com.utils.yimei.JsonHelper;
import com.web.bean.*;
import com.web.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author qcvisit
 * @date 2021-03-03 09:34
 */
@Controller
@RequestMapping(value = "/*")
@Api(value = "AddressBookController", tags = "API_文件上传管理", hidden = true)
public class AddressBookController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserService userService;

    @Autowired
    private ConfigureService configureService;

    @Autowired
    private SubAccountService subAccountService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ResidentVisitorService residentVisitorService;

    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    ManagerService mgrService;

    @Autowired
    private OperateLogService operateLogService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private EquipmentGroupService equipmentGroupService;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private FileUploadOrDownLoadServer fileUploadOrDownLoadServer;

    @Autowired
    private TokenServer tokenServer;
    
    @Autowired
    private FastFileStorageClient storageClient;
    

    private void saveEvents(FileItem file) throws Throwable {
        if (file.isFormField()) {
            return;
        }

        String fileName = file.getName();
        DataReader reader;

        if (fileName.endsWith(".csv")) {
            reader = new CSVReader(new InputStreamReader(file.getInputStream())).setFieldNamesInFirstRow(true);
        } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
            ExcelDocument excelDocument = new ExcelDocument(fileName.endsWith(".xlsx") ? ProviderType.POI_XSSF : ProviderType.POI);
            excelDocument.open(file.getInputStream());
            reader = new ExcelReader(excelDocument).setFieldNamesInFirstRow(true);
        } else {
            throw new DataException("unknown file type: expected .csv, .xls, or .xlsx file extension").set("fileName", fileName);
        }

//        reader = new TransformingReader(reader).add(new IncludeFields("姓名", "移动电话", "邮箱", "昵称", "团队"));
//        DataWriter writer = new JdbcWriter(db.getConnection(), "EVENT");
//        
//        JobTemplate.DEFAULT.transfer(reader, writer);
    }

    @Deprecated
    @RequestMapping(method = RequestMethod.POST, value = "/oldUploadAB")
    @ResponseBody
    public RespInfo oldUploadAB(@RequestParam(value = "filename") MultipartFile file,
                             @RequestParam(value = "user") int userId,
                             @RequestParam(value = "loginAccount") String loginAccount,
                             HttpServletRequest request) {
        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (authToken.getUserid() != userId ||
                (!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                        && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        final int FIELD_NAME = 0;
        final int FIELD_SEX = 1;
        final int FIELD_PHONE = 2;
        final int FIELD_RECEPTION = 3;//默认接待人
        final int FIELD_TELEPHONE = 4;//固定电话
        final int FIELD_COMPANY = 5;//入驻企业名字
        final int FIELD_STARTDATE = 6;//员工有效期
        final int FIELD_ENDDATE = 7;//员工有效期

        Configures conf = configureService.getConfigure(userId, Constant.EMPCOUNT);
        int confstatus = conf.getStatus();
        try {
            String fileName = file.getOriginalFilename().trim();
            DataReader reader;

            int empcount = employeeService.getEmployeeCount(userId);
            int ecc = Integer.parseInt(conf.getValue());
            if (confstatus == 1 && empcount > ecc) {
                return new RespInfo(43, "exceed the maximum limit");
            }

            if (fileName.endsWith(".csv")) {
                reader = new CSVReader(new InputStreamReader(file.getInputStream())).setFieldNamesInFirstRow(true);
            } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
                ExcelDocument excelDocument = new ExcelDocument(fileName.endsWith(".xlsx") ? ProviderType.POI_XSSF : ProviderType.POI);
                excelDocument.open(file.getInputStream());
                reader = new ExcelReader(excelDocument).setFieldNamesInFirstRow(true);
            } else {
                throw new DataException("unknown file type: expected .csv, .xls, or .xlsx file extension").set("fileName", fileName);
            }

            List<Employee> oldemplist = new ArrayList<Employee>();
            oldemplist = employeeService.getOldEmployeeList(userId);
            List<Employee> duplist = new ArrayList<Employee>();
            List<Employee> newemplist = new ArrayList<Employee>();

            Map<String, Integer> insertMap = new HashMap<String, Integer>();//已插入或将要插入的数据
            for (int k = 0; k < oldemplist.size(); k++) {
                insertMap.put(oldemplist.get(k).getEmpPhone().trim(), oldemplist.get(k).getEmpid());
            }

            ReqSubAccount rsa = new ReqSubAccount();
            rsa.setUserid(userId);
            rsa.setIsUse(-1);
            List<SubAccount> salist = subAccountService.getSubAccountByUserid(rsa);
            Map<String, Object> samap = new HashMap<String, Object>();
            for (int i = 0; i < salist.size(); i++) {
                samap.put(salist.get(i).getCompanyName().trim(), salist.get(i).getId());
            }
            EquipmentGroup eg = new EquipmentGroup();
            eg.setUserid(userId);
            Map egMap = new HashMap();
            List<EquipmentGroup> egList = equipmentGroupService.getEquipmentGroupByUserid(eg);
            for (EquipmentGroup group : egList) {
                egMap.put(group.getEgname(), group.getEgid());
            }

            reader.open();
            try {
                Record record;
                while ((record = reader.read()) != null) {
                    try {
                        Employee emp = new Employee();
                        if (record.getField(FIELD_NAME).isNotNull() && record.getField(FIELD_PHONE).isNotNull()) {
                            emp.setUserid(userId);
                            emp.setEmpName(record.getField(FIELD_NAME).getValueAsString());
                            if (record.getField(FIELD_SEX).getValueAsString().trim().equals("男")) {
                                emp.setEmpSex("1");
                            } else if (record.getField(1).getValueAsString().trim().equals("女")) {
                                emp.setEmpSex("0");
                            }
                            emp.setEmpPhone(record.getField(FIELD_PHONE).getValueAsString().trim());
                            if (insertMap.containsKey(emp.getEmpPhone())) {
                                duplist.add(emp);
                                continue;
                            }
                            if (record.getField(FIELD_RECEPTION).isNotNull() && "是".equals(record.getField(FIELD_RECEPTION).getValueAsString())) {
                                emp.setEmpType(1);
                            }

                            if (record.getField(FIELD_TELEPHONE).isNotNull()) {
                                emp.setTelephone(record.getField(FIELD_TELEPHONE).getValueAsString());
                            }

//                        if (record.getField(5).isNotNull()) {
//                            emp.setCardNo(record.getField(5).getValueAsString());
//                        }
//                        if (record.getField(6).isNotNull()) {
//                            String egNames = record.getField(6).getValueAsString().trim();
//                            StringBuffer sb = new StringBuffer();
//                            String[] egnameArr = egNames.split(",");
//                            for (int i = 0; i < egnameArr.length; i++) {
//                                if (egMap.containsKey(egnameArr[i])) {
//                                    if (i < egnameArr.length - 1) {
//                                        sb.append(egMap.get(egnameArr[i]) + ",");
//                                    } else {
//                                        sb.append(egMap.get(egnameArr[i]));
//                                    }
//                                }
//                            }
//
//                            emp.setEgids(sb.toString());
//                        }
                            if (record.getField(FIELD_COMPANY).isNotNull() && samap.containsKey(record.getField(FIELD_COMPANY).getValueAsString().trim())) {
                                int sid = (int) samap.get(record.getField(FIELD_COMPANY).getValueAsString());
                                if (sid == 0) {
                                    continue;
                                }
                                emp.setSubaccountId(sid);
                            }

                            if (record.getFieldCount() > FIELD_STARTDATE && record.getField(FIELD_STARTDATE).isNotNull()) {
                                emp.setStartDate(record.getField(FIELD_STARTDATE).getValueAsString());

                            }
                            if (record.getFieldCount() > FIELD_ENDDATE && record.getField(FIELD_ENDDATE).isNotNull()) {
                                emp.setEndDate(record.getField(FIELD_ENDDATE).getValueAsString());
                            }
                            insertMap.put(emp.getEmpPhone(), emp.getEmpid());
                            newemplist.add(emp);
                        } else {
                            emp.setEmpName(record.getField(FIELD_NAME).getValueAsString());
                            emp.setEmpPhone(record.getField(FIELD_PHONE).getValueAsString().trim());
                            duplist.add(emp);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                if (!newemplist.isEmpty()) {
                    employeeService.addEmployees(newemplist);

                    // TODO: 2020/4/15 管理员上传模板操作日志
                    if (StringUtils.isNotEmpty(loginAccount)) {
                        String optId = "";
                        String optName = "";
                        String optRole = "";
                        UserInfo userInfo = userService.getUserInfo(userId);

                        Manager manager = managerService.getManagerByAccount(loginAccount);
                        if (null != manager) {
                            optId = manager.getAccount();
                            optName = manager.getSname();
                            optRole = String.valueOf(manager.getsType());
                        } else {
                            SubAccount subAccount = subAccountService.getSubAccountByEmail(loginAccount);
                            if (null != subAccount) {
                                optId = subAccount.getEmail();
                                optName = subAccount.getCompanyName();
                                optRole = "6";
                            } else {
                                optId = userInfo.getEmail();
                                optName = userInfo.getUsername();
                                optRole = "0";
                            }
                        }

                        OperateLog log = new OperateLog();
                        String ipAddr = request.getHeader("X-Forwarded-For");
                        log.setUserid(userId);
                        log.setOptId(optId);
                        log.setOptName(optName);
                        log.setOptRole(optRole);
                        log.setIpAddr(ipAddr);
                        log.setObjId("");
                        log.setObjName("人员模板");
                        log.setoTime(new Date());
                        log.setOptEvent("新增");
                        log.setOptClient("0");
                        log.setOptModule("1");
                        log.setOptDesc("成功,模板上传，同步" + newemplist.size() + "位员工");
                        operateLogService.addLog(log);
                    }
                }

                UserInfo ui = new UserInfo();
                ui.setUserid(userId);
                ui.setRefreshDate(new Date());
                userService.updateRefreshDate(ui);
            } finally {
                reader.close();
            }
            return new RespInfo(0, "success", duplist);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new RespInfo(21, "fail to upload");
    }

    @RequestMapping(method = RequestMethod.POST, value = "/UploadSubaccountEmp")
    @ResponseBody
    public RespInfo UploadSubaccountEmp(@RequestParam(value = "filename") MultipartFile file,
                                        @RequestParam(value = "user") int userId,
                                        @RequestParam(value = "loginAccount") String loginAccount,
                                        @RequestParam(value = "subaccountId") String subaccountId,
                                        HttpServletRequest request) {
        Configures conf = configureService.getConfigure(userId, Constant.EMPCOUNT);
        int confstatus = conf.getStatus();
        try {
            String fileName = file.getOriginalFilename().trim();
            DataReader reader;

            int empcount = employeeService.getEmployeeCount(userId);
            int ecc = Integer.parseInt(conf.getValue());
            if (confstatus == 1 && empcount > ecc) {
                return new RespInfo(43, "exceed the maximum limit");
            }

            if (fileName.endsWith(".csv")) {
                reader = new CSVReader(new InputStreamReader(file.getInputStream())).setFieldNamesInFirstRow(true);
            } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
                ExcelDocument excelDocument = new ExcelDocument(fileName.endsWith(".xlsx") ? ProviderType.POI_XSSF : ProviderType.POI);
                excelDocument.open(file.getInputStream());
                reader = new ExcelReader(excelDocument).setFieldNamesInFirstRow(true);
            } else {
                throw new DataException("unknown file type: expected .csv, .xls, or .xlsx file extension").set("fileName", fileName);
            }
            SubAccount subAccount = subAccountService.getSubAccountById(Integer.parseInt(subaccountId));
            if (null != subAccount) {
                List<Employee> oldemplist = new ArrayList<Employee>();
                oldemplist = employeeService.getOldEmployeeList(userId);
                List<Employee> duplist = new ArrayList<Employee>();
                List<Employee> newemplist = new ArrayList<Employee>();

                Map<String, Integer> map = new HashMap<String, Integer>();
                for (int k = 0; k < oldemplist.size(); k++) {
                    map.put(oldemplist.get(k).getEmpPhone().trim(), oldemplist.get(k).getEmpid());
                }

//                ReqSubAccount rsa = new ReqSubAccount();
//                rsa.setUserid(userId);
//                rsa.setIsUse(-1);
//                List<SubAccount> salist = subAccountService.getSubAccountByUserid(rsa);
//                Map<String, Object> samap = new HashMap<String, Object>();
//                for (int i = 0; i < salist.size(); i++) {
//                    samap.put(salist.get(i).getCompanyName().trim(), salist.get(i).getId());
//                }
//                EquipmentGroup eg = new EquipmentGroup();
//                eg.setUserid(userId);
//                Map egMap = new HashMap();
//                List<EquipmentGroup> egList = equipmentGroupService.getEquipmentGroupByUserid(eg);
//                for (EquipmentGroup group : egList) {
//                    egMap.put(group.getEgname(), group.getEgid());
//                }

                reader.open();
                try {
                    Record record;
                    while ((record = reader.read()) != null) {
                        Employee emp = new Employee();
                        if (record.getField(0).isNotNull() && record.getField(1).isNotNull()) {
                            emp.setUserid(userId);
                            emp.setSubaccountId(Integer.parseInt(subaccountId));
                            emp.setEmpName(record.getField(0).getValueAsString());
                            if (record.getField(1).getValueAsString().trim().equals("男")) {
                                emp.setEmpSex("1");
                            } else if (record.getField(1).getValueAsString().trim().equals("女")) {
                                emp.setEmpSex("0");
                            }
                            emp.setEmpPhone(record.getField(2).getValueAsString().trim());
                            if (map.containsKey(emp.getEmpPhone())) {
                                duplist.add(emp);
                                continue;
                            }

                            if (record.getField(3).isNotNull() && "是".equals(record.getField(3).getValueAsString())) {
                                emp.setEmpType(1);
                            }

                            if (record.getField(4).isNotNull()) {
                                emp.setTelephone(record.getField(4).getValueAsString());
                            }

//                        if (record.getField(5).isNotNull()) {
//                            emp.setCardNo(record.getField(5).getValueAsString());
//                        }
//                        if (record.getField(6).isNotNull()) {
//                            String egNames = record.getField(6).getValueAsString().trim();
//                            StringBuffer sb = new StringBuffer();
//                            String[] egnameArr = egNames.split(",");
//                            for (int i = 0; i < egnameArr.length; i++) {
//                                if (egMap.containsKey(egnameArr[i])) {
//                                    if (i < egnameArr.length - 1) {
//                                        sb.append(egMap.get(egnameArr[i]) + ",");
//                                    } else {
//                                        sb.append(egMap.get(egnameArr[i]));
//                                    }
//                                }
//                            }
//
//                            emp.setEgids(sb.toString());
//                        }
//                        if (samap.containsKey(record.getField(5).getValueAsString().trim())) {
//                            int sid = (int) samap.get(record.getField(5).getValueAsString());
//                            emp.setSubaccountId(sid);
//                        }

                            if (record.getFieldCount() > 5 && record.getField(5).isNotNull()) {
                                emp.setStartDate(record.getField(5).getValueAsString());

                            }
                            if (record.getFieldCount() > 6 && record.getField(6).isNotNull()) {
                                emp.setEndDate(record.getField(6).getValueAsString());
                            }
                            newemplist.add(emp);
                        }
                    }

                    if (!newemplist.isEmpty()) {
                        employeeService.addEmployees(newemplist);


                        // TODO: 2020/4/15 管理员上传模板操作日志
                        if (StringUtils.isNotEmpty(loginAccount)) {
                            String optId = "";
                            String optName = "";
                            String optRole = "";
                            UserInfo userInfo = userService.getUserInfo(userId);

                            Manager manager = managerService.getManagerByAccount(loginAccount);
                            if (null != manager) {
                                optId = manager.getAccount();
                                optName = manager.getSname();
                                optRole = String.valueOf(manager.getsType());
                            } else {
                                if (null != subAccount) {
                                    optId = subAccount.getEmail();
                                    optName = subAccount.getCompanyName();
                                    optRole = "6";
                                } else {
                                    optId = userInfo.getEmail();
                                    optName = userInfo.getUsername();
                                    optRole = "0";
                                }
                            }

                            OperateLog log = new OperateLog();
                            String ipAddr = request.getHeader("X-Forwarded-For");
                            log.setUserid(userId);
                            log.setOptId(optId);
                            log.setOptName(optName);
                            log.setOptRole(optRole);
                            log.setIpAddr(ipAddr);
                            log.setObjId("");
                            log.setObjName("人员模板");
                            log.setoTime(new Date());
                            log.setOptEvent("新增");
                            log.setOptClient("0");
                            log.setOptModule("1");
                            log.setOptDesc("成功,模板上传，同步" + newemplist.size() + "位员工");
                            operateLogService.addLog(log);
                        }
                    }

                    UserInfo ui = new UserInfo();
                    ui.setUserid(userId);
                    ui.setRefreshDate(new Date());
                    userService.updateRefreshDate(ui);
                } finally {
                    reader.close();
                }
                return new RespInfo(0, "success", duplist);
            }else {
                throw new Exception("subaccount info is null ......");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new RespInfo(21, "fail to upload");
    }

    //excel导入员工
    @RequestMapping(method = RequestMethod.POST, value = "/UploadAB")
    @ResponseBody
    public RespInfo UploadAB(@RequestParam(value = "filename") MultipartFile file,
                                @RequestParam(value = "user") int userId,
                                @RequestParam(value = "loginAccount") String loginAccount,
                                HttpServletRequest request) {

        AuthToken authToken = tokenServer.getAuthTokenByRequest(request);
        if (authToken.getUserid() != userId ||
                (!AuthToken.ROLE_ADMIN.equals(authToken.getAccountRole())
                        && !AuthToken.ROLE_SUBADMIN.equals(authToken.getAccountRole()))) {
            return new RespInfo(ErrorEnum.E_610.getCode(), ErrorEnum.E_610.getMsg());
        }

        final int FIELD_NAME = 0;
        final int FIELD_PHONE = 1;
        final int FIELD_EMAIL = 2;
        final int FIELD_NICKNAME = 3;//昵称
        final int FIELD_DEPTNAME = 4;//部门路径
        final int FIELD_EMPNO = 5;//工号
        final int FIELD_WORKBAY = 6;//位置
        final int FIELD_EGIDS = 7;//
        final int FIELD_VEGIDS = 8;
        final int FIELD_REMARK = 9;
        final int FIELD_RECEPTION = 10;//是否禁止预约
        final int FIELD_INVITE = 11;//是否允许邀请
        final int FIELD_STARTDATE = 12;//员工有效期
        final int FIELD_ENDDATE = 13;//员工有效期

        Configures conf = configureService.getConfigure(userId, Constant.EMPCOUNT);
        int confstatus = conf.getStatus();
        try {
            String fileName = file.getOriginalFilename().trim();
            DataReader reader;

            int empcount = employeeService.getEmployeeCount(userId);
            int ecc = Integer.parseInt(conf.getValue());
            if (confstatus == 1 && empcount > ecc) {
                return new RespInfo(43, "exceed the maximum limit");
            }

            if (fileName.endsWith(".csv")) {
                reader = new CSVReader(new InputStreamReader(file.getInputStream())).setFieldNamesInFirstRow(true);
            } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
                ExcelDocument excelDocument = new ExcelDocument(fileName.endsWith(".xlsx") ? ProviderType.POI_XSSF : ProviderType.POI);
                excelDocument.open(file.getInputStream());
                reader = new ExcelReader(excelDocument).setFieldNamesInFirstRow(true);
            } else {
                throw new DataException("unknown file type: expected .csv, .xls, or .xlsx file extension").set("fileName", fileName);
            }

            List<Employee> oldemplist = new ArrayList<Employee>();
            oldemplist = employeeService.getOldEmployeeList(userId);

            Set<String> set = new HashSet<String>();

            List<Employee> duplist = new ArrayList<Employee>();//已存在的员工
            List<Employee> newemplist = new ArrayList<Employee>();//新员工
            Map<String, Set<Integer>> deptmap = new HashMap<String, Set<Integer>>();
            Map<String, Integer> map = new HashMap<String, Integer>();
            for (int k = 0; k < oldemplist.size(); k++) {
                map.put(oldemplist.get(k).getEmpPhone().trim(), oldemplist.get(k).getEmpid());
            }

            reader.open();
            try {
                Record record;
                while ((record = reader.read()) != null) {
                    Employee emp = new Employee();
                    if (record.getField(FIELD_NAME).isNotNull() && record.getField(FIELD_PHONE).isNotNull()) {
                        emp.setUserid(userId);
                        emp.setEmpName(record.getField(FIELD_NAME).getValueAsString());
                        emp.setEmpPhone(record.getField(FIELD_PHONE).getValueAsString().trim());
                        if (null == emp.getEmpPhone() || emp.getEmpPhone().length() != 11) {
                            continue;
                        }
                        if (!set.add(emp.getEmpPhone())) {
                            return new RespInfo(24, "phone no unique", emp);
                        }
                        emp.setEmpEmail(record.getField(FIELD_EMAIL).getValueAsString());
                        if (record.getFieldCount() > FIELD_NICKNAME) {
                            emp.setEmpNickname(record.getField(FIELD_NICKNAME).getValueAsString());
                            emp.setEmpRtxAccount(record.getField(FIELD_NICKNAME).getValueAsString());
                        }
                        if (record.getFieldCount() > FIELD_DEPTNAME) {
                            String deptName = record.getField(FIELD_DEPTNAME).getValueAsString();
                            if (null != deptName && !"".equals(deptName.trim())) {
                                String deptss[] = deptName.split(",");
                                Set<Integer> ilist = new HashSet<Integer>();
                                for (int t = 0; t < deptss.length; t++) {
                                    Department dept = new Department();
                                    dept.setUserid(userId);
                                    String depts[] = deptss[t].split("-");
                                    for (int i = 0; i < depts.length; i++) {
                                        if (i == 0) {
                                            dept.setDeptName(depts[i]);
                                            dept.setParentId(0);
                                        } else {
                                            dept.setDeptName(depts[i]);
                                            dept.setParentId(dept.getDeptid());
                                        }
                                        dept = departmentService.getDepartmentByDeptName(dept);
                                        if (null == dept) {
                                            break;
                                        }
                                    }
                                    if (null != dept) {
                                        ilist.add(dept.getDeptid());
                                    }
                                }
                                if (ilist.size() > 0) {
                                    deptmap.put(emp.getEmpPhone(), ilist);
                                }
                            }
                        }

                        if (record.getFieldCount() > FIELD_EMPNO) {
                            emp.setEmpNo(record.getField(FIELD_EMPNO).getValueAsString());
                        }
                        if (record.getFieldCount() > FIELD_WORKBAY) {
                            emp.setWorkbay(record.getField(FIELD_WORKBAY).getValueAsString());
                        }
                        if (record.getFieldCount() > FIELD_EGIDS) {
                            emp.setEgids(record.getField(FIELD_EGIDS).getValueAsString());
                        }
                        if (record.getFieldCount() > FIELD_VEGIDS) {
                            emp.setVegids(record.getField(FIELD_VEGIDS).getValueAsString());
                        }
                        if (record.getFieldCount() > FIELD_REMARK) {
                            emp.setRemark(record.getField(FIELD_REMARK).getValueAsString());
                        }

                        if (record.getField(FIELD_RECEPTION).isNotNull() && "是".equals(record.getField(FIELD_RECEPTION).getValueAsString())) {
                            emp.setEmpType(0);
                        }else {
                            emp.setEmpType(3);
                        }

                        if (record.getField(FIELD_INVITE).isNotNull() && "是".equals(record.getField(FIELD_INVITE).getValueAsString())) {
                            emp.setAppType(0);
                        }else{
                            emp.setAppType(1);
                        }

                        if (record.getFieldCount() > FIELD_STARTDATE && record.getField(FIELD_STARTDATE).isNotNull()) {
                            emp.setStartDate(record.getField(FIELD_STARTDATE).getValueAsString());

                        }
                        if (record.getFieldCount() > FIELD_ENDDATE && record.getField(FIELD_ENDDATE).isNotNull()) {
                            emp.setEndDate(record.getField(FIELD_ENDDATE).getValueAsString());
                        }
                        emp.setSubaccountId(0);

                        if (map.containsKey(emp.getEmpPhone())) {
                            emp.setEmpid(map.get(emp.getEmpPhone()));
                            duplist.add(emp);
                            continue;
                        }

                        newemplist.add(emp);
                    }
                }

                if (confstatus == 1 && oldemplist.size() + newemplist.size() > ecc) {
                    return new RespInfo(43, "exceed the maximum limit");
                }

                if (!newemplist.isEmpty()) {
                    employeeService.addEmployees(newemplist);
                    // TODO: 2020/4/17 批量导入模板员工
                    if (StringUtils.isNotEmpty(loginAccount)) {
                        String optId = "";
                        String optName = "";
                        String optRole = "";
                        UserInfo userInfo = userService.getUserInfo(userId);
                        Manager manager = managerService.getManagerByAccount(loginAccount);
                        if (null != manager) {
                            optId = manager.getAccount();
                            optName = manager.getSname();
                            optRole = String.valueOf(manager.getsType());
                        } else {

                            optId = userInfo.getEmail();
                            optName = userInfo.getUsername();
                            optRole = "0";
                        }
                        OperateLog log = new OperateLog();
                        String ipAddr = request.getHeader("X-Forwarded-For");
                        log.setUserid(userId);
                        log.setOptId(optId);
                        log.setOptName(optName);
                        log.setOptRole(optRole);
                        log.setIpAddr(ipAddr);
                        log.setObjId("");
                        log.setObjName("员工信息模板");
                        log.setoTime(new Date());
                        log.setOptEvent("增加");
                        log.setOptClient("0");
                        log.setOptModule("1");
                        if (StringUtils.isNotEmpty(userInfo.getEscapeClause())) {
                            log.setOptDesc("成功,模板批量上传" + newemplist.size() + "位员工,同意免责条款");
                        } else {
                            log.setOptDesc("成功,模板批量上传" + newemplist.size() + "位员工");
                        }
                        operateLogService.addLog(log);
                    }
                }

                List<Employee> empdeptlist = new ArrayList<Employee>();
                for (int i = 0; i < newemplist.size(); i++) {
                    if (null != deptmap.get(newemplist.get(i).getEmpPhone())) {
                        Set<Integer> iset = deptmap.get(newemplist.get(i).getEmpPhone());
                        for (Integer deptid : iset) {
                            Employee emp = new Employee();
                            emp.setDeptid(deptid);
                            emp.setUserid(userId);
                            emp.setEmpid(newemplist.get(i).getEmpid());
                            empdeptlist.add(emp);
                        }
                    }

                }

                if (duplist.size() > 0) {
                    List<Integer> empids = new ArrayList<Integer>();

                    for (int a = 0; a < duplist.size(); a++) {
                        empids.add(duplist.get(a).getEmpid());
                        if (null != deptmap.get(duplist.get(a).getEmpPhone())) {
                            Set<Integer> iset = deptmap.get(duplist.get(a).getEmpPhone());
                            for (Integer deptid : iset) {
                                Employee emp = new Employee();
                                emp.setDeptid(deptid);
                                emp.setUserid(userId);
                                emp.setEmpid(duplist.get(a).getEmpid());
                                empdeptlist.add(emp);
                            }
                        }
                    }
                    departmentService.batchDelRelationByEmp(empids);
                    employeeService.batchUpdateEmployees(duplist);
                }

                if (!empdeptlist.isEmpty()) {
                    departmentService.addDeptEmpRelation(empdeptlist);
                }

                UserInfo ui = new UserInfo();
                ui.setUserid(userId);
                ui.setRefreshDate(new Date());
                userService.updateRefreshDate(ui);
            } finally {
                reader.close();
            }
            return new RespInfo(0, "success", duplist);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new RespInfo(21, "fail to upload");
    }

    @RequestMapping(method = RequestMethod.POST, value = "/UploadApponintment")
    @ResponseBody
    public RespInfo UploadApponintment(@RequestParam(value = "filename") MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename().trim();
            DataReader reader;
            List<UploadAppointment> applist = new ArrayList<UploadAppointment>();

            if (fileName.endsWith(".csv")) {
                reader = new CSVReader(new InputStreamReader(file.getInputStream())).setFieldNamesInFirstRow(true);
            } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
                ExcelDocument excelDocument = new ExcelDocument(fileName.endsWith(".xlsx") ? ProviderType.POI_XSSF : ProviderType.POI);
                excelDocument.open(file.getInputStream());
                reader = new ExcelReader(excelDocument).setFieldNamesInFirstRow(true);
            } else {
                throw new DataException("unknown file type: expected .csv, .xls, or .xlsx file extension").set("fileName", fileName);
            }
            reader.open();
            try {
                Record record;
                while ((record = reader.read()) != null) {
                    if (record.getField(0).isNotNull() && record.getField(3).isNotNull()) {
                        UploadAppointment ua = new UploadAppointment();
                        ua.setName(record.getField(0).getValueAsString());
                        if (record.getField(1).isNotNull()) {
                            ua.setPhone(record.getField(1).getValueAsString().trim());
                        } else {
                            ua.setPhone("");
                        }
                        if (record.getField(2).isNotNull()) {
                            ua.setVemail(record.getField(2).getValueAsString().trim());
                        } else {
                            ua.setVemail("");
                        }
                        ua.setVisitType(record.getField(3).getValueAsString().trim());
                        ua.setAppointmentDate(record.getField(4).getValueAsString());
                        ua.setVcompany(record.getField(5).getValueAsString());
                        ua.setRemark(record.getField(6).getValueAsString());
                        ua.setvType(record.getField(7).getValueAsString());

                        applist.add(ua);
                    }
                }
            } finally {
                reader.close();
            }
            return new RespInfo(0, "success", applist);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new RespInfo(21, "fail to upload");
    }


    @RequestMapping(method = RequestMethod.POST, value = "/UploadResidentVisitor")
    @ResponseBody
    public RespInfo UploadResidentVisitor(@RequestParam(value = "filename") MultipartFile file,
                                          @RequestParam(value = "user") int userId,
                                          @RequestParam(value = "loginAccount") String loginAccount, HttpServletRequest request) {
        try {
            String fileName = file.getOriginalFilename().trim();
            DataReader reader;
            List<ResidentVisitor> rvlist = new ArrayList<ResidentVisitor>();
            ResidentProject rp = new ResidentProject();
            if (fileName.endsWith(".csv")) {
                reader = new CSVReader(new InputStreamReader(file.getInputStream())).setFieldNamesInFirstRow(true);
            } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
                ExcelDocument excelDocument = new ExcelDocument(fileName.endsWith(".xlsx") ? ProviderType.POI_XSSF : ProviderType.POI);
                excelDocument.open(file.getInputStream());
                reader = new ExcelReader(excelDocument).setFieldNamesInFirstRow(true);
            } else {
                throw new DataException("unknown file type: expected .csv, .xls, or .xlsx file extension").set("fileName", fileName);
            }
            reader.open();

            EquipmentGroup eg = new EquipmentGroup();
            eg.setUserid(userId);
            Map egMap = new HashMap();
            List<EquipmentGroup> egList = equipmentGroupService.getEquipmentGroupByUserid(eg);
            for (EquipmentGroup group : egList) {
                egMap.put(group.getEgname(), group.getEgid());
            }

            int i = 0;
            try {
                Record record;
                while ((record = reader.read()) != null) {
                    if (record.getField(0).isNotNull() && record.getField(3).isNotNull()) {
                        ResidentVisitor rv = new ResidentVisitor();
                        Date date = new Date();
                        DateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSSS");
                        String dateStr = dateFormat.format(date);
                        rv.setRid(dateStr + i);
                        rv.setUserid(userId);
                        if (record.getField(0).isNotNull()) {
                            rv.setName(record.getField(0).getValueAsString());
                        } else {
                            continue;
                        }
                        if (record.getField(1).isNotNull()) {
                            if ("男".equals(record.getField(1).getValueAsString())) {
                                rv.setSex(1);
                            } else {
                                rv.setSex(0);
                            }
                        }
                        if (record.getField(2).isNotNull()) {
                            rv.setAge(record.getField(2).getValueAsString());
                        }
                        if (record.getField(3).isNotNull()) {
                            rv.setCompany(record.getField(3).getValueAsString());
                        }
                        if (record.getField(4).isNotNull()) {
                            rp.setpName(record.getField(4).getValueAsString());
                            rp.setUserid(userId);
                            List<ResidentProject> rptList = residentVisitorService.getProjectByName(rp);
                            if (rptList.size() > 0) {
                                rv.setPid(rptList.get(0).getPid());
                                rv.setpName(rptList.get(0).getpName());
                            } else {
                                continue;
                            }

                        }
                        if (record.getField(5).isNotNull()) {
                            rv.setLeader(record.getField(5).getValueAsString());
                        } else {
                            continue;
                        }
                        if (record.getField(6).isNotNull()) {
                            rv.setPhone(record.getField(6).getValueAsString());
                        }
                        if (record.getField(7).isNotNull()) {
                            rv.setCardid(record.getField(7).getValueAsString());
                        }
                        if (record.getField(8).isNotNull()) {
                            rv.setArea(record.getField(8).getValueAsString());
                        }
                        if (record.getField(9).isNotNull()) {
                            rv.setJob(record.getField(9).getValueAsString());
                        }
                        if (record.getField(10).isNotNull()) {
                            rv.setDepartment(record.getField(10).getValueAsString());
                        }
                        if (record.getField(11).isNotNull()) {
                            rv.setStartDate(record.getField(11).getValueAsString());
                        } else {
                            continue;
                        }
                        if (record.getField(12).isNotNull()) {
                            rv.setEndDate(record.getField(12).getValueAsString());
                        } else {
                            continue;
                        }

                        //门禁组名字获取id
                        if (record.getField(13).isNotNull()) {
                            String egNames = record.getField(13).getValueAsString().trim();
                            egNames.replace("，",",");
                            StringBuffer sb = new StringBuffer();
                            String[] egnameArr = egNames.split(",");
                            for (int j = 0; j < egnameArr.length; j++) {
                                if (egMap.containsKey(egnameArr[j])) {
                                    if (j < egnameArr.length - 1) {
                                        sb.append(egMap.get(egnameArr[j]) + ",");
                                    } else {
                                        sb.append(egMap.get(egnameArr[j]));
                                    }
                                }
                            }
                            rv.setEgids(sb.toString());
                        }

                        if (record.getField(14).isNotNull()) {
                            rv.setRemark(record.getField(14).getValueAsString());
                        }


                        rvlist.add(rv);
                        i++;
                    }
                }
            } finally {
                reader.close();
            }

            if (rvlist.size() > 0) {
                residentVisitorService.batchAddResidentVisitor(rvlist);
                // TODO: 2020/4/15 上传供应商模板添加操作日志
                if (StringUtils.isNotEmpty(loginAccount)) {
                    String optId = "";
                    String optName = "";
                    String optRole = "";
                    Manager manager = managerService.getManagerByAccount(loginAccount);
                    if (null != manager) {
                        optId = manager.getAccount();
                        optName = manager.getSname();
                        optRole = String.valueOf(manager.getsType());
                    } else {
                        UserInfo userInfo = userService.getUserInfo(userId);
                        optId = userInfo.getEmail();
                        optName = userInfo.getUsername();
                        optRole = "0";
                    }
                    OperateLog log = new OperateLog();
                    String ipAddr = request.getHeader("X-Forwarded-For");
                    log.setUserid(userId);
                    log.setOptId(optId);
                    log.setOptName(optName);
                    log.setOptRole(optRole);
                    log.setIpAddr(ipAddr);
                    log.setObjId("");
                    log.setObjName("供应商模板");
                    log.setoTime(new Date());
                    log.setOptEvent("增加");
                    log.setOptClient("0");
                    log.setOptModule("3");
                    log.setOptDesc("成功,模板上传共添加" + rvlist.size() + "位供应商");
                    operateLogService.addLog(log);
                }
            } else {
                return new RespInfo(21, "fail to upload");
            }

            return new RespInfo(0, "success", rvlist);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new RespInfo(21, "fail to upload");
    }

    @ApiOperation(value = "/UploadSubAccount 导入入驻企业", httpMethod = "POST",
            consumes = "application/json;charset=UFT-8", produces = "application/json;charset=UFT-8",
            notes = "POST示例入参：\n" +
                    "{\n" +
                    "}"
    )
    @RequestMapping(method = RequestMethod.POST, value = "/UploadSubAccount")
    @ResponseBody
    public RespInfo UploadSubAccount(@RequestParam(value = "filename") MultipartFile file, @RequestParam(value = "user") int userId, HttpServletRequest request) {
        try {
//            boolean v = UtilTools.validateUser(request.getHeader("X-COOLVISIT-TOKEN"), String.valueOf(userId));
//            if (!v) {
//                return new RespInfo(1, "invalid user");
//            }

            String fileName = file.getOriginalFilename().trim();
            DataReader reader;
            List<SubAccount> salist = new ArrayList<SubAccount>();
            List<SubAccount> duplist = new ArrayList<SubAccount>();

            List<SubAccount> emaillist = subAccountService.getAllSubAccountEmail();
            Map<String, Integer> emailmap = new HashMap<String, Integer>();
            for (int i = 0; i < emaillist.size(); i++) {
                emailmap.put(emaillist.get(i).getEmail(), emaillist.get(i).getId());
            }

            List<CompanyInfo> ci = subAccountService.getAllCompany();
            Map<String, Integer> map = new HashMap<String, Integer>();
            for (int i = 0; i < ci.size(); i++) {
                map.put(ci.get(i).getCompany(), ci.get(i).getUserid());
            }

            Map<String, Integer> gmap = new HashMap<String, Integer>();
            Gate gate = new Gate();
            gate.setUserid(userId);
            List<Gate> glist = addressService.getGateList(gate);
            for (Gate g : glist) {
                gmap.put(g.getGname(), g.getGid());
            }
            if (fileName.endsWith(".csv")) {
                reader = new CSVReader(new InputStreamReader(file.getInputStream())).setFieldNamesInFirstRow(true);
            } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx") || fileName.endsWith(".xlsm")) {
                ExcelDocument excelDocument = new ExcelDocument(fileName.endsWith(".xlsx") ? ProviderType.POI_XSSF : ProviderType.POI);
                excelDocument.open(file.getInputStream());
                reader = new ExcelReader(excelDocument).setFieldNamesInFirstRow(true);
            } else {
                throw new DataException("unknown file type: expected .csv, .xls, or .xlsx file extension").set("fileName", fileName);
            }
            reader.open();
            try {
                Record record;
                while ((record = reader.read()) != null) {
                    if (record.getField(0).isNotNull() && record.getField(2).isNotNull() && record.getField(3).isNotNull()) {
                        SubAccount sa = new SubAccount();
                        sa.setUserid(userId);
                        String floor = "";
                        String roomNum = "";
                        String gid = "";
                        String gName = "";
                        if (record.getField(0).isNotNull()) {
                            sa.setCompanyName(record.getField(0).getValueAsString());
                        }

                        if (record.getField(1).isNotNull()) {
                            sa.setPhone(record.getField(1).getValueAsString());
                        }

                        if (record.getField(2).isNotNull()) {
                            sa.setEmail(record.getField(2).getValueAsString());
                        }

                        if (record.getField(3).isNotNull()) {
                            floor = record.getField(3).getValueAsString();
                        }

                        if (record.getField(4).isNotNull()) {
                            roomNum = record.getField(4).getValueAsString().trim();
                        }

                        if (record.getField(5).isNotNull()) {
                            String permission = record.getField(5).getValueAsString();
                            String va[] = permission.split(",");
                            int p = 0;
                            int sum = 0;
                            for (int s = 0; s < va.length; s++) {
                                if (va[s].equals("邀请")) {
                                    p = 1;
                                } else if (va[s].equals("预约")) {
                                    p = 2;
                                } else {
                                    p = 4;
                                }
                                sum += p;
                            }
                            if (sum != 0) {
                                sa.setVaPerm(sum);
                            }
                        }

                        Map<String, String> smap = new HashMap<String, String>();
                        if (record.getField(6).isNotNull()) {
                            gName = record.getField(6).getValueAsString().trim();
                            if (gmap.containsKey(gName)) {
                                gid = gmap.get(gName) + "";
                            }
                            sa.setGids(gid);
                        }

                        if (record.getField(7).isNotNull()) {
                            String egnames = record.getField(7).getValueAsString().trim();
                            String[] egnameArr = egnames.split(",");
                            StringBuffer sb = new StringBuffer();
                            for (String egname : egnameArr) {
                                EquipmentGroup eg = new EquipmentGroup();
                                eg.setUserid(userId);
                                eg.setEgname(egname);
                                List<EquipmentGroup> allGroup = equipmentGroupService.getEquipmentGroupByGname(eg);
                                for (EquipmentGroup group : allGroup) {
                                    sb.append(group.getEgid() + ",");
                                }
                            }
                            if (StringUtils.isNotEmpty(sb.toString())) {
                                String s = sb.toString().substring(0, sb.length() - 1);
                                sa.setEgids(s);
                            }
                        }

                        if (record.getField(8).isNotNull()) {
                            String egnames = record.getField(8).getValueAsString().trim();
                            String[] egnameArr = egnames.split(",");
                            StringBuffer sb = new StringBuffer();
                            for (String egname : egnameArr) {
                                EquipmentGroup eg = new EquipmentGroup();
                                eg.setUserid(userId);
                                eg.setEgname(egname);
                                List<EquipmentGroup> allGroup = equipmentGroupService.getEquipmentGroupByGname(eg);
                                for (EquipmentGroup group : allGroup) {
                                    sb.append(group.getEgid() + ",");
                                }
                            }
                            if (StringUtils.isNotEmpty(sb.toString())) {
                                String s = sb.toString().substring(0, sb.length() - 1);
                                sa.setVegids(s);
                            }
                        }

                        sa.setRoomNumber(gid + "," + roomNum);
                        sa.setFloor(gid + "," + floor);

                        if (emailmap.containsKey(record.getField(3).getValueAsString())) {
                            duplist.add(sa);
                            continue;
                        }

                        if (map.containsKey(record.getField(0).getValueAsString())) {
                            duplist.add(sa);
                            continue;
                        }

                        salist.add(sa);
                    }
                }
            } finally {
                reader.close();
            }

            if (salist.size() > 0) {
                subAccountService.batchAddSubAccount(salist);
                for (SubAccount subAccount : salist) {
                    subAccount.setSubAccountiId(subAccount.getId());
                    subAccountService.updateSubAccount(subAccount);
                }
                // TODO: 2020/4/16 添加模板上传的操作日志
                UserInfo userInfo = userService.getUserInfo(userId);
                OperateLog log = new OperateLog();
                String ipAddr = request.getHeader("X-Forwarded-For");
                log.setUserid(userId);
                log.setOptId(userInfo.getEmail());
                log.setOptName(userInfo.getUsername());
                log.setOptRole("0");
                log.setIpAddr(ipAddr);
                log.setObjId("");
                log.setObjName("企业");
                log.setoTime(new Date());
                log.setOptEvent("增加");
                log.setOptClient("0");
                log.setOptModule("4");
                log.setOptDesc("成功,模板上传共添加" + salist.size() + "个企业");
                operateLogService.addLog(log);
                return new RespInfo(0, "success", duplist);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            SysLog.error(e);
        }
        return new RespInfo(21, "fail to upload");
    }

    /**
     * 批量上传题目
     *
     * @param file
     * @param userId
     * @param qid
     * @param request
     * @return
     */

    @RequestMapping(method = RequestMethod.POST, value = "/UploadExamTemplate")
    @ResponseBody
    public RespInfo UploadExamTemplate(
            @RequestParam(value = "filename") MultipartFile file,
            @RequestParam(value = "user") int userId, @RequestParam(value = "qid") String qid, HttpServletRequest request) {
        try {
            String fileName = file.getOriginalFilename().trim();
            DataReader reader;
            List<QuestionnaireDetail> qlist = new ArrayList<QuestionnaireDetail>();

            if (fileName.endsWith(".csv")) {
                reader = new CSVReader(new InputStreamReader(file.getInputStream())).setFieldNamesInFirstRow(true);
            } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
                ExcelDocument excelDocument = new ExcelDocument(fileName.endsWith(".xlsx") ? ProviderType.POI_XSSF : ProviderType.POI);
                excelDocument.open(file.getInputStream());
                reader = new ExcelReader(excelDocument).setFieldNamesInFirstRow(true);
            } else {
                throw new DataException("unknown file type: expected .csv, .xls, or .xlsx file extension").set("fileName", fileName);
            }
            reader.open();
            try {
                Record record;
                while ((record = reader.read()) != null) {
                    if (record.getField(1).isNotNull() && record.getField(2).isNotNull() && record.getField(3).isNotNull()) {
                        List<AnswerOption> aoList = new ArrayList<AnswerOption>();
                        QuestionnaireDetail qd = new QuestionnaireDetail();
                        qd.setUserid(userId);
                        qd.setQid(qid);
                        qd.setQuestion(record.getField(1).getValueAsString());
                        qd.setAnswer(record.getField(2).getValueAsString());
                        qd.setScore(record.getField(3).getValueAsInteger());
                        for (int i = 4; i < record.getFieldCount(); i++) {
                            AnswerOption ao = new AnswerOption();
                            if (record.getField(i).isNotNull()) {
                                ao.setNum(record.getField(i).getValueAsString().substring(0, 1));
                                ao.setContent(record.getField(i).getValueAsString().substring(1));
                                ao.setoType("1");
                                aoList.add(ao);
                            }
                        }
                        qd.setFoption(JsonHelper.toJsonString(aoList));
                        qlist.add(qd);
                    }
                }
                questionnaireService.batchAddQuestionnaireDetail(qlist);
            } finally {
                reader.close();
            }
            return new RespInfo(0, "success");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new RespInfo(21, "fail to upload");
    }

    /**
     * 上传多企业logo压缩文件
     *
     * @param file
     * @param userid
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/uploadSubAccountLogo")
    @ResponseBody
    public RespInfo uploadSubAccountLogo(@RequestParam(value = "fileName") MultipartFile file, @RequestParam(value = "userid") int userid, HttpServletRequest request) {
        RespInfo respInfo = null;
        try {
            request.setCharacterEncoding("UTF-8");
            if (file.isEmpty()) {
                return new RespInfo(620, "Empty File upload");
            }
            if (!file.getOriginalFilename().endsWith(".zip")) {
                return new RespInfo(619, "File upload format error");
            }
            String targetDir = "/opt/subAccountLogo";
            long startTime = System.currentTimeMillis();
            respInfo = fileUploadOrDownLoadServer.unzip(file, targetDir);
            long endTime = System.currentTimeMillis();
            System.out.println("解压缩共计耗时：" + (endTime - startTime) + "毫秒");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return respInfo;
    }

    /**
     * 导入公司logo数据
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/importSubAccountLogo")
    @ResponseBody
    public RespInfo importSubAccountLogo(@RequestBody SubAccount subAccount) {
        String code = this.traverseFolderForSubAccountLogo("/opt/subAccountLogo", subAccount.getUserid());
        if (!"1".equals(code)) {
            return new RespInfo(2100, "file folder is not found");
        }
        return new RespInfo(0, "success");
    }

    public String traverseFolderForSubAccountLogo(String path, int userid) {
        File file = new File(path);
        if (file.exists()) {
            List<SubAccount> subAccountList = new ArrayList<SubAccount>();
            File[] files = file.listFiles();
            if (files.length == 0) {
                System.out.println("文件夹是空的!");
                return "-1";
            } else {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        System.out.println("文件夹:" + file2.getAbsolutePath());
                        this.traverseFolderForSubAccountLogo(file2.getAbsolutePath(), userid);
                    } else {
                        String filename = file2.getName();
                      //fdfs
                      //  String result = UtilTools.fileToImage(file2,storageClient);
                      //fdfs
                        
                        //minio
                          String result = UtilTools.fileToImage(file2,"/subAccountLogo/");
                        //minio
                        if (result.equals("")) {
                            continue;
                        } else {
                            System.out.println("文件名:" + filename.substring(0, filename.indexOf(".")));
                            System.out.println("Url:" + result);
                            SubAccount subAccount = new SubAccount();
                            subAccount.setUserid(userid);
                            subAccount.setCompanyName(filename.substring(0, filename.indexOf(".")).trim());
                            subAccount.setLogo(result);
                            subAccountList.add(subAccount);
                            file2.delete();
                        }
                    }
                }
                if (!subAccountList.isEmpty()) {
                    subAccountService.batchUpdateSubAccountLogo(subAccountList);
                }
                return "1";
            }
        } else {
            System.out.println("文件不存在!");
            return "-2";
        }
    }

}
