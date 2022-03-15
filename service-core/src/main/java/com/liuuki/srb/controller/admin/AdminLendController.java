package com.liuuki.srb.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuuki.srb.entity.Lend;
import com.liuuki.srb.service.LendService;
import com.liuuki.srb.vo.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <p>
 * 标的准备表 前端控制器
 * </p>
 *
 * @author liuuki
 * @since 2022-03-04
 */
@RestController
@RequestMapping("/admin/core/lend")
@Api(tags = "标的管理")
@Slf4j
public class AdminLendController {

    @Resource
    private LendService lendService;

    @ApiOperation("获取标的分页列表")
    @GetMapping("/list/{page}/{limit}")
    public R listPage(
            @ApiParam(value = "当前页数")
            @PathVariable(value = "page",required = true) Long page,

            @ApiParam(value = "页面大小")
            @PathVariable(value = "limit",required = true) Long limit){

        Page<Lend> pageParam = new Page<>(page, limit);
        IPage<Lend> pageModel = lendService.listPage(pageParam);
        return R.success().data(pageModel);
    }

    @ApiOperation("获取标的信息")
    @GetMapping("/show/{id}")
    public R<Map<String,Object>> show(
            @ApiParam(value = "标的id", required = true)
            @PathVariable Long id) {
        Map<String, Object> result = lendService.getLendDetail(id);
        return R.success().data(result);
    }

    @ApiOperation("放款")
    @GetMapping("/makeLoan/{id}")
    public R makeLoan(
            @ApiParam(value = "标的id", required = true)
            @PathVariable("id") Long id) {
        lendService.makeLoan(id);
        return R.success().message("放款成功");
    }

}

