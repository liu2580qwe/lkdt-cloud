package org.lkdt.modules.radar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.lkdt.modules.radar.entity.ZcLdRadarFrameCar;

import java.util.List;

/**
 *
 */
public interface ZcLdRadarFrameCarMapper extends BaseMapper<ZcLdRadarFrameCar> {

    int insertBatch(List<ZcLdRadarFrameCar> zcLdRadarFrameCarList);

}
