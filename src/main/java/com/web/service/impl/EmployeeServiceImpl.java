package com.web.service.impl;

import com.client.bean.EquipmentGroup;
import com.client.dao.EquipmentGroupDao;
import com.config.exception.ErrorEnum;
import com.config.qicool.common.utils.StringUtils;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiAuthScopesRequest;
import com.dingtalk.api.request.OapiV2DepartmentGetRequest;
import com.dingtalk.api.request.OapiV2UserListRequest;
import com.dingtalk.api.response.OapiAuthScopesResponse;
import com.dingtalk.api.response.OapiV2DepartmentGetResponse;
import com.dingtalk.api.response.OapiV2UserListResponse;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taobao.api.ApiException;
import com.utils.Constant;
import com.utils.SysLog;
import com.utils.UtilTools;
import com.utils.cacheUtils.CacheManager;
import com.utils.empUtils.EmpUtils;
import com.utils.httpUtils.HttpClientUtil;
import com.utils.jsonUtils.JacksonJsonUtil;
import com.web.bean.*;
import com.web.dao.ConfigureDao;
import com.web.dao.DepartmentDao;
import com.web.dao.EmployeeDao;
import com.web.dao.UserDao;
import com.web.service.EmployeeService;
import com.web.service.PersonInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.client.service.Impl.VisitorServiceImpl.*;

@Service("employeeService")
public class EmployeeServiceImpl implements EmployeeService {


    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private ConfigureDao configureDao;

    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private EquipmentGroupDao equipmentGroupDao;

    @Autowired
    private PersonInfoService personInfoService;

    private RedisTemplate redisTemplate;

    @Inject
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<Employee> getEmployeeList(RequestEmp rep) {
        // TODO Auto-generated method stub
        return employeeDao.getEmployeeList(rep);
    }

    @Override
    public Employee getEmployee(int empid) {
        // TODO Auto-generated method stub
        return employeeDao.getEmployee(empid);
    }

    @Override
    public int addEmployee(Employee emp) {
        // TODO Auto-generated method stub
        return employeeDao.addEmployee(emp);
    }

    @Override
    public int updateEmployee(Employee emp) {
        // TODO Auto-generated method stub
        return employeeDao.updateEmployee(emp);
    }

    @Override
    public int delEmployee(int empid) {
        // TODO Auto-generated method stub
        return employeeDao.delEmployee(empid);
    }

    @Override
    public int batchDelEmployee(List<Integer> empids) {
        // TODO Auto-generated method stub
        return employeeDao.batchDelEmployee(empids);
    }

    @Override
    public int delEmployees(int userid) {
        // TODO Auto-generated method stub
        return employeeDao.delEmployees(userid);
    }

    @Override
    public int addEmployees(List<Employee> emplist) {
        // TODO Auto-generated method stub
        return employeeDao.addEmployees(emplist);
    }

    @Override
    public int batchUpdateEmployees(List<Employee> emplist) {
        // TODO Auto-generated method stub
        return employeeDao.batchUpdateEmployees(emplist);
    }

    @Override
    public List<Employee> getEmployeeByEmail(String empEmail, int userid) {
        // TODO Auto-generated method stub
        return employeeDao.getEmployeeByEmail(empEmail, userid);
    }

    @Override
    public Employee getOpenid(int empid) {
        // TODO Auto-generated method stub
        return employeeDao.getOpenid(empid);
    }

    @Override
    public List<Employee> getOldEmployeeList(int userid) {
        // TODO Auto-generated method stub
        return employeeDao.getOldEmployeeList(userid);
    }

    @Override
    public int bindingOpenid(Employee emp) {
        // TODO Auto-generated method stub
        return employeeDao.bindingOpenid(emp);
    }

    @Override
    public int UpdateDefaultNotify(List<Employee> emplist) {
        // TODO Auto-generated method stub
        return employeeDao.UpdateDefaultNotify(emplist);
    }

    @Override
    public List<Employee> getDefalutNotify(Map<String, Object> map) {
        // TODO Auto-generated method stub
        return employeeDao.getDefalutNotify(map);
    }

    @Override
    public int getEmployeeCount(int userid) {
        // TODO Auto-generated method stub
        return employeeDao.getEmployeeCount(userid);
    }

    private List<Department> getDeptFromDD(UserInfo userinfo) {
        List<Department> deptlist = new ArrayList<Department>();
        String response = "";
        int deptid =1;
        String token = UtilTools.getDDAccToken(userinfo.getUserid(), userinfo.getDdAppid(), userinfo.getDdAppSccessSecret());
        try {

            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/auth/scopes");
            OapiAuthScopesRequest req = new OapiAuthScopesRequest();
            req.setHttpMethod("GET");
            OapiAuthScopesResponse rsp = client.execute(req, token);
            if(!rsp.isSuccess()){
                SysLog.error(rsp.getBody());
                return deptlist;
            }
            Map<String, Integer> pmap = new HashMap<String, Integer>();//外部部门id和内部部门id之间的映射关系
            List<Long> authedDept = rsp.getAuthOrgScopes().getAuthedDept();
            for(Long deptId:authedDept) {
                if(deptId != 1){
                    //如果不是根部门，则获取部门详情
                    client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/department/get");
                    OapiV2DepartmentGetRequest reqDeptInfo = new OapiV2DepartmentGetRequest();
                    reqDeptInfo.setDeptId(deptId);
                    reqDeptInfo.setLanguage("zh_CN");
                    OapiV2DepartmentGetResponse rspDeptInfo = client.execute(reqDeptInfo, token);
                    if(!rspDeptInfo.isSuccess()){
                        SysLog.error(rspDeptInfo.getBody());
                        deptlist.clear();
                        return deptlist;
                    }
                    Department department = new Department();
                    department.setUserid(userinfo.getUserid());
                    department.setDeptid(deptid++);
                    department.setStrDeptId(String.valueOf(deptId));
                    department.setParentId(0);
                    department.setDeptName(rspDeptInfo.getResult().getName());
                    deptlist.add(department);
                    pmap.put(department.getStrDeptId(),department.getDeptid());
                }

                //获取所有子部门
                Map<String, String> params = new HashMap<String, String>();
                params.put("access_token", token);
                params.put("id", String.valueOf(deptId));
                params.put("fetch_child", "true");

                response = HttpClientUtil.invokeGet("https://oapi.dingtalk.com/department/list", params, "UTF-8", 30000);
                ObjectMapper mapper = JacksonJsonUtil.getMapperInstance(false);

                JsonNode rootNode = mapper.readValue(response, JsonNode.class);
                if (rootNode.path("errcode").asInt() == 0) {
                    JsonNode result = rootNode.path("department");
                    Iterator<JsonNode> it = result.iterator();
                    while (it.hasNext()) {
                        JsonNode jn = it.next();
                        Department dept = new Department();
                        dept.setUserid(userinfo.getUserid());
                        dept.setDeptid(deptid++);
                        dept.setStrDeptId(jn.path("id").asLong() + "");
                        dept.setDeptName(jn.path("name").asText());
                        if (jn.path("parentid").asLong() == 1) {
                            dept.setParentId(0);
                        } else {
                            dept.setStrParentId(jn.path("parentid").asLong()+"");
                        }
                        deptlist.add(dept);
                        pmap.put(dept.getStrDeptId(),dept.getDeptid());
                    }

                } else {
                    SysLog.error(userinfo.getUserPrintInfo() + "钉钉同步部门失败 " + response);
                    return deptlist;
                }
            }

            for (int d = 0; d < deptlist.size(); d++) {
                if ("0".equals(deptlist.get(d).getStrParentId())
                        || pmap.get(deptlist.get(d).getStrParentId()) == null) {
                    deptlist.get(d).setParentId(0);
                } else {
                    deptlist.get(d).setParentId(pmap.get(deptlist.get(d).getStrParentId()));
                }
            }

            departmentDao.delAllDepartment(userinfo.getUserid());
            departmentDao.addDepartmentList(deptlist);
        } catch (JsonParseException e) {
            e.printStackTrace();
            SysLog.error(userinfo.getUserPrintInfo() + "钉钉同步部门失败 " ,e);
        } catch (JsonMappingException e) {
            e.printStackTrace();
            SysLog.error(userinfo.getUserPrintInfo() + "钉钉同步部门失败 " ,e);
        } catch (IOException e) {
            e.printStackTrace();
            SysLog.error(userinfo.getUserPrintInfo() + "钉钉同步部门失败 " ,e);
        } catch (ApiException e) {
            e.printStackTrace();
            SysLog.error(userinfo.getUserPrintInfo() + "钉钉同步部门失败 " ,e);
        }

        return deptlist;
    }

