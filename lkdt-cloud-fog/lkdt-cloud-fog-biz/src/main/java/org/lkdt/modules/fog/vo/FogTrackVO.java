package org.lkdt.modules.fog.vo;

import java.io.Serializable;
import java.util.Date;


/**
 * 
 * 
 * @author zhongkq
 * @email zhongkaiqiang@njzzhc.com
 * @date 2019-06-18 10:05:01
 */
public class FogTrackVO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//主键
	private String trackId;
	//轨迹开始位置：摄像头位置
	private String epIdStart;
	//轨迹终点位置：摄像头位置
	private String epIdEnd;
	//轨迹数据
	private String trackArray;
	//创建时间
	private Date createTime;
	//创建用户
	private String createUser;
	//更新时间
	private Date updateTime;
	//更新用户
	private String updateUser;
	//状态
	private String status;

	/**
	 * 设置：主键
	 */
	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}
	/**
	 * 获取：主键
	 */
	public String getTrackId() {
		return trackId;
	}
	/**
	 * 设置：轨迹开始位置：摄像头位置
	 */
	public void setEpIdStart(String epIdStart) {
		this.epIdStart = epIdStart;
	}
	/**
	 * 获取：轨迹开始位置：摄像头位置
	 */
	public String getEpIdStart() {
		return epIdStart;
	}
	/**
	 * 设置：轨迹终点位置：摄像头位置
	 */
	public void setEpIdEnd(String epIdEnd) {
		this.epIdEnd = epIdEnd;
	}
	/**
	 * 获取：轨迹终点位置：摄像头位置
	 */
	public String getEpIdEnd() {
		return epIdEnd;
	}
	/**
	 * 设置：轨迹数据
	 */
	public void setTrackArray(String trackArray) {
		this.trackArray = trackArray;
	}
	/**
	 * 获取：轨迹数据
	 */
	public String getTrackArray() {
		return trackArray;
	}
	/**
	 * 设置：创建时间
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	/**
	 * 获取：创建时间
	 */
	public Date getCreateTime() {
		return createTime;
	}
	/**
	 * 设置：创建用户
	 */
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	/**
	 * 获取：创建用户
	 */
	public String getCreateUser() {
		return createUser;
	}
	/**
	 * 设置：更新时间
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	/**
	 * 获取：更新时间
	 */
	public Date getUpdateTime() {
		return updateTime;
	}
	/**
	 * 设置：更新用户
	 */
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	/**
	 * 获取：更新用户
	 */
	public String getUpdateUser() {
		return updateUser;
	}
	/**
	 * 设置：状态
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * 获取：状态
	 */
	public String getStatus() {
		return status;
	}
}
