package com.liuuki.srb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liuuki.srb.entity.TransFlow;
import com.liuuki.srb.entity.bo.TransFlowBO;

/**
 * <p>
 * 交易流水表 服务类
 * </p>
 *
 * @author liuuki
 * @since 2022-03-04
 */
public interface TransFlowService extends IService<TransFlow> {

    void saveTransFlow(TransFlowBO transFlowBO);

    boolean isSaveTransFlow(String agentBillNo);
}
