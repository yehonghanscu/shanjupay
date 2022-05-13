package com.shanjupay.merchant.api;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.merchant.api.dto.MerchantDTO;

/**
 * Created by Administrator.
 */
public interface MerchantService {

    //根据 id查询商户
    MerchantDTO queryMerchantById(Long id);

    /**
     *  注册商户服务接口，接收账号、密码、手机号，为了可扩展性使用merchantDto接收数据
     * @param merchantDTO 商户注册信息
     * @return 注册成功的商户信息
     */
    MerchantDTO createMerchant(MerchantDTO merchantDTO) throws BusinessException;
}
