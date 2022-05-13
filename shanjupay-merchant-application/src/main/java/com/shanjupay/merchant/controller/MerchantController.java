package com.shanjupay.merchant.controller;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.PhoneUtil;
import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.convert.MerchantRegisterConvert;
import com.shanjupay.merchant.service.SmsService;
import com.shanjupay.merchant.vo.MerchantRegisterVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Administrator
 * @version 1.0
 **/
@RestController
@Api(value="商户平台应用接口",tags = "商户平台应用接口",description = "商户平台应用接口")
public class MerchantController {

    @org.apache.dubbo.config.annotation.Reference
    MerchantService merchantService;

    @Autowired //注入本地的bean
    SmsService smsService;


    @ApiOperation(value="根据id查询商户信息")
    @GetMapping("/merchants/{id}")
    public MerchantDTO queryMerchantById(@PathVariable("id") Long id){

        MerchantDTO merchantDTO = merchantService.queryMerchantById(id);
        return merchantDTO;
    }

    @ApiOperation(value = "获取手机验证码")
    @GetMapping("/sms")
    @ApiImplicitParam(value = "手机号",name = "phone",required = true,dataType = "string",paramType = "query")
    public String getSMSCode(@RequestParam("phone") String phone){
        //向验证码服务请求发送验证码
        return smsService.sendMsg(phone);
    }

    @ApiOperation("商户注册")
    @ApiImplicitParam(value = "商户注册信息",name = "merchantRegisterVO",required = true,dataType = "MerchantRegisterVO",paramType = "body")
    @PostMapping("/merchants/register")
    public MerchantRegisterVO registerMerchant(@RequestBody MerchantRegisterVO merchantRegisterVO){

        //校验参数的合法性
        if(merchantRegisterVO == null){
            throw new BusinessException(CommonErrorCode.E_100108);
        }
        if(StringUtils.isBlank(merchantRegisterVO.getMobile())){
            throw new BusinessException(CommonErrorCode.E_100112);
        }
        //手机号格式校验
        if(!PhoneUtil.isMatches(merchantRegisterVO.getMobile())){
            throw new BusinessException(CommonErrorCode.E_100109);
        }

        //校验验证码
        smsService.checkVerifiyCode(merchantRegisterVO.getVerifiykey(),merchantRegisterVO.getVerifiyCode());
        //调用dubbo服务接口
//        MerchantDTO merchantDTO = new MerchantDTO();
        //向dto写入商户注册的信息
//        merchantDTO.setMobile(merchantRegisterVO.getMobile());
//        merchantDTO.setUsername(merchantRegisterVO.getUsername());
        //...
        //使用MapStruct转换对象
        MerchantDTO merchantDTO = MerchantRegisterConvert.INSTANCE.vo2dto(merchantRegisterVO);
        merchantService.createMerchant(merchantDTO);
        return merchantRegisterVO;
    }
}
