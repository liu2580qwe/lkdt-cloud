package org.lkdt.common.util;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.lkdt.common.system.vo.HighwayModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 道路管理工具类
 * 获取redis缓存集合  及单个
 *
 * @author liusheng
 */
@Component
public class HighWayUtil {
    @Autowired
    private RedisUtil redisUtil;
    public final String highway_rootList = "highway_rootList:";


    /**
     * 获取redis中ZcHighway集合
     */
    public List<HighwayModel> getAllList() {
        Set<Object> set = redisUtil.getLikeKeys(highway_rootList);
        List<HighwayModel> list = new ArrayList<>();
        for (Object o : set) {
            if (o != null) {
                HighwayModel zcHighway = (HighwayModel) redisUtil.get((String) o);
                list.add(zcHighway);
            }
        }
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list;
    }

    /**
     * 获取单个ZcHighway
     *
     * @param id
     * @return
     */
    public HighwayModel getById(String id) {
        HighwayModel zcHighway = (HighwayModel) redisUtil.get(highway_rootList + id);
        if (zcHighway != null) {
            return zcHighway;
        } else {
            return null;
        }
    }

}
