package com.liuuki.srb.controller.api;


import com.liuuki.exception.Assert;
import com.liuuki.srb.entity.vo.LoginVo;
import com.liuuki.srb.entity.vo.RegisterVo;
import com.liuuki.srb.entity.vo.UserInfoVo;
import com.liuuki.srb.service.UserInfoService;
import com.liuuki.srb.util.JwtUtils;
import com.liuuki.srb.vo.R;
import com.liuuki.srb.vo.ResultEnum;
import com.liuuki.util.RegexValidateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户基本信息 前端控制器
 * </p>
 *
 * @author liuuki
 * @since 2022-03-04
 */
@RestController
@RequestMapping("/api/core/userInfo")
@Api(tags = "用户注册模块")
@Slf4j
public class UserInfoController {


    @Resource
    private UserInfoService userInfoService;

    @Resource
    private RedisTemplate redisTemplate;

    @ApiOperation(value = "用户注册")
    @PostMapping("/registerAc")
    public R register(
            @ApiParam(value = "用户注册对象")
            @RequestBody RegisterVo registerVo){

        //进行信息验证，通过后进行业务处理

        Assert.notEmpty(registerVo.getMobile(), ResultEnum.MOBILE_NULL_ERROR);  //MOBILE_NULL_ERROR(-202, "手机号不能为空"),
        Assert.isTrue(RegexValidateUtils.checkCellphone(registerVo.getMobile()), ResultEnum.MOBILE_ERROR);      //MOBILE_ERROR(-203, "手机号不正确"),
        Assert.notEmpty(registerVo.getPassword(), ResultEnum.PASSWORD_NULL_ERROR);//PASSWORD_NULL_ERROR(-204, "密码不能为空")
        Assert.isTrue(RegexValidateUtils.checkPassword(registerVo.getPassword()),ResultEnum.PASSWORD_ERROR);// PASSWORD_ERROR(-212,"密码规范错误" )
        //CODE_NULL_ERROR(-205, "验证码不能为空"),
        Assert.notEmpty(registerVo.getCode(), ResultEnum.CODE_NULL_ERROR);

        //校验是否和redis中验证码一致
        String code =(String) redisTemplate.opsForValue().get("srb:sms:code:" + registerVo.getMobile());
        //CODE_ERROR(-206, "验证码不正确"),
        Assert.equals(code, registerVo.getCode(), ResultEnum.CODE_ERROR);


        //注册
        userInfoService.register(registerVo);
        return R.success().message("注册成功");
    }

    @ApiOperation(value = "登录功能")
    @PostMapping("/loginAc")//需要写入登录日志，因此需要用post请求
    public R<UserInfoVo> userLogin(
            @ApiParam(value = "用户登录对象",required = true)
            @RequestBody LoginVo loginVo, HttpServletRequest httpServletRequest){
        //检验数据否为空
        Assert.notEmpty(loginVo.getMobile(), ResultEnum.MOBILE_NULL_ERROR);
        Assert.notEmpty(loginVo.getPassword(), ResultEnum.PASSWORD_NULL_ERROR);
        //检验数据是否符合规范
        Assert.isTrue(RegexValidateUtils.checkCellphone(loginVo.getMobile()), ResultEnum.MOBILE_ERROR);      //MOBILE_ERROR(-203, "手机号不正确"),
        Assert.isTrue(RegexValidateUtils.checkPassword(loginVo.getPassword()),ResultEnum.PASSWORD_ERROR);// PASSWORD_ERROR(-212,"密码规范错误" )

        //获取ip，写入登陆日志
        String ip =httpServletRequest.getRemoteAddr();

        //验证账号密码以及状态是否正确
        UserInfoVo userInfoVo=userInfoService.userLogin(loginVo,ip);
        return R.success().data(userInfoVo);

    }

    @ApiOperation(value = "校验用户的登陆令牌是否有效")
    @GetMapping("/checkToken")
    public R checkToken(HttpServletRequest request){
        String token=request.getHeader("token");
        boolean flag = JwtUtils.checkToken(token);
        if(flag){
            return R.success();
        }else return R.setResult(ResultEnum.LOGIN_AUTH_ERROR);  //LOGIN_AUTH_ERROR(-211, "未登录"),
    }

    @ApiOperation(value = "校验手机号是否已经被注册")
    @GetMapping("/checkPhone/{mobile}")
    public boolean checkPhone(
            @ApiParam(value = "手机号码")
            @PathVariable("mobile") String mobile){


        Assert.isTrue(RegexValidateUtils.checkCellphone(mobile), ResultEnum.MOBILE_ERROR);      //MOBILE_ERROR(-203, "手机号不正确"),

       return userInfoService.checkPhone(mobile);
    }

}

