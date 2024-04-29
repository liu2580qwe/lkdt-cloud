package org.lkdt.modules.radar.supports.radarDataService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.radar.entity.ZcLdLaneInfo;
import org.lkdt.modules.radar.entity.ZcLdThreeStatusCoefficient;
import org.lkdt.modules.radar.supports.radarDataService.observers.DO.RadarObjDO;
import org.lkdt.modules.radar.supports.radarTools.DO.RadarDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;


/**
 * 雷达交通流三态识别
 * @author wy
 *
 */
@Configuration
public class ThreeStatus {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 计算周期
	 */
	private long longTime = 30000;
	
	/**
	 * 每个雷达的时间
	 */
	private Map<String,Long> ltMap = new HashMap<>();
	
	/**
	 * 计算周期内的RSD数据集合---来向
	 */
	private Map<String,List<Double>> rsdFloatMapL = new HashMap<>();
	
	/**
	 * 计算周期内的RSD数据集合---去向
	 */
	private Map<String,List<Double>> rsdFloatMapR = new HashMap<>();
	
	/**
	 * 计算周期内的平均速度数据集合---来向
	 */
	private Map<String,List<Double>> vFloatMapL = new HashMap<>();
	
	/**
	 * 计算周期内的平均速度数据集合---去向
	 */
	private Map<String,List<Double>> vFloatMapR = new HashMap<>();
	
	/**
	 * 计算周期内的P数据集合---来向
	 */
	private Map<String,List<Double>> pFloatMapL = new HashMap<>();
	
	/**
	 * 计算周期内的P数据集合---去向
	 */
	private Map<String,List<Double>> pFloatMapR = new HashMap<>();
	
