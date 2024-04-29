package org.lkdt.modules.fog.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lkdt.common.util.AlarmLevelUtil;
import org.lkdt.common.util.StringUtils;
import org.lkdt.common.util.oConvertUtils;
import org.lkdt.modules.fog.calculator.FcFactory;
import org.lkdt.modules.fog.calculator.FogCalRedis;
import org.lkdt.modules.fog.calculator.FogCalculator;
import org.lkdt.modules.fog.entity.Alarm;
import org.lkdt.modules.fog.entity.AlarmRoad;
import org.lkdt.modules.fog.entity.AlarmRoadModel;
import org.lkdt.modules.fog.mapper.AlarmMapper;
import org.lkdt.modules.fog.mapper.AlarmRoadMapper;
import org.lkdt.modules.fog.service.IAlarmRoadService;
import org.lkdt.modules.fog.service.IAlertThreepartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;

/**
 * @Description: 告警道路表
 * @Author: jeecg-boot
 * @Date: 2021-04-27
 * @Version: V1.0
 */
@Service
public class AlarmRoadServiceImpl extends ServiceImpl<AlarmRoadMapper, AlarmRoad> implements IAlarmRoadService {
    private static final Logger logger = LoggerFactory.getLogger(AlarmRoadServiceImpl.class);
    @Resource
    private AlarmRoadMapper alarmRoadMapper;
    @Resource
    private AlarmMapper alarmMapper;

    @Autowired
    private FogCalRedis fogCalRedis;

    @Autowired
    private FcFactory fcFactory;

    @Autowired
    private EquipmentServiceImpl equipmentService;
    @Autowired
    private IAlertThreepartService alertThreepartService;

    @Override
    @Transactional
    public void saveMain(AlarmRoad alarmRoad, List<Alarm> alarmList) {
        alarmRoadMapper.insert(alarmRoad);
        if (alarmList != null && alarmList.size() > 0) {
            for (Alarm entity : alarmList) {
                //外键设置
                entity.setRoadAlarmId(alarmRoad.getId());
                alarmMapper.insert(entity);
            }
        }
    }

    @Override
    public List<AlarmRoad> queryCalendarAlarm(Map<String, Object> map) {
        return alarmRoadMapper.queryCalendarAlarm(map);
    }

    @Override
    public List<AlarmRoad> queryTheNumberOfAnnualAlarms(Map<String,Object> map) {
        return alarmRoadMapper.queryTheNumberOfAnnualAlarms(map);
    }


    @Override
    @Transactional
    public void updateMain(AlarmRoad alarmRoad, List<Alarm> alarmList) {
        alarmRoadMapper.updateById(alarmRoad);

        //1.先删除子表数据
        alarmMapper.deleteByMainId(alarmRoad.getId());

        //2.子表数据重新插入
        if (alarmList != null && alarmList.size() > 0) {
            for (Alarm entity : alarmList) {
                //外键设置
                entity.setRoadAlarmId(alarmRoad.getId());
                alarmMapper.insert(entity);
            }
        }
    }

