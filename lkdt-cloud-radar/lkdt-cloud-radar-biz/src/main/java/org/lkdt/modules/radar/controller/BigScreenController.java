package org.lkdt.modules.radar.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.lkdt.modules.radar.supports.radarDataService.radarCamUnion.CameraComponent;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.aspect.annotation.AutoLog;
import org.lkdt.common.util.DateUtils;
import org.lkdt.common.util.RedisUtil;
import org.lkdt.common.util.StringUtils;
import org.jfree.chart.JFreeChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONArray;
import org.lkdt.modules.radar.entity.ZcLdEquipment;
import org.lkdt.modules.radar.entity.ZcLdEventRadarInfo;
import org.lkdt.modules.radar.entity.ZcLdLaneInfo;
import org.lkdt.modules.radar.service.IFittingCurveService;
import org.lkdt.modules.radar.service.IZcLdEquipmentService;
import org.lkdt.modules.radar.service.IZcLdEventInfoService;
import org.lkdt.modules.radar.service.IZcLdEventRadarInfoService;
import org.lkdt.modules.radar.service.IZcLdLaneInfoService;
import org.lkdt.modules.radar.service.IZcLdRadarFrameLaneService;
import org.lkdt.modules.radar.service.IZcLdRadarFrameService;
import org.lkdt.modules.radar.supports.RadarDataWebSocket;
import org.lkdt.modules.radar.supports.mongodb.MongoRadarTemplate;
import org.lkdt.modules.radar.supports.radarTools.DO.MongoZcLdEventRadarInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

@Api(tags="大屏")
@RestController
@RequestMapping("/radar/bigScreen")
@Slf4j
public class BigScreenController {
	
	@Autowired
	private IZcLdLaneInfoService zcLdLaneInfoService;
	
	@Autowired
	private IZcLdEventRadarInfoService zcLdEventRadarInfoService;
	
	@Autowired
	private IZcLdEventInfoService zcLdEventInfoService;
	
	@Autowired
	private IFittingCurveService fittingCurveService;
	
	@Autowired
	private IZcLdRadarFrameService zcLdRadarFrameService;
	
	@Autowired
	private IZcLdRadarFrameLaneService zcLdRadarFrameLaneService;
	
	@Autowired
	private IZcLdEquipmentService zcLdEquipmentService ;
	
	@Value(value = "${lkdt.path.upload}")
	private String uploadPath;
	
	
	@Autowired
	private RedisUtil redisUtil;
	
	private static final String polynomialCurveData = "POLYNOMIAL-CURVE-DATA";
	private static final String polynomialCurveValue = "POLYNOMIAL-CURVE-VALUE";
	
	@Autowired
	MongoRadarTemplate mongoRadarTemplate;

	@Autowired
	CameraComponent cameraComponent;
	
	
	
	
	
	public List<String> getTimeList(String tjDateType,String dateTime,int longTime){
		List<String> timeList = new ArrayList<>();
		if(StringUtils.equals(tjDateType, "S")) {
			timeList = DateUtils.getAfterSecondList(DateUtils.str2Date(dateTime, DateUtils.datetimeFormat.get()), longTime);
		}else if(StringUtils.equals(tjDateType, "M")) {
			timeList = DateUtils.getAfterMinuteList(DateUtils.str2Date(dateTime, DateUtils.datetimeFormat.get()), longTime);
		}else if(StringUtils.equals(tjDateType, "H")){
			timeList = DateUtils.getAfterHoursList(DateUtils.str2Date(dateTime, DateUtils.datetimeFormat.get()), longTime);
		}
		return timeList;
	}
	
	public List<String> getLaneDirection(){
		List<String> list = new ArrayList<>();
		list.add("L");
		list.add("R");
		return list;
	}
	
	/**
	 * 获取折线报表数据（各车道）
	 * @param dataList
	 * @param laneList
	 * @param timeList
	 * @param valueKey
	 * @return
	 */
	public JSONObject getLineChartDataByLane(List<Map<String,Object>> dataList ,
			List<ZcLdLaneInfo> laneList ,List<String> timeList,String valueKey) {
		JSONObject jsonObject = new JSONObject();
		
		List<String> legendList = new ArrayList<>();
		JSONArray seriesArray = new JSONArray();
    	JSONObject seriesJson = new JSONObject();

    	boolean addZero = true;
		for(ZcLdLaneInfo lane : laneList) {
			if(StringUtils.equals(lane.getLaneRoad(), "0")) {
				continue;
			}
			legendList.add(lane.getLaneRoadName());
			seriesJson = new JSONObject();
			seriesJson.put("name", lane.getLaneRoadName());
			seriesJson.put("type", "line");
			seriesJson.put("smooth", "true");
//			seriesJson.put("stack", "总量");
			List<Double> seriesData = new ArrayList<>();
			for (String time : timeList) {
				addZero = true;
				for (Map<String, Object> map : dataList) {
					if(StringUtils.equals(lane.getLaneRadar().toString(), map.get("lane_num").toString())  && 
							StringUtils.equals(time, map.get("tj_date").toString())) {
						addZero = false;
						if(map.get(valueKey) != null) {
							seriesData.add( (double)Math.round(Double.parseDouble(map.get(valueKey).toString())*100)/100);
						}else {
							seriesData.add(0.0);
						}
					}
				}
				if(addZero) {
					seriesData.add(0.0);
				}
			}
			seriesJson.put("data", seriesData);
			seriesArray.add(seriesJson);
		}
		jsonObject.put("xAxis", timeList);
		jsonObject.put("legend", legendList);
		jsonObject.put("series", seriesArray);
		
    	return jsonObject;
	}
	
	/**
	 * 获取折线报表数据（单元路段--来去项）
	 * @param dataList
	 * @param laneList
	 * @param timeList
	 * @param valueKey
	 * @return
	 */
	public JSONObject getLineChartDataByUnit(List<Map<String,Object>> dataList ,
			List<String> laneList ,List<String> timeList,String valueKey) {
		JSONObject jsonObject = new JSONObject();
		
		List<String> legendList = new ArrayList<>();
		
		jsonObject.put("xAxis", timeList);
		
		JSONArray seriesArray = new JSONArray();
    	JSONObject seriesJson = new JSONObject();
    	boolean addZero = true;
		for(String direction : laneList) {
			String directionName = StringUtils.EMPTY;
			if(StringUtils.equals(direction, "L")) {
				directionName = "来向";
			}else if(StringUtils.equals(direction, "R")){
				directionName = "去向";
			}
			legendList.add(directionName);
			seriesJson = new JSONObject();
			seriesJson.put("name", directionName);
			seriesJson.put("type", "line");
			seriesJson.put("smooth", "true");
//			seriesJson.put("stack", "总量");
			List<Double> seriesData = new ArrayList<>();
			for (String time : timeList) {
				addZero = true;
				for (Map<String, Object> map : dataList) {
					if(StringUtils.equals(direction, map.get("lane_direction").toString()) && 
							StringUtils.equals(time, map.get("tj_date").toString())) {
						addZero = false;
						if(map.get(valueKey) != null) {
							seriesData.add( (double)Math.round(Double.parseDouble(map.get(valueKey).toString())*100)/100);
						}else {
							seriesData.add(0.0);
						}
					}
				}
				if(addZero) {
					seriesData.add(0.0);
				}
			}
			seriesJson.put("data", seriesData);
			seriesArray.add(seriesJson);
		}
		jsonObject.put("legend", legendList);
		jsonObject.put("series", seriesArray);
		
		return jsonObject;
	}
	
	
	@AutoLog(value = "查询雷达车道信息列表")
	@GetMapping(value = "/queryRadarLaneList")
    public Result<?> queryRadarLaneList(@RequestParam("equId") String equId){
    	List<ZcLdLaneInfo> list = zcLdLaneInfoService.queryByMainId(equId);
    	return Result.ok(list);
    }
	
	
	@AutoLog(value = "查询车型占比")
	@GetMapping(value = "/queryCarTypeRatio")
    public Result<?> queryCarTypeRatio(String equId,String beginTime,String endTime){
    	List<Map<String,Object>> list = zcLdEventRadarInfoService.queryCarTypeRatio(equId,beginTime,endTime);
    	for (Map<String, Object> map : list) {
    		if(StringUtils.equals(map.get("name").toString(), "1")) {
    			map.put("name", "小车");
    		}else if(StringUtils.equals(map.get("name").toString(), "2")) {
    			map.put("name", "超长车");
    		}else if(StringUtils.equals(map.get("name").toString(), "3")) {
    			map.put("name", "大车");
    		}
		}
    	return Result.ok(list);
    }
	
