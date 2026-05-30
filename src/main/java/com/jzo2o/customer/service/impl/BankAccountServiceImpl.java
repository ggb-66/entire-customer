package com.jzo2o.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.common.utils.BeanUtils;
import com.jzo2o.common.utils.ObjectUtils;
import com.jzo2o.customer.mapper.BankAccountMapper;
import com.jzo2o.customer.model.domain.BankAccount;
import com.jzo2o.customer.model.dto.request.BankAccountUpsertReqDTO;
import com.jzo2o.customer.model.dto.response.BankAccountResDTO;
import com.jzo2o.customer.service.IBankAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 银行账户 服务实现类
 * </p>
 *
 * @author jzo2o
 * @since 2026-05-23
 */
@Service
public class BankAccountServiceImpl extends ServiceImpl<BankAccountMapper, BankAccount> implements IBankAccountService {

    @Override
    public BankAccountResDTO getCurrentUserBankAccount(Long id) {
        // 1. 去数据库真正地把记录查出来
        BankAccount bankAccount = baseMapper.selectById(id);
        
        // 2. 判空处理：如果数据库还没录入该账户，直接返回 null 或者空对象，防止前端报错
        if (ObjectUtils.isNull(bankAccount)) {
            return null;
        }
        
        // 3. 属性复制并返回
        BankAccountResDTO bankAccountResDTO = new BankAccountResDTO();
        BeanUtils.copyProperties(bankAccount, bankAccountResDTO);
        return bankAccountResDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 涉及修改，添加事务控制
    public void upsertBankAccount(Long id, Integer type, BankAccountUpsertReqDTO dto) {
        // 1. 根据 id 检查数据库中是否存在记录
        BankAccount existAccount = baseMapper.selectById(id);

        BankAccount bankAccount = new BankAccount();
        BeanUtils.copyProperties(dto, bankAccount);
        bankAccount.setId(id);
        bankAccount.setType(type); // 💡 动态设置类型：由 Controller 根据路由端点传入

        if (ObjectUtils.isNull(existAccount)) {
            // 2. 没记录，走新增
            // 注意：如果你配置了 MyBatis-Plus 的公共字段自动填充，createTime/updateTime 可不写
            baseMapper.insert(bankAccount);
        } else {
            // 3. 有记录，走修改
            baseMapper.updateById(bankAccount);
        }
    }
}