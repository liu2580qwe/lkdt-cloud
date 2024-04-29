package org.lkdt.modules.radar.supports.radarDataService.dataCache.DO;

public class RadarCalcDO {

    public RadarCalcDO(){
        this.vAvg = 0;
        this.timeDistance = 0;
    }

    public void init(){
        this.vAvg = 0;
        this.timeDistance = 0;
    }

    /**平均速度*/
    private float vAvg;

    /**平均时距*/
    private float timeDistance;

    public float getvAvg() {
        return vAvg;
    }

    public void setvAvg(float vAvg) {
        this.vAvg = vAvg;
    }

    public float getTimeDistance() {
        return timeDistance;
    }

    public void setTimeDistance(float timeDistance) {
        this.timeDistance = timeDistance;
    }

}
