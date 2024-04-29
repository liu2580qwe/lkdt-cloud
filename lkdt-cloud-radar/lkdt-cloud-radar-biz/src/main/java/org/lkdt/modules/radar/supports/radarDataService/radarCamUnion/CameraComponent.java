package org.lkdt.modules.radar.supports.radarDataService.radarCamUnion;

import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.radar.entity.ZcLdEquipment;
import org.lkdt.modules.radar.supports.radarDataService.radarCamUnion.DO.CamStatus;
import org.lkdt.modules.radar.supports.radarDataService.radarCamUnion.DO.CameraDO;
import org.lkdt.modules.radar.supports.radarDataService.radarCamUnion.DO.NetDEVSDK;
import org.lkdt.modules.radar.supports.radarDataService.radarCamUnion.DO.NetDEVSDKExt;
import org.lkdt.modules.radar.supports.radarDataService.radarCamUnion.service.RecordCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 录屏组件
 */
@Component
public class CameraComponent {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**摄像机服务*/
    //private CameraService service = new CameraServiceImpl();

    @Value(value = "${radar.eventStorage.savePath}")
    private String savePath;

    @Value(value = "${radar.eventStorage.savePathSnapPic}")
    private String savePathSnapPic;

    /**摄像机集合*/
    private static Map<String, CameraDO> camMap = new HashMap<>();

    /**雷达映射*/
    private static Map<String, String> radarMappingCam = new HashMap<>();

    private static Thread cameraLoginListener = null;

    //录屏
    //private static ExecutorService recordThreadPool = null;
    private Map<String, ThreadPoolExecutor> recordThreadPools = new HashMap<>();

    //private int size = 0;

    //定时任务
    Timer timer = new Timer("triggerReLogin-" + Calendar.getInstance().getTimeInMillis());

