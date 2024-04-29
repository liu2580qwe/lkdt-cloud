package org.lkdt.modules.radar.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import org.lkdt.modules.radar.entity.ZcLdWind;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
/**
 * 大风配置表
 * @project org.lkdt.modules.radar.mapper.ZcLdWindMapper
 * @package org.lkdt.modules.radar.mapper
 * @className ZcLdWindMapper
 * @author Cai Xibei
 * @version 1.0.0
 * @createTime 2021/8/17 14:58
 */
public interface ZcLdWindMapper extends BaseMapper<ZcLdWind> {
	
	public List<ZcLdWind> selectByEpId(@Param("epId") String epId);
}
