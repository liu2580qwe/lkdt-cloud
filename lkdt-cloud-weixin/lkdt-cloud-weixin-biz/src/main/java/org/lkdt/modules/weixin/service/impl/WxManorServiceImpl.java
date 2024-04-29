package org.lkdt.modules.weixin.service.impl;

import org.lkdt.modules.weixin.entity.WxManor;
import org.lkdt.modules.weixin.mapper.WxManorMapper;
import org.lkdt.modules.weixin.mapper.WxUserMapper;
import org.lkdt.modules.weixin.service.IWxManorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 微信用户路段管理
 * @Author: jeecg-boot
 * @Date:   2021-04-26
 * @Version: V1.0
 */
@Service
public class WxManorServiceImpl extends ServiceImpl<WxManorMapper, WxManor> implements IWxManorService {
    @Autowired
    private WxManorMapper wxManorMapper;

    @Override
    public List<WxManor> get(String openid) {
        return wxManorMapper.get(openid);
    }
}
