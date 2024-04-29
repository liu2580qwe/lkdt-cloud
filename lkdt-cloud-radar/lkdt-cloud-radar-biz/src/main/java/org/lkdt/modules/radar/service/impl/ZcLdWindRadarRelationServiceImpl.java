package org.lkdt.modules.radar.service.impl;
import org.lkdt.modules.radar.entity.ZcLdWindRadarRelation;
import org.lkdt.modules.radar.mapper.ZcLdWindRadarRelationMapper;
import org.lkdt.modules.radar.service.IZcLdWindRadarRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
/**
 *  大风与雷达关联关系表
 * @project org.lkdt.modules.radar.service.impl.ZcLdWindRadarRelationServiceImpl
 * @package org.lkdt.modules.radar.service.impl
 * @className ZcLdWindRadarRelationServiceImpl
 * @author Cai Xibei
 * @version 1.0.0
 * @createTime 2021/8/17 15:09
 */
@Service
public class ZcLdWindRadarRelationServiceImpl extends ServiceImpl<ZcLdWindRadarRelationMapper, ZcLdWindRadarRelation> implements IZcLdWindRadarRelationService {
    @Autowired
    private ZcLdWindRadarRelationMapper zcLdWindRadarRelationMapper;
    
    @Override
    public List<String> selectByRadarId(String radarId){
        return zcLdWindRadarRelationMapper.selectByRadarId(radarId);
    }

    @Override
    public void delByWindIdAndRadarId(String windId, String radarId) {
        zcLdWindRadarRelationMapper.delByWindIdAndRadarId(windId,radarId);
    }

}
