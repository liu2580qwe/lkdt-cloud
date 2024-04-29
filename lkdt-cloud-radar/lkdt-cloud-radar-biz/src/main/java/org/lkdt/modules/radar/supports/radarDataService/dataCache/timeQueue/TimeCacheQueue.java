package org.lkdt.modules.radar.supports.radarDataService.dataCache.timeQueue;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.lkdt.modules.radar.supports.radarDataService.DataReader;
import org.lkdt.modules.radar.supports.radarDataService.dataCache.DO.RadarFrame;
import org.lkdt.modules.radar.supports.radarDataService.observers.RadarObserver;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 数据缓存，十分钟数据
 * 按时间缓存
 * @program: zcloud-boot-parent
 * @create: 2021-04-14 21:57
 **/
public class TimeCacheQueue extends RadarObserver{

    public TimeCacheQueue(String radarId){
        this.radarId = radarId;
        super.cacheTime = this.cacheTime;
        super.maxLaneSize = this.maxLaneSize;
    }

    /**毫秒*/
    public final long cacheTime = 60*10*1000;

    /**识别出最大车辆数*/
    public final long maxLaneSize = 256;

    public String radarId;

    /**<车辆id,车辆数据缓存>*/
    public Map<Integer, LinkedBlockingDeque<RadarFrame>> frames = new HashMap<>();

    public void offer(RadarFrame radarFrame){
        int targetId = radarFrame.getTargetId();
        //实现数据缓存
        if(this.frames.get(targetId) == null){
            LinkedBlockingDeque<RadarFrame> linkedBlockingDeque = new LinkedBlockingDeque<>();
            this.frames.put(targetId, linkedBlockingDeque);
            linkedBlockingDeque.offer(radarFrame);
            //TODO 增加统计数据元
        } else {
            this.frames.get(targetId).offer(radarFrame);
            //TODO 增加统计数据元
        }
        //清除过期数据
        RadarFrame radarFrame1 = this.frames.get(targetId).poll();
        //TODO 减少统计数据元
        while(radarFrame1 != null){
            radarFrame1 = this.frames.get(radarFrame1.getTargetId()).poll();
            //TODO 减少统计数据元
        }
    }

    private void poll(){
        for(Map.Entry<Integer, LinkedBlockingDeque<RadarFrame>> m: this.frames.entrySet()){
            RadarFrame radarFrame = m.getValue().poll();
            while(radarFrame != null){
                radarFrame = m.getValue().poll();
            }
        }
    }

    @Override
    public void radarDataHandle(DataReader dataReader, JSONArray jsonArray) {
        for(int i = 0; i < jsonArray.size(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            //每辆车解析
            RadarFrame radarFrame = new RadarFrame(this);

            this.readData(radarFrame, jsonObject);
            this.offer(radarFrame);

        }
    }

    @Override
    public void destroyTimeCounter() {
        //空
    }
}
