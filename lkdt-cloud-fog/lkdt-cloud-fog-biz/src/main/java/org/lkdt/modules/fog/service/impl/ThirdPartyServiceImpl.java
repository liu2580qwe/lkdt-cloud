package org.lkdt.modules.fog.service.impl;
import com.alibaba.fastjson.JSON;
import org.lkdt.common.util.DateUtils;
import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.api.SysBaseRemoteApi;
import org.lkdt.modules.fog.calculator.FcFactory;
import org.lkdt.modules.fog.calculator.FogCalculator;
import org.lkdt.modules.fog.service.IThirdPartyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * 感动科技-控股服务器数据接口
 *
 * @author wy
 */
@Service
public class ThirdPartyServiceImpl implements IThirdPartyService {
	private static final Logger logger = LoggerFactory.getLogger(ThirdPartyServiceImpl.class);

	@Autowired
	private SysBaseRemoteApi sysBaseRemoteApi;
	@Value("${sm2.url}")
	private String url;

	@Value("${sm2.corp}")
	private String corp;

	@Value("${sm2.prodCode}")
	private String prodCode;

	@Value("${sm2.md5}")
	private String md5;

	@Autowired
	private FcFactory fcFactory;

	@Autowired
	private FogCalculator fogCalculator;

	@Override
	public void thirdPartySend(final String sendUrl, Map<String, Object> map) {
		try {
			HttpHeaders headers = new HttpHeaders();
			MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
			headers.setContentType(type);
			HttpEntity<String> formEntity = new HttpEntity<String>(JSON.toJSONString(map), headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> result = restTemplate.postForEntity(sendUrl, formEntity, String.class);
			logger.info("第三方接口推送数据返回结果============" + result.getBody().toString());

		} catch (Exception e) {
			logger.error("ThirdPartyServiceImpl.thirdPartySend异常", e, e.getStackTrace());
		}
	}
	/**
	 * 验证发送能见度数据
	 * @param cal
	 */
	@Override
	public void checkDistanceSend(FogCalculator cal) {
		if (StringUtils.isEmpty(cal.getEquipment().getCameraNum())) {
			return;
		}
		if (!StringUtils.equals(cal.getEquipment().getState(), "0")) {
			return;
		}
		//只推人工确认有雾和散雾的逻辑
		boolean isFog = false;
		if (cal.isFogNow() && cal.getDistance() < 200) {
			isFog = true;
		}
		boolean endFog = false;
		Long now = System.currentTimeMillis();
//		if (cal.getAlarmEndTime() != null && now - cal.getAlarmEndTime().getTime() < 10 * 60 * 1000) {
//			endFog = true;
//		}
		int distance = cal.getDistance();
//		if (!cal.isFogNow() &&  cal.getDistance() < 200) {
//			distance = (int) (200 + (Math.random() * 50));
//		}
		if (endFog || isFog) {
			if (StringUtils.isNotEmpty(cal.getEquipment().getGdHwId())) {
				if (cal.getDistance() == null) {
					return;
				}
				logger.info(String.format("准备推送低能见度。摄像头ID：%s；isFog：%s；真实距离：%s米；推送距离：%s米；URL：%s", cal.getEpId(), cal.isFogNow(), cal.getDistance(), distance, this.url));
				Map<String, Object> map = getDistanceSendMap(cal, distance);
				logger.info("给感动发送数据", map);
				String sendUrl = url + "mvpai/ai";
				thirdPartySend(sendUrl, map);
			}

		}

	}

	/**
	 * 发送视频拉流异常数据
	 * @param epIds
	 */
	@Override
	public void sendVideoStreamEx(List<String> epIds) {
		Map<String, Object> map = new HashMap<>();
		String sendUrl = url + "mvpai/ai";
		String datetime = DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
		//厂商
		map.put("corp", this.corp);
		//产品编号
		map.put("prodCode", this.prodCode);
		map.put("datetime", datetime);
		map.put("sign", DigestUtils.md5DigestAsHex((md5 + datetime).getBytes()));
		List<Map<String, Object>> dataMapList = new ArrayList<Map<String, Object>>();
		for(String epId : epIds){
			FogCalculator cal = fcFactory.getCalculator(epId);
			if (StringUtils.isNotEmpty(cal.getEquipment().getCameraNum()) && StringUtils.isNotEmpty(cal.getEquipment().getGdHwId())) {
				logger.info(String.format("准备推送摄像头拉流异常。摄像头ID：%s；isFog：%s；真实距离：%s米；推送距离：%s米；URL：%s", cal.getEpId(), cal.isFogNow(), "video_stream_ex", "video_stream_ex", this.url));
				Map<String, Object> dataMap = getVideoStreamExSendMap(cal);
				dataMapList.add(dataMap);
			}
		}
		map.put("data", dataMapList);
		logger.info(JSON.toJSONString(map));
		logger.info("给感动发送数据", map);

		thirdPartySend(sendUrl, map);



	}

	/**
	 * 发送视频质量异常数据
	 * @param cal
	 */
	@Override
	public void sendVideoQualityEx(FogCalculator cal) {
		if (StringUtils.isNotEmpty(cal.getEquipment().getCameraNum()) && StringUtils.isNotEmpty(cal.getEquipment().getGdHwId())) {
			logger.info(String.format("准备推送摄像头画面异常。摄像头ID：%s；isFog：%s；真实距离：%s米；推送距离：%s米；URL：%s", cal.getEpId(), cal.isFogNow(), "video_quality_ex", "video_stream_ex", this.url));
			Map<String, Object> map = getVideoQualityExSendMap(cal);
			logger.info("给感动发送数据", map);
			String sendUrl = url + "mvpai/ai";
			thirdPartySend(sendUrl, map);
		}
	}

	private Map<String, Object> getDistanceSendMap(FogCalculator cal, int distance) {
		Map<String, Object> map = new HashMap<String, Object>();
//			map.put("department", "东部高速");
//			map.put("url", this.url);
		String datetime = DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
		//厂商
		map.put("corp", this.corp);
		//产品编号
		map.put("prodCode", this.prodCode);
		map.put("datetime", datetime);
		map.put("sign", DigestUtils.md5DigestAsHex((md5 + datetime).getBytes()));

		List<Map<String, Object>> dataMapList = new ArrayList<Map<String, Object>>();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		//事件id 必填
		dataMap.put("eventId", this.corp + "_" + this.prodCode + "_" + StringUtils.getUUID());
		//路线编号 必填
		dataMap.put("cameraNum", cal.getEquipment().getCameraNum());
		//数据上报时间 必填
		dataMap.put("startTime", DateUtils.format(cal.getImgtime(), "yyyy-MM-dd HH:mm:ss"));
		//路线编号 必填
		dataMap.put("roadCode", cal.getEquipment().getGdHwId());

		Integer[] ints = FogCalculator.parseEquName(cal.getEquipment().getEquName());
		//桩号 必填
		dataMap.put("mileageNo", ints[0] + "." + ints[1]);
		//对象类型 必填
		dataMap.put("objectType", "camera");
		//媒体文件 必填

		List<Map<String, Object>> dataList = null;
		Map<String, Object> imgMap = null;
		try {
			String imgPath = cal.getImgpath();
//			int startIndex = imgPath.lastIndexOf("/");
			int endIndex = imgPath.indexOf("?");
//			String imgName = imgPath.substring(startIndex + 1, endIndex);
			String imgName = imgPath.substring(endIndex - 17, endIndex);
			dataList = new ArrayList<Map<String, Object>>();
			imgMap = new HashMap<String, Object>();
			imgMap.put("name", imgName);
			imgMap.put("src", imgPath);
			imgMap.put("type", "image/jpeg");
		} catch (Exception e) {
			logger.error(cal.getImgpath());
			logger.error("图片截取报错：" + e);
			e.printStackTrace();
		}
		dataList.add(imgMap);
		dataMap.put("mediaList", dataList);
//		dataMap.put("mediaList", "{\"name\":\"" + imgName + "\",\"src\":\"" + imgPath + "\",\"type\":\"image/jpeg\"}");
		//事件类型 必填
		dataMap.put("eventType", "fog");
		//方向 非必填
//			dataMap.put("direction", );
		//事件参数 必填
		dataMap.put("eventPara", distance);
		dataMapList.add(dataMap);
		map.put("data", dataMapList);
		logger.info(JSON.toJSONString(map));
		return map;
	}

	private Map<String, Object> getVideoStreamExSendMap(FogCalculator cal) {

		Map<String, Object> dataMap = new HashMap<String, Object>();
		//事件id 必填
		dataMap.put("eventId", this.corp + "_" + this.prodCode + "_" + StringUtils.getUUID());
		//路线编号 必填
		dataMap.put("cameraNum", cal.getEquipment().getCameraNum());
		//数据上报时间 必填
		dataMap.put("startTime", DateUtils.format(cal.getImgtime(), "yyyy-MM-dd HH:mm:ss"));
		//路线编号 必填
		dataMap.put("roadCode", cal.getEquipment().getGdHwId());

		Integer[] ints = FogCalculator.parseEquName(cal.getEquipment().getEquName());
		//桩号 必填
		dataMap.put("mileageNo", ints[0] + "." + ints[1]);
		//对象类型 必填
		dataMap.put("objectType", "camera");
		//事件类型 必填
		dataMap.put("eventType", "video_stream_ex");
		//方向 非必填
//			dataMap.put("direction", );
		//事件参数 必填
		dataMap.put("eventPara", "视频拉流异常");

		return dataMap;
	}

	private Map<String, Object> getVideoQualityExSendMap(FogCalculator cal) {
		Map<String, Object> map = new HashMap<String, Object>();
//			map.put("department", "东部高速");
//			map.put("url", this.url);
		String datetime = DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
		//厂商
		map.put("corp", this.corp);
		//产品编号
		map.put("prodCode", this.prodCode);
		map.put("datetime", datetime);
		map.put("sign", DigestUtils.md5DigestAsHex((md5 + datetime).getBytes()));

		List<Map<String, Object>> dataMapList = new ArrayList<Map<String, Object>>();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		//事件id 必填
		dataMap.put("eventId", this.corp + "_" + this.prodCode + "_" + StringUtils.getUUID());
		//路线编号 必填
		dataMap.put("cameraNum", cal.getEquipment().getCameraNum());
		//数据上报时间 必填
		dataMap.put("startTime", DateUtils.format(cal.getImgtime(), "yyyy-MM-dd HH:mm:ss"));
		//路线编号 必填
		dataMap.put("roadCode", cal.getEquipment().getGdHwId());

		Integer[] ints = FogCalculator.parseEquName(cal.getEquipment().getEquName());
		//桩号 必填
		dataMap.put("mileageNo", ints[0] + "." + ints[1]);
		//对象类型 必填
		dataMap.put("objectType", "camera");
		//媒体文件 必填

		List<Map<String, Object>> dataList = null;
		Map<String, Object> imgMap = null;
		try {
			String imgPath = cal.getImgpath();
//			int startIndex = imgPath.lastIndexOf("/");
			int endIndex = imgPath.indexOf("?");
//			String imgName = imgPath.substring(startIndex + 1, endIndex);
			String imgName = imgPath.substring(endIndex - 17, endIndex);
			dataList = new ArrayList<Map<String, Object>>();
			imgMap = new HashMap<String, Object>();
			imgMap.put("name", imgName);
			imgMap.put("src", imgPath);
			imgMap.put("type", "image/jpeg");
		} catch (Exception e) {
			logger.error(cal.getImgpath());
			logger.error("图片截取报错：" + e);
			e.printStackTrace();
		}
		dataList.add(imgMap);
		dataMap.put("mediaList", dataList);
//		dataMap.put("mediaList", "{\"name\":\"" + imgName + "\",\"src\":\"" + imgPath + "\",\"type\":\"image/jpeg\"}");
		//事件类型 必填
		dataMap.put("eventType", "video_quality_ex");
		//方向 非必填
//			dataMap.put("direction", );
		//事件参数 必填
		dataMap.put("eventPara", sysBaseRemoteApi.queryDictTextByKey("equipment_state", fogCalculator.getEquipment().getState()));
		dataMapList.add(dataMap);
		map.put("data", dataMapList);
		logger.info(JSON.toJSONString(map));
		return map;
	}

}
