package org.lkdt.modules.radar.supports.radarDataService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.radar.entity.ZcLdEquipment;
import org.lkdt.modules.radar.entity.ZcLdEventRadarInfo;
import org.lkdt.modules.radar.entity.ZcLdLaneInfo;
import org.lkdt.modules.radar.supports.radarTools.DO.RadarDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

/**
 * 合流区算法
 * @author wy
 *
 */
@Configuration
public class ConfluenceArea {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 习惯舒适性刹车加速度
	 */
	@Value(value = "${radar.braking-acceleration}")
	private Double brakingAcceleration;
	
	/**
	 * 小车加速度
	 */
	@Value(value = "${radar.small-car-decrease-speed}")
	private Double smallCarDecreaseSpeed;
	
	/**
	 * 大车加速度
	 */
	@Value(value = "${radar.big-car-decrease-speed}")
	private Double bigCarDecreaseSpeed;
	
	/**
	 * 主路外侧连续三辆以上（含三辆）车辆平均车速阈值
	 */
	@Value(value = "${radar.avg-speed}")
	private Double avgSpeed;
	
	/**
	 * 主路外侧连续三辆以上（含三辆）车辆平均停车视距阈值
	 */
	@Value(value = "${radar.avg-ssd}")
	private Double avgSsd;
	
	/**
	 * 缓存节点雷达
	 */
	private Map<String,RadarDO> nodeRadarMap = new HashMap<>();
	
	
	
	/**
	 * 缓存三辆车的平均车速
	 */
	public Map<String,List<ZcLdEventRadarInfo>> radarInfoMap = new HashMap<String, List<ZcLdEventRadarInfo>>();
	
	private List<String> oldList = new ArrayList<>();
	private List<String> newList = new ArrayList<>();
	

