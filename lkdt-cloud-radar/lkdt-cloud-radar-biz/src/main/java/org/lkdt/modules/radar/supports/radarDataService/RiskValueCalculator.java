package org.lkdt.modules.radar.supports.radarDataService;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.lkdt.modules.radar.entity.ZcLdLaneInfo;
import org.lkdt.modules.radar.supports.radarDataService.dataCache.DO.RadarLaneCalcDO;
import org.lkdt.modules.radar.supports.radarDataService.observers.DO.RadarObjDO;
import org.lkdt.modules.radar.entity.RiskValues;
import org.lkdt.modules.radar.supports.radarDataService.radarCamUnion.CameraComponent;
import org.lkdt.modules.radar.supports.radarTools.DO.RadarDO;
import org.lkdt.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class RiskValueCalculator {

    static Logger logger = LoggerFactory.getLogger(RiskValueCalculator.class);

    /**
     /////////////////////////////////////////////////////////////////////////////////////////////////////
     //1、计算超车道（小车）、混合道大车和道路平均车速（单向三车道以上：左侧超车道因没有大车其平均速度可视为小车平均速度）
     /////////////////////////////////////////////////////////////////////////////////////////////////////
     */
    public static RiskValues[] calcAvg(RadarDO[] radarDOs, Set<Integer> targetIdSet, DataReader dataReader, CameraComponent cameraComponent){
        dataReader.riskValuesLai.init();
        dataReader.riskValuesQu.init();
        RiskValues[] riskValues = new RiskValues[2];
        if(dataReader.getDataReaderFlag()) {
            //按车道划分
            Map<Integer, List<RadarObjDO>> map = new HashMap<>();
            //解析雷达数据，按车道区分
            ///////////////////////start
            //////////////////////////////////////////////
            int carNumLai = 0, carNumQu = 0;
            JSONArray jsonArrays0 = radarDOs[0].getDataBody();
            JSONArray jsonArrays1 = radarDOs[1].getDataBody();
            JSONArray jsonArrays2 = radarDOs[2].getDataBody();
            int size = Math.max(jsonArrays0.size(), Math.max(jsonArrays1.size(), jsonArrays2.size()));
            for(int i = 0; i < size; i++){
                if(i < jsonArrays0.size()){
                    //每辆车解析
                    RadarObjDO radarObjDO = new RadarObjDO();
                    dataReader.readData(radarObjDO, jsonArrays0.getJSONObject(i), dataReader);

                    if(targetIdSet.contains(radarObjDO.getTargetId())){
                        if(map.get(radarObjDO.getLaneNum()) == null){
                            List<RadarObjDO> list = new ArrayList<>();
                            list.add(radarObjDO);
                            map.put(radarObjDO.getLaneNum(), list);
                        } else {
                            map.get(radarObjDO.getLaneNum()).add(radarObjDO);
                        }
                    }

                    //记录事件
                    calculateEvent(radarObjDO.getEvent(), dataReader.getRadarId(), cameraComponent);

                }
                if(i < jsonArrays1.size()){
                    //每辆车解析
                    RadarObjDO radarObjDO = new RadarObjDO();
                    dataReader.readData(radarObjDO, jsonArrays1.getJSONObject(i), dataReader);

                    if(targetIdSet.contains(radarObjDO.getTargetId())){
                        if(map.get(radarObjDO.getLaneNum()) == null){
                            List<RadarObjDO> list = new ArrayList<>();
                            list.add(radarObjDO);
                            map.put(radarObjDO.getLaneNum(), list);
                        } else {
                            map.get(radarObjDO.getLaneNum()).add(radarObjDO);
                        }
                    }

                    //记录事件
                    calculateEvent(radarObjDO.getEvent(), dataReader.getRadarId(), cameraComponent);

                }
                if(i < jsonArrays2.size()){
                    //每辆车解析
                    RadarObjDO radarObjDO = new RadarObjDO();
                    dataReader.readData(radarObjDO, jsonArrays2.getJSONObject(i), dataReader);

                    if(targetIdSet.contains(radarObjDO.getTargetId())){
                        if(map.get(radarObjDO.getLaneNum()) == null){
                            List<RadarObjDO> list = new ArrayList<>();
                            list.add(radarObjDO);
                            map.put(radarObjDO.getLaneNum(), list);
                        } else {
                            map.get(radarObjDO.getLaneNum()).add(radarObjDO);
                        }
                    }

                    String laneRoad = dataReader.radarEventDataGrab.searchZcLdLaneRoad(radarObjDO.getLaneNum());
                    if(laneRoad.startsWith("L-")){
                        carNumLai++;
                    } else if(laneRoad.startsWith("R-")){
                        carNumQu++;
                    }

                    //记录事件
                    calculateEvent(radarObjDO.getEvent(), dataReader.getRadarId(), cameraComponent);

                }
            }

//            if(carNumLai == 0 && dataReader.getRadarId().equals("1001")){
//                if(radarDOs[0] != null){
//                    System.out.println(dataReader.getRadarId() + ": " + radarDOs[0].getDataBody().toString());
//                }
//                if(radarDOs[1] != null){
//                    System.out.println(dataReader.getRadarId() + ": " + radarDOs[1].getDataBody().toString());
//                }
//                if(radarDOs[2] != null){
//                    System.out.println(dataReader.getRadarId() + ": " + radarDOs[2].getDataBody().toString());
//                }
//            }
            ///////////////////////end
            //////////////////////////////////////////////
            //速度离散度
            double wholeDiscreteValueLai = 0;
            double wholeDiscreteValueQu = 0;
            //计算每个车道的平均速度
            List<RadarLaneCalcDO> radarLaneCalcDOList = new ArrayList<>();
            for(HashMap.Entry<Integer, List<RadarObjDO>> entry: map.entrySet()){
                //计算车道平均速度
                RadarLaneCalcDO radarLaneCalcDO = new RadarLaneCalcDO();
                radarLaneCalcDO.setLaneNum(entry.getKey());
                double countV = 0;
                //小车平均速度
                double countCarV = 0;
                //大车平均速度
                double countTruckV = 0;
                //小型车计数
                double carCount = 0;
                //中型车计数
                double mediumTruckCount = 0;
                //大型车计数
                double lagerTruckCount = 0;
                //车道信息
                String laneRoad = dataReader.radarEventDataGrab.searchZcLdLaneRoad(radarLaneCalcDO.getLaneNum());
                for(RadarObjDO r:entry.getValue()){
                    countV += Math.abs(r.getvY());
                    //小型车计数  1小型车，2大型车，3中型车
                    if(r.getCarType() == 1){
                        carCount ++;
                        countCarV += Math.abs(r.getvY());
                    } else if (r.getCarType() == 2) {
                        lagerTruckCount ++;
                        countTruckV += Math.abs(r.getvY());
                    } else if (r.getCarType() == 3) {
                        mediumTruckCount ++;
                        countTruckV += Math.abs(r.getvY());
                    }

                    if(laneRoad != null){
                        //计算标准差1
                        if(laneRoad.startsWith("L-")){
                            if(r.getCarType() == 1){
                                wholeDiscreteValueLai += Math.pow(Math.abs(r.getvY()) - Math.abs(dataReader.dLai), 2);
                            }
                        } else if(laneRoad.startsWith("R-")){
                            if(r.getCarType() == 1){
                                wholeDiscreteValueQu += Math.pow(Math.abs(r.getvY()) - Math.abs(dataReader.dQu), 2);
                            }
                        }
                    }

                }
                //计算平均速度
                double v_ = entry.getValue().size() == 0? 0: countV/entry.getValue().size();
                //计算小车平均速度
                double v_car = carCount == 0? 0: countCarV/carCount;
                //计算大车平均速度
                double v_truck = (mediumTruckCount + lagerTruckCount) == 0? 0: countTruckV/(mediumTruckCount + lagerTruckCount);
                radarLaneCalcDO.setvAvg(v_);
                radarLaneCalcDO.setvAvgCar(v_car);
                radarLaneCalcDO.setvAvgTruck(v_truck);
                //计算小车个数 carCount mediumTruckCount lagerTruckCount
                radarLaneCalcDO.setCarCount(carCount);
                //计算中型车个数
                radarLaneCalcDO.setMediumTruckCount(mediumTruckCount);
                //计算大型车个数
                radarLaneCalcDO.setLagerTruckCount(lagerTruckCount);
                radarLaneCalcDO.setTotalCount(carCount + mediumTruckCount + lagerTruckCount);
                radarLaneCalcDOList.add(radarLaneCalcDO);
            }
            //计算整车道的平均速度
            double wholeLai = 0;//来向平均速度
            double wholeLaiXiao = 0;//来向小车平均速度
            double wholeLaiDa = 0;//来向大车平均速度
            double wholeQu = 0;//去向平均速度
            double wholeQuXiao = 0;//去向小车平均速度
            double wholeQuDa = 0;//去向大车平均速度
            int wholeTotalLai = 0;//计数
            int wholeTotalQu = 0;//计数
            int wholeTotalLaiXiao = 0;//来向小车数
            int wholeTotalLaiDa = 0;//来向大车数
            int wholeTotalQuXiao = 0;//去向小车数
            int wholeTotalQuDa = 0;//去向大车数
            /////////////////////////////////////////////////////////////////////////////////////
            // 智能计算道路小车速度、大车平均速度
            /////////////////////////////////////////////////////////////////////////////////////
            for(RadarLaneCalcDO radarLaneCalcDO: radarLaneCalcDOList){
                String laneRoad = dataReader.radarEventDataGrab.searchZcLdLaneRoad(radarLaneCalcDO.getLaneNum());
                if(laneRoad == null){
                    continue;
                }
                if(laneRoad.startsWith("L-")){
                    wholeTotalLai += radarLaneCalcDO.getTotalCount();
                    wholeLai += radarLaneCalcDO.getTotalCount()*radarLaneCalcDO.getvAvg();
                    wholeLaiXiao += radarLaneCalcDO.getvAvgCar()*radarLaneCalcDO.getCarCount();
                    wholeTotalLaiXiao += radarLaneCalcDO.getCarCount();
                    wholeLaiDa += radarLaneCalcDO.getvAvgTruck()*(radarLaneCalcDO.getMediumTruckCount() + radarLaneCalcDO.getLagerTruckCount());
                    wholeTotalLaiDa += radarLaneCalcDO.getMediumTruckCount() + radarLaneCalcDO.getLagerTruckCount();
                } else if(laneRoad.startsWith("R-")){
                    wholeTotalQu += radarLaneCalcDO.getTotalCount();
                    wholeQu += radarLaneCalcDO.getTotalCount()*radarLaneCalcDO.getvAvg();
                    wholeQuXiao += radarLaneCalcDO.getvAvgCar()*radarLaneCalcDO.getCarCount();
                    wholeTotalQuXiao += radarLaneCalcDO.getCarCount();
                    wholeQuDa += radarLaneCalcDO.getvAvgTruck()*(radarLaneCalcDO.getMediumTruckCount() + radarLaneCalcDO.getLagerTruckCount());
                    wholeTotalQuDa += radarLaneCalcDO.getMediumTruckCount() + radarLaneCalcDO.getLagerTruckCount();
                }
            }

            //来向车道平均速度
            wholeLai = wholeTotalLai == 0? 0: wholeLai/wholeTotalLai;
            //去向车道平均速度
            wholeQu = wholeTotalQu == 0? 0: wholeQu/wholeTotalQu;
            //来向小车平均速度
            wholeLaiXiao = wholeTotalLaiXiao == 0? 0: wholeLaiXiao/wholeTotalLaiXiao;
            //来向大车平均速度
            wholeLaiDa = wholeTotalLaiDa == 0? 0: wholeLaiDa/wholeTotalLaiDa;
            //去向小车平均速度
            wholeQuXiao = wholeTotalQuXiao == 0? 0: wholeQuXiao/wholeTotalQuXiao;
            //去向大车平均速度
            wholeQuDa = wholeTotalQuDa == 0? 0: wholeQuDa/wholeTotalQuDa;
            //来向小车速度离散度
            wholeDiscreteValueLai = wholeTotalLaiXiao == 0? 0:Math.sqrt(wholeDiscreteValueLai/wholeTotalLaiXiao);
            //去向小车速度离散度
            wholeDiscreteValueQu = wholeTotalQuXiao == 0? 0:Math.sqrt(wholeDiscreteValueQu/wholeTotalQuXiao);
            //来向小车大车平均速度差
            double deltVLai = Math.abs(wholeLaiXiao - wholeLaiDa);
            //去向小车大车平均速度差
            double deltVQu = Math.abs(wholeQuXiao - wholeQuDa);

            int riskValueLai = 0;
            int riskValueQu = 0;

            if(dataReader.isDay){
                //来向//白天
                if(wholeTotalLaiDa >= 0 && wholeTotalLaiDa < dataReader.dayCount.length){
                    if(wholeTotalLaiXiao <= dataReader.dayCount[wholeTotalLaiDa]
                            && wholeLaiXiao >= dataReader.dayCountAvg[wholeTotalLaiDa]){
                        riskValueLai = 1;
                    }
                }
                else if((wholeLaiXiao >= 100 && wholeLaiXiao <= 125 && wholeTotalLaiDa == 0)
                        || (wholeLaiDa >= 80 && wholeLaiDa <= 100 && wholeTotalLaiXiao == 0)
                        || (deltVLai >= dataReader.cLai)
                        || (wholeDiscreteValueLai <= dataReader.eLai)
                        || (deltVLai < 4)){
                    //低风险
                    riskValueLai = 1;
                }
                else if((deltVLai >= 4 && deltVLai < 16) || wholeDiscreteValueLai < 0.8*dataReader.eLai){
                    //中风险
                    riskValueLai = 2;
                }
                else if((deltVLai >= 16 && deltVLai < dataReader.cLai) || wholeDiscreteValueLai >= 0.8*dataReader.eLai){
                    //高风险
                    riskValueLai = 3;
                }
                //去向//白天
                if(wholeTotalQuDa >= 0 && wholeTotalQuDa < dataReader.dayCount.length){
                    if(wholeTotalQuXiao <= dataReader.dayCount[wholeTotalQuDa]
                            && wholeQuXiao >= dataReader.dayCountAvg[wholeTotalQuDa]){
                        riskValueQu = 1;
                    }
                }
                else if((wholeQuXiao >= 100 && wholeQuXiao <= 125 && wholeTotalQuDa == 0)
                        || (wholeQuDa >= 80 && wholeQuDa <= 100 && wholeTotalQuXiao == 0)
                        || (deltVQu >= dataReader.cQu)
                        || (wholeDiscreteValueQu <= dataReader.eQu)
                        || (deltVQu < 4)){
                    //低风险
                    riskValueQu = 1;
                }
                else if((deltVQu >= 4 && deltVQu < 16) || wholeDiscreteValueQu < 0.8*dataReader.eQu){
                    //中风险
                    riskValueQu = 2;
                }
                else if((deltVQu >= 16 && deltVQu < dataReader.cQu) || wholeDiscreteValueQu >= 0.8*dataReader.eQu){
                    //高风险
                    riskValueQu = 3;
                }
            } else {
                //来向//夜晚
                if(wholeTotalLaiDa >= 0 && wholeTotalLaiDa < dataReader.nightCount.length){
                    if(wholeTotalLaiXiao <= dataReader.nightCount[wholeTotalLaiDa]
                            && wholeLaiXiao >= dataReader.nightCountAvg[wholeTotalLaiDa]){
                        riskValueLai = 1;
                    }
                }
                else if((wholeLaiXiao >= 100 && wholeLaiXiao <= 125 && wholeTotalLaiDa == 0)
                        || (wholeLaiDa >= 80 && wholeLaiDa <= 100 && wholeTotalLaiXiao == 0)
                        || (deltVLai >= dataReader.cLai)
                        || (wholeDiscreteValueLai <= dataReader.eLai)
                        || (deltVLai < 4)){
                    //低风险
                    riskValueLai = 1;
                }
                else if((deltVLai >= 4 && deltVLai < 16) || wholeDiscreteValueLai < 0.8*dataReader.eLai){
                    //中风险
                    riskValueLai = 2;
                }
                else if((deltVLai >= 16 && deltVLai < dataReader.cLai) || wholeDiscreteValueLai >= 0.8*dataReader.eLai){
                    //高风险
                    riskValueLai = 3;
                }
                //去向//夜晚
                if(wholeTotalQuDa >= 0 && wholeTotalQuDa < dataReader.nightCount.length){
                    if(wholeTotalQuXiao <= dataReader.nightCount[wholeTotalQuDa]
                            && wholeQuXiao >= dataReader.nightCountAvg[wholeTotalQuDa]){
                        riskValueQu = 1;
                    }
                }
                else if((wholeQuXiao >= 100 && wholeQuXiao <= 125 && wholeTotalQuDa == 0)
                        || (wholeQuDa >= 80 && wholeQuDa <= 100 && wholeTotalQuXiao == 0)
                        || (deltVQu >= dataReader.cQu)
                        || (wholeDiscreteValueQu <= dataReader.eQu)
                        || (deltVQu < 4)){
                    //低风险
                    riskValueQu = 1;
                }
                else if((deltVQu >= 4 && deltVQu < 16) || wholeDiscreteValueQu < 0.8*dataReader.eQu){
                    //中风险
                    riskValueQu = 2;
                }
                else if((deltVQu >= 16 && deltVQu < dataReader.cQu) || wholeDiscreteValueQu >= 0.8*dataReader.eQu){
                    //高风险
                    riskValueQu = 3;
                }
            }

            //数据缓存刷新
            dataReader.avgSpeedLai = wholeLai;//平均速度
            dataReader.avgSpeedQu = wholeQu;//平均速度
            dataReader.carNumLai = carNumLai;//当前计数
            dataReader.carNumQu = carNumQu;//当前计数

            //来向
            dataReader.riskValuesLai.setRiskValue(riskValueLai);
            dataReader.riskValuesLai.setWhole(wholeLai);
            dataReader.riskValuesLai.setWholeXiao(wholeLaiXiao);
            dataReader.riskValuesLai.setWholeDa(wholeLaiDa);
            dataReader.riskValuesLai.setWholeDiscreteValue(wholeDiscreteValueLai);
            dataReader.riskValuesLai.setDeltV(deltVLai);
            dataReader.riskValuesLai.setDirection("L");
            riskValues[0] = dataReader.riskValuesLai;

            //去向
            dataReader.riskValuesQu.setRiskValue(riskValueQu);
            dataReader.riskValuesQu.setWhole(wholeQu);
            dataReader.riskValuesQu.setWholeXiao(wholeQuXiao);
            dataReader.riskValuesQu.setWholeDa(wholeQuDa);
            dataReader.riskValuesQu.setWholeDiscreteValue(wholeDiscreteValueQu);
            dataReader.riskValuesQu.setDeltV(deltVQu);
            dataReader.riskValuesQu.setDirection("R");
            riskValues[1] = dataReader.riskValuesQu;

        }
        return riskValues;
    }


    /**
     * 拟合后的车道
     * @param jsonObject
     * @return
     */
    public static void getPolynomialLane(JSONObject jsonObject, DataReader dataReader) {
        int ln = 0;
        try {
            double sx = jsonObject.getDouble("sX") ;
            double sy = jsonObject.getDouble("sY") ;
            int laneNum = jsonObject.getInt("laneNum");
            ZcLdLaneInfo lane = dataReader.twoSpeedThreeHurried.radarLaneInfoMap.get(dataReader.getRadarId()+"_"+laneNum) ;
            String direction = lane.getLaneRoad().substring(0,1);
            //用于拟合的车道线
            ZcLdLaneInfo lane2 = dataReader.twoSpeedThreeHurried.radarDirectionMap.get(dataReader.getRadarId()+"_"+direction) ;
            if(lane2 != null) {
            	if(lane2.getEquation() == null ) {
            		jsonObject.put("polynomialLane", laneNum);
            		return ;
            	}
            	if(lane2.getRadarInstallLaneDirection() == null) {
            		jsonObject.put("polynomialLane", laneNum);
            		return ;
            	}
            	double m = 0 ;
                double[] equation = lane2.getEquation();
                if(equation.length == 3) {
					//二次多项式
                	m = (sx - (Math.pow(sy,2) * equation[0] + sy * equation[1] + equation[2] )) / (Double.valueOf(lane2.getLaneWidth())/Double.valueOf(100));
				}
				if(equation.length == 4) {
					//三次多项式
					m = (sx - (Math.pow(sy,3) * equation[0] + Math.pow(sy,2) * equation[1] + sy * equation[2] + equation[3] )) / (Double.valueOf(lane2.getLaneWidth())/Double.valueOf(100));
				}
				if(equation.length == 5) {
					//四次多项式
					m = (sx - (Math.pow(sy,4) * equation[0] + Math.pow(sy,3) * equation[1] + Math.pow(sy,2) * equation[2] + sy * equation[3] + equation[4] )) / (Double.valueOf(lane2.getLaneWidth())/Double.valueOf(100));
				}
				if(equation.length == 6) {
					//五次多项式
					m = (sx - (Math.pow(sy,5) * equation[0] + Math.pow(sy,4) * equation[1] + Math.pow(sy,3) * equation[2] + Math.pow(sy,2) * equation[3] + sy * equation[4] + equation[5] )) / (Double.valueOf(lane2.getLaneWidth())/Double.valueOf(100));
				}
				if(m > 0) {
					ln = (int)Math.floor(m) ;
				}else {
					ln = (int)Math.ceil(m);
				}
				
				ln = lane2.getLaneRadar() + ln;
                if(ln <= 0) {
                    ln = 1;
                }
                
//                ZcLdLaneInfo lane3 = dataReader.twoSpeedThreeHurried.radarLaneInfoMap.get(dataReader.getRadarId()+"_"+ln) ;
//                if(lane3 == null || StringUtils.equals(lane3.getLaneRoad(), "0")) {
////                	ln = ln - lane2.getLaneRadar() ;
////                	if(ln <= 0) {
////                        ln = 1;
////                    }
//                	ln = laneNum;
//                }
                
            }else {
            	ln = laneNum;
            }
        } catch (NullPointerException e) {
            logger.error("getPolynomialLane", e.getMessage());
        } catch (Exception e) {
            logger.error("getPolynomialLane", e.getMessage());
        }
        jsonObject.put("polynomialLane", ln);
    }


    /**
     * 0 无事件 1 车辆停止 2 车辆反方向行驶 （逆行） 3 车辆超速行驶 5 车辆实线变道 101 车辆停止消失 102 车辆反方向行 驶消失 103 车辆超速行驶 消失
     * 105 车辆实线变道 消失暂定上面几个，后 续补充
     * @return
     */
    public static void calculateEvent(int eventType, String radarId, CameraComponent cameraComponent){
        //逆行
        if(eventType == 2){
            cameraComponent.record(radarId, "{self:2}", null);
        }
    }
}
