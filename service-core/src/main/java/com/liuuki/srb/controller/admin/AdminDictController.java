package com.liuuki.srb.controller.admin;


import com.alibaba.excel.EasyExcel;
import com.liuuki.exception.BusinessException;
import com.liuuki.srb.entity.Dict;
import com.liuuki.srb.entity.dto.ExcelDictDTO;
import com.liuuki.srb.service.DictService;
import com.liuuki.srb.vo.R;
import com.liuuki.srb.vo.ResultEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * <p>
 * 数据字典 前端控制器
 * </p>
 *
 * @author liuuki
 * @since 2022-03-04
 */
@Api(tags = "数据字典模块")
@RestController
@RequestMapping("/admin/core/dict")
@Slf4j
public class AdminDictController {
    @Resource
    private DictService dictService;

    @PostMapping("/importAc/excel")
    public R importExcel(
            @ApiParam(value = "excel文件",required = true)
            @RequestParam("file") MultipartFile file){
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            dictService.importDate(inputStream);
            return R.success().message("excel上传成功！");
        } catch (Exception e) {
            throw new BusinessException(ResultEnum.UPLOAD_ERROR,e);
        }


    }

    @ApiOperation(value = "用户词典数据的导出（Excel）")
    @GetMapping("/exportAc/excel")
    public void exportExcel(HttpServletResponse response){
        try {
            // 这里注意 使用swagger 会导致各种问题，请直接用浏览器或者用postman
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码
            String fileName = URLEncoder.encode("myDict", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), ExcelDictDTO.class).sheet("数据字典").doWrite(dictService.getListDictData());

        } catch (IOException e) {
            //EXPORT_DATA_ERROR(104, "数据导出失败"),
            throw  new BusinessException(ResultEnum.EXPORT_DATA_ERROR, e);
        }
    }

    @ApiOperation(value = "查找parentId为1的数据，即第一层数据")
    @GetMapping("/getAc/getDicsByPId/{parentId}")
    public R getDicByParentId(
            @ApiParam(value = "父Id",required = true)
            @PathVariable Long parentId
    ) {
       List<Dict> list= dictService.listByParentId(parentId);
       list.forEach(dict -> {
           log.info(dict.toString());
       });
       return R.success().data(list);

    }

}