	@AutoLog(value = "按车道统计")
	@GetMapping(value = "/querylaneStatistics")
    public Result<?> querylaneStatistics(String equId,String beginTime,String endTime){
    	List<Map<String,Object>> list = zcLdEventRadarInfoService.querylaneStatistics(equId,beginTime,endTime);
    	for (Map<String, Object> map : list) {
    		//暂定平均时速保留一位小数
    		map.put("speedAvg", (double)Math.round(Double.parseDouble(map.get("speedAvg").toString())*10)/10);
    		
    		// TODO: 暂无数据，用随机数
    		map.put("avgTH", (double)Math.round(Math.random()*100)/10);
		}
    	return Result.ok(list);
    }
	
	
	@AutoLog(value = "查询事件占比")
	@GetMapping(value = "/queryEventRatio")
    public Result<?> queryEventRatio(String equId){
		List<Map<String,Object>> list = zcLdEventInfoService.queryEventRatio(equId);
		for (Map<String, Object> map : list) {
			if(StringUtils.equals(map.get("name").toString(), "0")) {
    			map.put("name", "无事件");
    		}else if(StringUtils.equals(map.get("name").toString(), "1")) {
    			map.put("name", "车辆停止");
    		}else if(StringUtils.equals(map.get("name").toString(), "2")) {
    			map.put("name", "逆行");
    		}else if(StringUtils.equals(map.get("name").toString(), "3")) {
    			map.put("name", "超速行驶");
    		}else if(StringUtils.equals(map.get("name").toString(), "5")) {
    			map.put("name", "实线变道");
    		}else if(StringUtils.equals(map.get("name").toString(), "101")) {
    			map.put("name", "停止消失");
    		}else if(StringUtils.equals(map.get("name").toString(), "102")) {
    			map.put("name", "逆行消失");
    		}else if(StringUtils.equals(map.get("name").toString(), "103")) {
    			map.put("name", "超速行驶消失");
    		}else if(StringUtils.equals(map.get("name").toString(), "105")) {
    			map.put("name", "实线变道消失");
    		}
		}
    	return Result.ok(list);
    }
	
	
	@AutoLog(value = "按小时统计N小时前至上一个小时的车流量和平均时速")
	@GetMapping(value = "/queryFlowAndSpeed")
    public Result<?> queryFlowAndSpeed(String equId,Integer hours){
		List<Map<String,Object>> returnList = new ArrayList<>();
		Map<String,Object> returnMap = new HashMap<>();
		if(hours == null) {
			hours = 12 ;
		}
		if(hours > 48) {
			hours = 48;
		}
		hours++;
		List<String> hoursList = DateUtils.getBeforeHoursList(new Date(), hours);
		
		List<Map<String,Object>> list = zcLdEventRadarInfoService.queryFlowAndSpeed(equId,hours);
		
		for (int i = 0; i < (hoursList.size() -1) ; i++) {
			String hDate = hoursList.get(i);
			returnMap = new HashMap<>();
			boolean isData = false;
			for (Map<String, Object> map : list) {
				if(StringUtils.equals(map.get("begin_time").toString(), hDate)) {
					isData = true;
					returnMap.put("type", formatDate(hDate));
					returnMap.put("count", Integer.parseInt(map.get("data_count").toString()));
					//暂定平均时速保留一位小数
					returnMap.put("avg", (double)Math.round(Double.parseDouble(map.get("speed_avg").toString())*10)/10);
				}
			}
			if(!isData) {
				returnMap.put("type", formatDate(hDate));
				returnMap.put("count", 0);
				returnMap.put("avg", 0);
			}
			returnList.add(returnMap);
		}
		
    	
    	return Result.ok(returnList);
    }
	
	private String formatDate(String date) {
		String returnDate = date.substring(8,10) + "日" + date.substring(11,13) + "时";
		return returnDate;
	}
	
