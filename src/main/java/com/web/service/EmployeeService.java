package com.web.service;

import java.util.List;
import java.util.Map;

import com.web.bean.*;

public interface EmployeeService {
	public List<Employee> getEmployeeList(RequestEmp rep);

	Employee getEmployee(int empid);

	public int addEmployee(Employee emp);
	
	public int updateEmployee(Employee emp);
	
	public int delEmployee(int empid);
	
	public List<Employee> getEmployeeByEmail(String empEmail,int userid);
	
	public int batchDelEmployee(List<Integer> empids);
	
	public int delEmployees(int userid);
	
	public int delEmployeesByUserid(int userid);
	
	public int addEmployees(List<Employee> emplist);
	
	public int addEmployeesbydd(List<Employee> emplist);
	
	public int batchUpdateEmployees(List<Employee> emplist);
	
	public Employee getOpenid(int empid);
	
	public List<Employee> getOldEmployeeList(int userid);
	
	public int bindingOpenid(Employee emp); 
	
	public int UpdateDefaultNotify(List<Employee> emplist);
	
	public List<Employee> getDefalutNotify(Map<String,Object> map);
	
	public int getEmployeeCount(int userid);

	public List<Employee> getEmpListByName(int userid,String name);
	
	public int  quickbindingOpenid(Employee emp);
	
	public int quickbindingOpenidbyemail(Employee emp);
	
	public int bindingOpenidByPhone(Employee emp);
	
	public List<Employee> getEmpListByOpenid(String openid);
	
	public List<Integer> getEmpIdByOpenid(String openid);
	
	public int resetOpenid(Employee emp);
	
	public Employee getSendUrlEmp(Employee emp);
	 
	public List<Employee> getSubAccountEmpList(int userid,int subaccountId);
	
	public int delSAEmployees(int subaccountId);
	
	public int  updateEmpSubAccount(List<Employee> emplist);
	
	public int getRelAccEmpCount(Map<String,String> conditions);
	
	public  List<Employee> checkEmployeeExists(int userid,String empPhone);

	public  List<Employee> checkEmployeeExistsStrict(RequestEmp empPhone);

	public  Employee checkEmployeeExistsByRtxAccount(String empRtxAccount);

	public  List<Employee> getEmployeebyPhone(String empPhone,String openid,int userid);
	
	public List<Employee>  getEmpInfo(int userid,String empPhone);
	  
	public int updateEmpPwd (Employee emp);
	
	public List<Employee>  getEmployeePassword(String phone);
	
	public List<Employee> getEmpDeptList(RequestEmp rep);
	
	public List<Employee> getEmpDeptByUserid(int userid);
	
	public List<Employee> getEmpListPages(RequestEmp emp);
	
	public List<Employee> getDeptManager(Map<String,String> conditions);
	
	public List<Employee> getEmpRoleList(RequestEmp emp);
	
	public  Employee  getEmpByDDid(Employee emp);

	/**
	 * 访客专用，通过员工姓名模糊搜索员工信息，为了安全，最多返回20条数据，所以必须提供足够的条件
	 * @param reqemp
	 * @return empid,empName
	 */
	public List<Employee> getEmpListByempName(RequestEmp reqemp);

	/**
	 * 通过条件模糊搜索员工信息，最多返回100条数据
	 * @param reqemp
	 * @return
	 */
	public List<Employee> getEmpListByKey(RequestEmp reqemp);


	public int addEmployeesNoInc(List<Employee> emplist);
	
	public List<Employee> getEmployeesByEmpids(List<Integer> empids);
	
	public int updateEmpFace(Employee emp);
	
	public int updateEmpAvatar(Employee emp);
	
	public Employee getEmployeeByCardNo(Employee emp);
	
	public List<Employee> getDefaultEmpList(RequestEmp emp);
	
	public Employee getEmployeebyPlateNum(Employee emp);
	
	public int batchUpdateEmpName(List<Employee> emplist);
	
	public int batchUpdateEmpAvatar(List<Employee> emplist);
	
	public List<Employee> getEmpByFaceResult(RequestEmp emp);
	
	public int updateEmpEgids(Employee emp);
	
	public List<Employee>  getSubEmpListPages(RequestEmp emp);

    int batchUpdateEmpEgids(List<Employee> repEmp);

    List<Employee> searchEmpByCondition(Map<String, String> searchMap);

	public String getWchartUserIdByCode(int userid,String code);

	int ImportDepartment(UserInfo ui, List<Department> deptlist);

	int ImportEmpList(UserInfo ui, List<Employee> importmplist);

    Employee getEmployeeByempRtxAccount(String WchartUserId, int userid);

	Employee getEmpInfoByDDid(String openid, int userid);

    String getWxOpenIdByCode(String code);
    
    public String getFeiShuOpenIdByCode(int userid, String code);

	String getWxAppletsOpenIdByCode(String code);

	/**
	 * 同步企业微信组织架构及人员
	 * @param userInfo
	 * @return
	 */
	public RespInfo ImportEmpsByWechart(UserInfo userInfo);

	/**
	 * 同步飞书组织架构及人员
	 * @param userInfo
	 * @return
	 */
	public RespInfo ImportEmpsByFeishu(UserInfo userInfo);

	/**
	 * 同步钉钉组织架构及人员
	 * @param userInfo
	 * @return
	 */
	public RespInfo ImportEmpsByDD(UserInfo userInfo);

    List<Employee> getLeaders(Employee employee);
}
