package org.lkdt.modules.weixin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lkdt.modules.weixin.entity.WxSubscribe;

import java.util.List;
import java.util.Map;

/**
 * @Description: 微信用户绑定路段管理
 * @Author: jeecg-boot
 * @Date:   2021-04-26
 * @Version: V1.0
 */
public interface IWxSubscribeService extends IService<WxSubscribe> {

    List<String> getOpenidsByHwId(String hwId);

    List<String> getHwIdsByOpenid(String openid);

    void removeByOpenIdAndHwId(Map<String, Object> map);

    boolean removeByOpenId(String openid);

    List<String> getOpenidsByWindId(String string);
}
