package org.lkdt.modules.radar.supports.radarDataHandle.extend;

import org.lkdt.modules.radar.entity.ZcLdEventInfo;
import org.lkdt.modules.radar.entity.ZcLdEventRadarInfo;
import org.lkdt.modules.radar.supports.radarDataHandle.filter.DataFilter;
import org.lkdt.modules.radar.supports.radarDataService.DataReader;
import org.lkdt.modules.radar.supports.radarTools.DO.RadarDO;

import java.math.BigDecimal;

/**
 * 数据实时计算
 */
public abstract class AbstractCalculator {

//    /**
//     * 数据实时计算统计处理
//     * @param dataReader 数据计算器
//     * @param radarDO 雷达数据帧
//     */
//    public final void dataReaderCalculator(DataReader dataReader, RadarDO radarDO){
//        dataReader.putArrayBlockingQueueOfByteArray(radarDO);
//    }

    /**
     *
     * @param zcLdEventInfo
     * @param zcLdEventRadarInfo
     * @param low
     * @param high
     * @return true:预备入库, false:不入库
     */
    public boolean commonFilter(ZcLdEventInfo zcLdEventInfo, ZcLdEventRadarInfo zcLdEventRadarInfo, Integer low, Integer high){

        if(low == null || high == null || high <= low){
            return true;
        }

        if(zcLdEventInfo != null){
            BigDecimal y = zcLdEventInfo.getCoordinateY();
            if(y.doubleValue() < low || y.doubleValue() > high){
                return false;
            }
        }

        BigDecimal yBegin = zcLdEventRadarInfo.getBeginCoordinateY();
        BigDecimal yEnd = zcLdEventRadarInfo.getEndCoordinateY();

        if(yBegin == null || yEnd == null){
            return false;
        }

        if(zcLdEventRadarInfo != null){

            double yB = yBegin.doubleValue();
            double yE = yEnd.doubleValue();
            double size = high - low;

            double yE_yB = yE - yB;

            if(Math.abs(yE_yB) < size){
                return false;
            }

            //去向，过滤太晚开始和提前结束的数据
            if(yE_yB > 0){
                if(yB > low || yE < high){
                    return false;
                }
            }
            //来向，过滤太晚开始和提前结束的数据
            else if(yE_yB < 0){
                if(yB < high || yE > low){
                    return false;
                }
            }
            //System.out.printf("start:%s, end:%s, notHeGe:%s, HeGe:%s\n", yB, yE, iii, jjj);

        }

        return true;
    }

    /**
     * 大场景雷达：车辆目标持久化过滤
     * @param zcLdEventRadarInfo 雷达目标信息
     * @return 返回true数据允许通过，返回false数据不允许通过
     */
    public final boolean dataPersistenceHandleOfDetail(ZcLdEventRadarInfo zcLdEventRadarInfo, Integer low, Integer high){

        if(!commonFilter(null, zcLdEventRadarInfo, low, high)){
            return false;
        }

        return DataFilter.getInstance().dataFilterHandle(zcLdEventRadarInfo);

    }

    /**
     * 大场景雷达：事件持久化过滤
     * @param zcLdEventInfo 雷达事件信息
     * @return 返回true数据允许通过，返回false数据不允许通过
     */
    public final boolean dataPersistenceHandleOfEvent(ZcLdEventInfo zcLdEventInfo, ZcLdEventRadarInfo zcLdEventRadarInfo, Integer low, Integer high){

        if(!commonFilter(zcLdEventInfo, zcLdEventRadarInfo, low, high)){
            return false;
        }

        return DataFilter.getInstance().dataFilterHandle(zcLdEventInfo, zcLdEventRadarInfo);

    }

}
