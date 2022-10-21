package com.client.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@TableName("qcv_eqpt")
@ApiModel("Equipment 设备Bean")
public class Equipment {
	@TableId(value = "eid", type = IdType.AUTO)
	private int eid;
	@TableField("userid")
	private int userid;
	@TableField("deviceName")
	private String deviceName;
	@TableField("deviceCode")
	private String deviceCode;
	@TableField("extendCode")
	private String extendCode;
	@TableField("roomNum")
	private String roomNum;
	@TableField("deviceIp")
	private String deviceIp;
	@TableField("devicePort")
	private int devicePort;
	@TableField("deviceQrcode")
	private String deviceQrcode;
	@TableField("status")
	private int status;
	@TableField(exist = false)
	private List<Integer> egids;
	@TableField(exist = false)
	private Integer count;
	@TableField(exist = false)
	private Integer egid;


	@ApiModelProperty(value = "eType QRDT表示二维码设备，CAHK表示摄像头海康设备 CAHM表示车牌摄像头")
	@TableField(value = "eType")
	private String eType;
	@TableField(exist = false)
	private String account;

	@TableField(exist = false)
	private String loginAccount;
	@ApiModelProperty(value = "进门出门状态： 1：进门2 出门")
	@TableField(value = "enterStatus")
	private int enterStatus;
	@TableField(value = "onlineStatus")
	private int onlineStatus;

	public String getRoomNum() {
		return roomNum;
	}

	public void setRoomNum(String roomNum) {
		this.roomNum = roomNum;
	}

	public String getDeviceIp() {
		return deviceIp;
	}

	public void setDeviceIp(String deviceIp) {
		this.deviceIp = deviceIp;
	}

	public int getDevicePort() {
		return devicePort;
	}

	public void setDevicePort(int devicePort) {
		this.devicePort = devicePort;
	}

	public String getDeviceQrcode() {
		return deviceQrcode;
	}

	public void setDeviceQrcode(String deviceQrcode) {
		this.deviceQrcode = deviceQrcode;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public List<Integer> getEgids() {
		return egids;
	}

	public void setEgids(List<Integer> egids) {
		this.egids = egids;
	}

	public String geteType() {
		return eType;
	}

	public void seteType(String eType) {
		this.eType = eType;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getLoginAccount() {
		return loginAccount;
	}

	public void setLoginAccount(String loginAccount) {
		this.loginAccount = loginAccount;
	}

	public int getEnterStatus() {
		return enterStatus;
	}

	public void setEnterStatus(int enterStatus) {
		this.enterStatus = enterStatus;
	}

	public int getOnlineStatus() {
		return onlineStatus;
	}

	public void setOnlineStatus(int onlineStatus) {
		this.onlineStatus = onlineStatus;
	}

	public int getEid() {
		return eid;
	}

	public void setEid(int eid) {
		this.eid = eid;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceCode() {
		return deviceCode;
	}

	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}

	public String getExtendCode() {
		return extendCode;
	}

	public void setExtendCode(String extendCode) {
		this.extendCode = extendCode;
	}

	public Integer getEgid() {
		return egid;
	}

	public void setEgid(Integer egid) {
		this.egid = egid;
	}
}
