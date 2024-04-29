package org.lkdt.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.lkdt.modules.system.entity.Highway;

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

	public List<String> queryChildNodesByHwId(String hwId);

	public Highway queryHighwaysByHwId(String hwId);
}
