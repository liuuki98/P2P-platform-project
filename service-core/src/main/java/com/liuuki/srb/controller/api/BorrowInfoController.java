package com.liuuki.srb.controller.api;


import com.liuuki.srb.entity.BorrowInfo;
import com.liuuki.srb.service.BorrowInfoService;
import com.liuuki.srb.util.JwtUtils;
import com.liuuki.srb.vo.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * <p>
 * 借款信息表 前端控制器
 * </p>
 *
 * @author liuuki
 * @since 2022-03-04
 */
@Api(tags = "借款额度信息")
@RestController
@RequestMapping("/api/core/borrowInfo")
@Slf4j
public class BorrowInfoController {

    @Resource
    private BorrowInfoService borrowInfoService;

    @ApiOperation("获取借款额度")
    @GetMapping("/authAc/getBorrowAmount")
    public  R getBorrowAmount(HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        BigDecimal borrowAmount = borrowInfoService.getBorrowAmount(userId);
        return R.success().data(borrowAmount);
    }

    @ApiOperation("提交借款申请")
    @PostMapping("/authAc/save")
    public R save(@RequestBody BorrowInfo borrowInfo, HttpServletRequest request) {

        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        borrowInfoService.saveBorrowInfo(borrowInfo, userId);
        return R.success().message("提交成功");
    }

    @ApiOperation("获取借款申请审批状态")
    @GetMapping("/authAc/getBorrowInfoStatus")
    public R getBorrowerStatus(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        Integer status = borrowInfoService.getStatusByUserId(userId);
        return R.success().data(status);
    }
}