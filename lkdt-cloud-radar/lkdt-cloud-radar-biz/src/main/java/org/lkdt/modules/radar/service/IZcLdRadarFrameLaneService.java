package org.lkdt.modules.radar.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lkdt.modules.radar.entity.ZcLdRadarFrameLane;

/**
 * @Author: jeecg-boot
 * @Date:   2021-03-11
 * @Version: V1.0
 */
public interface IZcLdRadarFrameLaneService extends IService<ZcLdRadarFrameLane> {

	
	/**
	 * 统计（各车道）大小车占比度
	 * @param equId
	 * @param dateTime
	 * @return
	 */
	List<Map<String,Object>> queryLaneRoadCarType(@Param("equId") String equId,@Param("dateTime") String dateTime,@Param("tjDateType") String tjDateType);
	

	/**
	 * 统计（各车道）车道占有率和交通流密度
	 * @param equId
	 * @param dateTime
	 * @return
	 */
	List<Map<String,Object>> queryLaneRatioAndFlowDensity(@Param("equId") String equId,@Param("dateTime") String dateTime,@Param("tjDateType") String tjDateType);
	
	
	/**
	 * 统计（各车道）平均时距
	 * @param equId
	 * @param dateTime
	 * @return
	 */
	List<Map<String,Object>> queryLaneAvgTimeDistance(@Param("equId") String equId,@Param("dateTime") String dateTime,@Param("tjDateType") String tjDateType);
	
	
	
}
