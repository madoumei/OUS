package com.web.bean;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@ApiModel("Role 角色Bean")
public class Role {
	private int rid;
	private int empid;
	private int userid;
	private String rgName;
	private int parentId;
	private List<Role> childRoleList;
}
