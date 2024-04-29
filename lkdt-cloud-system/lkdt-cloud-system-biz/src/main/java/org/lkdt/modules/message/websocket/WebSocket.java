package org.lkdt.modules.message.websocket;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lkdt.common.constant.WebsocketConst;
import org.lkdt.common.util.oConvertUtils;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Author scott
 * @Date 2019/11/29 9:41
 * @Description: 此注解相当于设置访问URL
 */
@Component
@Slf4j
@ServerEndpoint("/websocket/{userId}") //此注解相当于设置访问URL
public class WebSocket {
    private static final String WEBSOCKET_QUEUE_NAME="websocket."+ oConvertUtils.getIp();

    private Session session;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    private String userId;


    private static CopyOnWriteArraySet<WebSocket> webSockets = new CopyOnWriteArraySet<>();
    private static Map<String, Session> sessionPool = new HashMap<>();
    private final String WEBSOCKET_RABBIT_EXCHANGE = "websocket_userHost";
    private final String WEBSOCKET_KEY = "websocket_key";


    @OnOpen
    public void onOpen(Session session, @PathParam(value = "userId") String userId) {
        try {
            this.session = session;
            this.userId = userId;
            webSockets.add(this);
            sessionPool.put(userId, session);
            log.debug("【websocket消息】有新的连接，总数为:" + webSockets.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose() {
        try {
            webSockets.remove(this);
            sessionPool.remove(this.userId);
            log.debug("【websocket消息】连接断开，总数为:" + webSockets.size());
        } catch (Exception e) {
        }
    }

    /**
     * 服务器端推送消息
     */
    public void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String message) {
        log.debug("【websocket消息】收到客户端消息:" + message);
        JSONObject obj = new JSONObject();
        obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_CHECK);//业务类型
        obj.put(WebsocketConst.MSG_TXT, "心跳响应");//消息内容
        for (WebSocket webSocketServer : webSockets) {
            webSocketServer.sendMessage(message);
        }
    }

    /**
     * 此为广播消息
     * @param message   消息内容
     */
    public void sendAllMessage(String message) {
        rabbitTemplate.convertAndSend(WEBSOCKET_RABBIT_EXCHANGE, WEBSOCKET_KEY, new UserMessage(null , message));
    }

    /**
     * 此为单点消息
     * @param userId    用户id
     * @param message   消息内容
     */
    public void sendOneMessage(String userId, String message) {
        log.info("用户{} , 消息{}" , userId , message);
        rabbitTemplate.convertAndSend(WEBSOCKET_RABBIT_EXCHANGE, WEBSOCKET_KEY, new UserMessage(userId , message));
    }

    /**
     * 此为单点消息(多人)
     * @param userIds  用户id
     * @param message   消息内容
     */
    public void sendMoreMessage(String[] userIds, String message) {
        Arrays.asList(userIds).forEach(userId -> sendOneMessage(userId, message));
    }

    /**
     * 带用户信息的消息存放对象
     */
    @Data
    @NoArgsConstructor
    public static class UserMessage implements Serializable {
        UserMessage(String userId, String message) {
            this.userId = userId;
            this.message = message;
        }

        String userId;
        String message;
    }
    /**
     * 临时存储当前机器ip信息，作为RabbitMq Queue
     */
    @Bean
    public TemQueueName temQueueName(){
        return new TemQueueName(WEBSOCKET_QUEUE_NAME);
    }
    /**
     * 临时存储当前机器ip信息，作为RabbitMq Queue
     */
    @Data
    public class TemQueueName {
        TemQueueName(String name) {
            this.name = name;
        }
        private String name;
    }
    /**
     * TODO 在消息的接受上面还可以再度进行优化，不需要的消息可以不用发送到该监听中
     * rabbit接收并转发ws消息
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "#{temQueueName.name}"),
            exchange = @Exchange(name = WEBSOCKET_RABBIT_EXCHANGE , type = "fanout"),
            key = WEBSOCKET_KEY)
    )
    @RabbitHandler
    public void receiveUserMessage(@Payload UserMessage message) throws IOException {
        log.debug("receiveUserMessage 接收mq消息:{}", message);
        if (StringUtils.isEmpty(message.getUserId())) {
            //广播消息
            webSockets.forEach(ws -> ws.sendMessage(message.getMessage()));
        } else if (sessionPool.containsKey(message.getUserId())) {
            //指定用户发送消息
            sessionPool.get(message.getUserId()).getBasicRemote().sendText(message.getMessage());
        }
    }
}