package com.liuuki.srb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuuki.srb.entity.UserLoginRecord;
import com.liuuki.srb.dao.UserLoginRecordMapper;
import com.liuuki.srb.service.UserLoginRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户登录记录表 服务实现类
 * </p>
 *
 * @author liuuki
 * @since 2022-03-04
 */
@Service
public class UserLoginRecordServiceImpl extends ServiceImpl<UserLoginRecordMapper, UserLoginRecord> implements UserLoginRecordService {

    @Override
    public List<UserLoginRecord> listTop50(Long userId) {
        QueryWrapper<UserLoginRecord> userLoginRecordQueryWrapper = new QueryWrapper<>();
        userLoginRecordQueryWrapper
                .eq("user_id", userId)
                .orderByDesc("id")
                .last("limit 50");
        List<UserLoginRecord> userLoginRecords = baseMapper.selectList(userLoginRecordQueryWrapper);
        return userLoginRecords;
    }
}