    /**
     * 从钉钉服务器同步人员
     *
     * @param userinfo
     * @param deptlist
     * @return 失败：<0   成功：人数
     */
    private int addEmpFromDD(UserInfo userinfo, List<Department> deptlist) {
        // TODO Auto-generated method stub
        String token = UtilTools.getDDAccToken(userinfo.getUserid(), userinfo.getDdAppid(), userinfo.getDdAppSccessSecret());

        List<Employee> importEmplist = new ArrayList<Employee>();

        try {
            for (int i = 0; i < deptlist.size(); i++) {
                long next_cursor = 0;
                DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/list");
                OapiV2UserListRequest req = new OapiV2UserListRequest();
                req.setDeptId(Long.parseLong(deptlist.get(i).getStrDeptId()));
                req.setSize(100L);
                req.setContainAccessLimit(false);
                req.setLanguage("zh_CN");
                req.setCursor(next_cursor);
                OapiV2UserListResponse rsp = client.execute(req, token);
                ObjectMapper mapper = JacksonJsonUtil.getMapperInstance(false);

                    JsonNode jsonNode = mapper.readValue(rsp.getBody(), JsonNode.class);
                    int errcode = jsonNode.path("errcode").asInt();
                    JsonNode result = jsonNode.path("result");
                    boolean has_more = result.path("has_more").asBoolean();

                    while (errcode == 0 && null != result) {
                        JsonNode userlist = result.path("list");
                        Iterator<JsonNode> it = userlist.iterator();
                        while (it.hasNext()) {
                            JsonNode jn = it.next();
                            Employee e = new Employee();
                            e.setUserid(userinfo.getUserid());
                            e.setDdid(jn.path("userid").asText());
                            e.setOpenid(jn.path("userid").asText());
                            e.setEmpPhone(jn.path("mobile").asText());
                            e.setEmpEmail(jn.path("email").asText());
                            e.setEmpName(jn.path("name").asText());
                            e.setEmpPosition(jn.path("position").asText());
                            e.setEmpPosition(jn.path("title").asText());
                            e.setEmpNo(jn.path("job_number").asText());
                            e.setRemark(jn.path("remark").asText());
                            e.setTelephone(jn.path("telephone").asText());
                            List<String> depts = new ArrayList<String>();
                            for (int s = 0; s < jn.path("dept_id_list").size(); s++) {
                                depts.add(jn.path("dept_id_list").get(s).asInt()+"");
                            }
                            e.setODeptidList(depts);
                            e.setIsLeader(jn.path("leader").asBoolean());
                            SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
                            String startDate = sd.format(new Date());
                            int endDate = Integer.parseInt(startDate) + 300000;
                            e.setStartDate(startDate);
                            e.setEndDate(String.valueOf(endDate));
                            importEmplist.add(e);
                        }
                        if (null != result.path("next_cursor")) {
                            next_cursor = result.path("next_cursor").asLong();
                        }

                        errcode = -1;
                        result= null;
                        if(has_more){
                            req.setCursor(next_cursor);
                            rsp = client.execute(req, token);
                            jsonNode = mapper.readValue(rsp.getBody(), JsonNode.class);
                            errcode = jsonNode.path("errcode").asInt();
                            result = jsonNode.path("result");
                            has_more = result.path("has_more").asBoolean();
                        }

                    }

            }
            return ImportEmpList(userinfo, importEmplist);
        } catch (ApiException e) {
            e.printStackTrace();
            SysLog.error(e);
        } catch (JsonMappingException e) {
            e.printStackTrace();
            SysLog.error(e);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            SysLog.error(e);
        }


        return -1;
    }

    @Override
    public int addEmployeesbydd(List<Employee> emplist) {
        // TODO Auto-generated method stub
        return employeeDao.addEmployeesbydd(emplist);
    }

    @Override
    public List<Employee> getEmpListByName(int userid, String name) {
        // TODO Auto-generated method stub
        return employeeDao.getEmpListByName(userid, name);
    }

    @Override
    public int quickbindingOpenid(Employee emp) {
        // TODO Auto-generated method stub
        return employeeDao.quickbindingOpenid(emp);
    }

    @Override
    public int quickbindingOpenidbyemail(Employee emp) {
        // TODO Auto-generated method stub
        return employeeDao.quickbindingOpenidbyemail(emp);
    }

