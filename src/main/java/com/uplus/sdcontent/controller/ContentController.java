package com.uplus.sdcontent.controller;

import com.uplus.sdcontent.dto.ContentDto;
import com.uplus.sdcontent.jpa.ContentEntity;
import com.uplus.sdcontent.messagequeue.ContentProducer;
import com.uplus.sdcontent.messagequeue.KafkaProducer;
import com.uplus.sdcontent.service.ContentService;
import com.uplus.sdcontent.vo.ContentForm;
import com.uplus.sdcontent.vo.ResponseContent;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Controller
@RequestMapping("/content-service")
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
    public String contentHome(){
        return "home";
    }

    @GetMapping("/contents/new")
    public String createContent(Model model){
        model.addAttribute("server_address", env.getProperty("eureka.instance.hostname"));
        model.addAttribute("server_port", env.getProperty("local.server.port"));

        return "content/createContentForm";
    }

    @PostMapping("/contents/new")
    public String createContent(ContentForm form){
        System.out.println(form);

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        ContentDto contentDto = mapper.map(form, ContentDto.class);
        contentService.createContent(contentDto);

        /* send this Content to the Kafka */
        //kafkaProducer.send("example-category-topic", contentDto);
        contentProducer.send("contents", contentDto);

        return "home";
    }


    @GetMapping("/contents")
    public String listContents(Model model){
        Iterable<ContentEntity> contentList = contentService.getContentsAll();
        List<ResponseContent> result = new ArrayList<>();
        contentList.forEach( v -> {
            result.add(new ModelMapper().map(v, ResponseContent.class));
        });
        model.addAttribute("contents", result);
        model.addAttribute("server_address", env.getProperty("eureka.instance.hostname"));
        model.addAttribute("server_port", env.getProperty("local.server.port"));

        return "content/listContent";
    }

}
