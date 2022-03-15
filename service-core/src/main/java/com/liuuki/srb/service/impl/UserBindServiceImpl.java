package com.liuuki.srb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuuki.exception.Assert;
import com.liuuki.srb.dao.UserInfoMapper;
import com.liuuki.srb.entity.UserBind;
import com.liuuki.srb.dao.UserBindMapper;
import com.liuuki.srb.entity.UserInfo;
import com.liuuki.srb.entity.vo.UserBindVO;
import com.liuuki.srb.enums.UserBindEnum;
import com.liuuki.srb.hfb.FormHelper;
import com.liuuki.srb.hfb.HfbConst;
import com.liuuki.srb.hfb.RequestHelper;
import com.liuuki.srb.service.UserBindService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuuki.srb.vo.ResultEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务实现类
 * </p>
 *
 * @author liuuki
 * @since 2022-03-04
 */
@Service
public class UserBindServiceImpl extends ServiceImpl<UserBindMapper, UserBind> implements UserBindService {
    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public String userBind(UserBindVO userBindVO, Long userId) {

        //查询是否已经绑定了信息
        QueryWrapper<UserBind> userBindQueryWrapper=new QueryWrapper<>();
        //对身份证，姓名验证
        userBindQueryWrapper.eq("user_id",userId).eq("id_card",userBindVO.getIdCard());
        UserBind userBind = baseMapper.selectOne(userBindQueryWrapper);
        //校验是否已经绑定 USER_BIND_IDCARD_EXIST_ERROR(-301, "身份证号码已绑定"),
        Assert.isNull(userBind, ResultEnum.USER_BIND_IDCARD_EXIST_ERROR);


        //查询用户绑定信息
        userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("user_id", userId);
        userBind = baseMapper.selectOne(userBindQueryWrapper);

        //判断是否有绑定记录
        if(userBind == null) {
            //如果未创建绑定记录，则创建一条记录
            userBind = new UserBind();
            BeanUtils.copyProperties(userBindVO, userBind);
            userBind.setUserId(userId);
            userBind.setStatus(UserBindEnum.NO_BIND.getStatus());
            baseMapper.insert(userBind);
        } else {
            //曾经跳转到托管平台，但是未操作完成，此时将用户最新填写的数据同步到userBind对象
            BeanUtils.copyProperties(userBindVO, userBind);
            baseMapper.updateById(userBind);
        }



        //执行用户绑定
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID); //AGENT_ID = "999888";
        paramMap.put("agentUserId", userId);
        paramMap.put("idCard",userBindVO.getIdCard()); //身份证
        paramMap.put("personalName", userBindVO.getName());//姓名
        paramMap.put("bankType", userBindVO.getBankType());//银行类型
        paramMap.put("bankNo", userBindVO.getBankNo());//银行卡号
        paramMap.put("mobile", userBindVO.getMobile()); //预留手机号
        paramMap.put("returnUrl", HfbConst.USERBIND_RETURN_URL); //USERBIND_RETURN_URL = "http://localhost:3000/user";
        paramMap.put("notifyUrl", HfbConst.USERBIND_NOTIFY_URL); //USERBIND_NOTIFY_URL = "http://localhost/api/core/userBind/notify";
        paramMap.put("timestamp",  new Date().getTime()); //处理时间，防止数据生命周期过长
        paramMap.put("sign", RequestHelper.getSign(paramMap)); //签名加密

        //构建充值自动提交表单
        String formStr = FormHelper.buildForm(HfbConst.USERBIND_URL, paramMap);//USERBIND_URL = "http://localhost:9900/userBind/BindAgreeUserV2";
        return formStr;

    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void notifies(Map<String, Object> paramMap) {

        String bindCode = (String)paramMap.get("bindCode");
        //会员id
        String agentUserId = (String)paramMap.get("agentUserId");

        //根据user_id查询user_bind记录
        QueryWrapper<UserBind> userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("user_id", agentUserId);

        //更新用户绑定表
        UserBind userBind = baseMapper.selectOne(userBindQueryWrapper);
        userBind.setBindCode(bindCode);
        userBind.setStatus(UserBindEnum.BIND_OK.getStatus());
        baseMapper.updateById(userBind);

        //更新用户表
        UserInfo userInfo = userInfoMapper.selectById(agentUserId);
        userInfo.setBindCode(bindCode);
        userInfo.setName(userBind.getName());
        userInfo.setIdCard(userBind.getIdCard());
        userInfo.setBindStatus(UserBindEnum.BIND_OK.getStatus());
        userInfoMapper.updateById(userInfo);

    }

    @Override
    public String getBindCodeByUserId(Long userId){
        QueryWrapper<UserBind> userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("user_id", userId);
        UserBind userBind = baseMapper.selectOne(userBindQueryWrapper);
        String bindCode = userBind.getBindCode();
        return bindCode;
    }
}
