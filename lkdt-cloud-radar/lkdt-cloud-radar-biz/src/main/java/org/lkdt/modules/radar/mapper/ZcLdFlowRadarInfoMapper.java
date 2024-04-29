package org.lkdt.modules.radar.mapper;

import java.util.List;

import org.lkdt.modules.radar.entity.ZcLdRadarFrameTargetAll;
import org.apache.ibatis.annotations.Param;
import org.lkdt.modules.radar.entity.ZcLdFlowRadarInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 流量雷达数据表
 * @Author: jeecg-boot
 * @Date:   2021-03-23
 * @Version: V1.0
 */
public interface ZcLdFlowRadarInfoMapper extends BaseMapper<ZcLdFlowRadarInfo> {

    int insertBatch(List<ZcLdFlowRadarInfo> zcLdFlowRadarInfos);

}
