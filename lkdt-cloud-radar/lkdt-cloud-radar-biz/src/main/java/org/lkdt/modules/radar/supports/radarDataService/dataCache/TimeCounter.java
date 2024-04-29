package org.lkdt.modules.radar.supports.radarDataService.dataCache;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.radar.entity.ZcLdLaneInfo;
import org.lkdt.modules.radar.entity.ZcLdRadarFrame;
import org.lkdt.modules.radar.entity.ZcLdRadarFrameCar;
import org.lkdt.modules.radar.entity.ZcLdRadarFrameLane;
import org.lkdt.modules.radar.mapper.ZcLdLaneInfoMapper;
import org.lkdt.modules.radar.mapper.ZcLdRadarFrameCarMapper;
import org.lkdt.modules.radar.mapper.ZcLdRadarFrameLaneMapper;
import org.lkdt.modules.radar.mapper.ZcLdRadarFrameMapper;
import org.lkdt.modules.radar.supports.radarDataHandle.Producer;
import org.lkdt.modules.radar.supports.radarDataHandle.RadarEventDataGrab;
import org.lkdt.modules.radar.supports.radarDataService.dataCache.DO.RadarCalcDO;
import org.lkdt.modules.radar.supports.radarDataService.dataCache.DO.RadarLaneCalcDO;
import org.lkdt.modules.radar.supports.radarTools.BeanUtil;
import org.lkdt.modules.radar.supports.radarTools.DO.RadarDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * 时间统计计数器
 *
 * 时间快照
 *
 */