    @Override
    public int bindingOpenidByPhone(Employee emp) {
        // TODO Auto-generated method stub
        return employeeDao.bindingOpenidByPhone(emp);
    }

    @Override
    public List<Employee> getEmpListByOpenid(String openid) {
        // TODO Auto-generated method stub
        return employeeDao.getEmpListByOpenid(openid);
    }

    @Override
    public List<Integer> getEmpIdByOpenid(String openid) {
        // TODO Auto-generated method stub
        return employeeDao.getEmpIdByOpenid(openid);
    }

    @Override
    public int resetOpenid(Employee emp) {
        // TODO Auto-generated method stub
        return employeeDao.resetOpenid(emp);
    }

    @Override
    public Employee getSendUrlEmp(Employee emp) {
        // TODO Auto-generated method stub
        return employeeDao.getSendUrlEmp(emp);
    }

    @Override
    public List<Employee> getSubAccountEmpList(int userid, int subaccountId) {
        // TODO Auto-generated method stub
        return employeeDao.getSubAccountEmpList(userid, subaccountId);
    }

    @Override
    public int delSAEmployees(int subaccountId) {
        // TODO Auto-generated method stub
        return employeeDao.delSAEmployees(subaccountId);
    }

    @Override
    public int updateEmpSubAccount(List<Employee> emplist) {
        // TODO Auto-generated method stub
        return employeeDao.updateEmpSubAccount(emplist);
    }

    @Override
    public int getRelAccEmpCount(Map<String, String> conditions) {
        // TODO Auto-generated method stub
        return employeeDao.getRelAccEmpCount(conditions);
    }

    @Override
    public List<Employee> checkEmployeeExists(int userid, String empPhone) {
        // TODO Auto-generated method stub
        return employeeDao.checkEmployeeExists(userid, empPhone);
    }

    @Override
    public List<Employee> checkEmployeeExistsStrict(RequestEmp empPhone) {
        return employeeDao.checkEmployeeExistsStrict(empPhone);
    }

    @Override
    public Employee checkEmployeeExistsByRtxAccount(String empRtxAccount) {
        return employeeDao.checkEmployeeExistsByRtxAccount(empRtxAccount);
    }

    @Override
    public int delEmployeesByUserid(int userid) {
        // TODO Auto-generated method stub
        return employeeDao.delEmployeesByUserid(userid);
    }

    @Override
    public List<Employee> getEmployeebyPhone(String empPhone, String openid, int userid) {
        // TODO Auto-generated method stub
        if (StringUtils.isNotBlank(empPhone) || StringUtils.isNotBlank(openid)) {
            return employeeDao.getEmployeebyPhone(empPhone, openid, userid);
        }
        return new ArrayList<Employee>();
    }

    @Override
    public List<Employee> getEmpInfo(int userid, String empPhone) {
        // TODO Auto-generated method stub
        if (StringUtils.isNotBlank(empPhone)) {
            return employeeDao.getEmpInfo(userid, empPhone);
        }
        return new ArrayList<Employee>();
    }

    @Override
    public int updateEmpPwd(Employee emp) {
        // TODO Auto-generated method stub
        return employeeDao.updateEmpPwd(emp);
    }

    @Override
    public List<Employee> getEmployeePassword(String phone) {
        // TODO Auto-generated method stub
        return employeeDao.getEmployeePassword(phone);
    }

    @Override
    public List<Employee> getEmpDeptList(RequestEmp rep) {
        // TODO Auto-generated method stub
        return employeeDao.getEmpDeptList(rep);
    }

    @Override
    public List<Employee> getEmpDeptByUserid(int userid) {
        // TODO Auto-generated method stub
        return employeeDao.getEmpDeptByUserid(userid);
    }

    @Override
    public List<Employee> getEmpListPages(RequestEmp emp) {
        // TODO Auto-generated method stub
        return employeeDao.getEmpListPages(emp);
    }

    @Override
    public List<Employee> getDeptManager(Map<String, String> conditions) {
        // TODO Auto-generated method stub
        return employeeDao.getDeptManager(conditions);
    }

    @Override
    public List<Employee> getEmpRoleList(RequestEmp emp) {
        // TODO Auto-generated method stub
        return employeeDao.getEmpRoleList(emp);
    }

    @Override
    public Employee getEmpByDDid(Employee emp) {
        // TODO Auto-generated method stub
        return employeeDao.getEmpByDDid(emp);
    }

    @Override
    public List<Employee> getEmpListByempName(RequestEmp reqemp) {
        // TODO Auto-generated method stub
        return employeeDao.getEmpListByempName(reqemp);
    }

    @Override
    public List<Employee> getEmpListByKey(RequestEmp reqemp) {
        // TODO Auto-generated method stub
        return employeeDao.getEmpListByKey(reqemp);
    }


    @Override
    public int addEmployeesNoInc(List<Employee> emplist) {
        // TODO Auto-generated method stub
        return employeeDao.addEmployeesNoInc(emplist);
    }

    @Override
    public List<Employee> getEmployeesByEmpids(List<Integer> empids) {
        // TODO Auto-generated method stub
        return employeeDao.getEmployeesByEmpids(empids);
    }

    @Override
    public int updateEmpFace(Employee emp) {
        // TODO Auto-generated method stub
        return employeeDao.updateEmpFace(emp);
    }

    @Override
    public int updateEmpAvatar(Employee emp) {
        // TODO Auto-generated method stub
        return employeeDao.updateEmpAvatar(emp);
    }

    @Override
    public Employee getEmployeeByCardNo(Employee emp) {
        // TODO Auto-generated method stub
        return employeeDao.getEmployeeByCardNo(emp);
    }

    @Override
    public List<Employee> getDefaultEmpList(RequestEmp emp) {
        // TODO Auto-generated method stub
        return employeeDao.getDefaultEmpList(emp);
    }

    @Override
    public Employee getEmployeebyPlateNum(Employee emp) {
        // TODO Auto-generated method stub
        return employeeDao.getEmployeebyPlateNum(emp);
    }

    @Override
    public int batchUpdateEmpName(List<Employee> emplist) {
        // TODO Auto-generated method stub
        return employeeDao.batchUpdateEmpName(emplist);
    }

    @Override
    public int batchUpdateEmpAvatar(List<Employee> emplist) {
        // TODO Auto-generated method stub
        return employeeDao.batchUpdateEmpAvatar(emplist);
    }

    @Override
    public List<Employee> getEmpByFaceResult(RequestEmp emp) {
        // TODO Auto-generated method stub
        return employeeDao.getEmpByFaceResult(emp);
    }

