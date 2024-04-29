package org.lkdt.modules.fog.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.lkdt.modules.fog.entity.AlarmRoadControlInfo;

/**
 * @Description: 告警与道路管制关联表
 * @Author: jeecg-boot
 * @Date:   2021-06-03
 * @Version: V1.0
 */
public interface AlarmRoadControlInfoMapper extends BaseMapper<AlarmRoadControlInfo> {
    /**
     * 根据路段告警ID查询告警信息和封路信息
     * @param roadAlarmId
     * @return
     */
    List<AlarmRoadControlInfo> queryAlarmRoadControlList(String roadAlarmId);

}
