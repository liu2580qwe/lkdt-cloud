package org.lkdt.modules.fog.controller;
import io.swagger.annotations.ApiOperation;
import org.lkdt.modules.fog.service.FogTrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
/**
 * @author zhangzb
 * @date 2019-06-18 10:05:01
 */
@Controller
@RequestMapping("/fog/fogTrack")
public class FogTrackController {
	@Autowired
	private FogTrackService fogTrackService;
	
	/**
	 * 根据用户绑定路段查询告警摄像头列表
	 * @param params
	 * @return
	 */
	@ResponseBody
	@GetMapping("/selectRealAlarmInfoListMerge")
	@ApiOperation("根据用户绑定路段查询告警摄像头列表")
	public String selectRealAlarmInfoListMerge(@RequestParam(required = false) Map<String, Object> params) {
		//登录用户不为空
		return fogTrackService.selectRealAlarmInfoListMerge();
	}
}
