package com.liuuki.srb.controller;

import com.liuuki.exception.BusinessException;
import com.liuuki.srb.service.FileService;
import com.liuuki.srb.vo.R;
import com.liuuki.srb.vo.ResultEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

@RestController
@Slf4j
@Api(tags = "阿里云OSS文件模块")
@RequestMapping("/api/oss/file")
public class Controller {
    @Resource
    private FileService fileService;

    @ApiOperation(value = "文件上传")
    @PostMapping("/uploadAc")
    public R uploadFile(
            @ApiParam(value = "文件",required = true)
            @RequestParam("file")MultipartFile file,
            @ApiParam(value = "模块",required = true)
            @RequestParam("module") String module
            ) {
        try {
            String originalFilename = file.getOriginalFilename();
            InputStream inputStream = file.getInputStream();
            String url = fileService.upload(inputStream, module, originalFilename);

            return R.success().message("文件上传成功").data(url);
        } catch (IOException e) {
            throw new BusinessException(ResultEnum.UPLOAD_ERROR, e);
        }
    }

    @ApiOperation(value = "文件删除")
    @DeleteMapping("/deleteAc")
    public R deleteByUrl(
            @ApiParam(value = "文件路径",required = true)
            @RequestParam("url") String url){
        try{
            fileService.deleteByUrl(url);
            return R.success().message("删除成功");
        }catch (Exception e){
            throw new BusinessException(ResultEnum.DELETE_ERROR,e);
        }

    }
}
