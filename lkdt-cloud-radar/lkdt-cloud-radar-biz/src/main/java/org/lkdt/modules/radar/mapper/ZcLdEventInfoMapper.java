package org.lkdt.modules.radar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.lkdt.modules.radar.entity.ZcLdEventInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description: 事件信息表
 * @Author: jeecg-boot
 * @Date:   2021-03-23
 * @Version: V1.0
 */
public interface ZcLdEventInfoMapper extends BaseMapper<ZcLdEventInfo> {

	/**
	 * 事件占比
	 * @param equId
	 * @return
	 */
	List<Map<String,Object>> queryEventRatio(@Param("equId") String equId);
}
