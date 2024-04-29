package org.lkdt.modules.fog.calculator;
import com.alibaba.fastjson.JSONArray;
import org.lkdt.modules.fog.entity.AlarmModel;
import org.lkdt.modules.fog.entity.WindHistoryModel;
import org.lkdt.modules.fog.entity.WindModel;

import java.util.Date;

/**
 * the calculator of group fog
 * @author do not know
 * @version 2019-06-06
 */
public class WindCalculator extends WindModel {
	private Float winds;//风速
	private Date time;
	private Float windd;//计算参数,风向
	private String alarmRoadId;//路段告警id

	public String getAlarmRoadId() {
		return alarmRoadId;
	}

	public void setAlarmRoadId(String alarmRoadId) {
		this.alarmRoadId = alarmRoadId;
	}

	// data in the last half hour
	/*private DLink cacheVals = new DLink();*/
	private JSONArray prediction;


	/**
	 * start alert
	 * @param alarm
	 */
	public void startAlarm(AlarmModel alarm) {
		this.alarmId = (alarm.getId());
		this.setDistance(alarm.getDistance());
		this.setAlarmImgpath(alarm.getImgpath());
		this.setAlarmLevel(alarm.getLevel());
		this.setAlarmStartTime(alarm.getBegintime());
		this.setAlarmImgtime(alarm.getImgtime());
	}

	/**
	 * set visibility
	 * @param distance
	 */
	/*public void setAlarmValue(Float distance) {
		cacheVals.getTail().getData().setWindd(distance);
	}*/

	/**
	 * Get the latest cached data
	 * @return
	 */
	/*public WindCalculatorResult getLastFogvalue() {
		if (cacheVals.getTail() == null) {return null;}
		return cacheVals.getTail().getData();
	}*/
	
	/**
	 * Data processing
	 * @param fdo Haze data
	 * @return Calculation results
	 */
	public void doCalculation(WindHistoryModel fdo) {
		Date time = fdo.getTime();
		// Determine whether it is already in the cache
		if (time.equals(this.getTime())) {
			return;
		}
		this.setTime(fdo.getTime());
		this.setWindd(fdo.getWindd());
		this.setWinds(fdo.getWinds());
	}

	/*****************************************计算器/摄像头属性,排序：end************************************************/
	public WindCalculator setWindCalculator(WindModel wind, Date date) {
		//摄像头id(AI专用)
		this.setId(wind.getId());
		//设备名称
		this.setWindName(wind.getWindName());
		//设备位置
		this.setWindLocation(wind.getWindLocation());
		//关联公路
		this.setHwId(wind.getHwId());
		//经度
		this.setLon(wind.getLon());
		//纬度
		this.setLat(wind.getLat());
		//临近摄像头ID
		this.setAdjoinEpid(wind.getAdjoinEpid());

		/***************告警缓存 start **************/
		AlarmModel alarm = new AlarmModel();
		alarm.setId(wind.getAlarmId());
		alarm.setDistance(wind.getDistance());
		alarm.setImgpath(wind.getAlarmImgpath());
		alarm.setLevel(wind.getAlarmLevel());
		alarm.setBegintime(wind.getAlarmStartTime());
		alarm.setImgtime(wind.getAlarmImgtime());
		this.startAlarm(alarm);
		/***************告警缓存 end **************/
		return this;
	}

	/**
	 * retrieve data
	 * @return
	 */
	public JSONArray getPrediction() {
		return prediction;
	}

	/**
	 * Set data
	 * @param prediction
	 */
	public void setPrediction(JSONArray prediction) {
		this.prediction = prediction;
	}
	public Float getWinds() {
		return winds;
	}

	public void setWinds(Float winds) {
		this.winds = winds;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Float getWindd() {
		return windd;
	}

	public void setWindd(Float windd) {
		this.windd = windd;
	}
}
