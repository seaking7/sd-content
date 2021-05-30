package com.uplus.sdcontent.service;

import com.uplus.sdcontent.dto.ContentDto;
import com.uplus.sdcontent.jpa.ContentEntity;

public interface ContentService {

    void createContent(ContentDto contentDto);

    Iterable<ContentEntity> getContentsAll();

    ContentDto getContentsByContentId(String contendId);

    void deleteByContentId(String contentId);
}
