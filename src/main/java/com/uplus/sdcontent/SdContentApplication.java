package com.uplus.sdcontent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class SdContentApplication {

	public static void main(String[] args) {
		SpringApplication.run(SdContentApplication.class, args);
	}

}
