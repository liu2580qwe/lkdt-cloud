package org.lkdt.modules.weixin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.system.vo.SysDepartModel;
import org.lkdt.common.util.DateUtils;
import org.lkdt.common.util.RandomValidateCodeUtil;
import org.lkdt.common.util.ShiroUtils;
import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.api.HighwayApi;
import org.lkdt.modules.api.SysBaseRemoteApi;
import org.lkdt.modules.api.SysUserRemoteApi;
import org.lkdt.modules.fog.api.FogApi;
import org.lkdt.modules.fog.calculator.FcFactory;
import org.lkdt.modules.fog.calculator.FogCalculator;
import org.lkdt.modules.fog.entity.AlarmRoadModel;
import org.lkdt.modules.fog.entity.EquipmentModel;
import org.lkdt.modules.weixin.entity.WxUser;
import org.lkdt.modules.weixin.service.IWxService;
import org.lkdt.modules.weixin.service.IWxSubscribeService;
import org.lkdt.modules.weixin.service.IWxUserService;
import org.lkdt.modules.weixin.utils.CheckUtil;
import org.lkdt.modules.weixin.utils.IflytekVoiceCloudUtil;
import org.lkdt.modules.weixin.utils.MessageUtil;
import org.lkdt.modules.weixin.utils.XmlUtil;
import org.lkdt.modules.weixin.utils.wx.WxPushUtil;
import org.lkdt.modules.weixin.utils.wx.WxUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author HuangJunYao
 * @date 2021/4/27
 */
@Controller
@Slf4j
@RequestMapping("/wx")
public class WxMapController {

	@Autowired
	private IWxSubscribeService wxSubscribeService;

	@Autowired
	private IWxUserService wxUserService;

	@Autowired
	private IWxService wxService;

	@Autowired
	private WxPushUtil wxPushUtil;

	@Autowired
	private HighwayApi highwayApi;

	@Autowired
	private SysBaseRemoteApi sysBaseRemoteApi;

	@Autowired
	private SysUserRemoteApi sysUserRemoteApi;

	@Autowired
	private FogApi fogApi;

	@Autowired
	private FcFactory fcFactory;

	@ApiOperation(value = "获取小程序二维码", notes = "")
	@RequestMapping("/levelControl/getMiniGramCode")
	@ResponseBody
	public String getMiniGramCode() {
		return WxUtil.getMiniGramCode();
	}

	@ApiOperation(value = "获取验证码", notes = "")
	@RequestMapping("/levelControl/getCode")
	@ResponseBody
	public JSONObject getCode() {
		return new RandomValidateCodeUtil().getRandCode();
	}

	@ApiOperation(value = "语音服务", notes = "")
	@RequestMapping("/map/voice")
	public void tts_index(String text,HttpServletResponse response) {
		new IflytekVoiceCloudUtil().getVoice(text, response);
	}

    @ApiOperation(value = "语音服务", notes = "")
    @RequestMapping("/map/voice/demo")
    @ResponseBody
    public String tts_index_demo(String text) {
        return new IflytekVoiceCloudUtil().getVoiceBase64(text);
    }

	@ApiOperation(value = "地图主页", notes = "")
	@RequestMapping("/map/mapHome")
	@ResponseBody
	public String mapHome() {
		return WxUtil.MAP_HOME;
	}

