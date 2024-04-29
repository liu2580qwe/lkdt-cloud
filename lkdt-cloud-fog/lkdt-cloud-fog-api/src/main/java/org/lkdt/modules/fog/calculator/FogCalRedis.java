package org.lkdt.modules.fog.calculator;

import cn.hutool.core.bean.BeanUtil;
import org.lkdt.common.util.RedisUtil;
import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.fog.entity.AlarmRoadModel;
import org.lkdt.modules.fog.entity.EquipmentModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HuangJunYao
 * @date 2020/12/28
 */
@Service
public class FogCalRedis {
    @Autowired
    FcFactory fcFactory;

    @Autowired
    RedisUtil redisUtil;

    public void setFmodelRedis(String fmodel, FogCalculator cal) {
        cal.setFmodel(fmodel);
        redisUtil.hset(fcFactory.CAMERA_CACHE_NAME + cal.getEpId(), "fmodel", fmodel);
    }

    public void setImgurlRedis(String imgurl, FogCalculator cal) {
        cal.setImgurl(imgurl);
        redisUtil.hset(fcFactory.CAMERA_CACHE_NAME + cal.getEpId(), "imgurl", imgurl);
    }

    public void setDistanceRedis(int distance, FogCalculator cal) {
        cal.setDistance(distance);
        redisUtil.hset(fcFactory.CAMERA_CACHE_NAME + cal.getEpId(), "distance", distance);
    }

    public void setSmDistanceRedis(int smDistance, FogCalculator cal) {
        cal.setSmDistance(smDistance);
        redisUtil.hset(fcFactory.CAMERA_CACHE_NAME + cal.getEpId(), "smDistance", smDistance);
    }

    public void setImgpathRedis(String imgpath, FogCalculator cal) {
        cal.setImgpath(imgpath);
        redisUtil.hset(fcFactory.CAMERA_CACHE_NAME + cal.getEpId(), "imgpath", imgpath);
    }

    public void setImgtimeRedis(Date imgtime, FogCalculator cal) {
        cal.setImgtime(imgtime);
        redisUtil.hset(fcFactory.CAMERA_CACHE_NAME + cal.getEpId(), "imgtime", imgtime);
    }

    public void setAlarmIdRedis(String alarmId, FogCalculator cal) {
        cal.setAlarmId(alarmId);
        redisUtil.hset(fcFactory.CAMERA_CACHE_NAME + cal.getEpId(), "alarmId", alarmId);
    }

    public void setNoticeIdRedis(String noticeId, FogCalculator cal) {
        cal.setNoticeId(noticeId);
        redisUtil.hset(fcFactory.CAMERA_CACHE_NAME + cal.getEpId(), "noticeId", noticeId);
    }

    public void setSmDistanceRedis(Integer smDistance, FogCalculator cal) {
        cal.setSmDistance(smDistance);
        redisUtil.hset(fcFactory.CAMERA_CACHE_NAME + cal.getEpId(), "smDistance", smDistance);
    }

    public void setAlarmDistanceRedis(int alarmDistance, FogCalculator cal) {
        cal.setAlarmDistance(alarmDistance);
        redisUtil.hset(fcFactory.CAMERA_CACHE_NAME + cal.getEpId(), "alarmDistance", alarmDistance);
    }

    public void setAlarmStartTimeRedis(Date alarmStartTime, FogCalculator cal) {
        cal.setAlarmStartTime(alarmStartTime);
        redisUtil.hset(fcFactory.CAMERA_CACHE_NAME + cal.getEpId(), "alarmStartTime", alarmStartTime);
    }

    public void setAlarmEndTimeRedis(Date alarmEndTime, FogCalculator cal) {
        cal.setAlarmEndTime(alarmEndTime);
        redisUtil.hset(fcFactory.CAMERA_CACHE_NAME + cal.getEpId(), "alarmEndTime", alarmEndTime);
    }

    public void setAlarmImgpathRedis(String alarmImgpath, FogCalculator cal) {
        cal.setAlarmImgpath(alarmImgpath);
        redisUtil.hset(fcFactory.CAMERA_CACHE_NAME + cal.getEpId(), "alarmImgpath", alarmImgpath);
    }

    public void setAlarmLevelRedis(String alarmLevel, FogCalculator cal) {
        cal.setAlarmLevel(alarmLevel);
        redisUtil.hset(fcFactory.CAMERA_CACHE_NAME + cal.getEpId(), "alarmLevel", alarmLevel);
    }

    public void setAlarmImgtimeRedis(Date alarmImgtime, FogCalculator cal) {
        cal.setAlarmImgtime(alarmImgtime);
        redisUtil.hset(fcFactory.CAMERA_CACHE_NAME + cal.getEpId(), "alarmImgtime", alarmImgtime);
    }

    public void setCameraTypeRedis(int cameraType, FogCalculator cal) {
        cal.setCameraType(cameraType);
        redisUtil.hset(fcFactory.CAMERA_CACHE_NAME + cal.getEpId(), "cameraType", cameraType);
    }

