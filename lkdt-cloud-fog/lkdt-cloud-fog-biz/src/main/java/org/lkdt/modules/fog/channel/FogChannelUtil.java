package org.lkdt.modules.fog.channel;

/**
 * @author HuangJunYao
 * @date 2021/6/9
 */

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * Bindable interface with one output channel.
 *
 * @author Dave Syer
 * @author Marius Bogoevici
 * @see org.springframework.cloud.stream.annotation.EnableBinding
 */
@Component
public class FogChannelUtil {
    @Autowired
    private StreamBridge streamBridge;
    /**
     * 发送大风告警
     * Name of the output channel.
     */
    private String WINDPUSHOUTPUT = "wind_push_channel";

    /**
     * 发送大风告警
     * @return output channel
     */
    public void windPushSend(JSONObject alarmJSONObject){
        Message<JSONObject> msg = MessageBuilder.withPayload(alarmJSONObject).build();
        streamBridge.send(WINDPUSHOUTPUT,msg);
    }

    /**
     * 验证是否发送通知
     * Name of the output channel.
     */
    private String CHECKSENDNOTICE = "check_send_channel";

    /**
     * 验证是否发送通知
     * @return output channel
     */
    public void checkSendNotice(){
        Message<String> msg = MessageBuilder.withPayload("").build();
        streamBridge.send(CHECKSENDNOTICE,msg);
    }
    /**
     * 发送三方告警
     * Name of the output channel.
     */
    String THREEPARTPUSHOUTPUT = "three_part_channel";

    /**
     * 发送三方告警
     * @return output channel
     */
    public void threePartPushOutPut(JSONObject jsonObject) {
        Message<JSONObject> msg = MessageBuilder.withPayload(jsonObject).build();
        streamBridge.send(THREEPARTPUSHOUTPUT,msg);
    }
}
