package com.liuuki.srb.dao;

import com.liuuki.srb.entity.Dict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liuuki.srb.entity.dto.ExcelDictDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 数据字典 Mapper 接口
 * </p>
 *
 * @author liuuki
 * @since 2022-03-04
 */
public interface DictMapper extends BaseMapper<Dict> {

    void saveExcel(List<ExcelDictDTO> list);
}
