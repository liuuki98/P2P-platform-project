package com.liuuki.srb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuuki.srb.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liuuki.srb.entity.query.UserInfoQuery;
import com.liuuki.srb.entity.vo.LoginVo;
import com.liuuki.srb.entity.vo.RegisterVo;
import com.liuuki.srb.entity.vo.UserInfoVo;

/**
 * <p>
 * 用户基本信息 服务类
 * </p>
 *
 * @author liuuki
 * @since 2022-03-04
 */
public interface UserInfoService extends IService<UserInfo> {

    void register(RegisterVo registerVo);

    UserInfoVo userLogin(LoginVo loginVo, String ip);

    IPage<UserInfo> getListByCondition(Page<UserInfo> pageParam, UserInfoQuery userInfoQuery);

    void lock(Long id, Integer status);

    boolean checkPhone(String mobile);
}