    @Override
    public int updateEmpEgids(Employee emp) {
        // TODO Auto-generated method stub
        return employeeDao.updateEmpEgids(emp);
    }

    @Override
    public List<Employee> getSubEmpListPages(RequestEmp emp) {
        // TODO Auto-generated method stub
        return employeeDao.getSubEmpListPages(emp);
    }

    @Override
    public int batchUpdateEmpEgids(List<Employee> repEmp) {
        return employeeDao.batchUpdateEmpEgids(repEmp);
    }

    @Override
    public List<Employee> searchEmpByCondition(Map<String, String> searchMap) {
//		List<Employee> employees = employeeDao.searchEmpByCondition(searchMap);
//		String deptid = searchMap.get("deptid");
//		if (StringUtils.isNotBlank(deptid) && Integer.parseInt(deptid)>0){
//			//判断部门下的所有员工 ，是否是条件部门下的
//			List<Department> allDeptTreeList = departmentDao.getAllDepartmentList(Integer.parseInt(searchMap.get("userid")));
//			List<Department> collect = allDeptTreeList.stream().filter(department -> department.getDeptid() == Integer.parseInt(deptid)).collect(Collectors.toList());
//			if (null != collect && collect.size()>0){
//				Department department = collect.get(0);
//
//			}
//		}
        return employeeDao.searchEmpByCondition(searchMap);
    }