	/**
	 * 雷达交通流三态
	 * @param radarDO 每一帧的数据
	 * @param radarId
	 */
	public Map<String,Object> radarThreeStatus(RadarDO radarDO ,String radarId) {
		Map<String,Object> map = new HashMap<>();
		if(ltMap.get(radarId) == null) {
			ltMap.put(radarId, radarDO.getDate().getTime());
		}
		
		List<Double> rsdFloatsL = new ArrayList<>();	//RSD来向集合
		List<Double> rsdFloatsR = new ArrayList<>();	//RSD去向集合
		
		List<Double> vFloatsL = new ArrayList<>();	//RSD来向集合
		List<Double> vFloatsR = new ArrayList<>();	//RSD去向集合
		
		List<Double> pFloatsL = new ArrayList<>();	//P来向集合
		List<Double> pFloatsR = new ArrayList<>();	//P去向集合
		
		if(radarDO != null && radarDO.getDataBody() != null && radarDO.getDataBody().size() > 0) {
			Double rsdL = null;
			Double rsdR = null;
			Double vL = null;
			Double vR = null;
			Double pL = null;
			Double pR = null;
			
			
			//解析瞬时数据
            List<RadarObjDO> radarObjDOList = laneLiveData(radarDO);

            //风险性车速离散度
            Map<String,Double> rsdMap = RSD(radarObjDOList ,radarId);	//一帧RSD
            if(rsdMap.get(radarId+"_R") != null) {
            	if(rsdFloatMapR.get(radarId) != null) {
            		rsdFloatsR = rsdFloatMapR.get(radarId);
            	}
            	if(rsdMap.get(radarId+"_R") > 0) {
            		rsdFloatsR.add(rsdMap.get(radarId+"_R"));
            		rsdR = rsdMap.get(radarId+"_R");
            	}
            	rsdFloatMapR.put(radarId, rsdFloatsR);
            }
            if(rsdMap.get(radarId+"_L") != null) {
            	if(rsdFloatMapL.get(radarId) != null) {
            		rsdFloatsL = rsdFloatMapL.get(radarId);
            	}
            	if(rsdMap.get(radarId+"_L") > 0) {
            		rsdFloatsL.add(rsdMap.get(radarId+"_L"));
            		rsdL = rsdMap.get(radarId+"_L");
            	}
            	rsdFloatMapL.put(radarId, rsdFloatsL);
            }
            
            //平均速度
            Map<String,Double> vMap = avgV(radarObjDOList ,radarId);	//一帧平均速度
            if(vMap.get(radarId+"_R") != null) {
            	if(vFloatMapR.get(radarId) != null) {
            		vFloatsR = vFloatMapR.get(radarId);
            	}
            	if(vMap.get(radarId+"_R") > 0) {
            		vFloatsR.add(vMap.get(radarId+"_R"));
            		vR = vMap.get(radarId+"_R");
            	}
            	vFloatMapR.put(radarId, vFloatsR);
            }
            if(vMap.get(radarId+"_L") != null) {
            	if(vFloatMapL.get(radarId) != null) {
            		vFloatsL = vFloatMapL.get(radarId);
            	}
            	if(vMap.get(radarId+"_L") > 0) {
            		vFloatsL.add(vMap.get(radarId+"_L"));
            		vL = vMap.get(radarId+"_L");
            	}
            	vFloatMapL.put(radarId, vFloatsL);
            }
            
            //通行速度指数
            Map<String,Double> pMap = P(radarObjDOList ,radarId);	//一帧P
            if(pMap.get(radarId+"_R") != null) {
            	if(pFloatMapR.get(radarId) != null) {
            		pFloatsR = pFloatMapR.get(radarId);
            	}
            	if(pMap.get(radarId+"_R") > 0) {
            		pFloatsR.add(pMap.get(radarId+"_R"));
            		pR = pMap.get(radarId+"_R");
            	}
            	pFloatMapR.put(radarId, pFloatsR);
            }
            if(pMap.get(radarId+"_L") != null) {
            	if(pFloatMapL.get(radarId) != null) {
            		pFloatsL = pFloatMapL.get(radarId);
            	}
            	if(pMap.get(radarId+"_L") > 0) {
            		pFloatsL.add(pMap.get(radarId+"_L"));
            		pL = pMap.get(radarId+"_L");
            	}
            	pFloatMapL.put(radarId, pFloatsL);
            }
            
            //每一帧来去向三态系数list
            List<ZcLdThreeStatusCoefficient> list = getCoefficientList(rsdL, rsdR, vL, vR, pL, pR, radarDO, radarId);
            map.put("ENTITY_LIST", list);	//每一帧结果
		}
		
		List<ZcLdThreeStatusCoefficient> list = null;
		//据以半分钟为周期取平均值
		if((radarDO.getDate().getTime() - ltMap.get(radarId)) >= longTime) {
			rsdFloatMapL.remove(radarId);
			rsdFloatMapR.remove(radarId);
			vFloatMapL.remove(radarId);
			vFloatMapR.remove(radarId);
			pFloatMapL.remove(radarId);
			pFloatMapR.remove(radarId);
			
			logger.info("雷达："+radarId+"；时间差："+(radarDO.getDate().getTime() - ltMap.get(radarId))+"；来向数量："+rsdFloatsL.size()+"；去向数量："+rsdFloatsR.size());
			
			
			ZcLdThreeStatusCoefficient zcLdThreeStatusCoefficientL = null;
			ZcLdThreeStatusCoefficient zcLdThreeStatusCoefficientR = null;
			
			//来向车道
			if(!rsdFloatsL.isEmpty() || !vFloatsL.isEmpty() || !pFloatsL.isEmpty() ) {
				if(zcLdThreeStatusCoefficientL == null) {
					zcLdThreeStatusCoefficientL = new ZcLdThreeStatusCoefficient();
					zcLdThreeStatusCoefficientL.setDataTime(radarDO.getDate());
					zcLdThreeStatusCoefficientL.setDirection("L");
					zcLdThreeStatusCoefficientL.setRadarId(radarId);
				}
				if(!rsdFloatsL.isEmpty()) {
					double rsd = rsdFloatsL.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
					zcLdThreeStatusCoefficientL.setCoefficientRsd(rsd);
				}
				if(!vFloatsL.isEmpty()) {
					double avgV = vFloatsL.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
					zcLdThreeStatusCoefficientL.setCoefficientV(avgV);
				}
				if(!rsdFloatsL.isEmpty() && !vFloatsL.isEmpty()) {
					double rsd = rsdFloatsL.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
					double avgV = vFloatsL.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
					double g = rsd / avgV ;
					zcLdThreeStatusCoefficientL.setCoefficientG(g);
				}
				if(!pFloatsL.isEmpty()) {
					double p = pFloatsL.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
					zcLdThreeStatusCoefficientL.setCoefficientP(p);
				}
			}
			//去向车道
			if(!rsdFloatsR.isEmpty() || !vFloatsR.isEmpty() || !pFloatsR.isEmpty()) {
				if(zcLdThreeStatusCoefficientR == null) {
					zcLdThreeStatusCoefficientR = new ZcLdThreeStatusCoefficient();
					zcLdThreeStatusCoefficientR.setDataTime(radarDO.getDate());
					zcLdThreeStatusCoefficientR.setDirection("R");
					zcLdThreeStatusCoefficientR.setRadarId(radarId);
				}
				if(!rsdFloatsR.isEmpty()) {
					double rsd = rsdFloatsR.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
					zcLdThreeStatusCoefficientR.setCoefficientRsd(rsd);
				}
				if(!vFloatsR.isEmpty()) {
					double avgV = vFloatsR.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
					zcLdThreeStatusCoefficientR.setCoefficientV(avgV);
				}
				if(!rsdFloatsR.isEmpty() && !vFloatsR.isEmpty()) {
					double rsd = rsdFloatsR.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
					double avgV = vFloatsR.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
					double g = rsd / avgV ;
					zcLdThreeStatusCoefficientR.setCoefficientG(g);
				}
				if(!pFloatsR.isEmpty()) {
					double p = pFloatsR.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
					zcLdThreeStatusCoefficientR.setCoefficientP(p);
				}
			}
			
			list = new ArrayList<>();
			
			//来向
			if(zcLdThreeStatusCoefficientL != null) {
				list.add(zcLdThreeStatusCoefficientL);
			}
			//去向
			if(zcLdThreeStatusCoefficientR != null) {
				list.add(zcLdThreeStatusCoefficientR);
			}
			
			ltMap.remove(radarId);
			map.put("LIST30", list);	//30秒周期结果
		}
		
		return map;
	}
	
