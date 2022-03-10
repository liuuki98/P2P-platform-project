package com.liuuki.srb.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuuki.srb.entity.Dict;
import com.liuuki.srb.dao.DictMapper;
import com.liuuki.srb.entity.dto.ExcelDictDTO;
import com.liuuki.srb.listener.ExcelDicListener;
import com.liuuki.srb.service.DictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author liuuki
 * @since 2022-03-04
 */
@Slf4j
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {
    @Resource
    private RedisTemplate redisTemplate;


    /**
     * 导入excel文件到数据库
     * @param inputStream
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void importDate(InputStream inputStream) {
        EasyExcel.read(inputStream, ExcelDictDTO.class,new  ExcelDicListener(baseMapper)).excelType(ExcelTypeEnum.XLSX).sheet().doRead();
        log.info("Excel导入完成!");
    }

    /**
     * 获取所有词典的数据
     * @return
     */
    @Override
    public List<ExcelDictDTO> getListDictData() {
        List<Dict> dicts = baseMapper.selectList(null);
        ArrayList<ExcelDictDTO> excelDictDTOS = new ArrayList<>(dicts.size());

        dicts.forEach(dict -> {
            ExcelDictDTO excelDictDTO = new ExcelDictDTO();
            BeanUtils.copyProperties(dict,excelDictDTO);
            excelDictDTOS.add(excelDictDTO);
        });
        return excelDictDTOS;
    }

    /**
     * 根据父ID去查找数据，并且封装在list中
     * @param parentId
     * @return
     */
    @Override
    public List<Dict> listByParentId(Long parentId) {

        List<Dict> dicts =null;
        //查询redis是否有词典数据
        try{
            log.info("从redis中取得数据");
            dicts = (List<Dict>)redisTemplate.opsForValue().get("srb:core:dicList:"+parentId);
            if(dicts!=null){
                return dicts;
            }
        }catch (Exception e){
            log.error("redis服务器异常：" + ExceptionUtils.getStackTrace(e));//此处不抛出异常，继续执行后面的代码
        }
        //redis没有对应的数据，从数据库中取数据
        log.info("从数据库中取值");
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id",parentId);
        dicts =baseMapper.selectList(queryWrapper);
        dicts.forEach(dict -> {
            boolean flag = hasChildren(dict.getId());
            dict.setHasChildren(flag);
        });
        //将数据库中取得的数据装载到redis中
        try{
            redisTemplate.opsForValue().set("srb:core:dicList:"+parentId,dicts,5, TimeUnit.MINUTES);
            log.info("从mysql数据库中取得数据");
        }catch (Exception e){
            log.error("redis服务器异常：" + ExceptionUtils.getStackTrace(e));//此处不抛出异常，继续执行后面的代码

        }


        return dicts;
    }

    @Override
    public List<Dict> findByDictCode(String dicCode) {
        QueryWrapper<Dict> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("dict_code",dicCode);
        Dict dict = baseMapper.selectOne(queryWrapper);
        return this.listByParentId(dict.getId());
    }

    /**\
     * 根据dict_code找出父节点，然后匹配父节点下的符合value的数据
     * @param dictCode dict_code
     * @param value value值
     * @return
     */
    @Override
    public String getNameByParentDictCodeAndValue(String dictCode, Integer value) {
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("dict_code",dictCode);
        Dict Parentdict = baseMapper.selectOne(dictQueryWrapper);

        if(Parentdict == null) {
            return "";
        }
        dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper
                .eq("parent_id", Parentdict.getId())
                .eq("value", value);
        Dict dict = baseMapper.selectOne(dictQueryWrapper);

        if(dict == null) {
            return "";
        }

        return dict.getName();
    }

    /**
     * 判断该节点是否有子节点
     */
    public boolean hasChildren(Long id){
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id",id);
        Integer i = baseMapper.selectCount(queryWrapper);
        return i>0;
    }
}