	/**
	 * 合流区
	 * @param radarDO
	 * @param radarId
	 */
	public void confluenceAreaMode(RadarDO[] radarDOs, Set<Integer> targetIdSet ,String radarId) {
		//计算最后一帧
		RadarDO radarDO = radarDOs[radarDOs.length-1];
		JSONArray array = radarDO.getDataBody() ;
		
		RadarDO radarDataDO = new RadarDO();
		JSONArray arrayData = new JSONArray();
		
		//缓存节点雷达数据
		if(TwoSpeedThreeHurried.nodeRadarList.contains(radarId)) {
			for(int i = 0 ; i < array.size() ; i ++) {
				JSONObject jo = array.getJSONObject(i);
				int targetId = jo.getInt("targetId");
				if(targetIdSet.contains(targetId)) {
					arrayData.add(jo);
				}
			}
			radarDataDO.setDataBody(arrayData);
			radarDataDO.setDate(radarDO.getDate());
			radarDataDO.setNanoSecond(radarDO.getNanoSecond());
			radarDataDO.setYmdhms(radarDO.getYmdhms());
			radarDataDO.setTimestamp(radarDO.getTimestamp());
			
			nodeRadarMap.put(radarId, radarDataDO);
		}else {
			return ;
		}
		
		//主路雷达
		RadarDO dataZhuDao = new RadarDO();
		//匝道雷达
		RadarDO dataZaDao = new RadarDO();
		//主路雷达ID
		String radarIdZhuDao = StringUtils.EMPTY;
		//匝道雷达ID
		String radarIdZaDao = StringUtils.EMPTY;
//		//主路外侧车道
//		Map<Integer, ZcLdLaneInfo> outsideLaneMap = new HashMap<>();
		
		boolean isZhuDao = false;
		//根据当前雷达找匹配的节点雷达
		for (Map.Entry<String, Map<String,ZcLdEquipment>> entry : TwoSpeedThreeHurried.nodeRadarMap.entrySet()) {
			Map<String,ZcLdEquipment> map = entry.getValue();
			for (Map.Entry<String,ZcLdEquipment> entry2 : map.entrySet()) {
				ZcLdEquipment equ = entry2.getValue();
				String[] nodeTypes = equ.getNodeId().split("_");
				if(Arrays.asList(nodeTypes).contains(radarId)) {
					//雷达类型（单元雷达：101，节点匝道雷达102，节点主路雷达103）
					if(StringUtils.equals(equ.getRadarType(), "102")) {
						dataZaDao = nodeRadarMap.get(entry2.getKey());
						radarIdZaDao = entry2.getKey();
						if(StringUtils.equals(equ.getId(), radarId)) {
							isZhuDao = false;
						}
					}else if(StringUtils.equals(equ.getRadarType(), "103")) {
						dataZhuDao = nodeRadarMap.get(entry2.getKey());
						radarIdZhuDao = entry2.getKey();
						if(StringUtils.equals(equ.getId(), radarId)) {
							isZhuDao = true;
						}
					}
				}
				
			}
			
		}
		
		double f = getRoadF();
		
		//能见度
		double njd = 500.0;
		
		//预警模式五：若加速车道无车，启动预警模式五，计算主匝道车辆是否存在碰撞风险
//		if(能见度>200 && 加速车道无车) {
		confluenceAreaMode5(dataZhuDao ,dataZaDao ,radarIdZhuDao);
//		}
		
		String direction = StringUtils.EMPTY;
		JSONArray arrayData2 = new JSONArray();
		array = radarDataDO.getDataBody() ;
		for(int i = 0 ; i < array.size() ; i ++) {
			JSONObject jo = array.getJSONObject(i);
			int targetId = jo.getInt("targetId");
			double vy = Math.abs(jo.getDouble("vY"));
			int carType = jo.getInt("carType");
			double decreaseV = -6;		//刹车距离
			double carLang = 4;
			if(carType == 1) {
				decreaseV = smallCarDecreaseSpeed;
			}else {
				decreaseV = bigCarDecreaseSpeed;
				carLang = 9;
			}
			decreaseV = Math.abs(decreaseV);
			
			double brakingDistance = ((vy/3.6)*(vy/3.6)) / (2 * brakingAcceleration);	//刹车距离
			//计算停车视距（1）
			double s_tingche = ((1.5 * vy / 3.6) + brakingDistance + 5 + carLang) * f ;	
			
			//计算低能见度下限速（2）
			double v_xiansu = 20 * (Math.log(njd/25) / Math.log(2)) ;
			
			//计算低能见度下，最大可能矫正安全车速
			double t1 = (2* (njd-carLang-5)) / decreaseV;
			double v_zuida = 3.6 * decreaseV * (-1.5 + Math.sqrt(1.5*1.5 + t1) ) ;
			
			//计算安全车速（2）
			double k = 0.3;
			double v_anquan = (v_zuida - v_xiansu) * k + v_xiansu;
			
			//计算安全车距（3）
			double s_anquan = 1.5 * v_anquan + (v_anquan*v_anquan / (2 * brakingAcceleration)) ;
			
			int polynomialLane = jo.getInt("polynomialLane");	//使用拟合后的车道
			ZcLdLaneInfo lane = TwoSpeedThreeHurried.radarLaneInfoMap.get(radarId+"_"+polynomialLane) ;
			//拟合以后的车道不存在，或者是绿化车道，则不处理
			if(lane == null || StringUtils.equals(lane.getLaneRoad(), "0")) {
				continue;
			}
			direction = lane.getLaneRoad().substring(0,1);
			if(isZhuDao) {
				//主道外侧车道
				if(StringUtils.equals(lane.getIsOutsideLane(), "Y")) {
					arrayData2.add(jo);
				}
			}else {
				//匝道
				arrayData2.add(jo);
			}
			
			
			//预警模式模式一
			if(njd <= 200) {
				//a：对于配置双时定位车载终端或手机APP的车辆，在其进入合流区前500m， 对其进行语音提醒，告知其在当前道路低能见度下应保持的安全车速(2)与安全车距（3）
				logger.info(String.format("a：安全车速：{%s}；安全车距：{%s}", v_anquan ,s_anquan ));
				
				//c: 情报板提示：“当前能见度低，车速请保持在xx以下”。
				logger.info("情报板提示：“当前能见度低，车速请保持在xx以下”。");
				
				//b：若雷达监测发现主路或匝道来车有1辆以上（含1辆）汽车的S停车视距>D低能见度的目标车，语音提醒主路或匝道来车按照（2）（3）计算的安全车速车距驾驶
				if(s_tingche > njd) {
					logger.info(String.format("b：安全车速：{%s}；安全车距：{%s}", v_anquan ,s_anquan ));
				}
			}else if(njd > 200 && f == 1.0){
//				System.out.println("能见度大于200，并且路面干燥");
				//预警模式二：合流区加速车道和主路外侧车道有违章停车、低速车或大型汽车，并检测到有匝道车辆进入，启动预警模式二
				
				//预警模式三：合流区加速车道和主路外侧车道有违章停车，并检测到匝道无车辆进入，启动预警模式三
				
				//预警模式四：合流区加速车道有来自匝道的低速车或大型汽车，并未检测到匝道无车辆进入，启动预警模式四
				
			}
		}
		
		//根据SY的值对车辆进行排序
		List<JSONObject> list = sort(arrayData2,direction);
		
		if(f > 1.0) {
			//预警模式七：实时计算雨后路面湿滑和路面积冰雪时的推荐安全车速（车距）
			//a)对于主匝道来车，若车流量大于1，根据其当前与前后车车距，计算其应保持的安全车速。
			if(list.size() > 1) {
				double syLast = -1;
				for(int i = 0 ; i < list.size() ; i ++) {
					JSONObject jo = list.get(i);
					double sy = jo.getDouble("sY") ;
					if(syLast > -1) {
						int carType = jo.getInt("carType");
						double decreaseV = -6;		//刹车距离
						double carLang = 4;
						if(carType == 1) {
							decreaseV = smallCarDecreaseSpeed;
						}else {
							decreaseV = bigCarDecreaseSpeed;
							carLang = 9;
						}
						decreaseV = Math.abs(decreaseV);
						
						double s_cheju = Math.abs(sy - syLast);		//S车距
						double t1 = (2* (s_cheju*f-carLang-5)) / decreaseV;
						double v_anquan = 3.6 * decreaseV * (-1.5 + Math.sqrt(1.5*1.5 + t1) ) ;
						logger.info(String.format("模式七：安全车速：{%s} ", v_anquan   ));
					}
					syLast = sy;
				}
			}
		}
		
		
		return ;
	}
	
