package org.lkdt.modules.weixin.controller;

import com.alibaba.fastjson.JSONObject;
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
import io.swagger.annotations.Api;

import java.util.List;

/**
 * @Description: 微信用户绑定路段管理
 * @Author: jeecg-boot
 * @Date:   2021-04-26
 * @Version: V1.0
 */
@Api(tags="微信用户绑定路段管理")
@RestController
@RequestMapping("/weixin/zcWxSubscribe")
@Slf4j
public class WxSubscribeController extends CloudController<WxSubscribe, IWxSubscribeService> {
	@Autowired
	private IWxSubscribeService zcWxSubscribeService;

	@Autowired
	private WxPushUtil wxPushUtil;

	/**
	 * obtain road segment id according to openid
	 * @return
	 */
	@AutoLog(value = "根据openid获取路段id")
	@ApiOperation(value="根据openid获取路段id", notes="根据openid获取路段id")
	@GetMapping(value = "/getHwIdsByOpenid")
	public Result<?> getHwIdsByOpenid(@RequestParam String openid) {
		Result<List<String>> result = new Result<>();
		List<String> hwIds = zcWxSubscribeService.getHwIdsByOpenid(openid);
		result.setResult(hwIds);
		result.setSuccess(true);
		return result;
	}

	/**
	 * send alert notification to be confirmed
	 * @return
	 */
	@AutoLog(value = "send alert notification to be confirmed")
	@ApiOperation(value="send alert notification to be confirmed", notes="send alert notification to be confirmed")
	@PostMapping(value = "/sendWind")
	public Result<?> sendWind(@RequestBody JSONObject jsonObject) {
		try{
			wxPushUtil.sendWind(jsonObject);
			return Result.ok("success");
		}catch (Exception e){
			return Result.error(e.getMessage());
		}
	}
	

}