    @Override
    public String getWchartUserIdByCode(int userid, String code) {
        UserInfo userInfo = userDao.getUserInfo(userid);
        CacheManager cm = CacheManager.getInstance();
        if (null == cm.getToken("WeChartToken_" + userid) || "".equals(cm.getToken("WeChartToken_" + userid))) {
            settoken(cm, userInfo);
        }
        String weChartToken = cm.getToken("WeChartToken_" + userid);
        //1.获取请求的url
        String url = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=ACCESS_TOKEN&code=" + code;
        try {
            String requrl = url.replace("ACCESS_TOKEN", weChartToken);
            ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
            Map<String, String> reqParam = new HashMap<>();
            //2.调用接口，发送请求，获取成员
            String response = HttpClientUtil.connectPostHttps(requrl, reqParam);
            System.out.println(response);
            JsonNode jsonNode = objectMapper.readValue(response, JsonNode.class);
            int errcode = jsonNode.path("errcode").asInt();
            if (errcode == 0) {
                String WchartUserId = jsonNode.path("UserId").asText();
                return WchartUserId;
            } else if (40001 == errcode || 42001 == errcode || 42002 == errcode || 40014 == errcode) {
                Map<String, Object> param = new HashMap<>();
                checkresult(cm, userInfo, response, url, param);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "-1";
    }

    @Override
    public String getFeiShuOpenIdByCode(int userid, String code) {
        UserInfo userInfo = userDao.getUserInfo(userid);
        CacheManager cm = CacheManager.getInstance();
        if (null == cm.getToken("FsToken_" + userid) || "".equals(cm.getToken("FsToken_" + userid))) {
            setFsToken(cm, userInfo);
        }
        String weChartToken = cm.getToken("FsToken_" + userid);
        //1.获取请求的url
        String url = "https://open.feishu.cn/open-apis/authen/v1/access_token";
        try {
            ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("grant_type", "authorization_code");
            params.put("code", code);
            //2.调用接口，发送请求，获取成员
            String response = HttpClientUtil.postJsonBodyForFs(url, 3000, params, "utf-8", weChartToken);
            System.out.println(response);
            JsonNode jsonNode = objectMapper.readValue(response, JsonNode.class);
            int errcode = jsonNode.path("code").asInt();
            if (errcode == 0) {
                String openId = jsonNode.path("data").path("open_id").asText();
                return openId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "-1";
    }

    @Override
    public String getWxOpenIdByCode(String code) {
        //1.获取请求的url
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
        try {
            String requrl = url
                    .replace("APPID", Constant.WeiXin_Notify.get("APPID"))
                    .replace("SECRET", Constant.WeiXin_Notify.get("APP_SECRET"))
                    .replace("CODE", code);
            ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
            Map<String, String> reqParam = new HashMap<>();
            //2.调用接口，发送请求，获取成员
            String response = HttpClientUtil.connectPostHttps(requrl, reqParam);
            System.out.println("微信扫码登录获取openid响应：" + response);
            JsonNode jsonNode = objectMapper.readValue(response, JsonNode.class);
            if (StringUtils.isNotBlank(jsonNode.path("openid").asText())) {
                return jsonNode.path("openid").asText();
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public RespInfo ImportEmpsByWechart(UserInfo userInfo){

        if (null == userInfo) {
            return new RespInfo(ErrorEnum.E_001.getCode(), ErrorEnum.E_001.getMsg());
        }

        List<Integer> rootDepart = new ArrayList<Integer>();
        String s = importDeptByWeChart(userInfo.getUserid(),rootDepart);
        int result=0;
        if ("0".equals(s)) {
            result = importEmpListByWeChart(userInfo.getUserid(), org.apache.commons.lang.StringUtils.join(rootDepart.toArray(), ","));
            if (result>0) {
//
            } else {
                SysLog.error("import employee failed errorcode="+result+" :"+userInfo.getUserPrintInfo());
                return new RespInfo(ErrorEnum.E_671.getCode(), ErrorEnum.E_671.getMsg());
            }
        } else {
            SysLog.error("import depatment failed errorcode="+s+" :"+userInfo.getUserPrintInfo());
            return new RespInfo(ErrorEnum.E_670.getCode(), ErrorEnum.E_670.getMsg());
        }

        return new RespInfo(ErrorEnum.E_0.getCode(), ErrorEnum.E_0.getMsg(),result);
    }



    @Override
    public RespInfo ImportEmpsByFeishu(UserInfo userInfo){

        if (null == userInfo) {
            return new RespInfo(ErrorEnum.E_001.getCode(), ErrorEnum.E_001.getMsg());
        }

        String s = importDeptByFeiShu(userInfo.getUserid());
        int result=0;
        if ("0".equals(s)) {
            result = importEmpListByFeiShu(userInfo.getUserid());
            if (result>0) {
//
            } else {
                SysLog.error("import employee failed errorcode="+result+" :"+userInfo.getUserPrintInfo());
                return new RespInfo(ErrorEnum.E_671.getCode(), ErrorEnum.E_671.getMsg());
            }
        } else {
            SysLog.error("import depatment failed errorcode="+s+" :"+userInfo.getUserPrintInfo());
            return new RespInfo(ErrorEnum.E_670.getCode(), ErrorEnum.E_670.getMsg());
        }

        return new RespInfo(ErrorEnum.E_0.getCode(), ErrorEnum.E_0.getMsg(),result);
    }

    @Override
    public RespInfo ImportEmpsByDD(UserInfo userInfo){

        if (null == userInfo) {
            return new RespInfo(ErrorEnum.E_001.getCode(), ErrorEnum.E_001.getMsg());
        }

        List<Department> deptlist = getDeptFromDD(userInfo);
        if(0 == deptlist.size()){
            return new RespInfo(ErrorEnum.E_670.getCode(), ErrorEnum.E_670.getMsg());
        }
        int result = addEmpFromDD(userInfo, deptlist);
        if (result == 0) {
            return new RespInfo(ErrorEnum.E_671.getCode(), ErrorEnum.E_671.getMsg());
        }

        return new RespInfo(ErrorEnum.E_0.getCode(), ErrorEnum.E_0.getMsg(),result);
    }


    /**
     * 同步企业微信组织架构
     * @param userid
     * @param rootDepart
     * @return
     */
    private String importDeptByWeChart(int userid, List<Integer> rootDepart) {
        UserInfo ui = userDao.getUserInfo(userid);
        List<Department> deptlist = new ArrayList<Department>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("同步部门开始：" + ui.getUserid() + "时间：" + sdf.format(new Date()));
        Map<String, String> params = new HashMap<String, String>();
        CacheManager cm = CacheManager.getInstance();
        if (null == cm.getToken("WeChartToken_" + userid) || "".equals(cm.getToken("WeChartToken_" + userid))) {
            settoken(cm, ui);
        }
        String url = "https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=ACCESS_TOKEN&id=ID";
        try {
            String requrl = url.replace("ACCESS_TOKEN", cm.getToken("WeChartToken_" + userid));
            String response = HttpClientUtil.invokeGet(requrl, params, "UTF-8", 5000);
            if(null == response){
                return "-4";
            }
            ObjectMapper mapper = JacksonJsonUtil.getMapperInstance(false);
            JsonNode rootNode = mapper.readValue(response, JsonNode.class);
            String errcode = rootNode.path("errcode").asText();
            if ("0".equals(errcode) && null != rootNode.path("department")) {
                JsonNode result = rootNode.path("department");
                Iterator<JsonNode> it = result.iterator();
                while (it.hasNext()) {
                    JsonNode jn = it.next();
                    Department d = new Department();
                    d.setStrDeptId(jn.path("id").asInt()+"");
                    d.setDeptName(jn.path("name").asText());
                    d.setStrParentId(jn.path("parentid").asInt()+"");
                    deptlist.add(d);
                }
                if (deptlist.isEmpty()) {
                    System.out.println("部门为空");
                    return "-1";
                }

                ImportDepartment(ui,deptlist);
                System.out.println("同步部门完毕：" + ui.getUserid() + "时间：" + sdf.format(new Date()));

                //查找根节点部门
                boolean hasRoot = false;
                for (Department parentDepartment : deptlist) {
                    hasRoot = false;
                    for (Department department : deptlist) {
                        if (parentDepartment.getParentId() == department.getDeptid()) {
                            hasRoot = true;
                            break;
                        }
                    }
                    if (!hasRoot) {
                        parentDepartment.setParentId(0);
                        rootDepart.add(Integer.parseInt(parentDepartment.getStrDeptId()));
                    }
                }
                return "0";
            } else if ("40001".equals(errcode) || "42001".equals(errcode) || "42002".equals(errcode) || "40014".equals(errcode)) {
                Map<String, Object> param = new HashMap<>();
                for (Map.Entry<String, String> value : params.entrySet()) {
                    param.put(value.getKey(), value.getValue());
                }
                checkresult(cm, ui, response, url, param);
                return "0";
            } else {
                return errcode;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ErrorEnum.E_2000.getCode() + "";
        }
    }

    /**
     * 同步企业微信人员信息及组织架构关系
     * @param userid
     * @param deptid
     * @return 返回同步员工的数量
     */
    private int importEmpListByWeChart(int userid, String deptid) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        UserInfo ui = userDao.getUserInfo(userid);
        List<Employee> importmplist = new ArrayList<Employee>();

        /**
         * 获取企业微信token
         */
        Map<String, String> params = new HashMap<String, String>();
        CacheManager cm = CacheManager.getInstance();
        if (null == cm.getToken("WeChartToken_" + userid) || "".equals(cm.getToken("WeChartToken_" + userid))) {
            settoken(cm, ui);
        }
        System.out.println("同步员工开始：" + ui.getUserid() + "时间：" + sdf.format(new Date()));

        String[] deptids = deptid.split(",");
        try {
            String fetch_child = "1";    //是否递归获取子部门下面的成员：1-递归获取，0-只获取本部门
            String access_token = cm.getToken("WeChartToken_" + userid);
            for (int i = 0; i < deptids.length; i++) {
                String url = "https://qyapi.weixin.qq.com/cgi-bin/user/list?access_token=ACCESS_TOKEN&department_id=" + deptids[i] + "&fetch_child=" + fetch_child;
                String reqUrl = url.replace("ACCESS_TOKEN", access_token);
                String response = HttpClientUtil.invokeGet(reqUrl, params, "UTF-8", 5000);
                if (null == response) {
                    return -4;
                }

                ObjectMapper mapper = JacksonJsonUtil.getMapperInstance(false);
                JsonNode rootNode = mapper.readValue(response, JsonNode.class);
                String errcode = rootNode.path("errcode").asText();

                if ("40001".equals(errcode) || "42001".equals(errcode) || "42002".equals(errcode) || "40014".equals(errcode)) {
                    Map<String, Object> param = new HashMap<>();
                    for (Map.Entry<String, String> value : params.entrySet()) {
                        param.put(value.getKey(), value.getValue());
                    }
                    checkresult(cm, ui, response, url, param);
                    return 0;
                } else if (!"0".equals(errcode) || null == rootNode.path("userlist")) {
                    SysLog.error("同步员工 errcode=" + errcode);
                    return -3;
                }

                JsonNode result = rootNode.path("userlist");
                Iterator<JsonNode> it = result.iterator();
                while (it.hasNext()) {
                    JsonNode jn = it.next();

                    Employee e = new Employee();
                    e.setUserid(ui.getUserid());
                    e.setOpenid(jn.path("userid").asText());
                    e.setEmpName(jn.path("name").asText());
                    e.setEmpEmail(jn.path("email").asText());
                    e.setEmpPhone(jn.path("mobile").asText());
                    e.setAvatar(jn.path("avatar").asText());
                    SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
                    String startDate = sd.format(new Date());
                    int endDate = Integer.parseInt(startDate) + 300000;
                    e.setStartDate(startDate);
                    e.setEndDate(String.valueOf(endDate));
                    if (1 == jn.path("gender").asInt()) {
                        e.setEmpSex("男");
                    } else if (2 == jn.path("gender").asInt()) {
                        e.setEmpSex("女");
                    } else {
                        e.setEmpSex("未定义");
                    }
                    e.setEmpPosition(jn.path("position").asText());
                    e.setTelephone(jn.path("telephone").asText());
                    e.setEmpRtxAccount(jn.path("userid").asText());

                    //部门列表
                    List<String> oDepList = new ArrayList<>();
                    for (int d = 0; d < jn.path("department").size(); d++) {
                        oDepList.add(jn.path("department").get(d).asText());
                    }
                    e.setODeptidList(oDepList);

                    importmplist.add(e);
                }

            }

            return ImportEmpList(ui, importmplist);
        } catch (IOException e) {
            SysLog.error(e);
            return (0 - ErrorEnum.E_2000.getCode());
        }
    }


    private String importDeptByFeiShu(int userid) {
        UserInfo ui = userDao.getUserInfo(userid);
        List<Department> deptlist = new ArrayList<Department>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("同步部门开始：" + ui.getUserid() + "时间：" + sdf.format(new Date()));
        Map<String, String> params = new HashMap<String, String>();
        CacheManager cm = CacheManager.getInstance();
        if (null == cm.getToken("FsToken_" + userid) || "".equals(cm.getToken("FsToken_" + userid))) {
            setFsToken(cm, ui);
        }
        int i = 100001;
        String url = "https://open.feishu.cn/open-apis/contact/v3/departments";
        params.put("fetch_child", "true");
        params.put("department_id_type", "open_department_id");
        try {
            String response = HttpClientUtil.invokeGetForFs(url, params, "UTF-8", 5000, cm.getToken("FsToken_" + userid));

            ObjectMapper mapper = JacksonJsonUtil.getMapperInstance(false);

            while (response != null) {
                JsonNode rootNode = mapper.readValue(response, JsonNode.class);
                String errcode = rootNode.path("code").asText();
                if (!"0".equals(errcode) || null == rootNode.path("data")) {
                    return "-1";
                }
                JsonNode result = rootNode.path("data").path("items");
                Iterator<JsonNode> it = result.iterator();
                while (it.hasNext()) {
                    JsonNode jn = it.next();
                    Department d = new Department();
                    d.setStrDeptId(jn.path("open_department_id").asText());
                    d.setDeptName(jn.path("name").asText());
                    d.setStrParentId(jn.path("parent_department_id").asText());
                    d.setUserid(ui.getUserid());
                    deptlist.add(d);
                }
                response = null;
                if (rootNode.path("data").path("has_more").asBoolean()) {
                    params.put("page_size", "50");
                    params.put("page_token", rootNode.path("data").path("page_token").asText());
                    response = HttpClientUtil.invokeGetForFs(url, params, "UTF-8", 5000, cm.getToken("FsToken_" + userid));
                }
            }


            if (deptlist.isEmpty()) {
                System.out.println("部门为空");
                return "-1";
            }

            ImportDepartment(ui,deptlist);
            System.out.println("同步部门完毕：" + ui.getUserid() + "时间：" + sdf.format(new Date()));
            return "0";

        } catch (IOException e) {
            e.printStackTrace();
            return "-1";
        }
    }


    private int importEmpListByFeiShu(int userid) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        UserInfo ui = userDao.getUserInfo(userid);

        List<Employee> importmplist = new ArrayList<Employee>();//导入的员工列表


        /**
         * 获取企业微信token
         */
        Map<String, String> params = new HashMap<String, String>();
        CacheManager cm = CacheManager.getInstance();
        if (null == cm.getToken("FsToken_" + userid) || "".equals(cm.getToken("FsToken_" + userid))) {
            setFsToken(cm, ui);
        }
        System.out.println("同步员工开始：" + ui.getUserid() + "时间：" + sdf.format(new Date()));

        String access_token = cm.getToken("FsToken_" + userid);
        String url = "https://open.feishu.cn/open-apis/contact/v3/users";
        params.put("user_id_type", "open_id");
        params.put("department_id_type", "open_department_id");
        try {
            String response = HttpClientUtil.invokeGetForFs(url, params, "UTF-8", 5000, access_token);
            ObjectMapper mapper = JacksonJsonUtil.getMapperInstance(false);
            if(response == null){
                return -1;
            }

            while (response != null) {
                JsonNode rootNode = mapper.readValue(response, JsonNode.class);
                String errcode = rootNode.path("code").asText();
                if (!"0".equals(errcode) || null == rootNode.path("data")) {
                    return -1;
                }

                JsonNode result = rootNode.path("data").path("items");
                Iterator<JsonNode> it = result.iterator();
                while (it.hasNext()) {
                    JsonNode jn = it.next();
                    Employee e = new Employee();
                    e.setUserid(ui.getUserid());
                    e.setOpenid(jn.path("open_id").asText());
                    e.setEmpName(jn.path("name").asText());
                    e.setEmpEmail(jn.path("email").asText());
                    e.setEmpPhone(jn.path("mobile").asText().replace("+86", ""));
                    e.setAvatar(jn.path("avatar").path("avatar_origin").asText());
                    SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
                    String startDate = sd.format(new Date());
                    int endDate = Integer.parseInt(startDate) + 300000;
                    e.setStartDate(startDate);
                    e.setEndDate(String.valueOf(endDate));
                    if (1 == jn.path("gender").asInt()) {
                        e.setEmpSex("男");
                    } else if (2 == jn.path("gender").asInt()) {
                        e.setEmpSex("女");
                    } else {
                        e.setEmpSex("保密");
                    }

                    //部门列表
                    List<String> oDepList = new ArrayList<>();
                    for (int d = 0; d < jn.path("department_ids").size(); d++) {
                        oDepList.add(jn.path("department_ids").get(d).asText());
                    }
                    e.setODeptidList(oDepList);

                    importmplist.add(e);
                }

                //获取更多
                response = null;
                if (rootNode.path("data").path("has_more").asBoolean()) {
                    params.put("page_size", "50");
                    params.put("page_token", rootNode.path("data").path("page_token").asText());
                    response = HttpClientUtil.invokeGetForFs(url, params, "UTF-8", 5000, cm.getToken("FsToken_" + userid));
                }
            }

            return ImportEmpList(ui, importmplist);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 全量同步组织架构
     * 	 strDeptId:原系统中的部门id
     * 	 strParentId：原系统中的父节点id
     * @param ui
     * @param deptlist
     * @return
     */
    @Override
    public int ImportDepartment(UserInfo ui, List<Department> deptlist){
        //查出旧部门
        List<Department> oldDep = departmentDao.getAllDepartmentList(ui.getUserid());
        Map<String, Department> oldDepMap = new HashMap<String, Department>();
        for(Department department:oldDep){
            oldDepMap.put(department.getStrDeptId(),department);
        }

        Map<String, Integer> pmap = new HashMap<String, Integer>();//外部部门id和id之间的映射关系
        int i = 100001;
        for(Department d:deptlist){
            d.setDeptid(i++);
            d.setUserid(ui.getUserid());
            if(oldDepMap.get(d.getStrDeptId()) != null){
                d.setDeptManagerEmpid(oldDepMap.get(d.getStrDeptId()).getDeptManagerEmpid());
            }
            pmap.put(d.getStrDeptId(), d.getDeptid());
        }
        departmentDao.delAllDepartment(ui.getUserid());

        for (int d = 0; d < deptlist.size(); d++) {
            if (StringUtils.isEmpty(deptlist.get(d).getStrParentId())
                    || pmap.get(deptlist.get(d).getStrParentId()) == null) {
                deptlist.get(d).setParentId(0);
            } else {
                deptlist.get(d).setParentId(pmap.get(deptlist.get(d).getStrParentId()));
            }
        }

        return departmentDao.addDepartmentList(deptlist);
    }

    /**
     * 全量同步importmplist中的数据到数据库
     * 注意点：导入的员工信息一定要设置外部部门id，需要通过外部部门id建立关系，设置方法-setODeptidList
     * @param ui
     * @param importmplist 要导入的人员列表
     * @return
     */
    @Override
    public int ImportEmpList(UserInfo ui, List<Employee> importmplist) {
        /**
         * 获取门禁组
         */
        EquipmentGroup eg = new EquipmentGroup();
        eg.setUserid(ui.getUserid());
        eg.setReqEtype("(0,1)");
        List<EquipmentGroup> eglist = equipmentGroupDao.getEquipmentGroupByUserid(eg);
        List<Integer> egids = eglist.stream().map(EquipmentGroup::getEgid).collect(Collectors.toList());
        String egidStr = UtilTools.listToString(egids);

        /**
         * 获取老员工列表
         */
        List<Employee> oldemplist =  employeeDao.getOldEmployeeList(ui.getUserid());


        List<Employee> emplist = new ArrayList<Employee>();
        List<Employee> newemplist = new ArrayList<Employee>();
        Map<String, Integer> map = new HashMap<String, Integer>();//存放openid和empid
        Map<String, Object> delmap = new HashMap<String, Object>();//openid和empid映射关系,需要更新的员工
        for (int k = 0; k < oldemplist.size(); k++) {
            map.put(oldemplist.get(k).getOpenid(), oldemplist.get(k).getEmpid());
            personInfoService.delInvitePersonByOpenid(oldemplist.get(k).getOpenid());
            personInfoService.delInvitePerson(oldemplist.get(k).getEmpPhone());
        }
        for(Employee employee:importmplist) {
            employee.setUserid(ui.getUserid());
            if(StringUtils.isNotEmpty(employee.getEmpPhone())){
                employee.setEmpPhone(employee.getEmpPhone().replace(" ","").replaceFirst("^[^1]*86[^1]*", "").trim());
            }
            if (null != map.get(employee.getOpenid())) {
                employee.setEmpid((int) map.get(employee.getOpenid()));
                delmap.put(employee.getOpenid(), employee.getEmpid());
                emplist.add(employee);
            } else {
                employee.setEgids(egidStr);
                newemplist.add(employee);
            }
        }


        if (newemplist.isEmpty() && emplist.isEmpty()) {
            System.out.println("没有员工数据");
            return 0;
        }

        List<Employee> empRellist = new ArrayList<Employee>();//人员组织架构关系
        List<Integer> empids = new ArrayList<Integer>();
        List<String> avatars = new ArrayList<String>();
        for (int s = 0; s < oldemplist.size(); s++) {
            if (!delmap.isEmpty() &&( null == delmap.get(oldemplist.get(s).getOpenid())
                    || oldemplist.get(s).getEmpid() != (Integer) delmap.get(oldemplist.get(s).getOpenid()))
                    &&oldemplist.get(s).getEmpType()!=2){
                empids.add(oldemplist.get(s).getEmpid());
                if (StringUtils.isNotBlank(oldemplist.get(s).getAvatar())) {
                    avatars.add(oldemplist.get(s).getAvatar());
                }
            }

            if(oldemplist.get(s).getEmpType()==2){
                //保留组织架构关系
                SysLog.info("保留组织架构关系："+oldemplist.get(s));
                List<Department> depts = departmentDao.getDeptByEmpid(oldemplist.get(s).getEmpid(), oldemplist.get(s).getUserid());
                if(depts.size()!=0){
                    for(Department department:depts){
                        oldemplist.get(s).setDeptid(department.getDeptid());
                        empRellist.add(oldemplist.get(s));
                    }
                }
            }
        }

        //自动同步时不删除头像
        if(avatars.size()>0) {
            //new Thread(new DelFileThread(avatars, personInfoService)).start();
        }

        if (empids.size() > 0) {
            HashOperations hashOperations = redisTemplate.opsForHash();
            List<Employee> employees = oldemplist.stream().filter(employee1 -> empids.contains(employee1.getEmpid())).collect(Collectors.toList());
            employeeDao.batchDelEmployee(empids);
            for (int i = 0; i < employees.size(); i++) {
                if (StringUtils.isNotEmpty(employees.get(i).getOpenid()) && hashOperations.hasKey(employees.get(i).getOpenid(), "token")) {
                    if (hashOperations.hasKey(hashOperations.get(employees.get(i).getOpenid(), "token"), "id")) {
                        hashOperations.delete(hashOperations.get(employees.get(i).getOpenid(), "token"), "id");
                    }
                    hashOperations.delete(employees.get(i).getOpenid(), "token");
                }
            }
        }
        if (emplist.size() > 0) {
            employeeDao.batchUpdateEmpName(emplist);
            for (Employee employee : emplist) {
                Person person = new Person();
                person.setPname(employee.getEmpName());
                person.setAvatar(employee.getAvatar());
                person.setPmobile(employee.getEmpPhone().replace("+86", ""));
                person.setPemail(employee.getEmpEmail());
                person.setPopenid(employee.getOpenid());
                person.setPcompany(employee.getCompanyName());
                person.setUserid(employee.getUserid());
                if ("男".equals(employee.getEmpSex())) {
                    person.setSex(1);
                } else if ("女".equals(employee.getEmpSex())) {
                    person.setSex(0);
                } else {
                    person.setSex(-1);
                }
                person.setFace(employee.getFace());
                try {
                    personInfoService.addInvitePerson(person);
                } catch (Exception e) {
                    continue;
                }
            }
        }
        if (!newemplist.isEmpty()) {
            employeeDao.addEmployees(newemplist);
            for (Employee employee : newemplist) {
                Person person = new Person();
                person.setPname(employee.getEmpName());
                person.setAvatar(employee.getAvatar());
                person.setPmobile(employee.getEmpPhone().replace("+86", ""));
                person.setPemail(employee.getEmpEmail());
                person.setPopenid(employee.getOpenid());
                person.setPcompany(employee.getCompanyName());
                person.setUserid(employee.getUserid());
                if ("男".equals(employee.getEmpSex())) {
                    person.setSex(1);
                } else if ("女".equals(employee.getEmpSex())) {
                    person.setSex(0);
                } else {
                    person.setSex(-1);
                }
                person.setFace(employee.getFace());
                try {
                    personInfoService.addInvitePerson(person);
                } catch (Exception e) {
                    continue;
                }
            }
        }

        //创建组织架构关系
        oldemplist = employeeDao.getOldEmployeeList(ui.getUserid());
        List<Employee> empidAdepid = new ArrayList<Employee>();//员工和部门关系
        Map<String,String> depMap = new HashMap<>();
        map.clear();
        for(Employee employee:oldemplist){
            map.put(employee.getOpenid(), employee.getEmpid());
        }
        /**
         * 获取组织架构
         */
        List<Department> deptlist = new ArrayList<Department>();
        deptlist = departmentDao.getAllDepartmentList(ui.getUserid());
        Map<String, Integer> pmap = new HashMap<String, Integer>();
        for (int k = 0; k < deptlist.size(); k++) {
            pmap.put(deptlist.get(k).getStrDeptId(), deptlist.get(k).getDeptid());
        }


        Integer empid = 0;
        Integer depid = null;
        for(Employee employee:importmplist){
            for(String odepid:employee.getODeptidList()){
                Employee e = new Employee();
                e.setUserid(ui.getUserid());
                empid = map.get(employee.getOpenid());
                if(empid == null){
                    continue;
                }
                e.setEmpid(empid);
                depid = pmap.get(odepid);
                if(depid == null){
                    continue;
                }
                e.setDeptid(depid);

                if(null != depMap.get(employee.getEmpid()+"&"+employee.getDeptid())){
                    //员工部门关系已存在
                    continue;
                }
                depMap.put(employee.getEmpid()+"&"+employee.getDeptid(),"");

                empRellist.add(e);
            }
        }


        if (!empRellist.isEmpty()) {
            departmentDao.delRelationByUserid(ui.getUserid());
            departmentDao.addDeptEmpRelation(empRellist);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        UserInfo uinfo = new UserInfo();
        uinfo.setUserid(ui.getUserid());
        uinfo.setRefreshDate(new Date());
        uinfo.setRefreshCount(0);
        userDao.updateRefreshDate(uinfo);
        System.out.println("同步完毕：" + ui.getUserid() + "时间：" + sdf.format(new Date()));

        //同步信息展示
        EmpUtils.addEmpSyncMsg(redisTemplate, Constant.EMP_SYNC + ui.getUserid(), "date", "number", LocalDateTime.now(), emplist.size() + newemplist.size());

        return emplist.size() + newemplist.size();
    }





    @Override
    public Employee getEmployeeByempRtxAccount(String WchartUserId, int userid) {
        return employeeDao.getEmployeeByempRtxAccount(WchartUserId, userid);
    }

    @Override
    public Employee getEmpInfoByDDid(String openid, int userid) {
        return employeeDao.getEmpInfoByDDid(openid, userid);
    }

    /**
     * 通过code获取小程序openid
     *
     * @param code
     * @return
     */
    @Override
    public String getWxAppletsOpenIdByCode(String code) {
        //1.获取请求的url
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code";
        try {
            String requrl = url
                    .replace("APPID", Constant.WeiXin_Applets_Notify.get("APPLETSID"))
                    .replace("SECRET", Constant.WeiXin_Applets_Notify.get("APPLETSSECRET"))
                    .replace("JSCODE", code);
            ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
            Map<String, String> reqParam = new HashMap<>();
            //2.调用接口，发送请求，获取成员
            String response = HttpClientUtil.connectPostHttps(requrl, reqParam);
            System.out.println("微信扫码登录获取openid响应：" + response);
            JsonNode jsonNode = objectMapper.readValue(response, JsonNode.class);
            if (StringUtils.isNotBlank(jsonNode.path("openid").asText())) {
                return jsonNode.path("openid").asText();
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    @Override
    public List<Employee> getLeaders(Employee employee) {
        List<Employee> managers = new ArrayList<>();
        List<Department> deptByEmpids = departmentDao.getDeptByEmpid(employee.getEmpid(), employee.getUserid());
        for(Department department:deptByEmpids){
            //向上递归查找，直到查找到为止
            String deptManagerEmpid = department.getDeptManagerEmpid();
            while (org.apache.commons.lang.StringUtils.isBlank(deptManagerEmpid)) {
                if(department.getParentId() == 0) {
                    break;
                }
                department = departmentDao.getDepartment(department.getParentId(),employee.getUserid());
                if(department == null){
                    break;
                }
                deptManagerEmpid = department.getDeptManagerEmpid();
            }
            if(org.apache.commons.lang.StringUtils.isNotBlank(deptManagerEmpid)) {
                String[] managersStr = deptManagerEmpid.split(",");
                for(int i=0;i<managersStr.length;i++){
                    Employee manager = getEmployee(Integer.parseInt(managersStr[i]));
                    if (null != manager) {
                        managers.add(manager);
                    }
                }

            }
        }
        return managers;
    }


}
