package com.uplus.sdcontent.vo;

import lombok.Data;

@Data
public class RequestDeleteContent {
    private String contentId;
    private String contentName;
    private String url;
}
