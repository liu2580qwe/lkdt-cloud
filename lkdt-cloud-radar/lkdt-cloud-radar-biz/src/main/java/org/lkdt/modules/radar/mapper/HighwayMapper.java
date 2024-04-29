package org.lkdt.modules.radar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.lkdt.modules.radar.entity.Highway;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: zc_highway
 * @Author: jeecg-boot
 * @Date:   2021-04-23
 * @Version: V1.0
 */
public interface HighwayMapper extends BaseMapper<Highway> {

	/**
	 * 编辑节点状态
	 * @param id
	 * @param status
	 */
	void updateTreeNodeStatus(@Param("id") String id,@Param("status") String status);

	public List<String> queryChildNodesByHwId(@Param("hwId") String hwId);

}
