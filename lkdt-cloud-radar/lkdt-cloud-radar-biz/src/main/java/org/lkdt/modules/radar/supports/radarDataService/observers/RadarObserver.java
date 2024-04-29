package org.lkdt.modules.radar.supports.radarDataService.observers;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.lkdt.modules.radar.supports.radarDataService.DataReader;
//import org.lkdt.modules.radar.supports.radarDataService.dataCookie.DO.RadarLaneCalcDO;
import org.lkdt.modules.radar.supports.radarDataService.dataCache.DO.RadarFrame;
import org.lkdt.modules.radar.supports.radarDataService.observers.DO.RadarObjDO;

import java.util.Calendar;

//import java.util.ArrayList;
//import java.util.List;

/**
 * 雷达监听
 */
public abstract class RadarObserver {

    protected DataReader dataReader;

    /**毫秒*/
    public long cacheTime;

    /**识别出最大车辆数*/
    public long maxLaneSize = 256;

//    /**缓存前一帧数据*/
//    protected JSONObject[] preJObjectArray = new JSONObject[128];
//
//    /**当前帧数据*/
//    protected DataReader dataReader;
//
//    /**同车辆前一帧数据*/
//    protected RadarObjDO radarObjDO_O = new RadarObjDO();
//
//    /**同车辆当前帧数据*/
//    protected RadarObjDO radarObjDO_N = new RadarObjDO();

//    protected void init(DataReader dataReader, JSONArray jsonArray){
//
//
//    }

//    protected void init1(DataReader dataReader, JSONArray jsonArray){
//
//        this.dataReader = dataReader;
//
//        //缓存当前数据帧targetID
//        List<Integer> currList = new ArrayList<>();
//
//        //车辆targetId从0开始
//        for (int j = 0; j < jsonArray.size(); j++) {
//            //数据解析
//            JSONObject obj = (JSONObject) jsonArray.get(j);
//            int targetId = obj.getInt("targetId");//目标ID
//
//            currList.add(targetId);
//
//            //获取上次数据
//            JSONObject preData = preJObjectArray[targetId];
//            if(preData == null){
//                //新增新车数据
//                preJObjectArray[targetId] = obj;
//            } else {
//
//                //老车数据
//                preJObjectArray[targetId] = obj;
//
//                this.readData(this.radarObjDO_O, preData);
//
//                this.readData(this.radarObjDO_N, obj);
//
//                radarDataHandleDetail(this.radarObjDO_O, this.radarObjDO_N);
//            }
//        }
//
//        //List<Integer> deleteTargets = new ArrayList<>();
//        //遍历缓存数据，删除dataReaderNum
//        for(JSONObject obj: preJObjectArray){
//            int targetId = obj.getInt("targetId");//目标ID
//            //未连续的记录
//            if(!currList.contains(targetId)){
//                //删除
//                preJObjectArray[targetId] = null;
//
//            }
//        }
//    }

    protected void readData(RadarObjDO radarObjDO, JSONObject obj){
        int targetId = obj.getInt("targetId");//目标ID
        float sX = obj.getFloat("sX");//x坐标
        float sY = obj.getFloat("sY");//y坐标
        float vX = obj.getFloat("vX");//x速度
        float vY = obj.getFloat("vY");//y速度
        float aX = obj.getFloat("aX");//x加速度
        float aY = obj.getFloat("aY");//y加速度
        int laneNum = obj.getInt("laneNum");//车道号
        //1 小型车，2 大型车，3 超大型车
        int carType = obj.getInt("carType");//车辆类型
        //0 无事件 1 车辆停止 2 车辆反方向行驶 （逆行） 3 车辆超速行驶 5 车辆实线变道
        //101 车辆停止消失 102 车辆反方向行 驶消失 103 车辆超速行驶 消失105 车辆实线变道 消失
        int event = obj.getInt("event");//事件类型
        int carLength = obj.getInt("carLength");//车辆长度
        radarObjDO.setTargetId(targetId).setsX(sX).setsY(sY).setvX(vX).setvY(vY).setaX(aX).setaY(aY)
                .setLaneNum(laneNum).setCarType(carType).setEvent(event).setCarLength(carLength);
    }

    protected void readData(RadarFrame radarFrame, JSONObject obj){
        int targetId = obj.getInt("targetId");//目标ID
        float sX = obj.getFloat("sX");//x坐标
        float sY = obj.getFloat("sY");//y坐标
        float vX = obj.getFloat("vX");//x速度
        float vY = obj.getFloat("vY");//y速度
        float aX = obj.getFloat("aX");//x加速度
        float aY = obj.getFloat("aY");//y加速度
        int laneNum = obj.getInt("laneNum");//车道号
        //1 小型车，2 大型车，3 超大型车
        int carType = obj.getInt("carType");//车辆类型
        //0 无事件 1 车辆停止 2 车辆反方向行驶 （逆行） 3 车辆超速行驶 5 车辆实线变道
        //101 车辆停止消失 102 车辆反方向行 驶消失 103 车辆超速行驶 消失105 车辆实线变道 消失
        int event = obj.getInt("event");//事件类型
        int carLength = obj.getInt("carLength");//车辆长度
        radarFrame.setTargetId(targetId).setsX(sX).setsY(sY).setvX(vX).setvY(vY).setaX(aX).setaY(aY)
                .setLaneNum(laneNum).setCarType(carType).setEvent(event).setCarLength(carLength)
                .setCreateTime(Calendar.getInstance().getTimeInMillis());
    }

//    protected void paramToRadarCookie(List<RadarLaneCalcDO> radarLaneCalcDOList){
//
//    }

    public abstract void radarDataHandle(DataReader dataReader, JSONArray jsonArray);

//    public abstract void radarDataHandleDetail(RadarObjDO oldJ, RadarObjDO newJ);

    public abstract void destroyTimeCounter();

}
