package com.liuuki.srb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuuki.exception.Assert;
import com.liuuki.srb.dao.BorrowerAttachMapper;
import com.liuuki.srb.dao.UserInfoMapper;
import com.liuuki.srb.dao.UserIntegralMapper;
import com.liuuki.srb.entity.Borrower;
import com.liuuki.srb.dao.BorrowerMapper;
import com.liuuki.srb.entity.BorrowerAttach;
import com.liuuki.srb.entity.UserInfo;
import com.liuuki.srb.entity.UserIntegral;
import com.liuuki.srb.entity.vo.BorrowerApprovalVO;
import com.liuuki.srb.entity.vo.BorrowerAttachVO;
import com.liuuki.srb.entity.vo.BorrowerDetailVO;
import com.liuuki.srb.entity.vo.BorrowerVO;
import com.liuuki.srb.enums.BorrowerStatusEnum;
import com.liuuki.srb.enums.IntegralEnum;
import com.liuuki.srb.service.BorrowerAttachService;
import com.liuuki.srb.service.BorrowerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuuki.srb.service.DictService;
import com.liuuki.srb.service.UserInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 借款人 服务实现类
 * </p>
 *
 * @author liuuki
 * @since 2022-03-04
 */
@Service
public class BorrowerServiceImpl extends ServiceImpl<BorrowerMapper, Borrower> implements BorrowerService {
    @Resource
    private BorrowerAttachMapper borrowerAttachMapper;
    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private DictService dictService;
    @Resource
    private BorrowerAttachService borrowerAttachService;

    @Resource
    private UserIntegralMapper userIntegralMapper; //积分mapper


    /**
     * 用户申请借款额度，将基本信息保存到相关数据库中
     * @param borrowerVO
     * @param userId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveBorrower(BorrowerVO borrowerVO, Long userId) {
        //获取用户信息
        UserInfo userInfo = userInfoMapper.selectById(userId);

        //保存借款人信息
        Borrower borrower = new Borrower();
        BeanUtils.copyProperties(borrowerVO,borrower);
        borrower.setUserId(userId);
        borrower.setName(userInfo.getName());
        borrower.setIdCard(userInfo.getIdCard());
        borrower.setMobile(userInfo.getMobile());
        borrower.setStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());//认证中
        baseMapper.insert(borrower);

        //保存附件到数据库
        List<BorrowerAttach> borrowerAttachList= borrowerVO.getBorrowerAttachList();
        borrowerAttachList.forEach(borrowerAttach -> {
            borrowerAttach.setBorrowerId(borrower.getId());
            borrowerAttachMapper.insert(borrowerAttach);
        });
        //更新会员状态，更新为认证中
        userInfo.setBorrowAuthStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public Integer getStatusByUserId(Long userId) {
        QueryWrapper<Borrower> borrowerQueryWrapper=new QueryWrapper<>();
        borrowerQueryWrapper.select("status").eq("user_id", userId);
        Borrower borrower = baseMapper.selectOne(borrowerQueryWrapper);
        if(borrower ==null){
            //借款人尚未提交信息
            return BorrowerStatusEnum.NO_AUTH.getStatus();
        }
        return borrower.getStatus();

    }

    /**
     * 获取借款人列表
     * @param pageParam
     * @param condition
     * @return
     */
    @Override
    public IPage<Borrower> listPage(Page<Borrower> pageParam, String condition) {
        if(!StringUtils.isNoneBlank(condition)){
            return baseMapper.selectPage(pageParam,null);
        }
        QueryWrapper<Borrower> borrowerQueryWrapper=new QueryWrapper<>();
        borrowerQueryWrapper.like("name",condition)
                .or().like("id_card", condition)
                .or().like("mobile", condition)
                .orderByDesc("id");
        return baseMapper.selectPage(pageParam, borrowerQueryWrapper);
    }