    public void setStateRedis(String state, FogCalculator cal) {
        cal.getEquipment().setState(state);
        redisUtil.hset(fcFactory.CAMERA_CACHE_NAME + cal.getEpId(), "equipment", cal.getEquipment());
    }

    public void setSysNameRedis(String sysName, FogCalculator cal) {
        cal.setSysName(sysName);
        redisUtil.hset(fcFactory.CAMERA_CACHE_NAME + cal.getEpId(), "sysName", sysName);
    }

    public void setConfirmDistanceRedis(int confirmDistance, FogCalculator cal) {
        cal.setConfirmDistance(confirmDistance);
        redisUtil.hset(fcFactory.CAMERA_CACHE_NAME + cal.getEpId(), "confirmDistance", confirmDistance);
    }

    public void setConfirmDateRedis(Date confirmDate, FogCalculator cal) {
        cal.setConfirmDate(confirmDate);
        redisUtil.hset(fcFactory.CAMERA_CACHE_NAME + cal.getEpId(), "confirmDate", confirmDate);
    }

    public AlarmRoadModel getAlarmRoadRedis(FogCalculator cal) {
        Map<String, Object> roadMap = redisUtil.hmget(fcFactory.ALARM_ROAD_CACHE_NAME + cal.getEquipment().getHwId());
        return BeanUtil.mapToBean(roadMap, AlarmRoadModel.class, false);
    }

    public void setAlarmRoadRedis(AlarmRoadModel alarmRoad, FogCalculator cal) {
        Map<String, Object> roadMap = BeanUtil.beanToMap(alarmRoad);
        redisUtil.hmset(fcFactory.ALARM_ROAD_CACHE_NAME + cal.getEquipment().getHwId(), roadMap);
    }

    public void setUpdateTimeRedis(Date updateTime, FogCalculator cal) {
        cal.setUpdateTime(updateTime);
        redisUtil.hset(fcFactory.CAMERA_CACHE_NAME + cal.getEpId(), "updateTime", updateTime);
    }

    public void setEquipmentRedis(EquipmentModel equipment){
        redisUtil.hset(fcFactory.CAMERA_CACHE_NAME + equipment.getId(), "equipment", equipment);
    }



    /**
     * actype:1 生成雾  2 雾等级提高 4雾消散 5:100米提醒 -1异常 -2异常解除
     *      isManual :true:人工确认操作  false：系统操作
     *      isEfct : 人工确认
     *     state:异常类型
     * @param actype
     * @param isManual
     * @param state
     * @param cal
     */
    public void fogOption(int actype, boolean isManual, String state, FogCalculator cal) {
        switch (actype) {
		    //1 生成雾
            case 1:
                if (isManual) {
				/*if(isEfct) {
					 cameraType = 2;*/
                    this.setCameraTypeRedis(2, cal);
                    /*fcFactory.fogNowMap.put(cal.getEpId(), this);  */
                    redisUtil.sSet(fcFactory.ALARM_EPIDS_CACHE_NAME, cal.getEpId());
                    if (StringUtils.isEmpty(cal.alarmId)) {
                        this.setAlarmIdRedis(StringUtils.getUUID(), cal);
                    }
                    redisUtil.setRemove(fcFactory.UNCONFIRM_ALARM_EPIDS_CACHE_NAME, cal.getEpId());
				/*} else {
					cal.alarmId = "";
					cameraType = 0;
					fcFactory.fogNowMap.remove(cal.getEpId());
				}*/
                } else {
                    this.setAlarmIdRedis(StringUtils.getUUID(), cal);
                    this.setCameraTypeRedis(1, cal);
                    redisUtil.sSet(fcFactory.UNCONFIRM_ALARM_EPIDS_CACHE_NAME, cal.getEpId());
//					 cameraType = 1;
                }
                break;
//		2告警等级提高
//		case 2:
//			cameraType = 3;
//			break;
//		4告警解除
            case 4:
                if (isManual) {
//				if(isEfct) {
                    this.setAlarmIdRedis("", cal);
                    redisUtil.setRemove(fcFactory.ALARM_EPIDS_CACHE_NAME, cal.getEpId());
                    redisUtil.setRemove(fcFactory.UNCONFIRM_ALARM_EPIDS_CACHE_NAME, cal.getEpId());
//					 cameraType = 0;
                    this.setCameraTypeRedis(0, cal);
//				} else {
//					cameraType = 2;
//				}
                } else {
//					 cameraType = 5;
                    this.setCameraTypeRedis(5, cal);
                    redisUtil.sSet(fcFactory.UNCONFIRM_ALARM_EPIDS_CACHE_NAME, cal.getEpId());
                }
                break;
            //100米提醒
            case 5:
//				 cameraType = 7;
                this.setCameraTypeRedis(7, cal);
                break;
            case -1:
//				 cal.cameraType = -1;
                this.setCameraTypeRedis(-1, cal);
                cal.alarmId = "";
                redisUtil.setRemove(fcFactory.ALARM_EPIDS_CACHE_NAME, cal.getEpId());
                redisUtil.setRemove(fcFactory.UNCONFIRM_ALARM_EPIDS_CACHE_NAME, cal.getEpId());
                this.setStateRedis(state, cal);
                break;
            case -2:
//				 cal.cameraType = 0;
                this.setCameraTypeRedis(0, cal);
                this.setStateRedis("0", cal);
                break;

            default:
                break;
        }

    }


//	public boolean isAlarm() {
//		return ("0".equals(cal.getIsconfirm())&&"0".equals(cal.getState()));
//	}

