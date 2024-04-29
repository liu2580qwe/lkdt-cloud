package org.lkdt.modules.radar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.lkdt.modules.radar.entity.RiskValues;

import java.util.List;

/**
 *
 */
public interface RiskValuesMapper extends BaseMapper<RiskValues> {

    int insertBatch(List<RiskValues> riskValuesList);

}
