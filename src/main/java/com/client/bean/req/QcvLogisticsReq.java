package com.client.bean.req;

import lombok.Data;

import java.util.Date;

@Data
public class QcvLogisticsReq {
    /**
     *
     */
    private Integer sid;

    /**
     *
     */
    private Integer userid;

    /**
     *
     */
    private String dname;

    /**
     *
     */
    private String dmobile;

    /**
     *
     */
    private String dcardid;

    /**
     *
     */
    private String driverextend;

    /**
     *
     */
    private String logextend;

    /**
     *
     */
    private String vehicleextend;

    /**
     *
     */
    private String platenum;

    /**
     *
     */
    private String goodsphoto;

    /**
     *
     */
    private String healthphoto;

    /**
     *
     */
    private String journphoto;

    /**
     *
     */
    private String nucleicphoto;

    /**
     *
     */
    private String logtype;

    /**
     *
     */
    private String company;

    /**
     *
     */
    private String rname;

    /**
     *
     */
    private Date appointmentdate;

    /**
     *
     */
    private Date visitdate;

    /**
     *
     */
    private Date leavetime;

    /**
     *
     */
    private Date finishtime;

    /**
     *
     */
    private Integer pstatus;

    /**
     *
     */
    private String lognum;

    /**
     *
     */
    private String goodsextend;

    /**
     *
     */
    private String remark;

    private String openid;

    private String otherextend;

    private String loadtime;

    private static final long serialVersionUID = 1L;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
