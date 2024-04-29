package org.lkdt.modules.traffic.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.lkdt.modules.traffic.entity.TrafficIncidentRecord;

/**
 * @Description: 道路事件记录
 * @Author: jeecg-boot
 * @Date:   2021-06-03
 * @Version: V1.0
 */
public interface TrafficIncidentRecordMapper extends BaseMapper<TrafficIncidentRecord> {
    List<String> getEventIdList();
}
