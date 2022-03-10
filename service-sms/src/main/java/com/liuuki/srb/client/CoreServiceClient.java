package com.liuuki.srb.client;


import com.liuuki.srb.client.fallback.CoreUserInfoClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-core",fallback = CoreUserInfoClientFallback.class)
public interface CoreServiceClient {

    @GetMapping("/api/core/userInfo/checkPhone/{mobile}")
    boolean checkPhone(@PathVariable String mobile);
}
