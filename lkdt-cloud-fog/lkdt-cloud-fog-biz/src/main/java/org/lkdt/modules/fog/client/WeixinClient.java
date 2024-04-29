package org.lkdt.modules.fog.client;

import com.alibaba.fastjson.JSONObject;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Component
@FeignClient(contextId = "WeixinClient", value =ServiceNameConstants.WEIXIN_SERVICE)
public interface WeixinClient {

    @PostMapping(value = "/weixin/WxPush/sendAlarmInfoToPolice")
    Result<?> sendAlarmInfoToPolice(@RequestBody JSONObject alertThreepartJson);

//    /**
//     *
//     * @param jsonObject
//     * @return
//     */
//    @PostMapping("/weixin/zcWxSubscribe/sendWind")
//    Result<List<String>> sendWind(@RequestBody JSONObject jsonObject);
}
