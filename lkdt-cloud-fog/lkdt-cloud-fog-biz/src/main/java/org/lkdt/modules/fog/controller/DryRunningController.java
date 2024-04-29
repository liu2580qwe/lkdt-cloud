package org.lkdt.modules.fog.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.api.R;
import com.google.gson.JsonObject;
import io.swagger.annotations.ApiOperation;
import netscape.javascript.JSObject;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.lkdt.common.api.vo.Result;
import org.lkdt.modules.fog.service.DryRunningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * 沙盘推演
 * 
 * @author wy
 *
 */

@Controller
@RequestMapping("/fog/dryRunning")
public class DryRunningController {

	@Autowired
	private DryRunningService dryRunningService;

	@GetMapping()
	@RequiresPermissions("fog:dryRunning:dryRunningPage")
	public String dryRunningPage() {
		return "/fog/dryRunning/dryRunningPage";
	}

	@ApiOperation(value = "执行沙盘推演，推演数据", notes = "")
	@ResponseBody
	@RequestMapping("/sandTableData")
	public Result sandTableData(@RequestBody JSONObject jsonObject) {
		String starttime=(String) jsonObject.get("starttime");
		String endtime=(String) jsonObject.get("endtime");
		Timestamp start = Timestamp.valueOf(starttime);
		Timestamp end = Timestamp.valueOf(endtime);
		String requestStr=(String) jsonObject.get("requestStr");
		Integer intervalTime=Integer.valueOf((String) jsonObject.get("intervalTime"));
		Integer magnification=Integer.valueOf((String) jsonObject.get("magnification"));
		return dryRunningService.sandTableData(start, end, requestStr, intervalTime ,magnification);
	}

}
