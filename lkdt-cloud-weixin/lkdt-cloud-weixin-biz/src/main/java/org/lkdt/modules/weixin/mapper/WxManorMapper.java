package org.lkdt.modules.weixin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.lkdt.modules.weixin.entity.WxManor;

import java.util.List;

/**
 * @Description: 微信用户路段管理
 * @Author: jeecg-boot
 * @Date:   2021-04-26
 * @Version: V1.0
 */
public interface WxManorMapper extends BaseMapper<WxManor> {

    List<WxManor> get(String openid);
}
