package com.liuuki.srb.client.fallback;

import com.liuuki.srb.client.CoreServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CoreUserInfoClientFallback implements CoreServiceClient {
    @Override
    public boolean checkPhone(String mobile) {
        log.error("service-core/api/core/userInfo/checkPhone/{mobile}远程调用失败，服务熔断");
        return false;
    }
}
