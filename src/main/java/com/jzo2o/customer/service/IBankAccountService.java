package com.jzo2o.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jzo2o.customer.model.domain.BankAccount;
import com.jzo2o.customer.model.dto.request.BankAccountUpsertReqDTO;
import com.jzo2o.customer.model.dto.response.BankAccountResDTO;

/**
 * <p>
 * 银行账户 服务类
 * </p>
 *
 * @author jzo2o
 * @since 2026-05-23
 */
public interface IBankAccountService extends IService<BankAccount> {

    /**
     * 获取当前用户的银行账户信息
     *
     * @param id 用户id（服务人员或机构id）
     * @return 银行账户响应数据
     */
    BankAccountResDTO getCurrentUserBankAccount(Long id);

    /**
     * 新增或更新当前用户的银行账户信息
     *
     * @param id 用户id（服务人员或机构id）
     * @param type 账户类型（2：服务人员，3：服务机构）
     * @param dto 前端上传的账户参数
     */
    void upsertBankAccount(Long id, Integer type, BankAccountUpsertReqDTO dto);
}