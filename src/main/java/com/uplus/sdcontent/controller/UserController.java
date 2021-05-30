package com.uplus.sdcontent.controller;

import com.uplus.sdcontent.client.UserServiceClient;
import com.uplus.sdcontent.service.ContentService;
import com.uplus.sdcontent.vo.RequestLogin;
import com.uplus.sdcontent.vo.RequestUser;
import com.uplus.sdcontent.vo.ResponseUser;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;



@Slf4j
@Controller
@RequestMapping("/")
public class UserController {


    private ContentService contentService;
    private UserServiceClient userServiceClient;
    private Environment env;

    @Autowired
    public UserController(ContentService contentService, UserServiceClient userServiceClient, Environment env) {
        this.contentService = contentService;
        this.userServiceClient = userServiceClient;
        this.env = env;
    }

    @GetMapping("/user/registerUser")
    public String selectRegisterUser(Model model){
        setEnvModel(model);
        return "user/registerUser";
    }


    @PostMapping("/user/new")
    public String registerUser(RequestUser form){
        System.out.println(form);

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        RequestUser requestUser = mapper.map(form, RequestUser.class);
        ResponseUser responseUser = userServiceClient.createUser(requestUser);

        return "home";
    }


    @GetMapping("/user/login")
    public String selectLoginUser(Model model){
        setEnvModel(model);
        return "user/login";
    }


    @PostMapping("/user/login")
    public String loginUser(RequestLogin form){
        System.out.println(form);

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        RequestLogin requestUser = mapper.map(form, RequestLogin.class);
        userServiceClient.login(requestUser);

        return "home";
    }

    public void setEnvModel(Model model){
        model.addAttribute("server_address", env.getProperty("eureka.instance.hostname"));
        model.addAttribute("server_port", env.getProperty("local.server.port"));
        model.addAttribute("server_service", env.getProperty("spring.application.name"));
    }
}
