package org.lkdt.modules.radar.supports.radarDataService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.lkdt.common.util.DateUtils;
import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.radar.entity.ZcLdEquipment;
import org.lkdt.modules.radar.entity.ZcLdLaneInfo;
import org.lkdt.modules.radar.entity.ZcLdRiskEventManage;
import org.lkdt.modules.radar.mapper.ZcLdEquipmentMapper;
import org.lkdt.modules.radar.mapper.ZcLdLaneInfoMapper;
import org.lkdt.modules.radar.service.IZcLdThreeStatusCoefficientService;
import org.lkdt.modules.radar.supports.radarDataService.entity.RadarEventType;
import org.lkdt.modules.radar.supports.radarTools.DO.RadarDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

/**
 * 计算二速三急车、变道
 * @author wy
 *
 */
@Configuration
public class TwoSpeedThreeHurried {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());

	//车辆变道范围 1.8~2.2 米之间算变道
	@Value(value = "${radar.lane-change-greater}")
	private Double laneChangeGreater;
	
	//车辆变道范围 1.8~2.2 米之间算变道
	@Value(value = "${radar.lane-change-less}")
	private Double laneChangeLess;
	
	//超高速
	@Value(value = "${radar.maximum-speed}")
	private Double maxSpeed;
	
	//超低速
	@Value(value = "${radar.minimum-speed}")
	private Double minSpeed;
	
	//小车急加速
	@Value(value = "${radar.small-car-rapid-acceleration}")
	private Double smallCarRapidAcceleration;
	
	//小车急减速
	@Value(value = "${radar.small-car-rapid-deceleration}")
	private Double smallCarRapidDeceleration;
	
	//大车急加速
	@Value(value = "${radar.big-car-rapid-acceleration}")
	private Double bigCarRapidAcceleration;
	
	//大车急减速
	@Value(value = "${radar.big-car-rapid-deceleration}")
	private Double bigCarRapidDeceleration;
	
	//小车加速度
	@Value(value = "${radar.small-car-decrease-speed}")
	private Double smallCarDecreaseSpeed;
	
	//大车加速度
	@Value(value = "${radar.big-car-decrease-speed}")
	private Double bigCarDecreaseSpeed;
	
	//前后车速度的车距算安全车距-实际车距的差值
	@Value(value = "${radar.safety-distance}")
	private Double safetyDistance;
	
	//速度低于30KM/H时触发计算刹停事件
	@Value(value = "${radar.brake-stop-vy}")
	private Double brakeStopVY;
	
	
	//缓存雷达帧数
	private int cacheNum  = 14; 
    //所有雷达数据缓存
    private Map<String,List<RadarDO>> radarAllMap = new HashMap<>();
    
    //急加、急减事件缓存
    private Map<String,List<RadarEventType>> speedSuddenChangeMap = new HashMap<>();
    
    @Autowired
    private ZcLdLaneInfoMapper zcLdLaneInfoMapper;

	@Autowired
	private ZcLdEquipmentMapper zcLdEquipmentMapper;
	
	@Autowired
    private IZcLdThreeStatusCoefficientService zcLdThreeStatusCoefficientService;
    
	/**
	 * 车道信息MAP
	 */
    public static Map<String, ZcLdLaneInfo> radarLaneInfoMap = new HashMap<>();

    /**
     * 车道方向MAP
     */
	public static Map<String, ZcLdLaneInfo> radarDirectionMap = new HashMap<>();

	/**
	 * 雷达设备LIST
	 */
	public static List<ZcLdEquipment> ldEquipmentList = new ArrayList<>();
	
	/**
	 * 节点雷达MAP
	 */
	public static Map<String, Map<String,ZcLdEquipment>> nodeRadarMap = new HashMap<>();
	
	/**
	 * 节点雷达ID的LIST
	 */
	public static List<String> nodeRadarList = new ArrayList<>();
	
	/**
     * 停车事件。
     * 一个停车的坐标点，只触发一次停车事件
     */
    private Map<String,List<String>> stopEventMap = new HashMap<>();
    
    /**
     * 三态算法，拟合函数值
     */
    public static Map<String,double[]> threeStatusEquationMap = new HashMap<>();
    
    /**
     * 三态系数是否入库开关
     */
    public static Map<String,ZcLdEquipment> threeStatusCoefficientIsSaveMap = new HashMap<>();
    
    /**
     * 是否计算三态风险开关
     */
    public static Map<String,String> calculateMap = new HashMap<>();
	

    @PostConstruct
    public void init() {
		this.initZcLdFlowLaneInfo();
	}

	/**
	 * TODO:初始化车道信息
	 */
	public void initZcLdFlowLaneInfo(){
		logger.info("雷达参数初始化......");
		HashMap<String, Object> paramMap = new HashMap<>();
		List<ZcLdEquipment> ldEquipments = zcLdEquipmentMapper.selectByMap(paramMap);
		//verify valid
		for(ZcLdEquipment z: ldEquipments){
			if(StringUtils.isNotEmpty(z.getIp()) && z.getPort() != null){
				if(StringUtils.equals(z.getEquType(), "001")){
					//事件雷达
					ldEquipmentList.add(z);
				} else if(StringUtils.equals(z.getEquType(), "002")) {
					//流量雷达
					ldEquipmentList.add(z);
				}
				if(StringUtils.isNotEmpty(z.getNodeId())) {
					//节点雷达
					Map<String, ZcLdEquipment> eMap = new HashMap<>();
					if(nodeRadarMap.get(z.getNodeId()) != null) {
						eMap = nodeRadarMap.get(z.getNodeId());
					}
					eMap.put(z.getId(), z);
					nodeRadarMap.put(z.getNodeId(), eMap);
					nodeRadarList.add(z.getId());
				}
				//三态系数是否入库开关
				threeStatusCoefficientIsSaveMap.put(z.getId(),z);
				//是否计算三态风险开关
				calculateMap.put(z.getId(),z.getIsCalculate());
			}
		}
		
		List<ZcLdLaneInfo> radarLaneInfoList = zcLdLaneInfoMapper.queryAllLane();
		for (ZcLdLaneInfo lane : radarLaneInfoList) {
			radarLaneInfoMap.put(lane.getEquId()+"_"+lane.getLaneRadar(), lane);
			if(lane.getLaneTrack() != null && !lane.getLaneTrack().isEmpty()) {
				String laneTrack = lane.getSideLineTrack();
				if(laneTrack != null) {
					if(lane.getLaneRoad().indexOf("R") > -1) {
						laneTrack = laneTrack.split(";")[0];
					}else {
						laneTrack = laneTrack.split(";")[1];
					}
					
					laneTrack = laneTrack.replace("[", "");
					laneTrack = laneTrack.replace("]", "");
					String[] laneTrackStrs = laneTrack.split(",");
					double[] equation = new double[laneTrackStrs.length];
					for (int i = 0; i < laneTrackStrs.length; i++) {
						equation[i] = Double.parseDouble(laneTrackStrs[i]) ;
					}
					lane.setEquation(equation);
					
					if(StringUtils.equals(lane.getLaneRoad(), "R-C")) {
						radarDirectionMap.put(lane.getEquId()+"_R", lane);
					}else if(StringUtils.equals(lane.getLaneRoad(), "L-C")) {
						radarDirectionMap.put(lane.getEquId()+"_L", lane);
					}
					
					ZcLdEquipment param = threeStatusCoefficientIsSaveMap.get(lane.getEquId());
					if(param != null && param.getIsCalculate() != null &&
							param.getSaveTimeStart() != null &&
							param.getSaveTimeEnd() != null ) {
						if(StringUtils.equals(lane.getLaneRoad(), "R-C")) {
							threeStatusEquationMap.put(lane.getEquId()+"_R", zcLdThreeStatusCoefficientService.getThreeStatusEquation(lane.getEquId(), "R", DateUtils.date2Str(param.getSaveTimeStart(), DateUtils.datetimeFormat.get()), DateUtils.date2Str(param.getSaveTimeEnd(), DateUtils.datetimeFormat.get())));
						}else if(StringUtils.equals(lane.getLaneRoad(), "L-C")) {
							threeStatusEquationMap.put(lane.getEquId()+"_L", zcLdThreeStatusCoefficientService.getThreeStatusEquation(lane.getEquId(), "L", DateUtils.date2Str(param.getSaveTimeStart(), DateUtils.datetimeFormat.get()), DateUtils.date2Str(param.getSaveTimeEnd(), DateUtils.datetimeFormat.get())));
						}
					}
				}
			}
		}
		
		for(Map.Entry<String, double[]> m : threeStatusEquationMap.entrySet()) {
			logger.info("雷达："+m.getKey()+"；value:"+Arrays.toString(m.getValue()));
		}
		logger.info("雷达参数初始化完成......");
	}
	
	
	/**
	 * 急停、撞车
	 * @param radarDOs  雷达数据
	 * @param targetIdSet 雷达数据ID的交集
	 */
	public ZcLdRiskEventManage twoSpeedThreeHurriedEvent(RadarDO[] radarDOs, Set<Integer> targetIdSet ,String radarId) {
		//9999：撞车
		ZcLdRiskEventManage event9999 = null;
		//1100：急停
		ZcLdRiskEventManage event1100 = null;
		//1005：急变道
		ZcLdRiskEventManage event1005 = null;
		
		
		//1001：超高速
		//1002：超低速
		//1003：急加速
		//1004：急减速
		//1005：急变道
		//1006：变道
		//9999：撞车
		//8888：停车
		//1100：急停
		
		boolean isLaneChange = false;
		boolean isEvent9999 = false;
		boolean isEvent1005 = false;
		//计算最后一帧
		RadarDO radarDO = radarDOs[radarDOs.length-1];
		JSONArray array = radarDO.getDataBody() ;
		for(int k = 0 ; k < array.size() ; k ++) {
			JSONObject jo = array.getJSONObject(k);
			jo.put("date", radarDO.getDate());
			int targetId = jo.getInt("targetId");
			if(targetIdSet.contains(targetId)) {
				//为了LinkedHashMap能每个targetId连续三条数据，所以最外层遍历targetIdSet
				double vy = Math.abs(jo.getDouble("vY"));
				double vx = jo.getDouble("vX");
				
				double sx = jo.getDouble("sX") ;
				double sy = jo.getDouble("sY") ;
				int polynomialLane = jo.getInt("polynomialLane");	//使用拟合后的车道
				int carType = jo.getInt("carType");
				int polynomialLaneLast = polynomialLane ;
				int laneNum = jo.getInt("laneNum");	//原车道
				int laneNumLast = laneNum ;
				
				ZcLdLaneInfo lane = radarLaneInfoMap.get(radarId+"_"+polynomialLane) ;
				//拟合以后的车道不存在，或者是绿化车道，则不处理
				if(lane == null || StringUtils.equals(lane.getLaneRoad(), "0")) {
					continue;
				}
				
				if(sy < lane.getEventSyMin() || sy > lane.getEventSyMax() ) {
					//不在计算范围之内的数据不算变道和急变道
					continue;
				}
				//车道方向
				String direction = lane.getLaneRoad().substring(0,1);
				
				//当前速度低于30KM/H，并且最高速度大于30KM/H ，触发计算刹停事件
				if(vy < brakeStopVY && jo.getDouble("maxVy") != null && Math.abs(jo.getDouble("maxVy")) > brakeStopVY ) {
					// (当前速度-最高速度) / (当前速度的时间-最高速度的时间)
					double speedDifference =  vy - Math.abs(jo.getDouble("maxVy"));
					double timeDifference = radarDO.getDate().getTime() - jo.getLong("maxVyTimeInMillis") ; 
					double speed = (speedDifference/3.6) / (Double.valueOf(timeDifference)/1000);
					if(Math.abs(jo.getDouble("maxVy")) > vy) {
						//刹停事件
						if( speed < smallCarRapidDeceleration ) {
							event1100 = new ZcLdRiskEventManage();
							event1100.setId(StringUtils.getUUID());
							event1100.setEventTime(radarDO.getDate());
							event1100.setRadarId(radarId);
							event1100.setEventType("1100");
							event1100.setEventLane(lane.getLaneRoad());
							event1100.setEventSy(sy);
							event1100.setValid("U");
							event1100.setEventPriorityLevel(1);
							
							jo.put("eventType1100", "Y");
							logger.info("刹停事件。雷达："+radarId+"；速度差："+speedDifference+"；时间差："+timeDifference+"；减速度："+speed+"；maxVy："+Math.abs(jo.getDouble("maxVy"))+"；vy："+vy + "；触发sy："+ sy + "；车道：" + polynomialLane);
						}
					}
				}
				
				ZcLdLaneInfo lane2 = radarDirectionMap.get(radarId+"_"+direction) ;
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
						//记录变道车变道前车道
						//取上一帧的数据
						RadarDO radarDOlast = radarDOs[radarDOs.length-2];
						JSONArray arrayLast = radarDOlast.getDataBody() ;
						for(int kk = 0 ; kk < arrayLast.size() ; kk ++) {
							JSONObject joLast = arrayLast.getJSONObject(kk);
							int targetIdLast = joLast.getInt("targetId");
							if(targetId == targetIdLast) {
								polynomialLaneLast = joLast.getInt("polynomialLane");	//使用拟合后的车道
							}
						}
						if(polynomialLaneLast == polynomialLane) {
							isLaneChange = false;
							continue;
						}
						
						isLaneChange = true;
						
						logger.info(String.format("雷达：{%s}；targetId：{%s}；时间：{%s}；SY：{%s}；SX：{%s}；VX：{%s}；VY：{%s}；车型：{%s}；"
								+ "变道前拟合车道：{%s}；变道后拟合车道：{%s}；变道前雷达车道：{%s}；变道后雷达车道：{%s}；", 
								radarId , targetId, DateUtils.date2Str(radarDO.getDate(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")) , 
								sy , sx, vx, vy, carType, polynomialLaneLast , polynomialLane ,laneNumLast ,laneNum));
							
//							jo.put("eventType1006", "Y");
					}
					//计算急变道
					if(isLaneChange) {
						int ln = jo.getInt("polynomialLane");	//拟合车道
						if(vx > 0) {
							// VX大于0 为向右变道，当前车道+1就是目标车道
							ln++;
						}else if(vx < 0) {
							// VX小于0 为向左变道，当前车道-1就是目标车道
							ln--;
						}
						
						double decreaseV = -6;		//刹车距离
						double carLang = 4;
						if(carType == 1) {
							decreaseV = smallCarDecreaseSpeed;
						}else {
							decreaseV = bigCarDecreaseSpeed;
							carLang = 9;
						}
						double brakingDistance = ((vy/3.6)*(vy/3.6)) / (2 * Math.abs(decreaseV));	//刹车距离
						JSONArray array2 = radarDO.getDataBody() ;
						for(int o = 0 ; o < array2.size() ; o ++) {
							JSONObject job = array2.getJSONObject(o);
							//targetIdSet中的targetId才会计算与变道车是否存在急变道事件，1帧、2帧闪现的数据不处理
							if(targetIdSet.contains(job.getInt("targetId")) && job.getInt("targetId") != targetId ) {
								int ltn = job.getInt("polynomialLane"); //目标车道
								if(ln == ltn) {
									int carType2 = job.getInt("carType");
									double sy2 = job.getDouble("sY") ;
									double vy2 = Math.abs(job.getDouble("vY"));
									double decreaseV2 = -6;		//刹车距离
									double carLang2 = 4;
									if(carType2 == 1) {
										decreaseV2 = smallCarDecreaseSpeed;
									}else {
										decreaseV2 = bigCarDecreaseSpeed;
										carLang2 = 9;
									}
									double brakingDistance2 = ((vy2/3.6)*(vy2/3.6)) / (2 * Math.abs(decreaseV2));	//刹车距离
									//安全车距 = 1.5*v后 + (后车刹车距离 - 前车刹车距离) + 5m + 后车车长
									double sd = 0;
									//前后车实际车距 = Y前 - Y后 - 后车车长
									double distance = 0;
									if(StringUtils.equals(direction, "L")) {
										if(sy > sy2) {
											//变道车为后车
											sd = (1.5 * vy / 3.6) + (brakingDistance - brakingDistance2) + 5 + carLang ;
											distance = Math.abs(sy - sy2) - carLang ;
											if((distance - sd) < safetyDistance) {
												isEvent1005 = true;
												jo.put("eventType1005", "Y");
//													System.out.println("急变道，变道车为后车，方向："+direction+"；安全车距："+sd+"；前后车实际车距："+distance+"；变道车ID："+ jo.getInt("targetId")+"；其他车ID："+ job.getInt("targetId") );
											}
										}else if(sy < sy2) {
											//变道车为前车
											sd = (1.5 * vy2 / 3.6) + (brakingDistance2 - brakingDistance) + 5 + carLang2 ;
											distance = Math.abs(sy - sy2) - carLang2 ;
											if( (distance - sd) < safetyDistance) {
												isEvent1005 = true;
												jo.put("eventType1005", "Y");
//													System.out.println("急变道，变道车为前车，方向："+direction+"；安全车距："+sd+"；前后车实际车距："+distance+"；变道车ID："+ jo.getInt("targetId")+"；其他车ID："+ job.getInt("targetId"));
											}
										}else {
											isEvent9999 = true;
											jo.put("eventType1005", "Y");
//												System.out.println("急变道，9999，方向："+direction+"；安全车距："+sd+"；前后车实际车距："+distance+"；变道车ID："+ jo.getInt("targetId")+"；其他车ID："+ job.getInt("targetId"));
										}
									}else {
										if(sy > sy2) {
											//变道车为前车
											sd = (1.5 * vy2 / 3.6) + (brakingDistance2 - brakingDistance) + 5 + carLang2 ;
											distance = Math.abs(sy - sy2) - carLang2 ;
											if( (distance - sd) < safetyDistance) {
												isEvent1005 = true;
												jo.put("eventType1005", "Y");
//													System.out.println("急变道，变道车为前车，方向："+direction+"；安全车距："+sd+"；前后车实际车距："+distance+"；变道车ID："+ jo.getInt("targetId")+"；其他车ID："+ job.getInt("targetId"));
											}
										}else if(sy < sy2) {
											//变道车为后车
											sd = (1.5 * vy / 3.6) + (brakingDistance - brakingDistance2) + 5 + carLang ;
											distance = Math.abs(sy - sy2) - carLang ;
											if((distance - sd) < safetyDistance) {
												isEvent1005 = true;
												jo.put("eventType1005", "Y");
//													System.out.println("急变道，变道车为后车，方向："+direction+"；安全车距："+sd+"；前后车实际车距："+distance+"；变道车ID："+ jo.getInt("targetId")+"；其他车ID："+ job.getInt("targetId"));
											}
										}else {
											isEvent9999 = true;
											jo.put("eventType1005", "Y");
//												System.out.println("急变道，9999，方向："+direction+"；安全车距："+sd+"；前后车实际车距："+distance+"；变道车ID："+ jo.getInt("targetId")+"；其他车ID："+ job.getInt("targetId"));
										}
									}
									
									if(isEvent1005) {
										event1005 = new ZcLdRiskEventManage();
										event1005.setId(StringUtils.getUUID());
										event1005.setEventTime(radarDO.getDate());
										event1005.setRadarId(radarId);
										event1005.setEventType("1005");
										event1005.setEventLane(lane.getLaneRoad());
										event1005.setEventSy(sy);
										event1005.setValid("U");
										event1005.setEventPriorityLevel(2);
									}
									
									if(isEvent9999) {
										event9999 = new ZcLdRiskEventManage();
										event9999.setId(StringUtils.getUUID());
										event9999.setEventTime(radarDO.getDate());
										event9999.setRadarId(radarId);
										event9999.setEventType("9999");
										event9999.setEventLane(lane.getLaneRoad());
										event9999.setEventSy(sy);
										event9999.setValid("U");
										event9999.setEventPriorityLevel(2);
									}
								}
							}
						}
					}
				}
			}
		}
		
		
		if(event1100 != null ) {
			return event1100;
		}else if(event9999 != null ) {
			return event9999;
		}else if(event1005 != null ) {
			return event1005;
		}else  {
			return null;
		}
	}
	
	
	/**
	 * 超低速、超高速、停车
	 * 
	 * @param radarDOs  雷达数据
	 * @param targetIdSet 雷达数据ID的交集
	 * @param radarId
	 * @return
	 */
	public ZcLdRiskEventManage speedSuddenChange(RadarDO radarDO ,String radarId) {
		ZcLdRiskEventManage zcLdEventManage = null;
		
		//是否有急加、急减
		boolean isSpeedSuddenChange = false;
		if(speedSuddenChangeMap.get(radarId) != null) {
			List<RadarEventType> eventList = speedSuddenChangeMap.get(radarId);
			for(int k = 0; k < eventList.size(); k++) {
				RadarEventType radarEventType = eventList.get(k);
				isSpeedSuddenChange = false;
				JSONArray jsonArray = radarDO.getDataBody();
				for(int i = 0 ; i < jsonArray.size(); i++ ) {
					JSONObject jo = jsonArray.getJSONObject(i);
					int targetId = jo.getInt("targetId");
					if(radarEventType.getTargetId() == targetId) {
						if(StringUtils.equals(radarEventType.getEventType(), "1003")) {
							jo.put("eventType1003", "Y");
							isSpeedSuddenChange = true;
						}else if(StringUtils.equals(radarEventType.getEventType(), "1004")) {
							jo.put("eventType1004", "Y");
							isSpeedSuddenChange = true;
						}else if(StringUtils.equals(radarEventType.getEventType(), "8888")) {
							jo.put("eventType8888", "Y");
							isSpeedSuddenChange = true;
						}else if(StringUtils.equals(radarEventType.getEventType(), "1001")) {
							jo.put("eventType1001", "Y");
							isSpeedSuddenChange = true;
						}else if(StringUtils.equals(radarEventType.getEventType(), "1002")) {
							jo.put("eventType1002", "Y");
							isSpeedSuddenChange = true;
						}
					}
				}
				if(!isSpeedSuddenChange) {
					speedSuddenChangeMap.get(radarId).remove(k);
					k--;
				}
			}
		}
		
		if(radarDO.getDataBody() == null || radarDO.getDataBody().size() == 0) {
			radarAllMap.remove(radarId);
			return zcLdEventManage ;
		}
		//缓存所有雷达数据
		if(radarAllMap.containsKey(radarId)) {
			List<RadarDO> list = new ArrayList<>();
			list = radarAllMap.get(radarId);
			if(list == null) {
				list = new ArrayList<>();
			}
			list.add(radarDO);
			radarAllMap.put(radarId, list);
		}else {
			List<RadarDO> list = new ArrayList<>();
			list.add(radarDO);
			radarAllMap.put(radarId, list);
		}
		
		for(ZcLdEquipment equ : ldEquipmentList) {
			List<RadarDO> list = radarAllMap.get(equ.getId()) ;
			//是否有雷达已经缓存了28帧数据
			if(list != null && list.size() == cacheNum * 2) {
				List<RadarDO> list2 = new ArrayList<>();
				list2.addAll(list);
				RadarDO[] radarDOSNew1 = new RadarDO[cacheNum]; 
				RadarDO[] radarDOSNew2 = new RadarDO[cacheNum]; 
				for (int i = 0; i < list2.size(); i++) {
					if(i < cacheNum) {
						radarDOSNew1[i] = list2.get(i);
					}else {
						radarDOSNew2[i-cacheNum] = list2.get(i);
					}
				}
				//缓存后一秒的数据，继续和下一秒比较
				List<RadarDO> tempList = Arrays.asList(radarDOSNew2);
				radarAllMap.put(equ.getId(), new ArrayList(tempList) );
				zcLdEventManage =  radarData(radarDOSNew1,  radarDOSNew2 ,equ.getId());
				radarDOSNew1 = null;
				radarDOSNew2 = null;
			}
		}
		
		return zcLdEventManage;
	}
	
	/**
	 * 解析两个数组
	 * @param radarDOS1
	 * @param radarDOS2
	 * @return
	 */
	private ZcLdRiskEventManage radarData(RadarDO[] radarDOS1 , RadarDO[] radarDOS2 ,String radarId) {
		Map<String,JSONObject> tmap = new LinkedHashMap<>();
		Map<String,JSONObject> lastMap = new LinkedHashMap<>();
		
		//1001：超高速
		ZcLdRiskEventManage event1001 = null;
		//1002：超低速
		ZcLdRiskEventManage event1002 = null;
		//1003：急加速
		ZcLdRiskEventManage event1003 = null;
		//1004：急减速
		ZcLdRiskEventManage event1004 = null;
		//8888：停车
		ZcLdRiskEventManage event8888 = null;
		//8887：慢停
		ZcLdRiskEventManage event8887 = null;
		
		Date eventDate = new Date();
		
		List<String> newStopList = new ArrayList<>();
		
		try {
			List<Integer> idList = new ArrayList<>();
			for(int i = 0 ; i < radarDOS1.length ; i ++) {
				RadarDO radarDo = radarDOS1[i];
				eventDate = radarDo.getDate();
				JSONArray jsonArray = radarDo.getDataBody() ;
				for(int j = 0 ; j < jsonArray.size() ; j++ ) {
					JSONObject jo = jsonArray.getJSONObject(j);
					int targetId = jo.getInt("targetId");
					//所有 targetId 存入LIST
					idList.add(targetId);
				}
			}
			for(int i = 0 ; i < radarDOS2.length ; i ++) {
				RadarDO radarDo = radarDOS2[i];
				JSONArray jsonArray = radarDo.getDataBody() ;
				for(int j = 0 ; j < jsonArray.size() ; j++ ) {
					JSONObject jo = jsonArray.getJSONObject(j);
					int targetId = jo.getInt("targetId");
					//所有 targetId 存入LIST
					idList.add(targetId);
				}
			}
			
			int c = 0;
			Set<Integer> targetIdSet = new HashSet<>();
			//取出每一帧都有的ID
			for (int targetId : idList) {
				c = 0;
				for (int targetId2 : idList) {
					if(targetId == targetId2) {
						c++;
					}
				}
				if(c == cacheNum*2) {
					targetIdSet.add(targetId);
				}
			}
			
			//根据ID集合计算前一秒和后一秒的数据
			List<JSONObject> dataList = new ArrayList<>();
			for (int id : targetIdSet) {
				JSONObject job = new JSONObject();
				double avgSpeed = 0 ;
				String vyStr = StringUtils.EMPTY;
				String timeStr = StringUtils.EMPTY;
				c = 0;
				for (int i = 0 ; i < radarDOS1.length ; i++) {
					JSONArray jsonArray = radarDOS1[i].getDataBody() ;
					for(int j = 0 ; j < jsonArray.size() ; j++ ) {
						JSONObject jo = jsonArray.getJSONObject(j);
						int laneNum = jo.getInt("polynomialLane");
						int targetId = jo.getInt("targetId");
						double sy = jo.getDouble("sY") ;
						if(targetId == id) {
							c++;
							lastMap.put(targetId+"_"+c, jo); //缓存第一秒的数据用于计算超高速、超低速、停车
							double vy = Math.abs(jo.getDouble("vY"));
							avgSpeed += vy;
							vyStr += vy+",";
							if(i > 0) {
								timeStr +=  radarDOS1[i].getDate().getTime() - radarDOS1[i-1].getDate().getTime()+",";
							}
							if(i == radarDOS1.length -1 ) {
								job.put("startTime", radarDOS1[i].getDate().getTime());
								job.put("startSpeedAvg", avgSpeed / cacheNum);	//前一秒的平均速度
								job.put("startVyStr", vyStr);
								job.put("startTimeStr", timeStr);
								job.put("polynomialLane", laneNum);
								job.put("startSy", sy);
							}
						}
					}
				}
				avgSpeed = 0 ;
				vyStr = StringUtils.EMPTY;
				timeStr = StringUtils.EMPTY;
				c = 0;
				for (int i = 0 ; i < radarDOS2.length ; i++) {
					JSONArray jsonArray = radarDOS2[i].getDataBody() ;
					for(int j = 0 ; j < jsonArray.size() ; j++ ) {
						JSONObject jo = jsonArray.getJSONObject(j);
						int targetId = jo.getInt("targetId");
						if(targetId == id) {
							c++;
							tmap.put(targetId+"_"+c, jo); //缓存第二秒的数据用于计算超高速、超低速、停车
							double vy = Math.abs(jo.getDouble("vY"));
							avgSpeed += vy;
							vyStr += vy+",";
							if(i > 0) {
								timeStr +=  radarDOS2[i].getDate().getTime() - radarDOS2[i-1].getDate().getTime()+",";
							}
							if(i == radarDOS2.length -1 ) {
								job.put("endTime", radarDOS2[i].getDate().getTime());
								job.put("endSpeedAvg", avgSpeed / cacheNum);	//前一秒的平均速度
								job.put("endVyStr", vyStr);
								job.put("endTimeStr", timeStr);
								job.put("targetId", id);
							}
						}
					}
				}
				dataList.add(job);
			}
			
			List<RadarEventType> eventList = new ArrayList<>();
			for (JSONObject jo : dataList) {
				int laneNum = jo.getInt("polynomialLane");
				ZcLdLaneInfo lane = radarLaneInfoMap.get(radarId+"_"+laneNum) ;
				//拟合以后的车道不存在，或者是绿化车道，则不处理
				if(lane == null || StringUtils.equals(lane.getLaneRoad(), "0")) {
					continue;
				}
				String direction = lane.getLaneRoad().substring(0,1);
				// (V-V前) / (date-date前)
				if(jo.getDouble("endSpeedAvg") == null || jo.getDouble("startSpeedAvg") == null || jo.getLong("endTime") == null || jo.getLong("startTime") == null ) {
					continue;
				}
				double avgV = jo.getDouble("endSpeedAvg") - jo.getDouble("startSpeedAvg");
				double avgD = jo.getLong("endTime") - jo.getLong("startTime") ; // jo.getDouble("endTimeAvg") - jo.getDouble("startTimeAvg") ;
				double speed = (avgV/3.6) / (Double.valueOf(avgD)/1000);
				int targetId = jo.getInt("targetId");
				if( speed > smallCarRapidAcceleration ) {
					event1003 = new ZcLdRiskEventManage();
					event1003.setId(StringUtils.getUUID());
					event1003.setEventTime(eventDate);
					event1003.setRadarId(radarId);
					event1003.setEventType("1003");
					event1003.setEventLane(lane.getLaneRoad());
					event1003.setEventSy(jo.getDouble("startSy"));
					event1003.setValid("U");
					event1003.setEventPriorityLevel(3);
					
					
					RadarEventType radarEventType = new RadarEventType();
					radarEventType.setTargetId(targetId);
					radarEventType.setEventType("1003");
					eventList.add(radarEventType);
				}
				//小车急减速
				if( speed < smallCarRapidDeceleration ) {
					event1004 = new ZcLdRiskEventManage();
					event1004.setId(StringUtils.getUUID());
					event1004.setEventTime(eventDate);
					event1004.setRadarId(radarId);
					event1004.setEventType("1004");
					event1004.setEventLane(lane.getLaneRoad());
					event1004.setEventSy(jo.getDouble("startSy"));
					event1004.setValid("U");
					event1004.setEventPriorityLevel(3);
					
					RadarEventType radarEventType = new RadarEventType();
					radarEventType.setTargetId(targetId);
					radarEventType.setEventType("1004");
					eventList.add(radarEventType);
				}
			}
			
			//计算上一秒每个车道的平均车速
			Map<Integer,Double> lastLaneMap = new HashMap<>();
			Map<Integer,Integer> lastLaneMap2 = new HashMap<>();
			for (Map.Entry<String, JSONObject> entry : lastMap.entrySet()) {
				JSONObject jo = entry.getValue() ;
				int laneNum = jo.getInt("polynomialLane");
				double vy = Math.abs(jo.getDouble("vY"));
				int count = 1;
				if(lastLaneMap.get(laneNum) != null) {
					vy += lastLaneMap.get(laneNum);
					count += lastLaneMap2.get(laneNum);
				}
				lastLaneMap.put(laneNum, vy);
				lastLaneMap2.put(laneNum, count);
			}
			Map<Integer,Double> lastLaneAvgVyMap = new HashMap<>();
			for (Map.Entry<Integer, Double> entry : lastLaneMap.entrySet()) {
				lastLaneAvgVyMap.put(entry.getKey(), entry.getValue() / lastLaneMap2.get(entry.getKey()));
			}
			
			String lastKey = StringUtils.EMPTY;
			double sumVY = 0;
			int count = 0 ;
			int itemCount = 0 ;
			int mapSize = 0 ;
			int lastTargetId = -1;
			double lastSx = 0.0;
			double lastSy = 0.0;
			int lastLaneNum = 0 ;
			String lastLaneRoad = StringUtils.EMPTY;
			
			for (Map.Entry<String, JSONObject> entry : tmap.entrySet()) {
				mapSize++;
				String key = entry.getKey().split("_")[0];
				JSONObject jo = entry.getValue() ;
				int laneNum = jo.getInt("polynomialLane");
				ZcLdLaneInfo lane = radarLaneInfoMap.get(radarId+"_"+laneNum) ;
				//拟合以后的车道不存在，或者是绿化车道，则不处理
				if(lane == null || StringUtils.equals(lane.getLaneRoad(), "0")) {
					continue;
				}
				
				double vy = Math.abs(jo.getDouble("vY"));
				//一辆车遍历完毕或者遍历到最后一帧 ，开始计算二速
				if(!StringUtils.equals(lastKey, key) || mapSize == tmap.size()) {
					if(mapSize == tmap.size()) {
						itemCount++;
						lastTargetId = jo.getInt("targetId");
					}
					if(itemCount == radarDOS2.length) {
						//14帧都是0
						if(sumVY == 0) {
							if(StringUtils.equals(radarId, "1004") && lastSy > 100) {
								//1004雷达对着服务区，只在100米以内才计算停车事件
								continue;
							}
							
							//一个停车的坐标点，只触发一次停车事件
							boolean isSendEvent = false;
							String stopStr = lastSx + "," + lastSy;
							if(stopEventMap.get(radarId) != null) {
								List<String> oldStopList = stopEventMap.get(radarId);
								if(oldStopList.contains(stopStr)) {
									isSendEvent = false;
								}else {
									isSendEvent = true;
								}
							}else {
								isSendEvent = true;
							}
							newStopList.add(stopStr);
							if(isSendEvent) {
								event8888 = new ZcLdRiskEventManage();
								event8888.setId(StringUtils.getUUID());
								event8888.setEventTime(eventDate);
								event8888.setRadarId(radarId);
								event8888.setEventType("8888");
								event8888.setEventLane(lastLaneRoad);
								event8888.setEventSy(lastSy);
								event8888.setValid("U");
								event8888.setEventPriorityLevel(1);
							}
							
							RadarEventType radarEventType = new RadarEventType();
							radarEventType.setTargetId(lastTargetId);
							radarEventType.setEventType("8888");
							eventList.add(radarEventType);	//通知webSocket，前端显示
							logger.info(String.format("停车事件,雷达：{%s}；车道：{%s}；sy：{%s}；平均速度：{%s}；lastTargetId：{%s}", 
									radarId, lastLaneRoad, lastSy, 0, lastTargetId ));
							
						}
						//当一秒的平均速度在0~10之间时，也视为停车
						if((sumVY / count) > 0 && (sumVY / count) <= 10) {
							if(StringUtils.equals(radarId, "1004") && lastSy > 100) {
								//1004雷达对着服务区，只在100米以内才计算停车事件
								continue;
							}
							
							event8887 = new ZcLdRiskEventManage();
							event8887.setId(StringUtils.getUUID());
							event8887.setEventTime(eventDate);
							event8887.setRadarId(radarId);
							event8887.setEventType("8887");
							event8887.setEventLane(lastLaneRoad);
							event8887.setEventSy(lastSy);
							event8887.setValid("U");
							event8887.setEventPriorityLevel(1);
							
							logger.info(String.format("慢停事件,雷达：{%s}；车道：{%s}；sy：{%s}；平均速度：{%s}；lastTargetId：{%s}", 
									radarId, lastLaneRoad, lastSy, sumVY/count, lastTargetId ));
						}
						//大于130算超高速
						if((sumVY / count) > maxSpeed ) {
							event1001 = new ZcLdRiskEventManage();
							event1001.setId(StringUtils.getUUID());
							event1001.setEventTime(eventDate);
							event1001.setRadarId(radarId);
							event1001.setEventType("1001");
							event1001.setEventLane(lastLaneRoad);
							event1001.setEventSy(lastSy);
							event1001.setValid("U");
							event1001.setEventPriorityLevel(2);
							
							RadarEventType radarEventType = new RadarEventType();
							radarEventType.setTargetId(lastTargetId);
							radarEventType.setEventType("1001");
							eventList.add(radarEventType);
						}
						//超低速计算-版本1：小于60算超低速
						//超低速计算-版本2：条件一：当前车这一秒平均速度小于车道上一秒平均速度*60%；条件二：如果上一秒车道没有平均速度，则按60计算
//						if((sumVY / count) < minSpeed ) {
						boolean isLowSpeed = false;
						if(lastLaneAvgVyMap.get(lastLaneNum) == null) {
							if((sumVY / count) < minSpeed) {
								isLowSpeed = true;
							}
						}else {
							if((sumVY / count) < (lastLaneAvgVyMap.get(lastLaneNum) * 0.6) ) {
								isLowSpeed = true;
							}
						}
						if(isLowSpeed ) {
							if(StringUtils.equals(lane.getRadarType(), "102")) {	
								//节点雷达不计算超低速
								continue;
							}
							if(lastLaneAvgVyMap.get(lastLaneNum) == null) {
								logger.info(String.format("超低速,雷达：{%s}；车道：{%s}；sy：{%s}；上一秒车道平均速度：{%s}；上一秒车道平均速度*0.6：{%s}；平均速度：{%s}", 
										radarId, lastLaneRoad, lastSy, null, null, sumVY / count  ));
							}else {
								logger.info(String.format("超低速,雷达：{%s}；车道：{%s}；sy：{%s}；上一秒车道平均速度：{%s}；上一秒车道平均速度*0.6：{%s}；平均速度：{%s}", 
										radarId, lastLaneRoad, lastSy, lastLaneAvgVyMap.get(lastLaneNum), lastLaneAvgVyMap.get(lastLaneNum) * 0.6, sumVY / count  ));
							}
							
							event1002 = new ZcLdRiskEventManage();
							event1002.setId(StringUtils.getUUID());
							event1002.setEventTime(eventDate);
							event1002.setRadarId(radarId);
							event1002.setEventType("1002");
							event1002.setEventLane(lastLaneRoad);
							event1002.setEventSy(lastSy);
							event1002.setValid("U");
							event1002.setEventPriorityLevel(2);
							
							RadarEventType radarEventType = new RadarEventType();
							radarEventType.setTargetId(lastTargetId);
							radarEventType.setEventType("1002");
							eventList.add(radarEventType);
						}
					}
					sumVY = 0;
					count = 0;
					itemCount = 0;
				}
				
				if(vy > 0) {
					sumVY += vy;
					count++;
				}
				itemCount++;
				lastKey = key;
				lastTargetId = jo.getInt("targetId");
				lastSx = jo.getDouble("sX");
				lastSy = jo.getDouble("sY");
				lastLaneNum = laneNum;
				lastLaneRoad = lane.getLaneRoad();
			}
			if(!newStopList.isEmpty()) {
				stopEventMap.put(radarId, newStopList);
			}
			speedSuddenChangeMap.put(radarId, eventList);
		} catch (Exception e) {
			logger.error("急加速、急减速异常", e);
		} 
		
		if(event8887 != null) {
			//慢停
			return event8887;
		}else if(event8888 != null) {
			//停车
			return event8888;
		}else if(event1004 != null) {
			//急减速
			return event1004;
		}else if(event1003 != null) {
			//急加速
			return event1003;
		}else if(event1001 != null) {
			//超高速
			return event1001;
		}else if(event1002 != null) {
			//超低速
			return event1002;
		}else {
			return null;
		}
	}
	
	
	
}


