package org.lkdt.modules.message.websocket;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.constant.WebsocketConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("webSocketApi")
@Api(tags="webSocket测试")
public class TestController {
	
    @Autowired
    private WebSocket webSocket;
 
    @PostMapping("/sendAll/{message}")
	@ApiOperation("sendAll")
    public Result<String> sendAll(@PathVariable(value = "message") String message) {
    	Result<String> result = new Result<String>();
    	JSONObject obj = new JSONObject();
    	obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_TOPIC);
		obj.put(WebsocketConst.MSG_ID, "M0001");
		obj.put(WebsocketConst.MSG_TXT, message);
    	webSocket.sendAllMessage(obj.toJSONString());
        result.setResult("群发！");
        return result;
    }

    @PostMapping("/sendUser/{userId}/{message}")
	@ApiOperation("sendUser")
    public Result<String> sendUser(@PathVariable("userId") String userId ,
								   @PathVariable("message") String message
								   ) {
    	Result<String> result = new Result<String>();
    	JSONObject obj = new JSONObject();
    	obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
    	obj.put(WebsocketConst.MSG_USER_ID, userId);
		obj.put(WebsocketConst.MSG_ID, "M0001");
		obj.put(WebsocketConst.MSG_TXT, message);
        webSocket.sendOneMessage(userId, obj.toJSONString());
        result.setResult("单发");
        return result;
    }
}