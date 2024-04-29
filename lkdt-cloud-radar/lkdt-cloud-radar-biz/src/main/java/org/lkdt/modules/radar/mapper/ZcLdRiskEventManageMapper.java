package org.lkdt.modules.radar.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.lkdt.modules.radar.entity.ZcLdRiskEventManage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 雷达事件管理
 * @Author: jeecg-boot
 * @Date:   2021-08-27
 * @Version: V1.0
 */
public interface ZcLdRiskEventManageMapper extends BaseMapper<ZcLdRiskEventManage> {

	/**
	 * 日报统计
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	List<ZcLdRiskEventManage> queryDailyStatistics(@Param("beginTime") String beginTime ,@Param("endTime") String endTime);
}