public abstract class TimeCounter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    protected String radarId;

    protected List<Integer> L_lane = new ArrayList<>();//左车道

    protected List<Integer> R_lane = new ArrayList<>();//右车道

    private ZcLdRadarFrameCarMapper zcLdRadarFrameCarMapper;

    private ZcLdRadarFrameLaneMapper zcLdRadarFrameLaneMapper;

    private ZcLdRadarFrameMapper zcLdRadarFrameMapper;

    protected RadarEventDataGrab radarEventDataGrab;

    protected List<RadarLaneCalcDO> laneResultOfFifteenMin = new ArrayList<>();

    protected List<RadarLaneCalcDO> laneResultOfThirtyMin = new ArrayList<>();

    protected List<RadarLaneCalcDO> laneResultOfSixtyMin = new ArrayList<>();

    protected RadarCalcDO globalResultOfFifteenMin = new RadarCalcDO();

    protected RadarCalcDO globalResultOfThirtyMin = new RadarCalcDO();

    protected RadarCalcDO globalResultOfSixtyMin = new RadarCalcDO();

    protected ExecutorService executorService = Executors.newFixedThreadPool(5);

    public void setLaneResultOfFifteenMin(List<RadarLaneCalcDO> laneResultOfFifteenMin) {

        this.laneResultOfFifteenMin = laneResultOfFifteenMin;
    }

    public void setLaneResultOfThirtyMin(List<RadarLaneCalcDO> laneResultOfThirtyMin) {
        this.laneResultOfThirtyMin = laneResultOfThirtyMin;
    }

    public void setLaneResultOfSixtyMin(List<RadarLaneCalcDO> laneResultOfSixtyMin) {
        this.laneResultOfSixtyMin = laneResultOfSixtyMin;
    }

    public void setGlobalResultOfFifteenMin(RadarCalcDO globalResultOfFifteenMin) {
        this.globalResultOfFifteenMin = globalResultOfFifteenMin;
    }

    public void setGlobalResultOfThirtyMin(RadarCalcDO globalResultOfThirtyMin) {
        this.globalResultOfThirtyMin = globalResultOfThirtyMin;
    }

    public void setGlobalResultOfSixtyMin(RadarCalcDO globalResultOfSixtyMin) {
        this.globalResultOfSixtyMin = globalResultOfSixtyMin;
    }

    protected Timer[] timer = new Timer[2];

    protected Timer timer_photo = null;

    //低频定时计数器
    private int timeCount = 0;

    //高频定时计时器
    private int timeCountHiRate = 0;

    public void init(String radarId, BeanUtil beanUtil){

        this.radarId = radarId;
        this.zcLdRadarFrameCarMapper = beanUtil.getBean(ZcLdRadarFrameCarMapper.class);
        this.zcLdRadarFrameLaneMapper = beanUtil.getBean(ZcLdRadarFrameLaneMapper.class);
        this.zcLdRadarFrameMapper = beanUtil.getBean(ZcLdRadarFrameMapper.class);

        //获取雷达数据抓取对象
        Producer producer = beanUtil.getBean(Producer.class);
        this.radarEventDataGrab = producer.radarEventDataGrabs.get(radarId);

        //获取车道方向数据
        ZcLdLaneInfoMapper zcLdLaneInfoMapper = beanUtil.getBean(ZcLdLaneInfoMapper.class);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("equ_id", radarId);
        List<ZcLdLaneInfo> zcLdLaneInfoList = zcLdLaneInfoMapper.selectByMap(paramMap);
        for(ZcLdLaneInfo z: zcLdLaneInfoList){
            if(z.getLaneRoad().indexOf("L-") == 0){
                L_lane.add(z.getLaneRadar());
            } else if(z.getLaneRoad().indexOf("R-") == 0){
                R_lane.add(z.getLaneRadar());
            }
        }

        //低频定时任务
        timer[0] = new Timer("TimeCounter_lowRate_" + radarId);
        timer[0].schedule(new TimerTask() {
            @Override
            public void run() {
                ++timeCount;

                //执行15分钟任务
                taskFifteenMin();
                //清除15分钟统计标记
                clearMarkFifteenMin();

                if(timeCount == 2 || timeCount == 4){
                    //执行30分钟任务
                    taskThirtyMin();
                    //清除30分钟统计标记
                    clearMarkThirtyMin();
                }

                if(timeCount == 4){
                    //执行60分钟任务
                    taskSixtyMin();
                    //清除60分钟统计标记
                    clearMarkSixtyMin();
                }

                if(timeCount >= 4){
                    timeCount = 0;
                }
            }
        }, 1000*60*15, 1000*60*15);

        //高频定时任务
        timer[1] = new Timer("TimeCounter_hiRate_" + radarId);
        timer[1].schedule(new TimerTask() {
            @Override
            public void run() {
                ++timeCountHiRate;

                //TODO:5秒钟 急加速车辆统计、急减速车辆统计、急变道车辆统计


                //TODO:30秒钟 车辆速度方差
                if(timeCountHiRate == 6){

                }


                //归零
                if(timeCount >= 6){//30秒（6次）
                    timeCount = 0;
                }
            }
        }, 1000 * 5, 1000 * 5);

        //雷达数据快照
        radar_photo();

    }

    /**
     * 雷达快照截取
     */
    private void radar_photo(){
        //1秒截取一次雷达数据快照
        timer_photo = new Timer("TimeCounter_hiRate_1s_" + radarId);
        timer_photo.schedule(new TimerTask() {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

            //雷达扫描有效范围
            double rangeSize = radarEventDataGrab.getRANGE_SIZE()*1.0;

            //L_zcLdRadarFrame R_zcLdRadarFrame zcLdRadarFrameLanes zcLdRadarFrameCars
            List<ZcLdRadarFrame> listZcLdRadarFrame = new ArrayList<>();
            List<ZcLdRadarFrameLane> listZcLdRadarFrameLane = new ArrayList<>();
            List<ZcLdRadarFrameCar> listZcLdRadarFrameCar = new ArrayList<>();

            @Override
            public void run() {
                //获取雷达数据快照
                if(radarEventDataGrab != null){
                    RadarDO radarDO = radarEventDataGrab.getMomentHandleRadarDO();
                    if(radarDO != null){
                        JSONArray jsonArray = radarDO.getDataBody();
                        if(jsonArray.size() == 0){
                            return;
                        }

                        Date dateTime = radarDO.getDate();
                        String frameId = sdf.format(dateTime) + "_" + StringUtils.getUUID();

                        //待入库雷达快照计算信息
                        ZcLdRadarFrame L_zcLdRadarFrame = new ZcLdRadarFrame();
                        L_zcLdRadarFrame.setRadarId(radarId);
                        L_zcLdRadarFrame.setLaneDirection("L");
                        L_zcLdRadarFrame.setFrameId(frameId);
                        L_zcLdRadarFrame.setDateTime(dateTime);
                        ZcLdRadarFrame R_zcLdRadarFrame = new ZcLdRadarFrame();
                        R_zcLdRadarFrame.setRadarId(radarId);
                        R_zcLdRadarFrame.setLaneDirection("R");
                        R_zcLdRadarFrame.setFrameId(frameId);
                        R_zcLdRadarFrame.setDateTime(dateTime);
                        //zcLdRadarFrameMapper.insert(zcLdRadarFrame);

                        //待入库雷达车道计算信息
                        List<ZcLdRadarFrameLane> zcLdRadarFrameLanes = new ArrayList<>();
                        //按车道划分
                        Map<Integer, List<ZcLdRadarFrameCar>> laneMap = new HashMap<>();

                        //待入库车辆目标信息
                        List<ZcLdRadarFrameCar> zcLdRadarFrameCars = new ArrayList<>();

                        //解析
                        for (int j = 0; j < jsonArray.size(); j++) {
                            //数据解析
                            JSONObject obj = (JSONObject) jsonArray.get(j);
                            int targetId = obj.getInt("targetId");//目标ID
                            double sX = obj.getDouble("sX");//x坐标
                            double sY = obj.getDouble("sY");//y坐标
                            double vX = obj.getDouble("vX");//x速度
                            double vY = obj.getDouble("vY");//y速度
                            double aX = obj.getDouble("aX");//x加速度
                            double aY = obj.getDouble("aY");//y加速度
                            int laneNum = obj.getInt("laneNum");//车道号
                            //1 小型车，2 大型 车，3 超大型车
                            int carType = obj.getInt("carType");//车辆类型
                            //0 无事件 1 车辆停止 2 车辆反方向行驶 （逆行） 3 车辆超速行驶 5 车辆实线变道
                            //101 车辆停止消失 102 车辆反方向行 驶消失 103 车辆超速行驶 消失105 车辆实线变道 消失
                            int event = obj.getInt("event");//事件类型
                            int carLength = obj.getInt("carLength");//车辆长度
                            ZcLdRadarFrameCar zcLdRadarFrameCar = new ZcLdRadarFrameCar();
                            zcLdRadarFrameCar.setCarId(StringUtils.getUUID());
                            zcLdRadarFrameCar.setRadarId(radarId);
                            zcLdRadarFrameCar.setFrameId(frameId);
                            zcLdRadarFrameCar.setTargetId(targetId);
                            zcLdRadarFrameCar.setSX(sX);
                            zcLdRadarFrameCar.setSY(sY);
                            zcLdRadarFrameCar.setVX(vX);
                            zcLdRadarFrameCar.setVY(vY);
                            zcLdRadarFrameCar.setAX(aX);
                            zcLdRadarFrameCar.setAY(aY);
                            zcLdRadarFrameCar.setLaneNum(laneNum);
                            zcLdRadarFrameCar.setCarType(carType);
                            zcLdRadarFrameCar.setEvent(event);
                            zcLdRadarFrameCar.setCarLength(carLength);
                            zcLdRadarFrameCar.setDateTime(dateTime);
                            //zcLdRadarFrameCarMapper.insert(zcLdRadarFrameCar);
                            zcLdRadarFrameCars.add(zcLdRadarFrameCar);

                            //解析雷达数据，按车道区分
                            if(laneMap.get(laneNum) == null){
                                List<ZcLdRadarFrameCar> list = new ArrayList<>();
                                list.add(zcLdRadarFrameCar);
                                laneMap.put(laneNum, list);
                            } else {
                                laneMap.get(laneNum).add(zcLdRadarFrameCar);
                            }
                        }

                        //计算车道
                        for(Map.Entry<Integer, List<ZcLdRadarFrameCar>> m: laneMap.entrySet()){
                            //车道号
                            int key = m.getKey();
                            //每个车道的车辆数据
                            List<ZcLdRadarFrameCar> valueList = m.getValue();
                            //排序【目的：计算平均时距】
                            Collections.sort(valueList);
                            //速度值累计
                            double countV = 0;
                            //第一辆车的Y坐标
                            double firstY = 0;
                            //累计总时距
                            double countY_timeDistance = 0;

                            //小型车计数
                            double smallCarCount = 0;
                            //中型车计数
                            double mediumTruckCount = 0;
                            //大型车计数
                            double lagerTruckCount = 0;
                            //求和离散度1：用速度与平均速度差计算【抽样方差（N）、标准方差（N-1）】
                            double discreteAvgCount = 0;
                            //求和离散度2：用前后车速度差计算
                            double discreteNeighborCount = 0;
                            //第一辆车速度
                            double firstV = 0;
                            //车道占有长度
                            double laneRatioLength = 0;

                            for(ZcLdRadarFrameCar z:valueList){
                                countV += Math.abs(z.getVY());
                                if(firstY == 0){
                                    firstY = Math.abs(z.getSY());
                                    firstV = Math.abs(z.getVY());
                                } else {
                                    countY_timeDistance += (Math.abs(z.getSY()) - firstY)/firstY*3.6;//把km/h转换成m/s
                                    firstY = Math.abs(z.getSY());
                                    discreteNeighborCount += Math.abs(Math.abs(z.getVY()) - firstV);
                                }
                                //小型车计数 1小型车，2大型车，3中型车
                                if(z.getCarType() == 1){
                                    smallCarCount ++;
                                } else if (z.getCarType() == 2) {
                                    lagerTruckCount ++;
                                } else if (z.getCarType() == 3) {
                                    mediumTruckCount ++;
                                }
                                laneRatioLength += z.getCarLength();
                            }

                            //计算：平均速度
                            double avg_speed = countV/valueList.size();

                            //计算速度差的平方和
                            for(ZcLdRadarFrameCar z:valueList){
                                discreteAvgCount += Math.pow(Math.abs(z.getVY()) - avg_speed, 2);
                            }

                            //计算：平均时距
                            double avgTimeDistance = parseDouble(countY_timeDistance/(valueList.size() - 1));
                            //计算：计算车流密度
                            double Ro = (smallCarCount + mediumTruckCount * 1.5 + lagerTruckCount * 2)/rangeSize*1000;
                            long ro = Math.round(Ro);//单位：辆/km
                            //计算：小型车占比
                            double smallCarRatio = parseDouble(smallCarCount/(smallCarCount + mediumTruckCount + lagerTruckCount));
                            //计算：中型车占比
                            double mediumTruckRatio = parseDouble(mediumTruckCount/(smallCarCount + mediumTruckCount + lagerTruckCount));
                            //计算：大型车占比
                            double lagerTruckRatio = parseDouble(lagerTruckCount/(smallCarCount + mediumTruckCount + lagerTruckCount));
                            //计算：离散度 用速度与平均速度差计算
                            double discreteAvgValue = parseDouble(Math.sqrt(discreteAvgCount/(valueList.size()-1)));
                            //计算：离散度 用前后车速度差计算
                            double discreteNeighborValue = parseDouble(discreteNeighborCount/(valueList.size()-1));
                            //计算：车道占有率
                            double laneRatio = laneRatioLength*1.0/rangeSize;


                            //车道数据【待入库对象】
                            ZcLdRadarFrameLane zcLdRadarFrameLane = new ZcLdRadarFrameLane();
                            zcLdRadarFrameLane.setRadarId(radarId);
                            zcLdRadarFrameLane.setFrameId(frameId);
                            zcLdRadarFrameLane.setLaneNum(key);
                            zcLdRadarFrameLane.setVAvg(avg_speed);
                            zcLdRadarFrameLane.setAvgTimeDistance(avgTimeDistance);
                            zcLdRadarFrameLane.setCarFlowDensity(ro);
                            zcLdRadarFrameLane.setSmallCount(smallCarCount);
                            zcLdRadarFrameLane.setMediumCount(mediumTruckCount);
                            zcLdRadarFrameLane.setLargeCount(lagerTruckCount);
                            zcLdRadarFrameLane.setSmallCountRatio(smallCarRatio);
                            zcLdRadarFrameLane.setMediumCountRatio(mediumTruckRatio);
                            zcLdRadarFrameLane.setLargeCountRatio(lagerTruckRatio);
                            zcLdRadarFrameLane.setSpeedDiscreteAvg(discreteAvgValue);
                            zcLdRadarFrameLane.setSpeedDiscreteNeighbor(discreteNeighborValue);
                            zcLdRadarFrameLane.setLaneRatio(laneRatio);
                            zcLdRadarFrameLane.setDateTime(dateTime);
                            //zcLdRadarFrameLaneMapper.insert(zcLdRadarFrameLane);
                            zcLdRadarFrameLanes.add(zcLdRadarFrameLane);
                        }
                        ////////////////////////////////////////////////////////////////////////////////////////////////////
                        ////////////////////////////////////////////////////////////////////////////////////////////////////
                        ////////////////////////////////////////////////////////////////////////////////////////////////////
                        //计算整体数值
                        //计算：平均速度
                        double L_global_avg_speed = 0;
                        double L_global_avg_speed_times = 0;
                        //计算：平均时距
                        double L_global_avg_time_distance = 0;
                        //计算：小型车占比
                        double L_global_small_car_ratio = 0;
                        //计算：中型车占比
                        double L_global_medium_trunk_ratio = 0;
                        //计算：大型车占比
                        double L_global_large_trunk_ratio = 0;
                        //计算：车流密度（辆/km）
                        double L_global_carFlowDensity = 0;
                        //计算：速度离散值
                        double L_global_speed_discrete_avg = 0;
                        //计算：车道占有率
                        double L_global_lane_ratio = 0;

                        //计算：平均速度
                        double R_global_avg_speed = 0;
                        double R_global_avg_speed_times = 0;
                        //计算：平均时距
                        double R_global_avg_time_distance = 0;
                        //计算：小型车占比
                        double R_global_small_car_ratio = 0;
                        //计算：中型车占比
                        double R_global_medium_trunk_ratio = 0;
                        //计算：大型车占比
                        double R_global_large_trunk_ratio = 0;
                        //计算：车流密度（辆/km）
                        double R_global_carFlowDensity = 0;
                        //计算：速度离散值
                        double R_global_speed_discrete_avg = 0;
                        //计算：车道占有率
                        double R_global_lane_ratio = 0;

                        for(ZcLdRadarFrameLane z:zcLdRadarFrameLanes){
                            if(L_lane.contains(z.getLaneNum())){
                                //左车道
                                L_global_avg_speed += z.getVAvg();
                                L_global_avg_time_distance += z.getAvgTimeDistance();
                                L_global_avg_speed_times ++;
                            } else if(R_lane.contains(z.getLaneNum())){
                                //右车道
                                R_global_avg_speed += z.getVAvg();
                                R_global_avg_time_distance += z.getAvgTimeDistance();
                                R_global_avg_speed_times ++;
                            }

                        }

                        L_global_avg_speed = parseDouble(L_global_avg_speed/L_global_avg_speed_times);
                        L_global_avg_time_distance = parseDouble(L_global_avg_time_distance/L_global_avg_speed_times);

                        R_global_avg_speed = parseDouble(R_global_avg_speed/R_global_avg_speed_times);
                        R_global_avg_time_distance = parseDouble(R_global_avg_time_distance/R_global_avg_speed_times);

                        double leftSmall = 0;
                        double leftMedium = 0;
                        double leftLarge = 0;
                        double rightSmall = 0;
                        double rightMedium = 0;
                        double rightLarge = 0;
                        double leftSpeedPlus = 0;
                        double rightSpeedPlus = 0;
                        double leftLengthPlus = 0;
                        double rightLengthPlus = 0;
                        for (int j = 0; j < jsonArray.size(); j++) {
                            JSONObject obj = jsonArray.getJSONObject(j);
                            double vY = obj.getDouble("vY");//y速度
                            int laneNum = obj.getInt("laneNum");//车道号
                            int carType = obj.getInt("carType");//车辆类型 1 小型车，2 大型 车，3 中型车
                            int carLength = obj.getInt("carLength");//车辆长度
                            if(L_lane.contains(laneNum)){
                                //左车道
                                if(carType == 1){
                                    leftSmall++;
                                } else if(carType == 2){
                                    leftLarge++;
                                } else if(carType == 3){
                                    leftMedium++;
                                }
                                leftSpeedPlus += Math.pow(Math.abs(vY) - L_global_avg_speed, 2);
                                leftLengthPlus += carLength;
                            } else if(R_lane.contains(laneNum)){
                                //右车道
                                if(carType == 1){
                                    rightSmall++;
                                } else if(carType == 2){
                                    rightLarge++;
                                } else if(carType == 3){
                                    rightMedium++;
                                }
                                rightSpeedPlus += Math.pow(Math.abs(vY) - R_global_avg_speed, 2);
                                rightLengthPlus += carLength;
                            }
                        }

                        L_global_small_car_ratio = parseDouble(leftSmall/(leftSmall + leftMedium + leftLarge));
                        L_global_medium_trunk_ratio = parseDouble(leftMedium/(leftSmall + leftMedium + leftLarge));
                        L_global_large_trunk_ratio = parseDouble(leftLarge/(leftSmall + leftMedium + leftLarge));
                        L_global_carFlowDensity = (leftSmall + leftMedium + leftLarge)/rangeSize*1000;//每公里车辆数
                        L_global_speed_discrete_avg = parseDouble(Math.sqrt(leftSpeedPlus/(leftSmall + leftMedium + leftLarge)));
                        L_global_lane_ratio = parseDouble(leftLengthPlus/(rangeSize*L_lane.size()));

                        R_global_small_car_ratio = parseDouble(rightSmall/(rightSmall + rightMedium + rightLarge));
                        R_global_medium_trunk_ratio = parseDouble(rightMedium/(rightSmall + rightMedium + rightLarge));
                        R_global_large_trunk_ratio = parseDouble(rightLarge/(rightSmall + rightMedium + rightLarge));
                        R_global_carFlowDensity = (rightSmall + rightMedium + rightLarge)/rangeSize*1000;//每公里车辆数
                        R_global_speed_discrete_avg = parseDouble(Math.sqrt(rightSpeedPlus/(rightSmall + rightMedium + rightLarge)));
                        R_global_lane_ratio = parseDouble(rightLengthPlus/(rangeSize*R_lane.size()));

                        L_zcLdRadarFrame.setVAvg(L_global_avg_speed);//平均速度是估值，有误差
                        L_zcLdRadarFrame.setAvgTimeDistance(L_global_avg_time_distance);//平均时距是估值，有误差
                        L_zcLdRadarFrame.setSmallCount(leftSmall);
                        L_zcLdRadarFrame.setMediumCount(leftMedium);
                        L_zcLdRadarFrame.setLargeCount(leftLarge);
                        L_zcLdRadarFrame.setSmallCountRatio(L_global_small_car_ratio);
                        L_zcLdRadarFrame.setMediumCountRatio(L_global_medium_trunk_ratio);
                        L_zcLdRadarFrame.setLargeCountRatio(L_global_large_trunk_ratio);
                        L_zcLdRadarFrame.setCarFlowDensity(L_global_carFlowDensity);
                        L_zcLdRadarFrame.setSpeedDiscreteAvg(L_global_speed_discrete_avg);
                        L_zcLdRadarFrame.setLaneRatio(L_global_lane_ratio);

                        R_zcLdRadarFrame.setVAvg(R_global_avg_speed);//平均速度是估值，有误差
                        R_zcLdRadarFrame.setAvgTimeDistance(R_global_avg_time_distance);//平均时距是估值，有误差
                        R_zcLdRadarFrame.setSmallCount(rightSmall);
                        R_zcLdRadarFrame.setMediumCount(rightMedium);
                        R_zcLdRadarFrame.setLargeCount(rightLarge);
                        R_zcLdRadarFrame.setSmallCountRatio(R_global_small_car_ratio);
                        R_zcLdRadarFrame.setMediumCountRatio(R_global_medium_trunk_ratio);
                        R_zcLdRadarFrame.setLargeCountRatio(R_global_large_trunk_ratio);
                        R_zcLdRadarFrame.setCarFlowDensity(R_global_carFlowDensity);
                        R_zcLdRadarFrame.setSpeedDiscreteAvg(R_global_speed_discrete_avg);
                        R_zcLdRadarFrame.setLaneRatio(R_global_lane_ratio);

                        /**入库操作*/
                        //////////////////////////////////////
                        //////////////////////////////////////
                        //////////////////////////////////////
                        try{
                            //L_zcLdRadarFrame R_zcLdRadarFrame zcLdRadarFrameLanes zcLdRadarFrameCars
                            if(zcLdRadarFrameLanes.size() > 0 && zcLdRadarFrameCars.size() > 0){
                                listZcLdRadarFrame.add(L_zcLdRadarFrame);
                                listZcLdRadarFrame.add(R_zcLdRadarFrame);
                                listZcLdRadarFrameLane.addAll(zcLdRadarFrameLanes);
                                listZcLdRadarFrameCar.addAll(zcLdRadarFrameCars);

//                                frame = zcLdRadarFrameMapper.insert(L_zcLdRadarFrame);
//                                frame = zcLdRadarFrameMapper.insert(R_zcLdRadarFrame);
//                                frameLane = zcLdRadarFrameLaneMapper.insertBatch(zcLdRadarFrameLanes);
//                                frameCar = zcLdRadarFrameCarMapper.insertBatch(zcLdRadarFrameCars);
                            }
                            if(listZcLdRadarFrame.size() >= 120){
                                List<ZcLdRadarFrame> zcLdRadarFrameList = new ArrayList<>();
                                List<ZcLdRadarFrameLane> zcLdRadarFrameLaneList = new ArrayList<>();
                                List<ZcLdRadarFrameCar> zcLdRadarFrameCarList = new ArrayList<>();
                                zcLdRadarFrameList.addAll(listZcLdRadarFrame);
                                zcLdRadarFrameLaneList.addAll(listZcLdRadarFrameLane);
                                zcLdRadarFrameCarList.addAll(listZcLdRadarFrameCar);
                                Runnable runnable = new RunnableImpl(zcLdRadarFrameList, zcLdRadarFrameLaneList, zcLdRadarFrameCarList);
                                executorService.submit(runnable);
                                listZcLdRadarFrame.clear();
                                listZcLdRadarFrameLane.clear();
                                listZcLdRadarFrameCar.clear();
                            }
                        } catch (Exception e) {
                            logger.error("雷达原始数据入库异常", e);
                        }
                        //////////////////////////////////////
                        //////////////////////////////////////
                        //////////////////////////////////////
                    } else {
                        //logger.info("雷达快照截取失败，原因可能是RadarDataHandle线程停止工作");
                        //logger.info("雷达快照截取失败，原因可能是路上无车");
                    }

                } else {
                    logger.info("雷达快照截取失败，原因可能是RadarEventDataGrab对象为null");
                }
            }
        }, 1000, 1000);
    }

    private double parseDouble(double d){
        if(Double.isNaN(d)){
            return 0;
        }
        return d;
    }

    public void cancelTimers(){
        for(int i = 0; i < timer.length; i++){
            if(timer[i] != null){
                timer[i].cancel();
            }
            timer[i] = null;
        }
        if(timer_photo != null){
            timer_photo.cancel();
            timer_photo = null;
        }
    }

    /**
     * 间隔十五分钟执行一次
     */
    public abstract void taskFifteenMin();

    public abstract void clearMarkFifteenMin();

    /**
     * 间隔三十分钟执行一次
     */
    public abstract void taskThirtyMin();

    public abstract void clearMarkThirtyMin();

    /**
     * 间隔六十分钟执行一次
     */
    public abstract void taskSixtyMin();

    public abstract void clearMarkSixtyMin();

    /**
     * 获取时钟->时
     * @return
     */
    public static int getCalendarHour(){
        int r = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        return r;
    }

    /**
     * 获取时钟->分钟
     * @return
     */
    public static int getCalendarMin(){
        int r = Calendar.getInstance().get(Calendar.MINUTE);
        return r;
    }

    /**
     * 获取时钟->秒
     * @return
     */
    public static int getCalendarSec(){
        int r = Calendar.getInstance().get(Calendar.SECOND);
        return r;
    }

    class RunnableImpl implements Runnable {

        List<ZcLdRadarFrame> listZcLdRadarFrame;
        List<ZcLdRadarFrameLane> listZcLdRadarFrameLane;
        List<ZcLdRadarFrameCar> listZcLdRadarFrameCar;

        public RunnableImpl(List<ZcLdRadarFrame> listZcLdRadarFrame, List<ZcLdRadarFrameLane> listZcLdRadarFrameLane, List<ZcLdRadarFrameCar> listZcLdRadarFrameCar){
            this.listZcLdRadarFrame = listZcLdRadarFrame;
            this.listZcLdRadarFrameLane = listZcLdRadarFrameLane;
            this.listZcLdRadarFrameCar = listZcLdRadarFrameCar;
        }

        @Override
        public void run() {
            int frame, frameLane, frameCar;
            if(listZcLdRadarFrame.size() > 0){
                frame = zcLdRadarFrameMapper.insertBatch(listZcLdRadarFrame);
            }
            if(listZcLdRadarFrameLane.size() > 0){
                frameLane = zcLdRadarFrameLaneMapper.insertBatch(listZcLdRadarFrameLane);
            }
            if(listZcLdRadarFrameCar.size() > 0){
                frameCar = zcLdRadarFrameCarMapper.insertBatch(listZcLdRadarFrameCar);
            }
        }
    }

    /////////////////////////////main////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    public static void main(String[] args) throws InterruptedException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        while(true){
//            System.out.println("new Date():" + sdf.format(new Date()));
//            System.out.println("Calendar:" + sdf.format(Calendar.getInstance().getTime()));
            System.out.println(System.currentTimeMillis());
        }

//        Thread thread = new Thread(()->{
//            while (true) {
//                System.out.println("Hello");
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        thread.start();

//        float x = 0;
//        String x1 = "";
//        x1.toLowerCase();
//        Character.isUpperCase('c');
//        System.out.println(x == 0);
//
        //无追赶特性
//        Timer timer1 = new Timer();
//        timer1.schedule(new TimerTask() {
//            public void run() {
//                System.out.println("-------demo1--------");
//            }
//        }, 1000, 1000);
//
//        Thread.sleep(5000);
//        timer1.cancel();

        //追赶特性
//        Timer timer2 = new Timer();
//        timer2.scheduleAtFixedRate(new TimerTask() {
//            public void run() {
//                System.out.println("-------demo2--------");
//            }
//        }, 1000, 1000);

    }


}
