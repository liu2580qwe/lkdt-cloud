package org.lkdt.modules.radar.supports.radarDataService.dataCache.DO;

@Deprecated
public enum TimeInterval {

    _15MIN, _30MIN, _60MIN;

    public static String getKey(TimeInterval timeInterval){
        switch (timeInterval){
            case _15MIN:
                return "15";
            case _30MIN:
                return "30";
            case _60MIN:
                return "60";
            default:
                return null;
        }
    }
}
