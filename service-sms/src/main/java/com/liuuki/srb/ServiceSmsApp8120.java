package com.liuuki.srb;

import com.liuuki.srb.util.SmsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages ={"com.liuuki.srb","com.liuuki.exception"})
public class ServiceSmsApp8120 {
    public static void main(String[] args) {
        SpringApplication.run(ServiceSmsApp8120.class, args);
    }
}