	/**
	 * 计算每一帧的RSD：风险性车速离散度
	 * @param radarObjDOList
	 * @return
	 */
	public Map<String,Double> RSD(List<RadarObjDO> radarObjDOList,String radarId) {
		Map<String,Double> map = new HashMap<>();
		
		//按车道方向细分划分
        Map<String, Map<Integer, List<RadarObjDO>>> directionMap = new HashMap<>();
        
        //数据处理
        for(RadarObjDO radarObjDO: radarObjDOList){
        	//0速度不计算
        	if(Math.abs(radarObjDO.getvY()) == 0) {
        		continue;
        	}
        	ZcLdLaneInfo lane = TwoSpeedThreeHurried.radarLaneInfoMap.get(radarId+"_"+radarObjDO.getLaneNum()) ;
    		//车道不存在，或者是绿化车道，则不处理
    		if(lane != null && !StringUtils.equals(lane.getLaneRoad(), "0")) {
    			//车道方向
    			String direction = lane.getLaneRoad().substring(0,1);
    			//解析雷达数据，按车道方向区分
                if(directionMap.get(direction) == null){
                	List<RadarObjDO> list = new ArrayList<>();
                    list.add(radarObjDO);
                    //按车道细分划分
                    Map<Integer, List<RadarObjDO>> laneMap = new TreeMap<>((Integer o1, Integer o2) -> o2 - o1);
                    laneMap.put(radarObjDO.getLaneNum(), list);
                    directionMap.put(direction, laneMap);
                } else {
                	//按车道细分划分
                    Map<Integer, List<RadarObjDO>> laneMap = new TreeMap<>((Integer o1, Integer o2) -> o2 - o1);
                    laneMap = directionMap.get(direction);
                	//解析雷达数据，按车道区分
                    if(laneMap.get(radarObjDO.getLaneNum()) == null){
                        List<RadarObjDO> list = new ArrayList<>();
                        list.add(radarObjDO);
                        laneMap.put(radarObjDO.getLaneNum(), list);
                        directionMap.put(direction, laneMap);
                    } else {
                        laneMap.get(radarObjDO.getLaneNum()).add(radarObjDO);
                        directionMap.put(direction, laneMap);
                    }
                }
    		}
        }


        for(Map.Entry<String, Map<Integer, List<RadarObjDO>>> m1: directionMap.entrySet()){
        	//当前路段离散速度差集合
            List<Float> roadDiscreteDifferList = new ArrayList<>();
        	double discreteValue = 0.0;
    		Map<Integer, List<RadarObjDO>> laneMap = new TreeMap<>((Integer o1, Integer o2) -> o1 - o2);
    		laneMap = m1.getValue();
    		
    		byte laneFlag = 0;
            //上一车道数据
            List<RadarObjDO> previewRadarObjDOs = null;
    		//计算车道
            for(Map.Entry<Integer, List<RadarObjDO>> m: laneMap.entrySet()){
                if(laneFlag == 0){
                    laneFlag = 1;
                    previewRadarObjDOs = m.getValue();
                    //排序preview
                    Collections.sort(previewRadarObjDOs, (o1, o2) -> o1.getsY() - o2.getsY() > 0? 1: o1.getsY() - o2.getsY() == 0? 0: -1);
                    continue;
                }
                //当前车道数据
                List<RadarObjDO> currentRadarObjDOs = m.getValue();
                //排序current
                Collections.sort(currentRadarObjDOs, (o1, o2) -> o1.getsY() - o2.getsY() > 0? 1: o1.getsY() - o2.getsY() == 0? 0: -1);
                //合并两车道数据
                List<RadarObjDO> mergeTwoLaneRadarObjDOs = new ArrayList<>();
                mergeTwoLaneRadarObjDOs.addAll(previewRadarObjDOs);
                mergeTwoLaneRadarObjDOs.addAll(currentRadarObjDOs);
                //排序mergeTwoLane
                Collections.sort(mergeTwoLaneRadarObjDOs, (o1, o2) -> o1.getsY() - o2.getsY() > 0? 1: o1.getsY() - o2.getsY() == 0? 0: -1);

                //上一车道速度差
                List<Float> previewLaneDiffers = new ArrayList<>();
                //当前车道速度差
                List<Float> currentLaneDiffers = new ArrayList<>();
                //合并车道速度差
                List<Float> mergeTwoLaneDiffers = new ArrayList<>();

                calcLaneDiffer(previewRadarObjDOs, previewLaneDiffers, true);

                calcLaneDiffer(currentRadarObjDOs, currentLaneDiffers, true);

                calcLaneDiffer(mergeTwoLaneRadarObjDOs, mergeTwoLaneDiffers, false);

                if(!roadDiscreteDifferList.containsAll(previewLaneDiffers)){
                    roadDiscreteDifferList.addAll(previewLaneDiffers);
                }
                if(!roadDiscreteDifferList.containsAll(currentLaneDiffers)){
                    roadDiscreteDifferList.addAll(currentLaneDiffers);
                }
                if(!roadDiscreteDifferList.containsAll(mergeTwoLaneDiffers)){
                    roadDiscreteDifferList.addAll(mergeTwoLaneDiffers);
                }

                //最后
                previewRadarObjDOs = m.getValue();
            }
            if(laneMap.size() == 1) {
            	//上一车道速度差
                List<Float> previewLaneDiffers = new ArrayList<>();
                calcLaneDiffer(previewRadarObjDOs, previewLaneDiffers,true);
                if(!roadDiscreteDifferList.containsAll(previewLaneDiffers)){
                    roadDiscreteDifferList.addAll(previewLaneDiffers);
                }
            }
            
            try {
                if(roadDiscreteDifferList.size() > 0){
                    discreteValue = roadDiscreteDifferList.stream().mapToDouble(Float::floatValue).average().getAsDouble();
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("计算每一帧的RSD：风险性车速离散度异常："+e);
            }
            
            map.put(radarId+"_"+m1.getKey(), discreteValue);
        }
        
		
		return map;
	}
	
	/**
	 * 计算每一帧的V：平均速度
	 * @param radarObjDOList
	 * @param radarId
	 * @return
	 */
	public Map<String,Double> avgV(List<RadarObjDO> radarObjDOList,String radarId) {
		Map<String,Double> map = new HashMap<>();
        
        try {
        	//来向速度集合
    		List<Float> vyListL = new ArrayList<>();
    		//去向速度集合
    		List<Float> vyListR = new ArrayList<>();

            //数据处理
            for(RadarObjDO radarObjDO: radarObjDOList){
            	//0速度不计算
            	if(Math.abs(radarObjDO.getvY()) == 0) {
            		continue;
            	}
            	ZcLdLaneInfo lane = TwoSpeedThreeHurried.radarLaneInfoMap.get(radarId+"_"+radarObjDO.getLaneNum()) ;
        		//车道不存在，或者是绿化车道，则不处理
        		if(lane != null && !StringUtils.equals(lane.getLaneRoad(), "0")) {
        			//车道方向
        			String direction = lane.getLaneRoad().substring(0,1);
        			if(StringUtils.equals(direction, "L")) {
        				vyListL.add(Math.abs(radarObjDO.getvY()));
        			}else if(StringUtils.equals(direction, "R")) {
        				vyListR.add(Math.abs(radarObjDO.getvY()));
        			}
        		}
            }
            
        	if(vyListL.size() > 0) {
            	map.put(radarId+"_L", vyListL.stream().mapToDouble(Float::floatValue).average().getAsDouble());
            }
        	if(vyListR.size() > 0) {
        		map.put(radarId+"_R", vyListR.stream().mapToDouble(Float::floatValue).average().getAsDouble());
        	}
		} catch (Exception e) {
			e.printStackTrace();
            logger.error("计算每一帧的V：平均速度异常："+e);
		}
        
		return map;
	}
	
	/**
	 * 计算每一帧的P：通行速度指数
	 * @param radarObjDOList
	 * @return
	 */
	public Map<String,Double> P(List<RadarObjDO> radarObjDOList,String radarId) {
		Map<String,Double> map = new HashMap<>();
		double p = 0.0;
		
		//按车道方向细分划分
        Map<String, List<RadarObjDO>> directionMap = new HashMap<>();

        //数据处理
        for(RadarObjDO radarObjDO: radarObjDOList){
        	//0速度不计算
        	if(Math.abs(radarObjDO.getvY()) == 0) {
        		continue;
        	}
        	ZcLdLaneInfo lane = TwoSpeedThreeHurried.radarLaneInfoMap.get(radarId+"_"+radarObjDO.getLaneNum()) ;
    		//车道不存在，或者是绿化车道，则不处理
    		if(lane != null && !StringUtils.equals(lane.getLaneRoad(), "0")) {
    			//车道方向
    			String direction = lane.getLaneRoad().substring(0,1);
    			//解析雷达数据，按车道方向区分
                if(directionMap.get(direction) == null){
                    List<RadarObjDO> list = new ArrayList<>();
                    list.add(radarObjDO);
                    directionMap.put(direction, list);
                } else {
                	directionMap.get(direction).add(radarObjDO);
                }
    		}
        }
        
        for(Map.Entry<String, List<RadarObjDO>> m: directionMap.entrySet()){
        	p = dp(m.getValue()); 
        	map.put(radarId+"_"+m.getKey(), p);
        }
        
		return map;
	}
	
	/**
     * 计算车道级速度差
     * @param previewRadarObjDOs
     * @param previewLaneDiffers
     */
    public static void calcLaneDiffer(List<RadarObjDO> previewRadarObjDOs, List<Float> previewLaneDiffers, boolean isEquLane){
        byte previewFlag = 0;
        RadarObjDO previewRadarObjDO = null;
        for(RadarObjDO radarObjDO: previewRadarObjDOs){
            if(previewFlag == 0){
                previewFlag = 1;
                previewRadarObjDO = radarObjDO;
                continue;
            }
            RadarObjDO currentRadarObjDO = radarObjDO;
            
            if(isEquLane) {	//通车道
            	//同车道 120m 以内最近的相邻车的车速差
                if(Math.abs(previewRadarObjDO.getsY() - currentRadarObjDO.getsY()) <= 120) {
                	float differ = Math.abs(Math.abs(previewRadarObjDO.getvY()) - Math.abs(currentRadarObjDO.getvY()));
                    previewLaneDiffers.add(differ);
                }
            }else {		//不同车道
            	if(previewRadarObjDO.getLaneNum() != currentRadarObjDO.getLaneNum()) {
            		//不同车道 120m 以内最近的相邻车的车速差
                    if(Math.abs(previewRadarObjDO.getsY() - currentRadarObjDO.getsY()) <= 120) {
                    	float differ = Math.abs(Math.abs(previewRadarObjDO.getvY()) - Math.abs(currentRadarObjDO.getvY()));
                        previewLaneDiffers.add(differ);
                    }
            	}
            }
            
            //最后赋值
            previewRadarObjDO = currentRadarObjDO;
        }
    }
	
    /**
     * 解析车道瞬时数据
     */
    public static List<RadarObjDO> laneLiveData(RadarDO radarDO){
        JSONArray jsonArray = radarDO.getDataBody();
        List<RadarObjDO> radarObjDOList = new ArrayList<>();
        for(int i = 0; i < jsonArray.size(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            //每辆车解析
            RadarObjDO radarObjDO = new RadarObjDO();
            readData(radarObjDO, jsonObject);
            radarObjDOList.add(radarObjDO);
        }
        return radarObjDOList;
    }
    
    protected static void readData(RadarObjDO radarObjDO, JSONObject obj){
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
    
    /**
     * 计算P值
     * @param radarDoList
     * @return
     */
    public double dp(List<RadarObjDO> radarDoList) {
    	float vLimitSmall = 120;	//小车限速
        float vLimitBig = 90;		//大车限速
    	double mBigSum = 0.0;	//每一帧P大之和
    	double nSmallSum = 0.0;	//每一帧P小之和
    	List<Float> vBigList = new ArrayList<>();
        List<Float> vSmallList = new ArrayList<>();
    	
    	for (RadarObjDO radarObjDO : radarDoList) {
    		//1小型车，2大型车，3中型车
            int carType = radarObjDO.getCarType();
            float vY = Math.abs(radarObjDO.getvY());
            if(carType == 1){
                vSmallList.add(vY);
                float vY_ = vY/vLimitSmall;
                nSmallSum += vY_;
            } else {
                vBigList.add(vY);
                float vY_ = vY/vLimitBig;
                mBigSum += vY_;
            }
		}
    	
    	double p = 0.0;
    	if((vBigList.size() + vSmallList.size()) == 0) {
    		p = 1.0;
    	}else {
    		p = (mBigSum + nSmallSum) / (vBigList.size() + vSmallList.size()) ;
    	}
    	
    	return p;
    }
    
    /**
     * 每一帧来去向 三态系数对象
     * @param rsdL
     * @param rsdR
     * @param vL
     * @param vR
     * @param pL
     * @param pR
     * @return
     */
    private List<ZcLdThreeStatusCoefficient> getCoefficientList(Double rsdL, Double rsdR, Double vL, Double  vR, Double  pL, Double pR ,RadarDO radarDO ,String radarId){
    	List<ZcLdThreeStatusCoefficient> list = null ;
    	
    	ZcLdThreeStatusCoefficient zcLdThreeStatusCoefficientL = null;
		ZcLdThreeStatusCoefficient zcLdThreeStatusCoefficientR = null;
		
		if(rsdL != null || vL != null || pL != null ) {
			if(zcLdThreeStatusCoefficientL == null) {
				zcLdThreeStatusCoefficientL = new ZcLdThreeStatusCoefficient();
				zcLdThreeStatusCoefficientL.setDataTime(radarDO.getDate());
				zcLdThreeStatusCoefficientL.setDirection("L");
				zcLdThreeStatusCoefficientL.setRadarId(radarId);
			}
			if(rsdL != null) {
				zcLdThreeStatusCoefficientL.setCoefficientRsd(rsdL);
			}
			if(vL != null) {
				zcLdThreeStatusCoefficientL.setCoefficientV(vL);
			}
			if(rsdL != null && vL != null) {
				double g = rsdL / vL ;
				zcLdThreeStatusCoefficientL.setCoefficientG(g);
			}
			if(pL != null) {
				zcLdThreeStatusCoefficientL.setCoefficientP(pL);
			}
		}
		if(rsdR != null || vR != null || pR != null) {
			if(zcLdThreeStatusCoefficientR == null) {
				zcLdThreeStatusCoefficientR = new ZcLdThreeStatusCoefficient();
				zcLdThreeStatusCoefficientR.setDataTime(radarDO.getDate());
				zcLdThreeStatusCoefficientR.setDirection("R");
				zcLdThreeStatusCoefficientR.setRadarId(radarId);
			}
			if(rsdR != null) {
				zcLdThreeStatusCoefficientR.setCoefficientRsd(rsdR);
			}
			if(vR != null) {
				zcLdThreeStatusCoefficientR.setCoefficientV(vR);
			}
			if(rsdR != null && vR != null) {
				double g = rsdR / vR ;
				zcLdThreeStatusCoefficientR.setCoefficientG(g);
			}
			if(pR != null) {
				zcLdThreeStatusCoefficientR.setCoefficientP(pR);
			}
		}
		
		list = new ArrayList<>();
		
		//来向
		if(zcLdThreeStatusCoefficientL != null) {
			list.add(zcLdThreeStatusCoefficientL);
		}
		//去向
		if(zcLdThreeStatusCoefficientR != null) {
			list.add(zcLdThreeStatusCoefficientR);
		}
    	
    	return list;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public static final String url = "jdbc:mysql://192.168.3.100:3306/lkdt-radar-test?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false" +
            "&zeroDateTimeconvertToNull&useCursorFetch=true&defaultFetchSize=1000";

    public static final String user = "root";
    public static final String password = "root";
    
    public static void main(String[] args) {
    	ZcLdLaneInfo z1 = new ZcLdLaneInfo();
    	z1.setLaneRadar(1);
    	z1.setLaneRoad("L-YJ");
    	TwoSpeedThreeHurried.radarLaneInfoMap.put("1001_1", z1);
    	
    	ZcLdLaneInfo z2 = new ZcLdLaneInfo();
    	z2.setLaneRadar(2);
    	z2.setLaneRoad("L-2");
    	TwoSpeedThreeHurried.radarLaneInfoMap.put("1001_2", z2);
    	
    	ZcLdLaneInfo z3 = new ZcLdLaneInfo();
    	z3.setLaneRadar(3);
    	z3.setLaneRoad("L-C");
    	TwoSpeedThreeHurried.radarLaneInfoMap.put("1001_3", z3);
    	
    	ZcLdLaneInfo z4 = new ZcLdLaneInfo();
    	z4.setLaneRadar(4);
    	z4.setLaneRoad("0");
    	TwoSpeedThreeHurried.radarLaneInfoMap.put("1001_4", z4);
    	
    	ZcLdLaneInfo z5 = new ZcLdLaneInfo();
    	z5.setLaneRadar(5);
    	z5.setLaneRoad("R-C");
    	TwoSpeedThreeHurried.radarLaneInfoMap.put("1001_5", z5);
    	
    	ZcLdLaneInfo z6 = new ZcLdLaneInfo();
    	z6.setLaneRadar(6);
    	z6.setLaneRoad("R-2");
    	TwoSpeedThreeHurried.radarLaneInfoMap.put("1001_6", z6);
    	
    	ZcLdLaneInfo z7 = new ZcLdLaneInfo();
    	z7.setLaneRadar(7);
    	z7.setLaneRoad("R-YJ");
    	TwoSpeedThreeHurried.radarLaneInfoMap.put("1001_7", z7);
    	
    	ThreeStatus t = new ThreeStatus();
    	for(int i = 0 ; i< 2; i++) {
    		Connection connection = null ;
            PreparedStatement preparedStatement = null ;

//            String sql = "select * from zc_ld_radar_frame_target_all where radar_id = 1001 and date_time = '2021-09-29 11:54:53.596' ";
//            String sql = "select * from zc_ld_radar_frame_target_all where radar_id = 1001 and date_time = '2021-09-29 10:50:00.029' ";
//            String sql = "select * from zc_ld_radar_frame_target_all where radar_id = 1001 and (date_time = '2021-09-29 11:54:53.596'  or date_time = '2021-09-29 10:50:00.029') ";
            String sql = "select * from zc_ld_radar_frame_target_all where radar_id = 1001 and date_time = '2021-09-30 15:10:21.087' ";
            
            RadarDO radarDO = new RadarDO();
            JSONArray jsonArray = new JSONArray();
            String radarId = null ;
            boolean flag = true;
            try {
            	//加载注册驱动
                Class.forName("com.mysql.jdbc.Driver");
                //获取连接
                connection = DriverManager.getConnection(url, user, password);
                connection.setAutoCommit(false);
                //读数据
                preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                	String carId = resultSet.getString("car_id");
                    radarId = resultSet.getString("radar_id");
                    int targetId = resultSet.getInt("target_id");
                    double sX = resultSet.getDouble("s_x");
                    double sY = resultSet.getDouble("s_y");
                    double vX = resultSet.getDouble("v_x");
                    double vY = resultSet.getDouble("v_y");
                    double aX = resultSet.getDouble("a_x");
                    double aY = resultSet.getDouble("a_y");
                    int laneNum = resultSet.getInt("lane_num");
                    int carType = resultSet.getInt("car_type");
                    int event = resultSet.getInt("event");
                    int carLength = resultSet.getInt("car_length");
//                    Date dateTime = new Date(resultSet.getTimestamp("date_time").getTime());
                    Date dateTime = new Date();
                    long nanoSecond = resultSet.getLong("nano_second");
                    
                    if(flag) {
                    	radarDO.setDate(dateTime);
                        radarDO.setNanoSecond(nanoSecond);
                        radarDO.setTimestamp(dateTime.getTime());
                        radarDO.setYmdhms(null);
                        flag = false;
                    }
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("carId", carId);
                    jsonObject.put("radarId", radarId);
                    jsonObject.put("targetId", targetId);
                    jsonObject.put("sX", sX);
                    jsonObject.put("sY", sY);
                    jsonObject.put("vX", vX);
                    jsonObject.put("vY", vY);
                    jsonObject.put("aX", aX);
                    jsonObject.put("aY", aY);
                    jsonObject.put("laneNum", laneNum);
                    jsonObject.put("carType", carType);
                    jsonObject.put("event", event);
                    jsonObject.put("carLength", carLength);
                    jsonObject.put("carId", carId);
                    jsonObject.put("nanoSecond", nanoSecond);
                    jsonArray.add(jsonObject);
                }
                radarDO.setDataBody(jsonArray);
                
                Map<String,Object> map = t.radarThreeStatus(radarDO, radarId);
                for(Map.Entry<String, Object> m : map.entrySet()) {
                	System.out.println(m);
                }
                System.out.println("--------------------------");
                Thread.sleep(40000);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    	
    	
	}
    
}
