package com.web.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@ApiModel("Manager 管理员Bean")
public class Manager {
	@ApiModelProperty("用户id")
	private int userid;
	@ApiModelProperty("账号")
	private String account;
	@ApiModelProperty("密码")
	private String password;
	@ApiModelProperty("类型")
	private int type;
	@ApiModelProperty("名称")
	private String sname;
	@ApiModelProperty("公司")
	private String company;
	@ApiModelProperty("手机")
	private String moblie;
	@ApiModelProperty("开始时间")
	private Date startDate;
	@ApiModelProperty("结束时间")
	private Date endDate;
	@ApiModelProperty("账户类型： 0-hse；1-劳务公司；2-前台；3-物管；4-访客机账号；5-子账号；")
	private int sType;
	@ApiModelProperty("门岗id")
	private String gid;
	@ApiModelProperty("备注")
	private String remark;
	@ApiModelProperty("找回密码验证码")
	private String digest;
	@ApiModelProperty("账号类型集合")
	private List<Integer> stList;
	@ApiModelProperty("管理员角色")
	private String manageRole;
	@ApiModelProperty("授权模块")
	private List<Permission> module;
	@ApiModelProperty("登录账号")
	private String loginAccount;
	@ApiModelProperty("入驻企业id")
	private int subAccountId;
	@ApiModelProperty("创建时间")
	private Date createTime;
	private String parentCompany;
	@ApiModelProperty("旧密码")
	private String oldPwd;
	@ApiModelProperty("邮箱")
	private String email;

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getSname() {
		return sname;
	}

	public void setSname(String sname) {
		this.sname = sname;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getMoblie() {
		return moblie;
	}

	public void setMoblie(String moblie) {
		this.moblie = moblie;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getsType() {
		return sType;
	}

	public void setsType(int sType) {
		this.sType = sType;
	}

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public List<Integer> getStList() {
		return stList;
	}

	public void setStList(List<Integer> stList) {
		this.stList = stList;
	}

	public String getManageRole() {
		return manageRole;
	}

	public void setManageRole(String manageRole) {
		this.manageRole = manageRole;
	}

	public List<Permission> getModule() {
		return module;
	}

	public void setModule(List<Permission> module) {
		this.module = module;
	}

	public String getLoginAccount() {
		return loginAccount;
	}

	public void setLoginAccount(String loginAccount) {
		this.loginAccount = loginAccount;
	}

	public int getSubAccountId() {
		return subAccountId;
	}

	public void setSubAccountId(int subAccountId) {
		this.subAccountId = subAccountId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getParentCompany() {
		return parentCompany;
	}

	public void setParentCompany(String parentCompany) {
		this.parentCompany = parentCompany;
	}

	public String getOldPwd() {
		return oldPwd;
	}

	public void setOldPwd(String oldPwd) {
		this.oldPwd = oldPwd;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
