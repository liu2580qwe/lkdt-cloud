package org.lkdt.modules.wind.domain;
import java.io.Serializable;
import java.util.Date;
/**
 * 
 * 
 * @author zhongkq
 * @email zhongkaiqiang@njzzhc.com
 * @date 2019-07-10 15:36:19
 */
public class EquipExceptionDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//主键
	private String equipExceptionId;
	//设备ID
	private String epId;
	//开始日期
	private String startDate;
	//开始时间
	private Date startTime;
	//最新时间
	private Date lastTime;
	//异常次数
	private Integer dayCount;
	//设备名称
	private String equName;
	//设备位置
	private String equLocation;

	/**
	 * 设置：主键
	 */
	public void setEquipExceptionId(String equipExceptionId) {
		this.equipExceptionId = equipExceptionId;
	}
	/**
	 * 获取：主键
	 */
	public String getEquipExceptionId() {
		return equipExceptionId;
	}
	/**
	 * 设置：设备ID
	 */
	public void setEpId(String epId) {
		this.epId = epId;
	}
	/**
	 * 获取：设备ID
	 */
	public String getEpId() {
		return epId;
	}
	/**
	 * 设置：开始日期
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	/**
	 * 获取：开始日期
	 */
	public String getStartDate() {
		return startDate;
	}
	/**
	 * 设置：开始时间
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	/**
	 * 获取：开始时间
	 */
	public Date getStartTime() {
		return startTime;
	}
	/**
	 * 设置：最新时间
	 */
	public void setLastTime(Date lastTime) {
		this.lastTime = lastTime;
	}
	/**
	 * 获取：最新时间
	 */
	public Date getLastTime() {
		return lastTime;
	}
	/**
	 * 设置：异常次数
	 */
	public void setDayCount(Integer dayCount) {
		this.dayCount = dayCount;
	}
	/**
	 * 获取：异常次数
	 */
	public Integer getDayCount() {
		return dayCount;
	}

	public String getEquName() {
		return equName;
	}

	public void setEquName(String equName) {
		this.equName = equName;
	}

	public String getEquLocation() {
		return equLocation;
	}

	public void setEquLocation(String equLocation) {
		this.equLocation = equLocation;
	}
}
