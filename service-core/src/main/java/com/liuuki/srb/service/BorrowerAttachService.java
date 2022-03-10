package com.liuuki.srb.service;

import com.liuuki.srb.entity.BorrowerAttach;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liuuki.srb.entity.vo.BorrowerAttachVO;

import java.util.List;

/**
 * <p>
 * 借款人上传资源表 服务类
 * </p>
 *
 * @author liuuki
 * @since 2022-03-04
 */
public interface BorrowerAttachService extends IService<BorrowerAttach> {

    List<BorrowerAttachVO> selectBorrowerAttachVOList(Long id);
}
