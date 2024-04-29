package org.lkdt.modules.radar.supports.radarDataService.dataCache;

import org.lkdt.modules.radar.supports.radarDataService.dataCache.DO.RadarCalcDO;
import org.lkdt.modules.radar.supports.radarDataService.dataCache.DO.RadarLaneCalcDO;
import org.lkdt.modules.radar.supports.radarTools.BeanUtil;

import java.util.List;

/**
 * 雷达数据统计
 * 累加瞬时车道以及总体数据，统计出间隔15分钟30分钟60分钟的数据结果
 */
public class RadarCache extends TimeCounter {

    /**十五分钟雷达所有车道分别统计结果*/
    public static List<RadarLaneCalcDO> chedaotongji_15;

    /**三十分钟雷达所有车道分别统计结果*/
    public static List<RadarLaneCalcDO> chedaotongji_30;

    /**六十分钟雷达所有车道分别统计结果*/
    public static List<RadarLaneCalcDO> chedaotongji_60;

    /**十五分钟整体统计*/
    public static RadarCalcDO zhegntitongji_15;

    /**三十分钟整体统计*/
    public static RadarCalcDO zhegntitongji_30;

    /**六十分钟整体统计*/
    public static RadarCalcDO zhegntitongji_60;

    public RadarCache(String radarId, BeanUtil beanUtil){
        init(radarId, beanUtil);
    }

    private RadarLaneCalcDO searchRadarLaneCalcDObyList(RadarLaneCalcDO dest, List<RadarLaneCalcDO> radarLaneCalcDOS){
        for(RadarLaneCalcDO radarLaneCalcDO: radarLaneCalcDOS){
            if(radarLaneCalcDO.getLaneNum() == dest.getLaneNum()){
                return radarLaneCalcDO;
            }
        }
        return null;
    }

    /**
     * 车道
     * @param laneResultOfFifteenMin
     */
    @Override
    public void setLaneResultOfFifteenMin(List<RadarLaneCalcDO> laneResultOfFifteenMin) {
        for(RadarLaneCalcDO radarLaneCalcDO: laneResultOfFifteenMin){
            RadarLaneCalcDO his = this.searchRadarLaneCalcDObyList(radarLaneCalcDO, this.laneResultOfFifteenMin);
            if(his != null){
                if(his.getvAvg() != 0){
                    his.setvAvg((his.getvAvg() + radarLaneCalcDO.getvAvg())/2);
                } else {
                    his.setvAvg(radarLaneCalcDO.getvAvg());
                }

                if(his.getTimeDistance() != 0){
                    his.setTimeDistance((his.getTimeDistance() + radarLaneCalcDO.getTimeDistance())/2);
                } else {
                    his.setTimeDistance(radarLaneCalcDO.getTimeDistance());
                }

            } else {
                this.laneResultOfFifteenMin.add(radarLaneCalcDO);
            }
        }
    }

    /**
     * 车道
     * @param laneResultOfThirtyMin
     */
    @Override
    public void setLaneResultOfThirtyMin(List<RadarLaneCalcDO> laneResultOfThirtyMin) {
        for(RadarLaneCalcDO radarLaneCalcDO: laneResultOfThirtyMin){
            RadarLaneCalcDO his = this.searchRadarLaneCalcDObyList(radarLaneCalcDO, this.laneResultOfThirtyMin);
            if(his != null){
                if(his.getvAvg() != 0){
                    his.setvAvg((his.getvAvg() + radarLaneCalcDO.getvAvg())/2);
                } else {
                    his.setvAvg(radarLaneCalcDO.getvAvg());
                }

                if(his.getTimeDistance() != 0){
                    his.setTimeDistance((his.getTimeDistance() + radarLaneCalcDO.getTimeDistance())/2);
                } else {
                    his.setTimeDistance(radarLaneCalcDO.getTimeDistance());
                }

            } else {
                this.laneResultOfThirtyMin.add(radarLaneCalcDO);
            }
        }
    }

    /**
     * 车道
     * @param laneResultOfSixtyMin
     */
    @Override
    public void setLaneResultOfSixtyMin(List<RadarLaneCalcDO> laneResultOfSixtyMin) {
        for(RadarLaneCalcDO radarLaneCalcDO: laneResultOfSixtyMin){
            RadarLaneCalcDO his = this.searchRadarLaneCalcDObyList(radarLaneCalcDO, this.laneResultOfSixtyMin);
            if(his != null){
                if(his.getvAvg() != 0){
                    his.setvAvg((his.getvAvg() + radarLaneCalcDO.getvAvg())/2);
                } else {
                    his.setvAvg(radarLaneCalcDO.getvAvg());
                }

                if(his.getTimeDistance() != 0){
                    his.setTimeDistance((his.getTimeDistance() + radarLaneCalcDO.getTimeDistance())/2);
                } else {
                    his.setTimeDistance(radarLaneCalcDO.getTimeDistance());
                }

            } else {
                this.laneResultOfSixtyMin.add(radarLaneCalcDO);
            }
        }
    }