	@AutoLog(value = "按分钟统计车流量和平均时速")
	@GetMapping(value = "/queryFlowAndSpeedGroupByMinutes")
    public Result<?> queryFlowAndSpeedGroupByMinutes(String equId,String beginTime,String endTime){
		List<Map<String,Object>> returnList = new ArrayList<>();
		Map<String,Object> returnMap = new HashMap<>();
		
		List<Map<String,Object>> list = zcLdEventRadarInfoService.queryFlowAndSpeedGroupByMinutes(equId,beginTime,endTime);
		for (Map<String, Object> map : list) {
			returnMap = new HashMap<>();
			returnMap.put("type", map.get("begin_time").toString());
			returnMap.put("count", Integer.parseInt(map.get("data_count").toString()));
			//暂定平均时速保留一位小数
			returnMap.put("avg", (double)Math.round(Double.parseDouble(map.get("speed_avg").toString())*10)/10);
			
			returnList.add(returnMap);
		}
		return Result.ok(returnList);
	}
	
	
	@AutoLog(value = "按秒统计车流量和平均时速")
	@GetMapping(value = "/queryFlowAndSpeedGroupBySecond")
    public Result<?> queryFlowAndSpeedGroupBySecond(String equId,String beginTime,String endTime){
		List<Map<String,Object>> returnList = new ArrayList<>();
		Map<String,Object> returnMap = new HashMap<>();
		
		List<Map<String,Object>> list = zcLdEventRadarInfoService.queryFlowAndSpeedGroupBySecond(equId,beginTime,endTime);
		for (Map<String, Object> map : list) {
			returnMap = new HashMap<>();
			returnMap.put("type", map.get("begin_time").toString());
			returnMap.put("count", Integer.parseInt(map.get("data_count").toString()));
			//暂定平均时速保留一位小数
			returnMap.put("avg", (double)Math.round(Double.parseDouble(map.get("speed_avg").toString())*10)/10);
			
			returnList.add(returnMap);
		}
		return Result.ok(returnList);
	}
	
	
	/**
	 * 获取多项式拟合曲线图表
	 * @param equId 雷达ID
	 * @param laneId 车道ID
	 * @param laneNum	雷达车道号
	 * @param direction 车道方向
	 * @param longTime	获取多少时间的数据（毫秒）
	 * @param pnum 多项式方程次数
	 * @param width		图表的宽度
	 * @param height	图表的高度
	 * @param imgName	图表的名称
	 * @return
	 */
	@AutoLog(value = "获取多项式拟合曲线图表")
	@GetMapping(value = "/queryPolynomialCurve")
    public Result<?> queryPolynomialCurve(@RequestParam("equId") String equId,
    		@RequestParam("laneId") String laneId,
    		@RequestParam("laneNum") String laneNum,
    		@RequestParam("direction") String direction,
    		@RequestParam("longTime") Long longTime,
    		@RequestParam("pnum") Integer pnum,
    		Integer width,Integer height,String imgName){
		try{
			if(width == null) {
				width = 500;
			}
			if(height == null) {
				height = 270;
			}
			if(imgName == null) {
				imgName = laneId;
			}
			String outputPath = uploadPath+"/"+imgName+".png";
			
			List<List<Double>> data = new ArrayList<>();
			data = getLaneData(laneNum,longTime,equId,direction);
			if(data == null || data.get(0).size() == 0) {
				return Result.ok("");
			}
			
			redisUtil.del(polynomialCurveData+"-"+laneId+"-Y");
			redisUtil.set(polynomialCurveData+"-"+laneId+"-Y", data.get(0).toString());
			
			redisUtil.del(polynomialCurveData+"-"+laneId+"-X");
			redisUtil.set(polynomialCurveData+"-"+laneId+"-X", data.get(1).toString());
			
			Map<String,Object> map = fittingCurveService.queryCategoryData(data, pnum);
	
			double[] equation = (double[])map.get("equation");
			
			redisUtil.del(polynomialCurveValue+laneId);
			redisUtil.set(polynomialCurveValue+laneId, Arrays.toString(equation));
			
			JFreeChart chart = fittingCurveService.getChart(data,equation,pnum);
			//将JFreeChart对象输出到文件    
			String returnPath = fittingCurveService.saveAsFile(chart, outputPath, width, height);  
			return Result.ok(returnPath);
		}catch(Exception ex){
			ex.printStackTrace();
			return Result.error("发生异常");
		}
	}
	
