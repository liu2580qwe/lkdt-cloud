package org.lkdt.modules.radar.service;

import org.lkdt.modules.radar.entity.ZcLdEventRadarInfo;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 事件雷达数据表
 * @Author: jeecg-boot
 * @Date:   2021-03-23
 * @Version: V1.0
 */
public interface IZcLdEventRadarInfoService extends IService<ZcLdEventRadarInfo> {

	/**
	 * 车型占比
	 * @param equId
	 * @return
	 */
	List<Map<String,Object>> queryCarTypeRatio(String equId,String beginTime,String endTime);
	
	/**
	 * 按小时统计N小时前至今的车流量和平均时速
	 * @param map
	 * @return
	 */
	List<Map<String,Object>> queryFlowAndSpeed(String equId,int hours);
	
	/**
	 * 按车道统计
	 * @param equId
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	List<Map<String,Object>> querylaneStatistics(String equId,String beginTime,String endTime);
	
	/**
	 * 按分钟统计车流量和平均时速
	 * @param equId
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	List<Map<String,Object>> queryFlowAndSpeedGroupByMinutes(String equId,String beginTime,String endTime);
	
	/**
	 * 按秒统计车流量和平均时速
	 * @param equId
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	List<Map<String,Object>> queryFlowAndSpeedGroupBySecond(String equId,String beginTime,String endTime);
	
	
	/**
	 * 统计（单元路段）平均车速，区分来、去向
	 * @param equId
	 * @param dateTime
	 * @return
	 */
	List<Map<String,Object>> queryUnitAvgSpeed(String equId,String dateTime,String tjDateType);
	
	/**
	 * 统计（各车道）平均车速
	 * @param equId
	 * @param dateTime
	 * @return
	 */
	List<Map<String,Object>> queryLaneAvgSpeed(String equId,String dateTime, String tjDateType);
	
	/**
	 * （单元路段）速度离散度
	 * @param equId
	 * @param dateTime
	 * @return
	 */
	List<Map<String,Object>> queryUnitSpeedStddev( String equId, String dateTime, String tjDateType);
	
	/**
	 * （各车道）速度离散度
	 * @param equId
	 * @param dateTime
	 * @return
	 */
	List<Map<String,Object>> queryLaneSpeedStddev( String equId, String dateTime, String tjDateType);
	
	List<Map<String,Object>> queryRadarNewDate();
	
	/**
	 * 统计车流量和平均时速
	 * @param equId
	 * @param dateTime
	 * @return
	 */
	List<Map<String,Object>> queryFlowAndAvgSpeed( String equId, String dateTime, String tjDateType);
	
	/**
	 * 查询车辆数据包
	 * @param equId
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	List<ZcLdEventRadarInfo> queryRadarInfoPathTrace(String equId,String beginTime,String endTime,String direction);
	
	
}
