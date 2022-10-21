package com.web.bean;

import com.client.bean.Visitor;
import com.config.qicool.common.persistence.BaseEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ProcessRecord extends BaseEntity<ProcessRecord> {
	private static final long serialVersionUID = -8591578763575538392L;
	private String rid;
	private String vid;
	private int aid;
	private int userid;
	private int subEmpId;
	private String subEmpName;
	private String rvwEmpId;
	private String  rvwEmpName;
	private Date submitTime;
	private int level;
	@ApiModelProperty(value="审批状态，0=待提交，1=通过，2=拒绝，3=退回，4=撤回，5=取消")
	private int status;
	private String remark;
	private String vname;
	private String vcompany;
	private String visitType;
	private String vremark;
	private Date appTime;
	@JsonProperty("pType")
	private int pType;//1邀请 2预约
	private List<Appointment> appList;
	private Visitor vt;
	private String taskName;//审批流节点名称
	private String taskid;
	private String processInstanceId;

}
