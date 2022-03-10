package com.liuuki.srb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.liuuki.exception.Assert;
import com.liuuki.srb.dao.UserAccountMapper;
import com.liuuki.srb.dao.UserLoginRecordMapper;
import com.liuuki.srb.entity.UserAccount;
import com.liuuki.srb.entity.UserInfo;
import com.liuuki.srb.dao.UserInfoMapper;
import com.liuuki.srb.entity.UserLoginRecord;
import com.liuuki.srb.entity.query.UserInfoQuery;
import com.liuuki.srb.entity.vo.LoginVo;
import com.liuuki.srb.entity.vo.RegisterVo;
import com.liuuki.srb.entity.vo.UserInfoVo;
import com.liuuki.srb.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuuki.srb.util.JwtUtils;
import com.liuuki.srb.vo.ResultEnum;
import com.liuuki.util.MD5;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 * 用户基本信息 服务实现类
 * </p>
 *
 * @author liuuki
 * @since 2022-03-04
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Resource
    private UserAccountMapper userAccountMapper;

    @Resource
    private UserLoginRecordMapper userLoginRecordMapper;


    /**
     * 用户注册
     * @param registerVO
     */
    @Transactional(rollbackFor = {Exception.class})//事务处理
    @Override
    public void register(RegisterVo registerVO) {
        //判断用户是否已经呗注册
        QueryWrapper<UserInfo> userInfoQueryWrapper=new QueryWrapper<>();
        userInfoQueryWrapper.eq("mobile",registerVO.getMobile());
        userInfoQueryWrapper.eq("user_type",registerVO.getUserType());
        int count = baseMapper.selectCount(userInfoQueryWrapper);
        Assert.isTrue(count==0, ResultEnum.MOBILE_EXIST_ERROR); //MOBILE_EXIST_ERROR(-207, "手机号已被注册")

        //注册UserInfo
        UserInfo userInfo=new UserInfo();
//        BeanUtils.copyProperties(registerVo,userInfo);

        userInfo.setUserType(registerVO.getUserType());
        userInfo.setNickName(registerVO.getMobile());
        userInfo.setName(registerVO.getMobile());
        userInfo.setMobile(registerVO.getMobile());
        userInfo.setPassword(MD5.encrypt(registerVO.getPassword()));
        userInfo.setStatus(UserInfo.STATUS_NORMAL); //正常
        //设置一张静态资源服务器上的头像图片
        userInfo.setHeadImg("https://com-liuuki-srb-file.oss-cn-beijing.aliyuncs.com/headPic/OIP-C.jpg");
        baseMapper.insert(userInfo);

        //创建会员账户
        UserAccount userAccount = new UserAccount();
        userAccount.setUserId(userInfo.getId());
        userAccountMapper.insert(userAccount);

    }

    /**
     * 用户登录验证
     * @param loginVo
     * @param ip
     * @return
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public UserInfoVo userLogin(LoginVo loginVo, String ip) {

        //验证是否存在账号
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("mobile",loginVo.getMobile());
        userInfoQueryWrapper.eq("user_type",loginVo.getUserType());
        UserInfo userInfo = baseMapper.selectOne(userInfoQueryWrapper);

        //当用户不存在
        Assert.notNull(userInfo, ResultEnum.LOGIN_MOBILE_ERROR);

        //校验密码
        Assert.equals(userInfo.getPassword(),MD5.encrypt(loginVo.getPassword()),ResultEnum.LOGIN_PASSWORD_ERROR);

        //校验是否被禁用
        Assert.equals(userInfo.getStatus(),UserInfo.STATUS_NORMAL,ResultEnum.LOGIN_LOKED_ERROR);

        //登陆成功后，加入登录日子
        UserLoginRecord userLoginRecord = new UserLoginRecord();
        userLoginRecord.setUserId(userInfo.getId());
        userLoginRecord.setIp(ip);
        userLoginRecordMapper.insert(userLoginRecord);

        //生成token
        String token = JwtUtils.createToken(userInfo.getId(), userInfo.getName());
        UserInfoVo userInfoVO = new UserInfoVo();
        userInfoVO.setToken(token);
        userInfoVO.setName(userInfo.getName());
        userInfoVO.setNickName(userInfo.getNickName());
        userInfoVO.setHeadImg(userInfo.getHeadImg());
        userInfoVO.setMobile(userInfo.getMobile());
        userInfoVO.setUserType(userInfo.getUserType());


        return userInfoVO;
    }

    /**
     * 根据条件对用户信息进行查找并进行分页显示
     * @param pageParam
     * @param userInfoQuery
     * @return
     */
    @Override
    public IPage<UserInfo> getListByCondition(Page<UserInfo> pageParam, UserInfoQuery userInfoQuery) {
        QueryWrapper<UserInfo> userInfoQueryWrapper=new QueryWrapper<>();
        //无条件进行的查询
        if(userInfoQuery == null){
            return baseMapper.selectPage(pageParam, null);
        }

        //条件查询
        userInfoQueryWrapper.like(StringUtils.isNotBlank(userInfoQuery.getMobile()),"mobile",userInfoQuery.getMobile())
                .like(userInfoQuery.getStatus()!=null,"status",userInfoQuery.getStatus())
                .like(userInfoQuery.getUserType()!=null,"user_type",userInfoQuery.getUserType());
        return baseMapper.selectPage(pageParam,userInfoQueryWrapper);
    }

    /**
     * 锁定/解锁 用户
     * @param id
     * @param status
     */
    @Override
    public void lock(Long id, Integer status) {
        UserInfo userInfo=new UserInfo();
        userInfo.setId(id);
        userInfo.setStatus(status);
        baseMapper.updateById(userInfo);

    }

    @Override
    public boolean checkPhone(String mobile) {
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("mobile",mobile);
        int count = baseMapper.selectCount(userInfoQueryWrapper);
        return count>0;
    }
}
