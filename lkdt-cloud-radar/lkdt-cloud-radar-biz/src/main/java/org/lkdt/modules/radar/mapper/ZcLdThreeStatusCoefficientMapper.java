package org.lkdt.modules.radar.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.lkdt.modules.radar.entity.ZcLdThreeStatusCoefficient;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 交通流三态系数
 * @Author: jeecg-boot
 * @Date:   2021-09-23
 * @Version: V1.0
 */
public interface ZcLdThreeStatusCoefficientMapper extends BaseMapper<ZcLdThreeStatusCoefficient> {

	/**
	 * 对P值分段(0.7-1.0之间，以每 0.01 为一段) 排序
	 * @param radarId
	 * @param direction
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	List<ZcLdThreeStatusCoefficient> queryByPElt(@Param("radarId") String radarId ,@Param("direction") String direction ,@Param("beginTime") String beginTime ,@Param("endTime") String endTime);
	
	/**
	 * 批量入库
	 * @param zcLdThreeStatusCoefficientList
	 * @return
	 */
	int insertBatch(List<ZcLdThreeStatusCoefficient> zList);
}
