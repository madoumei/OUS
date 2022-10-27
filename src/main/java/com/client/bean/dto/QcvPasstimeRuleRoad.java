package com.client.bean.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName qcv_passtime_rule_road
 */
@TableName(value ="qcv_passtime_rule_road")
@Data
public class QcvPasstimeRuleRoad implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer rid;

    /**
     * 
     */
    private Integer userid;

    /**
     * 
     */
    private String rname;

    /**
     * 
     */
    private Date startdate;

    /**
     * 
     */
    private Date enddate;

    /**
     * 
     */
    private String mon;

    /**
     * 
     */
    private String tues;

    /**
     * 
     */
    private String wed;

    /**
     * 
     */
    private String thur;

    /**
     * 
     */
    private String fri;

    /**
     * 
     */
    private String sat;

    /**
     * 
     */
    private String sun;

    /**
     * 
     */
    private String hol;

    /**
     * 
     */
    private String daysoff;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        QcvPasstimeRuleRoad other = (QcvPasstimeRuleRoad) that;
        return (this.getRid() == null ? other.getRid() == null : this.getRid().equals(other.getRid()))
            && (this.getUserid() == null ? other.getUserid() == null : this.getUserid().equals(other.getUserid()))
            && (this.getRname() == null ? other.getRname() == null : this.getRname().equals(other.getRname()))
            && (this.getStartdate() == null ? other.getStartdate() == null : this.getStartdate().equals(other.getStartdate()))
            && (this.getEnddate() == null ? other.getEnddate() == null : this.getEnddate().equals(other.getEnddate()))
            && (this.getMon() == null ? other.getMon() == null : this.getMon().equals(other.getMon()))
            && (this.getTues() == null ? other.getTues() == null : this.getTues().equals(other.getTues()))
            && (this.getWed() == null ? other.getWed() == null : this.getWed().equals(other.getWed()))
            && (this.getThur() == null ? other.getThur() == null : this.getThur().equals(other.getThur()))
            && (this.getFri() == null ? other.getFri() == null : this.getFri().equals(other.getFri()))
            && (this.getSat() == null ? other.getSat() == null : this.getSat().equals(other.getSat()))
            && (this.getSun() == null ? other.getSun() == null : this.getSun().equals(other.getSun()))
            && (this.getHol() == null ? other.getHol() == null : this.getHol().equals(other.getHol()))
            && (this.getDaysoff() == null ? other.getDaysoff() == null : this.getDaysoff().equals(other.getDaysoff()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getRid() == null) ? 0 : getRid().hashCode());
        result = prime * result + ((getUserid() == null) ? 0 : getUserid().hashCode());
        result = prime * result + ((getRname() == null) ? 0 : getRname().hashCode());
        result = prime * result + ((getStartdate() == null) ? 0 : getStartdate().hashCode());
        result = prime * result + ((getEnddate() == null) ? 0 : getEnddate().hashCode());
        result = prime * result + ((getMon() == null) ? 0 : getMon().hashCode());
        result = prime * result + ((getTues() == null) ? 0 : getTues().hashCode());
        result = prime * result + ((getWed() == null) ? 0 : getWed().hashCode());
        result = prime * result + ((getThur() == null) ? 0 : getThur().hashCode());
        result = prime * result + ((getFri() == null) ? 0 : getFri().hashCode());
        result = prime * result + ((getSat() == null) ? 0 : getSat().hashCode());
        result = prime * result + ((getSun() == null) ? 0 : getSun().hashCode());
        result = prime * result + ((getHol() == null) ? 0 : getHol().hashCode());
        result = prime * result + ((getDaysoff() == null) ? 0 : getDaysoff().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", rid=").append(rid);
        sb.append(", userid=").append(userid);
        sb.append(", rname=").append(rname);
        sb.append(", startdate=").append(startdate);
        sb.append(", enddate=").append(enddate);
        sb.append(", mon=").append(mon);
        sb.append(", tues=").append(tues);
        sb.append(", wed=").append(wed);
        sb.append(", thur=").append(thur);
        sb.append(", fri=").append(fri);
        sb.append(", sat=").append(sat);
        sb.append(", sun=").append(sun);
        sb.append(", hol=").append(hol);
        sb.append(", daysoff=").append(daysoff);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}