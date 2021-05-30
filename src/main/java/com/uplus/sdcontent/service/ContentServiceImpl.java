package com.uplus.sdcontent.service;

import com.uplus.sdcontent.client.ExecuteServiceClient;
import com.uplus.sdcontent.client.ReportServiceClient;
import com.uplus.sdcontent.client.UserServiceClient;
import com.uplus.sdcontent.dto.ContentDto;
import com.uplus.sdcontent.jpa.ContentEntity;
import com.uplus.sdcontent.jpa.ContentRepository;
import com.uplus.sdcontent.jpa.JdbcContentRepository;
import com.uplus.sdcontent.vo.RequestDeleteContent;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContentServiceImpl implements ContentService{

    ContentRepository contentRepository;
    JdbcContentRepository jdbcContentRepository;
    //Feign Client
    ExecuteServiceClient executeServiceClient;
    ReportServiceClient reportServiceClient;
    UserServiceClient userServiceClient;

    CircuitBreakerFactory circuitBreakerFactory;

    @Autowired
    public ContentServiceImpl(ContentRepository contentRepository, JdbcContentRepository jdbcContentRepository,
                              ExecuteServiceClient executeServiceClient, ReportServiceClient reportServiceClient,
                              UserServiceClient userServiceClient, CircuitBreakerFactory circuitBreakerFactory) {
        this.contentRepository = contentRepository;
        this.jdbcContentRepository = jdbcContentRepository;
        this.executeServiceClient = executeServiceClient;
        this.reportServiceClient = reportServiceClient;
        this.userServiceClient = userServiceClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }


    @Override
    public Iterable<ContentEntity> getContentsAll() {
        return contentRepository.findAll();
    }

    @Override
    public void createContent(ContentDto contentDto) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        ContentEntity contentEntity = mapper.map(contentDto, ContentEntity.class);
        contentRepository.save(contentEntity);
    }

    @Override
    public ContentDto getContentsByContentId(String contendId) {
        ContentDto contentDto = jdbcContentRepository.findByContentId(contendId).get();
        return contentDto;
    }

    @Override
    public void deleteByContentId(String contentId) {
        jdbcContentRepository.deleteByContentId(contentId);


        RequestDeleteContent requestDeleteContent = new RequestDeleteContent();
        requestDeleteContent.setContentId(contentId);

        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
        circuitBreaker.run(() -> executeServiceClient.deleteExecute(requestDeleteContent),
                throwable -> true);

        circuitBreaker.run(() -> reportServiceClient.deleteReport(requestDeleteContent),
                throwable -> true);

    }
}