    /**
     * 整体
     * @param globalResultOfFifteenMin
     */
    @Override
    public void setGlobalResultOfFifteenMin(RadarCalcDO globalResultOfFifteenMin) {
        if(this.globalResultOfFifteenMin.getvAvg() != 0){
            this.globalResultOfFifteenMin.setvAvg((this.globalResultOfFifteenMin.getvAvg() + globalResultOfFifteenMin.getvAvg())/2);
        } else {
            this.globalResultOfFifteenMin.setvAvg(globalResultOfFifteenMin.getvAvg());
        }

        if(this.globalResultOfFifteenMin.getTimeDistance() != 0){
            this.globalResultOfFifteenMin.setTimeDistance((this.globalResultOfFifteenMin.getTimeDistance() + globalResultOfFifteenMin.getTimeDistance())/2);
        } else {
            this.globalResultOfFifteenMin.setTimeDistance(globalResultOfFifteenMin.getTimeDistance());
        }
    }

    /**
     * 整体
     * @param globalResultOfThirtyMin
     */
    @Override
    public void setGlobalResultOfThirtyMin(RadarCalcDO globalResultOfThirtyMin) {
        if(this.globalResultOfThirtyMin.getvAvg() != 0){
            this.globalResultOfThirtyMin.setvAvg((this.globalResultOfThirtyMin.getvAvg() + globalResultOfThirtyMin.getvAvg())/2);
        } else {
            this.globalResultOfThirtyMin.setvAvg(globalResultOfThirtyMin.getvAvg());
        }

        if(this.globalResultOfThirtyMin.getTimeDistance() != 0){
            this.globalResultOfThirtyMin.setTimeDistance((this.globalResultOfThirtyMin.getTimeDistance() + globalResultOfThirtyMin.getTimeDistance())/2);
        } else {
            this.globalResultOfThirtyMin.setTimeDistance(globalResultOfThirtyMin.getTimeDistance());
        }
    }

    /**
     * 整体
     * @param globalResultOfSixtyMin
     */
    @Override
    public void setGlobalResultOfSixtyMin(RadarCalcDO globalResultOfSixtyMin) {
        if(this.globalResultOfSixtyMin.getvAvg() != 0){
            this.globalResultOfSixtyMin.setvAvg((this.globalResultOfSixtyMin.getvAvg() + globalResultOfSixtyMin.getvAvg())/2);
        } else {
            this.globalResultOfSixtyMin.setvAvg(globalResultOfSixtyMin.getvAvg());
        }

        if(this.globalResultOfSixtyMin.getTimeDistance() != 0){
            this.globalResultOfSixtyMin.setTimeDistance((this.globalResultOfSixtyMin.getTimeDistance() + globalResultOfSixtyMin.getTimeDistance())/2);
        } else {
            this.globalResultOfSixtyMin.setTimeDistance(globalResultOfSixtyMin.getTimeDistance());
        }
    }

    /**
     * 间隔十五分钟执行一次
     */
    @Override
    public void taskFifteenMin() {
        RadarCache.chedaotongji_15 = this.laneResultOfFifteenMin;
        RadarCache.zhegntitongji_15 = this.globalResultOfFifteenMin;
        //清空十五分钟统计信息
        this.laneResultOfFifteenMin.clear();
        //初始化数据
        this.globalResultOfFifteenMin.init();
    }

    @Override
    public void clearMarkFifteenMin() {

    }

    /**
     * 间隔三十分钟执行一次
     */
    @Override
    public void taskThirtyMin() {
        RadarCache.chedaotongji_30 = this.laneResultOfThirtyMin;
        RadarCache.zhegntitongji_30 = this.globalResultOfThirtyMin;
        //清空三十分钟统计数据
        this.laneResultOfThirtyMin.clear();
        //初始化数据
        this.globalResultOfThirtyMin.init();
    }

    @Override
    public void clearMarkThirtyMin() {

    }

    /**
     * 间隔六十分钟执行一次
     */
    @Override
    public void taskSixtyMin() {
        RadarCache.chedaotongji_60 = this.laneResultOfSixtyMin;
        RadarCache.zhegntitongji_60 = this.globalResultOfSixtyMin;
        //清空六十分钟统计数据
        this.laneResultOfSixtyMin.clear();
        //初始化数据
        this.globalResultOfSixtyMin.init();
    }

    @Override
    public void clearMarkSixtyMin() {

    }
}
