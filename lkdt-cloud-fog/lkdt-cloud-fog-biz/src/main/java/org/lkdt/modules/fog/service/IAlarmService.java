package org.lkdt.modules.fog.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.lkdt.modules.fog.entity.Alarm;
import org.lkdt.modules.fog.entity.AlarmLog;
import org.lkdt.modules.fog.vo.AlarmCountVo;
import org.lkdt.modules.fog.vo.ChartData;
import org.lkdt.modules.wind.domain.AlarmDO;
import org.lkdt.modules.wind.entity.WindLog;

import java.util.List;
import java.util.Map;
/**
 * @Description: 告警表
 * @Author: jeecg-boot
 * @Date:   2021-04-27
 * @Version: V1.0
 */
public interface IAlarmService extends IService<Alarm> {

	public List<Alarm> selectByMainId(String mainId);
	public List<Alarm> selectByAlarmRoadIdAndEndTime(String mainId);
	/**
	 * 报表-告警摄像头
	 * @param map
	 * @return
	 */
	List<AlarmDO> queryEquAlarm(Map<String, Object> map);
	/**
	 * 查询道路告警信息
	 * @return
	 */
	List<Alarm> queryAlarmCalenderData(String hwid, String start, String end);

	List<Alarm> queryDayAlarm(String start, String level);
	Alarm selectById(String id);

    List<AlarmLog> getFileList(String beginTime, String endTime, String epId);

	List<WindLog> getWindFileList(String beginTime, String endTime, String windId);

	String getDistanceStatistic(String startDate, String endDate, String date, String epId);

	String getDistanceStatistic(String date, String epId);
	/**
	 * 根据告警摄像头ID统计指定时间段的可视距离
	 * @param beginTime
	 * @param endTime
	 * @param epId
	 * @return
	 */
	String alarmDistanceStatistic(String beginTime,String endTime, String epId);

	List<AlarmCountVo> getAlarmCountGroupEpId(Map<String, Object> map);

	List<Map<String, Object>> queryRoadAlarmByYear(Map<String, Object> map);

	Integer queryListToday(Map<String, Object> map);
	Integer queryListMonth(Map<String, Object> map);
	List<AlarmDO> selectInfoByCondition(Map<String, Object> map);
	List<ChartData> queryAlarmByroadAlarmType();

	List<Alarm> selectInfoByEpId(Map<String, Object> map);

    /*List<AlarmDO> queryEquAlarm(Map<String, Object> map);*/
	/**
	 * 根据大风告警点ID统计指定时间段的风信息
	 * @param beginTime
	 * @param endTime
	 * @param epId
	 * @return
	 */
	String alarmDistanceStatisticWind(String beginTime,String endTime, String epId);

	/**
	 * 根据时间和道路获取能见度报表
	 * @param beginTime
	 * @param endTime
	 * @param hwId
	 * @return
	 */
	JSONObject getVisibilityReport(String beginTime, String endTime, String hwId);
}
