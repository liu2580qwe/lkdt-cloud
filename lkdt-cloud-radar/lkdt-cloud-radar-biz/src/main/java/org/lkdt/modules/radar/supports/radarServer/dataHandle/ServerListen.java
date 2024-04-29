package org.lkdt.modules.radar.supports.radarServer.dataHandle;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.radar.entity.ZcLdEquipment;
import org.lkdt.modules.radar.mapper.ZcLdEquipmentMapper;
import org.lkdt.modules.radar.supports.radarDataHandle.RADAR_MODE;
import org.lkdt.modules.radar.supports.radarDataHandle.RadarEventDataGrab;
import org.lkdt.modules.radar.supports.radarDataHandle.interfaces.IRadarProduceInterface;
import org.lkdt.modules.radar.supports.radarDataService.DataReader;
import org.lkdt.modules.radar.supports.radarDataService.radarCamUnion.CameraComponent;
import org.lkdt.modules.radar.supports.radarTools.RadarUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class ServerListen implements IRadarProduceInterface {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    //事件雷达集合
    public static Map<String, RadarEventDataGrab> radarEventDataGrabs = new ConcurrentHashMap<>();
    //监听
    Thread thread = null;

    @Autowired
    ZcLdEquipmentMapper zcLdEquipmentMapper;

    @Autowired
    ServerRadarDataHandle radarDataHandle;

    @Autowired
    CameraComponent cameraComponent;

    public JSONArray getRadarEventDataGrabLiveQueuePoll(String radarId) {
        RadarEventDataGrab radarEventDataGrab = radarEventDataGrabs.get(radarId);
        if(radarEventDataGrab != null){
            return radarEventDataGrab.getLiveQueue().poll().getDataBody();
        }
    	return null;
    }

    /**
     * [countNumLai, countNumQu, avgSpeedLai, avgSpeedQu, carNumLai, carNumQu]
     * @param radarId
     * @return
     */
    public JSONObject getRadarDataReader(String radarId) {
        RadarEventDataGrab radarEventDataGrab = radarEventDataGrabs.get(radarId);
        JSONObject jsonObject = new JSONObject();
        DataReader dataReader = radarEventDataGrab.dataReader;
        jsonObject.put("countNumLai", dataReader.countNumLai);
        jsonObject.put("countNumQu", dataReader.countNumQu);
        jsonObject.put("avgSpeedLai", dataReader.avgSpeedLai);
        jsonObject.put("avgSpeedQu", dataReader.avgSpeedQu);
        jsonObject.put("carNumLai", dataReader.carNumLai);
        jsonObject.put("carNumQu", dataReader.carNumQu);
        jsonObject.put("riskValuesLai", dataReader.riskValuesLai.getRiskValue());
        jsonObject.put("riskValuesQu", dataReader.riskValuesQu.getRiskValue());
        return jsonObject;
    }

    public DataReader getDataReader(String radarId) {
        RadarEventDataGrab radarEventDataGrab = radarEventDataGrabs.get(radarId);
        return radarEventDataGrab.dataReader;
    }

    /**
     * 数据库雷达参数实例化
     */
    @PostConstruct
    public void init() {

        if(RADAR_MODE.RUN_MODE != RADAR_MODE.SERVER){
            System.out.println("stop serverListen init");
            return;
        }
        System.out.println("Radar program is running by server way");

        HashMap<String, Object> paramMap = new HashMap<>();
        List<ZcLdEquipment> ldEquipments = zcLdEquipmentMapper.selectByMap(paramMap);
        //new Thread(() -> cameraComponent.init(ldEquipments)).start();
        cameraComponent.init(ldEquipments);//初始化相机资源

        List<ZcLdEquipment> eventEquList = new ArrayList<>();
        //List<ZcLdEquipment> flowEquList = new ArrayList<>();
        //verify valid
        for(ZcLdEquipment z: ldEquipments){
            if(StringUtils.isNotEmpty(z.getIp()) && z.getPort() != null){
                if(StringUtils.equals(z.getEquType(), "001")){
                    //事件雷达
                    eventEquList.add(z);
                } else if(StringUtils.equals(z.getEquType(), "002")) {
                    //流量雷达
                    //flowEquList.add(z);
                }

            }
        }

        thread = new Thread(()->{
            //事件雷达
            for(ZcLdEquipment z: eventEquList){
                RadarEventDataGrab radarEventDataHandler = new RadarEventDataGrab(z.getIp(), z.getPort(), z.getId(), null, null);
                radarEventDataGrabs.put(radarEventDataHandler.getRadarId(), radarEventDataHandler);
            }

            //数据处理启动
            radarDataHandle.initialHandle();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

            logger.info("雷达监听开启中......");
            try {
                while(true){
                    try {
                        Thread.sleep(30000);

                        //事件雷达监听
                        eventRadarDataGrabListenV2(sdf);

                        //流量雷达监听
                        //flowRadarDataGrabListen(sdf);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                logger.error("雷达监听异常关闭......");
            }

        }, "thread_radar_listener");
        thread.start();
    }

    /**
     * 事件雷达监听
     * @param sdf
     */
    private void eventRadarDataGrabListenV2(SimpleDateFormat sdf){
        logger.info(">>>>>>>>>>>>******************************************");

        //监听客户端
//        for(Map.Entry<String, RadarEventDataGrab> r: radarEventDataGrabs.entrySet()){
//            RadarEventDataGrab radarEventDataGrab = r.getValue();
//            logger.info("事件雷达【{}】，心跳时间：{}", radarEventDataGrab.getRadarId(), sdf.format(radarEventDataGrab.getTimeClock()));
//        }

        //输出监听信息
        for(Map.Entry<String, RadarEventDataGrab> r: radarEventDataGrabs.entrySet()){
            RadarEventDataGrab radarEventDataGrab = r.getValue();
            //雷达缓存监控
            logger.info("事件雷达【{}】缓存数目：{}， " + "总容量：{}， " + "剩余容量：{}%" + "， 刷新时钟：{}， 数据活跃：{}-{}分钟以内"
                , radarEventDataGrab.getRadarId(), radarEventDataGrab.getArrayBlockingQueue().size(), RadarUtils.RadarEventSize,
                10000*(RadarUtils.RadarEventSize - radarEventDataGrab.getArrayBlockingQueue().size())/RadarUtils.RadarEventSize/100.0,
                sdf.format(radarEventDataGrab.getHeartBeatPacketClock()), radarEventDataGrab.getHeartBeatPacketClock() != 0l, (Calendar.getInstance().getTimeInMillis() - radarEventDataGrab.getHeartBeatPacketClock())/1000/60);
        }

        logger.info("******************************************<<<<<<<<<<<<");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 主方法
     * @param args
     */
    public static void main(String[] args) {


    }

}