	/**
	 * 拟合数据
	 * @param laneNum
	 * @param longTime
	 * @param equId
	 * @return
	 */
	private List<List<Double>> getLaneData(String laneNum,Long longTime,String equId, String direction ){
		List<List<Double>> list = new ArrayList<>();
		List<JSONArray> dataList = new ArrayList<>(); 
		// x为x轴坐标
		List<Double> x = new ArrayList<Double>();
		List<Double> y = new ArrayList<Double>();
		
		Date endTime = new Date();
		Date beginTime = DateUtils.getDate((endTime.getTime() - longTime));
		
		SimpleDateFormat sdf_ymd = new SimpleDateFormat(mongoRadarTemplate.str_ymd);
		List<ZcLdEventRadarInfo> infoList = zcLdEventRadarInfoService.queryRadarInfoPathTrace(equId, DateUtils.date2Str(beginTime, DateUtils.datetimeFormat.get()) , DateUtils.date2Str(endTime, DateUtils.datetimeFormat.get()) ,direction);
		boolean isLaneDate = false;
		for (ZcLdEventRadarInfo info : infoList) {
			isLaneDate = false;
			MongoZcLdEventRadarInfo ldEventRadarInfo = mongoRadarTemplate.
					find(mongoRadarTemplate.MongoZcLdEventRadarInfo_ + sdf_ymd.format(info.getCreateTime()), info.getId());
			if(ldEventRadarInfo != null){
				JSONArray jsonArray = JSONArray.parseArray(ldEventRadarInfo.getPathTrace());
				for (Object object : jsonArray) {
        			JSONObject jo = JSONObject.parseObject(object.toString());
        			double sy = jo.getDoubleValue("sY");
        			if( (sy > 130 && sy < 160) && StringUtils.equals(jo.getString("laneNum"), laneNum) ) {
        				isLaneDate = true;
        			}
				}
				if(isLaneDate) {
					dataList.add(jsonArray);
				}
			}
		}
		
		ZcLdEquipment equ = zcLdEquipmentService.getById(equId);
		if(!StringUtils.isEmpty(equ.getPolynomialY())) {
			String[] py = equ.getPolynomialY().split("-");
			double miny = Double.valueOf(py[0]);
			double maxy = Double.valueOf(py[1]);
			for (JSONArray jsonArray : dataList) {
				for (Object object : jsonArray) {
					JSONObject jo = JSONObject.parseObject(object.toString());
					if(Double.parseDouble(jo.getString("sY")) >= miny && Double.parseDouble(jo.getString("sY")) <= maxy) {
						y.add(Double.parseDouble(jo.getString("sX")));
			 			x.add(Double.parseDouble(jo.getString("sY")));
					}
				}
			}
		}else {
			for (JSONArray jsonArray : dataList) {
				for (Object object : jsonArray) {
					JSONObject jo = JSONObject.parseObject(object.toString());
					y.add(Double.parseDouble(jo.getString("sX")));
		 			x.add(Double.parseDouble(jo.getString("sY")));
				}
			}
		}
		
		
		
        list.add(x);
		list.add(y);
		return list;
	}
	
	
	/**
	 * 更新车道拟合字段
	 * @param equId			雷达设备ID
	 * @param laneId		车道ID
	 * @param direction		车道方向
	 * @param laneMiddleWidth	车道中心线距离雷达的宽度
	 * @param type			类型（ALL:更新同方向的所有车道；更新当前车道）
	 * @return
	 */
	@AutoLog(value = "更新车道拟合字段")
	@ApiOperation(value="更新车道拟合字段", notes="更新车道拟合字段")
	@PostMapping(value = "/updateLanePolynomial")
	public Result<?> updateLanePolynomial(@RequestBody HashMap<String, Object> params ) {
		
		if(params.get("equId") == null || 
				params.get("laneId") == null || 
				params.get("direction") == null || 
				params.get("laneMiddleWidth") == null || 
				params.get("type") == null ||
				params.get("pnum") == null ||
				params.get("polynomialNum") == null ||
				params.get("polynomialTime") == null ||
				params.get("laneRadar") == null  ) {
			return Result.error("缺少参数");
		}
		String equId = params.get("equId").toString();
		String laneId = params.get("laneId").toString();
		String direction = params.get("direction").toString();
		Integer laneMiddleWidth = Integer.parseInt(params.get("laneMiddleWidth").toString());
		String type = params.get("type").toString();
		Integer pnum = Integer.parseInt(params.get("pnum").toString());
		Integer polynomialNum = Integer.parseInt(params.get("polynomialNum").toString());
		Integer polynomialTime = Integer.parseInt(params.get("polynomialTime").toString());
		Integer laneRadar = Integer.parseInt(params.get("laneRadar").toString());
		
		ZcLdEquipment equ = zcLdEquipmentService.getById(equId);
		ZcLdLaneInfo laneInfo = zcLdLaneInfoService.getById(laneId);
		
		//指定车道
		String value = redisUtil.get(polynomialCurveValue+laneId).toString();
		value = value.replace("[", "");
		value = value.replace("]", "");
		String[] values = value.split(",");
		double[] carEquation = new double[values.length];
		double[] laneEquation = new double[values.length];
		
		String[] radarSideLineStrs = equ.getRadarSideLine().split(",");
		double[] radarSideLines = new double[radarSideLineStrs.length];
		for(int i = 0 ; i < radarSideLineStrs.length ;i++) {
			radarSideLines[i] = Double.valueOf(radarSideLineStrs[i]);
		}
		
		//雷达距离该车道中心点的距离
		double laneMid = 0;
		double sikeLane = 0;
		List<ZcLdLaneInfo> list = zcLdLaneInfoService.queryByMainId(equId);
		laneMid = Double.valueOf(laneInfo.getLineOutermostWidth()) - Double.valueOf(laneInfo.getLaneWidth()) / 2 ;
		sikeLane = Double.valueOf(laneInfo.getLineOutermostWidth()) ;
		if(StringUtils.equals(equ.getRadarInstallLaneDirection(), "R")) {
			//中心点=ABS(雷达距离最外层应急车道的距离+坐标偏移) - 路面宽度
			laneMid = (Math.abs(radarSideLines[0] + equ.getLaneCoordinateCorrection()*100) -  laneMid ) / 100;
			sikeLane = (Math.abs(radarSideLines[0] + equ.getLaneCoordinateCorrection()*100) - sikeLane ) / 100;
		}else if(StringUtils.equals(equ.getRadarInstallLaneDirection(), "L")){
			laneMid += radarSideLines[0]; //加上雷达距离最左侧应急车道距离
			sikeLane += radarSideLines[0];
			laneMid = (laneMid - equ.getLaneCoordinateCorrection()*100) / 100;	//减去雷达坐标偏移，即为车道中心点
			sikeLane = (sikeLane - equ.getLaneCoordinateCorrection()*100) / 100;
		}else if(StringUtils.equals(equ.getRadarInstallLaneDirection(), "M")){	//双向雷达，雷达安装在中间绿岛
			if(laneInfo.getLaneRoad().indexOf("L") > -1) {	//来向车道
				laneMid += radarSideLines[0]; //加上雷达距离最左侧应急车道距离
				sikeLane += radarSideLines[0];
				laneMid = (laneMid - equ.getLaneCoordinateCorrection()*100) / 100;	//减去雷达坐标偏移，即为车道中心点
				sikeLane = (sikeLane - equ.getLaneCoordinateCorrection()*100) / 100;
			}else if(laneInfo.getLaneRoad().indexOf("R") > -1) {	//去向车道
				//中心点=ABS(雷达距离最外层应急车道的距离+坐标偏移) - 路面宽度
				laneMid = (Math.abs(radarSideLines[0] + equ.getLaneCoordinateCorrection()*100) -  laneMid ) / 100;
				sikeLane = (Math.abs(radarSideLines[0] + equ.getLaneCoordinateCorrection()*100) - sikeLane ) / 100;
			}
		}
		
		for (int i = 0; i < values.length; i++) {
			if( i == values.length - 1 ) {
				carEquation[i] = laneMid;
				laneEquation[i] = sikeLane;
			}else {
				carEquation[i] = Double.parseDouble(values[i]);
				laneEquation[i] = Double.parseDouble(values[i]);
			}
		}
		
		double[] laneEquationWC = new double[laneEquation.length] ;
		System.arraycopy(laneEquation, 0, laneEquationWC, 0, laneEquation.length);
		String laneEquationStr = StringUtils.EMPTY;
		
		if(StringUtils.equals(type, "ALL")) {
			double half = 0;
			//计算超车道与应急车道，两个车道宽度的差值
			double yj = 0,chao = 0;
			for(int i = 0 ; i < list.size() ; i++ ) {
				ZcLdLaneInfo lane = list.get(i);
				if(StringUtils.equals(lane.getLaneRoad().substring(0,1), direction)) {
					if(lane.getLaneRoad().indexOf("-YJ") > -1) {
						yj = lane.getLaneWidth();
					}
					if(lane.getLaneRoad().indexOf("-C") > -1) {
						chao = lane.getLaneWidth();
					}
				}
			}
			half = Math.abs(chao-yj) / 2 ;
			half = half / 100;
			
			for(int i = 0 ; i < list.size() ; i++ ) {
				ZcLdLaneInfo lane = list.get(i);
				if(StringUtils.equals(lane.getLaneRoad().substring(0,1), direction)) {
					if(StringUtils.equals(lane.getId(), laneId)) {
						//雷达安装位置处于雷达监测方向的右侧
						if(StringUtils.equals(equ.getRadarInstallLaneDirection(), "R")) {
							laneEquationWC[laneEquationWC.length-1] = (sikeLane*1000 + Double.valueOf(laneInfo.getLaneWidth()*10)) / 1000  ;
							laneEquationStr = Arrays.toString(laneEquation)+";"+Arrays.toString(laneEquationWC) ;
						}else if(StringUtils.equals(equ.getRadarInstallLaneDirection(), "L")){	//雷达安装位置处于雷达监测方向的左侧
							laneEquationWC[laneEquationWC.length-1] = (sikeLane*1000 - Double.valueOf(laneInfo.getLaneWidth()*10)) / 1000  ;
							laneEquationStr = Arrays.toString(laneEquationWC)+";"+Arrays.toString(laneEquation) ;
						}else if(StringUtils.equals(equ.getRadarInstallLaneDirection(), "M")){	//双向雷达，雷达安装在中间绿岛
							if(laneInfo.getLaneRoad().indexOf("L") > -1) {	//来向车道
								laneEquationWC[laneEquationWC.length-1] = (sikeLane*1000 - Double.valueOf(laneInfo.getLaneWidth()*10)) / 1000  ;
								laneEquationStr = Arrays.toString(laneEquationWC)+";"+Arrays.toString(laneEquation) ;
							}else if(laneInfo.getLaneRoad().indexOf("R") > -1) {	//去向车道
								laneEquationWC[laneEquationWC.length-1] = (sikeLane*1000 + Double.valueOf(laneInfo.getLaneWidth()*10)) / 1000  ;
								laneEquationStr = Arrays.toString(laneEquation)+";"+Arrays.toString(laneEquationWC) ;
							}
						}
						
						ZcLdLaneInfo zcLdLaneInfo = new ZcLdLaneInfo();
						zcLdLaneInfo.setId(laneId);
						zcLdLaneInfo.setLaneTrack(Arrays.toString(carEquation));
						zcLdLaneInfo.setPolynomialNum(polynomialNum);
						zcLdLaneInfo.setPolynomialTime(polynomialTime);
						zcLdLaneInfo.setSideLineTrack(laneEquationStr);
						zcLdLaneInfoService.updateById(zcLdLaneInfo);
					}else {
						double[] carEquation2 = new double[carEquation.length];  
						System.arraycopy(carEquation, 0, carEquation2, 0, carEquation.length);
						double[] laneEquation2 = new double[laneEquation.length] ;
						System.arraycopy(laneEquation, 0, laneEquation2, 0, laneEquation.length);
						double width = Double.valueOf(laneMiddleWidth - lane.getLaneMiddleWidth()) /1000;	//横移的宽度
						
						//雷达安装位置处于雷达监测方向的右侧
						if(StringUtils.equals(equ.getRadarInstallLaneDirection(), "R")) {
							carEquation2[carEquation2.length-1] = (laneMid*1000 + width*1000)/1000 ;
							//普通车道与应急车道存在0.25的宽度差
							if(lane.getLaneRoad().indexOf("YJ") > -1) {
								//应急车道内边车道标线
								laneEquation2[laneEquation2.length-1] = (sikeLane*1000 + width*1000)/1000 + half ;
								//应急车道外侧车道标线
								laneEquationWC[laneEquationWC.length-1] = (sikeLane*1000 + width*1000)/1000 + half + Double.valueOf(lane.getLaneWidth()) / 100 ;
							}else {
								laneEquation2[laneEquation2.length-1] = (sikeLane*1000 + width*1000)/1000 ;
								laneEquationWC[laneEquationWC.length-1] = (sikeLane*1000 + width*1000)/1000 + Double.valueOf(lane.getLaneWidth()) / 100 ;
							}
							laneEquationStr = Arrays.toString(laneEquation2)+";"+Arrays.toString(laneEquationWC) ;
						}else if(StringUtils.equals(equ.getRadarInstallLaneDirection(), "L")){  //雷达安装位置处于雷达监测方向的左侧
							carEquation2[carEquation2.length-1] = (laneMid*1000 - width*1000)/1000 ;
							//普通车道与应急车道存在0.25的宽度差
							if(lane.getLaneRoad().indexOf("YJ") > -1) {
								//应急车道内边车道标线
								laneEquation2[laneEquation2.length-1] = (sikeLane*1000 - width*1000)/1000 - half ;
								//应急车道外侧车道标线
								laneEquationWC[laneEquationWC.length-1] = (sikeLane*1000 - width*1000)/1000 - half - Double.valueOf(lane.getLaneWidth()) / 100 ;
							}else {
								laneEquation2[laneEquation2.length-1] = (sikeLane*1000 - width*1000)/1000 ;
								laneEquationWC[laneEquationWC.length-1] = (sikeLane*1000 - width*1000)/1000 - Double.valueOf(lane.getLaneWidth()) / 100 ;
							}
							laneEquationStr = Arrays.toString(laneEquationWC)+";"+Arrays.toString(laneEquation2) ;
						}else if(StringUtils.equals(equ.getRadarInstallLaneDirection(), "M")) {		//双向雷达，雷达安装在中间绿岛
							if(laneInfo.getLaneRoad().indexOf("L") > -1) {	//来向车道
								carEquation2[carEquation2.length-1] = (laneMid*1000 + width*1000)/1000 ;
								//普通车道与应急车道存在0.25的宽度差
								if(lane.getLaneRoad().indexOf("YJ") > -1) {
									//应急车道内边车道标线
									laneEquation2[laneEquation2.length-1] = (sikeLane*1000 + width*1000)/1000 - half ;
									//应急车道外侧车道标线
									laneEquationWC[laneEquationWC.length-1] = (sikeLane*1000 + width*1000)/1000 - half - Double.valueOf(lane.getLaneWidth()) / 100 ;
								}else {
									laneEquation2[laneEquation2.length-1] = (sikeLane*1000 + width*1000)/1000 ;
									laneEquationWC[laneEquationWC.length-1] = (sikeLane*1000 + width*1000)/1000 - Double.valueOf(lane.getLaneWidth()) / 100 ;
								}
								laneEquationStr = Arrays.toString(laneEquationWC)+";"+Arrays.toString(laneEquation2) ;
							}else if(laneInfo.getLaneRoad().indexOf("R") > -1) {	//去向车道
								carEquation2[carEquation2.length-1] = (laneMid*1000 - width*1000)/1000 ;
								//普通车道与应急车道存在0.25的宽度差
								if(lane.getLaneRoad().indexOf("YJ") > -1) {
									//应急车道内边车道标线
									laneEquation2[laneEquation2.length-1] = (sikeLane*1000 - width*1000)/1000 + half ;
									//应急车道外侧车道标线
									laneEquationWC[laneEquationWC.length-1] = (sikeLane*1000 - width*1000)/1000 + half + Double.valueOf(lane.getLaneWidth()) / 100 ;
								}else {
									laneEquation2[laneEquation2.length-1] = (sikeLane*1000 - width*1000)/1000 ;
									laneEquationWC[laneEquationWC.length-1] = (sikeLane*1000 - width*1000)/1000 + Double.valueOf(lane.getLaneWidth()) / 100 ;
								}
								laneEquationStr = Arrays.toString(laneEquation2)+";"+Arrays.toString(laneEquationWC) ;
							}
						}
						
						ZcLdLaneInfo zcLdLaneInfo = new ZcLdLaneInfo();
						zcLdLaneInfo.setId(lane.getId());
						zcLdLaneInfo.setLaneTrack(Arrays.toString(carEquation2));
						zcLdLaneInfo.setPolynomialNum(polynomialNum);
						zcLdLaneInfo.setPolynomialTime(polynomialTime);
						zcLdLaneInfo.setSideLineTrack(laneEquationStr);
						zcLdLaneInfoService.updateById(zcLdLaneInfo);
					}
				}
			}
			//取一个方向的所有车道
		}else {
			//雷达安装位置处于雷达监测方向的右侧
			if(StringUtils.equals(equ.getRadarInstallLaneDirection(), "R")) {
				laneEquationWC[laneEquationWC.length-1] = (sikeLane*1000 + Double.valueOf(laneInfo.getLaneWidth()*10)) / 1000 ;
				laneEquationStr = Arrays.toString(laneEquation)+";"+Arrays.toString(laneEquationWC) ;
			}else if(StringUtils.equals(equ.getRadarInstallLaneDirection(), "L")) {
				laneEquationWC[laneEquationWC.length-1] = (sikeLane*1000 - Double.valueOf(laneInfo.getLaneWidth()*10)) / 1000 ;
				laneEquationStr = Arrays.toString(laneEquationWC)+";"+Arrays.toString(laneEquation) ;
			}else if(StringUtils.equals(equ.getRadarInstallLaneDirection(), "M")){	//双向雷达，雷达安装在中间绿岛
				if(laneInfo.getLaneRoad().indexOf("L") > -1) {	//来向车道
					laneEquationWC[laneEquationWC.length-1] = (sikeLane*1000 + Double.valueOf(laneInfo.getLaneWidth()*10)) / 1000 ;
					laneEquationStr = Arrays.toString(laneEquation)+";"+Arrays.toString(laneEquationWC) ;
				}else if(laneInfo.getLaneRoad().indexOf("R") > -1) {	//去向车道
					laneEquationWC[laneEquationWC.length-1] = (sikeLane*1000 - Double.valueOf(laneInfo.getLaneWidth()*10)) / 1000 ;
					laneEquationStr = Arrays.toString(laneEquationWC)+";"+Arrays.toString(laneEquation) ;
				}
			}
			
			//指定车道
			ZcLdLaneInfo zcLdLaneInfo = new ZcLdLaneInfo();
			zcLdLaneInfo.setId(laneId);
			zcLdLaneInfo.setLaneTrack(Arrays.toString(carEquation));
			zcLdLaneInfo.setPolynomialNum(polynomialNum);
			zcLdLaneInfo.setPolynomialTime(polynomialTime);
			zcLdLaneInfo.setSideLineTrack(laneEquationStr);
			zcLdLaneInfoService.updateById(zcLdLaneInfo);
		}
		
//		if(StringUtils.equals(type, "ALL")) {
//			List<List<Double>> data = new ArrayList<>();
//			List<Double> yList = new ArrayList<>();
//			List<Double> xList = new ArrayList<>();
//			String yStr = redisUtil.get(polynomialCurveData+"-"+laneId+"-Y").toString();
//			yStr = yStr.replace("[", "");
//			yStr = yStr.replace("]", "");
//			String[] yStrs = yStr.split(",");
//			for (int i = 0; i < yStrs.length; i++) {
//				yList.add(Double.parseDouble(yStrs[i]));
//			}
//			
//			String xStr = redisUtil.get(polynomialCurveData+"-"+laneId+"-X").toString();
//			xStr = xStr.replace("[", "");
//			xStr = xStr.replace("]", "");
//			String[] xStrs = xStr.split(",");
//			for (int i = 0; i < xStrs.length; i++) {
//				xList.add(Double.parseDouble(xStrs[i]));
//			}
//			
//			//查询所有车道
//			List<ZcLdLaneInfo> list = zcLdLaneInfoService.queryByMainId(equId);
//			for (ZcLdLaneInfo lane : list) {
//				if(StringUtils.equals(lane.getLaneRoad().substring(0,1), direction)) {
//					if(StringUtils.equals(lane.getId(), laneId)) {
//						ZcLdLaneInfo zcLdLaneInfo = new ZcLdLaneInfo();
//						zcLdLaneInfo.setId(laneId);
//						zcLdLaneInfo.setLaneTrack(value);
//						zcLdLaneInfo.setPolynomialNum(polynomialNum);
//						zcLdLaneInfo.setPolynomialTime(polynomialTime);
//						zcLdLaneInfoService.updateById(zcLdLaneInfo);
//					}else {
//						double width = (lane.getLaneMiddleWidth() - laneMiddleWidth) /100;	//横移的宽度
//						List<Double> x = new ArrayList<>();
//						for (Double d : xList) {
//							x.add(d+width);
//						}
//						List<List<Double>> newData = new ArrayList<>();
//						newData.add(yList);
//						newData.add(x);
//						
//						Map<String,Object> map = fittingCurveService.queryCategoryData(newData, pnum);
//						double[] equation = (double[])map.get("equation");
//						
//						ZcLdLaneInfo zcLdLaneInfo = new ZcLdLaneInfo();
//						zcLdLaneInfo.setId(lane.getId());
//						zcLdLaneInfo.setLaneTrack(Arrays.toString(equation));
//						zcLdLaneInfo.setPolynomialNum(polynomialNum);
//						zcLdLaneInfo.setPolynomialTime(polynomialTime);
//						zcLdLaneInfoService.updateById(zcLdLaneInfo);
//					}
//				}
//			}
//			//取一个方向的所有车道
//		}else {
//			//指定车道
//			ZcLdLaneInfo zcLdLaneInfo = new ZcLdLaneInfo();
//			zcLdLaneInfo.setId(laneId);
//			zcLdLaneInfo.setLaneTrack(value);
//			zcLdLaneInfo.setPolynomialNum(polynomialNum);
//			zcLdLaneInfo.setPolynomialTime(polynomialTime);
//			zcLdLaneInfoService.updateById(zcLdLaneInfo);
//		}
		
		return Result.ok("入库成功!");
	}
	
	
	
	
	