    public final void init(List<ZcLdEquipment> zcLdEquipments){

        //recordThreadPool = Executors.newFixedThreadPool(zcLdEquipments.size());
//        recordThreadPool = new ThreadPoolExecutor(zcLdEquipments.size(), zcLdEquipments.size(),
//                0L, TimeUnit.MILLISECONDS,
//                new ArrayBlockingQueue<Runnable>(100));

        boolean bInit = NetDEVSDK.INSTANCE.NETDEV_Init();
        logger.info("初始化相机资源：{}", bInit);

        if(!bInit){
            int initStatus = NetDEVSDK.INSTANCE.NETDEV_GetLastError();
            logger.error("初始化失败错误码：{}", initStatus);
            return;
        }

        boolean bInitExt = NetDEVSDKExt.INSTANCE.NETDEV_Init();
        logger.info("初始化相机资源：{}", bInitExt);

        if(!bInitExt){
            int initStatus = NetDEVSDKExt.INSTANCE.NETDEV_GetLastError();
            logger.error("初始化失败错误码：{}", initStatus);
            return;
        }

        for(ZcLdEquipment zcLdEquipment: zcLdEquipments){
            if(StringUtils.isNotEmpty(zcLdEquipment.getIp()) && zcLdEquipment.getPort() != null) {
                //事件雷达
                if (StringUtils.equals(zcLdEquipment.getEquType(), "001")) {

                    String videoWebArr = zcLdEquipment.getVideoWeb();

                    if(StringUtils.isNotEmpty(videoWebArr)){
                        String[] params = videoWebArr.trim().split("\\|");
                        for(String param: params){
                            if(StringUtils.isNotEmpty(param)){
                                String[] para = param.trim().split(",");
                                if(para.length == 4){

                                    String cameraId = "cam_" + zcLdEquipment.getId() + "_" + para[0] + "_" + Integer.valueOf(para[1]);

                                    radarMappingCam.put(zcLdEquipment.getId(), cameraId);
                                    recordThreadPools.put(cameraId, new ThreadPoolExecutor(2, 2,
                                    0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(10)));

                                    //创建摄像机
                                    CameraDO cameraDO = new CameraDO(bInit, cameraId,
                                            "uniview", para[0], Integer.valueOf(para[1]), para[2], para[3],
                                            true, zcLdEquipment.getId(), new ArrayList<>(), CamStatus.INIT, savePath, savePathSnapPic);
                                    camMap.put(cameraDO.getCameraId(), cameraDO);

//                                    if(cameraDO.isSaveVideo()) {
////                                        cameraDO.login_();
////                                        threadPool.submit(cameraDO);
//                                    }


                                }
                            }
                        }
                    }

                }
            }

        }

        //登录监听
        cameraLoginListener = new Thread(new CameraLoginListener(camMap));
        cameraLoginListener.start();

        //15分钟执行一次
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("触发摄像机重新登录");
                triggerReLogin();
            }
        }, 15*60*1000, 15*60*1000);

    }

    /**
     * 开始实时播放
     * @param radarId
     */
    public static boolean startLiveVideo(String radarId){
        String cameraId = radarMappingCam.get(radarId);
        CameraDO cameraDO = camMap.get(cameraId);
        return cameraDO.createVideoPic();

    }

    /**
     * 取消实时播放
     * @param radarId
     */
    public static void endLiveVideo(String radarId){
        String cameraId = radarMappingCam.get(radarId);
        CameraDO cameraDO = camMap.get(cameraId);
        cameraDO.cancelVideoPic();
    }

    /**
     *
     * @param radarId
     * @return
     */
    public static final String getLiveVideoPic(String radarId){
        try {
            String cameraId = radarMappingCam.get(radarId);
            if(!StringUtils.isEmpty(cameraId) ){
            	CameraDO cameraDO = camMap.get(cameraId);
                return cameraDO.getCurrentBase64();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    /**
//     * TODO:断线重连
//     * @param cameraId
//     */
//    public static final void reInit(String cameraId) {
//        CameraDO cameraDO = camMap.get(cameraId);
//        if(cameraDO.isSaveVideo()) {
//            threadPool.submit(cameraDO);
//        }
//    }

//    /**
//     * 创建摄像机
//     * @param zcLdEquipment
//     */
//    public void createCamera(ZcLdEquipment zcLdEquipment){
//        //运行摄像机
//
//    }

    /**
     * 事件录屏
     */
    public final void record(String radarId, String eventType, RecordCall recordCall){
    	boolean isSaveVideo = false;
        for(Map.Entry<String, CameraDO> m: camMap.entrySet()){
            if(m.getKey().startsWith("cam_" + radarId)){
                CameraDO cameraDO = m.getValue();
                if(cameraDO.isSaveVideo()) {
                	isSaveVideo = true;
                    ThreadPoolExecutor executorService = recordThreadPools.get(cameraDO.getCameraId());

                    if(executorService != null){
                        Runnable record = new EventRunnable(cameraDO, eventType, recordCall);
                        executorService.submit(record);
                    }

                }
            }
        }
        
        if(!isSaveVideo) {
        	if(recordCall != null){
                recordCall.callback(eventType, "ERROR");
            }
        }
        
    }

//    /**
//     * 获取最新的图片数据流
//     * @return
//     */
//    public static byte[] getLastCapPic() {
//
//        return null;
//    }

    public static void triggerReLogin(){

        cameraLoginListener.interrupt();
    }

}



class CameraLoginListener implements Runnable {

    Map<String, CameraDO> cameraDOMap;

    Logger logger = LoggerFactory.getLogger(CameraLoginListener.class);

    long sleepTimes = 15*60*1000;

    public CameraLoginListener(Map<String, CameraDO> cameraDOMap) {
        this.cameraDOMap = cameraDOMap;
    }

    @Override
    public void run() {

        while(true){

            try {

                //初始化成功，登录
                for(Map.Entry<String, CameraDO> m: cameraDOMap.entrySet()){
                    CameraDO cameraDO = m.getValue();
                    cameraDO.login();
                    //cameraDO.loginExt();
                }


                Thread.sleep(sleepTimes);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}

class EventRunnable implements Runnable {

    private String eventType;

    private RecordCall recordCall;

    private CameraDO cameraDO;

    public EventRunnable(CameraDO cameraDO, String eventType, RecordCall recordCall){
        this.cameraDO = cameraDO;
        this.eventType = eventType;
        this.recordCall = recordCall;
    }

    @Override
    public void run() {
        cameraDO.tryEventVideoCache(eventType, recordCall);
    }

}

class CameraRunnablePool {

    public static ThreadPoolExecutor executorService;

    CameraRunnablePool(int coreSize){
        executorService = new ThreadPoolExecutor(coreSize, coreSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());

    }
}
