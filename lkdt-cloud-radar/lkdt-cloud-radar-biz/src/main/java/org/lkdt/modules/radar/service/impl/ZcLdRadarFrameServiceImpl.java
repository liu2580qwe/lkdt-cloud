package org.lkdt.modules.radar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lkdt.modules.radar.entity.ZcLdRadarFrame;
import org.lkdt.modules.radar.mapper.ZcLdRadarFrameMapper;
import org.lkdt.modules.radar.service.IZcLdRadarFrameService;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: jeecg-boot
 * @Date:   2021-03-11
 * @Version: V1.0
 */
@Service
public class ZcLdRadarFrameServiceImpl extends ServiceImpl<ZcLdRadarFrameMapper, ZcLdRadarFrame> implements IZcLdRadarFrameService {

	@Autowired
	private ZcLdRadarFrameMapper zcLdRadarFrameMapper ;
	
	
	@Override
	public List<Map<String, Object>> queryUnitRoadCarTypeByDirection(String equId, String dateTime, String tjDateType) {
		return zcLdRadarFrameMapper.queryUnitRoadCarTypeByDirection(equId,dateTime,tjDateType);
	}


	@Override
	public List<Map<String, Object>> queryUnitAvgTimeDistance(String equId, String dateTime, String tjDateType) {
		return zcLdRadarFrameMapper.queryUnitAvgTimeDistance(equId,dateTime,tjDateType);
	}


	
	
	

	
	

}
