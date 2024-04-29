package org.lkdt.modules.wind.domain;
import org.lkdt.common.util.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 
 * @author zhongkq
 * @email zhongkaiqiang@njzzhc.com
 * @date 2019-04-12 10:02:56
 */
public class AlarmDO implements Serializable {
	private static final long serialVersionUID = 1L;
	// 主键
	private String alarmId;
	// 摄像头id
	private String epId;
	// 路段告警ID
	private String roadAlarmId;
	// 是否有效，0：无效；1有效 9:未确认
	private String iseffective;
	// 确认人
	private String confirmor;
	// 确认时间
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date confirmtime;
	// 可见距离
	private int distance;
	// 风速
	private float winds;
	// 团雾/雾霾
	// 类型 11：团雾，12：雾霾
	// 21：车祸，22：停车，23：非法闯入，24：有障碍物
	private String fogType;
	// 等级
	private String level;
	// 开始时间
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date begintime;
	// 结束时间
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date endtime;
	// 摄像头地址描述
	private String address;
	// 经度
	private String lon;
	// 纬度
	private String lat;
	// 0:起雾；1散雾
	private int status;
	private String imgpath;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date imgtime;

	private float calx;

	private int type;
	private String startmonth;
	private int alarmsum;

	// 序号
	private int rank;
	/** 摄像头柱号 */
	private String equName;
	/** 摄像头位置 */
	private String equLocation;
	
	/**雾情持续时长（分钟）**/
	private String forMinute;
	
	/***路段名称**/
	private String hwName;

	/**统计值（用于报表）***/
	private int statisticsNum;
	/**统计时间（用于报表）**/
	private String statisticsDate;
	
	private String windName;
	
	
	
	
	
	public String getStatisticsDate() {
		return statisticsDate;
	}
	public void setStatisticsDate(String statisticsDate) {
		this.statisticsDate = statisticsDate;
	}
	public String getWindName() {
		return windName;
	}
	public void setWindName(String windName) {
		this.windName = windName;
	}
	public int getStatisticsNum() {
		return statisticsNum;
	}
	public void setStatisticsNum(int statisticsNum) {
		this.statisticsNum = statisticsNum;
	}
	public String getHwName() {
		return hwName;
	}
	public void setHwName(String hwName) {
		this.hwName = hwName;
	}
	/**持续时长（分钟）**/
	public String getForMinute() {
		return forMinute;
	}
	/**持续时长（分钟）**/
	public void setForMinute(String forMinute) {
		this.forMinute = forMinute;
	}

	/**
	 * 设置：主键
	 */
	public void setAlarmId(String alarmId) {
		this.alarmId = alarmId;
	}

	/**
	 * 获取：主键
	 */
	public String getAlarmId() {
		return alarmId;
	}

	/**
	 * 设置：摄像头id
	 */
	public void setEpId(String epId) {
		this.epId = epId;
	}

	public String getRoadAlarmId() {
		return roadAlarmId;
	}

	public void setRoadAlarmId(String roadAlarmId) {
		this.roadAlarmId = roadAlarmId;
	}

	/**
	 * 获取：摄像头id
	 */
	public String getEpId() {
		return epId;
	}

	/**
	 * 设置：是否有效，0：无效；1有效
	 */
	public void setIseffective(String iseffective) {
		this.iseffective = iseffective;
	}

	/**
	 * 获取：是否有效，0：无效；1有效
	 */
	public String getIseffective() {
		return iseffective;
	}

	/**
	 * 设置：确认人
	 */
	public void setConfirmor(String confirmor) {
		this.confirmor = confirmor;
	}

	/**
	 * 获取：确认人
	 */
	public String getConfirmor() {
		return confirmor;
	}

	/**
	 * 设置：确认时间
	 */
	public void setConfirmtime(Date confirmtime) {
		this.confirmtime = confirmtime;
	}

	/**
	 * 获取：确认时间
	 */
	public Date getConfirmtime() {
		return confirmtime;
	}

	/** 团雾/雾霾 */
	public String getFogType() {
		return fogType;
	}

	public String getFogTypeDesc() {
		if ("11".equals(fogType)) {
			return "团雾";
		} else if ("12".equals(fogType)) {
			return "雾霾";
		}
		return fogType;
	}

	/** 团雾/雾霾 */
	public void setFogType(String fogType) {
		this.fogType = fogType;
	}

	/**
	 * 设置：可见距离
	 */
	public void setDistance(int distance) {
		this.distance = distance;
	}

