package org.lkdt.modules.radar.supports.radarDataHandle.interfaces;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.lkdt.modules.radar.supports.radarDataService.DataReader;

/**
 * 雷达监听接口
 */
public interface IRadarProduceInterface {

    /**
     * 获取雷达实时队列
     * @param radarId
     * @return
     */
    JSONArray getRadarEventDataGrabLiveQueuePoll(String radarId);

    /**
     * 获取数据
     * @param radarId
     * @return
     */
    JSONObject getRadarDataReader(String radarId);

    /**
     * 获取雷达DataReader对象
     * @param radarId
     * @return
     */
    DataReader getDataReader(String radarId);

    /**
     * 初始化入口
     */
    void init();

}
