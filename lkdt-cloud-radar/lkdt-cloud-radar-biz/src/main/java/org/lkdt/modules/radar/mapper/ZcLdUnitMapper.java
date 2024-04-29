package org.lkdt.modules.radar.mapper;
import org.lkdt.modules.radar.entity.ZcLdUnit;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
/**
 * @Description: 雷达单元表
 * @Author: jeecg-boot
 * @Date:   2021-07-23
 * @Version: V1.0
 */
public interface ZcLdUnitMapper extends BaseMapper<ZcLdUnit> {
    public List<String> selectIdsByHwId(String hw_id);
    public List<String> selectIdsLikeId(String id);
    public ZcLdUnit selectLdUnitById(String id);
}
