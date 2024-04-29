package org.lkdt.modules.radar.supports.radarDataService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.springframework.stereotype.Component;

import org.lkdt.modules.radar.entity.ZcLdEventRadarInfo;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

/**
 * 计算车辆线性拟合方程
 * @author wy
 *
 */
@Component
public class CarLinearFitting {

	/**
	 * 计算每辆车的线性方程
	 * @param zcLdEventRadarInfo
	 */
	public void linearFitting(ZcLdEventRadarInfo zcLdEventRadarInfo) {
		try{
			if(zcLdEventRadarInfo != null){
				JSONArray jsonArray = zcLdEventRadarInfo.getJsonArray();
				List<List<Double>> data = getCarData(jsonArray);
				//拟合方程
				double[] equation = polynomialCurve(data.get(0), data.get(1), 2);
				//System.out.println(Arrays.toString(equation));
				zcLdEventRadarInfo.setEquationParam(Arrays.toString(equation));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 
	 * @param jsonArray 雷达车辆轨迹数据
	 * @return
	 */
	private List<List<Double>> getCarData(JSONArray jsonArray){
		List<List<Double>> list = new ArrayList<>();
		// x为x轴坐标
		List<Double> x = new ArrayList<Double>();
		List<Double> y = new ArrayList<Double>();
		
		for(int i = 0 ; i < jsonArray.size() ; i++) {
			JSONObject jo = jsonArray.getJSONObject(i);
			y.add(Double.parseDouble(jo.getStr("sX")));
			x.add(Double.parseDouble(jo.getStr("sY")));
		}
		
		list.add(x);
		list.add(y);
		return list;
	}
	
	
	// 多项式拟合
	public double[] polynomialCurve(List<Double> x, List<Double> y, int degree) {

		WeightedObservedPoints points = new WeightedObservedPoints();

		for (int index = 0; index < x.size(); index++) {
			points.add(x.get(index), y.get(index));
		}

		PolynomialCurveFitter fitter = PolynomialCurveFitter.create(degree);
		double[] result = fitter.fit(points.toList());
		
		ArrayUtils.reverse(result);
		return result;
	}
	
//	/**
//	 * 拟合后的车道
//	 * @param jsonObject
//	 * @param radarId
//	 * @return
//	 */
//	public int getPolynomialLane(JSONObject jsonObject,String radarId) {
//		int ln = 0;
//		double sx = jsonObject.getDoubleValue("sX") ;
//		double sy = jsonObject.getDoubleValue("sY") ;
//		int laneNum = jsonObject.getIntValue("laneNum");
//
//		ZcLdLaneInfo lane = radarLaneInfoMap.get(radarId+"_"+laneNum) ;
//		String direction = lane.getLaneRoad().substring(0,1);
//		ZcLdLaneInfo lane2 = radarDirectionMap.get(radarId+"_"+direction) ;
//		if(lane2 != null) {
//			double[] equation = lane2.getEquation();
//			ln = (int)Math.ceil((sx - (sy * sy * equation[0] + sy * equation[1] + equation[2] )) / 4) ;
//			if(StringUtils.equals(direction, "R") && ln <= 0) {
//				ln = 1;
//			}
//		}
//		jsonObject.put("polynomialLane", ln);
//		return ln;
//	}
	
	
	
	
}
