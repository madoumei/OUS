package com.client.bean;


import com.config.qicool.common.persistence.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ApiModel("EquipmentGroupResp 门禁组Bean")
public class EquipmentGroupResp extends BaseEntity<EquipmentGroupResp> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5602489423010474260L;
	private int eid;
	private int egid;
	private int userid;
	private String egname;
	private int status;
	@ApiModelProperty(value = "设备类型")
	private String etype;
	private String reqEtype;
	private String gids;
	private String account;
	private String loginAccount;
    private int startIndex;
    private int requestedCount;
    private Integer enter;
    private Integer out;
    private Integer vehicleEnter;
    private Integer vehicleOut;
    private String startDate;
    private String endDate;
	private Map<String, List<Equipment>> equipmentStatus;
}