	@AutoLog(value = "统计（单元路段）大车占比度，区分来、去向和车型")
	@GetMapping(value = "/queryUnitRoadCarTypeByDirection")
    public Result<?> queryUnitRoadCarTypeByDirection(@RequestParam("equId") String equId,
    		@RequestParam("dateTime") String dateTime,
    		@RequestParam("tjDateType") String tjDateType,
    		@RequestParam("longTime") Integer longTime){
		
		List<Map<String,Object>> list = zcLdRadarFrameService.queryUnitRoadCarTypeByDirection(equId,dateTime,tjDateType);
		List<String> laneList = getLaneDirection();
		List<String> timeList = getTimeList(tjDateType, dateTime, longTime);
		
		JSONObject jsonObject = getLineChartDataByUnit(list, laneList, timeList, "big_avg");
    	return Result.ok(jsonObject);
    }
	
	@AutoLog(value = "统计（单元路段）平均车速，区分来、去向")
	@GetMapping(value = "/queryUnitAvgSpeed")
    public Result<?> queryUnitAvgSpeed(@RequestParam("equId") String equId,
    		@RequestParam("dateTime") String dateTime,
    		@RequestParam("tjDateType") String tjDateType,
    		@RequestParam("longTime") Integer longTime){
		
		List<Map<String,Object>> list = zcLdEventRadarInfoService.queryUnitAvgSpeed(equId,dateTime,tjDateType);
		List<String> laneList = getLaneDirection();
		List<String> timeList = getTimeList(tjDateType, dateTime, longTime);
		
		JSONObject jsonObject = getLineChartDataByUnit(list, laneList, timeList, "speed_avg");
    	return Result.ok(jsonObject);
    }
	
	
	@AutoLog(value = "统计（各车道）平均车速")
	@GetMapping(value = "/queryLaneAvgSpeed")
    public Result<?> queryLaneAvgSpeed(@RequestParam("equId") String equId,
    		@RequestParam("dateTime") String dateTime,
    		@RequestParam("tjDateType") String tjDateType,
    		@RequestParam("longTime") Integer longTime){
		
		List<Map<String,Object>> list = zcLdEventRadarInfoService.queryLaneAvgSpeed(equId,dateTime,tjDateType);
		List<ZcLdLaneInfo> laneList = zcLdLaneInfoService.queryByMainId(equId);
		List<String> timeList = getTimeList(tjDateType, dateTime, longTime);
		
		JSONObject jsonObject = getLineChartDataByLane(list, laneList, timeList, "speed_avg");
    	return Result.ok(jsonObject);
    }
	
	
	