    @Override
    @Transactional
    public void delMain(String id) {
        alarmMapper.deleteByMainId(id);
        alarmRoadMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void delBatchMain(Collection<? extends Serializable> idList) {
        for (Serializable id : idList) {
            alarmMapper.deleteByMainId(id.toString());
            alarmRoadMapper.deleteById(id);
        }
    }

    @Override
    public boolean alarmRoad(Alarm alarm, boolean sendSms, String phones, String openid, String type) {
        try {

            int distance = alarm.getDistance().intValue();
            FogCalculator cal = fcFactory.getCalculator(alarm.getEpId());
            AlarmRoadModel alarmRoad = fogCalRedis.getAlarmRoadRedis(cal);
            if (StringUtils.isEmpty(alarmRoad.getId())) {
                if (distance > 200) {
                    log.error("告警解除取消，已解除" + cal.toString());
                    return false;
                }
                // 发生告警
                alarmRoad.setId(StringUtils.getUUID());
                alarmRoad.setAlarmLevel(AlarmLevelUtil.getLevelByDist(distance));
                alarmRoad.setImgpath(alarm.getImgpath());
                alarmRoad.setImgtime(alarm.getImgtime());
                alarmRoad.setMindistanceHis((float) distance);
                alarmRoad.setMindistanceNow((float) distance);
                alarmRoad.setStarttime(new Date());
                alarmRoad.setRoadAlarmType(alarm.getFogType());
                alarm.setRoadAlarmId(alarmRoad.getId());
                AlarmRoad alarmRoad1 = new AlarmRoad();
                BeanUtils.copyProperties(alarmRoad, alarmRoad1);
                alarmRoadMapper.insert(alarmRoad1);
                // 发送消息
                log.error("222" + JSON.toJSONString(alarmRoad));
                alertThreepartService.sendAlertMessage(alarmRoad, alarm.getEpId(), openid, sendSms, type);
                alarmRoadCall(alarmRoad, cal, openid);
                log.error("发生告警" + JSON.toJSONString(alarmRoad));
                //纳兴诱导灯
                fogCalRedis.setAlarmRoadRedis(alarmRoad, cal);
                return true;
            } else {
                if (Integer.valueOf(AlarmLevelUtil.getLevelByDist(distance)) > Integer.valueOf(alarmRoad.getAlarmLevel())) {
                    // 等级提高
                    alarmRoad.setAlarmLevel(AlarmLevelUtil.getLevelByDist(distance));
                    alarmRoad.setImgpath(alarm.getImgpath());
                    alarmRoad.setImgtime(alarm.getImgtime());
                    alarmRoad.setMindistanceHis((float) distance);
                    alarmRoad.setMindistanceNow((float) distance);
                    alarmRoad.setRoadAlarmType(alarm.getFogType());
                    AlarmRoad alarmRoad1 = new AlarmRoad();
                    BeanUtils.copyProperties(alarmRoad, alarmRoad1);
                    alarmRoadMapper.updateById(alarmRoad1);
                    // 发送消息
                    alertThreepartService.sendAlertMessage(alarmRoad, alarm.getEpId(), openid, sendSms, type);
                    log.error("告警等级增加" + JSON.toJSONString(alarmRoad));
                    //纳兴诱导灯
                    fogCalRedis.setAlarmRoadRedis(alarmRoad, cal);
                    return true;
                } else if ("0".equals(AlarmLevelUtil.getLevelByDist(distance))) {
                    // 判断同路段摄像头是否有雾
                    List<FogCalculator> fogCalList = fcFactory.getCalculatorsByHwId(alarmRoad.getHwId().toString());
                    for (FogCalculator fogCalculator : fogCalList) {
                        if (fogCalculator.isFogNow()) {
                            log.error("因有雾，告警解除取消" + fogCalculator.toString());
                            return false;
                        }
                    }
                    // 告警解除
                    alarmRoad.setEndtime(new Date());
                    alarmRoad.setAlarmLevel(AlarmLevelUtil.getLevelByDist(alarmRoad.getMindistanceHis().intValue()));
                    alarmRoad.setRoadAlarmType(alarm.getFogType());
                    try {
                        // 计算影响里程数
                        double effectedMile = 0;
                        if(!oConvertUtils.isEmpty(alarmRoad)){
                            // 查询解除告警路段摄像头
                            List<Alarm> alarms = alarmMapper.selectByMainId(alarmRoad.getId());
                            // 根据epId获取权重信息
                            List<Double> scoreList = new ArrayList<>();
                            for (Alarm alarm1:alarms){
                                scoreList.add(fcFactory.hwCamScoreScore(alarmRoad.getHwId(),alarm1.getEpId()));
                            }
                            Collections.sort(scoreList);
                            double min = scoreList.get(0)/1000;
                            double max = scoreList.get(scoreList.size()-1)/1000;
                            effectedMile = max-min;
                        }
                        alarmRoad.setEffectedMile(effectedMile);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("计算影响里程数失败" + e);
                    }
                    AlarmRoad alarmRoad1 = new AlarmRoad();
                    BeanUtils.copyProperties(alarmRoad, alarmRoad1);
                    alarmRoadMapper.updateById(alarmRoad1);
                    alarmRoad.setAlarmLevel(AlarmLevelUtil.getLevelByDist(distance));
                    alarmRoad.setMindistanceNow((float) distance);
                    // 发送消息
                    alertThreepartService.sendAlertMessage(alarmRoad, alarm.getEpId(), openid, sendSms, type);
                    alarmRoadCall(alarmRoad, cal, openid);
                    log.error("告警解除" + JSON.toJSONString(alarmRoad));
                    alarmRoad.setId("");
                    alarmRoad.setEndtime(null);
                    //纳兴诱导灯
                    fogCalRedis.setAlarmRoadRedis(alarmRoad, cal);
                    return true;
                }
                if (distance < alarmRoad.getMindistanceHis()) {
                    // 记录历史最低能见度
                    alarmRoad.setImgpath(alarm.getImgpath());
                    alarmRoad.setImgtime(alarm.getImgtime());
                    alarmRoad.setMindistanceHis((float) distance);
                }
                if (Integer.valueOf(alarmRoad.getAlarmLevel()) >= 2 && distance > fcFactory.getPhaseRelease()) {
                    FogCalculator lastCal = fcFactory.check100(fcFactory.getCalculatorsByHwId(alarmRoad.getHwId().toString()));
                    if (lastCal != null && lastCal.getDistance() <= 200) {
                        alarmRoad.setAlarmLevel(AlarmLevelUtil.getLevelByDist(distance));
                        alarmRoad.setMindistanceNow((float) distance);
                        alertThreepartService.sendAlertMessage(alarmRoad, alarm.getEpId(), openid, sendSms, type);
                        log.error("能见度回升100--" + JSON.toJSONString(alarmRoad));
                    }
                }
            }
            fogCalRedis.setAlarmRoadRedis(alarmRoad, cal);
            return false;
        } catch (Exception e) {
            log.error("", e);
            return false;
        }

    }

    /**
     * 打电话
     *
     * @param alarmRoad
     * @param openId
     */
    @Override
    public void alarmRoadCall(AlarmRoadModel alarmRoad, FogCalculator cal, String openId) {
//		HighwayDO hw = fcFactory.getHighwayByHwId(alarmRoad.getHwId().toString());
//		if(hw.getPhoneNumber() == null) {
//			return ;
//		}
//		String[] phones = hw.getPhoneNumber().split(",");
//		logger.info("告警拨打电话号码："+JSON.toJSONString(phones));
//		for (int i = 0; i < phones.length; i++) {
//			if(!StringUtils.isEmpty(phones[i])) {
//				Map<String, Object> map = new HashMap<String, Object>();
//				map.put("roadAlarmId", alarmRoad.getRoadAlarmId());
//				map.put("alarmLevel", alarmRoad.getAlarmLevel());
//				map.put("callNumber", phones[i]);
//				int count = alarmRoadCallLogService.count(map);
//				if(count == 0) {
//					//打电话接口
//					HttpHeaders headers = new HttpHeaders();
//					MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
//					headers.setContentType(type);
//
//					String url = externalpServicePath+"ext/aliyun/sendCallByTts";
//					Map<String,Object> paramMap = new HashMap<String,Object>();
//					paramMap.put("CalledShowNumber", "");
//					paramMap.put("calledNumber", phones[i]);
//					JSONObject jsonObject = new JSONObject();
//					String distance = alarmRoad.getMindistanceNow() > 500 ? "大于500" : ""+alarmRoad.getMindistanceNow();
//					jsonObject.put("destance", distance);
//					if(alarmRoad.getMindistanceNow() > 200 ) {
//						//解除模板
//						paramMap.put("ttsCode", "TTS_210067045");
//						jsonObject.put("first", hw.getName());
//					}else {
//						//告警模板
//						paramMap.put("ttsCode", "TTS_209837650");
//						Integer[] ints = FogCalculator.parseEquName(cal.getEquipment().getEquName());
//						jsonObject.put("first", hw.getName()+"，桩号：K"+ints[0]);
//					}
//					paramMap.put("ttsParam", jsonObject.toJSONString());
//					HttpEntity<String> entity = new HttpEntity<String>(JSON.toJSONString(paramMap), headers);
//
//					JSONObject result = new JSONObject();
//					try {
//						RestTemplate restTemplate = new RestTemplate();
//						ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, entity, String.class);
//						String pResponse = responseEntity.getBody();
//						JSONObject json = JSONObject.parseObject(pResponse);
//						logger.info("告警拨打电话结果："+json);
//						result = JSONObject.parseObject(json.getString("result"));
//					} catch (Exception e) {
//						// TODO: handle exception
//						result.put("message", e.getMessage());
//					}
//
//
//					//入库
//					AlarmRoadCallLogDO alarmRoadCallLog = new AlarmRoadCallLogDO();
//					alarmRoadCallLog.setId(StringUtils.getUUID());
//					alarmRoadCallLog.setRoadAlarmId(alarmRoad.getRoadAlarmId());
//					alarmRoadCallLog.setHwId(alarmRoad.getHwId().intValue());
//					alarmRoadCallLog.setAlarmLevel(alarmRoad.getAlarmLevel());
//					alarmRoadCallLog.setDistance(alarmRoad.getMindistanceNow());
//					alarmRoadCallLog.setAlarmTime(alarmRoad.getStarttime());
//					alarmRoadCallLog.setCallId(result.getString("callId"));
//					alarmRoadCallLog.setCallTime(new Date());
//					alarmRoadCallLog.setCallContent(jsonObject.toJSONString());
//					alarmRoadCallLog.setCallNumber(phones[i]);
//					alarmRoadCallLog.setCallResultCode(result.getString("code"));
//					alarmRoadCallLog.setCallResultMessage(result.getString("message"));
//					alarmRoadCallLog.setOpenid(openId);
//					alarmRoadCallLogService.save(alarmRoadCallLog);
//				}
//			}
//		}


    }

    @Override
    public String checkAlarmRoad(Alarm alarm) {
        try {
            int distance = alarm.getDistance().intValue();
            FogCalculator cal = fcFactory.getCalculator(alarm.getEpId());
            AlarmRoadModel alarmRoad = fogCalRedis.getAlarmRoadRedis(cal);
            if (StringUtils.isEmpty(alarmRoad.getId())) {
                if (distance > 200) {
                    logger.error("check告警解除取消，已解除" + cal.toString());
                    return "";
                }
                // 发生告警
                logger.error("check发生告警" + JSON.toJSONString(alarmRoad));
                return AlarmLevelUtil.getLevelDescByDist(distance);
            } else {
                if (Integer.valueOf(AlarmLevelUtil.getLevelByDist(distance)) > Integer.valueOf(alarmRoad.getAlarmLevel())) {
                    // 等级提高
                    logger.error("check告警等级增加" + JSON.toJSONString(alarmRoad));
                    return AlarmLevelUtil.getLevelDescByDist(distance);
                } else if ("0".equals(AlarmLevelUtil.getLevelByDist(distance))) {
                    // 判断同路段摄像头是否有雾
                    List<FogCalculator> fogCalList = fcFactory.getCalculatorsByHwId(alarmRoad.getHwId().toString());

                    for (FogCalculator fogCalculator : fogCalList) {
                        if (fogCalculator.isFogNow() && !alarm.getEpId().equals(fogCalculator.getEpId())) {
                            logger.error("check因有雾，告警解除取消" + fogCalculator.toString());
                            return "";
                        }
                    }
                    // 告警解除
                    logger.error("check告警解除" + JSON.toJSONString(alarmRoad));
                    return AlarmLevelUtil.getLevelDescByDist(distance);

                }
            }
            if (Integer.valueOf(alarmRoad.getAlarmLevel()) >= 2 && distance > fcFactory.getPhaseRelease()) {
                FogCalculator lastCal = fcFactory.check100(fcFactory.getCalculatorsByHwId(alarmRoad.getHwId().toString()));
                if (lastCal != null && lastCal.getDistance() <= 200) {
                    log.error("check能见度回升100--" + JSON.toJSONString(alarmRoad));
                    return "回升";
                }
            }
            return "";
        } catch (Exception e) {
            logger.error("check错误",e);
            return "";
        }
    }

    @Override
    public Integer queryBindCount() {
        return alarmRoadMapper.queryMonthCount() - alarmRoadMapper.queryBindCount();
    }

    @Override
    public Integer queryMonthCount() {
        return alarmRoadMapper.queryMonthCount() - alarmRoadMapper.queryBindCount();
    }

    @Override
    public List<AlarmRoad> queryYearRoadReport(Map<String, Object> map) {
        return alarmRoadMapper.queryYearRoadReport(map);
    }

    @Override
    public List<AlarmRoad> queryYearLevelReport(Map<String, Object> map) {
        return alarmRoadMapper.queryYearLevelReport(map);
    }

    @Override
    public List<AlarmRoad> queryMonthRoadReport(Map<String, Object> map) {
        return alarmRoadMapper.queryMonthRoadReport(map);
    }

    @Override
    public List<AlarmRoad> queryMonthLevelReport(Map<String, Object> map) {
        return alarmRoadMapper.queryMonthLevelReport(map);
    }

    @Override
    public List<AlarmRoad> queryDayRoadReport(Map<String, Object> map) {
        return alarmRoadMapper.queryDayRoadReport(map);
    }

    @Override
    public List<AlarmRoad> queryDayLevelReport(Map<String, Object> map) {
        return alarmRoadMapper.queryDayLevelReport(map);
    }

    @Override
    public List<AlarmRoad> queryListByDay(Map<String, Object> paramsMap) {
        return alarmRoadMapper.queryListByDay(paramsMap);
    }

    @Override
    public List<AlarmRoad> queryMongoListByDay(Map<String, Object> paramsMap) {
        return alarmRoadMapper.queryMongoListByDay(paramsMap);
    }

    @Override
    public AlarmRoad queryByHwId(String hwId){
        return alarmRoadMapper.queryByHwId(hwId);
    }
}
