package org.lkdt.modules.radar.supports;

import com.alibaba.fastjson.JSONObject;
import org.lkdt.modules.radar.supports.radarDataService.DataReader;
import org.lkdt.modules.radar.supports.radarDataService.radarCamUnion.CameraComponent;
import org.lkdt.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class RadarDataBase {

    private static final Logger logger = LoggerFactory.getLogger(RadarDataBase.class);

    public RadarDataBase(){
        init();
        //启动发送消息线程
        send();
    }

    //用户集合
    protected static Set<RadarDataWebSocket> webSocketSet = new CopyOnWriteArraySet<>();

    /**消息发送*/
    protected static volatile Thread msgThread;

    /**消息监听*/
    protected static volatile Thread msgThreadListen;

    private static final long timeOut = 30000;//毫秒

    /**线程状态*/
    protected static volatile boolean msgThreadBool = true;

    //子对象
    protected RadarDataWebSocket $radarDataWebSocket;

    protected abstract void init();

    /**
     * 初始化当前对象
     * @param radarDataWebSocket 当前对象
     */
    protected void init(RadarDataWebSocket radarDataWebSocket) {
        $radarDataWebSocket = radarDataWebSocket;
    }

    //消息发送
    protected void send() {
        if(msgThread == null){
            synchronized (RadarDataWebSocket.class) {
                if(msgThread == null){
                    //消息发送线程
                    msgThread = new Thread(() -> {

                        //消息缓存
                        Map<String, String> msgMap = new HashMap<>();

                        while(msgThreadBool){
                            try{

                                msgMap.clear();

                                for (RadarDataWebSocket radarDataWebSocket : webSocketSet) {
                                    if(radarDataWebSocket != null){
                                        if(StringUtils.isNotEmpty(radarDataWebSocket.equId)){
                                            String message = null;
                                            if(msgThreadBool){

                                                message = msgMap.get(radarDataWebSocket.equId);

                                                if(StringUtils.isEmpty(message)){
                                                    message = getRadarData(radarDataWebSocket.equId);
                                                    msgMap.put(radarDataWebSocket.equId, message);
                                                }

                                                if(StringUtils.isNotEmpty(message)){
                                                    try {
                                                        if(msgThreadBool){
                                                            radarDataWebSocket.session.getBasicRemote().sendText(message);
                                                        }
                                                    } catch (Exception e) {
                                                        //logger.error(e.getMessage(),e);
                                                    }
                                                }


//                                                else {
//                                                    try {
//                                                        if(msgThreadBool){
//                                                            radarDataWebSocket.session.getBasicRemote().sendText("NULL");
//                                                        }
//                                                    } catch (Exception e) {
//                                                        //logger.error(e.getMessage(),e);
//                                                    }
//                                                }

                                            }
                                        }
                                    }
                                }

                                try {
                                    if(webSocketSet.size() == 0){
                                        Thread.sleep(100);
                                    }
                                } catch (InterruptedException e) {
                                    //e.printStackTrace();
                                }
                            } catch (Exception e) {
                                logger.error("websocket集合遍历异常", e);
                            }

                        }
                    });
                    msgThread.setName("onOpen_msgThread_single01");
                    msgThread.start();

//                    //消息监听线程
//                    msgThreadListen = new Thread(() -> {
//
//                        //待删除websocket
//                        List<RadarDataWebSocket> removedRadarDataWebSocket = new ArrayList<>();
//
//                        while(msgThreadBool){
//                            try{
//
//                                removedRadarDataWebSocket.clear();
//
//                                for (RadarDataWebSocket radarDataWebSocket: webSocketSet) {
//                                    if(radarDataWebSocket.updateTime == 0){
//                                        removedRadarDataWebSocket.add(radarDataWebSocket);
//                                    }
//                                    long currMilli = Calendar.getInstance().getTimeInMillis();
//                                    //超时3秒
//                                    if(currMilli - radarDataWebSocket.updateTime >= timeOut){
//                                        removedRadarDataWebSocket.add(radarDataWebSocket);
//                                    }
//                                }
//
//                                //移除失效websocket
//                                for(RadarDataWebSocket r: removedRadarDataWebSocket){
//                                    webSocketSet.remove(r);
//                                }
//                                if(removedRadarDataWebSocket.size() > 0){
//                                    logger.info("移除失效连接！当前在线人数为{}", webSocketSet.size());
//                                }
//                                try {
//                                    Thread.sleep(timeOut);
//                                } catch (InterruptedException e) {
//                                    //e.printStackTrace();
//                                }
//                            } catch (Exception e) {
//                                logger.error("异常", e);
//                            }
//
//                        }
//                    });
//                    msgThreadListen.setName("onOpen_msgThread_listen");
//                    msgThreadListen.start();
                }
            }
        }
    }

    protected void close(RadarDataWebSocket radarDataWebSocket){
        Iterator<RadarDataWebSocket> ite = webSocketSet.iterator();
        boolean isCloseVideoPlay = true;
        if(ite.hasNext()){
            if(StringUtils.equals(ite.next().equId, radarDataWebSocket.equId)){
                isCloseVideoPlay = false;
            }
        }
        //已无用户连接
        if(isCloseVideoPlay){
            //关闭视频播放
            CameraComponent.endLiveVideo(radarDataWebSocket.equId);
        }
    }


    /**
     * 消息发送，有bug停止使用
     * @param equId
     */
    @Deprecated
    protected void send2(String equId) {
        if(msgThread == null){
            synchronized (RadarDataWebSocket.class) {
                if(msgThread == null){
                    msgThread = new Thread(() -> {
                        while(msgThreadBool){
                            String message = null;
                            if(msgThreadBool){
                                message = getRadarData(equId);
                            }
                            if(!StringUtils.isEmpty(message)){
                                Thread.currentThread().interrupt();
                                try {
                                    //无中断标记时，往下执行
                                    if(msgThreadBool){
                                        for (RadarDataWebSocket radarDataWebSocket : webSocketSet) {
                                            radarDataWebSocket.session.getBasicRemote().sendText(message);
                                        }
                                    }
                                } catch (Exception e) {
                                    logger.error(e.getMessage(),e);
                                }
                            }

                            try {
                                if(!Thread.interrupted()){
                                    Thread.sleep(100);
                                }
                            } catch (InterruptedException e) {
                                //e.printStackTrace();
                            }

                        }
                    });
                    msgThread.setName("onOpen_msgThread_" + $radarDataWebSocket.tokenId);
                    msgThread.start();
                }
            }
        }
    }

//    public static String getPolynomialDataEquId = StringUtils.EMPTY;
//    public static List<Object> polynomialDataList = new ArrayList<Object>();

    /**
     * 测试数据
     * @return
     */
    public String getRadarData(String equId){
        try {
            Object o = $radarDataWebSocket.getProducer().getRadarEventDataGrabLiveQueuePoll(equId);

            cn.hutool.json.JSONObject jsonObject = $radarDataWebSocket.getProducer().getRadarDataReader(equId);

            DataReader dataReader = $radarDataWebSocket.getProducer().getDataReader(equId);

            if(o != null) {
//            	if(StringUtils.equals(getPolynomialDataEquId, equId)) {
//                    polynomialDataList.add(o);
//                }
                JSONObject json = new JSONObject();
                json.put("camBase64", CameraComponent.getLiveVideoPic(dataReader.getRadarId()));
                json.put("radarData", o.toString());
                json.put("eventTypes1min", dataReader.twoSpeedThreeHurriedExt.getEventTypesOfN(1));
                json.put("eventTypes5min", dataReader.twoSpeedThreeHurriedExt.getEventTypesOfN(5));
                json.put("eventTypes10min", dataReader.twoSpeedThreeHurriedExt.getEventTypesOfN(10));

            	json.put("countNumLai", jsonObject.getInt("countNumLai"));
            	json.put("countNumQu", jsonObject.getInt("countNumQu"));
            	json.put("avgSpeedLai", jsonObject.getDouble("avgSpeedLai"));
                json.put("avgSpeedQu", jsonObject.getDouble("avgSpeedQu"));
                json.put("carNumLai", jsonObject.getInt("carNumLai"));
                json.put("carNumQu", jsonObject.getInt("carNumQu"));
                json.put("riskValuesLai", jsonObject.getInt("riskValuesLai"));
                json.put("riskValuesQu", jsonObject.getInt("riskValuesQu"));
                json.put("threeStatusLai", jsonObject.getInt("threeStatusLai"));
                json.put("threeStatusQu", jsonObject.getInt("threeStatusQu"));
                json.put("riskLai", jsonObject.getInt("riskLai"));
                json.put("riskQu", jsonObject.getInt("riskQu"));
                
                return json.toString();
            }else {
                return StringUtils.EMPTY;
            }
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }

    }

}
