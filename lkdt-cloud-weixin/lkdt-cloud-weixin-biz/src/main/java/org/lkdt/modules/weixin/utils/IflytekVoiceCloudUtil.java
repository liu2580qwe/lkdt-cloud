package org.lkdt.modules.weixin.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;
import okio.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;

/**
 * 测试：
 * APPID = '5e93e12c'
 * API_SECRET = 'ba1f11d2962ed36d91e6231f838b1258'
 * API_KEY = 'ed0cbd012615df1f469a75185c59c424'
 */
public class IflytekVoiceCloudUtil {
    private Logger logger = LoggerFactory.getLogger(IflytekVoiceCloudUtil.class);
    private final String hostUrl = "https://tts-api.xfyun.cn/v2/tts";
    private final String appid = "5e93e12c";
    private final String apiSecret = "ba1f11d2962ed36d91e6231f838b1258";
    private final String apiKey = "ed0cbd012615df1f469a75185c59c424";
    private final String text = "您正行驶在江苏东部高速连运港路段，目前实时能见度150米，依据交通安全管理规范，提醒安全车速限80";
    private final Gson json = new Gson();
    String base64 = "";
    byte[] byteArr = new byte[0];

    public String getVoiceBase64(String text) {
        try{
            // 构建鉴权url
            String authUrl = getAuthUrl(hostUrl, apiKey, apiSecret);
            System.out.println("鉴权url：" + authUrl);
            OkHttpClient client = new OkHttpClient.Builder().build();
            //将url中的 schema http://和https://分别替换为ws:// 和 wss://
            String url = authUrl.toString().replace("http://", "ws://").replace("https://", "wss://");
            System.out.println("鉴权url（wss）：" + url);
            Request request = new Request.Builder().url(url).build();

            CountDownLatch countDownLatch = new CountDownLatch(1);
            WebSocket webSocket = client.newWebSocket(request, new WebSocketListener() {
                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    super.onOpen(webSocket, response);
                    try {
                        System.out.println(response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //发送数据
                    JsonObject frame = new JsonObject();
                    JsonObject business = new JsonObject();
                    JsonObject common = new JsonObject();
                    JsonObject data = new JsonObject();
                    // 填充common
                    common.addProperty("app_id", appid);
                    //填充business
                    business.addProperty("aue", "lame");
                    business.addProperty("sfl", 1);
                    business.addProperty("tte", "UTF8");//小语种必须使用UNICODE编码
                    business.addProperty("ent", "intp65");
                    business.addProperty("vcn", "xiaoyan");//到控制台-我的应用-语音合成-添加试用或购买发音人，添加后即显示该发音人参数值，若试用未添加的发音人会报错11200
                    business.addProperty("pitch", 50);
                    business.addProperty("speed", 50);
                    //填充data
                    data.addProperty("status", 2);//固定位2
                    try {
                        data.addProperty("text", Base64.getEncoder().encodeToString(text.getBytes("utf8")));
                        //使用小语种须使用下面的代码，此处的unicode指的是 utf16小端的编码方式，即"UTF-16LE"”
                        //data.addProperty("text", Base64.getEncoder().encodeToString(text.getBytes("UTF-16LE")));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    //填充frame
                    frame.add("common", common);
                    frame.add("business", business);
                    frame.add("data", data);
                    System.out.println("发送数据：" + frame.toString());
                    webSocket.send(frame.toString());
                }
                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    super.onMessage(webSocket, text);
                    //处理返回数据
                    System.out.println("receive=>" + text);
                    ResponseData resp = null;
                    try {
                        resp = json.fromJson(text, ResponseData.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (resp != null) {
                        if (resp.getCode() != 0) {
                            System.out.println("error=>" + resp.getMessage() + " sid=" + resp.getSid());
                            countDownLatch.countDown();
                            return;
                        }
                        if (resp.getData() != null) {
                            String result = resp.getData().audio;
                            byte[] audio = Base64.getDecoder().decode(result);
                            byteArr = byteMerger(byteArr,audio);//拼接片段
                            if (resp.getData().status == 2) {
                                base64 = Base64.getEncoder().encodeToString(byteArr);//base64结果
                                // resp.data.status ==2 说明数据全部返回完毕，可以关闭连接，释放资源
                                logger.info("IflytekVoiceCloudUtil：session end ");
                                // TTS_ShellUtils.tranPcmToWavFile(f);
                                webSocket.close(1000, "");
                                countDownLatch.countDown();
                            }

                        }
                    }
                }
                @Override
                public void onMessage(WebSocket webSocket, ByteString bytes) {
                    super.onMessage(webSocket, bytes);
                }
                @Override
                public void onClosing(WebSocket webSocket, int code, String reason) {
                    super.onClosing(webSocket, code, reason);
                    logger.info("IflytekVoiceCloudUtil：socket closing");
                }
                @Override
                public void onClosed(WebSocket webSocket, int code, String reason) {
                    super.onClosed(webSocket, code, reason);
                    logger.info("IflytekVoiceCloudUtil：socket closed");
                }
                @Override
                public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                    super.onFailure(webSocket, t, response);
                    logger.error("IflytekVoiceCloudUtil：connection failed");
                    countDownLatch.countDown();
                }
            });
            countDownLatch.await();
            return base64;
        } catch (Exception e){
            logger.error("语音合成异常",e);
        }
        return base64;
    }

    /**
     * 合并数组
     * @param bt1
     * @param bt2
     * @return
     */
    private byte[] byteMerger(byte[] bt1, byte[] bt2){
        byte[] bt3 = new byte[bt1.length+bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }

    public void getVoice(String text, HttpServletResponse response) {
        try{
            response.setHeader("Content-type", "text/html; charset=UTF-8");
            response.setContentType("text/html;charset=UTF-8");
            response.setHeader("Content-Type", "audio/mpeg");
            OutputStream out = response.getOutputStream();
            // 构建鉴权url
            String authUrl = getAuthUrl(hostUrl, apiKey, apiSecret);
            System.out.println("鉴权url：" + authUrl);
            OkHttpClient client = new OkHttpClient.Builder().build();
            //将url中的 schema http://和https://分别替换为ws:// 和 wss://
            String url = authUrl.toString().replace("http://", "ws://").replace("https://", "wss://");
            System.out.println("鉴权url（wss）：" + url);
            Request request = new Request.Builder().url(url).build();

            CountDownLatch countDownLatch = new CountDownLatch(1);
            WebSocket webSocket = client.newWebSocket(request, new WebSocketListener() {
                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    super.onOpen(webSocket, response);
                    try {
                        System.out.println(response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //发送数据
                    JsonObject frame = new JsonObject();
                    JsonObject business = new JsonObject();
                    JsonObject common = new JsonObject();
                    JsonObject data = new JsonObject();
                    // 填充common
                    common.addProperty("app_id", appid);
                    //填充business
                    business.addProperty("aue", "lame");
                    business.addProperty("sfl", 1);
                    business.addProperty("tte", "UTF8");//小语种必须使用UNICODE编码
                    business.addProperty("ent", "intp65");
                    business.addProperty("vcn", "xiaoyan");//到控制台-我的应用-语音合成-添加试用或购买发音人，添加后即显示该发音人参数值，若试用未添加的发音人会报错11200
                    business.addProperty("pitch", 50);
                    business.addProperty("speed", 50);
                    //填充data
                    data.addProperty("status", 2);//固定位2
                    try {
                        data.addProperty("text", Base64.getEncoder().encodeToString(text.getBytes("utf8")));
                        //使用小语种须使用下面的代码，此处的unicode指的是 utf16小端的编码方式，即"UTF-16LE"”
                        //data.addProperty("text", Base64.getEncoder().encodeToString(text.getBytes("UTF-16LE")));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    //填充frame
                    frame.add("common", common);
                    frame.add("business", business);
                    frame.add("data", data);
                    System.out.println("发送数据：" + frame.toString());
                    webSocket.send(frame.toString());
                }
                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    super.onMessage(webSocket, text);
                    //处理返回数据
                    System.out.println("receive=>" + text);
                    ResponseData resp = null;
                    try {
                        resp = json.fromJson(text, ResponseData.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (resp != null) {
                        if (resp.getCode() != 0) {
                            System.out.println("error=>" + resp.getMessage() + " sid=" + resp.getSid());
                            return;
                        }
                        if (resp.getData() != null) {
                            String result = resp.getData().audio;
                            byte[] audio = Base64.getDecoder().decode(result);
                            try {
                                out.write(audio);
                                out.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (resp.getData().status == 2) {
                                // resp.data.status ==2 说明数据全部返回完毕，可以关闭连接，释放资源
                                logger.error("IflytekVoiceCloudUtil：session end ");
                                // TTS_ShellUtils.tranPcmToWavFile(f);
                                webSocket.close(1000, "");
                                try {
                                    out.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                @Override
                public void onMessage(WebSocket webSocket, ByteString bytes) {
                    super.onMessage(webSocket, bytes);
                }
                @Override
                public void onClosing(WebSocket webSocket, int code, String reason) {
                    super.onClosing(webSocket, code, reason);
                    logger.error("IflytekVoiceCloudUtil：socket closing");
                }
                @Override
                public void onClosed(WebSocket webSocket, int code, String reason) {
                    super.onClosed(webSocket, code, reason);
                    logger.error("IflytekVoiceCloudUtil：socket closed");
                }
                @Override
                public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                    super.onFailure(webSocket, t, response);
                    logger.error("IflytekVoiceCloudUtil：connection failed");
                }
            });
            countDownLatch.await();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        //new IflytekVoiceCloudUtil().getVoice("您正行驶在江苏东部高速连云港路段，目前实时能见度200米，依据交通安全管理规范，提醒安全车速限120");
        //["您正行驶在江苏东部高速连云港路段，目前实时能见度150米，依据交通安全管理规范，提醒安全车速限80。",
        //								"请绑定路段。",
        //								"已连接设备。"]
        new IflytekVoiceCloudUtil().getVoice("您正行驶在江苏东部高速连云港路段，目前实时能见度150米，依据交通安全管理规范，提醒安全车速限80。");
        new IflytekVoiceCloudUtil().getVoice("请绑定路段。");
        new IflytekVoiceCloudUtil().getVoice("已连接设备。");
//        File file = new File("D://20200415085951739.mp3");
//        FileInputStream inputFile = new FileInputStream(file);
//        byte[] buffer = new byte[(int)file.length()];
//        inputFile.read(buffer);
//        inputFile.close();
//        String base = Base64.getEncoder().encodeToString(buffer);
//        System.out.println(base);
    }

    public void getVoice(String text) {
        try{
            // 构建鉴权url
            String authUrl = getAuthUrl(hostUrl, apiKey, apiSecret);
            System.out.println("鉴权url：" + authUrl);
            OkHttpClient client = new OkHttpClient.Builder().build();
            //将url中的 schema http://和https://分别替换为ws:// 和 wss://
            String url = authUrl.toString().replace("http://", "ws://").replace("https://", "wss://");
            System.out.println("鉴权url（wss）：" + url);
            Request request = new Request.Builder().url(url).build();
            // 存放音频的文件
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String date = sdf.format(new Date());
            //File f = new File("resource/tts/" + date + ".pcm");
            File audioR = new File("D:/" + date + ".mp3");
            if (!audioR.exists()) {
                audioR.createNewFile();
            }
            FileOutputStream os = new FileOutputStream(audioR);
            CountDownLatch countDownLatch = new CountDownLatch(1);
            WebSocket webSocket = client.newWebSocket(request, new WebSocketListener() {
                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    super.onOpen(webSocket, response);
                    try {
                        System.out.println(response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //发送数据
                    JsonObject frame = new JsonObject();
                    JsonObject business = new JsonObject();
                    JsonObject common = new JsonObject();
                    JsonObject data = new JsonObject();
                    // 填充common
                    common.addProperty("app_id", appid);
                    //填充business
                    business.addProperty("aue", "lame");
                    business.addProperty("sfl", 1);
                    business.addProperty("tte", "UTF8");//小语种必须使用UNICODE编码
                    business.addProperty("ent", "intp65");
                    business.addProperty("vcn", "x2_yezi");//到控制台-我的应用-语音合成-添加试用或购买发音人，添加后即显示该发音人参数值，若试用未添加的发音人会报错11200
                    business.addProperty("pitch", 50);
                    business.addProperty("speed", 50);
                    //填充data
                    data.addProperty("status", 2);//固定位2
                    try {
                        data.addProperty("text", Base64.getEncoder().encodeToString(text.getBytes("utf8")));
                        //使用小语种须使用下面的代码，此处的unicode指的是 utf16小端的编码方式，即"UTF-16LE"”
                        //data.addProperty("text", Base64.getEncoder().encodeToString(text.getBytes("UTF-16LE")));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    //填充frame
                    frame.add("common", common);
                    frame.add("business", business);
                    frame.add("data", data);
                    System.out.println("发送数据：" + frame.toString());
                    webSocket.send(frame.toString());
                }
                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    super.onMessage(webSocket, text);
                    //处理返回数据
                    System.out.println("receive=>" + text);
                    ResponseData resp = null;
                    try {
                        resp = json.fromJson(text, ResponseData.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (resp != null) {
                        if (resp.getCode() != 0) {
                            System.out.println("error=>" + resp.getMessage() + " sid=" + resp.getSid());
                            return;
                        }
                        if (resp.getData() != null) {
                            String result = resp.getData().audio;
                            byte[] audio = Base64.getDecoder().decode(result);
                            try {
                                os.write(audio);
                                os.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (resp.getData().status == 2) {
                                // resp.data.status ==2 说明数据全部返回完毕，可以关闭连接，释放资源
                                System.out.println("session end ");
                                System.out.println("合成的音频文件保存在：" + audioR.getPath());
                                // TTS_ShellUtils.tranPcmToWavFile(f);
                                webSocket.close(1000, "");
                                try {
                                    os.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                @Override
                public void onMessage(WebSocket webSocket, ByteString bytes) {
                    super.onMessage(webSocket, bytes);
                }
                @Override
                public void onClosing(WebSocket webSocket, int code, String reason) {
                    super.onClosing(webSocket, code, reason);
                    System.out.println("socket closing");
                }
                @Override
                public void onClosed(WebSocket webSocket, int code, String reason) {
                    super.onClosed(webSocket, code, reason);
                    System.out.println("socket closed");
                }
                @Override
                public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                    super.onFailure(webSocket, t, response);
                    System.out.println("connection failed");
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        StringBuilder builder = new StringBuilder("host: ").append(url.getHost()).append("\n").
                append("date: ").append(date).append("\n").append("GET ").append(url.getPath()).append(" HTTP/1.1");
        Charset charset = Charset.forName("UTF-8");
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(charset), "hmacsha256");
        mac.init(spec);
        byte[] hexDigits = mac.doFinal(builder.toString().getBytes(charset));
        String sha = Base64.getEncoder().encodeToString(hexDigits);
        String authorization = String.format("hmac username=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        HttpUrl httpUrl = HttpUrl.parse("https://" + url.getHost() + url.getPath()).newBuilder().
                addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(charset))).
                addQueryParameter("date", date).addQueryParameter("host", url.getHost()).build();
        return httpUrl.toString();
    }

    private class ResponseData {
        private int code;
        private String message;
        private String sid;
        private Data data;
        public int getCode() {
            return code;
        }
        public String getMessage() {
            return this.message;
        }
        public String getSid() {
            return sid;
        }
        public Data getData() {
            return data;
        }
    }

    private class Data {
        private int status;  //标志音频是否返回结束  status=1，表示后续还有音频返回，status=2表示所有的音频已经返回
        private String audio;  //返回的音频，base64 编码
        private String ced;  // 合成进度
    }
}