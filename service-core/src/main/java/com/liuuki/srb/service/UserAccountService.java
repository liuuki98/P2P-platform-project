package com.liuuki.srb.service;

import com.liuuki.srb.entity.UserAccount;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 用户账户 服务类
 * </p>
 *
 * @author liuuki
 * @since 2022-03-04
 */
public interface UserAccountService extends IService<UserAccount> {

    String commitCharge(BigDecimal chargeAmt, Long userId);

    String notified(Map<String, Object> paramMap);

    BigDecimal getAccount(Long userId);
}
