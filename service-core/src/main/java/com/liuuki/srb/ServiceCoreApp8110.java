package com.liuuki.srb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages ={"com.liuuki.srb","com.liuuki.exception"})
public class ServiceCoreApp8110 {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCoreApp8110.class,args);
    }
}
