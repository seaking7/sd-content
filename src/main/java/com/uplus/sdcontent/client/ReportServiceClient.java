package com.uplus.sdcontent.client;


import com.uplus.sdcontent.vo.RequestDeleteContent;
import com.uplus.sdcontent.vo.ResponseContent;
import com.uplus.sdcontent.vo.ResponseReport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="report-service")
public interface ReportServiceClient {

    @PostMapping("/report/deleteContent")
    ResponseReport deleteReport(@RequestBody RequestDeleteContent requestDeleteContent);


}
