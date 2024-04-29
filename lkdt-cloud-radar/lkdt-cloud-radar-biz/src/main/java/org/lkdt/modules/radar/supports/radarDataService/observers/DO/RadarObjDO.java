package org.lkdt.modules.radar.supports.radarDataService.observers.DO;

public class RadarObjDO implements Comparable<RadarObjDO>{
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
    /**计算拟合车道*/
    private int polynomialLane;//车道号

    public int getTargetId() {
        return targetId;
    }

    public RadarObjDO setTargetId(int targetId) {
        this.targetId = targetId;
        return this;
    }

    public float getsX() {
        return sX;
    }

    public RadarObjDO setsX(float sX) {
        this.sX = sX;
        return this;
    }

    public float getsY() {
        return sY;
    }

    public RadarObjDO setsY(float sY) {
        this.sY = sY;
        return this;
    }

    public float getvX() {
        return vX;
    }

    public RadarObjDO setvX(float vX) {
        this.vX = vX;
        return this;
    }

    public float getvY() {
        return vY;
    }

    public RadarObjDO setvY(float vY) {
        this.vY = vY;
        return this;
    }

    public float getaX() {
        return aX;
    }

    public RadarObjDO setaX(float aX) {
        this.aX = aX;
        return this;
    }

    public float getaY() {
        return aY;
    }

    public RadarObjDO setaY(float aY) {
        this.aY = aY;
        return this;
    }

    public int getPolynomialLane() {
        return polynomialLane;
    }

    public RadarObjDO setPolynomialLane(int polynomialLane) {
        this.polynomialLane = polynomialLane;
        return this;
    }

    public int getLaneNum() {
        return laneNum;
    }

    public RadarObjDO setLaneNum(int laneNum) {
        this.laneNum = laneNum;
        return this;
    }

    public int getCarType() {
        return carType;
    }

    public RadarObjDO setCarType(int carType) {
        this.carType = carType;
        return this;
    }

    public int getEvent() {
        return event;
    }

    public RadarObjDO setEvent(int event) {
        this.event = event;
        return this;
    }

    public int getCarLength() {
        return carLength;
    }

    public RadarObjDO setCarLength(int carLength) {
        this.carLength = carLength;
        return this;
    }

    @Override
    public int compareTo(RadarObjDO o) {
        if(this.sY < o.sY){
            return -1;
        } else if(this.sY == o.sY){
            return 0;
        }
        return 1;
    }

}
