package com.client.bean;


import com.config.qicool.common.persistence.BaseEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class ReqVR extends BaseEntity<VehicleRecord> {

    /**
     *
     */
    private static final long serialVersionUID = 3097309599528800791L;

    private int userid;
    private String plateNum;
    private String deviceCode;
    private String deviceName;
    private String startDate;
    private String endDate;
    private String name;
    private String gid;
    private List<Integer> vehList;
    private int startIndex;// 开始行数
    private int requestedCount;// 请求条数

}
