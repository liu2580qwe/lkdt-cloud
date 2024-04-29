package org.lkdt.modules.radar.service;
import com.baomidou.mybatisplus.extension.service.IService;
import org.lkdt.modules.radar.entity.ZcLdEquipment;
import org.lkdt.modules.radar.entity.ZcLdUnit;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
/**
 * @Description: 雷达单元表
 * @Author: jeecg-boot
 * @Date:   2021-07-23
 * @Version: V1.0
 */
public interface IZcLdUnitService extends IService<ZcLdUnit> {

	/**
	 * 添加一对多
	 * 
	 */
	public void saveMain(ZcLdUnit zcLdUnit,List<ZcLdEquipment> zcLdRadarEquipmentList) ;
	
	/**
	 * 修改一对多
	 * 
	 */
	public void updateMain(ZcLdUnit zcLdUnit,List<ZcLdEquipment> zcLdRadarEquipmentList);
	
	/**
	 * 删除一对多
	 */
	public void delMain (String id);
	
	/**
	 * 批量删除一对多
	 */
	public void delBatchMain (Collection<? extends Serializable> idList);

	public List<String> selectIdsByHwId(String hw_id);

	public List<String> selectIdsLikeId(String id);

	public ZcLdUnit selectLdUnitById(String id);
}