	public float getWinds() {
		return winds;
	}
	public void setWinds(float winds) {
		this.winds = winds;
	}
	/**
	 * 获取：可见距离
	 */
	public int getDistance() {
		return distance;
	}

	/**
	 * 设置：等级
	 */
	public void setLevel(String level) {
		this.level = level;
	}

	/**
	 * 获取：等级
	 */
	public String getLevel() {
		return level;
	}

	/**
	 * 设置：开始时间
	 */
	public void setBegintime(Date begintime) {
		this.begintime = begintime;
	}

	/**
	 * 获取：开始时间
	 */
	public Date getBegintime() {
		return begintime;
	}

	/**
	 * 设置：结束时间
	 */
	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}

	/**
	 * 获取：结束时间
	 */
	public Date getEndtime() {
		return endtime;
	}

	/**
	 * 设置：摄像头地址描述
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * 获取：摄像头地址描述
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * 设置：
	 */
	public void setLon(String lon) {
		this.lon = lon;
	}

	/**
	 * 获取：
	 */
	public String getLon() {
		return lon;
	}

	/**
	 * 设置：
	 */
	public void setLat(String lat) {
		this.lat = lat;
	}

	/**
	 * 获取：
	 */
	public String getLat() {
		return lat;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setImgpath(String imgpath) {
		this.imgpath = imgpath;
	}

	public String getImgpath() {
		return imgpath;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void setStartmonth(String startmonth) {
		this.startmonth = startmonth;
	}

	public String getStartmonth() {
		return startmonth;
	}

	public void setAlarmsum(int alarmsum) {
		this.alarmsum = alarmsum;
	}

	public int getAlarmsum() {
		return alarmsum;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getRank() {
		return rank;
	}

	public void setImgtime(Date imgtime) {
		this.imgtime = imgtime;
	}

	public Date getImgtime() {
		return imgtime;
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

	/**
	 * @return the calx
	 */
	public float getCalx() {
		return calx;
	}

	/**
	 * @param calx the calx to set
	 */
	public void setCalx(float calx) {
		this.calx = calx;
	}

	/**
	 * 获取雾描述
	 */
	public String getFogDesc() {
		if (this.distance <= 30) {
			return "特大浓雾";
		} else if (this.distance <= 50) {
			return "特浓雾";
		} else if (this.distance <= 100) {
			return "浓雾";
		} else if (this.distance <= 200) {
			return "大雾";
		}
		return "";
	}

	/**
	 * 获取告警等级汉字
	 */
	public String getLevelDesc() {
		if (this.distance <= 30) {
			return "特";
		} else if (this.distance <= 50) {
			return "一";
		} else if (this.distance <= 100) {
			return "二";
		} else if (this.distance <= 200) {
			return "三";
		} else if(this.distance > 200){
			return "解除";
		}
		return "";
	}
	
	/**
	 * 根据告警等级获取告警等级汉字
	 * @return
	 */
	public String getLevelDescByLevel() {
		if (StringUtils.equals(this.level, "1")) {
			return "三级告警";
		}
		if (StringUtils.equals(this.level, "2")) {
			return "二级告警";
		}
		if (StringUtils.equals(this.level, "3")) {
			return "一级告警";
		}
		if (StringUtils.equals(this.level, "4")) {
			return "特级告警";
		}
		return "";
	}

	/**
	 * 获取告警等级数字
	 */
	public String getLevelByDist() {
		if (this.distance <= 30) {
			return "4";
		} else if (this.distance <= 50) {
			return "3";
		} else if (this.distance <= 100) {
			return "2";
		} else if (this.distance <= 200) {
			return "1";
		} else if (this.distance > 200) {
			return "0";
		}
		return "";
	}

	/**
	 * 获取告警类型汉字
	 */
	public String getTypeDesc() {
		if (this.type == 1) {
			return "告警";
		}
		if (this.type == 2) {
			return "告警等级提升";
		}
		if (this.type == 3) {
			return "告警等级降低";
		}
		if (this.type == 4) {
			return "告警解除";
		}
		return "";
	}
	
	/**
	 * 获取管制描述
	 */
	public String getTypeGuanZhi(String guanzhiLevel) {
		if (this.type == 1) {
			return "实施" + guanzhiLevel + "级管制";
		}
		if (this.type == 2) {
			return "变更为" + guanzhiLevel + "级管制";
		}
		if (this.type == 3) {
			return "变更为" + guanzhiLevel + "级管制";
		}
		if (this.type == 4) {
			return "解除管制";
		}
		return "";
	}

}
