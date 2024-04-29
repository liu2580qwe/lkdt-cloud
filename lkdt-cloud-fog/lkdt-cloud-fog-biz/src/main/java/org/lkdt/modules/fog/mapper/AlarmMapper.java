package org.lkdt.modules.fog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.lkdt.modules.fog.entity.Alarm;
import org.lkdt.modules.fog.vo.AlarmCountVo;
import org.lkdt.modules.fog.vo.ChartData;
import org.lkdt.modules.wind.domain.AlarmDO;

import java.util.List;
import java.util.Map;

/**
 * @Description: alarm table
 * @Author: Cai Xibei
 * @Date:   2021-04-27
 * @Version: V1.0
 */
public interface AlarmMapper extends BaseMapper<Alarm> {

	public boolean deleteByMainId(@Param("mainId") String mainId);
	public List<Alarm> selectByAlarmRoadIdAndEndTime(@Param("mainId") String mainId);

	public List<Alarm> selectByMainId(@Param("mainId") String mainId);

	public Alarm selectById(@Param("id") String id);

	public List<Alarm> queryAlarmCalenderData(@Param("hwid") String hw_id, @Param("start")String start, @Param("end")String end);

	public List<Alarm> queryDayAlarm(@Param("start")String start,@Param("level")String level);

	List<AlarmCountVo> getAlarmCountGroupEpId(Map<String, Object> map);

	List<Map<String, Object>> queryRoadAlarmByYear(Map<String, Object> map);
	List<AlarmDO> selectInfoByCondition(Map<String,Object> map);
	Integer queryListToday(Map<String, Object> map);
	Integer queryListMonth(Map<String, Object> map);
	List<AlarmDO> queryEquAlarm(Map<String, Object> map);
	List<ChartData> queryAlarmByroadAlarmType();

	List<Alarm> selectInfoByEpId(Map<String,Object> map);

}
