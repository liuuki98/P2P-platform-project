package com.liuuki.srb.service.impl;

import com.liuuki.srb.entity.UserAccount;
import com.liuuki.srb.dao.UserAccountMapper;
import com.liuuki.srb.service.UserAccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户账户 服务实现类
 * </p>
 *
 * @author liuuki
 * @since 2022-03-04
 */
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {

}
