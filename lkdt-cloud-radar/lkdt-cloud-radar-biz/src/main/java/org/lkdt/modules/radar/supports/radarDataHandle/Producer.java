package org.lkdt.modules.radar.supports.radarDataHandle;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.radar.entity.ZcLdEquipment;
import org.lkdt.modules.radar.mapper.ZcLdEquipmentMapper;
import org.lkdt.modules.radar.supports.radarDataHandle.extend.RadarClientV2;
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
public class Producer implements IRadarProduceInterface {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    //事件雷达集合
    public Map<String, RadarEventDataGrab> radarEventDataGrabs = new ConcurrentHashMap<>();
    //监听
    Thread thread = null;

    @Autowired
    ZcLdEquipmentMapper zcLdEquipmentMapper;

    @Autowired
    RadarDataHandle radarDataHandle;

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
        jsonObject.put("threeStatusLai", dataReader.threeStatusLai);
        jsonObject.put("threeStatusQu", dataReader.threeStatusQu);
        jsonObject.put("riskLai", dataReader.riskLai);
        jsonObject.put("riskQu", dataReader.riskQu);
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

        if(RADAR_MODE.RUN_MODE != RADAR_MODE.CLIENT){
            System.out.println("stop producer init");
            return;
        }
        System.out.println("Radar program is running by client way");

        HashMap<String, Object> paramMap = new HashMap<>();
        List<ZcLdEquipment> ldEquipments = zcLdEquipmentMapper.selectByMap(paramMap);
        cameraComponent.init(ldEquipments);
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
                RadarEventDataGrab radarEventDataHandler = new RadarEventDataGrab(z.getIp(), z.getPort(), z.getId(), null, null).run("thread_radar_event_grab_" + z.getId());
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
     * 数据库雷达参数实例化
     */
    //@PostConstruct
    public void testInit() {

        if(RADAR_MODE.RUN_MODE != RADAR_MODE.CLIENT){
            System.out.println("stop producer init");
            return;
        }
        System.out.println("Radar program is running by client way");

        HashMap<String, Object> paramMap = new HashMap<>();
        List<ZcLdEquipment> ldEquipments = zcLdEquipmentMapper.selectByMap(paramMap);
        cameraComponent.init(ldEquipments);
        List<ZcLdEquipment> eventEquList = new ArrayList<>();
        //List<ZcLdEquipment> flowEquList = new ArrayList<>();
        //verify valid
        for(ZcLdEquipment z: ldEquipments){

            if(StringUtils.isNotEmpty(z.getIp()) && z.getPort() != null){
                if(StringUtils.equals(z.getEquType(), "001") && z.getId().equals("1001")){
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
                RadarEventDataGrab radarEventDataHandler = new RadarEventDataGrab(z.getIp(), z.getPort(), z.getId(), null, null).run("thread_radar_event_grab_" + z.getId());
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

        //监听客户端
        for(Map.Entry<String, RadarEventDataGrab> r: radarEventDataGrabs.entrySet()){
            RadarEventDataGrab radarEventDataGrab = r.getValue();
            logger.info("事件雷达【{}】，心跳时间：{}", radarEventDataGrab.getRadarId(), sdf.format(radarEventDataGrab.getRadarClient().getLastHeartHeapTime()));
            long differSecond = (Calendar.getInstance().getTimeInMillis() - radarEventDataGrab.getRadarClient().getLastHeartHeapTime())/1000; //秒
            if(differSecond > 120){//超过120秒无心跳数据，重新创建连接
                //关闭
                radarEventDataGrab.getRadarClient().shutdown();
                logger.info("事件雷达【{}】重新创建连接", radarEventDataGrab.getRadarId());
                RadarClientV2 radarClient = new RadarClientV2(radarEventDataGrab.getHost(), radarEventDataGrab.getPort(), radarEventDataGrab);
                radarEventDataGrab.setRadarClient(radarClient);
                radarClient.connect();
            }

            long differDataMin = (Calendar.getInstance().getTimeInMillis() - radarEventDataGrab.getRadarClient().getLastDataTime())/1000/60;
            if(differDataMin > 30) {//超过30分钟无数据，重新发送开始运行
                //发送启动命令
                logger.info("事件雷达【{}】开始运行", radarEventDataGrab.getRadarId());
                radarEventDataGrab.getRadarClient().sendStartCommand();
            }
        }

        //输出监听信息
        for(Map.Entry<String, RadarEventDataGrab> r: radarEventDataGrabs.entrySet()){
            RadarEventDataGrab radarEventDataGrab = r.getValue();
            RadarClientV2 radarClient = radarEventDataGrab.getRadarClient();
            //雷达缓存监控
            logger.info("事件雷达【{}】缓存数目：{}， " + "总容量：{}， " + "剩余容量：{}%" + "， 刷新时钟：{}， 数据活跃：{}-{}分钟以内"
                , radarEventDataGrab.getRadarId(), radarEventDataGrab.getArrayBlockingQueue().size(), RadarUtils.RadarEventSize,
                10000*(RadarUtils.RadarEventSize - radarEventDataGrab.getArrayBlockingQueue().size())/RadarUtils.RadarEventSize/100.0,
                sdf.format(radarClient.getLastDataTime()), radarClient.getLastDataTime() != 0l, (Calendar.getInstance().getTimeInMillis() - radarClient.getLastDataTime())/1000);
            radarEventDataGrab.setTimeOldClock(radarEventDataGrab.getTimeClock());
        }
    }


}


