package org.lkdt.modules.radar.supports.radarDataService;

import java.util.HashMap;
import java.util.Map;

import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.radar.entity.ZcLdEventRadarInfo;
import org.lkdt.modules.radar.entity.ZcLdLaneInfo;
import org.lkdt.modules.radar.entity.ZcLdRiskEventManage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

/**
 * 障碍物算法
 * @author wy
 *
 */
@Configuration
public class Obstacle {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//车辆变道范围 1.8~2.2 米之间算变道
	@Value(value = "${radar.lane-change-greater}")
	private Double laneChangeGreater;
	
	//车辆变道范围 1.8~2.2 米之间算变道
	@Value(value = "${radar.lane-change-less}")
	private Double laneChangeLess;
	
	private Map<String,Integer> changeMap = new HashMap<>();
	
	private Map<String,String> idsMap = new HashMap<>();
	private Map<String,String> syMap = new HashMap<>();
	
	/**
	 * 1007：障碍物
	 * @param zcLdEventRadarInfo
	 * @return
	 */
	public ZcLdRiskEventManage obstacleServe(ZcLdEventRadarInfo zcLdEventRadarInfo){
		try {
			ZcLdRiskEventManage eventManage = null;
			boolean isResult = false;
			
			boolean isChange = false;
			int polynomialLaneLast = -99;
			String direction = StringUtils.EMPTY;
			String lastLaneRoad = StringUtils.EMPTY;
			double lastSy = 0.0;
			
			//第一个VX>2时的时间(计算变道时长)
			long firstTime = 0L;
			
			//自由态
			JSONArray array = zcLdEventRadarInfo.getJsonArraySrc();
			/********** 压线逻辑 ***************/
			for(int k = 0 ; k < array.size() ; k ++) {
				JSONObject jo = array.getJSONObject(k);
				if(jo == null) {
					continue;
				}
				int targetId = jo.getInt("targetId");
				int polynomialLane = jo.getInt("polynomialLane");	//使用拟合后的车道
				
				ZcLdLaneInfo lane = TwoSpeedThreeHurried.radarLaneInfoMap.get(zcLdEventRadarInfo.getEquId()+"_"+polynomialLane) ;
				//拟合以后的车道不存在，或者是绿化车道，则不处理
				if(lane == null || StringUtils.equals(lane.getLaneRoad(), "0")) {
					continue;
				}
				double sy = jo.getDouble("sY") ;
				if(sy < lane.getEventSyMin() || sy > lane.getEventSyMax() ) {
					//不在计算范围之内的数据不算变道
					continue;
				}
				direction = lane.getLaneRoad().substring(0,1);
				double sx = jo.getDouble("sX") ;
				double vx = jo.getDouble("vX");
				double vy = jo.getDouble("vY");
				
				if(Math.abs(vx) > 2 && firstTime == 0 && jo.getDate("date") != null) {
					firstTime = jo.getDate("date").getTime();
				}
				
				ZcLdLaneInfo lane2 = TwoSpeedThreeHurried.radarDirectionMap.get(zcLdEventRadarInfo.getEquId()+"_"+direction) ;
				if(lane2 != null) {
					double[] equation = lane2.getEquation();
					double mod1 = 0 ;
					if(equation.length == 3) {
						//二次多项式
						mod1 = Math.abs((sx - (Math.pow(sy,2) * equation[0] + sy * equation[1] + equation[2] ))) % 3.75 ;
					}
					if(equation.length == 4) {
						//三次多项式
						mod1 = Math.abs((sx - (Math.pow(sy,3) * equation[0] + Math.pow(sy,2) * equation[1] + sy * equation[2] + equation[3] ))) % 3.75 ;
					}
					if(equation.length == 5) {
						//四次多项式
						mod1 = Math.abs((sx - (Math.pow(sy,4) * equation[0] + Math.pow(sy,3) * equation[1] + Math.pow(sy,2) * equation[2] + sy * equation[3] + equation[4] ))) % 3.75 ;
					}
					if(equation.length == 6) {
						//五次多项式
						mod1 = Math.abs((sx - (Math.pow(sy,5) * equation[0] + Math.pow(sy,4) * equation[1] + Math.pow(sy,3) * equation[2] + Math.pow(sy,2) * equation[3] + sy * equation[4] + equation[5] ))) % 3.75 ;
					}
					
					//大于1.8并且小于2.2算变道
					if(mod1 > laneChangeGreater && mod1 < laneChangeLess && Math.abs(vx) > 2 ) {
						if(polynomialLaneLast != -99 && polynomialLane != polynomialLaneLast) {
							//变道时长
//							changeTime(array , k, zcLdEventRadarInfo.getEquId() , vy);
							changeTime2(array, zcLdEventRadarInfo.getEquId(), firstTime, vy);
							
							isChange = true;
							
							String ids = StringUtils.EMPTY;
							if(idsMap.get(zcLdEventRadarInfo.getEquId()+"_"+polynomialLaneLast) != null) {
								ids = idsMap.get(zcLdEventRadarInfo.getEquId()+"_"+polynomialLaneLast);
							}
							ids += targetId+",";
							idsMap.put(zcLdEventRadarInfo.getEquId()+"_"+polynomialLaneLast, ids);
							
							String sys = StringUtils.EMPTY;
							if(syMap.get(zcLdEventRadarInfo.getEquId()+"_"+polynomialLaneLast) != null) {
								sys = syMap.get(zcLdEventRadarInfo.getEquId()+"_"+polynomialLaneLast);
							}
							sys += sy+",";
							syMap.put(zcLdEventRadarInfo.getEquId()+"_"+polynomialLaneLast, sys);
							
							break;
						}
					}
					polynomialLaneLast = polynomialLane;
					lastLaneRoad = lane.getLaneRoad();
					lastSy = sy;
				}
			}
			if(polynomialLaneLast == -99) {
				return eventManage;
			}else {
				if(isChange) {
					if(changeMap.get(zcLdEventRadarInfo.getEquId()+"_"+polynomialLaneLast) != null) {
						int count = changeMap.get(zcLdEventRadarInfo.getEquId()+"_"+polynomialLaneLast);
						count++;
						changeMap.put(zcLdEventRadarInfo.getEquId()+"_"+polynomialLaneLast, count);
						//连续两辆车变道认为有障碍物
//						if(count > 1) {
//							isResult = true;
//						}
						//连续三辆车变道认为有障碍物
						if(count > 2) {
							isResult = true;
						}
					}else {
						changeMap.put(zcLdEventRadarInfo.getEquId()+"_"+polynomialLaneLast, 1);
						return eventManage;
					}
				}else {
					changeMap.put(zcLdEventRadarInfo.getEquId()+"_"+polynomialLaneLast, 0);
					idsMap.put(zcLdEventRadarInfo.getEquId()+"_"+polynomialLaneLast, StringUtils.EMPTY);
					syMap.put(zcLdEventRadarInfo.getEquId()+"_"+polynomialLaneLast, StringUtils.EMPTY);
					return eventManage;
				}
			}
			
			
			//拥堵态
			
			//计算认为有障碍物
			if(isResult) {
				logger.info("障碍物，雷达："+zcLdEventRadarInfo.getEquId()+"；车道号："+polynomialLaneLast+"；变道车次："+changeMap.get(zcLdEventRadarInfo.getEquId()+"_"+polynomialLaneLast)+"；IDS:"+idsMap.get(zcLdEventRadarInfo.getEquId()+"_"+polynomialLaneLast)+"；SYS:"+syMap.get(zcLdEventRadarInfo.getEquId()+"_"+polynomialLaneLast));
				eventManage = new ZcLdRiskEventManage();
				eventManage.setId(StringUtils.getUUID());
				eventManage.setEventTime(zcLdEventRadarInfo.getBeginTime());
				eventManage.setRadarId(zcLdEventRadarInfo.getEquId());
				eventManage.setEventType("1007");
				eventManage.setEventLane(lastLaneRoad);
				eventManage.setEventSy(lastSy);
				eventManage.setValid("U");
				eventManage.setEventPriorityLevel(2);
				return eventManage;
			}else {
				return eventManage;
			}
		} catch (Exception e) {
			logger.error("障碍物算法异常", e);
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	/**
	 * 变道时长计算
	 * 以压线为中间点，获取前后连续VX>2的时间段
	 * @param array
	 * @param i
	 * @param radarId
	 * @param vy
	 */
	private void changeTime(JSONArray array , int i, String radarId, double vy) {
		try {
			long minTimeTop = 0L;
			long maxTimeTop = 0L;
			long minTimeBottom = 0L;
			long maxTimeBottom = 0L;
			
			for(int k = (i-1) ; k >= 0 ; k-- ) {
				JSONObject jo = (JSONObject)array.get(k);
				if(Math.abs(jo.getDouble("vX")) > 2) {
					try {
						if(k == (i-1)) {
							maxTimeTop = jo.getDate("date").getTime();
						}
						minTimeTop = jo.getDate("date").getTime();;
					} catch (Exception e) {
						break;	
					}
					
				}else {
					break;	
				}
			}
			for(int j = (i+1); j < array.size(); j++) {
				JSONObject jo = (JSONObject)array.get(j);
				if(Math.abs(jo.getDouble("vX")) > 2) {
					try {
						if(j == (i+1)) {
							minTimeBottom = jo.getDate("date").getTime();;
						}
						maxTimeBottom = jo.getDate("date").getTime();;
					} catch (Exception e) {
						break;	
					}
				}else {
					break;	
				}
			}
			
			long minTime = minTimeTop == 0 ? minTimeBottom : minTimeTop;
			long maxTime = maxTimeBottom == 0 ? maxTimeTop : maxTimeBottom;
			logger.info("变道雷达："+radarId+"；变道开始："+minTime+"；变道结束："+maxTime+"；耗时："+(maxTime-minTime) + "；瞬时车速：" + vy);
		} catch (Exception e) {
			logger.error("变道时长异常："+ e);
		}
	}
	
	/**
	 * 变道时长计算
	 * 变道车辆，进入雷达后第一个VX>2开始、出雷达之前最后一个VX>2截至，计算时长
	 * @param array
	 * @param radarId
	 * @param firstTime
	 * @param vy
	 */
	private void changeTime2(JSONArray array, String radarId, long firstTime, double vy) {
		long lastTime = 0L;
		try {
			for(int k = array.size()-1 ; k >= 0  ; k --) {
				JSONObject jo = array.getJSONObject(k);
				double vx = jo.getDouble("vX");
				if(Math.abs(vx) > 2 ) {
					lastTime = jo.getDate("date").getTime();
					break;
				}
			}
			
			logger.info("变道雷达："+radarId+"；变道开始："+firstTime+"；变道结束："+lastTime+"；耗时："+(lastTime-firstTime) + "；瞬时车速：" + vy);
		} catch (Exception e) {
			logger.error("变道时长异常："+ e);
		}
	}
	
	
	
	
}
