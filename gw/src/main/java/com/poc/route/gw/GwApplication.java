package com.poc.route.gw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;

import lombok.extern.slf4j.Slf4j;

@ComponentScan("com.poc.route.gw")
@SpringBootApplication
@Slf4j
public class GwApplication {

	public static void main(String[] args) {
		SpringApplication.run(GwApplication.class, args);
	}
	
	@EventListener(RefreshRoutesEvent.class)
	public void handleEvents() {
		log.info(" $$$$$$$$$$$ Inside Refresh Event $$$$$$$$$$$");
	}

}
