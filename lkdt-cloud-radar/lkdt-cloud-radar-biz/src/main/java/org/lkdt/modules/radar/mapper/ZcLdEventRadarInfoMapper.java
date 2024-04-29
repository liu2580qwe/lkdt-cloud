package org.lkdt.modules.radar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.lkdt.modules.radar.entity.ZcLdEventRadarInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description: 事件雷达数据表
 * @Author: jeecg-boot
 * @Date:   2021-03-23
 * @Version: V1.0
 */
public interface ZcLdEventRadarInfoMapper extends BaseMapper<ZcLdEventRadarInfo> {

	
	/**
	 * 车型占比
	 * @param equId
	 * @return
	 */
	List<Map<String,Object>> queryCarTypeRatio(@Param("equId") String equId,@Param("beginTime") String beginTime ,@Param("endTime") String endTime);
	
	
	/**
	 * 按小时统计N小时前至今的车流量和平均时速
	 * @param map
	 * @return
	 */
	List<Map<String,Object>> queryFlowAndSpeed(@Param("equId") String equId,@Param("hours") int hours);
	
	/**
	 * 按车道统计
	 * @param equId
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	List<Map<String,Object>> querylaneStatistics(@Param("equId") String equId,@Param("beginTime") String beginTime ,@Param("endTime") String endTime);
	
	/**
	 * 按分钟统计车流量和平均时速
	 * @param equId
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	List<Map<String,Object>> queryFlowAndSpeedGroupByMinutes(@Param("equId") String equId,@Param("beginTime") String beginTime ,@Param("endTime") String endTime);
	
	/**
	 * 按秒统计车流量和平均时速
	 * @param equId
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	List<Map<String,Object>> queryFlowAndSpeedGroupBySecond(@Param("equId") String equId,@Param("beginTime") String beginTime ,@Param("endTime") String endTime);
	
	
	/**
	 * 统计（单元路段）平均车速，区分来、去向
	 * @param equId
	 * @param dateTime
	 * @return
	 */
	List<Map<String,Object>> queryUnitAvgSpeed(@Param("equId") String equId,@Param("dateTime") String dateTime,@Param("tjDateType") String tjDateType);
	
	
	/**
	 * 统计（各车道）平均车速
	 * @param equId
	 * @param dateTime
	 * @return
	 */
	List<Map<String,Object>> queryLaneAvgSpeed(@Param("equId") String equId,@Param("dateTime") String dateTime,@Param("tjDateType") String tjDateType);
	
	/**
	 * （单元路段）速度离散度
	 * @param equId
	 * @param dateTime
	 * @return
	 */
	List<Map<String,Object>> queryUnitSpeedStddev(@Param("equId") String equId,@Param("dateTime") String dateTime,@Param("tjDateType") String tjDateType);
	
	/**
	 * （各车道）速度离散度
	 * @param equId
	 * @param dateTime
	 * @return
	 */
	List<Map<String,Object>> queryLaneSpeedStddev(@Param("equId") String equId,@Param("dateTime") String dateTime,@Param("tjDateType") String tjDateType);
	
	List<Map<String,Object>> queryRadarNewDate();

	/**数车*/
	List<Map<String,Object>> queryCountNow(@Param("equId") String equId);

	
	/**
	 * 统计车流量和平均时速
	 * @param equId
	 * @param dateTime
	 * @return
	 */
	List<Map<String,Object>> queryFlowAndAvgSpeed(@Param("equId") String equId,@Param("dateTime") String dateTime,@Param("tjDateType") String tjDateType);
	
	
	/**
	 * 查询车辆数据包
	 * @param equId
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	List<ZcLdEventRadarInfo> queryRadarInfoPathTrace(@Param("equId") String equId,@Param("beginTime") String beginTime ,@Param("endTime") String endTime ,@Param("direction") String direction);
	
}
