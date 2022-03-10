package com.liuuki.srb.controller.api;


import com.alibaba.fastjson.JSON;
import com.liuuki.srb.entity.vo.UserBindVO;
import com.liuuki.srb.hfb.RequestHelper;
import com.liuuki.srb.service.UserBindService;
import com.liuuki.srb.util.JwtUtils;
import com.liuuki.srb.vo.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 前端控制器
 * </p>
 *
 * @author liuuki
 * @since 2022-03-04
 */
@RestController
@RequestMapping("/api/core/userBind")
@Api(tags = "用户绑定第三方金融监管平台模块")
@Slf4j
public class UserBindController {
    @Resource
    private UserBindService userBindService;

    /**
     * 身份验证通过后，将用户提交得信息绑定到第三方监管平台
     * @param userBindVO
     * @param request
     * @return
     */
    @ApiOperation(value = "用户绑定")
    @PostMapping("/authAc/bind") //autoAc路径下请求需要对身份进行校验
    public R userBind(
            @ApiParam(value = "绑定数据对象")
            @RequestBody UserBindVO userBindVO, HttpServletRequest request){
        //获取令牌信息
        String token = request.getHeader("token");
        //验证令牌，并取得ID
        Long userId = JwtUtils.getUserId(token);


        String formInfo=userBindService.userBind(userBindVO,userId);
        return R.success().data(formInfo);
    }

    @ApiOperation(value = "信息回传，修改相应数据库")
    @PostMapping("/notify")
    public String notify(HttpServletRequest request){

        Map<String, Object> paramMap = RequestHelper.switchMap(request.getParameterMap());
        log.info("用户账号绑定异步回调：" + JSON.toJSONString(paramMap));

        //校验签名
        if(!RequestHelper.isSignEquals(paramMap)) {
            log.error("用户账号绑定异步回调签名错误：" + JSON.toJSONString(paramMap));
            return "fail";
        }

        //修改绑定状态
        userBindService.notifies(paramMap);
        return "success";
    }

}

