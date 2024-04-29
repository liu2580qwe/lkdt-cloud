package org.lkdt.modules.radar.supports.radarTools;

import org.lkdt.modules.radar.entity.ZcLdEventInfo;
import org.lkdt.modules.radar.entity.ZcLdEventRadarInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 雷达事件池
 */
public class RadarEventPool {
	
	public static Map<String, RadarEventPool> PUBLIC_POOL = new ConcurrentHashMap<>();

    /**常规池*/
    private Map<String, ZcLdEventRadarInfo> commonPool;

    /**丢失池*/
    private Map<String, ZcLdEventRadarInfo> zcLdEventRadarInfos;

    /**事件池*/
    private Map<String, ZcLdEventInfo> zcLdEventInfos;

    public RadarEventPool(){
        this.commonPool = new HashMap<>();
        this.zcLdEventRadarInfos = new HashMap<>();
        this.zcLdEventInfos = new HashMap<>();
    }

    public Map<String, ZcLdEventRadarInfo> getCommonPool() {
        return commonPool;
    }

    public void setCommonPool(Map<String, ZcLdEventRadarInfo> commonPool) {
        this.commonPool = commonPool;
    }

    public Map<String, ZcLdEventInfo> getZcLdEventInfos() {
        return zcLdEventInfos;
    }

    public void setZcLdEventInfos(Map<String, ZcLdEventInfo> zcLdEventInfos) {
        this.zcLdEventInfos = zcLdEventInfos;
    }

    public Map<String, ZcLdEventRadarInfo> getZcLdEventRadarInfos() {
        return zcLdEventRadarInfos;
    }

    public void setZcLdEventRadarInfos(Map<String, ZcLdEventRadarInfo> zcLdEventRadarInfos) {
        this.zcLdEventRadarInfos = zcLdEventRadarInfos;
    }
}
