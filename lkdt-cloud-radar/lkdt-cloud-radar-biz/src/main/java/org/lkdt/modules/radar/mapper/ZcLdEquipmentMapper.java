package org.lkdt.modules.radar.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.lkdt.modules.radar.entity.ZcLdEquipment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 雷达设备表
 * @Author: jeecg-boot
 * @Date:   2021-03-11
 * @Version: V1.0
 */
public interface ZcLdEquipmentMapper extends BaseMapper<ZcLdEquipment> {

	/**
	 * 设备列表
	 * @return
	 */
	List<ZcLdEquipment> queryEquipment();
}
