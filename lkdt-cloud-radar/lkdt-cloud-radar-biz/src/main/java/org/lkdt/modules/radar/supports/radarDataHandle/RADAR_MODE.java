package org.lkdt.modules.radar.supports.radarDataHandle;

public enum RADAR_MODE {

    CLIENT, SERVER;

    /**应用程序连接雷达方式， 缺省值：客户端模式*/
    public static RADAR_MODE RUN_MODE = RADAR_MODE.CLIENT;

    static {
        //RADAR_MODE.RUN_MODE = RADAR_MODE.SERVER;
    }
}
