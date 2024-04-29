package org.lkdt.modules.weixin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.lkdt.modules.weixin.entity.WxUser;

/**
 * @Description: 微信用户管理
 * @Author: hjy
 * @Date:   2021-04-23
 * @Version: V1.0
 */
public interface WxUserMapper extends BaseMapper<WxUser> {

    WxUser getByUnionId(String unionid);

    WxUser getByMinOpenId(String minOpenId);
}
