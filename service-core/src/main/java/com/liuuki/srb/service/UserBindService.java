package com.liuuki.srb.service;

import com.liuuki.srb.entity.UserBind;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liuuki.srb.entity.vo.UserBindVO;

import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务类
 * </p>
 *
 * @author liuuki
 * @since 2022-03-04
 */
public interface UserBindService extends IService<UserBind> {

    String userBind(UserBindVO userBindVO, Long userId);

    void notifies(Map<String, Object> paramMap);
}
