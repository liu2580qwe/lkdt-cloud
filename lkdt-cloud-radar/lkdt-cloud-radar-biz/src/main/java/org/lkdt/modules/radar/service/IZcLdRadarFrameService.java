package org.lkdt.modules.radar.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lkdt.modules.radar.entity.ZcLdRadarFrame;

/**
 * @Author: jeecg-boot
 * @Date:   2021-03-11
 * @Version: V1.0
 */
public interface IZcLdRadarFrameService extends IService<ZcLdRadarFrame> {

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
