package com.web.bean;

import lombok.Getter;
import lombok.Setter;

public class ProcessRule {
	private int rid;
	private int aid;
	private int level;
	private int role;
	private String roleName;
	private int rType;


	public int getRid() {
		return rid;
	}

	public void setRid(int rid) {
		this.rid = rid;
	}

	public int getAid() {
		return aid;
	}

	public void setAid(int aid) {
		this.aid = aid;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public int getrType() {
		return rType;
	}

	public void setrType(int rType) {
		this.rType = rType;
	}
}
