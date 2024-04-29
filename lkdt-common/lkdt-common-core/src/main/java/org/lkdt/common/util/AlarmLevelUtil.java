package org.lkdt.common.util;

import org.springframework.stereotype.Component;

/** 
* @author Huangjunyao 
* E-mail:897581567@qq.com 
* @version 创建时间：2020年5月8日 上午10:39:31 
* 类说明 
*/
@Component
public class AlarmLevelUtil {
	
	/**
	 * 获取大风告警等级数字
	 */
	public static String getLevelByWinds(Float winds) {
		if (winds >= 28.5) {
			return "4";
		} else if (winds >= 20.8) {
			return "3";
		} else if (winds >= 13.9) {
			return "2";
		} else if (winds >= 10.8) {
			return "1";
		} else {
			return "0";
		}
	}
	/**
	 * 获取告警类型描述
	 */
	public static String getAlarmType(int alarmType){
		/*-1：异常
		0：没雾
		1：有雾未确认
		2：有雾已确认
		3：升级未确认
		5：解除未确认*/
		 if(alarmType==1){
			 return "告警";
		 }
		 if(alarmType==2){
			 return "告警提升";
		 }
		 if(alarmType==3){
			 return "告警等级增加";
		 }
		 /* if(alarmType==3){
			 return "告警等级减少";
		 } */
		 if(alarmType==5){
			 return "告警解除";
		 }
		 if(alarmType==7){
			 return "100米提醒";
		 }
		 
		 return "";
		
	}
	
	/**
	 * 获取大风管制等级描述
	 */
	public static String getWindDescByDist(float winds) {
		if (winds >= 28.5) {
			return "特级管制";
			// 调用你的告警service.增加一条报警;反推websocket;
		} else if (winds >= 20.8) {
			return "一级管制";
		} else if (winds >= 13.9) {
			return "二级管制";
		} else if (winds >= 10.8) {
			return "三级管制";
		} else {
			return "";
		}
	}
	
	/**
	 * 获取管制等级描述
	 */
	public static String getLevelDesc(int alarmlevel) {
		if (alarmlevel == 1) {
			return "三级管制";
		}
		if (alarmlevel == 2) {
			return "二级管制";
		}
		if (alarmlevel == 3) {
			return "一级管制";
		}
		if (alarmlevel == 4) {
			return "特级管制";
		}
		return "";
	}

	/**
	 * 获取管制等级描述
	 */
	public static String getLevelDescByDist(int nowdistance) {
		if (nowdistance <= 30) {
			return "特级管制";
		} else if (nowdistance <= 50) {
			return "一级管制";
		} else if (nowdistance <= 100) {
			return "二级管制";
		} else if (nowdistance <= 200) {
			return "三级管制";
		} else {
			return "解除管制";
		}
	}
	
	/**
	 * 获取告警等级数字
	 */
	public static String getLevelByDist(int distance) {
		if (distance <= 30) {
			return "4";
		} else if (distance <= 50) {
			return "3";
		} else if (distance <= 100) {
			return "2";
		} else if (distance <= 200) {
			return "1";
		} else {
			return "0";
		}
	}
	/**
	 * 获取告警等级汉字
	 */
	public static String  getLevelDesc(Integer distance) {
		if (distance <= 30) {
			return "特";
		} else if (distance <= 50) {
			return "一";
		} else if (distance <= 100) {
			return "二";
		} else if (distance <= 200) {
			return "三";
		} else if(distance > 200){
			return "解除";
		}
		return "";
	}
	/**
	 * 获取告警等级数字
	 * 0：特级
	 * 1：一级告警
	 * 2：二级告警
	 * 3：三级告警
	 * -1: 无告警
	 */
	public static String getLevelByDistance(int distance) {
		if (distance <= 30) {
			return "4";
		} else if (distance <= 50) {
			return "3";
		} else if (distance <= 100) {
			return "2";
		} else if (distance <= 200) {
			return "1";
		} else {
			return "0";
		}
	}

	/**
	 * 获取告警等级数字
	 */
	public static int getLevelByDistInt(int distance) {
		if (distance <= 30) {
			return 4;
		} else if (distance <= 50) {
			return 3;
		} else if (distance <= 100) {
			return 2;
		} else if (distance <= 200) {
			return 1;
		} else {
			return 0;
		}
	}
	
	/**
	 * 获取雾描述
	 */
	public static String getFogDesc(int distance) {
		if (distance <= 30) {
			return "特强浓雾";
		} else if (distance <= 50) {
			return "特强浓雾";
		} else if (distance <= 100) {
			return "强浓雾";
		} else if (distance <= 200) {
			return "强浓雾";
		}
		return "";
	}
	
	/**
	 * 根据告警等级获取雾描述
	 * @param alarmlevel
	 * @return
	 */
	public static String getFogDescByAlarmlevel(int alarmlevel) {
		if (alarmlevel == 1) {
			return "大雾";
		}
		if (alarmlevel == 2) {
			return "浓雾";
		}
		if (alarmlevel == 3) {
			return "特浓雾";
		}
		if (alarmlevel == 4) {
			return "特大浓雾";
		}
		return "";
	}
	
	/**
	 * 根据告警等级数字获取告警等级中文描述
	 * @param alarmlevel
	 * @return
	 */
	public static String getAlarmLevelDesc(int alarmlevel) {
		if(alarmlevel == 0) {
			return "告警解除";
		}else if (alarmlevel == 1) {
			return "三级告警";
		}else if (alarmlevel == 2) {
			return "二级告警";
		}else if (alarmlevel == 3) {
			return "一级告警";
		}else if (alarmlevel == 4) {
			return "特级告警";
		}else {
			return "";
		}
	}

}
 