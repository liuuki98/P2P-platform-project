package com.liuuki.srb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuuki.exception.Assert;
import com.liuuki.srb.dao.BorrowInfoMapper;
import com.liuuki.srb.dao.BorrowerMapper;
import com.liuuki.srb.dao.IntegralGradeMapper;
import com.liuuki.srb.dao.UserInfoMapper;
import com.liuuki.srb.entity.BorrowInfo;
import com.liuuki.srb.entity.Borrower;
import com.liuuki.srb.entity.IntegralGrade;
import com.liuuki.srb.entity.UserInfo;
import com.liuuki.srb.entity.vo.BorrowInfoApprovalVO;
import com.liuuki.srb.entity.vo.BorrowerDetailVO;
import com.liuuki.srb.enums.BorrowInfoStatusEnum;
import com.liuuki.srb.enums.BorrowerStatusEnum;
import com.liuuki.srb.enums.UserBindEnum;
import com.liuuki.srb.service.BorrowInfoService;
import com.liuuki.srb.service.BorrowerService;
import com.liuuki.srb.service.DictService;
import com.liuuki.srb.service.LendService;
import com.liuuki.srb.vo.ResultEnum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 服务实现类
 * </p>
 *
 * @author liuuki
 * @since 2022-03-04
 */
@Service
public class BorrowInfoServiceImpl extends ServiceImpl<BorrowInfoMapper, BorrowInfo> implements BorrowInfoService {


    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private IntegralGradeMapper integralGradeMapper;

    @Resource
    private DictService dictService;
    @Resource
    private BorrowerMapper borrowerMapper;
    @Resource
    private BorrowerService borrowerService;
    @Resource
    private LendService lendService;

    /**
     * 根据用户id获取积分信息以及借款额度区间
     * @param userId
     * @return
     */
    @Override
    public BigDecimal getBorrowAmount(Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        Integer point = userInfo.getIntegral();

        //获取积分对应的额度区间
        QueryWrapper<IntegralGrade> integralGradeQueryWrapper = new QueryWrapper<>();
        integralGradeQueryWrapper.le("integral_start", point);
        integralGradeQueryWrapper.ge("integral_end", point);
        IntegralGrade integralGrade = integralGradeMapper.selectOne(integralGradeQueryWrapper);
        if(integralGrade == null){
            return new BigDecimal("0");
        }
        return integralGrade.getBorrowAmount();

    }

    /**
     * 审核用户提交的信息，并对信息进行验证，通过后录入数据库。
     * @param borrowInfo
     * @param userId
     */
    @Override
    public void saveBorrowInfo(BorrowInfo borrowInfo, Long userId) {
        //获取用户信息，查验是否通过开户、资格审核
        UserInfo userInfo = userInfoMapper.selectById(userId);

        Assert.isTrue(userInfo.getBindStatus().intValue()== UserBindEnum.BIND_OK.getStatus().intValue(),
                ResultEnum.USER_NO_BIND_ERROR);
        Assert.isTrue(userInfo.getBorrowAuthStatus().intValue()== BorrowerStatusEnum.AUTH_OK.getStatus().intValue(),
                ResultEnum.USER_NO_AMOUNT_ERROR);

        //判断用户额度是否正确
        BigDecimal count = this.getBorrowAmount(userId);
        Assert.isTrue(borrowInfo.getAmount().doubleValue() <= count.doubleValue(),
                ResultEnum.USER_AMOUNT_LESS_ERROR);

        //存储数据
        borrowInfo.setUserId(userId);
        //百分比转成小数,年利率为百分数
        borrowInfo.setBorrowYearRate( borrowInfo.getBorrowYearRate().divide(new BigDecimal(100)));
        borrowInfo.setStatus(BorrowInfoStatusEnum.CHECK_RUN.getStatus());
        baseMapper.insert(borrowInfo);
    }

    /**
     * 获取用户的审核状态
     * @param userId
     * @return
     */
    @Override
    public Integer getStatusByUserId(Long userId) {
        QueryWrapper<BorrowInfo> borrowInfoQueryWrapper = new QueryWrapper<>();
        borrowInfoQueryWrapper.select("status").eq("user_id",userId);
        List<Object> objects = baseMapper.selectObjs(borrowInfoQueryWrapper);

        if(objects.size() == 0){
            //借款人尚未提交信息
            return BorrowInfoStatusEnum.NO_AUTH.getStatus();
        }
        Integer status = (Integer)objects.get(0);
        return status;
    }

    /**
     *
     * @return
     */
    @Override
    public List<BorrowInfo> selectList() {
        List<BorrowInfo> borrowInfoList = baseMapper.selectBorrowInfoList();
        borrowInfoList.forEach(borrowInfo -> {
            String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod", borrowInfo.getReturnMethod());
            String moneyUse = dictService.getNameByParentDictCodeAndValue("moneyUse", borrowInfo.getMoneyUse());
            String status = BorrowInfoStatusEnum.getMsgByStatus(borrowInfo.getStatus());
            borrowInfo.getParam().put("returnMethod", returnMethod);
            borrowInfo.getParam().put("moneyUse", moneyUse);
            borrowInfo.getParam().put("status", status);
        });

        return borrowInfoList;
    }

    /**
     * 根据用户id获取借款人信息和借款信息
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> getBorrowInfoDetail(Long id) {


        //查询借款对象
        BorrowInfo borrowInfo = baseMapper.selectById(id);
        //组装数据
        String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod", borrowInfo.getReturnMethod());
        String moneyUse = dictService.getNameByParentDictCodeAndValue("moneyUse", borrowInfo.getMoneyUse());
        String status = BorrowInfoStatusEnum.getMsgByStatus(borrowInfo.getStatus());
        borrowInfo.getParam().put("returnMethod", returnMethod);
        borrowInfo.getParam().put("moneyUse", moneyUse);
        borrowInfo.getParam().put("status", status);

        //根据user_id获取借款人对象
        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<Borrower>();
        borrowerQueryWrapper.eq("user_id", borrowInfo.getUserId());
        Borrower borrower = borrowerMapper.selectOne(borrowerQueryWrapper);
        //组装借款人对象
        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerDetailVOById(borrower.getId());

        //组装数据
        Map<String, Object> result = new HashMap<>();
        result.put("borrowInfo", borrowInfo);
        result.put("borrower", borrowerDetailVO);
        return result;
    }

    /**
     * 对借款信息进行审批
     *通过后将产生相关标地
     * @param borrowInfoApprovalVO
     */
    @Override
    public void approval(BorrowInfoApprovalVO borrowInfoApprovalVO) {

        //修改借款信息状态
        Long borrowInfoId = borrowInfoApprovalVO.getId();
        BorrowInfo borrowInfo = baseMapper.selectById(borrowInfoId);
        borrowInfo.setStatus(borrowInfoApprovalVO.getStatus());
        borrowInfo.setBorrowYearRate(borrowInfoApprovalVO.getLendYearRate().divide(new BigDecimal(100)));
        baseMapper.updateById(borrowInfo);

        //审核通过则创建标的
        if (borrowInfoApprovalVO.getStatus().intValue() == BorrowInfoStatusEnum.CHECK_OK.getStatus().intValue()) {
            //创建标的
            lendService.createLend(borrowInfoApprovalVO, borrowInfo);
        }
    }
}
