package com.client.bean;

import com.utils.Constant;
import com.web.bean.UserInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;
import java.util.Map;

@ApiModel("OpendoorInfo 开门信息Bean")
public class OpendoorInfo  {
	private int userid;
	@ApiModelProperty("设备编号")
	private String deviceCode;
	@ApiModelProperty("设备名称")
	private String deviceName;
	@ApiModelProperty("姓名")
	private String vname;
	@ApiModelProperty(value = "人员类型,员工,访客,供应商",example = "员工",allowableValues = "员工,访客,供应商")
	private String vtype;
	@ApiModelProperty(value = "所属公司")
	private String company;
	@ApiModelProperty(value = "手机号")
	private String mobile;
	@ApiModelProperty(value = "开门状态 1=成功，其他=错误描述,100字符以内",example = "1")
	private String openStatus;
	@ApiModelProperty(value = "进门时间")
	private Date openDate;
	@ApiModelProperty(value = "进门方向")
	private String direction;
	@ApiModelProperty(value = "所属门岗名字")
	private String gname;
	private String temp;
	@ApiModelProperty(value = "访客预约id")
	private String vid;
	@ApiModelProperty(value = "设备List")
	private Map<String,List<Equipment>> equipmentMap;
	private String photoUrl;
	private String equimentName;
	private String passType;
	private String equimentGroupName;
	private String passWay;

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getDeviceCode() {
		return deviceCode;
	}

	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getVname() {
		return vname;
	}

	public void setVname(String vname) {
		this.vname = vname;
	}

	public String getVtype() {
		return vtype;
	}

	public void setVtype(String vtype) {
		this.vtype = vtype;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getOpenStatus() {
		return openStatus;
	}

	public void setOpenStatus(String openStatus) {
		this.openStatus = openStatus;
	}

	public Date getOpenDate() {
		return openDate;
	}

	public void setOpenDate(Date openDate) {
		this.openDate = openDate;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getGname() {
		return gname;
	}

	public void setGname(String gname) {
		this.gname = gname;
	}

	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
	}

	public String getVid() {
		return vid;
	}

	public void setVid(String vid) {
		this.vid = vid;
	}

	public Map<String, List<Equipment>> getEquipmentMap() {
		return equipmentMap;
	}

	public void setEquipmentMap(Map<String, List<Equipment>> equipmentMap) {
		this.equipmentMap = equipmentMap;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getEquimentName() {
		return equimentName;
	}

	public void setEquimentName(String equimentName) {
		this.equimentName = equimentName;
	}

	public String getPassType() {
		return passType;
	}

	public void setPassType(String passType) {
		this.passType = passType;
	}

	public String getEquimentGroupName() {
		return equimentGroupName;
	}

	public void setEquimentGroupName(String equimentGroupName) {
		this.equimentGroupName = equimentGroupName;
	}

	public String getPassWay() {
		return passWay;
	}

	public void setPassWay(String passWay) {
		this.passWay = passWay;
	}
}