	@AutoLog(value = "统计（各车道）大小车占比度")
	@GetMapping(value = "/queryLaneRoadCarType")
    public Result<?> queryLaneRoadCarType(@RequestParam("equId") String equId,
    		@RequestParam("dateTime") String dateTime,
    		@RequestParam("tjDateType") String tjDateType,
    		@RequestParam("longTime") Integer longTime){
		
		List<Map<String,Object>> list = zcLdRadarFrameLaneService.queryLaneRoadCarType(equId,dateTime,tjDateType);
		List<ZcLdLaneInfo> laneList = zcLdLaneInfoService.queryByMainId(equId);
		List<String> timeList = getTimeList(tjDateType, dateTime, longTime);
		
		JSONObject jsonObject = getLineChartDataByLane(list, laneList, timeList, "big_avg");
    	return Result.ok(jsonObject);
    }
	
	  
	@AutoLog(value = "（单元路段）速度离散度")
	@GetMapping(value = "/queryUnitSpeedStddev")
    public Result<?> queryUnitSpeedStddev(@RequestParam("equId") String equId,
    		@RequestParam("dateTime") String dateTime,
    		@RequestParam("tjDateType") String tjDateType,
    		@RequestParam("longTime") Integer longTime){
		
		List<Map<String,Object>> list = zcLdEventRadarInfoService.queryUnitSpeedStddev(equId,dateTime,tjDateType);
		List<String> laneList = getLaneDirection();
		List<String> timeList = getTimeList(tjDateType, dateTime, longTime);
		JSONObject jsonObject = getLineChartDataByUnit(list, laneList, timeList, "mini_avg_v");
		
    	return Result.ok(jsonObject);
    }
	
	
	@AutoLog(value = "（各车道）速度离散度")
	@GetMapping(value = "/queryLaneSpeedStddev")
    public Result<?> queryLaneSpeedStddev(@RequestParam("equId") String equId,
    		@RequestParam("dateTime") String dateTime,
    		@RequestParam("tjDateType") String tjDateType,
    		@RequestParam("longTime") Integer longTime){
		
		List<Map<String,Object>> list = zcLdEventRadarInfoService.queryLaneSpeedStddev(equId,dateTime,tjDateType);
		List<ZcLdLaneInfo> laneList = zcLdLaneInfoService.queryByMainId(equId);
		List<String> timeList = getTimeList(tjDateType, dateTime, longTime);
		
		JSONObject jsonObject = getLineChartDataByLane(list, laneList, timeList, "mini_avg_v");
    	return Result.ok(jsonObject);
    }
	
	
	@AutoLog(value = "统计（各车道）车道占有率和交通流密度")
	@GetMapping(value = "/queryLaneRatioAndFlowDensity")
    public Result<?> queryLaneRatioAndFlowDensity(@RequestParam("equId") String equId,
    		@RequestParam("dateTime") String dateTime,
    		@RequestParam("tjDateType") String tjDateType,
    		@RequestParam("longTime") Integer longTime){
		JSONObject jsonObject = new JSONObject();
		
		List<Map<String,Object>> list = zcLdRadarFrameLaneService.queryLaneRatioAndFlowDensity(equId,dateTime,tjDateType);
		
		List<ZcLdLaneInfo> laneList = zcLdLaneInfoService.queryByMainId(equId);
		List<String> legendList = new ArrayList<>();
		
		List<String> timeList = getTimeList(tjDateType, dateTime, longTime);
		jsonObject.put("xAxis", timeList);
		
		//车道占用率
		JSONArray seriesRatioArray = new JSONArray();
    	JSONObject seriesRatioJson = new JSONObject();
    	//车流密度
    	JSONArray seriesFlowArray = new JSONArray();
    	JSONObject seriesFlowJson = new JSONObject();
    	boolean addZero = true;
		for(ZcLdLaneInfo lane : laneList) {
			if(StringUtils.equals(lane.getLaneRoad(), "0")) {
				continue;
			}
			legendList.add(lane.getLaneRoadName());
			seriesRatioJson = new JSONObject();
			seriesRatioJson.put("name", lane.getLaneRoadName());
			seriesRatioJson.put("type", "line");
//			seriesJson.put("stack", "总量");
			List<Double> seriesRatioData = new ArrayList<>();
			List<Double> seriesFlowData = new ArrayList<>();
			for (String time : timeList) {
				addZero = true;
				for (Map<String, Object> map : list) {
					if(lane.getLaneRadar() == Integer.parseInt(map.get("lane_num").toString()) && 
							StringUtils.equals(time, map.get("tj_date").toString())) {
						addZero = false;
						seriesRatioData.add( (double)Math.round(Double.parseDouble(map.get("lane_ratio").toString())*10)/10);
						seriesFlowData.add( (double)Math.round(Double.parseDouble(map.get("car_flow_density").toString())*10)/10);
					}
				}
				if(addZero) {
					seriesRatioData.add(0.0);
					seriesFlowData.add(0.0);
				}
			}
			seriesRatioJson.put("data", seriesRatioData);
			seriesFlowJson.put("data", seriesFlowData);
			seriesRatioArray.add(seriesRatioJson);
			seriesFlowArray.add(seriesFlowJson);
		}
		jsonObject.put("legend", legendList);
		jsonObject.put("seriesRatio", seriesRatioArray);
		jsonObject.put("seriesFlow", seriesFlowArray);
		
    	return Result.ok(jsonObject);
    }
	
	
	
