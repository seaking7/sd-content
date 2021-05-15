package com.uplus.sdcontent.controller;

import com.uplus.sdcontent.dto.ContentDto;
import com.uplus.sdcontent.jpa.ContentEntity;
import com.uplus.sdcontent.service.ContentService;
import com.uplus.sdcontent.vo.ContentForm;
import com.uplus.sdcontent.vo.ResponseContent;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/content-service")
public class ContentController {

    private ContentService contentService;

    @Autowired
    public ContentController(ContentService contentService) {
        this.contentService = contentService;
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
    public String createContent(){
        return "content/createContentForm";
    }

    @PostMapping("/contents/new")
    public String createContent(ContentForm form){
        System.out.println(form);

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        ContentDto contentDto = mapper.map(form, ContentDto.class);
        contentService.createContent(contentDto);


        return "redirect:/content-service/";

    }


    @GetMapping("/contents")
    public String viewContents(Model model){
        Iterable<ContentEntity> contentList = contentService.getContentsAll();
        List<ResponseContent> result = new ArrayList<>();
        contentList.forEach( v -> {
            result.add(new ModelMapper().map(v, ResponseContent.class));
        });
        model.addAttribute("contents", result);

        return "content/viewContent";
    }

}
