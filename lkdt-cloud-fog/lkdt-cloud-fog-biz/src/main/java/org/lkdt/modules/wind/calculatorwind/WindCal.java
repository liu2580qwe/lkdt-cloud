package org.lkdt.modules.wind.calculatorwind;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.lkdt.common.util.AlarmLevelUtil;
import org.lkdt.common.util.DateUtils;
import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.fog.calculator.WcFactory;
import org.lkdt.modules.fog.calculator.WindCalculator;
import org.lkdt.modules.fog.channel.FogChannelUtil;
import org.lkdt.modules.fog.entity.*;
import org.lkdt.modules.fog.service.IAlarmRoadService;
import org.lkdt.modules.fog.service.IAlarmService;
import org.lkdt.modules.wind.domain.WindDO;
import org.lkdt.modules.wind.entity.WindLog;
import org.lkdt.modules.wind.mapper.ZcWindMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
@Component
@Slf4j
public class WindCal {
	private final static String LOCK_KEY = "LOCK_KEY_GET_GANDONG_CAMERA";

	/**
	 * yyyyMM
	 */
	public final String str_ymd = "yyyyMM";

	/**
	 * yyyy-MM-dd HH:mm:ss.SSS
	 */
	public final String str_dateTime = "yyyy-MM-dd HH:mm:ss.SSS";


	public final String windLog_ = "windLog";

	private final static long TIMEOUT = 10000;

	private static Date mktime = null;
	@Autowired
	private IAlarmService alarmService;
	@Autowired
	private IAlarmRoadService alarmRoadService;
	@Autowired
	private ZcWindMapper zcWindMapper;
	@Autowired
	private FogChannelUtil fogChannelUtil;

	@Autowired
	private WcFactory wcFactory;

