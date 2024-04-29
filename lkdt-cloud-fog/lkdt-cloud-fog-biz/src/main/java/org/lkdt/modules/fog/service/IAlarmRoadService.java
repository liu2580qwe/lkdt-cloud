package org.lkdt.modules.fog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lkdt.modules.fog.calculator.FogCalculator;
import org.lkdt.modules.fog.entity.Alarm;
import org.lkdt.modules.fog.entity.AlarmRoad;
import org.lkdt.modules.fog.entity.AlarmRoadModel;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Description: 告警道路表
 * @Author: jeecg-boot
 * @Date: 2021-04-27
 * @Version: V1.0
 */
public interface IAlarmRoadService extends IService<AlarmRoad> {

    /**
     * 添加一对多
     */
    public void saveMain(AlarmRoad alarmRoad, List<Alarm> alarmList);

    /**
     * 修改一对多
     */
    public void updateMain(AlarmRoad alarmRoad, List<Alarm> alarmList);

    /**
     * 删除一对多
     */
    public void delMain(String id);

    /**
     * 批量删除一对多
     */
    public void delBatchMain(Collection<? extends Serializable> idList);


    boolean alarmRoad(Alarm alarm, boolean sendSms, String phones, String openid, String type);

    void alarmRoadCall(AlarmRoadModel alarmRoad, FogCalculator cal, String openId);

    String checkAlarmRoad(Alarm alarm);

    List<AlarmRoad> queryCalendarAlarm(Map<String, Object> map);

    List<AlarmRoad> queryTheNumberOfAnnualAlarms(Map<String,Object> map);

    Integer queryBindCount();

    Integer queryMonthCount();

    List<AlarmRoad> queryYearLevelReport(Map<String, Object> paramsMap);

    List<AlarmRoad> queryYearRoadReport(Map<String, Object> paramsMap);

    List<AlarmRoad> queryMonthRoadReport(Map<String, Object> paramsMap);

    List<AlarmRoad> queryMonthLevelReport(Map<String, Object> paramsMap);

    List<AlarmRoad> queryDayRoadReport(Map<String, Object> paramsMap);

    List<AlarmRoad> queryDayLevelReport(Map<String, Object> paramsMap);

    List<AlarmRoad> queryListByDay(Map<String, Object> paramsMap);

    List<AlarmRoad> queryMongoListByDay(Map<String, Object> paramsMap);

    AlarmRoad queryByHwId(String hwId);
}
