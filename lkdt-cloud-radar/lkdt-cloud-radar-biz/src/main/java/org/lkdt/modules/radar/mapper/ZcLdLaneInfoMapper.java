package org.lkdt.modules.radar.mapper;

import java.util.List;
import org.lkdt.modules.radar.entity.ZcLdLaneInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 雷达车道与道路车道关系表
 * @Author: jeecg-boot
 * @Date:   2021-03-11
 * @Version: V1.0
 */
public interface ZcLdLaneInfoMapper extends BaseMapper<ZcLdLaneInfo> {

	public boolean deleteByMainId(@Param("mainId") String mainId);
    
	public List<ZcLdLaneInfo> selectByMainId(@Param("mainId") String mainId);
	
	public List<ZcLdLaneInfo> queryByMainId(@Param("mainId") String mainId);
	
	public List<ZcLdLaneInfo> queryAllLane();

}
