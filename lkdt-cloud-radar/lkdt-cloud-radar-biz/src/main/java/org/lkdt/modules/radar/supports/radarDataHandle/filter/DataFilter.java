package org.lkdt.modules.radar.supports.radarDataHandle.filter;

import org.lkdt.modules.radar.entity.ZcLdEventInfo;
import org.lkdt.modules.radar.entity.ZcLdEventRadarInfo;
import org.lkdt.modules.radar.entity.baseEntity.ZcLDBaseEntity;
import org.lkdt.modules.radar.supports.radarDataHandle.filter.filterSuper.AbstractDataFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 对外接口：数据过滤
 */
public class DataFilter extends AbstractDataFilter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static volatile DataFilter instance = null;

    public static DataFilter getInstance(){
        if(instance==null){
            synchronized (DataFilter.class){
                if(instance == null){
                    instance = new DataFilter();
                }
            }
        }
        return instance;
    }

    /**
     * 对外接口:车辆详情数据过滤处理
     * @param zcLDBaseEntity
     * @return
     */
    public boolean dataFilterHandle(ZcLDBaseEntity zcLDBaseEntity){

        return validFilter(zcLDBaseEntity);

    }

    /**
     * 对外接口:事件数据过滤处理
     * @param zcLDBaseEntity
     * @param zcLDBaseEntity_DETAIL
     * @return
     */
    public boolean dataFilterHandle(ZcLDBaseEntity zcLDBaseEntity, ZcLDBaseEntity zcLDBaseEntity_DETAIL){

        return validFilter2(zcLDBaseEntity, zcLDBaseEntity_DETAIL);

    }

    /**
     * 大场景雷达车辆数据入库过滤
     * @param zcLDBaseEntity
     * @return 返回true允许通过，返回false数据非法不允许通过
     */
    @Override
    protected boolean validFilter(ZcLDBaseEntity zcLDBaseEntity) {
        if(zcLDBaseEntity instanceof ZcLdEventRadarInfo){
            //检测目标物详情
            ZcLdEventRadarInfo zcLdEventRadarInfo = (ZcLdEventRadarInfo) zcLDBaseEntity;
            //1、过滤y坐标小于100或者大于250米的目标【已在数据抓取时过滤】
//            float beginY = zcLdEventRadarInfo.getBeginCoordinateY().floatValue();
//            float endY = zcLdEventRadarInfo.getEndCoordinateY().floatValue();

            try{

                //2、结束时间为NULL
                Date endDate = zcLdEventRadarInfo.getEndTime();
                if(endDate == null){
                    return false;
                }

                //3、开始时间与结束时间时间差小于1秒
                long beginTime = zcLdEventRadarInfo.getBeginTime().getTime();
                long endTime = zcLdEventRadarInfo.getEndTime().getTime();

                if(endTime - beginTime < 1000){
                    return false;
                }
                //debug调试
                //System.out.println(zcLdEventRadarInfo);
            } catch (Exception e) {
                logger.error("数据过滤时异常", e);
            }
        } else if(zcLDBaseEntity instanceof ZcLdEventInfo){
            //事件检测详情
            ZcLdEventInfo zcLdEventInfo = (ZcLdEventInfo) zcLDBaseEntity;

            //debug调试
            //System.out.println(zcLdEventInfo);
        }
        return true;
    }

    /**
     * 大场景雷达事件入库过滤
     * @param zcLDBaseEntity
     * @param zcLDBaseEntity_DETAIL
     * @return
     */
    @Override
    protected boolean validFilter2(ZcLDBaseEntity zcLDBaseEntity, ZcLDBaseEntity zcLDBaseEntity_DETAIL) {
        ZcLdEventRadarInfo zcLdEventRadarInfo = null;
        if(zcLDBaseEntity_DETAIL instanceof ZcLdEventRadarInfo){
            zcLdEventRadarInfo = (ZcLdEventRadarInfo) zcLDBaseEntity_DETAIL;
        }
        //判断事件合法性
        if(zcLdEventRadarInfo == null){
            return false;
        }
        if(zcLDBaseEntity instanceof ZcLdEventInfo){
            //事件检测详情
            ZcLdEventInfo zcLdEventInfo = (ZcLdEventInfo) zcLDBaseEntity;
            if(zcLdEventInfo.getCreateTime().after(zcLdEventRadarInfo.getBeginTime())
                && zcLdEventInfo.getCreateTime().before(zcLdEventRadarInfo.getEndTime())){
                //事件发生时间处在车辆行驶时间期间
                return true;
            }
            return false;
            //debug调试
            //System.out.println(zcLdEventInfo);
        }
        return false;
    }
}

