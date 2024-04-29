package org.lkdt.modules.radar.supports.radarDataService;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.radar.entity.ZcLdEventInfo;
import org.lkdt.modules.radar.entity.ZcLdEventRadarInfo;
import org.lkdt.modules.radar.entity.ZcLdLaneInfo;
import org.lkdt.modules.radar.mapper.ZcLdEventRadarInfoMapper;
import org.lkdt.modules.radar.mapper.ZcLdLaneInfoMapper;
import org.lkdt.modules.radar.supports.mongodb.MongoRadarTemplate;
import org.lkdt.modules.radar.supports.radarDataHandle.PrioritizedRunnable;
import org.lkdt.modules.radar.supports.radarDataHandle.RadarEventDataGrab;
import org.lkdt.modules.radar.supports.radarDataHandle.extend.AbstractCalculator;
import org.lkdt.modules.radar.supports.radarTools.BeanUtil;
import org.lkdt.modules.radar.supports.radarTools.DO.MongoZcLdEventRadarInfo;
import org.lkdt.modules.radar.supports.radarTools.DO.RadarDO;
import org.lkdt.modules.radar.supports.radarTools.RadarEventPool;
import org.lkdt.modules.radar.supports.radarTools.RadarUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class CommonDataHandle extends AbstractCalculator {

    /**入库操作标记：仅作为debug模式调试用*/
    private static boolean isInsertFlag = true;

    static {
        System.out.println("参数初始化");
        //debug调试模式下无需入库
        isInsertFlag = !RadarUtils.isDebug();
        if(!isInsertFlag){
            debugLog();
        }
    }

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BeanUtil beanUtil;

    @Autowired
    ZcLdEventRadarInfoMapper zcLdEventRadarInfoMapper;

    @Autowired
    ZcLdLaneInfoMapper zcLdLaneInfoMapper;

    @Autowired
    CarLinearFitting carLinearFitting;

    @Autowired
    private MongoRadarTemplate mongoRadarTemplate;

    ExecutorService executorService = Executors.newFixedThreadPool(3);

    // 0 无事件 1 车辆停止 2 车辆反方向行驶 （逆行） 3 车辆超速行驶 5 车辆实线变道
    // 101 车辆停止消失 102 车辆反方向行 驶消失 103 车辆超速行驶 消失105 车辆实线变道 消失
    String[] eventArray = new String[]{"1", "2", "3", "5", "101", "102", "103", "105"};

    //事件雷达车道
    public Map<Integer, String> searchZcLdLaneMap(String radarId){
        //事件雷达车道缓存
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("equ_id", radarId);
        List<ZcLdLaneInfo> zcLdLaneInfoList = zcLdLaneInfoMapper.selectByMap(paramMap);
        Map<Integer, String> zcLdLaneMap = new HashMap<>();
        for(ZcLdLaneInfo z: zcLdLaneInfoList){
            zcLdLaneMap.put(z.getLaneRadar(), z.getLaneRoad());
        }
        return zcLdLaneMap;
    }

    /**
     * 事件雷达数据处理线程初始化
     * @param radarEventDataGrab 雷达抓手
     * @param zcLdLaneMap 车道数据
     * @param threadName 线程名称
     * @return
     */
    public void eventThread(RadarEventDataGrab radarEventDataGrab, Map<Integer, String> zcLdLaneMap, String threadName){

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

        Thread thread = new Thread(() -> {

            ArrayBlockingQueue<RadarDO> arrayBlockingQueue = radarEventDataGrab.getArrayBlockingQueue();

            /**数据帧ID缓存old*/
            //List<Integer> integerListOld = new ArrayList<>();
            //定义雷达事件池
            RadarEventPool radarEventPool = new RadarEventPool();
            int low  = radarEventDataGrab.getRANGE_LOW();
            int high = radarEventDataGrab.getRANGE_HIGH();
            while (radarEventDataGrab.getThreadHandleFlag()) {
                //long start = System.currentTimeMillis();
//                    System.out.println("常量池：" + radarEventPool.getCommonPool().size() +
//                            "事件池：" + radarEventPool.getZcLdEventInfos().size() + "丢失池：" + radarEventPool.getZcLdEventRadarInfos().size());
                RadarDO radarDO = null;
                try {
                    radarDO = arrayBlockingQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (radarDO != null) {

//                    //每帧数据入库
//                    try {
//                        radarEventDataGrab.dataReader.targetAllMapperInsertLib(radarEventDataGrab.getRadarId(), radarDO);
//                        radarEventDataGrab.dataReader.targetRadarDataValueCalculator(radarEventDataGrab, radarDO);
//                        radarEventDataGrab.getLiveQueue().offer(radarDO.getDataBody());
//                    } catch (Exception e) {
//                        logger.error("targetAllMapperInsertLib | targetRadarDataValueCalculator | getLiveQueue", e);
//                    }

                    //瞬时快照——3-1
                    RadarDO mRadarDO = new RadarDO();
                    mRadarDO.setDate(radarDO.getDate());
                    JSONArray jArray = new JSONArray();

                    JSONArray jsonArray = radarDO.getDataBody();

                    ////////////////////////////////////////////////////////////////////////////////////////////////
                    ////////////////////////////////////////////////////////////////////////////////////////////////
                    ////////////////////////////////////////////////////////////////////////////////////////////////
                    //TODO:【待删除】事件雷达数据实时统计：数据输入处理
                    //this.dataReaderCalculator(radarEventDataGrab.dataReader, radarDO);
                    ////////////////////////////////////////////////////////////////////////////////////////////////
                    ////////////////////////////////////////////////////////////////////////////////////////////////
                    ////////////////////////////////////////////////////////////////////////////////////////////////
                    try{
                        /**数据帧ID缓存*/
                        List<String> dataFrameList = new ArrayList<>();
                        //获取常规池数据
                        Map<String, ZcLdEventRadarInfo> commonPool = radarEventPool.getCommonPool();
                        //获取事件池数据
                        Map<String, ZcLdEventInfo> eventPool = radarEventPool.getZcLdEventInfos();
                        for (int j = 0; j < jsonArray.size(); j++) {
                            //数据解析
                            JSONObject obj = (JSONObject) jsonArray.get(j);
                            String targetId = obj.getStr("targetId");//目标ID
                            float sX = obj.getFloat("sX");//x坐标
                            float sY = obj.getFloat("sY");//y坐标
                            float vX = obj.getFloat("vX");//x速度
                            float vY = obj.getFloat("vY");//y速度
                            float aX = obj.getFloat("aX");//x加速度
                            float aY = obj.getFloat("aY");//y加速度
                            int laneNum = obj.getInt("laneNum");//车道号
                            //1 小型车，2 大型 车，3 超大型车
                            int carType = obj.getInt("carType");//车辆类型
                            //0 无事件 1 车辆停止 2 车辆反方向行驶 （逆行） 3 车辆超速行驶 5 车辆实线变道
                            //101 车辆停止消失 102 车辆反方向行 驶消失 103 车辆超速行驶 消失105 车辆实线变道 消失
                            int event = obj.getInt("event");//事件类型
                            int carLength = obj.getInt("carLength");//车辆长度
                            //////////////////////////////////////////
                            //修改于2021-04-08 16:15
                            //过滤 y坐标<100 或 y坐标>250 立即排除
                            //////////////////////////////////////////

                            //瞬时快照——3-2
                            jArray.add(obj);

                            //ID缓存
                            dataFrameList.add(targetId);
                            ZcLdEventRadarInfo zcLdEventRadarInfoPool = commonPool.get(targetId);
                            //更新数据
                            if(zcLdEventRadarInfoPool != null){
                                //数值计算处理
                                BigDecimal avg = zcLdEventRadarInfoPool.getSpeedAvg();
                                BigDecimal max = zcLdEventRadarInfoPool.getSpeedMax();
                                long maxVyTimeInMillis = zcLdEventRadarInfoPool.getMaxVyTimeInMillis();
                                BigDecimal min = zcLdEventRadarInfoPool.getSpeedMin();
                                //BigDecimal speedX = zcLdEventRadarInfoPool.getSpeedX();
                                //BigDecimal speedY = zcLdEventRadarInfoPool.getSpeedY();
                                avg = BigDecimal.valueOf(vY).add(avg).divide(new BigDecimal(2));
                                if(BigDecimal.valueOf(vY).abs().compareTo(max.abs()) > 0) {
                                    max = BigDecimal.valueOf(vY);
                                    maxVyTimeInMillis = radarDO.getDate().getTime() ;
                                }
                                obj.put("maxVy", max);
                                obj.put("maxVyTimeInMillis", maxVyTimeInMillis);
                                min = BigDecimal.valueOf(vY).abs().compareTo(min.abs()) < 0? BigDecimal.valueOf(vY): min;
                                BigDecimal speedX = new BigDecimal(vX);
                                BigDecimal speedY = new BigDecimal(vY);
                                Date endTime = radarDO.getDate();
                                BigDecimal coordinateX = new BigDecimal(sX);
                                BigDecimal coordinateY = new BigDecimal(sY);
                                //常规池新增数据
                                zcLdEventRadarInfoPool.setTargetId(targetId);//目标ID
                                zcLdEventRadarInfoPool.setEquId(radarEventDataGrab.getRadarId());//设备ID
                                zcLdEventRadarInfoPool.setSpeedEndX(speedX);//X速度
                                zcLdEventRadarInfoPool.setSpeedEndY(speedY);//Y速度
                                zcLdEventRadarInfoPool.setLaneEndRadar(laneNum);//雷达车道号
                                zcLdEventRadarInfoPool.setLaneEndRoad(zcLdLaneMap.get(laneNum));//道路车道号
                                zcLdEventRadarInfoPool.setCarType(String.valueOf(carType));//车型
                                zcLdEventRadarInfoPool.setCarLength(carLength);//车厂


                                if (zcLdEventRadarInfoPool.getLastY() != -1 ) {
                                    if (Math.abs(vY - zcLdEventRadarInfoPool.getLastY()) < 3) {
                                        zcLdEventRadarInfoPool.setSpeedMax(max);// 最高行驶速度
                                        zcLdEventRadarInfoPool.setMaxVyTimeInMillis(maxVyTimeInMillis);
                                    }
                                }

                                zcLdEventRadarInfoPool.setSpeedMin(min);//最低行驶速度
                                zcLdEventRadarInfoPool.setSpeedAvg(avg);//平均行驶速度
                                zcLdEventRadarInfoPool.setEndTime(endTime);//出雷达时间
                                zcLdEventRadarInfoPool.setEndCoordinateX(coordinateX);//出雷达坐标X
                                zcLdEventRadarInfoPool.setEndCoordinateY(coordinateY);//出雷达坐标Y
                                //常规池数据装入
                                commonPool.put(targetId, zcLdEventRadarInfoPool);

                                //更新行驶轨迹
                                JSONObject jObj = new JSONObject();
                                jObj.put("sX", sX);
                                jObj.put("sY", sY);
                                jObj.put("vX", vX);
                                jObj.put("vY", vY);
                                jObj.put("aX", aX);
                                jObj.put("aY", aY);
                                jObj.put("laneNum", laneNum);
                                jObj.put("carType", carType);
                                jObj.put("event", event);
                                jObj.put("carLength", carLength);
                                zcLdEventRadarInfoPool.getJsonArray().put(jObj);
                                zcLdEventRadarInfoPool.getJsonArraySrc().put(obj);

                                zcLdEventRadarInfoPool.setLastY(sY);



                            } else {
                                //新数据
                                ZcLdEventRadarInfo zcLdEventRadarInfo = new ZcLdEventRadarInfo();
                                zcLdEventRadarInfo.setJsonArray(new JSONArray());
                                zcLdEventRadarInfo.setJsonArraySrc(new JSONArray());
                                //常规池新增数据
                                zcLdEventRadarInfo.setTargetId(targetId);//目标ID
                                zcLdEventRadarInfo.setEquId(radarEventDataGrab.getRadarId());//设备ID
                                zcLdEventRadarInfo.setSpeedStartX(new BigDecimal(vX));//X速度
                                zcLdEventRadarInfo.setSpeedStartY(new BigDecimal(vY));//Y速度
                                zcLdEventRadarInfo.setLaneStartRadar(laneNum);//雷达车道号 1、2、3、4、5、6、7
                                zcLdEventRadarInfo.setLaneStartRoad(zcLdLaneMap.get(laneNum));//道路车道号
                                zcLdEventRadarInfo.setCarType(String.valueOf(carType));//车型
                                zcLdEventRadarInfo.setCarLength(carLength);//车厂
                                zcLdEventRadarInfo.setSpeedMax(new BigDecimal(vY));//最高行驶速度
                                zcLdEventRadarInfo.setMaxVyTimeInMillis(radarDO.getDate().getTime());
                                zcLdEventRadarInfo.setSpeedMin(new BigDecimal(vY));//最低行驶速度
                                zcLdEventRadarInfo.setSpeedAvg(new BigDecimal(vY));//平均行驶速度
                                zcLdEventRadarInfo.setBeginTime(radarDO.getDate());//进雷达时间
                                zcLdEventRadarInfo.setEndTime(null);//出雷达时间
                                zcLdEventRadarInfo.setBeginCoordinateX(new BigDecimal(sX));//进雷达坐标X
                                zcLdEventRadarInfo.setBeginCoordinateY(new BigDecimal(sY));//进雷达坐标Y
                                zcLdEventRadarInfo.setEndCoordinateX(null);//出雷达坐标X
                                zcLdEventRadarInfo.setEndCoordinateY(null);//出雷达坐标Y
                                //zcLdEventRadarInfoMapper.insert(zcLdEventRadarInfo);
                                //行驶轨迹
                                JSONObject jObj = new JSONObject();
                                jObj.put("sX", sX);
                                jObj.put("sY", sY);
                                jObj.put("vX", vX);
                                jObj.put("vY", vY);
                                jObj.put("aX", aX);
                                jObj.put("aY", aY);
                                jObj.put("laneNum", laneNum);
                                jObj.put("carType", carType);
                                jObj.put("event", event);
                                jObj.put("carLength", carLength);
                                zcLdEventRadarInfo.getJsonArray().put(jObj);
                                zcLdEventRadarInfo.getJsonArraySrc().put(obj);

                                //常规池数据装入
                                commonPool.put(targetId, zcLdEventRadarInfo);

                                obj.put("maxVy", new BigDecimal(vY));
                                obj.put("maxVyTimeInMillis", radarDO.getDate().getTime());
                            }

                            if (event != 0 ) {
                                ZcLdEventInfo zcLdEventInfo = new ZcLdEventInfo();
                                zcLdEventInfo.setEquId(radarEventDataGrab.getRadarId());
                                zcLdEventInfo.setEventRadarInfoId(null);
                                zcLdEventInfo.setCoordinateX(new BigDecimal(sX));
                                zcLdEventInfo.setCoordinateY(new BigDecimal(sY));
                                zcLdEventInfo.setSpeedX(new BigDecimal(vX));
                                zcLdEventInfo.setSpeedY(new BigDecimal(vY));
                                zcLdEventInfo.setAccelerationX(new BigDecimal(aX));
                                zcLdEventInfo.setAccelerationY(new BigDecimal(aY));
                                zcLdEventInfo.setLaneRadar(laneNum);//1、2、3、4、5、6、7
                                zcLdEventInfo.setLaneRoad(zcLdLaneMap.get(laneNum));
                                zcLdEventInfo.setEventType(String.valueOf(event));
                                zcLdEventInfo.setCarLength(carLength);
                                zcLdEventInfo.setCreateTime(radarDO.getDate());
                                //同一车辆相同事件只记录一条数据
                                eventPool.put(targetId + "_" + event, zcLdEventInfo);
                            }

                        }

                        //每帧数据入库
                        try {
                            radarEventDataGrab.dataReader.targetAllMapperInsertLib(radarEventDataGrab.getRadarId(), radarDO);
                            radarEventDataGrab.dataReader.targetRadarDataValueCalculator(radarEventDataGrab, radarDO);
                            radarEventDataGrab.getLiveQueue().offer(radarDO);
                        } catch (Exception e) {
                            logger.error("targetAllMapperInsertLib | targetRadarDataValueCalculator | getLiveQueue", e);
                        }

                        //System.out.println(dataFrameList.toString());
                        //获取丢失池数据
                        //Map<String, ZcLdEventRadarInfo> diuShiPool = radarEventPool.getZcLdEventRadarInfos();
                        //常规池待删除数据
                        List<String> commonRemoved = new ArrayList<>();
                        //遍历常规池
                        for(Map.Entry<String, ZcLdEventRadarInfo> map: commonPool.entrySet()){
                            //新ID不包含旧ID，数据丢入丢失池中
                            //1、信号消失时立即处理
                            //2、end y坐标<100 或 y坐标>250 立即处理
                            BigDecimal y = map.getValue().getEndCoordinateY();
                            //////////////////////////////////////////
                            //修改于2021-04-08 16:15   || (y != null && (y.floatValue() < 100 || y.floatValue() > 250))
                            //////////////////////////////////////////
                            if(!dataFrameList.contains(map.getKey())){
                                ZcLdEventRadarInfo diuShiData = map.getValue();
                                //diuShiPool.put(zcLdEventRadarInfo.getKey(), zcLdEventRadarInfo.getValue());
                                commonRemoved.add(map.getKey());
                                //1.检查丢失池中数据是否有事件发生
                                //1.1若无事件发生则入库一条数据至ZcLdEventRadarInfos表中
                                //1.2若有事件发生则需入库多条事件至ZcLdEventInfo表中
                                ///////////////////////////////////////////////////////////////////
                                ////////////////////////入库操作////////////////////////////////////
                                ///////////////////////////////////////////////////////////////////
                                //线程池操作
                                if(diuShiData != null){
                                    //已丢失数据入库
                                    //insert
                                    //RadarDataLibHandle.zcLdEventRadarLibQueue.offer(diuShiData);
//                                    long start = System.currentTimeMillis();
                                    if(this.dataPersistenceHandleOfDetail(diuShiData, low, high)){
                                        if(isInsertFlag){

                                            //车辆计数
                                            String laneStartRoad = diuShiData.getLaneStartRoad();
                                            if(StringUtils.isNotEmpty(laneStartRoad)){
                                                if(laneStartRoad.startsWith("L")){
                                                    radarEventDataGrab.dataReader.countNumLai++;
                                                } else if(laneStartRoad.startsWith("R")){
                                                    radarEventDataGrab.dataReader.countNumQu++;
                                                }
                                            }

                                            diuShiData.setId(StringUtils.getUUID()+diuShiData.getEquId());
                                            //数据拟合
                                            carLinearFitting.linearFitting(diuShiData);

                                            try{
                                                radarEventDataGrab.dataReader.targetBeforeInsertLib(radarEventDataGrab.getRadarId(), diuShiData);
                                            } catch (Exception e){}

                                            zcLdEventRadarInfoMapper.insert(diuShiData);

                                            //mongo入库
                                            executorService.submit(()->{
                                                try{
                                                    ZcLdEventRadarInfo diuShiDataCopy = diuShiData;

                                                    MongoZcLdEventRadarInfo mongoZcLdEventRadarInfo = new MongoZcLdEventRadarInfo();
                                                    mongoZcLdEventRadarInfo.setId(diuShiDataCopy.getId())
                                                            .setCreateTime(diuShiDataCopy.getCreateTime())
                                                            .setEquId(diuShiDataCopy.getEquId())
                                                            .setPathTrace(diuShiDataCopy.getJsonArray().toString());
                                                    mongoRadarTemplate.insert(mongoZcLdEventRadarInfo);
                                                } catch (Exception e) {logger.error("【mongo】雷达轨迹入库异常", e);}
                                            });


                                            try {
                                                //TODO:待删除
                                                radarEventDataGrab.dataReader.targetRadarDataTrack(diuShiData);
                                            } catch (Exception e) { e.printStackTrace(); }

                                        }
                                    }

                                    for(String e: eventArray){
                                        ZcLdEventInfo zcLdEventInfo = eventPool.get(diuShiData.getTargetId() + "_" + e);
                                        //事件入库
                                        if(zcLdEventInfo != null){
                                            //RadarDataLibHandle.zcLdEventLibQueue.offer(zcLdEventInfo);
                                            zcLdEventInfo.setEventRadarInfoId(diuShiData.getId());
                                            if(this.dataPersistenceHandleOfEvent(zcLdEventInfo, diuShiData, low, high)){
//                                                if(isInsertFlag){
//                                                    zcLdEventInfoMapper.insert(zcLdEventInfo);
//                                                }
                                            }
                                            eventPool.remove(diuShiData.getTargetId() + "_" + e);
                                        }
                                    }

//                                    long end = System.currentTimeMillis();
//                                    System.out.println("插入耗时：" + (end - start));

                                }
                                ///////////////////////////////////////////////////////////////////
                                ///////////////////////////////////////////////////////////////////
                                ///////////////////////////////////////////////////////////////////
                            }
                        }
                        //常规池删除已丢失数据
                        for(String s: commonRemoved){
                            commonPool.remove(s);
                        }

                        //瞬时快照——3-3
                        mRadarDO.setDataBody(jArray);
                        radarEventDataGrab.setMomentHandleRadarDO(mRadarDO);

                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("事件雷达数据", e);
                    }
                } else {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

//                logger.info("常量池数据大小：{}，事件池数据大小{}，丢失池{}", radarEventPool.getCommonPool().size(),
//                        radarEventPool.getZcLdEventInfos().size(), radarEventPool.getZcLdEventRadarInfos().size());

//                long end = System.currentTimeMillis();
//                System.out.printf("【%s】耗时：%s\n", radarEventDataGrab.getRadarId(), end - start);
            }
        }, threadName);
        //threadEvent.add(thread);
        radarEventDataGrab.setDataHandleThread(thread);
        radarEventDataGrab.setZcLdLaneMap(zcLdLaneMap);
        thread.start();
    }

    /**
     * 事件雷达数据分发器
     * @param radarEventDataGrab 雷达抓手
     * @param zcLdLaneMap 车道数据
     * @param threadName 线程名称
     * @return
     */
    public void eventRadarDataDispatcher(Map<String, RadarEventDataGrab> radarEventDataGrabs){
        ArrayBlockingQueue<RadarDO> arrayBlockingQueue = RadarEventDataGrab.COMMON_ArrayBlockingQueue;

        while(true) {
            try {

                RadarDO radarDO = arrayBlockingQueue.take();

                String radarId = radarDO.getRadarId();
                RadarEventDataGrab radarEventDataGrab = radarEventDataGrabs.get(radarId);
                if(radarEventDataGrab == null){
                    continue;
                }
                Map<Integer, String> zcLdLaneMap = radarEventDataGrab.getZcLdLaneMap();
                
                RadarEventDataGrab.executor.execute(new PrioritizedRunnable(radarDO.getDate().getTime()) {
                    @Override
                    public void run() {
                    	
                    	//定义雷达事件池
                        RadarEventPool radarEventPool = RadarEventPool.PUBLIC_POOL.get(radarId);
                        if(radarEventPool == null) {
                        	RadarEventPool.PUBLIC_POOL.put(radarId, new RadarEventPool());
                        	radarEventPool = RadarEventPool.PUBLIC_POOL.get(radarId);
                        }
                        
                        int low  = radarEventDataGrab.getRANGE_LOW();
                        int high = radarEventDataGrab.getRANGE_HIGH();
                        if(radarEventDataGrab.getThreadHandleFlag()) {
                            if (radarDO != null) {

//                    //每帧数据入库
//                    try {
//                        radarEventDataGrab.dataReader.targetAllMapperInsertLib(radarEventDataGrab.getRadarId(), radarDO);
//                        radarEventDataGrab.dataReader.targetRadarDataValueCalculator(radarEventDataGrab, radarDO);
//                        radarEventDataGrab.getLiveQueue().offer(radarDO.getDataBody());
//                    } catch (Exception e) {
//                        logger.error("targetAllMapperInsertLib | targetRadarDataValueCalculator | getLiveQueue", e);
//                    }

                                //瞬时快照——3-1
                                RadarDO mRadarDO = new RadarDO();
                                mRadarDO.setDate(radarDO.getDate());
                                JSONArray jArray = new JSONArray();

                                JSONArray jsonArray = radarDO.getDataBody();

                                ////////////////////////////////////////////////////////////////////////////////////////////////
                                //TODO:【待删除】事件雷达数据实时统计：数据输入处理
                                //this.dataReaderCalculator(radarEventDataGrab.dataReader, radarDO);
                                ////////////////////////////////////////////////////////////////////////////////////////////////
                                try{
                                    /**数据帧ID缓存*/
                                    List<String> dataFrameList = new ArrayList<>();
                                    //获取常规池数据
                                    Map<String, ZcLdEventRadarInfo> commonPool = radarEventPool.getCommonPool();
                                    //获取事件池数据
                                    Map<String, ZcLdEventInfo> eventPool = radarEventPool.getZcLdEventInfos();
                                    for (int j = 0; j < jsonArray.size(); j++) {
                                        //数据解析
                                        JSONObject obj = (JSONObject) jsonArray.get(j);
                                        String targetId = obj.getStr("targetId");//目标ID
                                        float sX = obj.getFloat("sX");//x坐标
                                        float sY = obj.getFloat("sY");//y坐标
                                        float vX = obj.getFloat("vX");//x速度
                                        float vY = obj.getFloat("vY");//y速度
                                        float aX = obj.getFloat("aX");//x加速度
                                        float aY = obj.getFloat("aY");//y加速度
                                        int laneNum = obj.getInt("laneNum");//车道号
                                        //1 小型车，2 大型 车，3 超大型车
                                        int carType = obj.getInt("carType");//车辆类型
                                        //0 无事件 1 车辆停止 2 车辆反方向行驶 （逆行） 3 车辆超速行驶 5 车辆实线变道
                                        //101 车辆停止消失 102 车辆反方向行 驶消失 103 车辆超速行驶 消失105 车辆实线变道 消失
                                        int event = obj.getInt("event");//事件类型
                                        int carLength = obj.getInt("carLength");//车辆长度
                                        //////////////////////////////////////////
                                        //修改于2021-04-08 16:15
                                        //过滤 y坐标<100 或 y坐标>250 立即排除
                                        //////////////////////////////////////////

                                        //瞬时快照——3-2
                                        jArray.add(obj);

                                        //ID缓存
                                        dataFrameList.add(targetId);
                                        ZcLdEventRadarInfo zcLdEventRadarInfoPool = commonPool.get(targetId);
                                        //更新数据
                                        if(zcLdEventRadarInfoPool != null){
                                            //数值计算处理
                                            BigDecimal avg = zcLdEventRadarInfoPool.getSpeedAvg();
                                            BigDecimal max = zcLdEventRadarInfoPool.getSpeedMax();
                                            long maxVyTimeInMillis = zcLdEventRadarInfoPool.getMaxVyTimeInMillis();
                                            BigDecimal min = zcLdEventRadarInfoPool.getSpeedMin();
                                            //BigDecimal speedX = zcLdEventRadarInfoPool.getSpeedX();
                                            //BigDecimal speedY = zcLdEventRadarInfoPool.getSpeedY();
                                            avg = BigDecimal.valueOf(vY).add(avg).divide(new BigDecimal(2));
                                            if(BigDecimal.valueOf(vY).abs().compareTo(max.abs()) > 0) {
                                                max = BigDecimal.valueOf(vY);
                                                maxVyTimeInMillis = radarDO.getDate().getTime() ;
                                            }
                                            obj.put("maxVy", max);
                                            obj.put("maxVyTimeInMillis", maxVyTimeInMillis);
                                            min = BigDecimal.valueOf(vY).abs().compareTo(min.abs()) < 0? BigDecimal.valueOf(vY): min;
                                            BigDecimal speedX = new BigDecimal(vX);
                                            BigDecimal speedY = new BigDecimal(vY);
                                            Date endTime = radarDO.getDate();
                                            BigDecimal coordinateX = new BigDecimal(sX);
                                            BigDecimal coordinateY = new BigDecimal(sY);
                                            //常规池新增数据
                                            zcLdEventRadarInfoPool.setTargetId(targetId);//目标ID
                                            zcLdEventRadarInfoPool.setEquId(radarEventDataGrab.getRadarId());//设备ID
                                            zcLdEventRadarInfoPool.setSpeedEndX(speedX);//X速度
                                            zcLdEventRadarInfoPool.setSpeedEndY(speedY);//Y速度
                                            zcLdEventRadarInfoPool.setLaneEndRadar(laneNum);//雷达车道号
                                            zcLdEventRadarInfoPool.setLaneEndRoad(zcLdLaneMap.get(laneNum));//道路车道号
                                            zcLdEventRadarInfoPool.setCarType(String.valueOf(carType));//车型
                                            zcLdEventRadarInfoPool.setCarLength(carLength);//车厂


                                            if (zcLdEventRadarInfoPool.getLastY() != -1 ) {
                                                if (Math.abs(vY - zcLdEventRadarInfoPool.getLastY()) < 3) {
                                                    zcLdEventRadarInfoPool.setSpeedMax(max);// 最高行驶速度
                                                    zcLdEventRadarInfoPool.setMaxVyTimeInMillis(maxVyTimeInMillis);
                                                }
                                            }

                                            zcLdEventRadarInfoPool.setSpeedMin(min);//最低行驶速度
                                            zcLdEventRadarInfoPool.setSpeedAvg(avg);//平均行驶速度
                                            zcLdEventRadarInfoPool.setEndTime(endTime);//出雷达时间
                                            zcLdEventRadarInfoPool.setEndCoordinateX(coordinateX);//出雷达坐标X
                                            zcLdEventRadarInfoPool.setEndCoordinateY(coordinateY);//出雷达坐标Y
                                            //常规池数据装入
                                            commonPool.put(targetId, zcLdEventRadarInfoPool);

                                            //更新行驶轨迹
                                            JSONObject jObj = new JSONObject();
                                            jObj.put("sX", sX);
                                            jObj.put("sY", sY);
                                            jObj.put("vX", vX);
                                            jObj.put("vY", vY);
                                            jObj.put("aX", aX);
                                            jObj.put("aY", aY);
                                            jObj.put("laneNum", laneNum);
                                            jObj.put("carType", carType);
                                            jObj.put("event", event);
                                            jObj.put("carLength", carLength);
                                            zcLdEventRadarInfoPool.getJsonArray().put(jObj);
                                            zcLdEventRadarInfoPool.getJsonArraySrc().put(obj);

                                            zcLdEventRadarInfoPool.setLastY(sY);



                                        } else {
                                            //新数据
                                            ZcLdEventRadarInfo zcLdEventRadarInfo = new ZcLdEventRadarInfo();
                                            zcLdEventRadarInfo.setJsonArray(new JSONArray());
                                            zcLdEventRadarInfo.setJsonArraySrc(new JSONArray());
                                            //常规池新增数据
                                            zcLdEventRadarInfo.setTargetId(targetId);//目标ID
                                            zcLdEventRadarInfo.setEquId(radarEventDataGrab.getRadarId());//设备ID
                                            zcLdEventRadarInfo.setSpeedStartX(new BigDecimal(vX));//X速度
                                            zcLdEventRadarInfo.setSpeedStartY(new BigDecimal(vY));//Y速度
                                            zcLdEventRadarInfo.setLaneStartRadar(laneNum);//雷达车道号 1、2、3、4、5、6、7
                                            zcLdEventRadarInfo.setLaneStartRoad(zcLdLaneMap.get(laneNum));//道路车道号
                                            zcLdEventRadarInfo.setCarType(String.valueOf(carType));//车型
                                            zcLdEventRadarInfo.setCarLength(carLength);//车厂
                                            zcLdEventRadarInfo.setSpeedMax(new BigDecimal(vY));//最高行驶速度
                                            zcLdEventRadarInfo.setMaxVyTimeInMillis(radarDO.getDate().getTime());
                                            zcLdEventRadarInfo.setSpeedMin(new BigDecimal(vY));//最低行驶速度
                                            zcLdEventRadarInfo.setSpeedAvg(new BigDecimal(vY));//平均行驶速度
                                            zcLdEventRadarInfo.setBeginTime(radarDO.getDate());//进雷达时间
                                            zcLdEventRadarInfo.setEndTime(null);//出雷达时间
                                            zcLdEventRadarInfo.setBeginCoordinateX(new BigDecimal(sX));//进雷达坐标X
                                            zcLdEventRadarInfo.setBeginCoordinateY(new BigDecimal(sY));//进雷达坐标Y
                                            zcLdEventRadarInfo.setEndCoordinateX(null);//出雷达坐标X
                                            zcLdEventRadarInfo.setEndCoordinateY(null);//出雷达坐标Y
                                            //zcLdEventRadarInfoMapper.insert(zcLdEventRadarInfo);
                                            //行驶轨迹
                                            JSONObject jObj = new JSONObject();
                                            jObj.put("sX", sX);
                                            jObj.put("sY", sY);
                                            jObj.put("vX", vX);
                                            jObj.put("vY", vY);
                                            jObj.put("aX", aX);
                                            jObj.put("aY", aY);
                                            jObj.put("laneNum", laneNum);
                                            jObj.put("carType", carType);
                                            jObj.put("event", event);
                                            jObj.put("carLength", carLength);
                                            zcLdEventRadarInfo.getJsonArray().put(jObj);
                                            zcLdEventRadarInfo.getJsonArraySrc().put(obj);

                                            //常规池数据装入
                                            commonPool.put(targetId, zcLdEventRadarInfo);

                                            obj.put("maxVy", new BigDecimal(vY));
                                            obj.put("maxVyTimeInMillis", radarDO.getDate().getTime());
                                        }

                                        if (event != 0 ) {
                                            ZcLdEventInfo zcLdEventInfo = new ZcLdEventInfo();
                                            zcLdEventInfo.setEquId(radarEventDataGrab.getRadarId());
                                            zcLdEventInfo.setEventRadarInfoId(null);
                                            zcLdEventInfo.setCoordinateX(new BigDecimal(sX));
                                            zcLdEventInfo.setCoordinateY(new BigDecimal(sY));
                                            zcLdEventInfo.setSpeedX(new BigDecimal(vX));
                                            zcLdEventInfo.setSpeedY(new BigDecimal(vY));
                                            zcLdEventInfo.setAccelerationX(new BigDecimal(aX));
                                            zcLdEventInfo.setAccelerationY(new BigDecimal(aY));
                                            zcLdEventInfo.setLaneRadar(laneNum);//1、2、3、4、5、6、7
                                            zcLdEventInfo.setLaneRoad(zcLdLaneMap.get(laneNum));
                                            zcLdEventInfo.setEventType(String.valueOf(event));
                                            zcLdEventInfo.setCarLength(carLength);
                                            zcLdEventInfo.setCreateTime(radarDO.getDate());
                                            //同一车辆相同事件只记录一条数据
                                            eventPool.put(targetId + "_" + event, zcLdEventInfo);
                                        }

                                    }

                                    //每帧数据入库
                                    try {
                                        radarEventDataGrab.dataReader.targetAllMapperInsertLib(radarEventDataGrab.getRadarId(), radarDO);
                                        radarEventDataGrab.dataReader.targetRadarDataValueCalculator(radarEventDataGrab, radarDO);
                                        radarEventDataGrab.getLiveQueue().offer(radarDO);
                                    } catch (Exception e) {
                                        logger.error("targetAllMapperInsertLib | targetRadarDataValueCalculator | getLiveQueue", e);
                                    }

                                    //System.out.println(dataFrameList.toString());
                                    //获取丢失池数据
                                    //Map<String, ZcLdEventRadarInfo> diuShiPool = radarEventPool.getZcLdEventRadarInfos();
                                    //常规池待删除数据
                                    List<String> commonRemoved = new ArrayList<>();
                                    //遍历常规池
                                    for(Map.Entry<String, ZcLdEventRadarInfo> map: commonPool.entrySet()){
                                        //新ID不包含旧ID，数据丢入丢失池中
                                        //1、信号消失时立即处理
                                        //2、end y坐标<100 或 y坐标>250 立即处理
                                        BigDecimal y = map.getValue().getEndCoordinateY();
                                        //////////////////////////////////////////
                                        //修改于2021-04-08 16:15   || (y != null && (y.floatValue() < 100 || y.floatValue() > 250))
                                        //////////////////////////////////////////
                                        if(!dataFrameList.contains(map.getKey())){
                                            ZcLdEventRadarInfo diuShiData = map.getValue();
                                            //diuShiPool.put(zcLdEventRadarInfo.getKey(), zcLdEventRadarInfo.getValue());
                                            commonRemoved.add(map.getKey());
                                            //1.检查丢失池中数据是否有事件发生
                                            //1.1若无事件发生则入库一条数据至ZcLdEventRadarInfos表中
                                            //1.2若有事件发生则需入库多条事件至ZcLdEventInfo表中
                                            ///////////////////////////////////////////////////////////////////
                                            ////////////////////////入库操作////////////////////////////////////
                                            ///////////////////////////////////////////////////////////////////
                                            //线程池操作
                                            if(diuShiData != null){
                                                //已丢失数据入库
                                                //insert
                                                //RadarDataLibHandle.zcLdEventRadarLibQueue.offer(diuShiData);
//                                    long start = System.currentTimeMillis();
                                                if(CommonDataHandle.this.dataPersistenceHandleOfDetail(diuShiData, low, high)){
                                                    if(isInsertFlag){

                                                        //车辆计数
                                                        String laneStartRoad = diuShiData.getLaneStartRoad();
                                                        if(StringUtils.isNotEmpty(laneStartRoad)){
                                                            if(laneStartRoad.startsWith("L")){
                                                                radarEventDataGrab.dataReader.countNumLai++;
                                                            } else if(laneStartRoad.startsWith("R")){
                                                                radarEventDataGrab.dataReader.countNumQu++;
                                                            }
                                                        }

                                                        diuShiData.setId(StringUtils.getUUID()+diuShiData.getEquId());
                                                        //数据拟合
                                                        carLinearFitting.linearFitting(diuShiData);

                                                        try{
                                                            radarEventDataGrab.dataReader.targetBeforeInsertLib(radarEventDataGrab.getRadarId(), diuShiData);
                                                        } catch (Exception e){}

                                                        zcLdEventRadarInfoMapper.insert(diuShiData);

                                                        //mongo入库
                                                        executorService.submit(()->{
                                                            try{
                                                                ZcLdEventRadarInfo diuShiDataCopy = diuShiData;

                                                                MongoZcLdEventRadarInfo mongoZcLdEventRadarInfo = new MongoZcLdEventRadarInfo();
                                                                mongoZcLdEventRadarInfo.setId(diuShiDataCopy.getId())
                                                                        .setCreateTime(diuShiDataCopy.getCreateTime())
                                                                        .setEquId(diuShiDataCopy.getEquId())
                                                                        .setPathTrace(diuShiDataCopy.getJsonArray().toString());
                                                                mongoRadarTemplate.insert(mongoZcLdEventRadarInfo);
                                                            } catch (Exception e) {logger.error("【mongo】雷达轨迹入库异常", e);}
                                                        });


                                                        try {
                                                            //TODO:待删除
                                                            radarEventDataGrab.dataReader.targetRadarDataTrack(diuShiData);
                                                        } catch (Exception e) { e.printStackTrace(); }

                                                    }
                                                }

                                                for(String e: eventArray){
                                                    ZcLdEventInfo zcLdEventInfo = eventPool.get(diuShiData.getTargetId() + "_" + e);
                                                    //事件入库
                                                    if(zcLdEventInfo != null){
                                                        //RadarDataLibHandle.zcLdEventLibQueue.offer(zcLdEventInfo);
                                                        zcLdEventInfo.setEventRadarInfoId(diuShiData.getId());
                                                        if(CommonDataHandle.this.dataPersistenceHandleOfEvent(zcLdEventInfo, diuShiData, low, high)){
//                                                if(isInsertFlag){
//                                                    zcLdEventInfoMapper.insert(zcLdEventInfo);
//                                                }
                                                        }
                                                        eventPool.remove(diuShiData.getTargetId() + "_" + e);
                                                    }
                                                }


                                            }
                                            ///////////////////////////////////////////////////////////////////
                                        }
                                    }
                                    //常规池删除已丢失数据
                                    for(String s: commonRemoved){
                                        commonPool.remove(s);
                                    }

                                    //瞬时快照——3-3
                                    mRadarDO.setDataBody(jArray);
                                    radarEventDataGrab.setMomentHandleRadarDO(mRadarDO);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    logger.error("事件雷达数据", e);
                                }
                            }
                        }
                    }
                });

//                radarEventDataGrab.executors.execute(() -> {
//
//                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
            }
        }
    }

    public static void debugLog(){
        System.out.println("------------------------------------------------------------------------------------------------------------");
        System.out.println("警告：如果启动过程中发现此日志，请联系开发人员修改并重新编译，因为本地测试用的代码启动成功，生产环境禁止使用此方法");
        System.out.println("------------------------------------------------------------------------------------------------------------");
    }

    public static void main(String[] args) throws InterruptedException {
        int i = 0;
        while(true){
            RadarDO radarDO = new RadarDO();
            radarDO.setDate(Calendar.getInstance().getTime());
            radarDO.setNanoSecond(System.nanoTime());
            i++;
            System.out.println(i);
        }

    }
}
