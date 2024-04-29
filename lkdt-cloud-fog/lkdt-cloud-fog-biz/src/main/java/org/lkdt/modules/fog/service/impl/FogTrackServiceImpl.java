package org.lkdt.modules.fog.service.impl;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.fog.calculator.FcFactory;
import org.lkdt.modules.fog.calculator.FogCalculator;
import org.lkdt.modules.fog.calculator.FogSpaceCal;
import org.lkdt.modules.fog.service.FogTrackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Service
public class FogTrackServiceImpl implements FogTrackService {
	Logger logger = LoggerFactory.getLogger(FogTrackServiceImpl.class);
	@Autowired
	FcFactory fcFactory;
	@Autowired
	FogSpaceCal fogSpaceCal;
	/**
	 * 查询缓存实时告警列表
	 * @return
	 */
	@Override
	public String selectRealAlarmInfoListMerge() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = null;
		//所有摄像头
		//已告警摄像头
		List<String> hwIds = fcFactory.getHwIds();
		try{
			for(String hwId : hwIds){
				Date now = new Date();
				FogCalculator startAlarmCal = null;
				List<FogCalculator> alarmCalList = new ArrayList<>();
				int i = 0;
				List<FogCalculator> fogCalList= fcFactory.getCalculatorsByHwIdNormal(hwId);
				for(FogCalculator fogCalculator : fogCalList){
					i++;
					//有雾
					if(StringUtils.isNotEmpty(fogCalculator.getAlarmId()) && fogCalculator.isFogNow() && "0".equals(fogCalculator.getEquipment().getState())){
						if(startAlarmCal == null){
							startAlarmCal = fogCalculator;
						}
						alarmCalList.add(fogCalculator);
						if(i == fogCalList.size()){
							jsonObject = getJSONObject(simpleDateFormat, now, alarmCalList, fogCalculator);
							jsonArray.add(jsonObject);
						}
					}else {
						if(startAlarmCal != null ){
							jsonObject = getJSONObject(simpleDateFormat, now, alarmCalList, fogCalculator);
							jsonArray.add(jsonObject);
						}

						startAlarmCal = null;
						alarmCalList.clear();
					}
				}
			}
		} catch (Exception e){
			logger.error("未知错误,method:selectRealAlarmInfoList",e);
			return jsonArray.toJSONString();
		}
		return jsonArray.toJSONString();
	}
	private JSONObject getJSONObject(SimpleDateFormat simpleDateFormat, Date now, List<FogCalculator> alarmCalList, FogCalculator fogCalculator) {
		JSONObject jsonObject = new JSONObject();
		if(alarmCalList.size() == 1){
			FogCalculator alarmCal = alarmCalList.get(0);
			jsonObject.put("distance", alarmCal.getDistance());//距离
			jsonObject.put("cameraType", alarmCal.getCameraType());//1：有雾未确认 3：升级未确认 5：解除未确认
			jsonObject.put("epId", alarmCal.getEpId());//摄像头ID
			Integer[] ints1 = FogCalculator.parseEquName(alarmCal.getEquipment().getEquName());
			jsonObject.put("equName", "K" + ints1[0]);//桩号
			jsonObject.put("influence", 1);//影响里程
			if (alarmCal.getDistance() != null) {
				jsonObject.put("imgfn", alarmCal.getImgpath());
			} else {
				jsonObject.put("imgfn",-1);
			}

			jsonObject.put("address", alarmCal.getEquipment().getEquLocation());//地址
			jsonObject.put("hwName",alarmCal.getEquipment().getHwName());//路段
			jsonObject.put("fogType",fogSpaceCal.isFog(alarmCal));//雾霾类型
			try{
				Date begintime_db = alarmCal.getAlarmStartTime();
				String beginTime = begintime_db != null ? simpleDateFormat.format(begintime_db) : "";
				if (StringUtils.isEmpty(beginTime)) {
					beginTime = simpleDateFormat.format(now);
				}
				jsonObject.put("begintime",beginTime);//开始时间
				long minute = (now.getTime()-simpleDateFormat.parse(beginTime).getTime())/1000/60;
				if (minute> 60) {
					double hours = Math.round(minute / 60.0);
					jsonObject.put("forTime",hours+"小时");//持续时间
				} else {
					jsonObject.put("forTime",minute+"分钟");//持续时间
				}
			} catch (Exception e){
				logger.error("首页获取告警时间出现错误 方法：selectRealAlarmInfoList()",e);
				jsonObject.put("begintime","");//开始时间
				jsonObject.put("forTime","分钟");//持续时间
			}

		}else{
			jsonObject = new JSONObject();
			FogCalculator minCal = fcFactory.minCal(alarmCalList);

			jsonObject.put("distance",minCal.getDistance());//距离
			jsonObject.put("cameraType",minCal.getCameraType());//1：有雾未确认 3：升级未确认 5：解除未确认
			jsonObject.put("epId",minCal.getEpId());//摄像头ID
			Integer[] ints1 = FogCalculator.parseEquName(alarmCalList.get(0).getEquipment().getEquName());
			Integer[] ints2 = FogCalculator.parseEquName(alarmCalList.get(alarmCalList.size() - 1).getEquipment().getEquName());
			jsonObject.put("equName", "K" + ints1[0] + " - " + "K" + ints2[0]);//桩号
			jsonObject.put("influence", ints2[0] - ints1[0] + 1);//影响里程
			if (fogCalculator.getDistance() != null) {
				jsonObject.put("imgfn",minCal.getImgpath());
			} else {
				jsonObject.put("imgfn",-1);
			}

			jsonObject.put("address",minCal.getEquipment().getEquLocation());//地址
			jsonObject.put("hwName",minCal.getEquipment().getHwName());//路段
			jsonObject.put("fogType",fogSpaceCal.isFog(minCal));//雾霾类型
			try{
				Date begintime_db = minCal.getAlarmStartTime();
				String beginTime = begintime_db != null ? simpleDateFormat.format(begintime_db) : "";
				if (StringUtils.isEmpty(beginTime)) {
					beginTime = simpleDateFormat.format(now);
				}
				jsonObject.put("begintime",beginTime);//开始时间
				long minute = (now.getTime()-begintime_db.getTime())/1000/60;
				if (minute> 60) {
					double hours = Math.round(minute / 60.0);
					jsonObject.put("forTime",hours+"小时");//持续时间
				} else {
					jsonObject.put("forTime",minute+"分钟");//持续时间
				}
			} catch (Exception e){
				logger.error("首页获取告警时间出现错误 方法：selectRealAlarmInfoList()",e);
				jsonObject.put("begintime","");//开始时间
				jsonObject.put("forTime","分钟");//持续时间
			}



		}
		return jsonObject;
	}

}
