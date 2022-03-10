package com.liuuki.srb.controller.api;


import com.liuuki.srb.entity.Dict;
import com.liuuki.srb.service.DictService;
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
import java.util.List;

/**
 * <p>
 * 数据字典 前端控制器
 * </p>
 *
 * @author liuuki
 * @since 2022-03-04
 */
@RestController
@RequestMapping("/api/core/dict")
@Slf4j
@Api(tags = "用户端获得词典信息")
public class DictController {
    @Resource
    private DictService dictService;

    @ApiOperation(value = "根据dict_code获取到节点得父id")
    @GetMapping("/getDicListByCode/{dicCode}")
    public R getDicListByCode(
            @ApiParam(value = "dict_code",required = true)
            @PathVariable("dicCode") String dicCode){
        List<Dict> list = dictService.findByDictCode(dicCode);
        return R.success().data(list);
    }
}

