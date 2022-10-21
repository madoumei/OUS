package com.web.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.web.bean.Department;
import com.web.bean.Employee;

public interface DepartmentService {
	public  int addDepartment(Department dept);
	
	public  int addDeptEmpRelation(List<Employee> emplist);
	
	public  List<Department> getDepartmentList(int userid);
	
	public  List<Department> getAllDepartmentList(int userid);
	
	public  List<Department> getChildDeptList(int deptid,int userid);
	
	public  int updateDepartment(Department dept);
	
	public  int delDepartment(int deptid,int userid);
	
	public  int delRelationByDept(int deptid,int userid);
	
	public  int delRelationByEmp(int empid);
	
	public  int updateEmpDept(Employee emp);
	
	public  List<Department> getDeptByEmpid(int empid,int userid);
	
	public  int batchDelRelationByEmp(List<Integer> empids);
	
	public Department getChildDeptCount(Department dept);
	
	public Department getDepartment(int deptid,int userid);
	
	public int  addDepartmentList(List<Department> deptlist);
	
	public int  delAllDepartment(int userid);
	
	public int delRelationByUserid(int userid);
	
	public Department getDepartmentByDeptName(Department dept);

	String deptTreeNameByEmpId(Department department);

	List<Department> allDeptTreeList(int userid);

	Department getDeptChildTreeList(Department department);

	Set<Integer> getchildDeptIds(int deptId, Set<Integer> deptids ,List<Department> allDeptList);

}
