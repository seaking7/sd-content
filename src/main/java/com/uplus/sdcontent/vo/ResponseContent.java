package com.uplus.sdcontent.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ResponseContent {
    private String contentId;
    private String contentName;
    private String url;
    private String creator;
    private String cp;
    private String category;
    private Date insertDate;
}
