package com.uplus.sdcontent.controller;

import com.uplus.sdcontent.client.ExecuteServiceClient;
import com.uplus.sdcontent.client.ReportServiceClient;
import com.uplus.sdcontent.client.UserServiceClient;
import com.uplus.sdcontent.dto.ContentDto;
import com.uplus.sdcontent.jpa.ContentEntity;
import com.uplus.sdcontent.messagequeue.ContentProducer;
import com.uplus.sdcontent.messagequeue.KafkaProducer;
import com.uplus.sdcontent.service.ContentService;
import com.uplus.sdcontent.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Slf4j
@Controller
@RequestMapping("/")
public class ContentController {


    private ContentService contentService;
    KafkaProducer kafkaProducer;
    ContentProducer contentProducer;
    private Environment env;

    @Autowired
    public ContentController(ContentService contentService, KafkaProducer kafkaProducer, ContentProducer contentProducer, Environment env) {
        this.contentService = contentService;
        this.kafkaProducer = kafkaProducer;
        this.contentProducer = contentProducer;
        this.env = env;
    }



    @GetMapping("/welcome")
    public String welcome(){
        return "hellotest";
    }

    @GetMapping("/")
    public String contentHome(Model model){
        setEnvModel(model);

        return "home";
    }
    

    @GetMapping("/contents/new")
    public String createContent(Model model){
        setEnvModel(model);
        return "content/createContentForm";
    }

    @PostMapping("/contents/new")
    public String createContent(ContentForm form, Model model){
        System.out.println(form);

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        ContentDto contentDto = mapper.map(form, ContentDto.class);
        contentService.createContent(contentDto);

        /* send this Content to the Kafka */
        //kafkaProducer.send("example-category-topic", contentDto);
        contentProducer.send("contents", contentDto);

        return listContents(model);
    }


    @PostMapping("/contents/delete")
    public String deleteContent(ContentForm form, Model model){

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        ContentDto contentDto = mapper.map(form, ContentDto.class);
        log.info(contentDto.getContentId());
        contentService.deleteByContentId(contentDto.getContentId());

        return listContents(model);
    }

    @GetMapping("/contents")
    public String listContents(Model model){
        Iterable<ContentEntity> contentList = contentService.getContentsAll();
        List<ResponseContent> result = new ArrayList<>();
        contentList.forEach( v -> {
            result.add(new ModelMapper().map(v, ResponseContent.class));
        });
        model.addAttribute("contents", result);
        setEnvModel(model);
        return "content/listContent";
    }

    @GetMapping("/{contentId}/contents")
    public String detailContent(@PathVariable String contentId, Model model){

        log.info(contentId);
        ContentDto contentDto = contentService.getContentsByContentId(contentId);

        model.addAttribute("contentId", contentDto.getContentId());
        model.addAttribute("contentName", contentDto.getContentName());
        model.addAttribute("url", contentDto.getUrl());
        model.addAttribute("creator", contentDto.getCreator());
        model.addAttribute("cp", contentDto.getCp());
        model.addAttribute("category", contentDto.getCategory());
        setEnvModel(model);
        return "content/detailContent";
    }

    public void setEnvModel(Model model){
        model.addAttribute("server_address", env.getProperty("eureka.instance.hostname"));
        model.addAttribute("server_port", env.getProperty("local.server.port"));
        model.addAttribute("server_service", env.getProperty("spring.application.name"));
    }

}
