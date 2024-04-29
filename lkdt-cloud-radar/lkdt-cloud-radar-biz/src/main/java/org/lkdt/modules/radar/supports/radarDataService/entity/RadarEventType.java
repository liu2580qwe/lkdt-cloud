package org.lkdt.modules.radar.supports.radarDataService.entity;

import java.io.Serializable;



public class RadarEventType  implements Serializable {
    private static final long serialVersionUID = 1L;

	//雷达ID
    private String radarId ;
    //雷达数据ID
    private Integer targetId;
    //事件类型
    private String eventType;
    //X坐标
    private double sx;
    //Y坐标
    private double sy;
    //通知雷摄联动抓拍次数
    private int noticeCount;
    //间隔多久再次通知
    private long longTime;
    
	public String getRadarId() {
		return radarId;
	}
	public void setRadarId(String radarId) {
		this.radarId = radarId;
	}
	public Integer getTargetId() {
		return targetId;
	}
	public void setTargetId(Integer targetId) {
		this.targetId = targetId;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public double getSx() {
		return sx;
	}
	public void setSx(double sx) {
		this.sx = sx;
	}
	public double getSy() {
		return sy;
	}
	public void setSy(double sy) {
		this.sy = sy;
	}
	public int getNoticeCount() {
		return noticeCount;
	}
	public void setNoticeCount(int noticeCount) {
		this.noticeCount = noticeCount;
	}
	public long getLongTime() {
		return longTime;
	}
	public void setLongTime(long longTime) {
		this.longTime = longTime;
	}
    
    
}
