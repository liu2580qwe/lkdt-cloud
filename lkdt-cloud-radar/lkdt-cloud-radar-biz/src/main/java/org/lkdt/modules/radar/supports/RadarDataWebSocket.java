package org.lkdt.modules.radar.supports;

import org.lkdt.modules.radar.supports.radarDataHandle.Producer;
import org.lkdt.modules.radar.supports.radarTools.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 雷达实时数据
 * @author wy
 *
 */
@Component
@ServerEndpoint(value = "/radarDataWebSocket/{tokenId}/{equId}")
public class RadarDataWebSocket extends RadarDataBase{

    @Autowired
    BeanUtil beanUtil;
	
    @Autowired
    Producer producer;

    Producer getProducer(){
        if(producer == null){
            producer = beanUtil.getBean(Producer.class);
        }
        return producer;
    }

	private static final Logger logger = LoggerFactory.getLogger(RadarDataWebSocket.class);

	//静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    //private static AtomicInteger onlineCount = new AtomicInteger(0);

    //concurrent包的线程安全Map，用来存放每个客户端对应的MyWebSocket对象。
    //public static Map<String, VTRealStatusWebSocket> webSocketMap = new ConcurrentHashMap<>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    protected Session session;

    protected String tokenId;

    protected long updateTime = 0L;
    
//    private long timeOut;

    protected String equId;

//    @Autowired
    //private RadarEventDataGrab radarEventDataHandler;

    @Override
    protected void init(){
        init(this);
    }

    /**
     * 连接建立成功调用的方法
     *
     * @param session 可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    @OnOpen
    public void onOpen(Session session) {
    	//radarEventDataHandler = new RadarEventDataGrab();
        this.session = session;

        Map<String,String> pathParameters = session.getPathParameters();
        this.tokenId = pathParameters.get("tokenId");
//        this.timeOut  = Long.parseLong(pathParameters.get("timeOut"));
        this.equId = pathParameters.get("equId");

        webSocketSet.add(this);
        //webSocketMap.put(this.classesId + "" + this.tokenId, this);

        logger.info("有新连接加入！当前在线人数为" + webSocketSet.size());
        
        /**给客户端发送消息*/
        //send();

    }
    
    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        //停止消息发送
        //this.msgThreadBool = false;
        webSocketSet.remove(this);
        //webSocketMap.remove(this.classesId + "" + this.tokenId);
        logger.info("有一连接关闭！当前在线人数为" + webSocketSet.size());
        super.close(this);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        //logger.info("来自客户端的消息:" + message);
        //刷新时间
        this.updateTime = Calendar.getInstance().getTimeInMillis();
        //群发消息
        /* for(MyWebSocket item: webSocketSet){
        try {
        item.sendMessage(message);
        } catch (IOException e) {
        e.printStackTrace();
        continue;
        }
        }*/
    }

    /**
     * 发生错误时调用
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        logger.info("发生错误");
        webSocketSet.remove(this);
        error.printStackTrace();
    }

    private static ReentrantLock lock = new ReentrantLock(true);

    /**
     * 该方法是我们根据业务需要调用的.
     *
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        synchronized(this.session) {
            if (session.isOpen()) {
                this.session.getAsyncRemote().sendText(message);
            }
        }
    }




//    /**
//     * 获取实时数据
//     */
//    private void getLiveData(){
//        getProducer().radarEventDataGrabs.get(0).getLiveQueue();
////        getProducer().radarEventDataGrabs.get(0).getLiveTJQueue();
//        getProducer().radarFlowDataGrabs.get(0).getLiveQueue();
//    }

   




}
