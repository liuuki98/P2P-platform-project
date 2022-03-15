package com.liuuki.srb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuuki.exception.Assert;
import com.liuuki.srb.dao.UserAccountMapper;
import com.liuuki.srb.dao.UserInfoMapper;
import com.liuuki.srb.entity.UserAccount;
import com.liuuki.srb.entity.UserInfo;
import com.liuuki.srb.entity.bo.TransFlowBO;
import com.liuuki.srb.enums.TransTypeEnum;
import com.liuuki.srb.hfb.FormHelper;
import com.liuuki.srb.hfb.HfbConst;
import com.liuuki.srb.hfb.RequestHelper;
import com.liuuki.srb.service.TransFlowService;
import com.liuuki.srb.service.UserAccountService;
import com.liuuki.srb.util.LendNoUtils;
import com.liuuki.srb.vo.ResultEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

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

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private TransFlowService transFlowService;


    /**
     * 进行充值，需要组装表单发送给第三方交易平台进行业务对接
     * @param chargeAmt
     * @param userId
     * @return
     */
    @Override
    public String commitCharge(BigDecimal chargeAmt, Long userId) {

        UserInfo userInfo = userInfoMapper.selectById(userId);
        String bindCode = userInfo.getBindCode();
        //判断账户绑定状态
        Assert.notEmpty(bindCode, ResultEnum.USER_NO_BIND_ERROR);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentBillNo", LendNoUtils.getNo());
        paramMap.put("bindCode", bindCode);
        paramMap.put("chargeAmt", chargeAmt);
        paramMap.put("feeAmt", new BigDecimal("0"));
        paramMap.put("notifyUrl", HfbConst.RECHARGE_NOTIFY_URL);//检查常量是否正确
        paramMap.put("returnUrl", HfbConst.RECHARGE_RETURN_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(paramMap);
        paramMap.put("sign", sign);

        //构建充值自动提交表单
        String formStr = FormHelper.buildForm(HfbConst.RECHARGE_URL, paramMap);
        return formStr;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String notified(Map<String, Object> paramMap) {

        String agentBillNo = (String)paramMap.get("agentBillNo"); //商户充值订单号
        //判断交易流水是否存在
        boolean isSave = transFlowService.isSaveTransFlow(agentBillNo);
        if(isSave){
            log.warn("幂等性返回");
            return "success";
        }
        //账户交易处理
        String bindCode = (String)paramMap.get("bindCode"); //充值人绑定协议号
        String chargeAmt = (String)paramMap.get("chargeAmt"); //充值金额
        baseMapper.updateAccount(bindCode,new BigDecimal(chargeAmt),new BigDecimal(0));

        //增加流水交易

        TransFlowBO transFlowBO = new TransFlowBO(
                agentBillNo,
                bindCode,
                new BigDecimal(chargeAmt),
                TransTypeEnum.RECHARGE,
                "充值");
        transFlowService.saveTransFlow(transFlowBO);



        return "success";
    }

    @Override
    public BigDecimal getAccount(Long userId) {
        //根据userId查找用户账户
        QueryWrapper<UserAccount> userAccountQueryWrapper = new QueryWrapper<>();
        userAccountQueryWrapper.eq("user_id", userId);
        UserAccount userAccount = baseMapper.selectOne(userAccountQueryWrapper);

        BigDecimal amount = userAccount.getAmount();
        return amount;
    }
}
