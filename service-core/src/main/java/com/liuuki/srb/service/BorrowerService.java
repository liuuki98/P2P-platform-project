package com.liuuki.srb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuuki.srb.entity.Borrower;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liuuki.srb.entity.vo.BorrowerApprovalVO;
import com.liuuki.srb.entity.vo.BorrowerDetailVO;
import com.liuuki.srb.entity.vo.BorrowerVO;

/**
 * <p>
 * 借款人 服务类
 * </p>
 *
 * @author liuuki
 * @since 2022-03-04
 */
public interface BorrowerService extends IService<Borrower> {

    void saveBorrower(BorrowerVO borrowerVO, Long userId);

    Integer getStatusByUserId(Long userId);

    IPage<Borrower> listPage(Page<Borrower> pageParam, String condition);

    BorrowerDetailVO getBorrowerDetailVOById(Long id);

    void approval(BorrowerApprovalVO borrowerApprovalVO);
}
