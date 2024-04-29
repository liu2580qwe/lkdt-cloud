package org.lkdt.modules.radar.supports.radarDataService.dataCache.DO;

public class RadarLaneCalcDO {

    public RadarLaneCalcDO(){
        this.LaneNum = 0;
        this.vAvg = 0;
        this.timeDistance = 0;
    }

    public void init(){
        this.LaneNum = 0;
        this.vAvg = 0;
        this.timeDistance = 0;
    }

    /**车道号*/
    private int LaneNum;

    /**平均速度*/
    private double vAvg;

    /**速度标准差*/
    private double standardDivide;

    /**小车平均速度*/
    private double vAvgCar;

    /**大车平均速度*/
    private double vAvgTruck;

    /**平均时距*/
    private double timeDistance;

    /**车流总个数*/
    private double totalCount;

    /**客车个数*/
    private double carCount;

    /**中型车个数*/
    private double mediumTruckCount;

    /**大型车个数*/
    private double lagerTruckCount;

    /**车流密度 辆/km*/
    private double carFlowDensity;

    /**车辆速度方差*/
    private double speedDiscreteValue;

    //TODO: 车辆突然停止统计、超高速车辆统计、超低速车辆统计、车辆预测轨迹偏差

    public int getLaneNum() {
        return LaneNum;
    }

    public void setLaneNum(int laneNum) {
        LaneNum = laneNum;
    }

    public double getvAvg() {
        return vAvg;
    }

    public void setvAvg(double vAvg) {
        this.vAvg = vAvg;
    }

    public double getStandardDivide() {
        return standardDivide;
    }

    public void setStandardDivide(double standardDivide) {
        this.standardDivide = standardDivide;
    }

    public double getvAvgCar() {
        return vAvgCar;
    }

    public void setvAvgCar(double vAvgCar) {
        this.vAvgCar = vAvgCar;
    }

    public double getvAvgTruck() {
        return vAvgTruck;
    }

    public void setvAvgTruck(double vAvgTruck) {
        this.vAvgTruck = vAvgTruck;
    }

    public double getTimeDistance() {
        return timeDistance;
    }

    public void setTimeDistance(double timeDistance) {
        this.timeDistance = timeDistance;
    }

    public double getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(double totalCount) {
        this.totalCount = totalCount;
    }

    public double getCarCount() {
        return carCount;
    }

    public void setCarCount(double carCount) {
        this.carCount = carCount;
    }

    public double getMediumTruckCount() {
        return mediumTruckCount;
    }

    public void setMediumTruckCount(double mediumTruckCount) {
        this.mediumTruckCount = mediumTruckCount;
    }

    public double getLagerTruckCount() {
        return lagerTruckCount;
    }

    public void setLagerTruckCount(double lagerTruckCount) {
        this.lagerTruckCount = lagerTruckCount;
    }

    public double getCarFlowDensity() {
        return carFlowDensity;
    }

    public void setCarFlowDensity(double carFlowDensity) {
        this.carFlowDensity = carFlowDensity;
    }

    public double getSpeedDiscreteValue() {
        return speedDiscreteValue;
    }

    public void setSpeedDiscreteValue(double speedDiscreteValue) {
        this.speedDiscreteValue = speedDiscreteValue;
    }

}
