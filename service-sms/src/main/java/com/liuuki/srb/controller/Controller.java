package com.liuuki.srb.controller;

import com.liuuki.exception.Assert;
import com.liuuki.srb.client.CoreServiceClient;
import com.liuuki.srb.service.SmsService;
import com.liuuki.srb.util.SmsProperties;
import com.liuuki.srb.vo.R;
import com.liuuki.srb.vo.ResultEnum;
import com.liuuki.util.RandomUtils;
import com.liuuki.util.RegexValidateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@Api(tags = "短信验证模块")
@RequestMapping("/api/sms")
public class Controller {
    @Resource
    private SmsService service;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private CoreServiceClient coreServiceClient;

    @ApiOperation(value = "获取验证码")
    @GetMapping("/sendAc/{mobile}")
    public R sendMessage(
            @ApiParam(value = "手机号码",required = true)
            @PathVariable String mobile){
        Assert.notEmpty(mobile, ResultEnum.MOBILE_NULL_ERROR);
        Assert.isTrue(RegexValidateUtils.checkCellphone(mobile),ResultEnum.MOBILE_ERROR);

        //远程调用servi-core的校验方法
        boolean flag = coreServiceClient.checkPhone(mobile);

        Assert.isTrue(!flag,ResultEnum.MOBILE_EXIST_ERROR);

        String code = RandomUtils.getSixBitRandom();
        Map<String,Object> map = new HashMap<>();
        map.put("code",code);

        service.send(mobile, SmsProperties.TEMPLATE_CODE,map);//发送验证码

        redisTemplate.opsForValue().set("srb:sms:code:"+mobile,code);
        return R.success().message("短信发送成功！");


    }
}
