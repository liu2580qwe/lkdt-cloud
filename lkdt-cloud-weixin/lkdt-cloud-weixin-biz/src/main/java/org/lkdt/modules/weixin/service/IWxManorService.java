package org.lkdt.modules.weixin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lkdt.modules.weixin.entity.WxManor;

import java.util.List;

/**
 * @Description: 微信用户路段管理
 * @Author: jeecg-boot
 * @Date:   2021-04-26
 * @Version: V1.0
 */
public interface IWxManorService extends IService<WxManor> {

    List<WxManor> get(String openid);
}
