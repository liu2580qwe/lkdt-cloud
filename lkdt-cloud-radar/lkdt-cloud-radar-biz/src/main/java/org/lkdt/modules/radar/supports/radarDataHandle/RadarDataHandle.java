package org.lkdt.modules.radar.supports.radarDataHandle;

import org.lkdt.modules.radar.mapper.ZcLdEventRadarInfoMapper;
import org.lkdt.modules.radar.supports.radarDataHandle.extend.AbstractCalculator;
import org.lkdt.modules.radar.supports.radarDataService.CommonDataHandle;
import org.lkdt.modules.radar.supports.radarDataService.DataReader;
import org.lkdt.modules.radar.supports.radarTools.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 客户端模式运行：
 * 数据瞬时处理入口
 */
@Component
public class RadarDataHandle extends AbstractCalculator {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Producer producer;

    @Autowired
    CommonDataHandle commonDataHandle;

    @Autowired
    ZcLdEventRadarInfoMapper zcLdEventRadarInfoMapper;

    @Autowired
    BeanUtil beanUtil;

    /**数据处理初始化线程*/
    private Thread thread;

    public void initialHandle(){

        if(RADAR_MODE.RUN_MODE != RADAR_MODE.CLIENT){
            System.out.println("stop RadarDataHandle initialHandle since radar program is running by Server way");
            return;
        }

        thread = initThread();
        thread.start();
    }

    private Thread initThread(){
        Thread thread = new Thread(()->{
            //eventRadarDataHandle();
            eventRadarDataHandleV2();
        }, "thread_radar_data_handle_main_event");
        return thread;
    }

//    /**
//     * 事件雷达数据处理
//     */
//    private void eventRadarDataHandle(){
//
//        while(producer.radarEventDataGrabs.size() == 0){
//            try {
//                //等待雷达数据接入5秒
//                //logger.info("等待事件雷达数据接入5秒时间");
//                Thread.sleep(5000);
//                logger.info("等待雷达数据接入······");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        logger.info("事件雷达数据处理开始");
//
//        int i = 0;
//        //事件雷达数据
//        for(Map.Entry<String, RadarEventDataGrab> r: producer.radarEventDataGrabs.entrySet()) {
//            RadarEventDataGrab radarEventDataGrab = r.getValue();
//            //set车道信息
//            if(radarEventDataGrab.getZcLdLaneMap() == null){
//                radarEventDataGrab.setZcLdLaneMap(commonDataHandle.searchZcLdLaneMap(radarEventDataGrab.getRadarId()));
//            }
//
//            //数据处理线程
//            commonDataHandle.eventThread(radarEventDataGrab, radarEventDataGrab.getZcLdLaneMap(),
//                    "thread_radar_event_data_handle_" + i + "_" + radarEventDataGrab.getRadarId());
//            i++;
//        }
//
//    }

    /**
     * 事件雷达数据处理
     */
    private void eventRadarDataHandleV2(){

        while(producer.radarEventDataGrabs.size() == 0){
            try {
                //等待雷达数据接入5秒
                //logger.info("等待事件雷达数据接入5秒时间");
                Thread.sleep(5000);
                logger.info("等待雷达数据接入······");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        logger.info("事件雷达数据处理开始");

        //事件雷达数据
        for(Map.Entry<String, RadarEventDataGrab> r: producer.radarEventDataGrabs.entrySet()) {
            RadarEventDataGrab radarEventDataGrab = r.getValue();
            //set车道信息
            if(radarEventDataGrab.getZcLdLaneMap() == null){
                radarEventDataGrab.setZcLdLaneMap(commonDataHandle.searchZcLdLaneMap(radarEventDataGrab.getRadarId()));
            }
            ////////////////////////////////////////////////////////////////////////////////////////////////
            //事件雷达数据实时统计
            //DataReader dataReader = new DataReader(radarEventDataGrab.getRadarId());
            if(radarEventDataGrab.dataReader == null){
                radarEventDataGrab.dataReader = new DataReader(radarEventDataGrab.getRadarId()).init(beanUtil);
            }
            //车辆计数
            radarEventDataGrab.dataReader.countNumLai = 0;
            radarEventDataGrab.dataReader.countNumQu = 0;
            List<Map<String,Object>> queryCountMapList = zcLdEventRadarInfoMapper.queryCountNow(radarEventDataGrab.dataReader.getRadarId());
            if(queryCountMapList != null){
                for(Map<String,Object> map: queryCountMapList){
                    String direction = null;
                    long count = 0;
                    if(map.get("direction") != null){
                        direction = (String) map.get("direction");
                    }
                    if(map.get("count") != null){
                        count = (long) map.get("count");
                    }
                    if("L".equals(direction)){
                        radarEventDataGrab.dataReader.countNumLai += count;
                    } else if("R".equals(direction)){
                        radarEventDataGrab.dataReader.countNumQu += count;
                    }
                }

            }
            ////////////////////////////////////////////////////////////////////////////////////////////////
        }

        commonDataHandle.eventRadarDataDispatcher(producer.radarEventDataGrabs);

    }

}
