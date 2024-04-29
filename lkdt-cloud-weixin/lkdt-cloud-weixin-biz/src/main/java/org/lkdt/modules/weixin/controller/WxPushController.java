package org.lkdt.modules.weixin.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.aspect.annotation.AutoLog;
import org.lkdt.common.system.base.controller.CloudController;
import org.lkdt.modules.weixin.entity.WxSubscribe;
import org.lkdt.modules.weixin.service.IWxSubscribeService;
import org.lkdt.modules.weixin.utils.wx.WxPushUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
* @Description: 微信用户绑定路段管理
* @Author: jeecg-boot
* @Date:   2021-04-26
* @Version: V1.0
*/
@Api(tags="微信用户绑定路段管理")
@RestController
@RequestMapping("/weixin/WxPush")
@Slf4j
public class WxPushController extends CloudController<WxSubscribe, IWxSubscribeService> {
   @Autowired
   private WxPushUtil wxPushUtil;

   /**
    * 发送待确认告警通知
    *
    * @return
    */
   @AutoLog(value = "发送待确认告警通知")
   @ApiOperation(value="发送待确认告警通知", notes="发送待确认告警通知")
   @GetMapping(value = "/sendConfirmNotice")
   public Result<?> sendConfirmNotice() {
       wxPushUtil.sendNotice();
       return Result.ok("发送成功");
   }

    /**
     * 发送待确认告警通知
     *
     * @return
     */
    @AutoLog(value = "发送已确认告警通知")
    @ApiOperation(value="发送已确认告警通知", notes="发送已确认告警通知")
    @PostMapping(value = "/sendAlarmInfoToPolice")
    Result<?> sendAlarmInfoToPolice(@RequestBody JSONObject alertThreepartJson){
        wxPushUtil.sendAlarmInfoToPolice(alertThreepartJson, alertThreepartJson.getString("epName"));
        return Result.ok("发送成功");
    }


}
