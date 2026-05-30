package com.jzo2o.customer.controller.open;
import com.jzo2o.api.publics.SmsCodeApi;
import com.jzo2o.common.constants.UserType;
import com.jzo2o.common.enums.SmsBussinessTypeEnum;
import com.jzo2o.common.expcetions.BadRequestException;
import com.jzo2o.common.utils.StringUtils;
import com.jzo2o.customer.model.dto.request.InstitutionRegisterReqDTO;
import com.jzo2o.customer.service.IServeProviderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RequestMapping("/open/serve-provider")
@RestController("openRegisterController")
@Api(tags = "开放接口 - 注册相关接口")
public class RegisterController {

    @Resource
    private IServeProviderService serveProviderService;
    @Resource
    private  SmsCodeApi smsCodeApi;
    @Resource
    private  PasswordEncoder passwordEncoder;

    /**
     * 机构端注册相关接口*/

    @ApiOperation("机构端注册相关接口")
    @PostMapping("/institution/register")
    @Transactional(rollbackFor = Exception.class)
    public void institutionRegister(@RequestBody InstitutionRegisterReqDTO institutionRegisterReqDTO)
    {
        log.info("机构端注册相关接口");
        String phone = institutionRegisterReqDTO.getPhone();
        String password = institutionRegisterReqDTO.getPassword();
        String verifyCode = institutionRegisterReqDTO.getVerifyCode();

        // 数据校验
        if(StringUtils.isEmpty(institutionRegisterReqDTO.getVerifyCode())){
            throw new BadRequestException("验证码错误，请重新获取");
        }
        //远程调用publics服务校验验证码是否正确
        boolean verifyResult =
                smsCodeApi.verify(phone,SmsBussinessTypeEnum.INSTITION_REGISTER,verifyCode).getIsSuccess();
        if(!verifyResult) {
            throw new BadRequestException("验证码错误，请重新获取");
        }
       serveProviderService.add(phone, UserType.INSTITUTION, passwordEncoder.encode(password));

    }

}
