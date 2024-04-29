package org.lkdt.modules.fog.mapper;

import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.lkdt.modules.fog.entity.DutyClock;

/**
 * @Description: 值班打卡
 * @Author: jeecg-boot
 * @Date:   2021-07-01
 * @Version: V1.0
 */
public interface DutyClockMapper extends BaseMapper<DutyClock> {

	public boolean deleteByMainId(@Param("mainId") String mainId);
    
	public List<DutyClock> selectByMainId(@Param("mainId") String mainId);
}
