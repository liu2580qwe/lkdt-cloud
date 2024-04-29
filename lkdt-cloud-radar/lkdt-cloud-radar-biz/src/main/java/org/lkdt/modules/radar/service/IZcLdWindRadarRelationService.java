package org.lkdt.modules.radar.service;
import org.lkdt.modules.radar.entity.ZcLdWindRadarRelation;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * 大风与雷达关联关系表
 * @project org.lkdt.modules.radar.service.IZcLdWindRadarRelationService
 * @package org.lkdt.modules.radar.service
 * @className IZcLdWindRadarRelationService
 * @author Cai Xibei
 * @version 1.0.0
 * @createTime 2021/8/17 15:07
 */
public interface IZcLdWindRadarRelationService extends IService<ZcLdWindRadarRelation> {
       public List<String> selectByRadarId(String radarId);
       public void delByWindIdAndRadarId(String windId,String radarId);
}
