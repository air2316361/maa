package com.maa.remote.service;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WebsiteSchedule {

    private RestTemplate restTemplate = new RestTemplate();
    @Value("${DOMAIN:gai.cloudns.org}")
    private String domain;

    @Scheduled(cron = "0 * * * * *")
    public void schedule() {
        restTemplate.getForObject("https://" + domain, String.class);
    }
}