    /**
     * 展示借款人得详细信息，以及各种照片
     * @param id
     * @return
     */
    @Override
    public BorrowerDetailVO getBorrowerDetailVOById(Long id) {
        //获取借款人信息
        Borrower borrower = baseMapper.selectById(id) ;

        //填充基本借款人信息
        BorrowerDetailVO borrowerDetailVO = new BorrowerDetailVO();
        BeanUtils.copyProperties(borrower, borrowerDetailVO);

        //为一些下拉框值进行重幅值
        borrowerDetailVO.setMarry(borrower.getMarry()?"是":"否");
        borrowerDetailVO.setSex(borrower.getSex()==1?"男":"女");
        String education = dictService.getNameByParentDictCodeAndValue("education", borrower.getEducation());
        String industry = dictService.getNameByParentDictCodeAndValue("moneyUse", borrower.getIndustry());
        String income = dictService.getNameByParentDictCodeAndValue("income", borrower.getIncome());
        String returnSource = dictService.getNameByParentDictCodeAndValue("returnSource", borrower.getReturnSource());
        String contactsRelation = dictService.getNameByParentDictCodeAndValue("relation", borrower.getContactsRelation());
        borrowerDetailVO.setEducation(education);
        borrowerDetailVO.setIndustry(industry);
        borrowerDetailVO.setIncome(income);
        borrowerDetailVO.setReturnSource(returnSource);
        borrowerDetailVO.setContactsRelation(contactsRelation);
        //审批状态
        String status = BorrowerStatusEnum.getMsgByStatus(borrower.getStatus());
        borrowerDetailVO.setStatus(status);


        //获取附件VO列表
        //获取附件VO列表
        List<BorrowerAttachVO> borrowerAttachVOList =  borrowerAttachService.selectBorrowerAttachVOList(id);
        borrowerDetailVO.setBorrowerAttachVOList(borrowerAttachVOList);
        return borrowerDetailVO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void approval(BorrowerApprovalVO borrowerApprovalVO) {
        //借款人认证状态
        Long borrowerId = borrowerApprovalVO.getBorrowerId();
        Borrower borrower = baseMapper.selectById(borrowerId);
        borrower.setStatus(borrowerApprovalVO.getStatus());//通过或不通过
        baseMapper.updateById(borrower);

        //获取用户信息，用于积分的更新
        Long userId = borrower.getUserId();
        UserInfo userInfo = userInfoMapper.selectById(userId);

        //积分更新

        //申请：借款人基本信息积分
        UserIntegral userIntegral = new UserIntegral();
        userIntegral.setUserId(userId);
        userIntegral.setIntegral(borrowerApprovalVO.getInfoIntegral());
        userIntegral.setContent("借款人基本信息");
        userIntegralMapper.insert(userIntegral);

        //用户基本积分=现有积分+借款人基本信息积分
        int curIntegral = userInfo.getIntegral() + borrowerApprovalVO.getInfoIntegral();

        //判断是否有身份正积分，有则添加积分记录到数据库中
        if(borrowerApprovalVO.getIsIdCardOk()) {
            curIntegral += IntegralEnum.BORROWER_IDCARD.getIntegral();
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_IDCARD.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_IDCARD.getMsg());
            userIntegralMapper.insert(userIntegral);
        }
        //判断是否有房产证积分，有则添加积分记录到数据库中
        if(borrowerApprovalVO.getIsHouseOk()) {
            curIntegral += IntegralEnum.BORROWER_HOUSE.getIntegral();
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_HOUSE.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_HOUSE.getMsg());
            userIntegralMapper.insert(userIntegral);
        }
        //判断是否有车产积分，有则添加积分记录到数据库中
        if(borrowerApprovalVO.getIsCarOk()) {
            curIntegral += IntegralEnum.BORROWER_CAR.getIntegral();
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_CAR.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_CAR.getMsg());
            userIntegralMapper.insert(userIntegral);
        }

        //将积分添加到用户信息中，并更新
        userInfo.setIntegral(curIntegral);
        //修改审核状态
        userInfo.setBorrowAuthStatus(borrowerApprovalVO.getStatus());
        userInfoMapper.updateById(userInfo);
    }
}
