package org.lkdt.modules.radar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lkdt.modules.radar.entity.ZcLdRadarFrameLane;
import org.lkdt.modules.radar.mapper.ZcLdRadarFrameLaneMapper;
import org.lkdt.modules.radar.service.IZcLdRadarFrameLaneService;

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
public class ZcLdRadarFrameLaneServiceImpl extends ServiceImpl<ZcLdRadarFrameLaneMapper, ZcLdRadarFrameLane> implements IZcLdRadarFrameLaneService {

	@Autowired
	private ZcLdRadarFrameLaneMapper zcLdRadarFrameLaneMapper;
	
	@Override
	public List<Map<String, Object>> queryLaneRoadCarType(String equId, String dateTime, String tjDateType) {
		return zcLdRadarFrameLaneMapper.queryLaneRoadCarType(equId, dateTime, tjDateType);
	}

	@Override
	public List<Map<String, Object>> queryLaneRatioAndFlowDensity(String equId, String dateTime, String tjDateType) {
		return zcLdRadarFrameLaneMapper.queryLaneRatioAndFlowDensity(equId, dateTime, tjDateType);
	}

	@Override
	public List<Map<String, Object>> queryLaneAvgTimeDistance(String equId, String dateTime, String tjDateType) {
		return zcLdRadarFrameLaneMapper.queryLaneAvgTimeDistance(equId, dateTime, tjDateType);
	}



}
