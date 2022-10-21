package com.client.bean;

import com.config.qicool.common.persistence.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Training extends BaseEntity<Training> {
    /**
     *
     */
    private static final long serialVersionUID = 3789265939841329891L;
    private String tvid;
    private int userid;
    private String qid;
    private String title;
    private String duration;
    private String uploader;
    private Date uploadTime;
    private String filesize;
    private String videoUrl;
    private int startIndex;
    private int requestedCount;


}
