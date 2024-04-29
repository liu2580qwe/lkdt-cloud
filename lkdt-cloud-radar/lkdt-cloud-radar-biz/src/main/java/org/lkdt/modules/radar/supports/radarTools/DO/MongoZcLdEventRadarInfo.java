package org.lkdt.modules.radar.supports.radarTools.DO;

import java.util.Date;

/**
 * @Description: mongo事件雷达数据轨迹
 * @Author: jeecg-boot
 * @Date:   2021-05-27
 */
public class MongoZcLdEventRadarInfo {
	/**主键*/
    private String id;
	/**创建日期*/
    private Date createTime;
	/**雷达设备ID*/
    private String equId;
    /**目标行驶轨迹*/
    private String pathTrace;

    public String getId() {
        return id;
    }

    public MongoZcLdEventRadarInfo setId(String id) {
        this.id = id;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public MongoZcLdEventRadarInfo setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getEquId() {
        return equId;
    }

    public MongoZcLdEventRadarInfo setEquId(String equId) {
        this.equId = equId;
        return this;
    }

    public String getPathTrace() {
        return pathTrace;
    }

    public MongoZcLdEventRadarInfo setPathTrace(String pathTrace) {
        this.pathTrace = pathTrace;
        return this;
    }
}
