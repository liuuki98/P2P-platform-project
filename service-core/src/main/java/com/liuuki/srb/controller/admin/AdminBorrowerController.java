package com.liuuki.srb.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuuki.srb.entity.Borrower;
import com.liuuki.srb.entity.vo.BorrowerApprovalVO;
import com.liuuki.srb.entity.vo.BorrowerDetailVO;
import com.liuuki.srb.service.BorrowerService;
import com.liuuki.srb.vo.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "借款人管理列表")
@RestController
@RequestMapping("/admin/core/borrower")
@Slf4j
public class AdminBorrowerController {
    @Resource
    private BorrowerService borrowerService;



    @ApiOperation("获取借款人分页列表")
    @GetMapping("/list/{page}/{limit}")
    public R listPage(
            @ApiParam(value = "当前页数")
            @PathVariable(value = "page",required = true) Long page,

            @ApiParam(value = "页面大小")
            @PathVariable(value = "limit",required = true) Long limit,

            @ApiParam(value = "查询条件",required = false)
            @RequestParam String condition
            ){

        Page<Borrower> pageParam = new Page<>(page, limit);
        IPage<Borrower> pageModel = borrowerService.listPage(pageParam, condition);
        return R.success().data(pageModel);
    }


    @ApiOperation("获取借款人信息")
    @GetMapping("/show/{id}")
    public R show(
            @ApiParam(value = "借款人id", required = true)
            @PathVariable Long id) {
        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerDetailVOById(id);
        return R.success().data(borrowerDetailVO);
    }

    @ApiOperation("借款额度审批")
    @PostMapping("/approval")
    public R approval(@RequestBody BorrowerApprovalVO borrowerApprovalVO) {
        borrowerService.approval(borrowerApprovalVO);
        return R.success().message("审批完成");
    }

}