	@AutoLog(value = "（各车道）车道占有率")
	@GetMapping(value = "/queryLaneRatio")
    public Result<?> queryLaneRatio(@RequestParam("equId") String equId,
    		@RequestParam("dateTime") String dateTime,
    		@RequestParam("tjDateType") String tjDateType,
    		@RequestParam("longTime") Integer longTime){
		
		List<Map<String,Object>> list = zcLdRadarFrameLaneService.queryLaneRatioAndFlowDensity(equId,dateTime,tjDateType);
		List<ZcLdLaneInfo> laneList = zcLdLaneInfoService.queryByMainId(equId);
		List<String> timeList = getTimeList(tjDateType, dateTime, longTime);
		
		JSONObject jsonObject = getLineChartDataByLane(list, laneList, timeList, "lane_ratio");
    	return Result.ok(jsonObject);
    }
	
	
	@AutoLog(value = "（各车道）交通流密度")
	@GetMapping(value = "/queryLaneFlowDensity")
    public Result<?> queryLaneFlowDensity(@RequestParam("equId") String equId,
    		@RequestParam("dateTime") String dateTime,
    		@RequestParam("tjDateType") String tjDateType,
    		@RequestParam("longTime") Integer longTime){
		
		List<Map<String,Object>> list = zcLdRadarFrameLaneService.queryLaneRatioAndFlowDensity(equId,dateTime,tjDateType);
		List<ZcLdLaneInfo> laneList = zcLdLaneInfoService.queryByMainId(equId);
		List<String> timeList = getTimeList(tjDateType, dateTime, longTime);
		
		JSONObject jsonObject = getLineChartDataByLane(list, laneList, timeList, "car_flow_density");
    	return Result.ok(jsonObject);
    }
	
	
	@AutoLog(value = "统计（单元路段）平均时距")
	@GetMapping(value = "/queryUnitAvgTimeDistance")
    public Result<?> queryUnitAvgTimeDistance(@RequestParam("equId") String equId,
    		@RequestParam("dateTime") String dateTime,
    		@RequestParam("tjDateType") String tjDateType,
    		@RequestParam("longTime") Integer longTime){
		
		List<Map<String,Object>> list = zcLdRadarFrameService.queryUnitAvgTimeDistance(equId,dateTime,tjDateType);
		List<String> laneList = getLaneDirection();
		List<String> timeList = getTimeList(tjDateType, dateTime, longTime);
		
		JSONObject jsonObject = getLineChartDataByUnit(list, laneList, timeList, "avg_time_distance");
    	return Result.ok(jsonObject);
    }
	
	
	@AutoLog(value = "统计（各车道）平均时距")
	@GetMapping(value = "/queryLaneAvgTimeDistance")
    public Result<?> queryLaneAvgTimeDistance(@RequestParam("equId") String equId,
    		@RequestParam("dateTime") String dateTime,
    		@RequestParam("tjDateType") String tjDateType,
    		@RequestParam("longTime") Integer longTime){

		List<Map<String,Object>> list = zcLdRadarFrameLaneService.queryLaneAvgTimeDistance(equId,dateTime,tjDateType);
		List<ZcLdLaneInfo> laneList = zcLdLaneInfoService.queryByMainId(equId);
		List<String> timeList = getTimeList(tjDateType, dateTime, longTime);

		JSONObject jsonObject = getLineChartDataByLane(list, laneList, timeList, "avg_time_distance");

    	return Result.ok(jsonObject);
    }
	
	
	@AutoLog(value = "根据最新数据时间判断雷达是否掉线")
	@GetMapping(value = "/queryRadarNewDate")
    public Result<?> queryRadarNewDate(){
		List<Map<String,Object>> returnList = new ArrayList<>();
		Map<String,Object> returnMap = new HashMap<>();
		
		List<Map<String,Object>> list = zcLdEventRadarInfoService.queryRadarNewDate();
		for (Map<String, Object> map : list) {
			returnMap = new HashMap<>();
			returnMap.put("equ_id", map.get("id"));
			returnMap.put("equ_name", map.get("equ_name"));
			returnMap.put("tj_date", map.get("tj_date"));
			
			returnList.add(returnMap);
		}
		return Result.ok(returnList);
	}
	
	
	
