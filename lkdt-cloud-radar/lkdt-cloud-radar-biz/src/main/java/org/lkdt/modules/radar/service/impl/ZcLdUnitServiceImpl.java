package org.lkdt.modules.radar.service.impl;
import org.lkdt.modules.radar.entity.ZcLdEquipment;
import org.lkdt.modules.radar.entity.ZcLdUnit;
import org.lkdt.modules.radar.mapper.ZcLdRadarEquipmentMapper;
import org.lkdt.modules.radar.mapper.ZcLdUnitMapper;
import org.lkdt.modules.radar.service.IZcLdUnitService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;
/**
 * @Description: 雷达单元表
 * @Author: jeecg-boot
 * @Date:   2021-07-23
 * @Version: V1.0
 */
@Service
public class ZcLdUnitServiceImpl extends ServiceImpl<ZcLdUnitMapper, ZcLdUnit> implements IZcLdUnitService {

	@Autowired
	private ZcLdUnitMapper zcLdUnitMapper;

	@Autowired
	private ZcLdRadarEquipmentMapper zcLdRadarEquipmentMapper;

	@Override
	public void saveMain(ZcLdUnit zcLdUnit, List<ZcLdEquipment> zcLdRadarEquipmentList) {
		zcLdUnitMapper.insert(zcLdUnit);
	}

	@Override
	public void updateMain(ZcLdUnit zcLdUnit,List<ZcLdEquipment> zcLdRadarEquipmentList) {
		zcLdUnitMapper.updateById(zcLdUnit);
	}

	@Override
	public void delMain(String id) {
		zcLdUnitMapper.deleteById(id);
	}

	@Override
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			zcLdUnitMapper.deleteById(id);
		}
	}
	@Override
	public List<String> selectIdsLikeId(String id){
		return zcLdUnitMapper.selectIdsLikeId(id);
	}

	@Override
	public ZcLdUnit selectLdUnitById(String id) {
		return zcLdUnitMapper.selectLdUnitById(id);
	}

	@Override
	public List<String> selectIdsByHwId(String hw_id) {
		return zcLdUnitMapper.selectIdsByHwId(hw_id);
	}


}
