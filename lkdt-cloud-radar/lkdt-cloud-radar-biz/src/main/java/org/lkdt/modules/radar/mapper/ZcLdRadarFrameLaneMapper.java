package org.lkdt.modules.radar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.lkdt.modules.radar.entity.ZcLdRadarFrameLane;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 *
 */
public interface ZcLdRadarFrameLaneMapper extends BaseMapper<ZcLdRadarFrameLane> {

    int insertBatch(List<ZcLdRadarFrameLane> zcLdRadarFrameLaneList);

    
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
