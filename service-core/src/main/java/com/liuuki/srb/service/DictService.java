package com.liuuki.srb.service;

import com.liuuki.srb.entity.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liuuki.srb.entity.dto.ExcelDictDTO;

import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * 数据字典 服务类
 * </p>
 *
 * @author liuuki
 * @since 2022-03-04
 */
public interface DictService extends IService<Dict> {

    void importDate(InputStream inputStream);

    List<ExcelDictDTO> getListDictData();

    List<Dict> listByParentId(Long parentId);

}
