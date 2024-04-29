package org.lkdt.modules.weixin.service.impl;

import org.lkdt.modules.weixin.entity.WxSubscribe;
import org.lkdt.modules.weixin.mapper.WxSubscribeMapper;
import org.lkdt.modules.weixin.service.IWxSubscribeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.Map;

/**
 * @Description: 微信用户绑定路段管理
 * @Author: jeecg-boot
 * @Date:   2021-04-26
 * @Version: V1.0
 */
@Service
public class WxSubscribeServiceImpl extends ServiceImpl<WxSubscribeMapper, WxSubscribe> implements IWxSubscribeService {
    @Autowired
    private WxSubscribeMapper wxSubscribeMapper;
    @Override
    public List<String> getOpenidsByHwId(String hwId) {
        return wxSubscribeMapper.getOpenidsByHwId(hwId);
    }

    @Override
    public List<String> getHwIdsByOpenid(String openid) {
        return wxSubscribeMapper.getHwIdsByOpenid(openid);
    }

    @Override
    public void removeByOpenIdAndHwId(Map<String, Object> map) {
        wxSubscribeMapper.removeByOpenIdAndHwId(map);
    }

    @Override
    public boolean removeByOpenId(String openid) {
        wxSubscribeMapper.removeByOpenId(openid);
        return true;
    }

    @Override
    public List<String> getOpenidsByWindId(String string) {
        return wxSubscribeMapper.getOpenidsByWindId(string);
    }
}
