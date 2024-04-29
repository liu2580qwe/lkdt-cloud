package org.lkdt.modules.radar.mapper;

import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.lkdt.modules.radar.entity.ZcLdEquipment;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 雷达设备表
 * @Author: jeecg-boot
 * @Date:   2021-07-23
 * @Version: V1.0
 */
public interface ZcLdRadarEquipmentMapper extends BaseMapper<ZcLdEquipment> {

	public boolean deleteByMainId(@Param("mainId") String mainId);
    
	public List<ZcLdEquipment> selectByMainId(@Param("mainId") String mainId);
}
