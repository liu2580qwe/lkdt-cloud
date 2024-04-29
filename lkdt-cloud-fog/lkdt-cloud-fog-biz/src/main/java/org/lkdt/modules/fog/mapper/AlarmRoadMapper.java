package org.lkdt.modules.fog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.lkdt.modules.fog.entity.AlarmRoad;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: 告警道路表
 * @Author: jeecg-boot
 * @Date: 2021-04-27
 * @Version: V1.0
 */
public interface AlarmRoadMapper extends BaseMapper<AlarmRoad> {

    Integer queryMonthCount();

    Integer queryBindCount();

    List<AlarmRoad> queryCalendarAlarm(Map<String, Object> map);

    List<AlarmRoad> queryYearRoadReport(Map<String, Object> map);

    List<AlarmRoad> queryYearLevelReport(Map<String, Object> map);

    List<AlarmRoad> queryMonthRoadReport(Map<String, Object> map);

    List<AlarmRoad> queryMonthLevelReport(Map<String, Object> map);

    List<AlarmRoad> queryDayRoadReport(Map<String, Object> map);

    List<AlarmRoad> queryDayLevelReport(Map<String, Object> map);

    List<AlarmRoad> queryTheNumberOfAnnualAlarms(Map<String,Object> map);

    List<AlarmRoad> queryListByDay(Map<String, Object> paramsMap);

    List<AlarmRoad> queryMongoListByDay(Map<String, Object> paramsMap);
    public AlarmRoad queryByHwId(String hwId);
}
