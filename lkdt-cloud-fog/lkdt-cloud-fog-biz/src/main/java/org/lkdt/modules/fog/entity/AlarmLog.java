package org.lkdt.modules.fog.entity;

/**
 * 告警日志
 *
 * @program: fog
 * @create: 2020-09-29 16:10
 **/
public class AlarmLog {
    /**主键*/
    private String id;
    /**日期时间*/
    private String dateTime;
    /**设备id*/
    private String epId;
    /**设备名称*/
    private String epName;
    /**最初值*/
    private String originVal;
    /**未知参数*/
    private String culumn1;
    /**修正值*/
    private String modifyVal;
    /**系数*/
    private String paramNum;
    /**告警id*/
    private String alarmId;
    /**图片名称*/
    private String imgName;
    /**系统名称*/
    private String systemName;
    /**创建时间*/
    private String createtime;

    public String getId() {
        return id;
    }

    public AlarmLog setId(String id) {
        this.id = id;
        return this;
    }

    public String getDateTime() {
        return dateTime;
    }

    public AlarmLog setDateTime(String dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public String getEpId() {
        return epId;
    }

    public AlarmLog setEpId(String epId) {
        this.epId = epId;
        return this;
    }

    public String getEpName() {
        return epName;
    }

    public AlarmLog setEpName(String epName) {
        this.epName = epName;
        return this;
    }

    public String getOriginVal() {
        return originVal;
    }

    public AlarmLog setOriginVal(String originVal) {
        this.originVal = originVal;
        return this;
    }

    public String getModifyVal() {
        return modifyVal;
    }

    public AlarmLog setModifyVal(String modifyVal) {
        this.modifyVal = modifyVal;
        return this;
    }

    public String getParamNum() {
        return paramNum;
    }

    public AlarmLog setParamNum(String paramNum) {
        this.paramNum = paramNum;
        return this;
    }

    public String getImgName() {
        return imgName;
    }

    public AlarmLog setImgName(String imgName) {
        this.imgName = imgName;
        return this;
    }

    public String getCulumn1() {
        return culumn1;
    }

    public AlarmLog setCulumn1(String culumn1) {
        this.culumn1 = culumn1;
        return this;
    }

    public String getAlarmId() {
        return alarmId;
    }

    public AlarmLog setAlarmId(String alarmId) {
        this.alarmId = alarmId;
        return this;
    }

    public String getSystemName() {
        return systemName;
    }

    public AlarmLog setSystemName(String systemName) {
        this.systemName = systemName;
        return this;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }
}
