package com.web.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.web.bean.Department;
import com.web.bean.Employee;
import com.web.bean.RequestEmp;



@Mapper
public interface EmployeeDao {
	public List<Employee> getEmployeeList(RequestEmp rep);
	
	public Employee getEmployee(@Param("empid") int empid);
	
	public int addEmployee(Employee emp);
	
	public int updateEmployee(Employee emp);
	
	public int delEmployee(@Param("empid")int empid);
	
	public int batchDelEmployee(List<Integer> empids);
	
	public int delEmployees(@Param("userid")int userid);
	
	public int delEmployeesByUserid(@Param("userid")int userid);
	
	public int addEmployees(List<Employee> emplist);
	
	public int addEmployeesbydd(List<Employee> emplist);
	
	public int batchUpdateEmployees(List<Employee> emplist);
	
	public List<Employee> getEmployeeByEmail(@Param("empEmail")String empEmail,@Param("userid")int userid);
	
	public Employee getOpenid(@Param("empid")int empid);
	
	public List<Employee> getOldEmployeeList(@Param("userid")int userid);
	
	public int bindingOpenid(Employee emp); 
	
	public int UpdateDefaultNotify(List<Employee> emplist);
	
	public List<Employee> getDefalutNotify(Map<String,Object> map);
	
	public int getEmployeeCount(@Param("userid") int userid);
	
	public List<Employee> getEmpListByName(@Param("userid")int userid,@Param("empName")String empName);
	
	public int  quickbindingOpenid(Employee emp);
	
	public int quickbindingOpenidbyemail(Employee emp);
	
	public int bindingOpenidByPhone(Employee emp);
	
	public List<Employee> getEmpListByOpenid(@Param("openid")String openid);
	
    public List<Integer> getEmpIdByOpenid(@Param("openid")String openid);
    
    public int resetOpenid(Employee emp);
    
    public Employee getSendUrlEmp(Employee emp);
    
    public List<Employee> getSubAccountEmpList(@Param("userid")int userid,@Param("subaccountId")int subaccountId);
    
    public int delSAEmployees(@Param("subaccountId")int subaccountId);
    
    public int updateEmpSubAccount(List<Employee> emplist);
    
    public int getRelAccEmpCount(Map<String,String> conditions);
    
    public List<Employee> checkEmployeeExists(@Param("userid")int userid,@Param("empPhone")String empPhone);

    public List<Employee> checkEmployeeExistsStrict(RequestEmp emp);

	Employee checkEmployeeExistsByRtxAccount(@Param("empRtxAccount")String empRtxAccount);
    
    public  List<Employee> getEmployeebyPhone(@Param("empPhone") String empPhone,@Param("openid") String openid,@Param("userid") int userid);
    
    public List<Employee>  getEmpInfo(@Param("userid") int userid,@Param("empPhone") String empPhone);
    
    public int updateEmpPwd (Employee emp);
    
    public List<Employee>  getEmployeePassword(@Param("phone") String phone);
    
	public List<Employee> getEmpDeptList(RequestEmp rep);
	
	public List<Employee> getEmpDeptByUserid(@Param("userid") int userid);
	
	public List<Employee> getEmpListPages(RequestEmp emp);
	
	public List<Employee> getDeptManager(Map<String,String> conditions);
	
	public List<Employee> getEmpRoleList(RequestEmp emp);
	
	public List<Employee> getDefaultEmpList(RequestEmp emp);
	
	public  Employee  getEmpByDDid(Employee emp);
	
	public List<Employee> getEmpListByempName(RequestEmp reqemp);

	public List<Employee> getEmpListByKey(RequestEmp reqemp);

	public int addEmployeesNoInc(List<Employee> emplist);
	
	public List<Employee> getEmployeesByEmpids(List<Integer> empids);
	
	public int updateEmpFace(Employee emp);
	
	public int updateEmpAvatar(Employee emp);
	
	public Employee getEmployeeByCardNo(Employee emp);
	
	public Employee getEmployeebyPlateNum(Employee emp);
	
	public int batchUpdateEmpName(List<Employee> emplist);
	
	public int batchUpdateEmpAvatar(List<Employee> emplist);
	
	public List<Employee> getEmpByFaceResult(RequestEmp emp);
	
	public int updateEmpEgids(Employee emp);
	
	public List<Employee>  getSubEmpListPages(RequestEmp emp);

    int batchUpdateEmpEgids(List<Employee> repEmp);

	List<Employee> searchEmpByCondition(Map<String, String> searchMap);

	Employee getEmployeeByempRtxAccount(@Param("wchartUserId") String wchartUserId, @Param("userid")int userid);

    Employee getEmpInfoByDDid(@Param("ddid")String ddid, @Param("userid")int userid);
}
