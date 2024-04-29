package org.lkdt.modules.radar.service.impl;

import org.lkdt.modules.radar.entity.ZcLdLaneInfo;
import org.lkdt.modules.radar.mapper.ZcLdLaneInfoMapper;
import org.lkdt.modules.radar.service.IZcLdLaneInfoService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 雷达车道与道路车道关系表
 * @Author: jeecg-boot
 * @Date:   2021-03-11
 * @Version: V1.0
 */
@Service
public class ZcLdLaneInfoServiceImpl extends ServiceImpl<ZcLdLaneInfoMapper, ZcLdLaneInfo> implements IZcLdLaneInfoService {
	
	@Autowired
	private ZcLdLaneInfoMapper zcLdLaneInfoMapper;
	
	@Override
	public List<ZcLdLaneInfo> selectByMainId(String mainId) {
		return zcLdLaneInfoMapper.selectByMainId(mainId);
	}

	@Override
	public List<ZcLdLaneInfo> queryByMainId(String mainId) {
		return zcLdLaneInfoMapper.queryByMainId(mainId);
	}
}
