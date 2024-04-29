package org.lkdt.modules.fog.calculator;

import cn.hutool.core.net.URLDecoder;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.lkdt.common.util.AlarmLevelUtil;
import org.lkdt.common.util.DateUtils;
import org.lkdt.common.util.RedisUtil;
import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.fog.controller.AlarmController;
import org.lkdt.modules.fog.entity.Alarm;
import org.lkdt.modules.fog.entity.AlarmLog;
import org.lkdt.modules.fog.entity.AlarmModel;
import org.lkdt.modules.fog.entity.AlarmNotice;
import org.lkdt.modules.fog.mongodb.MongoLogTemplate;
import org.lkdt.modules.fog.service.IAlarmNoticeService;
import org.lkdt.modules.fog.service.IAlarmService;
import org.lkdt.modules.fog.service.IEquipmentService;
import org.lkdt.modules.fog.service.IThirdPartyService;
import org.lkdt.modules.fog.vo.FogHistory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class CalculatorService {


	private long limitTime = 30 * 60 * 1000;

	@Value("${lkdt.isAutoAlarm}")
	private String isAutoAlarm;

//	@Autowired
//	private ZcloudConfig zcloudconfig;
//
//	@Autowired
//	private ImageService imageService;

	@Autowired
    IEquipmentService equipmentService;

//	@Autowired
//	FogvalueService fogvalueService;

	@Autowired
	private IAlarmService alarmService;

//	@Autowired
//	private ActiveMqService activeMqService;

	@Autowired
	private IAlarmNoticeService alarmNoticeService;

//	@Autowired
//	UserService userService;

	@Autowired
	private IThirdPartyService thirdPartyGDService;

	@Autowired
	private MongoLogTemplate mongoLogTemplate;

	@Autowired
	FcFactory fcFactory;

	@Autowired
	RedisUtil redisUtil;

	@Autowired
	private FogSpaceCal fogSpaceCal;

	@Autowired
	FogCalRedis fogCalRedis;

	@Autowired
	AlarmController alarmController;

	public void execute(List<FogHistory> fhs, String sysName) {
		// 1.计算可见距离，计算添加团雾/雾霾等级
		if (fhs == null || fhs.size() == 0) {
			return;
		}
		fcFactory.saveSysTime(sysName, System.currentTimeMillis());
		for (FogHistory fdo : fhs) {
			String camuuid = fdo.getEpId();
			FogCalculator cal = fcFactory.getCalculator(camuuid);
			if (cal == null) {
				continue;
			}
			cal.setSysName(sysName);
			// 计算可见距离，把起雾/散雾状态添加到缓存
			if (fdo.getfValue() <= 0) {
				continue;
			}
			boolean result = doCalculation(fdo, cal);

			if (!result) {
				continue;
			}
			//前面不更新redis，这里统一操作
			fogCalRedis.extracted(cal);
			// 添加告警功能
			String alarmId = "";
			if ("0".equals(cal.getEquipment().getState()) && cal.getDistance() != null) {
				alarmId = this.executeAlarm(cal);
			}

			String equname = "";
			try {
				equname = new String(cal.getEquipment().getEquName().getBytes("utf-8"), "utf-8");
			} catch (Exception e) {
				log.error("equname转换错误" + cal,e);
			}

			if(cal.getDistance() == null){
				return;
			}
			AlarmLog alarmLog = new AlarmLog();
			alarmLog.setAlarmId(alarmId);
			alarmLog.setDateTime(DateUtils.format(fdo.getfSampleTime(), "yyyy-MM-dd HH:mm:ss.SSS"));
			alarmLog.setEpId(cal.getEpId());
			alarmLog.setEpName(equname);
			alarmLog.setModifyVal(String.valueOf(cal.getDistance()));
			alarmLog.setOriginVal(String.valueOf(fdo.getfValue()));
//			alarmLog.setParamNum(String.valueOf(cal.getCalx()));
			alarmLog.setSystemName(sysName);
			alarmLog.setCreatetime(DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
//			alarmLog.setCulumn1();

			fogCalRedis.setFmodelRedis(fdo.getFmodel(), cal);
			// 沙盘模式
			if (org.apache.commons.lang.StringUtils.equalsIgnoreCase(fdo.getFmodel(), "ST")) {
				fogCalRedis.setImgtimeRedis(new Date(), cal);
				fogCalRedis.setImgurlRedis(fdo.getImgurl(), cal);
				alarmLog.setImgName(fdo.getImgurl());
				alarmLog.setDateTime(DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
				mongoLogTemplate.insert(alarmLog);
//				logger.warn(cal.getEpId() + "|" + equname + "|" + fdo.getfValue() + "|-" + "|" + cal.getDistance() + "|"
//						+ cal.getCalx() + "|" + alarmId + "|" + fdo.getImgurl() + "|" + fdo.getFmodel());
			} else { // 非沙盘模式
				alarmLog.setImgName(fdo.getImgfn());
				mongoLogTemplate.insert(alarmLog);
//				logger.warn(cal.getEpId() + "|" + equname + "|" + fdo.getfValue() + "|-" + "|" + cal.getDistance() + "|"
//						+ cal.getCalx() + "|" + alarmId + "|" + fdo.getImgfn() + "|" + fdo.getFmodel());

				// 判断是否发送第三方数据
				thirdPartyGDService.checkDistanceSend(cal);

				// 判断是否发出蓝色通知

			}

		}
	}

	/**
	 * 添加告警功能
	 * 
	 * @param cal              计算器
	 */
	private String executeAlarm(FogCalculator cal) {
		try {
			Alarm alarm = new Alarm();
			alarm.setEpId(cal.getEpId());
			alarm.setAddress(cal.getEquipment().getEquLocation());
			alarm.setImgpath(cal.getImgpath());
			alarm.setImgtime(cal.getImgtime());
			alarm.setEquName(cal.getEquipment().getEquName());
			alarm.setLevel(AlarmLevelUtil.getLevelByDist(cal.getDistance()));
			if (cal.getDistance() > 200) {
				// 调用你的告警service.增加一条报警;反推websocket;
			}
			int avg = fogCalRedis.getAverage(5, cal.getDistance(), cal);
			if (cal.getDistance() > 200 && avg > 200) {
				// 判断前10分钟数据平均值
				// 录入结束告警时间
				if (StringUtils.isNotEmpty(cal.getAlarmId())) {
					alarm.setId(cal.getAlarmId());
					alarm.setEndtime(new Date());
					alarm.setDistance(Float.valueOf(cal.getSmDistance()));// update用
					alarm.setType(4);
					// 告警结束将告警id置空
					if (!cal.isFogNow()) {
						fogCalRedis.setAlarmDistanceRedis(cal.getDistance(), cal);
						fogCalRedis.fogOption(4, true, "", cal);
						fogCalRedis.setUpdateTimeRedis(new Date(), cal);
//						cal.setAlarmImgpath("");
//						cal.setAlarmImgtime(null);
						cal.setStatus(9);
						log.error("=================告警解除邮件");

						if (StringUtils.isNotEmpty(cal.getNoticeId())) {
							AlarmNotice notice = new AlarmNotice();
							notice.setId(cal.getNoticeId());
							notice.setHandletime(new Date());
							alarmNoticeService.updateById(notice);
						}

						alarmService.updateById(alarm);
					} else {
//						cal.setAlarmLevel("0");
						// 发通知用
						alarm.setDistance(Float.valueOf(cal.getDistance()));
						alarm.setCalx(cal.getVal());
						cal.setAlarmLevel(alarm.getLevel());
						if(cal.getCameraType() != 5){

							sendNotice(alarm, cal, 4);// 告警解除
						}


					}
				}
			} else if (cal.getDistance() <= 200 && avg <= 200) {
				// 判断前10分钟数据平均值
				// 出现告警时间
				Date alarmDate = new Date();
				// 能见度小于200m备份告警图片
//				alarmService.alarmImgBackUp(path, r.getImgfn(), cal.getEpId(), alarmDate);
				// 告警信息入库
				if (StringUtils.isEmpty(cal.getAlarmId())) {
//					Map<String, Object> params = new HashMap<>();
//					params.put("epId", cal.getEpId());
//					List<AlarmDO> adolist = alarmService.selectInfoByEpId(params);
//					if (adolist.size() > 0) {
//						AlarmDO ad = adolist.get(0);
//						// 计算器设置告警id，当前可见距离
//						cal.startAlarmFromDB(ad);
//					} else {
						// 在告警入库时判断 1：团雾，2：雾霾
						FogCalculator fogCalculator = fcFactory.getCalculator(cal.getEpId());
						if (fogSpaceCal.isFog(fogCalculator)) {
							alarm.setFogType("11");
						} else {
							alarm.setFogType("12");
						}
//					cal.setFogStatus(r.getStatus());
						alarm.setImgpath(cal.getImgpath());
						alarm.setImgtime(cal.getImgtime());
						alarm.setIseffective("9");// 1:已确认；0确认无效；9未确认
						alarm.setBegintime(alarmDate);
						alarm.setDistance(Float.valueOf(cal.getDistance()));
//						alarm.setCalx(cal.getVal());
						alarm.setType(1);
						fogCalRedis.setAlarmStartTimeRedis(alarmDate, cal);
						cal.setAlarmLevel(alarm.getLevel());
						cal.setLevel(alarm.getLevel());
						// alarmService.save(alarm);
						// 计算器设置告警id，当前可见距离
						fogCalRedis.setSmDistanceRedis(cal.getDistance(), cal);
//						sendEmail(alarm);
						if ("0".equals(cal.getEquipment().getState())) {
							sendNotice(alarm, cal, 1);// 发生告警
						}

//					}
				} else {
					if (cal.getCameraType() == 5) {
						fogCalRedis.fogOption(1, true, "", cal);
					}
					if (cal.getDistance() < cal.getSmDistance() && cal.getDistance() > 0) {
						// 发出蓝色告警时存储path
						fogCalRedis.setSmDistanceRedis(cal.getDistance(), cal);
						fogCalRedis.setAlarmImgpathRedis(cal.getImgpath(), cal);
						fogCalRedis.setAlarmImgtimeRedis(cal.getImgtime(), cal);
						redisUtil.sSet(fcFactory.UNCONFIRM_ALARM_EPIDS_CACHE_NAME, cal.getEpId());
						if(StringUtils.equals("1", isAutoAlarm)){
							sendNotice(alarm, cal, 2);// 发生告警
						}
					}

				}
			} else {
				log.error(avg + "=================" + cal.getDistance());
				return "-";
			}
		} catch (Exception e) {
			log.error("executeAlarm=================" + cal.getDistance(), e);
			return "-";
		}

		String alarmId = "-";
		if (StringUtils.isNotEmpty(cal.getAlarmId())) {
			alarmId = cal.getAlarmId();
		}
		return alarmId;
	}
	//发出告警
	public void sendNotice(Alarm alarm, FogCalculator cal, int alarmType) {
		fogCalRedis.fogOption(alarmType, false, "", cal);
		alarm.setId(cal.getAlarmId());
//		fogCalRedis.setUpdateTimeRedis(new Date(), cal);
//		fogCalRedis.setAlarmImgpathRedis(cal.getImgpath(), cal);
//		fogCalRedis.setAlarmImgtimeRedis(cal.getImgtime(), cal);
//		fogCalRedis.setAlarmDistanceRedis(cal.getDistance(), cal);
//		fogCalRedis.setAlarmLevelRedis(alarm.getLevel(), cal);
		cal.setUpdateTime(new Date());
		cal.setAlarmImgpath(cal.getImgpath());
		cal.setAlarmImgtime(cal.getImgtime());
		cal.setAlarmDistance(cal.getDistance());
		cal.setAlarmLevel(alarm.getLevel());

		AlarmNotice alarmnotice = new AlarmNotice();
		alarmnotice.setId(StringUtils.getUUID());
		alarmnotice.setEpId(cal.getEpId());
		alarmnotice.setAlarmId(alarm.getId());
		alarmnotice.setHandler("1");
		alarmnotice.setSender("1");
		alarmnotice.setSendtext(JSONObject.toJSONString(alarm));
		alarmnotice.setSendtime(new Date());
		alarmnotice.setType(String.valueOf(alarmType));
		alarmnotice.setIseffective(alarm.getIseffective());
		alarmNoticeService.save(alarmnotice);
		cal.setNoticeId(alarmnotice.getId());
//		fogCalRedis.setNoticeIdRedis(cal.getNoticeId(), cal);
		fogCalRedis.extractedNotice(cal);
		if(StringUtils.equals("1", isAutoAlarm)){
			AlarmModel alarmModel = new AlarmModel();
			BeanUtils.copyProperties(alarm, alarmModel);
			alarmController.confirm(alarmModel);
		}

	}

	/**
	 * 处理数据
	 *
	 * @param fdo 雾霾数据
	 * @return 计算结果
	 */
	public boolean doCalculation(FogHistory fdo, FogCalculator cal) {
		Date time = fdo.getfSampleTime();
		int val = fdo.getfValue();
		// 判断是否已经在缓存
		// 判断是否夜晚
		int distance = val;
		boolean isnight = false;
		// 人工确认时间在半小时内的，上升5%限制
		Integer confirmVal = cal.getConfirmDistance();
//		if (cal.getConfirmDate() != null && confirmVal != null && confirmVal != 0 && System.currentTimeMillis() - cal.getConfirmDate().getTime() < limitTime
//				&& confirmVal <= 200 && distance > confirmVal * 1.05) {
//			int maxVal;
//			if (confirmVal <= 30) {
//				maxVal = 30;
//			} else if (confirmVal <= 50) {
//				maxVal = 50;
//			} else if (confirmVal <= 100) {
//				maxVal = 100;
//			} else {
//				maxVal = 200;
//			}
//			distance = (int) (confirmVal * 1.05);
//			if (distance > maxVal) {
//				distance = maxVal;
//			}
////			this.setConfirmDistanceRedis(distance, cal);
//			cal.setConfirmDistance(distance);
//		} else
		if (cal.getDistance() != null) {
			int lastVal = cal.getDistance();
			if (distance > lastVal * 1.5) {
				distance = (int) (lastVal * 1.5);
			} else if (distance < lastVal * 0.5) {
				distance = (int) (lastVal * 0.5);
			}
		}
		// 判断起雾、散雾--通过可见距离判断是逐渐变大或变小
		int status = 9;// 0:起雾；1散雾; 9没有雾
		if (cal.getDistance() != null && distance < 200) {
			if (cal.getDistance() > distance) {
				status = 1;
			} else {
				status = 0;
			}
		}

		cal.setVal(val);
		cal.setStatus(status);
//        this.setDistanceRedis(distance, cal);
//        this.setImgtimeRedis(time, cal);
//        this.setImgpathRedis(fdo.getImgfn(), cal);
		cal.setDistance(distance);
		cal.setImgtime(time);
		cal.setImgpath(fdo.getImgfn());

//		FogCalculatorResult fv = new FogCalculatorResult(isnight, distance, status, time, val, fdo.getImgfn(),
//				(float) 1);
		// 删除1分钟以外的数据

//		FogCalculatorResult start = cacheVals.getHead().getData();
//		if(start.getFtime().getTime() - time.getTime() > 1*60*1000) {
		return true;
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		String url = "http://fog-images.oss-cn-hangzhou.aliyuncs.com/2021-7-21/K761_680S-2/1626831905499.jpg?Expires=1658368168&OSSAccessKeyId=LTAI4G7Rk6YgVGRU9MxBd2iQ&Signature=ZDF4JAl6/BA%2BdUAt2mdNQjqY4EM=";
		url = URLDecoder.decode(url, Charset.defaultCharset()).replaceAll("\\+","%2B");
		System.out.print(url);
	}

}

