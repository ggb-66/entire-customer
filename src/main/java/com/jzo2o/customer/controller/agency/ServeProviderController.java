package com.jzo2o.customer.controller.agency;


import com.jzo2o.api.publics.SmsCodeApi;
import com.jzo2o.common.constants.UserType;
import com.jzo2o.common.enums.SmsBussinessTypeEnum;
import com.jzo2o.common.expcetions.BadRequestException;
import com.jzo2o.common.utils.StringUtils;
import com.jzo2o.customer.model.domain.ServeProvider;
import com.jzo2o.customer.model.dto.request.InstitutionResetPasswordReqDTO;
import com.jzo2o.customer.model.dto.response.ServeProviderInfoResDTO;
import com.jzo2o.customer.service.IServeProviderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 服务人员/机构表 前端控制器
 * </p>
 *
 * @author itcast
 * @since 2023-07-17
 */
@Slf4j
@RestController("agencyServeProviderController")
@RequestMapping("/agency/serve-provider")
@Api(tags = "机构端 - 服务人员或机构相关接口")
public class ServeProviderController {
    @Resource
    private IServeProviderService serveProviderService;

    @Resource
    private SmsCodeApi smsCodeApi;
    @Resource
    private PasswordEncoder passwordEncoder;


    @GetMapping("/currentUserInfo")
    @ApiOperation("获取当前用户信息")
    public ServeProviderInfoResDTO currentUserInfo() {
        return serveProviderService.currentUserInfo();
    }



    @PostMapping("/institution/resetPassword")
    @ApiOperation("机构重置密码")
    public void resetPassword(@RequestBody InstitutionResetPasswordReqDTO institutionResetPasswordReqDTO)
    {


        log.info("机构重置密码,请求参数:{}    :{}", institutionResetPasswordReqDTO,passwordEncoder.encode(institutionResetPasswordReqDTO.getPassword()));
        // 数据校验
        if(StringUtils.isEmpty(institutionResetPasswordReqDTO.getVerifyCode())){
            throw new BadRequestException("验证码错误，请重新获取-1");
        }
        //远程调用publics服务校验验证码是否正确
        boolean verifyResult =
                smsCodeApi.verify(institutionResetPasswordReqDTO.getPhone(), SmsBussinessTypeEnum.INSTITUTION_RESET_PASSWORD,institutionResetPasswordReqDTO.getVerifyCode()).getIsSuccess();
        if(!verifyResult) {
            throw new BadRequestException("验证码错误，请重新获取-2");
        }

        ServeProvider serveProvider = serveProviderService
                .findByPhoneAndType(institutionResetPasswordReqDTO.getPhone(), UserType.INSTITUTION);
        if(serveProvider == null) {
            throw new BadRequestException("该手机号未注册");
        }
        if(passwordEncoder.matches(institutionResetPasswordReqDTO.getPassword(),serveProvider.getPassword())) {
            throw new BadRequestException("新密码不能与旧密码相同");
        }

        serveProvider.setPassword(passwordEncoder.encode(institutionResetPasswordReqDTO.getPassword()));
        serveProviderService.updateById(serveProvider);

    }
}
