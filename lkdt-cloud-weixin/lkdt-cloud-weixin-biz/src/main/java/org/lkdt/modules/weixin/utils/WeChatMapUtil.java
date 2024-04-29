package org.lkdt.modules.weixin.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.lkdt.modules.weixin.utils.wx.WxPushUtil;
import org.lkdt.modules.weixin.utils.wx.WxUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Formatter;
import java.util.UUID;

@Component
public class WeChatMapUtil {

    @Autowired
    WxPushUtil wXPushUtil;
    /**
     * 发起http get请求
     */
    public static JSONObject doGet(String requestUrl) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String responseContent = null;
        JSONObject result = null;

        try {
            HttpGet httpGet = new HttpGet(requestUrl);
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            responseContent = EntityUtils.toString(entity, "UTF-8");
            result = JSON.parseObject(responseContent);
        } catch (IOException e) {
            System.out.println("HTTP请求异常：" + e.getMessage());
        }

        return result;
    }

    @Deprecated
    public static String getJsApiTicket(String accessToken) {
        String apiTicketUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
        String requestUrl = apiTicketUrl.replace("ACCESS_TOKEN", accessToken);
        System.out.println("getJsApiTicket.requestUrl ====> " + requestUrl);

        JSONObject result = WeChatMapUtil.doGet(requestUrl);
        System.out.println("getHsApiTicket.response ====> " + result);

        String jsApiTicket = null;
        if (null != result) {
            jsApiTicket = result.getString("ticket");
        }
        return jsApiTicket;
    }

    /**
     * 获取 appId timestamp nonceStr signature
     * @param jsApiTicket
     * @param url
     * @return
     */
    public JSONObject generateWxTicket(String jsApiTicket, String url) {
        JSONObject jsonObject = new JSONObject();
        try {
            String appId = WxUtil.APPID;
            String nonceStr = createNonceStr();
            String timestamp = createTimestamp();
            String string1;
            String signature = "";
            string1 = "jsapi_ticket=" + jsApiTicket + "&noncestr=" + nonceStr + "&timestamp=" + timestamp + "&url=" + url;
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
            jsonObject.put("appId",appId);
            jsonObject.put("timestamp",timestamp);
            jsonObject.put("nonceStr",nonceStr);
            jsonObject.put("signature",signature);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 字节数组转换为十六进制字符串
     *
     * @param hash 字节数组
     * @return 十六进制字符串
     */
    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    /**
     * 生成随机字符串
     *
     * @return 随机字符串
     */
    private static String createNonceStr() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成时间戳
     *
     * @return 时间戳
     */
    private static String createTimestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }
}
