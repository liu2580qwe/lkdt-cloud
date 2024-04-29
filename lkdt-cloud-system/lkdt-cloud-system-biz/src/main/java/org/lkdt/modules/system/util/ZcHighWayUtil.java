package org.lkdt.modules.system.util;


import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.lkdt.common.system.vo.HighwayModel;
import org.lkdt.common.util.HighWayUtil;
import org.lkdt.common.util.RedisUtil;
import org.lkdt.modules.system.entity.Highway;
import org.lkdt.modules.system.service.IHighwayService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;

@Component
public class ZcHighWayUtil {
    @Autowired
    private IHighwayService zcHighwayService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private HighWayUtil highWayUtil;

    /**
     * 加载redis中ZcHighway缓存
     */
    @PostConstruct
    public void initHighWay() {
        List<Highway> zcHighways = zcHighwayService.selectZcHighWayList();
        if (CollectionUtils.isEmpty(zcHighways)) {
            return;
        } else {
            Set<Object> set = redisUtil.getLikeKeys(highWayUtil.highway_rootList);
            for (Object o : set) {
                if (o != null) {
                    redisUtil.del((String) o);
                }
            }
            for (Highway zc : zcHighways) {
                HighwayModel highwayModel = new HighwayModel();
                BeanUtils.copyProperties(zc, highwayModel);
                redisUtil.set(highWayUtil.highway_rootList + zc.getId(), highwayModel);
            }
        }
    }
}
