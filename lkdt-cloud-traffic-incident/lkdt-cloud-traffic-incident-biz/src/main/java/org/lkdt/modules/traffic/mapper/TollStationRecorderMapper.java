package org.lkdt.modules.traffic.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.lkdt.modules.traffic.entity.TollStationRecorder;

/**
 * @Description: 收费站封路事件详细
 * @Author: jeecg-boot
 * @Date:   2021-06-03
 * @Version: V1.0
 */
public interface TollStationRecorderMapper extends BaseMapper<TollStationRecorder> {


    boolean deleteByMainId(@Param("mainId") String mainId);

    List<TollStationRecorder> selectByMainId(@Param("mainId") String mainId);

    List<TollStationRecorder> list(Map<String, Object> map);
}
