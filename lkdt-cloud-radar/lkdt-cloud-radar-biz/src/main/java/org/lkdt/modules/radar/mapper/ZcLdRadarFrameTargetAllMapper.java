package org.lkdt.modules.radar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.lkdt.modules.radar.entity.ZcLdRadarFrameTargetAll;

import java.util.List;

/**
 *
 */
public interface ZcLdRadarFrameTargetAllMapper extends BaseMapper<ZcLdRadarFrameTargetAll> {

    int insertBatch(List<ZcLdRadarFrameTargetAll> zcLdRadarFrameTargetAllList);

}
