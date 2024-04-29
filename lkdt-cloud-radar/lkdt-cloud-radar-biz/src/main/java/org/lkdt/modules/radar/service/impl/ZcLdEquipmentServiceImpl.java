package org.lkdt.modules.radar.service.impl;

import org.lkdt.modules.radar.entity.ZcLdEquipment;
import org.lkdt.modules.radar.entity.ZcLdLaneInfo;
import org.lkdt.modules.radar.mapper.ZcLdLaneInfoMapper;
import org.lkdt.modules.radar.mapper.ZcLdEquipmentMapper;
import org.lkdt.modules.radar.service.IZcLdEquipmentService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;

/**
 * @Description: 雷达设备表
 * @Author: jeecg-boot
 * @Date:   2021-03-11
 * @Version: V1.0
 */
@Service
public class ZcLdEquipmentServiceImpl extends ServiceImpl<ZcLdEquipmentMapper, ZcLdEquipment> implements IZcLdEquipmentService {

	@Autowired
	private ZcLdEquipmentMapper zcLdEquipmentMapper;
	@Autowired
	private ZcLdLaneInfoMapper zcLdLaneInfoMapper;
	
	@Override
	@Transactional
	public void delMain(String id) {
		zcLdLaneInfoMapper.deleteByMainId(id);
		zcLdEquipmentMapper.deleteById(id);
	}

	@Override
	@Transactional
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			zcLdLaneInfoMapper.deleteByMainId(id.toString());
			zcLdEquipmentMapper.deleteById(id);
		}
	}

	@Override
	public List<ZcLdEquipment> queryEquipment() {
		return zcLdEquipmentMapper.queryEquipment();
	}
	
}
