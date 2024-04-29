package org.lkdt.modules.fog.entity;
import lombok.Data;

import java.util.Date;

/**
 * 
 * 
 * @author zhongkq
 * @email zhongkaiqiang@njzzhc.com
 * @date 2020-04-09 15:37:34
 */
@Data
public class WindModel {
	//
	private String id;
	//
	private String windCode;
	//
	private String windName;
	//所属区域
	private String windLocation;
	//纬度
	private String lat;
	//经度
	private String lon;
	//路段ID
	private String hwId;
	//
	private Date createTime;
	//
	private Date updateTime;
	//
	private String creator;
	//
	private String updater;
	//状态
	private String state;
	
	/***************告警缓存 start **************/
	/**告警距离*/
	private Float distance;
	/**告警开始时间*/
	private Date alarmStartTime;
	/**告警结束时间*/
	private Date alarmEndTime;
	/**告警图片*/
	private String alarmImgpath;
	/**告警ID*/
	protected String alarmId;
	/**等级*/
	private String alarmLevel;
	/**通知ID*/
	private String noticeId;
	
	/**相邻摄像头ID*/
	private String adjoinEpid;
	
	
	/**告警图片产生时间*/
	private Date alarmImgtime;


}
