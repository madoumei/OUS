package com.client.bean;

import java.util.Date;

import com.config.qicool.common.persistence.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utils.jsonUtils.JacksonJsonUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


@ApiModel(description = "访客邀请信息")
public class RespVisitor  extends BaseEntity {
	private static final long serialVersionUID = -6211470284322813058L;
	@ApiModelProperty(value = "id",notes = "访客id")
	private int vid;
	private int empid;
	@ApiModelProperty(value = "被访人部门id")
	private int empdeptid;
	@ApiModelProperty(value = "被访人部门名称")
	private String empDeptName;
	private String empName;
	private String empPhone;
	private String vname;
	private String vemail;
	private String vphone;
	private String vphoto;
	@ApiModelProperty(value = "拜访类型",example = "商务#Business")
	private String visitType;
	private Date visitdate;
	private int peopleCount;
	@ApiModelProperty(value = "邀约类型",allowableValues = "1=邀请,2=预约",example = "1")
	private String SigninType;
	@ApiModelProperty(value = "扩展字段，记录数据表格中没有字段，比如可以自定义字段",example = "{\"access\":\"1266,1132\",\"qrcodeConf\":\"1\",\"remark\":\"测试授权\"}")
	private String extendCol;
	@ApiModelProperty(value = "预约授权状态,0=未授权,1=已授权,2=拒绝,3=已取消",allowableValues = "0=未授权,1=已授权,2=拒绝,3=已取消",example = "0")
	private int permission;
	@ApiModelProperty(value = "预约时间",example = "2021-09-14 14:00:00")
	private Date appointmentDate;
	@ApiModelProperty(value = "多企业服务模式时，入驻企业账号id")
	private int subaccountId;
	@ApiModelProperty(value = "证件号",example = "320xxxxx4692")
	private String cardId;
	private Date signOutDate;
	@ApiModelProperty(value = "被访人所在公司")
	private String company;
	private String startDate;
	private String endDate;
	@ApiModelProperty(value = "邀请函状态,跟踪访客的行为,0=未签到,1=已签到,2=已打开,3=接受,4=拒绝",allowableValues = "0=未签到,1=已签到,2=已打开,3=接受,4=拒绝",example = "0")
	private int status;
	private String remark;
	@ApiModelProperty(value = "只有随访人员有此数据，为主预约人的vid",example = "17")
	private String vgroup;
	@ApiModelProperty(value = "虚拟字段，表示当前数据是否为一个合并的团队，0=个人，1=团队")
	private int isGroup;
	@ApiModelProperty(value = "访客公司")
	private String vcompany;
	@ApiModelProperty(value = "随访人员名字，中间用','号隔开",example = "Jake,Tony")
	private String memberName;
	private String encryption;
	private String signInOpName;
	private String signOutOpName;
	private String signInGate;
	private String signOutGate;
	@ApiModelProperty(value = "结束拜访时间")
	private Date leaveTime;
	@ApiModelProperty(value = "会议id")
	private int mid;//对应qcv_meeting表
	@ApiModelProperty(value = "手写签名文件名称")
	private String signPdf;
	@ApiModelProperty(value = "访客类型id")
	private int tid;//访客类型id,对应qcv_visitor_type表
	@ApiModelProperty(value = "访客类型",example ="普通访客#Normal Visitor")
	private String vType;
	private int appid;
	@ApiModelProperty(value = "车牌号")
	private String plateNum;
	private String vtExtendCol;
	private String meetingPoint;
	private int userid;
	@ApiModelProperty(value = "门岗id")
	private String gid;
	@ApiModelProperty(value = "门岗名称")
	private String gname;
	@ApiModelProperty(value = "有效期时长")
	private int qrcodeConf;
	@ApiModelProperty(value = "有效期类型 0.天数 1.次数",allowableValues = "0=天数,1=次数",example ="0")
	private int qrcodeType;
	@ApiModelProperty(value = "性别,0=女性,1=男性",allowableValues = "0=女性,1=男性",example ="1")
	private String sex;
	@ApiModelProperty("访客卡号")
	private String cardNo;
	@ApiModelProperty(value = "预约途径,0-前台 1-小程序/微信 2-邀请函 3-前台 4-访客机",allowableValues = "0,1,2,3,4",example ="1" )
	private Integer clientNo;//0-前台 1-小程序/微信 2-邀请函 3-前台 4-访客机
	@ApiModelProperty("访客卡发放人")
	private String cardOpName;
	@ApiModelProperty("拜访楼层")
	private String floors;//拜访楼层
	@ApiModelProperty("邀请预约数据自动创建时间")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date createTime;//邀请预约数据自动创建时间
	@ApiModelProperty("调查问卷分数")
	private int overallScore;
	@ApiModelProperty("调查问卷详细信息")
	private String scoreDetail;
	@ApiModelProperty("健康申报信息")
	private String healthDeclaration;

	public int getVid() {
		return vid;
	}

	public void setVid(int vid) {
		this.vid = vid;
	}

	public int getEmpid() {
		return empid;
	}

	public void setEmpid(int empid) {
		this.empid = empid;
	}

	public int getEmpdeptid() {
		return empdeptid;
	}

	public void setEmpdeptid(int empdeptid) {
		this.empdeptid = empdeptid;
	}

	public String getEmpDeptName() {
		return empDeptName;
	}

