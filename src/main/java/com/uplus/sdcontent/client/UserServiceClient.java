package com.uplus.sdcontent.client;


import com.uplus.sdcontent.vo.RequestLogin;
import com.uplus.sdcontent.vo.RequestUser;
import com.uplus.sdcontent.vo.ResponseUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name="user-service")
public interface UserServiceClient {

    @PostMapping("/users")
    ResponseUser createUser(RequestUser user);

    @PostMapping("/login")
    ResponseEntity<Object> login(RequestLogin user);

}
