package org.lkdt.modules.wind.domain;
import java.io.Serializable;
import java.util.Date;
/**
 * 
 * 
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2019-04-03 15:07:30
 */
public class FogvalueDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer zhen;
	//
	private Integer fogval;
	//
	private Double jinduSecond;
	//
	private Date time;
	//
	private String cameraId;

	/**
	 * 设置：
	 */
	public void setZhen(Integer zhen) {
		this.zhen = zhen;
	}
	/**
	 * 获取：
	 */
	public Integer getZhen() {
		return zhen;
	}
	/**
	 * 设置：
	 */
	public void setFogval(Integer fogval) {
		this.fogval = fogval;
	}
	/**
	 * 获取：
	 */
	public Integer getFogval() {
		return fogval;
	}
	/**
	 * 设置：
	 */
	public void setJinduSecond(Double jinduSecond) {
		this.jinduSecond = jinduSecond;
	}
	/**
	 * 获取：
	 */
	public Double getJinduSecond() {
		return jinduSecond;
	}
	/**
	 * 设置：
	 */
	public void setTime(Date time) {
		this.time = time;
	}
	/**
	 * 获取：
	 */
	public Date getTime() {
		return time;
	}
	/**
	 * 设置：
	 */
	public void setCameraId(String cameraId) {
		this.cameraId = cameraId;
	}
	/**
	 * 获取：
	 */
	public String getCameraId() {
		return cameraId;
	}
}
