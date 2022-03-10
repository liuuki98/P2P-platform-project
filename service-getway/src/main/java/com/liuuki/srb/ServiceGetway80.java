package com.liuuki.srb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ServiceGetway80 {

    public static void main(String[] args) {
        SpringApplication.run(ServiceGetway80.class, args);
    }
}
