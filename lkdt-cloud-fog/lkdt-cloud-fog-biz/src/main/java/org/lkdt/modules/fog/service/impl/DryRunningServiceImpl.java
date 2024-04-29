package org.lkdt.modules.fog.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.api.R;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.util.DateUtils;
import org.lkdt.modules.fog.calculator.FogListenerMap;
import org.lkdt.modules.fog.entity.AlarmLog;
import org.lkdt.modules.fog.service.DryRunningService;
import org.lkdt.modules.fog.service.IAlarmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 沙盘推演
 * @author wy
 *
 */
@Service
public class DryRunningServiceImpl implements DryRunningService {
	
	private static final Logger logger = LoggerFactory.getLogger(DryRunningServiceImpl.class);
	
	@Autowired
	private IAlarmService alarmService;

	@Autowired
	RestTemplate restTemplate;
	
	@Override
	public Result sandTableData(Timestamp starttime, Timestamp endtime, String requestStr, int intervalTime, int magnification) {
		Timestamp newTime = starttime;
		Timestamp oldTime = starttime;
		
		while(newTime.getTime() < endtime.getTime()) {
			oldTime = newTime;
			newTime = DateUtils.timePastTenSecond(newTime,intervalTime*magnification);
			
			JSONArray jsonArray = new JSONArray();
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("startTime", oldTime.toString());
			params.put("endTime", newTime.toString());
//			params.put("equcode", "");
			//空集合
			List<AlarmLog> allalarmLogList = alarmService.getFileList(oldTime.toString() + "00", newTime.toString() + "00", null);
			//可视距离、桩号、图片
			for (AlarmLog alarmLog : allalarmLogList) {
				if(alarmLog != null) {
					JSONObject jsonObject = getSendFogJSONObject(alarmLog);
					jsonArray.add(jsonObject);
				}
			}
			
			logger.info("沙盘数据============="+allalarmLogList.size()+"，时间===="+newTime.toString());
			if(allalarmLogList != null && allalarmLogList.size() > 0) {
				try {
					HttpHeaders headers = new HttpHeaders();
					MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
					headers.setContentType(type);
					JSONObject json = new JSONObject();
					json.put("result", jsonArray);
					json.put("sysName", FogListenerMap.SYSNAME_SANDTABLE);
					//先写死
					//json.put("sysName", "沙盘");
					HttpEntity formEntity = new HttpEntity(json.toString(), headers);

					ResponseEntity resultEntity = null;
					RestTemplate restTemplate = new RestTemplate();	//启用集群时将次行注释
					try {
						resultEntity = restTemplate.postForEntity(requestStr, formEntity,String.class);
						resultEntity.getStatusCode();
					} catch (Exception e) {
						logger.info(requestStr + " 访问错误 ");
						logger.error("推送", e);
						return Result.error("操作失败");
					}
					
				} catch (Exception e) {
					logger.error("沙盘推演异常：日志数据推送异常。");
					return Result.error("沙盘推演异常：日志数据推送异常。");
				}
			}
			
			params.clear();
			allalarmLogList.clear();
			jsonArray.clear();
			
			try {
				Thread.sleep(intervalTime*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return Result.ok("操作成功");
	}

	@Override
	public JSONObject getSendFogJSONObject(AlarmLog alarmLog) {
		//System.out.println("桩号："+alarmLogDO.getEpId()+"；可视距离："+alarmLogDO.getDistance()+"；图片："+alarmLogDO.getImgpath()+"；时间："+DateUtils.format(alarmLogDO.getAlarmtime(), "yyyy-MM-dd HH:mm:ss"));
		JSONObject json = new JSONObject();
		if(Integer.valueOf(alarmLog.getModifyVal()) < 0) {
			return null;
		}
		json.put("fValue", alarmLog.getOriginVal());
		json.put("epId", alarmLog.getEpId());
		json.put("fmodel", "ST");
		
		//https://ainjdjc.jchc.cn/system/alarm/showImg?fname=1585651469018.jpg&epId=K800S-1&dateStr=2020-3-31
		SimpleDateFormat sdf_dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String imgurl = null;
		imgurl = alarmLog.getImgName();
		try {
			imgurl = URLEncoder.encode(imgurl, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		json.put("imgurl", imgurl);
		json.put("fname", alarmLog.getImgName());
		try {
			json.put("fogtime", DateUtils.format(sdf_dateTime.parse(alarmLog.getDateTime()), "yyyy-MM-dd HH:mm:ss"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return json;
	}


}
