package com.liuuki.srb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuuki.srb.entity.BorrowInfo;
import com.liuuki.srb.entity.Lend;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liuuki.srb.entity.vo.BorrowInfoApprovalVO;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 标的准备表 服务类
 * </p>
 *
 * @author liuuki
 * @since 2022-03-04
 */
public interface LendService extends IService<Lend> {

    void createLend(BorrowInfoApprovalVO borrowInfoApprovalVO, BorrowInfo borrowInfo);

    IPage<Lend> listPage(Page<Lend> pageParam);

    Map<String, Object> getLendDetail(Long id);

    BigDecimal getInterestCount(BigDecimal invest, BigDecimal yearRate, Integer totalmonth, Integer returnMethod);
    /**
     * 满标放款
     * @param lendId
     */
    void makeLoan(Long lendId);
}
