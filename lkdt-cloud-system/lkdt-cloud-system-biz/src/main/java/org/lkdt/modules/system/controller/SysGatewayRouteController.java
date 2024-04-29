package org.lkdt.modules.system.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.system.base.controller.CloudController;
import org.lkdt.common.util.oConvertUtils;
import org.lkdt.modules.system.entity.SysGatewayRoute;
import org.lkdt.modules.system.service.ISysGatewayRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: gateway路由管理
 * @Author: jeecg-boot
 * @Date:   2020-05-26
 * @Version: V1.0
 */
@Api(tags="gateway路由管理")
@RestController
@RequestMapping("/sys/gatewayRoute")
@Slf4j
public class SysGatewayRouteController extends CloudController<SysGatewayRoute, ISysGatewayRouteService> {

	@Autowired
	private ISysGatewayRouteService sysGatewayRouteService;

	@PostMapping(value = "/updateAll")
	public Result<?> updateAll(@RequestBody JSONObject json) {
		JSONObject route = json.getJSONObject("router");
		sysGatewayRouteService.updateAll(route);
		return Result.ok("操作成功！");
	}

	@GetMapping(value = "/list")
	public Result<?> queryPageList(SysGatewayRoute sysGatewayRoute) {
		LambdaQueryWrapper<SysGatewayRoute> query = new LambdaQueryWrapper<>();
		query.eq(SysGatewayRoute::getStatus,1);
		List<SysGatewayRoute> ls = sysGatewayRouteService.list(query);
		JSONArray array = new JSONArray();
		for(SysGatewayRoute rt: ls){
			JSONObject obj = (JSONObject)JSONObject.toJSON(rt);
			if(oConvertUtils.isNotEmpty(rt.getPredicates())){
				obj.put("predicates", JSONArray.parseArray(rt.getPredicates()));
			}
			if(oConvertUtils.isNotEmpty(rt.getFilters())){
				obj.put("filters", JSONArray.parseArray(rt.getFilters()));
			}
			array.add(obj);
		}
		return Result.ok(array);
	}

	@GetMapping(value = "/clearRedis")
	public Result<?> clearRedis() {
		sysGatewayRouteService.clearRedis();
		return Result.ok("清除成功！");
	}

}
