package org.lkdt.modules.radar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.lkdt.modules.radar.entity.ZcLdRadarFrame;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 */
public interface ZcLdRadarFrameMapper extends BaseMapper<ZcLdRadarFrame> {

	int insertBatch(List<ZcLdRadarFrame> zcLdRadarFrames);

	/**
	 * 统计（单元路段）大车占比度，区分来、去向和车型
	 * @param equId
	 * @param dateTime
	 * @return
	 */
	List<Map<String,Object>> queryUnitRoadCarTypeByDirection(@Param("equId") String equId,@Param("dateTime") String dateTime,@Param("tjDateType") String tjDateType);
	
	
	/**
	 * 统计（单元路段）平均时距
	 * @param equId
	 * @param dateTime
	 * @return
	 */
	List<Map<String,Object>> queryUnitAvgTimeDistance(@Param("equId") String equId,@Param("dateTime") String dateTime,@Param("tjDateType") String tjDateType);
	
	
	
}
