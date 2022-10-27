package com.web.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.client.bean.dto.QcvLogistics;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@TableName("qcv_passtime_rule")
@ApiModel("PassRule 通行策略Bean")
public class PassRule {

	@ApiModelProperty("rid")
	@TableField(value = "rid")
	private int rid;
	@ApiModelProperty("")
	@TableField(value = "userid")
	private int userid;
	@ApiModelProperty("策略名称")
	@TableField(value = "rname")
	private String rname;
	@ApiModelProperty("策略规开始时间")
	@TableField(value = "startDate")
	private String startDate;
	@ApiModelProperty("策略规则结束时间")
	@TableField(value = "endDate")
	private String endDate;
	@ApiModelProperty("周一通行时间")
	@TableField(value = "mon")
	private String mon;
	@ApiModelProperty("周二通行时间")
	@TableField(value = "tues")
	private String tues;
	@ApiModelProperty("周三通行时间")
	@TableField(value = "wed")
	private String wed;
	@ApiModelProperty("周四通行时间")
	@TableField(value = "thur")
	private String thur;
	@ApiModelProperty("周五通行时间")
	@TableField(value = "fri")
	private String fri;
	@ApiModelProperty("周六通行时间")
	@TableField(value = "sat")
	private String sat;
	@ApiModelProperty("周日通行时间")
	@TableField(value = "sun")
	private String sun;
	@ApiModelProperty("节假日通行时间")
	@TableField(value = "hol")
	private String hol;
	@ApiModelProperty("调休通行时间")
	@TableField(value = "daysOff")
	private String daysOff;
	@ApiModelProperty("调休假期")
	@TableField(exist = false)
	private List<Holiday> hList;

	@ApiModelProperty("调休工作日")
	@TableField(exist = false)
	private List<DaysOffTranslation> daysOffTranslations;

	public int getRid() {
		return rid;
	}

	public void setRid(int rid) {
		this.rid = rid;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getRname() {
		return rname;
	}

	public void setRname(String rname) {
		this.rname = rname;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getMon() {
		return mon;
	}

	public void setMon(String mon) {
		this.mon = mon;
	}

	public String getTues() {
		return tues;
	}

	public void setTues(String tues) {
		this.tues = tues;
	}

	public String getWed() {
		return wed;
	}

	public void setWed(String wed) {
		this.wed = wed;
	}

	public String getThur() {
		return thur;
	}

	public void setThur(String thur) {
		this.thur = thur;
	}

	public String getFri() {
		return fri;
	}

	public void setFri(String fri) {
		this.fri = fri;
	}

	public String getSat() {
		return sat;
	}

	public void setSat(String sat) {
		this.sat = sat;
	}

	public String getSun() {
		return sun;
	}

	public void setSun(String sun) {
		this.sun = sun;
	}

	public String getHol() {
		return hol;
	}

	public void setHol(String hol) {
		this.hol = hol;
	}

	public String getDaysOff() {
		return daysOff;
	}

	public void setDaysOff(String daysOff) {
		this.daysOff = daysOff;
	}

	public List<Holiday> gethList() {
		return hList;
	}

	public void sethList(List<Holiday> hList) {
		this.hList = hList;
	}

	public List<DaysOffTranslation> getDaysOffTranslations() {
		return daysOffTranslations;
	}

	public void setDaysOffTranslations(List<DaysOffTranslation> daysOffTranslations) {
		this.daysOffTranslations = daysOffTranslations;
	}
}
