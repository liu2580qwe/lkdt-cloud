package org.lkdt.modules.radar.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.lkdt.modules.radar.entity.ZcLdRadarVideo;
import org.apache.ibatis.annotations.Param;
import java.util.List;
/**
 * @Description: 雷达设备摄像头表
 * @Author: jeecg-boot
 * @Date:   2021-07-26
 * @Version: V1.0
 */
public interface ZcLdRadarVideoMapper extends BaseMapper<ZcLdRadarVideo> {

	public String deleteByMainId(@Param("mainId") String mainId);
    
	public List<ZcLdRadarVideo> selectByMainId(@Param("mainId") String mainId);
	public List<ZcLdRadarVideo> selectByUnitId(@Param("mainId") String mainId);
	public List<ZcLdRadarVideo> selectByEpId(@Param("epId") String epId);

}
