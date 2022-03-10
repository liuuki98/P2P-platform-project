package com.liuuki.srb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

@SpringBootApplication(scanBasePackages = {"com.liuuki.srb","com.liuuki.exception"})
public class ServiceOssApp8130 {
    public static void main(String[] args) {
        SpringApplication.run(ServiceOssApp8130.class,args);
    }
}
