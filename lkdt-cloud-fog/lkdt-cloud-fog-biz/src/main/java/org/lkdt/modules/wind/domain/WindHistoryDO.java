package org.lkdt.modules.wind.domain;
import com.alibaba.fastjson.JSONObject;
import java.io.Serializable;
import java.util.Date;
/**
 * 
 * 
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2019-04-03 15:07:30
 */
public class WindHistoryDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private float winds;
	private float windd;
	private Date time;
	private String windId;
	private JSONObject predictObject;

	public JSONObject getPredictObject() {
		return predictObject;
	}
	public void setPredictObject(JSONObject predictObject) {
		this.predictObject = predictObject;
	}

	public String getWindId() {
		return windId;
	}
	public void setWindId(String windId) {
		this.windId = windId;
	}
	public float getWinds() {
		return winds;
	}
	public void setWinds(float winds) {
		this.winds = winds;
	}
	public float getWindd() {
		return windd;
	}
	public void setWindd(float windd) {
		this.windd = windd;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "WindHistoryDO{" +
				"winds=" + winds +
				", windd=" + windd +
				", time=" + time +
				", windId='" + windId + '\'' +
				'}';
	}
}
