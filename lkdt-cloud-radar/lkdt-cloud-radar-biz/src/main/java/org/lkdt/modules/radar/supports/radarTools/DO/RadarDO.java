package org.lkdt.modules.radar.supports.radarTools.DO;

import cn.hutool.json.JSONArray;

import java.util.Date;

public class RadarDO {

    /**接收到数据时间*/
    private Date date;

    private long nanoSecond;

    /**接收到数据*/
    private JSONArray dataBody;

    /**年月日时分秒*/
    private String ymdhms;

    /**格林尼治时间*/
    private long timestamp;

    /**雷达ID*/
    private String radarId;

    /**雷达设备ID*/
    private String radarKeyId;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getNanoSecond() {
        return nanoSecond;
    }

    public void setNanoSecond(long nanoSecond) {
        this.nanoSecond = nanoSecond;
    }

    public JSONArray getDataBody() {
        return dataBody;
    }

    public void setDataBody(JSONArray dataBody) {
        this.dataBody = dataBody;
    }

    public String getYmdhms() {
        return ymdhms;
    }

    public void setYmdhms(String ymdhms) {
        this.ymdhms = ymdhms;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getRadarId() {
        return radarId;
    }

    public void setRadarId(String radarId) {
        this.radarId = radarId;
    }

    public String getRadarKeyId() {
        return radarKeyId;
    }

    public void setRadarKeyId(String radarKeyId) {
        this.radarKeyId = radarKeyId;
    }

}
