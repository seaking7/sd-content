package com.uplus.sdcontent.client;


import com.uplus.sdcontent.vo.RequestDeleteContent;
import com.uplus.sdcontent.vo.ResponseContent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="execute-service")
public interface ExecuteServiceClient {

    @PostMapping("/execute-service/execute/deleteContent")
    ResponseContent deleteExecute(@RequestBody RequestDeleteContent requestDeleteContent);


}
