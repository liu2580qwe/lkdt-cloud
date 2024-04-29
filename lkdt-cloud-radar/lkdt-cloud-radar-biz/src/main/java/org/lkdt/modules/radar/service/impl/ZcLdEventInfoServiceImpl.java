package org.lkdt.modules.radar.service.impl;

import org.lkdt.modules.radar.entity.ZcLdEventInfo;
import org.lkdt.modules.radar.mapper.ZcLdEventInfoMapper;
import org.lkdt.modules.radar.mapper.ZcLdEventRadarInfoMapper;
import org.lkdt.modules.radar.service.IZcLdEventInfoService;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 事件信息表
 * @Author: jeecg-boot
 * @Date:   2021-03-23
 * @Version: V1.0
 */
@Service
public class ZcLdEventInfoServiceImpl extends ServiceImpl<ZcLdEventInfoMapper, ZcLdEventInfo> implements IZcLdEventInfoService {

	@Autowired
	private ZcLdEventInfoMapper zcLdEventInfoMapper ;
	
	@Override
	public List<Map<String, Object>> queryEventRatio(String equId) {
		return zcLdEventInfoMapper.queryEventRatio(equId);
	}

}
