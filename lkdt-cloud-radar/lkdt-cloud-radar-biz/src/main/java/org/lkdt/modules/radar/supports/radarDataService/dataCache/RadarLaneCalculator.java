package org.lkdt.modules.radar.supports.radarDataService.dataCache;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.lkdt.modules.radar.supports.radarDataService.DataReader;
import org.lkdt.modules.radar.supports.radarDataService.dataCache.DO.RadarCalcDO;
import org.lkdt.modules.radar.supports.radarDataService.dataCache.DO.RadarLaneCalcDO;
import org.lkdt.modules.radar.supports.radarDataService.observers.DO.RadarObjDO;
import org.lkdt.modules.radar.supports.radarDataService.observers.RadarObserver;
import org.lkdt.modules.radar.supports.radarTools.BeanUtil;

import java.util.*;

/**
 * 雷达车道瞬时数据计算器
 */
public class RadarLaneCalculator extends RadarObserver {

    /**时间计数器*/
    TimeCounter timeCounter;

    public RadarLaneCalculator(String radarId, BeanUtil beanUtil){
        timeCounter = new RadarCache(radarId, beanUtil);
    }

    /**销毁定时统计*/
    @Override
    public void destroyTimeCounter(){
        this.timeCounter.cancelTimers();
    }

    /**
     * 处理
     * @param dataReader
     * @param jsonArray
     */
    @Override
    public void radarDataHandle(DataReader dataReader, JSONArray jsonArray) {

        this.dataReader = dataReader;

        //解析车道瞬时数据
        List<RadarLaneCalcDO> radarLaneCalcDOList = laneLiveData(jsonArray);

        //计算雷达整体瞬时数据
        radarGlobalLiveData(radarLaneCalcDOList);

    }

    /**
     * 解析车道瞬时数据
     */
    private List<RadarLaneCalcDO> laneLiveData(JSONArray jsonArray){
        //按车道划分
        Map<Integer, List<RadarObjDO>> map = new HashMap<>();
        //解析雷达数据，按车道区分
        ///////////////////////start
        //////////////////////////////////////////////
        for(int i = 0; i < jsonArray.size(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            //每辆车解析
            RadarObjDO radarObjDO = new RadarObjDO();
            this.readData(radarObjDO, jsonObject);

            if(map.get(radarObjDO.getLaneNum()) == null){
                List<RadarObjDO> list = new ArrayList<>();
                list.add(radarObjDO);
                map.put(radarObjDO.getLaneNum(), list);
            } else {
                map.get(radarObjDO.getLaneNum()).add(radarObjDO);
            }

        }
        ///////////////////////end
        //////////////////////////////////////////////

        List<RadarLaneCalcDO> radarLaneCalcDOList = new ArrayList<>();

        //车道排序:小->大
        for(HashMap.Entry<Integer, List<RadarObjDO>> entry: map.entrySet()){

            //排序【目的：计算平均时距】
            Collections.sort(entry.getValue());

            //计算车道平均速度，平均时距
            RadarLaneCalcDO radarLaneCalcDO = new RadarLaneCalcDO();
            radarLaneCalcDO.setLaneNum(entry.getKey());


            float countV = 0;
            float firstY = 0;
            float countY = 0;
            //小型车计数
            float carCount = 0;
            //中型车计数
            float mediumTruckCount = 0;
            //大型车计数
            float lagerTruckCount = 0;
            for(RadarObjDO r:entry.getValue()){

                countV += Math.abs(r.getvY());

                if(firstY == 0){
                    firstY = Math.abs(r.getsY());
                } else {
                    countY += (Math.abs(r.getsY()) - firstY);
                    firstY = Math.abs(r.getsY());
                }

                //小型车计数  1小型车，2大型车，3中型车
                if(r.getCarType() == 1){
                    carCount ++;
                } else if (r.getCarType() == 2) {
                    lagerTruckCount ++;
                } else if (r.getCarType() == 3) {
                    mediumTruckCount ++;
                }

            }

            //计算平均速度
            float v_ = countV/entry.getValue().size();
            radarLaneCalcDO.setvAvg(v_);

            //计算平均时距
            float timeDistance = countY/entry.getValue().size();
            radarLaneCalcDO.setTimeDistance(timeDistance);

            //计算小车占比 carCount mediumTruckCount lagerTruckCount
            radarLaneCalcDO.setCarCount(carCount);

            //计算中型车占比
            radarLaneCalcDO.setMediumTruckCount(mediumTruckCount);

            //计算大型车占比
            radarLaneCalcDO.setLagerTruckCount(lagerTruckCount);

            //TODO: 车辆突然停止统计、超高速车辆统计、超低速车辆统计、车辆预测轨迹偏差


            //车流密度
            double Ro = (carCount + mediumTruckCount * 1.5 + lagerTruckCount * 2)/150*1000;
            long ro = Math.round(Ro);//单位：辆/km
            radarLaneCalcDO.setCarFlowDensity(ro);

            //车流间距

            radarLaneCalcDOList.add(radarLaneCalcDO);
        }

        //计算：车道瞬时数据
        timeCounter.setLaneResultOfFifteenMin(radarLaneCalcDOList);
        timeCounter.setLaneResultOfThirtyMin(radarLaneCalcDOList);
        timeCounter.setLaneResultOfSixtyMin(radarLaneCalcDOList);
        return radarLaneCalcDOList;
    }

    /**
     * 计算雷达整体瞬时数据
     */
    private void radarGlobalLiveData(List<RadarLaneCalcDO> radarLaneCalcDOList){
        //雷达整体瞬时统计
        RadarCalcDO radarCalcDO = new RadarCalcDO();
        float timeDistance = 0;
        float avg = 0;
        for(RadarLaneCalcDO r: radarLaneCalcDOList){
            timeDistance += r.getTimeDistance();
            avg += r.getvAvg();
        }
        timeDistance = timeDistance / radarLaneCalcDOList.size();
        avg = avg / radarLaneCalcDOList.size();
        radarCalcDO.setTimeDistance(timeDistance);
        radarCalcDO.setvAvg(avg);

        //计算时间范围统计数据
        timeCounter.setGlobalResultOfFifteenMin(radarCalcDO);
        timeCounter.setGlobalResultOfThirtyMin(radarCalcDO);
        timeCounter.setGlobalResultOfSixtyMin(radarCalcDO);
    }

//    @Override
//    public void radarDataHandleDetail(RadarObjDO oldJ, RadarObjDO newJ) {
//
//        //变道
//
//        //计算平均速度
//
//        //计算平均时距
//    }
}
