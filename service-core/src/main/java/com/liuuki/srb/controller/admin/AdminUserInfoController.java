package com.liuuki.srb.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuuki.srb.entity.UserInfo;
import com.liuuki.srb.entity.query.UserInfoQuery;
import com.liuuki.srb.service.UserInfoService;
import com.liuuki.srb.vo.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 后台用户信息的管理模块
 * </p>
 *
 * @author liuuki
 * @since 2022-03-09
 */
@RestController
@RequestMapping("/admin/core/userInfo")
@Slf4j
@Api(tags = "后台用户信息的管理模块")
public class AdminUserInfoController {
    @Resource
    private UserInfoService userInfoService;

    @ApiOperation(value = "根据条件查询并展示用户信息分页")
    @GetMapping("/getListByCondition/{page}/{limit}")
    public R getListByCondition(
            @ApiParam(value = "当前页数")
            @PathVariable Long page,
            @ApiParam(value = "每页展示的条目数")
            @PathVariable Long limit,
            @ApiParam(value = "查询条件")
            UserInfoQuery userInfoQuery){

        Page<UserInfo> pageParam = new Page<>(page, limit);
        IPage<UserInfo> userInfoIPage=userInfoService.getListByCondition(pageParam,userInfoQuery);
        return R.success().data(userInfoIPage);


    }


    @ApiOperation(value = "锁定用户")
    @PutMapping("/lockAc/{id}/{status}")
    public R lock(
            @ApiParam(value = "用户Id")
            @PathVariable("id") Long id,
            @ApiParam(value = "用户锁定状态")
            @PathVariable("status") Integer status){
        userInfoService.lock(id,status);
        return R.success().message(status==1?"解锁成功":"锁定成功");
    }
}
