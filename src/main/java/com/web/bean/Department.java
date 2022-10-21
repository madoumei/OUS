package com.web.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@ApiModel("Department 部门Bean")
public class Department {

	@ApiModelProperty("外部节点id")
	@NotBlank(message = "strDeptId不能为空")
	@Size(max = 150,message = "strDeptId最大长度150")
	private String strDeptId;

	@ApiModelProperty("外部父节点id")
	@Size(max = 150,message = "strParentId最大长度150")
	private String strParentId;

	@ApiModelProperty("部门名称")
	@NotBlank(message = "deptName不能为空")
	@Size(max = 50,message = "deptName最大长度50")
	private String deptName;

	private int userid;
	@ApiModelProperty("部门id")
	private int deptid;
	@ApiModelProperty("部门父节点id")
	private int parentId;

	@ApiModelProperty("部门主管id")
	private String deptManagerEmpid;
	@ApiModelProperty(value = "管理员详情")
	private List<Employee> deptManager;
	private int empCount;
	private List<Department> childDeptList;
	private String defaultReceiver;
}
