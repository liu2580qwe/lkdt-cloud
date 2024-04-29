package org.lkdt.modules.radar.mapper;
import java.util.List;
import org.lkdt.modules.radar.entity.ZcLdWindRadarRelation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
/**
 * 大风与雷达关联关系表
 * @project org.lkdt.modules.radar.mapper.ZcLdWindRadarRelationMapper
 * @package org.lkdt.modules.radar.mapper
 * @className ZcLdWindRadarRelationMapper
 * @author Cai Xibei
 * @version 1.0.0
 * @createTime 2021/8/17 15:02
 */
public interface ZcLdWindRadarRelationMapper extends BaseMapper<ZcLdWindRadarRelation> {
    public List<String> selectByRadarId(String radarId);

    public void delByWindIdAndRadarId(String windId,String radarId);
}
