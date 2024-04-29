package org.lkdt.modules.radar.supports.radarDataHandle.filter.filterSuper;

import org.lkdt.modules.radar.entity.ZcLdEventRadarInfo;
import org.lkdt.modules.radar.entity.baseEntity.ZcLDBaseEntity;
import org.lkdt.modules.radar.supports.radarDataHandle.filter.filterSuper.interfaces.Filter;

/**
 * 数据过滤实现
 * 1、
 * 2、
 * 3、
 */
public abstract class AbstractDataFilter implements Filter {

    /**
     * 数据验证
     */
    protected abstract boolean validFilter(ZcLDBaseEntity zcLDBaseEntity);

    protected abstract boolean validFilter2(ZcLDBaseEntity zcLDBaseEntity, ZcLDBaseEntity zcLdEventRadarInfo);

}