	@AutoLog(value = "获取雷达设备")
	@GetMapping(value = "/queryEquipment")
    public Result<?> queryEquipment(){
		List<ZcLdEquipment> resultList = new ArrayList<>();
		List<ZcLdEquipment> list = zcLdEquipmentService.queryEquipment();
		for (ZcLdEquipment zcLdEquipment : list) {
			if(StringUtils.equals(zcLdEquipment.getEquType(), "001")) {
				resultList.add(zcLdEquipment);
			}
		}
		return Result.ok(resultList);
	}
	
	
	
	@AutoLog(value = "统计车流量和平均时速")
	@GetMapping(value = "/queryFlowAndAvgSpeed")
    public Result<?> queryFlowAndAvgSpeed(@RequestParam("equId") String equId,
    		@RequestParam("dateTime") String dateTime,
    		@RequestParam("tjDateType") String tjDateType,
    		@RequestParam("longTime") Integer longTime){
		
		List<Map<String,Object>> list = zcLdEventRadarInfoService.queryFlowAndAvgSpeed(equId,dateTime,tjDateType);
		List<String> timeList = getTimeList(tjDateType, dateTime, longTime);
		List<String> legendList = new ArrayList<>();
		legendList.add("车流量");
		legendList.add("平均车速");
		
		JSONObject jsonObject = new JSONObject();
		
		JSONArray seriesArray = new JSONArray();
    	JSONObject seriesJson = new JSONObject();
    	
    	JSONArray yAxisArray = new JSONArray();
    	JSONObject yAxisJson = new JSONObject();
    	int maxCount = 0;
    	int maxSpeed = 0;

    	boolean addZero = true;
		for(String legend : legendList) {
			yAxisJson = new JSONObject();
			yAxisJson.put("type", "value");
			yAxisJson.put("name", legend);
			yAxisJson.put("min", 0);
			
			
			seriesJson = new JSONObject();
			seriesJson.put("name", legend);
			seriesJson.put("type", "line");
			seriesJson.put("smooth", "true");
//			seriesJson.put("stack", "总量");
			List<Double> seriesData = new ArrayList<>();
			for (String time : timeList) {
				addZero = true;
				for (Map<String, Object> map : list) {
					if(StringUtils.equals(time, map.get("tj_date").toString()) &&
							StringUtils.equals(legend, "车流量")) {
						addZero = false;
						if(map.get("data_count") != null) {
							seriesData.add( (double)Math.round(Double.parseDouble(map.get("data_count").toString())*100)/100);
							if(maxCount < Integer.parseInt(map.get("data_count").toString())) {
								maxCount = Integer.parseInt(map.get("data_count").toString()) ;
							}
						}else {
							seriesData.add(0.0);
						}
					}
					if(StringUtils.equals(time, map.get("tj_date").toString()) &&
							StringUtils.equals(legend, "平均车速")) {
						addZero = false;
						if(map.get("speed_avg") != null) {
							seriesData.add( (double)Math.round(Double.parseDouble(map.get("speed_avg").toString())*100)/100);
							if(maxSpeed < Math.ceil(Double.valueOf(map.get("speed_avg").toString()))) {
								maxSpeed = (int)Math.ceil(Double.valueOf(map.get("speed_avg").toString()));
							}
						}else {
							seriesData.add(0.0);
						}
					}
				}
				if(addZero) {
					seriesData.add(0.0);
				}
			}
			
			
			if(StringUtils.equals(legend, "车流量")) {
				yAxisJson.put("max", maxCount);
			}else if(StringUtils.equals(legend, "平均车速")) {
				yAxisJson.put("max", maxSpeed);
				seriesJson.put("yAxisIndex", 1);
			}
			yAxisArray.add(yAxisJson);
			
			seriesJson.put("data", seriesData);
			seriesArray.add(seriesJson);
		}
		jsonObject.put("xAxis", timeList);
		jsonObject.put("yAxis", yAxisArray);
		jsonObject.put("legend", legendList);
		jsonObject.put("series", seriesArray);
    	return Result.ok(jsonObject);
    }

    @RequestMapping(value = "/visibleCurve")
	public @ResponseBody JSONObject visibleCurve(@RequestBody(required = false) JSONObject jsonObject){
		RestTemplate restTemplate = new RestTemplate();

		log.info("【能见度】开始构建能见度查询策略=============================");
		String IP = "https://ainjdjc.jchc.cn/public/getEquDistance";
		HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(type);
		Map<String,Object> map = new HashMap<>();
		map.put("startTime",jsonObject.get("startTime"));
		map.put("endTime",jsonObject.get("endTime"));
		map.put("epId",jsonObject.get("epId"));
		HttpEntity<Map<String,Object>> httpEntity = new HttpEntity<>(map, headers);
		log.info("【能见度】===查询策略------IP:"+IP+"---------------查询参数："+ JSON.toJSONString(httpEntity));

		JSONObject resultJSON = restTemplate.postForObject(IP, httpEntity,JSONObject.class);
		return resultJSON;
	}

	/**
	 * 开启实时播放
	 * @return
	 */
    @GetMapping(value = "/startLiveVideo")
	public Result<?> startLiveVideo(@RequestParam("radarId") String radarId){
    	try {
    		boolean bool = cameraComponent.startLiveVideo(radarId);
    		if(bool){
				return Result.ok("播放成功");
			} else {
    			return Result.error("播放失败");
			}
		} catch (Exception e) {
			return Result.error("");
		}
	}

	/**
	 * 取消实时播放
	 * @return
	 */
    @GetMapping(value = "/endLiveVideo")
	public Result<?> endLiveVideo(@RequestParam("radarId") String radarId){
		cameraComponent.endLiveVideo(radarId);
		try {
			cameraComponent.endLiveVideo(radarId);
    		return Result.ok("");
		} catch (Exception e) {
			return Result.error("");
		}
	}
    
}

