package com.client.bean.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName qcv_logistics
 */
@TableName(value = "qcv_logistics")
@Data
public class QcvLogistics implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
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


    private String appointmentstartdate;

    /**
     *
     */
    private String appointmentenddate;

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
    private String openid;

    private String loadtime;

    /**
     *
     */
    private String remark;

    private String otherextend;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