    /**
     * 计算平均能见度
     * @param minute
     * @param fMeter
     * @return
     */
    public int getAverage(int minute, int fMeter, FogCalculator cal) {
        List<Object> lastDistList = redisUtil.lGet(fcFactory.LATE_DISTANCE_CACHE_NAME + cal.getEpId(), 0, -1);
        int sum = 0;
        int i = 0;
        if (lastDistList == null || lastDistList.size() == 0) {
            return 0;
        }
        for (Object o : lastDistList) {
            if (i < minute) {
                sum += Integer.parseInt((String) o);
                i++;
            } else {
                break;
            }
        }
        if (i == 0) {
            return fMeter;
        }
        return (sum) / (i);
    }

    /**
     * 计算能见度趋势
     * @param minute
     * @return
     */
    public int getTrend(int minute, FogCalculator cal) {
        List<Object> lastDistList = redisUtil.lGet(fcFactory.LATE_DISTANCE_CACHE_NAME + cal.getEpId(), 0, -1);
        int i = 0;
        if (lastDistList == null || lastDistList.size() == 0) {
            return 0;
        }
        int max = 0;
        int min = 500;
        for (Object o : lastDistList) {
            if (i < minute) {
                int dist = Integer.parseInt((String) o);
                if(dist > max){
                    max = dist;
                }
                if(dist < min){
                    min = dist;
                }
                i++;
            } else {
                break;
            }
        }
        return (max - min) / i;
    }


    public FogCalculator getPrevious(FogCalculator cal) {
        long index = redisUtil.zSetRank(fcFactory.CAMERAS_CACHE_NAME + cal.getEquipment().getHwId(), cal.getEpId());
        if (index == 0) {
            return null;
        }
        String zSetEpId = (String) redisUtil.zSetGetByIndex(fcFactory.CAMERAS_CACHE_NAME + cal.getEquipment().getHwId(), index - 1);

        return fcFactory.getCalculator(zSetEpId);
    }


    public FogCalculator getNext(FogCalculator cal) {
        long index = redisUtil.zSetRank(fcFactory.CAMERAS_CACHE_NAME + cal.getEquipment().getHwId(), cal.getEpId());
        String zSetEpId = (String) redisUtil.zSetGetByIndex(fcFactory.CAMERAS_CACHE_NAME + cal.getEquipment().getHwId(), index + 1);
        if(StringUtils.isEmpty(zSetEpId)){
            return null;
        }
        return fcFactory.getCalculator(zSetEpId);
    }

    public void extracted(FogCalculator cal) {
        Integer distance = cal.getDistance();
        Map<String, Object> map = new HashMap<>();
        map.put("sysName", cal.getSysName());
        map.put("distance", distance);
        map.put("imgtime", cal.getImgtime());
        map.put("imgpath", cal.getImgpath());
        map.put("confirmDistance", cal.getConfirmDistance());
        redisUtil.hmset(fcFactory.CAMERA_CACHE_NAME + cal.getEpId(), map);

        long listSize = redisUtil.lGetListSize(fcFactory.LATE_DISTANCE_CACHE_NAME + cal.getEpId());
        if (listSize >= 5) {
            redisUtil.lrightPop(fcFactory.LATE_DISTANCE_CACHE_NAME + cal.getEpId());
        }
        redisUtil.lLeftPush(fcFactory.LATE_DISTANCE_CACHE_NAME + cal.getEpId(), String.valueOf(distance));
    }

    public void extractedNotice(FogCalculator cal) {
        Map<String, Object> map = new HashMap<>();
        map.put("updateTime", cal.getUpdateTime());
        map.put("alarmImgpath", cal.getAlarmImgpath());
        map.put("alarmImgtime", cal.getAlarmImgtime());
        map.put("alarmDistance", cal.getAlarmDistance());
        map.put("alarmLevel", cal.getAlarmLevel());
        map.put("noticeId", cal.getNoticeId());
        redisUtil.hmset(fcFactory.CAMERA_CACHE_NAME + cal.getEpId(), map);
//        setUpdateTimeRedis(new Date(), cal);
//        setAlarmImgpathRedis(cal.getImgpath(), cal);
//        setAlarmImgtimeRedis(cal.getImgtime(), cal);
//        setAlarmDistanceRedis(cal.getDistance(), cal);
//        setAlarmLevelRedis(cal.getAlarmLevel(), cal);
//        setNoticeIdRedis(cal.getNoticeId(), cal);
    }

}
