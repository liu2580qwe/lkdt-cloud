package org.lkdt.modules.radar.service.impl;
import java.util.List;

import org.lkdt.modules.radar.entity.ZcLdEquipment;
import org.lkdt.modules.radar.mapper.ZcLdRadarEquipmentMapper;
import org.lkdt.modules.radar.service.IZcLdRadarEquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
/**
 * @Description: 雷达设备表
 * @Author: jeecg-boot
 * @Date:   2021-07-23
 * @Version: V1.0
 */
@Service
public class ZcLdRadarEquipmentServiceImpl extends ServiceImpl<ZcLdRadarEquipmentMapper, ZcLdEquipment> implements IZcLdRadarEquipmentService {
	
	@Autowired
	private ZcLdRadarEquipmentMapper zcLdRadarEquipmentMapper;
	
	@Override
	public List<ZcLdEquipment> selectByMainId(String mainId) {
		return zcLdRadarEquipmentMapper.selectByMainId(mainId);
	}

}
