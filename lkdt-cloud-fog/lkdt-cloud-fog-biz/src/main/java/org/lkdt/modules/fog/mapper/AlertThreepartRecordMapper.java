package org.lkdt.modules.fog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.lkdt.modules.fog.entity.AlertThreepartRecord;

import java.util.List;

/**
 * @Description: 三方告警信息操作记录
 * @Author: jeecg-boot
 * @Date:   2021-04-27
 * @Version: V1.0
 */
public interface AlertThreepartRecordMapper extends BaseMapper<AlertThreepartRecord> {

	public boolean deleteByMainId(@Param("mainId") String mainId);
    
	public List<AlertThreepartRecord> selectByMainId(@Param("mainId") String mainId);
}
