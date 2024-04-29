package org.lkdt.modules.radar.service;

import org.lkdt.modules.radar.entity.ZcLdLaneInfo;
import org.lkdt.modules.radar.entity.ZcLdEquipment;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 雷达设备表
 * @Author: jeecg-boot
 * @Date:   2021-03-11
 * @Version: V1.0
 */
public interface IZcLdEquipmentService extends IService<ZcLdEquipment> {

	/**
	 * 删除一对多
	 */
	public void delMain (String id);
	
	/**
	 * 批量删除一对多
	 */
	public void delBatchMain (Collection<? extends Serializable> idList);

	/**
	 * 设备列表
	 * @return
	 */
	List<ZcLdEquipment> queryEquipment();

}
