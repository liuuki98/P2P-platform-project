package com.liuuki.srb.controller.admin;


import com.liuuki.srb.entity.IntegralGrade;
import com.liuuki.srb.service.IntegralGradeService;
import com.liuuki.srb.vo.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 积分等级表 前端控制器
 * </p>
 *
 * @author liuuki
 * @since 2022-03-04
 */
@RestController
@CrossOrigin
@RequestMapping("/admin/core/integralGrade")
@Api(tags = "积分管理模块")
public class AdminIntegralGradeController {
    @Resource
    private IntegralGradeService integralGradeService;

    @GetMapping("/getAc/allList")
    @ApiOperation(value = "获取用户的积分信息")
    public R<List> getAllList(){
        List<IntegralGrade> integralGradeList = integralGradeService.list();
        return R.success().data(integralGradeList);
    }

    @ApiOperation(value = "根据提供的id参数对数据库中的积分信息进行伪删除")
    @DeleteMapping("/deleteAc/deleteById/{id}")
    public R DeleteById(
            @ApiParam(value = "数据id", required = true, example = "1")
            @PathVariable Long id){
        Boolean flag =integralGradeService.removeById(id);
        if(flag){
            return R.success().message("删除ID："+id+"成功！");
        }else {
            return R.error().message("删除ID："+id+"失败！");
        }
    }

    @PostMapping("/addAc/addOne")
    @ApiOperation(value = "添加一条积分信息到数据库中")
    public R addIntegralGrade(
            @ApiParam(value = "积分信息的对象",readOnly = true)
            @RequestBody IntegralGrade integralGrade){

        Boolean flag = integralGradeService.save(integralGrade);
        if(flag){
            return R.success().message("新增成功！"+"/n"+"新增内容：/n"+integralGrade.toString());
        }else {
            return R.error().message("新增失败！");
        }
    }

    @ApiOperation("根据单个 ID 获取一条积分等级信息")
    @GetMapping("/getAc/getOne/{id}")
    public R<IntegralGrade> getById(
            @ApiParam(value = "数据id", required = true, example = "1")
            @PathVariable Long id
    ){
        IntegralGrade integralGrade = integralGradeService.getById(id);
        if(integralGrade != null){
            return R.success().data(integralGrade);
        }else{
            return R.error().message("数据不存在!");
        }
    }

    @ApiOperation("根据单个ID更新积分等级信息")
    @PutMapping("/updateAc/updateById/{id}")
    public R updateById(
            @ApiParam(value = "积分等级的对象",readOnly = true)
            @RequestBody IntegralGrade integralGrade
    ){
        Boolean flag = integralGradeService.updateById(integralGrade);
        if(flag){
            return R.success().message("修改成功");
        }else{
            return R.error().message("修改失败");
        }
    }



}