	@Autowired
	MongoTemplate mongoTemplate;
	/**
	 * Timed tasks for calculating gale data
	 * @param param
	 * @return
	 */
	@XxlJob("windJobHandler")
	public ReturnT<String> windJobHandler(String param){
		log.info("Scheduled task start---->Calculation program>>>>");
		try {
			List<WindHistoryModel> fhs = new ArrayList<>();
			Map<String, Object> map = new HashMap<>();
			Calendar timec = Calendar.getInstance();
			//time adjustment
			if (mktime == null) {
				timec.add(Calendar.MINUTE, -5);
				mktime = new Date(timec.getTimeInMillis());
			}
			//Obtain strong wind warning information
			List<WindDO> winds = zcWindMapper.listWindAlarm(map);
			log.info("Gale warning data-------windDo(WindAlarm)---->"+winds);
			//Obtain the gale warning data collection
			Map<String, JSONObject> windMap = getWind();
			log.info("Get wind data-------windMap----->>>>"+windMap.toString());
			//No strong wind warning, termination
			if (windMap == null) {return null;}
			//There is a gale warning, set historical warning information
			for (WindDO winddo : winds) {
				WindHistoryModel windHistoryModel = new WindHistoryModel();
				JSONObject wind = windMap.get(winddo.getId());
				JSONObject predictObject = windMap.get(winddo.getId());
				if (wind == null){ continue;}
				//Set history data
				windHistoryModel.setWinds(((float) Math.round(wind.getDouble("winds") * 10) / 10));
				windHistoryModel.setWindd(((float) Math.round(wind.getDouble("windd") * 10) / 10));
				windHistoryModel.setWindId(winddo.getId());
				windHistoryModel.setPredictObject(predictObject);
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				//Time format conversion
				try {
					windHistoryModel.setTime(formatter.parse(wind.getString("time")));
				} catch (Exception e) {
					windHistoryModel.setTime(new Date());
				}
				//Add to historical data list
				fhs.add(windHistoryModel);
			}
			log.info("Get historical data---------WindHistoryDO--->>>>>>>"+fhs.toString());
			// Analyze all camera information, calculate the visible distance, calculate the level of the added group of fog and haze
			if (fhs != null && fhs.size() > 0) {
				for (WindHistoryModel fdo : fhs) {
					String windId = fdo.getWindId();
					WindCalculator cal = wcFactory.getCalculator(windId);
					if (cal == null) {
						WindDO wind = zcWindMapper.get(windId);
						WindModel windModel = new WindModel();
						BeanUtils.copyProperties(wind, windModel);
						windModel.setId(windId);
						cal = new WindCalculator();
						cal.setWindCalculator(windModel, fdo.getTime());
						/*wcFactory.putCalculator(cal, windId);*/
					}
					// Calculate the visible distance and add the fogging and dispersing state to the cache
					cal.doCalculation(fdo);
					if (cal == null) {return null;}
					// Add alarm function
					String equname = "";
					try {
						equname = new String(cal.getId().getBytes("utf-8"), "utf-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					String alarmId = this.executeAlarm(cal,fdo);
					cal.setAlarmId(alarmId);
					wcFactory.putCalculator(cal, windId);
					WindLog windLog=new WindLog();
					windLog.setWindd(cal.getWindd().toString());
					windLog.setWinds(cal.getWinds().toString());
					windLog.setTime(DateUtils.format(cal.getTime(), "yyyy-MM-dd HH:mm:ss.SSS"));
					windLog.setWindId(cal.getId());
					this.insertMongo(windLog);
					log.debug(equname + "|" + fdo.getWindd() + "|" + cal.getWinds() + "|" + alarmId);
				}
			} else {
				log.error("定时任务----->>>>没有计算结果");
			}
			mktime = timec.getTime();
		}catch (Exception e){
			log.error("定时任务失败",e);
		}
		return null;
	}
	/**
	 * @apiNote
	 * 		Obtain the data of gale warning based on calling remote services
	 * 		Remote call address:	http://118.31.70.169:8080/foreData/2021-06-02 13.json
	 * @return
	 * 		windMap Gale warning data, by calling remote service
	 * 		null Return null when there is no strong wind warning
	 */
	public Map<String, JSONObject> getWind() {
		try {
			Map<String, JSONObject> windMap = new HashMap<>();
			//Splicing remote call address
			String now = DateUtils.format(new Date(), "yyyy-MM-dd HH");
			String URL = "http://118.31.70.169:8080/foreData/" + now + ".json";
			//Make remote service calls
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
			ResponseEntity<String> result = restTemplate.getForEntity(URL, String.class);
			String jsonData = result.getBody();
			//Process remote call callback data
			JSONObject json = JSONObject.parseObject(jsonData);
			Set<String> iterator = json.keySet();
			for (String key:iterator) {
				JSONArray winds = json.getJSONArray(key);
				JSONObject nowjson = winds.getJSONObject(0);
				winds.remove(0);
				nowjson.put(key,winds);
				windMap.put(key, nowjson);
				WindCalculator cal = wcFactory.getCalculator(key);
				//Delete the first one after fetching (fetch once every hour)
				if (cal != null) {

					cal.setPrediction(winds);
					wcFactory.putCalculator(cal,key);
				}
			}
			return windMap;
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * add alarm function
	 * @param cal              calculator
	 * @param cal                calculator result
	 * @param fdo              history record
	 */
	private String executeAlarm(WindCalculator cal, WindHistoryModel fdo) {
		log.info("Start sending alert information..");
		try {
			Alarm alarm = new Alarm();
			alarm.setEpId(cal.getId());
			alarm.setAddress(cal.getWindLocation());
			/*SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			if (cal.getWinds() >= 28.5) {// Super Control
				alarm.setLevel("0");
			} else if (cal.getWinds() >= 20.8) {// Primary control
				alarm.setLevel("1");
			} else if (cal.getWinds() >= 13.9) {// Secondary control
				alarm.setLevel("2");
			}else if (cal.getWinds() >= 10.8) {// Three-level control
				alarm.setLevel("3");
			}else {// Unregulated
				alarm.setLevel("4");
			}*/
			alarm.setLevel(AlarmLevelUtil.getLevelByWinds(cal.getWinds()));
			//Release the alarm when the wind speed is less than 10.8
			if (cal.getWinds() < 10.8) {
				// entry end alarm time
				if (StringUtils.isNotEmpty(cal.getAlarmId())) {
					alarm.setId(cal.getAlarmId());
					alarm.setEndtime(new Date());
					alarm.setDistance(cal.getDistance());
					alarm.setLevel(cal.getAlarmLevel());
					alarm.setImgpath(cal.getAlarmImgpath());
					alarm.setImgtime(cal.getAlarmImgtime());
					alarmService.updateById(alarm);
					AlarmRoad alarmRoad=new AlarmRoad();
					alarmRoad.setId(cal.getAlarmRoadId());
					alarmRoad.setEndtime(new Date());
					alarmRoad.setMindistanceNow(cal.getWinds());
					alarmRoadService.updateById(alarmRoad);
					// when the alarm is over leave the alarm id blank
					cal.setDistance(cal.getWinds());
					cal.setAlarmLevel("0");
					cal.setAlarmId("");
					log.error("=================告警解除邮件");
				}
			} else {
				// Determine the average value of the data in the first 10 minutes
				Date alarmDate = new Date();
				// alarm information storage
				if (StringUtils.isEmpty(cal.getAlarmId())) {
					Map<String, Object> params = new HashMap<>();
					params.put("epId", cal.getId());
					List<Alarm> adolist = alarmService.selectInfoByEpId(params);
					if (adolist.size() > 0) {
						Alarm ad = adolist.get(0);
						AlarmModel alarmModel = new AlarmModel();
						BeanUtils.copyProperties(ad, alarmModel);
						// The calculator sets the alarm id, the current visible distance
						cal.startAlarm(alarmModel);
					} else {
						//Judgment at the time of warning and warehousing 1: Group fog, 2: Haze
						//cal.setFogStatus(cal.getStatus());
						alarm.setImgtime(alarmDate);
						alarm.setId(StringUtils.getUUID());
						// 1: Confirmed; 0 confirmation is invalid; 9 not confirmed
						alarm.setIseffective("1");
						alarm.setBegintime(alarmDate);
						alarm.setFogType("21");
						alarm.setDistance(cal.getWinds());
						/*alarm.setWinds(cal.getWinds());*/
						alarmService.save(alarm);
						cal.setAlarmRoadId(StringUtils.getUUID());
						AlarmRoad alarmRoad=new AlarmRoad();
						alarmRoad.setId(cal.getAlarmRoadId());
						alarmRoad.setStarttime(alarmDate);
						alarmRoad.setHwId(cal.getHwId());
						alarmRoad.setRoadAlarmType("21");
						alarmRoad.setAlarmLevel(AlarmLevelUtil.getLevelByWinds(cal.getWinds()));
						alarmRoad.setMindistanceHis(cal.getWinds());
						alarmRoad.setMindistanceNow(cal.getWinds());
						alarmRoadService.save(alarmRoad);
						// The calculator sets the alarm id, the current visible distance
						AlarmModel alarmModel = new AlarmModel();
						BeanUtils.copyProperties(alarm, alarmModel);
						cal.startAlarm(alarmModel);
						//Convert Alarm entities and WindCalculator into JSONObject objects
						JSONObject alarmJSONObject = JSONObject.parseObject(JSONObject.toJSONString(alarm));
						log.info("Alarm data---->>>>>"+alarmJSONObject.toJSONString());
						log.error("What is the cal obtained at the end of the test------>>>>>----"+cal);
						alarmJSONObject.put("winds",cal.getWinds());
						alarmJSONObject.put("windd",cal.getWindd());
						alarmJSONObject.put("windTime",cal.getTime());
//						weixinClient.sendWind(alarmJSONObject);
						fogChannelUtil.windPushSend(alarmJSONObject);
					}
				} else {
					// Record the minimum visibility distance, picture name, grade
					try {
						if (Integer.valueOf(alarm.getLevel()) > Integer.valueOf(cal.getAlarmLevel())) {
							cal.setDistance(cal.getWinds());
							cal.setAlarmLevel(alarm.getLevel());
							cal.setAlarmImgtime(fdo.getTime());
							//Convert Alarm entities and WindCalculator into JSONObject objects(The following one line of code can be reused)
							JSONObject alarmJSONObject = JSONObject.parseObject(JSONObject.toJSONString(alarm));
							log.info("Alarm data---->>>>>"+alarmJSONObject.toJSONString());
							log.error("What is the cal obtained at the end of the test------>>>>>----"+cal);
							alarmJSONObject.put("winds",cal.getWinds());
							alarmJSONObject.put("windd",cal.getWindd());
							alarmJSONObject.put("windTime",cal.getTime());
//							weixinClient.sendWind(alarmJSONObject);
							AlarmRoad alarmRoad=new AlarmRoad();
							alarmRoad.setAlarmLevel(AlarmLevelUtil.getLevelByWinds(cal.getWinds()));
							alarmRoad.setMindistanceHis(cal.getWinds());
							alarmRoad.setMindistanceNow(cal.getWinds());
							alarmRoadService.updateById(alarmRoad);
							fogChannelUtil.windPushSend(alarmJSONObject);
						}
					} catch (Exception e) {
						log.error("大风告警错误------>>>>>----"+cal, e);
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			log.error("大风告警错误------>>>>>----"+cal, e);
			e.printStackTrace();
			return "-";
		}
		String alarmId = "-";
		if (StringUtils.isNotEmpty(cal.getAlarmId())) {
			alarmId = cal.getAlarmId();
		}
		return alarmId;
	}
	/**
	 * 插入大风日志
	 *
	 * @param windLog
	 * @return
	 */
	public boolean insertMongo(WindLog windLog) {
		if (StringUtils.isEmpty(windLog.getWindId())) {
			windLog.setWindId(StringUtils.getUUID());
		}
		//建立索引
		mongoTemplate.insert(windLog, windLog_);

		return true;
	}
}
