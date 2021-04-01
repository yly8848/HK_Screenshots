package com.yly.hk;


import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;



@SpringBootApplication
public class HkScreenshotsApplication {

	public static void main(String[] args) {
		SpringApplicationBuilder builder = new SpringApplicationBuilder(HkScreenshotsApplication.class);
		builder.headless(false).run(args);
	}

}
