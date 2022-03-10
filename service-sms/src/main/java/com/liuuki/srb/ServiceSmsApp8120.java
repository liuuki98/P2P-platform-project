package com.liuuki.srb;

import com.liuuki.srb.util.SmsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages ={"com.liuuki.srb","com.liuuki.exception"})
@EnableFeignClients
public class ServiceSmsApp8120 {
    public static void main(String[] args) {
        SpringApplication.run(ServiceSmsApp8120.class, args);
    }
}
