package org.lkdt.modules.weixin.service;

import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.service.IService;
import org.lkdt.common.api.vo.Result;
import org.lkdt.modules.weixin.entity.WxUser;

/**
 * @Description: 微信用户管理
 * @Author: hjy
 * @Date:   2021-04-23
 * @Version: V1.0
 */
public interface IWxUserService extends IService<WxUser> {

    Result<?> getPubOpenIdByMinCode(String minCode);

    Result<?> getUserInfo(String code, String encryptedData, String iv);

    Result<?> phoneNumber(String pubOpenId, String code, String encryptedData, String iv);

    Object getAuthStatus(String pubOpenId);
}
