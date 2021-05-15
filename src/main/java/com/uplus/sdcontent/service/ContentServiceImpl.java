package com.uplus.sdcontent.service;

import com.uplus.sdcontent.dto.ContentDto;
import com.uplus.sdcontent.jpa.ContentEntity;
import com.uplus.sdcontent.jpa.ContentRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContentServiceImpl implements ContentService{

    ContentRepository contentRepository;

    @Autowired
    public ContentServiceImpl(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    @Override
    public Iterable<ContentEntity> getContentsAll() {
        return contentRepository.findAll();
    }

    @Override
    public ContentDto createContent(ContentDto contentDto) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        ContentEntity contentEntity = mapper.map(contentDto, ContentEntity.class);
        contentRepository.save(contentEntity);

        return null;
    }
}
