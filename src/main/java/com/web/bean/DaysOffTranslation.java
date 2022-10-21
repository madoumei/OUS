package com.web.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class DaysOffTranslation {
    private String did;
    private int userid;
    private Date date;
    private String remark;
    private int rid;
}
