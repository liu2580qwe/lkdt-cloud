package org.lkdt.modules.fog.calculator;

import cn.hutool.core.bean.BeanUtil;
import org.lkdt.modules.fog.entity.AlarmRoadModel;
import org.lkdt.modules.fog.entity.EquipmentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Fog计算器
 *
 * @version 2019-06-06
 */
@Component
public class FogCalculator {
    protected static final Logger logger = LoggerFactory.getLogger(FogCalculator.class);

    public FogCalculator(){
        this.equipment = new EquipmentModel();
    }

    /**计算器没有属性,全在父类***/

    private String epId;

    private String sysName;

    private Integer confirmDistance;

    private Date confirmDate;

    /**
     * 数据模式，沙盘ST,ZC
     **/
    private String fmodel;

    /**
     * 沙盘模式图片全路径
     **/
    private String imgurl;

    private Integer distance;

    private Integer smDistance;

    private String imgpath;

    private Date imgtime;

    /**路段信息**/
//	private AlarmRoadDO alarmRoad ;

    /***************告警缓存 start **************/
    /**
     * 告警距离
     */
    private int alarmDistance;
    /**
     * 告警开始时间
     */
    private Date alarmStartTime;
    /**
     * 告警结束时间
     */
    private Date alarmEndTime;
    /**
     * 告警图片
     */
    private String alarmImgpath;
    /**
     * 告警ID
     */
    protected String alarmId;
    /**
     * 等级
     */
    private String alarmLevel;
    /**
     * 通知ID
     */
    private String noticeId;
    /**告警图片产生时间*/
    private Date alarmImgtime;


    /***************告警缓存 end **************/

    private int status;//0:起雾；1散雾; 9没有雾
    private int val; // 原始数据
    private Date updateTime;

    /**
     * 告警类型
     * -1：异常
     * 0：没雾
     * 1：有雾未确认
     * 2：有雾已确认
     * 3：升级未确认
     * 5：解除未确认
     */
    protected int cameraType;

    private String level;

    /**
     * 摄像头实体类
     */
    private EquipmentModel equipment;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    public String getFmodel() {
        return fmodel;
    }

    public void setFmodel(String fmodel) {
        this.fmodel = fmodel;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public Integer getDistance() {
        if (distance == null || distance == 0) {
            return null;
        }
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public Integer getSmDistance() {
        if(smDistance == null){
            return 0;
        }
        return smDistance;
    }

    public void setSmDistance(int smDistance) {
        this.smDistance = smDistance;
    }

    public String getImgpath() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public Date getImgtime() {
        return imgtime;
    }

    public void setImgtime(Date imgtime) {
        this.imgtime = imgtime;
    }

    public String getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }

    public void setConfirmDistance(Integer confirmDistance) {
        this.confirmDistance = confirmDistance;
    }

    public void setSmDistance(Integer smDistance) {
        this.smDistance = smDistance;
    }

    public int getAlarmDistance() {
        return alarmDistance;
    }

    public void setAlarmDistance(int alarmDistance) {
        this.alarmDistance = alarmDistance;
    }

    public Date getAlarmStartTime() {
        return alarmStartTime;
    }

    public void setAlarmStartTime(Date alarmStartTime) {
        this.alarmStartTime = alarmStartTime;
    }

    public Date getAlarmEndTime() {
        return alarmEndTime;
    }

    public void setAlarmEndTime(Date alarmEndTime) {
        this.alarmEndTime = alarmEndTime;
    }

    public String getAlarmImgpath() {
        return alarmImgpath;
    }

    public void setAlarmImgpath(String alarmImgpath) {
        this.alarmImgpath = alarmImgpath;
    }

    public String getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(String alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public Date getAlarmImgtime() {
        return alarmImgtime;
    }

    public void setAlarmImgtime(Date alarmImgtime) {
        this.alarmImgtime = alarmImgtime;
    }

    public boolean isFogNow() {
        return this.getCameraType() >= 2 && "0".equals(this.getEquipment().getState());
    }

    public boolean isConfirmed() {
        return (this.getCameraType() == 0 || this.getCameraType() == 2);
    }

    public boolean isAbn() {
        return ("2".equals(getEquipment().getState()) || "3".equals(getEquipment().getState())
                || "4".equals(getEquipment().getState()) || "5".equals(getEquipment().getState())
                || "6".equals(getEquipment().getState()) || "7".equals(getEquipment().getState()) || "8".equals(getEquipment().getState()));
    }

    public int getCameraType() {
        return cameraType;
    }

    public void setCameraType(int cameraType) {
        this.cameraType = cameraType;
    }

    @Override
    public String toString() {
        return "FogCalculator{" +
                "epId='" + getEpId() + '\'' +
                ", equName='" + this.getEquipment().getEquName() + '\'' +
                ", cameraDesc='" + this.getEquipment().getEquLocation() + '\'' +
                '}';
    }

    /**
     * 解析设备名称
     *
     * @param equName 设备名称
     * @return 数组
     */
    public static Integer[] parseEquName(String equName) {
        //数字标记
        String kiloFlag = "K".toUpperCase();
        //数字标记
        String meterFlag = "\\+".toUpperCase();
        //正则
        Pattern pattern = Pattern.compile("(" + kiloFlag + "\\d+|" + meterFlag + "\\d+)");
        //返回值
        Integer[] kiloMeter = new Integer[2];
        //下标
        int i = 0;
        try {
            //匹配
            Matcher arg = pattern.matcher(equName.toUpperCase());
            while (arg.find()) {
                switch (i) {
                    case 0:
                        kiloMeter[i] = Integer.valueOf(arg.group().substring(arg.group().indexOf(kiloFlag) + 1));
                        break;
                    case 1:
                        kiloMeter[i] = Integer.valueOf(arg.group().substring(arg.group().indexOf(meterFlag) + 1));
                        break;
                }
                if (kiloMeter[i] < 0) {
                    return null;
                }
                i++;
            }
        } catch (Exception e) {
            logger.error("【ERROR】设备名称解析错误,请检查设备名称是否符合规则||\n设备名称：" + equName);
            return null;
        }
        return kiloMeter;
    }

    /*****************************************计算器/摄像头属性,排序：end************************************************/


    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public Integer getConfirmDistance() {
        return confirmDistance;
    }

    public Date getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(Date confirmDate) {
        this.confirmDate = confirmDate;
    }

    public void setAlarmRoad(AlarmRoadModel alarmRoad) {
        Map<String, Object> roadMap = BeanUtil.beanToMap(alarmRoad);
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    /*****************摄像头实体类开始*********************/

    public EquipmentModel getEquipment() {
        return equipment;
    }

    public void setEquipment(EquipmentModel equipment) {
        this.equipment = equipment;
    }

    /**
     * 设置：
     */
    public void setEpId(String epId) {
        this.epId = epId;
        equipment.setId(epId);
    }

    /**
     * 获取：
     */
    public String getEpId() {
        return epId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    /*****************摄像头实体类结束*********************/

}
