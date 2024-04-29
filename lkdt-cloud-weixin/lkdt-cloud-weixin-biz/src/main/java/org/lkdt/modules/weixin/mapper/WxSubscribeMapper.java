package org.lkdt.modules.weixin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.lkdt.modules.weixin.entity.WxSubscribe;

import java.util.List;
import java.util.Map;

/**
 * @Description: 微信用户绑定路段管理
 * @Author: jeecg-boot
 * @Date:   2021-04-26
 * @Version: V1.0
 */
@Mapper
public interface WxSubscribeMapper extends BaseMapper<WxSubscribe> {
    List<String> getOpenidsByHwId(String hwId);

    List<String> getHwIdsByOpenid(String openid);

    void removeByOpenIdAndHwId(Map<String, Object> map);

    void removeByOpenId(String openid);

    List<String> getOpenidsByWindId(String string);
}
