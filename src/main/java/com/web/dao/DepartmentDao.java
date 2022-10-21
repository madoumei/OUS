package com.web.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.web.bean.Department;
import com.web.bean.Employee;


@Mapper
public interface DepartmentDao {
    public int addDepartment(Department dept);

    public int addDeptEmpRelation(List<Employee> emplist);

    public List<Department> getAllDepartmentList(@Param("userid") int userid);

    public List<Department> getDepartmentList(@Param("userid") int userid);

    public List<Department> getChildDeptList(@Param("deptid") int deptid, @Param("userid") int userid);

    public int updateDepartment(Department dept);

    public int delDepartment(@Param("deptid") int deptid, @Param("userid") int userid);

    public int delRelationByDept(@Param("deptid") int deptid, @Param("userid") int userid);

    public int delRelationByEmp(@Param("empid") int empid);

    public int updateEmpDept(Employee emp);

    public List<Department> getDeptByEmpid(@Param("empid") int empid, @Param("userid") int userid);

    public int batchDelRelationByEmp(List<Integer> empids);

    public Department getChildDeptCount(Department dept);

    public Department getDepartment(@Param("deptid") int deptid, @Param("userid") int userid);

    public int addDepartmentList(List<Department> deptlist);

    public int delAllDepartment(@Param("userid") int userid);

    public int delRelationByUserid(@Param("userid") int userid);

    public Department getDepartmentByDeptName(Department dept);

}
