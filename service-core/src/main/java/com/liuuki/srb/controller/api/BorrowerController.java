package com.liuuki.srb.controller.api;


import com.liuuki.srb.entity.vo.BorrowerVO;
import com.liuuki.srb.service.BorrowerService;
import com.liuuki.srb.util.JwtUtils;
import com.liuuki.srb.vo.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 借款人 前端控制器
 * </p>
 *
 * @author liuuki
 * @since 2022-03-04
 */
@RestController
@RequestMapping("/api/core/borrower")
@Api(tags = "借款人额度申请管理")
@Slf4j
public class BorrowerController {
    @Resource
    private BorrowerService borrowerService;

    @ApiOperation(value = "借款人申请额度")
    @PostMapping("/authAc/save")
    public R save(
            @ApiParam(value = "申请对象",required = true)
            @RequestBody BorrowerVO borrowerVO, HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId=JwtUtils.getUserId(token);
        borrowerService.saveBorrower(borrowerVO,userId);
        return R.success().message("提交成功");
    }

    @ApiOperation(value = "借款人状态判断")
    @GetMapping("/getAc/status")
    public R getStatus(HttpServletRequest request){
        String token =request.getHeader("token");
        Long userId=JwtUtils.getUserId(token);
        Integer status =borrowerService.getStatusByUserId(userId);
        return R.success().data(status);
    }




}