	public void setEmpDeptName(String empDeptName) {
		this.empDeptName = empDeptName;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getEmpPhone() {
		return empPhone;
	}

	public void setEmpPhone(String empPhone) {
		this.empPhone = empPhone;
	}

	public String getVname() {
		return vname;
	}

	public void setVname(String vname) {
		this.vname = vname;
	}

	public String getVemail() {
		return vemail;
	}

	public void setVemail(String vemail) {
		this.vemail = vemail;
	}

	public String getVphone() {
		return vphone;
	}

	public void setVphone(String vphone) {
		this.vphone = vphone;
	}

	public String getVphoto() {
		return vphoto;
	}

	public void setVphoto(String vphoto) {
		this.vphoto = vphoto;
	}

	public String getVisitType() {
		return visitType;
	}

	public void setVisitType(String visitType) {
		this.visitType = visitType;
	}

	public Date getVisitdate() {
		return visitdate;
	}

	public void setVisitdate(Date visitdate) {
		this.visitdate = visitdate;
	}

	public int getPeopleCount() {
		return peopleCount;
	}

	public void setPeopleCount(int peopleCount) {
		this.peopleCount = peopleCount;
	}

	public String getSigninType() {
		return SigninType;
	}

	public void setSigninType(String signinType) {
		SigninType = signinType;
	}

	public String getExtendCol() {
		return extendCol;
	}

	public void setExtendCol(String extendCol) {
		this.extendCol = extendCol;
	}

	public int getPermission() {
		return permission;
	}

	public void setPermission(int permission) {
		this.permission = permission;
	}

	public Date getAppointmentDate() {
		return appointmentDate;
	}

	public void setAppointmentDate(Date appointmentDate) {
		this.appointmentDate = appointmentDate;
	}

	public int getSubaccountId() {
		return subaccountId;
	}

	public void setSubaccountId(int subaccountId) {
		this.subaccountId = subaccountId;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public Date getSignOutDate() {
		return signOutDate;
	}

	public void setSignOutDate(Date signOutDate) {
		this.signOutDate = signOutDate;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getVgroup() {
		return vgroup;
	}

	public void setVgroup(String vgroup) {
		this.vgroup = vgroup;
	}

	public String getVcompany() {
		return vcompany;
	}

	public void setVcompany(String vcompany) {
		this.vcompany = vcompany;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getEncryption() {
		return encryption;
	}

	public void setEncryption(String encryption) {
		this.encryption = encryption;
	}

	public String getSignInOpName() {
		return signInOpName;
	}

	public void setSignInOpName(String signInOpName) {
		this.signInOpName = signInOpName;
	}

	public String getSignOutOpName() {
		return signOutOpName;
	}

	public void setSignOutOpName(String signOutOpName) {
		this.signOutOpName = signOutOpName;
	}

	public String getSignInGate() {
		return signInGate;
	}

	public void setSignInGate(String signInGate) {
		this.signInGate = signInGate;
	}

	public String getSignOutGate() {
		return signOutGate;
	}

	public void setSignOutGate(String signOutGate) {
		this.signOutGate = signOutGate;
	}

	public Date getLeaveTime() {
		return leaveTime;
	}

	public void setLeaveTime(Date leaveTime) {
		this.leaveTime = leaveTime;
	}

	public int getMid() {
		return mid;
	}

	public void setMid(int mid) {
		this.mid = mid;
	}

	public String getSignPdf() {
		return signPdf;
	}

	public void setSignPdf(String signPdf) {
		this.signPdf = signPdf;
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public String getvType() {
		return vType;
	}

	public void setvType(String vType) {
		this.vType = vType;
	}

	public int getAppid() {
		return appid;
	}

	public void setAppid(int appid) {
		this.appid = appid;
	}

	public String getPlateNum() {
		return plateNum;
	}

	public void setPlateNum(String plateNum) {
		this.plateNum = plateNum;
	}

	public String getVtExtendCol() {
		return vtExtendCol;
	}

	public void setVtExtendCol(String vtExtendCol) {
		this.vtExtendCol = vtExtendCol;
	}

	public String getMeetingPoint() {
		return meetingPoint;
	}

	public void setMeetingPoint(String meetingPoint) {
		this.meetingPoint = meetingPoint;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public int getQrcodeConf() {
		return qrcodeConf;
	}

	public void setQrcodeConf(int qrcodeConf) {
		this.qrcodeConf = qrcodeConf;
	}

	public int getQrcodeType() {
		return qrcodeType;
	}

	public void setQrcodeType(int qrcodeType) {
		this.qrcodeType = qrcodeType;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public Integer getClientNo() {
		return clientNo;
	}

	public void setClientNo(Integer clientNo) {
		this.clientNo = clientNo;
	}

	public String getCardOpName() {
		return cardOpName;
	}

	public void setCardOpName(String cardOpName) {
		this.cardOpName = cardOpName;
	}

	public String getFloors() {
		return floors;
	}

	public void setFloors(String floors) {
		this.floors = floors;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public int getOverallScore() {
		return overallScore;
	}

	public void setOverallScore(int overallScore) {
		this.overallScore = overallScore;
	}

	public String getScoreDetail() {
		return scoreDetail;
	}

	public void setScoreDetail(String scoreDetail) {
		this.scoreDetail = scoreDetail;
	}

	public String getHealthDeclaration() {
		return healthDeclaration;
	}

	public void setHealthDeclaration(String healthDeclaration) {
		this.healthDeclaration = healthDeclaration;
	}

	public int getIsGroup() {
		if(permission==0
		||permission==4
		||permission==5){
			if(peopleCount>1){
				return 1;
			}
		}
		return 0;
	}

	public String getExtendValue(String key) {
		if (extendCol == null) {
			return null;
		}
		ObjectMapper instance = JacksonJsonUtil.getMapperInstance(false);
		try {
			JsonNode jsonNode = instance.readValue(extendCol, JsonNode.class);
			if (null != jsonNode
					&& (jsonNode.get(key) != null)) {
				return jsonNode.get(key).asText();
			}
		} catch (Exception e) {

		}
		return null;
	}
}
