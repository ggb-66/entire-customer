package com.jzo2o.customer.controller.worker;

import com.jzo2o.customer.model.dto.request.BankAccountUpsertReqDTO;
import com.jzo2o.customer.model.dto.response.BankAccountResDTO;
import com.jzo2o.customer.service.IBankAccountService;
import com.jzo2o.mvc.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/worker/bank-account")
@Api(tags = "服务端-银行账户相关接口")
public class BankAccountController {

    @Resource
    private IBankAccountService bankAccountService;

    @GetMapping("/currentUserBankAccount")
    @ApiOperation("获取当前服务人员的银行账户信息")
    public BankAccountResDTO getCurrentUserBankAccount() {
        Long userId = UserContext.currentUserId();
        log.info("获取服务人员银行账户，用户id:{}", userId);
        return bankAccountService.getCurrentUserBankAccount(userId);
    }

    @PostMapping
    @ApiOperation("更新当前服务人员的银行账户信息")
    public void update(@RequestBody BankAccountUpsertReqDTO bankAccountUpsertReqDTO) {
        Long userId = UserContext.currentUserId();
        log.info("更新服务人员银行账户，用户id:{}, 参数:{}", userId, bankAccountUpsertReqDTO);
        // 传入类型 2：代表服务人员
        bankAccountService.upsertBankAccount(userId, bankAccountUpsertReqDTO.getType(), bankAccountUpsertReqDTO);
    }
}
