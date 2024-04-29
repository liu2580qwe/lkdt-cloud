package org.lkdt.modules.weixin.api;

import org.lkdt.common.api.vo.Result;
import org.lkdt.common.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author HuangJunYao
 * @date 2021/4/30
 */
@Component
@FeignClient(contextId = "weixinApi", value = ServiceNameConstants.WEIXIN_SERVICE)
public interface WeixinApi {
    /**
     *
     * @param openid
     * @return
     */

    @GetMapping("/weixin/zcWxSubscribe/getHwIdsByOpenid")
    Result<List<String>> getHwIdsByOpenid(@RequestParam String openid);

}