	@ApiOperation(value = "微信地图-小程序跳转", notes = "")
	@RequestMapping("/map/wxMapJump")
	public ModelAndView wxMapJump(String pubOpenId) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("weixin/map/wxMapJump");
		mav.addObject("openid",pubOpenId);
		return mav;
	}
	
	@ApiOperation(value = "微信地图-小程序跳转", notes = "")
	@RequestMapping("/map/index")
	public ModelAndView index(String pubOpenId) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("weixin/map/index");
		mav.addObject("openid",pubOpenId);
		return mav;
	}

	@ApiOperation(value = "微信地图-测试", notes = "")
	@RequestMapping("/map/wxMapTest")
	public ModelAndView wxMapTest(String pubOpenId) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("weixin/map/wxMapTest");
		mav.addObject("openid",pubOpenId);
		return mav;
	}

	@ApiOperation(value = "跳转路段绑定", notes = "")
	@RequestMapping("/map/bindRoadMulti")
	public String bindRoadMulti() {
		return "weixin/map/bindRoadMulti";
	}

	@ApiOperation(value = "跳转路段绑定v2", notes = "")
	@RequestMapping("/map/bindRoadMulti_v2")
	public String bindRoadMulti_select_v2() {
		return "weixin/map/bindRoadMulti_v2";
	}
	
	@ApiOperation(value = "跳转路段绑定选择", notes = "")
	@RequestMapping("/map/bindRoadMulti_select")
	public String bindRoadMulti_select() {
		return "weixin/map/bindRoadMulti_select";
	}

	@GetMapping("/cameraAbnormal")
	String cameraAbnormal(Model model) {
		return "fog/alarm/cameraAbnormal";
	}

    /**
     * 获取已关注路段
     * @return
     */
    @PostMapping("/getSubscribeRoad")
	@ResponseBody
    public JSONArray getSubscribeRoad() {
		return wxService.getSubscribeRoad();
    }

	@ApiOperation(value = "获取openid", notes = "")
	@RequestMapping("/map/getOpenFid")
	@ResponseBody
	public String getOpenFid(@RequestBody String body) {
		JSONObject obj = JSONObject.parseObject(body);
		return wxService.getOpenFid(obj);
	}

	@ApiOperation(value = "获取config", notes = "")
	@RequestMapping("/map/getConfig")
	@ResponseBody
	public String getConfig(@RequestBody String body) {
		JSONObject obj = JSONObject.parseObject(body);
		return wxService.getConfig(obj);
	}

	@ApiOperation(value = "根据已关注路段获取摄像头", notes = "")
	@RequestMapping("/map/getEquWWxxByHw")
	@ResponseBody
	public String getEquWWxxByHw(@RequestBody String body) {
		JSONObject obj = JSONObject.parseObject(body);
		return wxService.getEquWWxxByHw();
	}

	@ApiOperation(value = "根据已关注路段获取摄像头", notes = "")
	@RequestMapping("/map/getEquWWxxByHwAbn")
	@ResponseBody
	public String getEquWWxxByHwAbn(@RequestBody String body) {
		JSONObject obj = JSONObject.parseObject(body);
		return wxService.getEquWWxxByHwAbn();
	}

	@ApiOperation(value = "获取所有路段摄像头数据", notes = "")
	@RequestMapping("/map/getEquWWxxAll")
	@ResponseBody
	public String getEquWWxxAll(@RequestBody String body) {
		JSONObject obj = JSONObject.parseObject(body);
		return wxService.getEquWWxxAll();
	}

	@ApiOperation(value = "获取路段信息", notes = "")
	@RequestMapping("/map/getHighways")
	@ResponseBody
	public String getHighways(@RequestBody String body) {
		JSONObject obj = JSONObject.parseObject(body);
		return wxService.getHighways(Long.valueOf(String.valueOf(obj.get("hwId"))));
	}

	@ApiOperation(value = "获取用户关注路段信息", notes = "")
	@RequestMapping("/map/getHwIdsByOpenid")
	@ResponseBody
	public String getHwIdsByOpenid(@RequestBody String body) {
		JSONObject obj = JSONObject.parseObject(body);
		return wxService.getHwIdsByOpenid();
	}

	@ApiOperation(value = "获取用户关注路段信息-菜单结构", notes = "")
	@RequestMapping("/map/getTreeHwIdsByOpenid")
	@ResponseBody
	public String getTreeHwIdsByOpenid(@RequestBody String body) {
		return wxService.getTreeHwIdsByOpenid();
	}

	@ApiOperation(value = "保存路段信息", notes = "")
	@RequestMapping("/map/saveHwInfo")
	@ResponseBody
	public Result<?> saveHwInfo(@RequestBody String body) {
		JSONObject obj = JSONObject.parseObject(body);
		return wxService.saveHwInfo(obj);
	}

	@ApiOperation(value = "删除关联路段", notes = "")
	@RequestMapping("/map/deleteHwOp")
	@ResponseBody
	public Result<?> deleteHwOp(@RequestBody String body) {
		JSONObject obj = JSONObject.parseObject(body);
		return wxService.deleteHwOp(obj);
	}

	@ApiOperation(value = "告警雾霾，散雾", notes = "")
	@RequestMapping(value = "/map/getWXAlarm")
	@ResponseBody
	public String getWXAlarm(@RequestBody String body) {
//		JSONObject obj = JSONObject.parseObject(body);
		return wxService.getWXAlarm();
	}

	@ApiOperation(value = "摄像头可见距离统计24小时", notes = "")
	@RequestMapping("/map/getDisplayDistanceByEqu")
	@ResponseBody
	public String getDisplayDistanceByEqu(@RequestBody String body) {
		JSONObject obj = JSONObject.parseObject(body);
		String date = obj.getString("date");
		String epId = obj.getString("epId");

		return fogApi.getDistanceStatistic(date,epId);
	}
	
	@ApiOperation(value = "获取微信用户信息", notes = "")
	@RequestMapping("/getUserInfo")
	@ResponseBody
	public String getUserInfo(@RequestParam String openid) {
		return wxPushUtil.getUserInfo(openid);
	}

	@ResponseBody
	@GetMapping()
	public void get(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			// 微信加密签名
			String signature = request.getParameter("signature");
			// 时间戳
			String timestamp = request.getParameter("timestamp");
			// 随机数
			String nonce = request.getParameter("nonce");
			// 随机字符串
			String echostr = request.getParameter("echostr");
			out = response.getWriter();
			log.info("signature[{}], timestamp[{}], nonce[{}], echostr[{}]", signature, timestamp, nonce, echostr);
			if (CheckUtil.checkSignature(signature, timestamp, nonce)) {
				out.print(echostr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	@ResponseBody
	@PostMapping()
	public void post(HttpServletRequest request, HttpServletResponse response) {
		String message = "success";
		PrintWriter out = null;
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			out = response.getWriter();

			// 把微信返回的xml信息转义成map
			Map<String, String> map = XmlUtil.xmlToMap(request);
			String openid = map.get("FromUserName");// 消息来源用户标识
			String toUserName = map.get("ToUserName");// 消息目的用户标识
			String msgType = map.get("MsgType");// 消息类型
			String content = map.get("Content");// 消息内容

			String eventType = map.get("Event");
//			WxSubscribeDO wxSubscribe = new WxSubscribeDO();
//			wxSubscribe.setOpenid(openid);
//			wxSubscribe.setTousername(toUserName);
//			wxSubscribe.setCreatetime(new Date());

			WxUser wxUser = new WxUser();
			wxUser.setOpenid(openid);
			wxUser.setCreateTime(new Date());
			wxUser.setPoliceId("123456");//TODO

			log.error("微信接收" + openid + "---" + msgType + "---" + eventType + "---" + content);
			if (MessageUtil.MSGTYPE_EVENT.equals(msgType)) {// 如果为事件类型
				if (MessageUtil.MESSAGE_SUBSCIBE.equals(eventType)) {// 处理订阅事件
//					message = MessageUtil.subscribeForText(toUserName, openid);
					String result = wxPushUtil.getUserInfo(openid);
					result = new String(result.getBytes("ISO-8859-1"), "UTF-8");
					JSONObject jsonobj = JSONObject.parseObject(result);
					wxUser.setNickname(WxUtil.emojiConvert1(jsonobj.getString("nickname")));
					wxUser.setUnionid(jsonobj.getString("unionid"));
					if (/* wxSubscribeService.save(wxSubscribe) > 0 && */wxUserService.save(wxUser)) {
						log.error("微信关注" + openid + "保存成功");
					} else {
						log.error("微信关注" + openid + "保存失败");
					}
				} else if (MessageUtil.MESSAGE_UNSUBSCIBE.equals(eventType)) {// 处理取消订阅事件
//					message = MessageUtil.unsubscribe(toUserName, openid);
					if (wxSubscribeService.removeById(openid) && wxUserService.removeById(openid)) {
						log.error("微信取消关注" + openid + "删除成功");
					} else {
						log.error("微信取消关注" + openid + "删除失败");
					}
				}
			}
		} catch (Exception e) {
			log.error("微信关注发生异常======================================");
			e.printStackTrace();
		} finally {
			out.println(message);
			if (out != null) {
				out.close();
			}
		}
	}

	/**
	 * 微信菜单
	 * @throws Exception
	 */
	@GetMapping("/menu")
	@ResponseBody
	public void WXMenu() throws Exception {
		WxUtil.WxMenu(wxPushUtil.getAccess_token());
		
	}

	@GetMapping("/auth")
	@ResponseBody
	public void GuideServlet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 设置编码
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		/**
		 * 第一步：用户同意授权，获取code:https://open.weixin.qq.com/connect/oauth2/authorize
		 * ?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE
		 * &state=STATE#wechat_redirect
		 */
		String redirect_uri = URLEncoder.encode("http://1.1.1.1/wechatServer/login", "UTF-8");// 授权后重定向的回调链接地址，请使用urlencode对链接进行处理（文档要求）
		// 按照文档要求拼接访问地址
		String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + WxUtil.APPID + "&redirect_uri="
				+ redirect_uri + "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
		response.sendRedirect(url);// 跳转到要访问的地址

	}

	@ResponseBody
	@RequestMapping(value = "/login")
	public String login(HttpServletRequest request) throws JSONException, IOException {
		String code = request.getParameter("code");
		System.out.println("终于获取到CODE了:" + code);
		WxUtil.oauth2(code);
		return code;
	}
	
	@GetMapping("/createTag")
	@ResponseBody
	public void createTag(@RequestParam("tagName") String tagName) throws Exception {
		WxUtil.createTag(tagName, wxPushUtil.getAccess_token());
	}

	@ResponseBody
	@PostMapping("/lookup/updateimgpath/{epId}")
	JSONObject updateimgpath(@PathVariable("epId") String epId) {
		JSONObject result = new JSONObject();
		FogCalculator cal = fcFactory.getCalculator(epId);
		if (cal != null) {
			result.put("fMeter", (cal.getDistance() != null ? cal.getDistance() : ""));
			result.put("imgpath", cal.getImgpath());
		}

		return result;
	}
	
	/** ---------------提取日报 start **/

	@ResponseBody
	@RequestMapping("/zhiBan/queryDeptList")
	public JSONArray queryDeptList() {
		Map<String,Object> map = new HashMap<>();
		map.put("parentId", "0");
		List<SysDepartModel> list = sysUserRemoteApi.queryDepartListByUid(ShiroUtils.getUserId()).getResult();

		return JSONArray.parseArray(JSON.toJSONString(list));
	}
	
	
	/**
	 * 按路公司提取日报
	 * 日报时间段：昨日中午12点至今日早9点
	 * @return
	 */
//	@ResponseBody
//	@RequestMapping("/zhiBan/getDailyPaper")
//	public JSONArray getDailyPaper(@RequestBody String body) {
//		List<String> resultList = new ArrayList<String>();
//		//日报统计时间从昨天15点开始
//		Date date = new Date();
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(date);
//		calendar.add(Calendar.DATE, -1);
//		String starttime = DateUtils.format(calendar.getTime(), "yyyy-MM-dd");
//		starttime = starttime+" 15:00:00";
//		String dpTime = DateUtils.format(new Date(), "yyyy-MM-dd");
//		dpTime = dpTime+" 09:00:00";
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		List<String> deptIds = ShiroUtils.getUser().getBelongDeptIds();
//		List<SysDepartModel> sysDepartList = sysBaseRemoteApi.getAllSysDepart();
//
//		for(String deptId : deptIds){
//			//根据路公司ID关联用户表，查询路公司管辖的路段
//			Result<List<String>> result = highwayApi.queryHwIdsByDeptId(deptId);
//			List<String> hwIds = result.getResult();
//			SysDepartModel sysDepartModel = sysDepartList.stream().filter(item  -> deptId.equalsIgnoreCase(item.getId())).findFirst().get();
//			String deptName = sysDepartModel.getDepartName();
//			if(!StringUtils.isEmpty(deptName)) {
//				Map<String, Object> map = new HashMap<>();
//				map.put("hwIds", hwIds);
//				map.put("starttime", starttime);
////				map.put("endtime", endtime);
//				map.put("sort", "starttime");
//				map.put("order", "asc");
//				List<AlarmRoadModel> alarmRoadList = fogApi.queryListByParams(map);
//
//				int alarmCount = 0;			//路段告警总次数
//				String dailyPaper = "【日报】截止上午9点（前24小时内）"+deptName;
//				for (String hwId : hwIds) {	//部门管辖路段
//					StringBuffer sb = new StringBuffer();
//					boolean isAlarm = false;	//是否有路段告警
//					String alarmHwName = StringUtils.EMPTY;	//告警路段名称
//					int minDistance = 500;	//最低能见度
//					String level = "强浓雾";
//					Date startTime = null;	//开始时间
//					Date endTime = null;	//结束时间
//					String lengthTime = StringUtils.EMPTY;	//持续时长
//
//					for (AlarmRoadModel ar : alarmRoadList) {	//告警路段
//						if(hwId == ar.getHwId()) {
//							isAlarm = true;
//							alarmCount++;
//							alarmHwName = ar.getHwName();
//							if(minDistance > ar.getMindistanceHis()) {
//								minDistance = ar.getMindistanceHis();
//							}
//							if(minDistance <= 50 ) {
//								level = "特强浓雾";
//							}
//							if(startTime == null || startTime.compareTo(ar.getStarttime()) > 0) {
//								startTime = ar.getStarttime();
//							}
//							if(ar.getEndtime() != null && lengthTime != null) {
//								//截至上午9点
//								if( (endTime == null || endTime.compareTo(ar.getEndtime()) < 0) &&
//										DateUtils.str2Date(dpTime, sdf).compareTo(ar.getEndtime()) >= 0 ) {
//									endTime = ar.getEndtime();
//								}else {
//									lengthTime = null;
//								}
//							}else {
//								lengthTime = null;
//							}
//						}
//					}
//					if(isAlarm) {
//						sb.append(alarmHwName);
//						sb.append("发生");
//						sb.append(level);
//						sb.append("，其间能见度（道路可视距离）分布最低值低于");
//						sb.append(minDistance);
//						sb.append("米，");
//						if(lengthTime != null) {
//							sb.append("持续约");
//							sb.append(DateUtils.getTimeDifferenceAccurate(startTime, endTime,false));
//							sb.append("("+DateUtils.format(startTime, "MM月dd日HH点mm分")+"-"+DateUtils.format(endTime, "MM月dd日HH点mm分")+")");
//						}else {
//							sb.append("目前持续中");
//						}
//						sb.append("；");
//						dailyPaper += sb.toString();
//					}
//				}
//
//				if(alarmCount == 0) {
//					dailyPaper = "【日报】截止上午9点（前24小时内）"+deptName+"视频监测路段没有发生团雾和能见度（道路可视距离）低于200米的现象。（中交信科集团海德科技）" ;
//				}else {
//					dailyPaper = dailyPaper.substring(0, dailyPaper.length()-1);
//					dailyPaper += "。（中交信科集团海德科技）";
//				}
//				resultList.add(dailyPaper);
//			}
//		}
//
//		return JSONArray.parseArray(JSON.toJSONString(resultList));
//	}
	
	/** ---------------提取日报 end **/

	/**
	 * 修改
	 */
	@ResponseBody
	@RequestMapping("/equipment/update")
	public Result<?> update(EquipmentModel equipment) {
		return fogApi.edit(equipment);
	}

	
}
