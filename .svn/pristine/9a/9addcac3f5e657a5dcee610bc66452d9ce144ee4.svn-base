package com.web.service.impl;

import com.web.bean.Department;
import com.web.bean.Employee;
import com.web.dao.DepartmentDao;
import com.web.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service("departmentService")
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentDao departmentDao;

    @Override
    public int addDepartment(Department dept) {
        // TODO Auto-generated method stub
        return departmentDao.addDepartment(dept);
    }

    @Override
    public int addDeptEmpRelation(List<Employee> emplist) {
        // TODO Auto-generated method stub
        return departmentDao.addDeptEmpRelation(emplist);
    }

    @Override
    public List<Department> getDepartmentList(int userid) {
        // TODO Auto-generated method stub
        return departmentDao.getDepartmentList(userid);
    }

    @Override
    public int updateDepartment(Department dept) {
        // TODO Auto-generated method stub
        return departmentDao.updateDepartment(dept);
    }

    @Override
    public int delDepartment(int deptid, int userid) {
        // TODO Auto-generated method stub
        return departmentDao.delDepartment(deptid, userid);
    }

    @Override
    public int delRelationByDept(int deptid, int userid) {
        // TODO Auto-generated method stub
        return departmentDao.delRelationByDept(deptid, userid);
    }

    @Override
    public int delRelationByEmp(int empid) {
        // TODO Auto-generated method stub
        return departmentDao.delRelationByEmp(empid);
    }

    @Override
    public int updateEmpDept(Employee emp) {
        // TODO Auto-generated method stub
        return departmentDao.updateEmpDept(emp);
    }

    @Override
    public List<Department> getChildDeptList(int deptid, int userid) {
        // TODO Auto-generated method stub
        return departmentDao.getChildDeptList(deptid, userid);
    }

    @Override
    public List<Department> getDeptByEmpid(int empid, int userid) {
        // TODO Auto-generated method stub
        return departmentDao.getDeptByEmpid(empid, userid);
    }

    @Override
    public int batchDelRelationByEmp(List<Integer> empids) {
        // TODO Auto-generated method stub
        return departmentDao.batchDelRelationByEmp(empids);
    }

    @Override
    public Department getChildDeptCount(Department dept) {
        // TODO Auto-generated method stub
        return departmentDao.getChildDeptCount(dept);
    }

    @Override
    public Department getDepartment(int deptid, int userid) {
        // TODO Auto-generated method stub
        return departmentDao.getDepartment(deptid, userid);
    }

    @Override
    public int addDepartmentList(List<Department> deptlist) {
        // TODO Auto-generated method stub
        return departmentDao.addDepartmentList(deptlist);
    }

    @Override
    public int delAllDepartment(int userid) {
        // TODO Auto-generated method stub
        return departmentDao.delAllDepartment(userid);
    }

    @Override
    public int delRelationByUserid(int userid) {
        // TODO Auto-generated method stub
        return departmentDao.delRelationByUserid(userid);
    }

    @Override
    public Department getDepartmentByDeptName(Department dept) {
        // TODO Auto-generated method stub
        return departmentDao.getDepartmentByDeptName(dept);
    }

    @Override
    public List<Department> getAllDepartmentList(int userid) {
        // TODO Auto-generated method stub
        return departmentDao.getAllDepartmentList(userid);
    }


    @Override
    public String deptTreeNameByEmpId(Department department) {
    	return deptTreeByDeptid(department.getDeptName(),department);
    }

    /**
     * 获取所有部门集合，并按树形结构封装
     * @param allDeptList
     * @return
     */
    @Override
    public List<Department> allDeptTreeList(int userid) {
        List<Department> allDeptList = departmentDao.getAllDepartmentList(userid);
        List<Department> root = allDeptList.stream()
                .filter(department -> department.getParentId() == 0)
                .map((department)->{
                    department.setChildDeptList(getChildDeptTree(department,allDeptList));
                    return department;
                }).collect(Collectors.toList());
        return root;
    }

    private List<Department> getChildDeptTree(Department root,List<Department> all){
        List<Department> collect = all.stream()
                .filter(department -> root.getDeptid() == department.getParentId())
                .map((department) -> {
                    department.setChildDeptList(getChildDeptTree(department, all));
                    return department;
                }).collect(Collectors.toList());
        return collect;
    }


    @Override
    public Department getDeptChildTreeList(Department department) {
        List<Department> allDepartmentList = departmentDao.getAllDepartmentList(department.getUserid());
        List<Department> childDeptTree = getChildDeptTree(department, allDepartmentList);
        department.setChildDeptList(childDeptTree);
        return department;
    }

	private String deptTreeByDeptid(String deptName,Department level_1){
		StringBuffer sb = new StringBuffer(deptName);
		if (0 == level_1.getParentId()){
			return sb.toString();
		}else {
			Department department = departmentDao.getDepartment(level_1.getParentId(), level_1.getUserid());
			sb.insert(0,department.getDeptName()+"/");
			return deptTreeByDeptid(sb.toString(),department);
		}
	}

    @Override
    public Set<Integer> getchildDeptIds(int deptId,Set<Integer> deptids,List<Department> allDeptList) {
        deptids.add(deptId);
        for (Department department : allDeptList) {
            if (deptId == department.getParentId()){
                deptids.add(department.getDeptid());
                getchildDeptIds(department.getDeptid(),deptids,allDeptList);
            }
        }
        return  deptids;
    }
}
