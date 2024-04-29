package org.lkdt.modules.traffic.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.lkdt.modules.traffic.entity.TollStationEvent;


/**
 * @Description: 收费站封路事件
 * @Author: jeecg-boot
 * @Date:   2021-06-03
 * @Version: V1.0
 */

public interface TollStationEventMapper extends BaseMapper<TollStationEvent> {
    int change(TollStationEvent tollStationEvent);
}
