package com.uplus.sdcontent.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Payload {
    private String content_id;
    private String content_name;
    private String url;
}