	/**
	 * 根据SY的值对车辆进行排序
	 * @param arrayData2
	 */
	public static List<JSONObject> sort(JSONArray array, String direction) {
		if(array == null || array.isEmpty()) {
			return new ArrayList<>();
		}
		
		List<JSONObject> list = new ArrayList<>();
		for(int i = 0 ; i < array.size() ; i ++) {
			JSONObject jo = array.getJSONObject(i);
			list.add(jo);
		}
		
        Collections.sort(list, (JSONObject o1, JSONObject o2) -> {
            //转成JSON对象中保存的值类型
            double a = Double.parseDouble(o1.getStr("sY"));
            double b = Double.parseDouble(o2.getStr("sY"));
            // 如果a, b数据类型为int，可直接 return a - b ;(升序，降序为 return b - a;)
            if(StringUtils.equals(direction, "R")) {
            	//去向
            	if (a < b) {  //降序排列，升序改成a>b
                    return 1;
                } else if(a == b) {
                    return 0;
                } else {
                    return -1;
                }
            }else {
            	//来向
            	if (a > b) {  
                    return 1;
                } else if(a == b) {
                    return 0;
                } else {
                    return -1;
                }
            }
            
        });
        
        return list;
	}
	
	
	
	/**
	 * 预警模式五：若加速车道无车，启动预警模式五，计算主匝道车辆是否存在碰撞风险
	 * @param radarDO
	 * @param radarId
	 */
	public void confluenceAreaMode5(RadarDO dataZhuDao ,RadarDO dataZaDao ,String radarIdZhuDao) {
		if(dataZhuDao == null || dataZhuDao.getDataBody() == null || dataZhuDao.getDataBody().isEmpty() || 
				dataZaDao == null || dataZaDao.getDataBody() == null || dataZaDao.getDataBody().isEmpty() ) {
			return ;
		}
		
		double sJin = 46.0 ;	//禁止汇入线长度
		
		//主道
		JSONArray arrayZhuDao = dataZhuDao.getDataBody() ;
		//匝道
		JSONArray arrayZaDao = dataZaDao.getDataBody() ;
		
		
		for(int i = 0 ; i < arrayZhuDao.size() ; i ++) {
			JSONObject joZhuDao = arrayZhuDao.getJSONObject(i);
			double va = Math.abs(joZhuDao.getDouble("vY")) ;		//A车速度
			if(va == 0) {
				continue;
			}
			int targetId = joZhuDao.getInt("targetId");
			int polynomialLane = joZhuDao.getInt("polynomialLane");	//使用拟合后的车道
			ZcLdLaneInfo lane = TwoSpeedThreeHurried.radarLaneInfoMap.get(radarIdZhuDao+"_"+polynomialLane) ;
			//拟合以后的车道不存在，或者是绿化车道，则不处理
			if(lane == null || StringUtils.equals(lane.getLaneRoad(), "0")) {
				continue;
			}
			if(!StringUtils.equals(lane.getIsOutsideLane(), "Y")) {
				continue;
			}
			//车道方向
			String direction = lane.getLaneRoad().substring(0,1);
			long ta0 = dataZhuDao.getDate().getTime() ;		//瞬时时间戳
			double sZhu = 186.0 ;	//主路雷达到禁止汇入线的距离
			double s1 = joZhuDao.getDouble("sY") ; 		//A车在雷达的Y坐标
			va = va / 3.6 ;
			double ta = (ta0/1000) + ((sZhu - s1 + sJin) / va) ;
			
			double la = 4;		//车长，小车4、大车6	
			int carTypea = joZhuDao.getInt("carType");
			if(carTypea == 1) {
				la = 4;
			}else {
				la = 6;
			}
			for (int j = 0; j < arrayZaDao.size(); j++) {
				JSONObject joZaDao = arrayZaDao.getJSONObject(j);
				double vb = Math.abs(joZaDao.getDouble("vY")) ;		//B车速度
				if(vb == 0) {
					continue;
				}
				long tb0 = dataZaDao.getDate().getTime() ;		//瞬时时间戳
				double sZaDao = 30.0 ;	//匝道雷达到禁止汇入线的距离
				double s2 = joZaDao.getDouble("sY") ;		//B车在雷达的Y坐标		
				
				vb = vb / 3.6 ;
				double tb = (tb0/1000) + ((s2 + sZaDao + sJin) / vb) ;
				
				double sa = 0.0 ;		//sa-sb的值
				double sb = 0.0 ;		//sb-sa的值
				double ls = 0.0 ;
				
				double aa = 6.0;
				double ab = 2.0;
				double l = 5.0;			//两车静止停放时的安全距离
				
				double lb = 4;			//车长，小车4、大车6
				int carTypeb = joZaDao.getInt("carType");
				if(carTypeb == 1) {
					lb = 4;
				}else {
					lb = 6;
				}
				
				sa = va * (tb - ta);
				sb = vb * (ta - tb);
				
				if(ta == tb) {
					newList.add(radarIdZhuDao+"_"+targetId+"_1");
					if(!oldList.contains(radarIdZhuDao+"_"+targetId+"_1")) {
						//控制次数，同一个targetId同一个场景只入库一次
					}
					
				}else if(ta > tb) {
					aa = 2.0;	//后车刹车
					ab = 6.0;	//前车刹车
					
					ls = 1.5 * va + ((va*va) / (2*aa)) - ((vb*vb) / (2*ab)) + l + la ;
					
					if(sb < ls) {
						newList.add(radarIdZhuDao+"_"+targetId+"_2");
						if(!oldList.contains(radarIdZhuDao+"_"+targetId+"_2")) {
							
						}
						
					}
				}else if(ta < tb) {
					aa = 6.0;	//前车刹车
					ab = 2.0;	//后车刹车
					ls = 1.5 * vb + ((vb*vb)/(2*ab)) - ((va*va)/(2*aa)) + l + lb ;
					
					if(sa < ls) {
						newList.add(radarIdZhuDao+"_"+targetId+"_3");
						if(!oldList.contains(radarIdZhuDao+"_"+targetId+"_3")) {
							
						}
						
					}
				}
			}
		}
		
		oldList.clear();
		oldList.addAll(newList);
		newList.clear();
		
		return ;
	}
	
	
	/**
	 * 预警模式六：当主路外侧连续三辆以上（含三辆）车辆平均车速大于一定阈值或平均停车视距小于一定阈值时，启动预警模式六
	 * @param zcLdEventRadarInfo
	 * @param radarId	
	 */
	public void confluenceAreaMode6(ZcLdEventRadarInfo zcLdEventRadarInfo) {
		
		double f = getRoadF();
		
		//是否节点雷达
		if(TwoSpeedThreeHurried.nodeRadarList.contains(zcLdEventRadarInfo.getEquId())) {
			//遍历所有节点雷达
			for (Map.Entry<String, Map<String,ZcLdEquipment>> entry : TwoSpeedThreeHurried.nodeRadarMap.entrySet()) {
				Map<String,ZcLdEquipment> eMap = entry.getValue();
				if(eMap.get(zcLdEventRadarInfo.getEquId()) != null) {
					ZcLdEquipment equ = eMap.get(zcLdEventRadarInfo.getEquId());
					//并且当前雷达是主路雷达
					if(equ != null && StringUtils.equals(equ.getRadarType(), "103")) {
						List<ZcLdEventRadarInfo> radarInfoList = radarInfoMap.get(zcLdEventRadarInfo.getEquId());
						if(radarInfoList == null) {
							radarInfoList = new ArrayList<ZcLdEventRadarInfo>();
							radarInfoList.add(zcLdEventRadarInfo);
						}else if(radarInfoList.size() < 3) {
							radarInfoList.add(zcLdEventRadarInfo);
						}else if(radarInfoList.size() == 3) {
							radarInfoList.remove(0);
							radarInfoList.add(zcLdEventRadarInfo);
						}
						radarInfoMap.put(zcLdEventRadarInfo.getEquId(), radarInfoList);
						//缓存到3辆车时，开始计算
						if(radarInfoList.size() == 3) {
							double sumSpeed = 0.0;
							double sumSsd = 0.0;
							String direction = StringUtils.EMPTY;
							for (ZcLdEventRadarInfo r : radarInfoList) {
								ZcLdLaneInfo lane = TwoSpeedThreeHurried.radarLaneInfoMap.get(zcLdEventRadarInfo.getEquId()+"_"+r.getLaneEndRadar()) ;
								direction = lane.getLaneRoad().substring(0,1);
								int carType = Integer.valueOf(r.getCarType());
								double vy = Math.abs(r.getSpeedAvg().doubleValue());
								double carLang = 4;
								if(carType != 1) {
									carLang = 9;
								}
								
								double brakingDistance = ((vy/3.6)*(vy/3.6)) / (2 * brakingAcceleration);	//刹车距离
								//计算停车视距（1）
								double s_tingche = ((1.5 * vy / 3.6) + brakingDistance + 5 + carLang) * f ;	
								
								sumSpeed += vy;
								sumSsd += s_tingche;
							}
							
							
							//车辆平均车速大于一定阈值或平均停车视距小于一定阈值时
							if( (sumSpeed / 3) > avgSpeed ) {
//							if( (sumSpeed / 3) > avgSpeed || (sumSsd / 3) < avgSsd ) {
								logger.info("车速："+sumSpeed+"；车速阈值"+avgSpeed);
								logger.info("停车视距："+sumSsd+"；停车视距阈值"+avgSsd);
								//a:对于使用双时鸟车载终端或手机APP的车辆，在其从匝道进入合流区前500m，告知其主路车速较快，谨慎驾驶
								// TODO
								logger.info("对于使用双时鸟车载终端或手机APP的车辆，在其从匝道进入合流区前500m，告知其主路车速较快，谨慎驾驶");
								
								//b:情报板提示匝道：主路车速较快，诱导灯橙色慢速闪烁
								// TODO
								
								//c:匝道中风险，橙色预警
								// TODO
							}
						}
					}
				}
			}
		}
		
		
	}
	
	
	/**
	 * 路面摩擦系数
	 * @return
	 */
	private double getRoadF() {
		double f = 1.0;
//		if(路面湿滑) {
//			f = 1.5;
//		}
//		if(积雪) {
//			f = 3.5;
//		}
//		if(结冰) {
//			f = 6.0;
//		}
		return f;
	}
	
	
	
	
}
