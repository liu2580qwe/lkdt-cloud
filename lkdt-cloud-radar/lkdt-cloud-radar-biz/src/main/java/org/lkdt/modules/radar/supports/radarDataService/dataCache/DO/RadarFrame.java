package org.lkdt.modules.radar.supports.radarDataService.dataCache.DO;

import org.lkdt.modules.radar.supports.radarDataService.dataCache.timeQueue.TimeCacheQueue;
import org.lkdt.modules.radar.supports.radarDataService.observers.DO.RadarObjDO;
import org.lkdt.modules.radar.supports.radarDataService.observers.RadarObserver;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 帧数据定义
 *
 * @program: zcloud-boot-parent
 * @create: 2021-04-14 22:19
 **/
public class RadarFrame implements Delayed {
    private RadarObserver timeCacheQueue;
    /**目标ID*/
    private int targetId;//目标ID
    /**x坐标*/
    private float sX;//x坐标
    /**y坐标*/
    private float sY;//y坐标
    /**x速度*/
    private float vX;//x速度
    /**y速度*/
    private float vY;//y速度
    /**x加速度*/
    private float aX;//x加速度
    /**y加速度*/
    private float aY;//y加速度
    /**车道号*/
    private int laneNum;//车道号
    //1 小型车，2 大型车，3 超大型车
    /**车辆类型*/
    private int carType;//车辆类型
    //0 无事件 1 车辆停止 2 车辆反方向行驶 （逆行） 3 车辆超速行驶 5 车辆实线变道
    //101 车辆停止消失 102 车辆反方向行 驶消失 103 车辆超速行驶 消失105 车辆实线变道 消失
    /**事件类型*/
    private int event;//事件类型
    /**车辆长度*/
    private int carLength;//车辆长度
    /**数据创建时间*/
    private long createTime;

    public RadarFrame(RadarObserver timeCacheQueue){
        this.timeCacheQueue = timeCacheQueue;
    }

    /*
     * 实现Comparable接口
     */
    @Override
    public int compareTo(Delayed o) {
        if(this.getDelay(TimeUnit.MILLISECONDS) < o.getDelay(TimeUnit.MILLISECONDS))
            return -1; //轮到该执行的时间已经过去了
        else if(this.getDelay(TimeUnit.MILLISECONDS) > o.getDelay(TimeUnit.MILLISECONDS))
            return 1; //还剩一会儿时间才执行
        else
            return 0;
    }

    /*
     * 还有多长时间往外输出
     */
    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(createTime + timeCacheQueue.cacheTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    public int getTargetId() {
        return targetId;
    }

    public RadarFrame setTargetId(int targetId) {
        this.targetId = targetId;
        return this;
    }

    public float getsX() {
        return sX;
    }

    public RadarFrame setsX(float sX) {
        this.sX = sX;
        return this;
    }

    public float getsY() {
        return sY;
    }

    public RadarFrame setsY(float sY) {
        this.sY = sY;
        return this;
    }

    public float getvX() {
        return vX;
    }

    public RadarFrame setvX(float vX) {
        this.vX = vX;
        return this;
    }

    public float getvY() {
        return vY;
    }

    public RadarFrame setvY(float vY) {
        this.vY = vY;
        return this;
    }

    public float getaX() {
        return aX;
    }

    public RadarFrame setaX(float aX) {
        this.aX = aX;
        return this;
    }

    public float getaY() {
        return aY;
    }

    public RadarFrame setaY(float aY) {
        this.aY = aY;
        return this;
    }

    public int getLaneNum() {
        return laneNum;
    }

    public RadarFrame setLaneNum(int laneNum) {
        this.laneNum = laneNum;
        return this;
    }

    public int getCarType() {
        return carType;
    }

    public RadarFrame setCarType(int carType) {
        this.carType = carType;
        return this;
    }

    public int getEvent() {
        return event;
    }

    public RadarFrame setEvent(int event) {
        this.event = event;
        return this;
    }

    public int getCarLength() {
        return carLength;
    }

    public RadarFrame setCarLength(int carLength) {
        this.carLength = carLength;
        return this;
    }

    public long getCreateTime() {
        return createTime;
    }

    public RadarFrame setCreateTime(long createTime) {
        this.createTime = createTime;
        return this;
    }


}
