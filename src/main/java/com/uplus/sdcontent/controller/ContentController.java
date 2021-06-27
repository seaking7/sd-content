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
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @Timed(value="contents.insert", longTask = true)
    public String createContent(ContentForm form, Model model){

        log.info("{createContent} contentId: "+ form.getContentId());

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
    @Timed(value="contents.delete", longTask = true)
    public String deleteContent(ContentForm form, Model model){

        log.info("{deleteContent} contentId: "+ form.getContentId());

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        ContentDto contentDto = mapper.map(form, ContentDto.class);
        log.info(contentDto.getContentId());
        contentService.deleteByContentId(contentDto.getContentId());

        return listContents(model);
    }

    @GetMapping("/contents")
    @Timed(value="contents.list", longTask = true)
    public String listContents(Model model){
        log.info("{listContents} list content ");

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
    @Timed(value="contents.detail", longTask = true)
    public String detailContent(@PathVariable String contentId, Model model){

        log.info("{detailContent} contentId: "+ contentId);
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

    @GetMapping("/{contentId}/contents/client")
    public ResponseEntity<ResponseContent> selectFromReport(@PathVariable String contentId)
    {
        log.info("{selectFromReport} contentId: "+ contentId);
        ContentDto contentDto = contentService.getContentsByContentId(contentId);

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ResponseContent responseContent = mapper.map(contentDto, ResponseContent.class);

        return ResponseEntity.status(HttpStatus.OK).body(responseContent);

    }

    public void setEnvModel(Model model){
        model.addAttribute("server_address", env.getProperty("eureka.instance.hostname"));
        model.addAttribute("server_port", env.getProperty("local.server.port"));
        model.addAttribute("server_service", env.getProperty("spring.application.name"));
    }

}
