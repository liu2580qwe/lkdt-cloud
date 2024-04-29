package org.lkdt.modules.radar.service.impl;

import org.lkdt.modules.radar.entity.ZcLdEventRadarInfo;
import org.lkdt.modules.radar.mapper.ZcLdEventRadarInfoMapper;
import org.lkdt.modules.radar.service.IZcLdEventRadarInfoService;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 事件雷达数据表
 * @Author: jeecg-boot
 * @Date:   2021-03-23
 * @Version: V1.0
 */
@Service
public class ZcLdEventRadarInfoServiceImpl extends ServiceImpl<ZcLdEventRadarInfoMapper, ZcLdEventRadarInfo> implements IZcLdEventRadarInfoService {

	@Autowired
	private ZcLdEventRadarInfoMapper zcLdEventRadarInfoMapper ;
	
	@Override
	public List<Map<String, Object>> queryCarTypeRatio(String equId,String beginTime,String endTime) {
		
		return zcLdEventRadarInfoMapper.queryCarTypeRatio(equId ,beginTime,endTime);
	}

	@Override
	public List<Map<String, Object>> queryFlowAndSpeed(String equId,int hours) {
		return zcLdEventRadarInfoMapper.queryFlowAndSpeed(equId,hours);
	}

	@Override
	public List<Map<String, Object>> querylaneStatistics(String equId, String beginTime, String endTime) {
		return zcLdEventRadarInfoMapper.querylaneStatistics(equId ,beginTime,endTime);
	}

	@Override
	public List<Map<String, Object>> queryFlowAndSpeedGroupByMinutes(String equId, String beginTime, String endTime) {
		return zcLdEventRadarInfoMapper.queryFlowAndSpeedGroupByMinutes(equId ,beginTime,endTime);
	}

	@Override
	public List<Map<String, Object>> queryFlowAndSpeedGroupBySecond(String equId, String beginTime, String endTime) {
		return zcLdEventRadarInfoMapper.queryFlowAndSpeedGroupBySecond(equId ,beginTime,endTime);
	}
	
	@Override
	public List<Map<String, Object>> queryUnitAvgSpeed(String equId, String dateTime, String tjDateType) {
		return zcLdEventRadarInfoMapper.queryUnitAvgSpeed(equId,dateTime,tjDateType);
	}

	@Override
	public List<Map<String, Object>> queryLaneAvgSpeed(String equId, String dateTime, String tjDateType) {
		return zcLdEventRadarInfoMapper.queryLaneAvgSpeed(equId,dateTime,tjDateType);
	}

	@Override
	public List<Map<String, Object>> queryUnitSpeedStddev(String equId, String dateTime, String tjDateType) {
		return zcLdEventRadarInfoMapper.queryUnitSpeedStddev(equId,dateTime,tjDateType);
	}

	@Override
	public List<Map<String, Object>> queryLaneSpeedStddev(String equId, String dateTime, String tjDateType) {
		return zcLdEventRadarInfoMapper.queryLaneSpeedStddev(equId,dateTime,tjDateType);
	}

	@Override
	public List<Map<String, Object>> queryRadarNewDate() {
		return zcLdEventRadarInfoMapper.queryRadarNewDate();
	}

	@Override
	public List<Map<String, Object>> queryFlowAndAvgSpeed(String equId, String dateTime, String tjDateType) {
		return zcLdEventRadarInfoMapper.queryFlowAndAvgSpeed(equId,dateTime,tjDateType);
	}

	@Override
	public List<ZcLdEventRadarInfo> queryRadarInfoPathTrace(String equId, String beginTime, String endTime, String direction) {
		return zcLdEventRadarInfoMapper.queryRadarInfoPathTrace(equId,beginTime,endTime,direction);
	}

}
