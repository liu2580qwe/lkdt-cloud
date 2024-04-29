package org.lkdt.modules.weixin.entity;

import java.io.Serializable;
import java.util.Date;


/**
 * 三方告警信息
 * 
 * @author zhongkq
 * @email zhongkaiqiang@njzzhc.com
 * @date 2020-04-02 14:09:12
 */
public class AlertThreepart implements Serializable {
	private static final long serialVersionUID = 1L;
	//主键
	private String alertThreepartId;
	//路段ID
	private String hwId;
	//路段告警信息主键
	private String roadAlarmId;
	//告警等级
	private String alarmLevel;
	//当前能见度
	private Integer mindistanceNow;
	//历史最低能见度
	private Integer mindistanceHis;
	//图片时间
	private Date imgtime;
	//图片地址
	private String imgpath;
	//消息推送时间
	private Date alertTime;
	//摄像头ID
	private String epId;
	//记录表数据更新时间排他用
	private Date detailUpdateTime;
	//name
	private String name;
	//detail
	private String detail;
	//处理状态
	private String handleStatus;
	//操作人
	private String openid;
	//操作 1：发送微信消息；2：发送短信消息；3：发送微信+短信消息
	private String operation;
	/**路段名称**/
	private String hwName;
	/**类型
	 *
	 **/
	private Integer type;
	/**趋势**/
	private Integer trend;

	public Integer getTrend() {
		return trend;
	}

	public void setTrend(Integer trend) {
		this.trend = trend;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getHwName() {
		return hwName;
	}
	public void setHwName(String hwName) {
		this.hwName = hwName;
	}
	/**
	 * 设置：主键
	 */
	public void setAlertThreepartId(String alertThreepartId) {
		this.alertThreepartId = alertThreepartId;
	}
	/**
	 * 获取：主键
	 */
	public String getAlertThreepartId() {
		return alertThreepartId;
	}
	/**
	 * 设置：路段ID
	 */
	public void setHwId(String hwId) {
		this.hwId = hwId;
	}
	/**
	 * 获取：路段ID
	 */
	public String getHwId() {
		return hwId;
	}
	/**
	 * 设置：告警等级
	 */
	public void setAlarmLevel(String alarmLevel) {
		this.alarmLevel = alarmLevel;
	}
	/**
	 * 获取：告警等级
	 */
	public String getAlarmLevel() {
		return alarmLevel;
	}
	/**
	 * 设置：当前能见度
	 */
	public void setMindistanceNow(int mindistanceNow2) {
		this.mindistanceNow = mindistanceNow2;
	}
	/**
	 * 获取：当前能见度
	 */
	public int getMindistanceNow() {
		return mindistanceNow;
	}
	/**
	 * 设置：历史最低能见度
	 */
	public void setMindistanceHis(int mindistanceHis) {
		this.mindistanceHis = mindistanceHis;
	}
	/**
	 * 获取：历史最低能见度
	 */
	public int getMindistanceHis() {
		return mindistanceHis;
	}
	/**
	 * 设置：图片时间
	 */
	public void setImgtime(Date imgtime) {
		this.imgtime = imgtime;
	}
	/**
	 * 获取：图片时间
	 */
	public Date getImgtime() {
		return imgtime;
	}
	/**
	 * 设置：图片地址
	 */
	public void setImgpath(String imgpath) {
		this.imgpath = imgpath;
	}
	/**
	 * 获取：图片地址
	 */
	public String getImgpath() {
		return imgpath;
	}
	/**
	 * 设置：消息推送时间
	 */
	public void setAlertTime(Date alertTime) {
		this.alertTime = alertTime;
	}
	/**
	 * 获取：消息推送时间
	 */
	public Date getAlertTime() {
		return alertTime;
	}
	/**
	 * 设置：摄像头ID
	 */
	public void setEpId(String epId) {
		this.epId = epId;
	}
	/**
	 * 获取：摄像头ID
	 */
	public String getEpId() {
		return epId;
	}
	/**
	 * 设置：记录表数据更新时间排他用
	 */
	public void setDetailUpdateTime(Date detailUpdateTime) {
		this.detailUpdateTime = detailUpdateTime;
	}
	/**
	 * 获取：记录表数据更新时间排他用
	 */
	public Date getDetailUpdateTime() {
		return detailUpdateTime;
	}

	/**
	 * 获取：
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置：
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取：
	 */
	public String getDetail() {
		return detail;
	}

	/**
	 * 设置：
	 */
	public void setDetail(String detail) {
		this.detail = detail;
	}

	/**
	 * 路段告警信息主键
	 * @return
	 */
	public String getRoadAlarmId() {
		return roadAlarmId;
	}

	/**
	 * 路段告警信息主键
	 * @param roadAlarmId
	 */
	public void setRoadAlarmId(String roadAlarmId) {
		this.roadAlarmId = roadAlarmId;
	}

	public String getHandleStatus() {
		return handleStatus;
	}

	public void setHandleStatus(String handleStatus) {
		this.handleStatus = handleStatus;
	}
	
	/**
	 * 设置：操作人
	 */
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	/**
	 * 获取：操作人
	 */
	public String getOpenid() {
		return openid;
	}
	/**
	 * 设置：操作 1：发送微信消息；2：发送短信消息；3：发送微信+短信消息
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}
	/**
	 * 获取：操作 1：发送微信消息；2：发送短信消息；3：发送微信+短信消息
	 */
	public String getOperation() {
		return operation;
	}
	
}
