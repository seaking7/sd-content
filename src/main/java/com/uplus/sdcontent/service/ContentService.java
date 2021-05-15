package com.uplus.sdcontent.service;

import com.uplus.sdcontent.dto.ContentDto;
import com.uplus.sdcontent.jpa.ContentEntity;

public interface ContentService {

    ContentDto createContent(ContentDto contentDto);

    Iterable<ContentEntity> getContentsAll();
}
