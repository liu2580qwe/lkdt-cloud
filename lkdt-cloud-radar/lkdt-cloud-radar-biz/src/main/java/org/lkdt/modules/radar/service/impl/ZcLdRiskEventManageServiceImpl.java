package org.lkdt.modules.radar.service.impl;

import java.util.List;
import java.util.Map;

import org.lkdt.modules.radar.entity.ZcLdRiskEventManage;
import org.lkdt.modules.radar.mapper.ZcLdRiskEventManageMapper;
import org.lkdt.modules.radar.service.IZcLdRiskEventManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 雷达事件管理
 * @Author: jeecg-boot
 * @Date:   2021-08-27
 * @Version: V1.0
 */
@Service
public class ZcLdRiskEventManageServiceImpl extends ServiceImpl<ZcLdRiskEventManageMapper, ZcLdRiskEventManage> implements IZcLdRiskEventManageService {

	@Autowired
	private ZcLdRiskEventManageMapper riskEventManageMapper;
	
	@Override
	public List<ZcLdRiskEventManage> queryDailyStatistics(String beginTime, String endTime) {
		return riskEventManageMapper.queryDailyStatistics(beginTime, endTime);
	}

}
