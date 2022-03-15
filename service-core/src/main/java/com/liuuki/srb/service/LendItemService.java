package com.liuuki.srb.service;

import com.liuuki.srb.entity.LendItem;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liuuki.srb.entity.vo.InvestVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借记录表 服务类
 * </p>
 *
 * @author liuuki
 * @since 2022-03-04
 */
public interface LendItemService extends IService<LendItem> {

    String commitInvest(InvestVO investVO);

    void notified(Map<String, Object> paramMap);

    /**
     * 根据lendId获取投资记录
     * @param lendId
     * @param i
     * @return
     */
    List<LendItem> selectByLendId(Long lendId, Integer status);

    List<LendItem> selectByLendIdNoStatus(Long lendId);
}
