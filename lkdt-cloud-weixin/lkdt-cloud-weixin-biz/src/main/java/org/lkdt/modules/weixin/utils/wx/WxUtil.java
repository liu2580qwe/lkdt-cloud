package org.lkdt.modules.weixin.utils.wx;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.lkdt.common.util.Rest;
import org.lkdt.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.Security;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author HuangJunYao
 * @date 2021/4/27
 */
@Slf4j
@Component
public class WxUtil {
    protected static String MINIGRAM_APPID;

    protected static String MINIGRAM_APPSECRET;

    public static String APPID;

    protected static String APPSECRET;

    public static String TOUSERNAME;

    protected static String TEMPLATEWARNID;

    protected static String TEMPLATECLOSEDROADID;

    protected static String TEMPLATEWINDID;

    protected static String TEMPLATEABNID;

    protected static String ALARMPOLICETEMPLATEID;

    protected static String POLICENOTICETEMPLATEID;

    protected static String RELIEVEPOLICENOTICETEMPLATEID;

    @Value("${wx.minigram-appid}")
    protected void setMINIGRAM_APPID(String mINIGRAM_APPID) {
        MINIGRAM_APPID = mINIGRAM_APPID;
    }

    @Value("${wx.minigram-appsecret}")
    protected void setMINIGRAM_APPSECRET(String mINIGRAM_APPSECRET) {
        MINIGRAM_APPSECRET = mINIGRAM_APPSECRET;
    }

    @Value("${wx.appid}")
    protected void setAPPID(String aPPID) {
        APPID = aPPID;
    }

    @Value("${wx.appsecret}")
    protected void setAPPSECRET(String aPPSECRET) {
        APPSECRET = aPPSECRET;
    }

    @Value("${wx.tousername}")
    protected void setTOUSERNAME(String tOUSERNAME) {
        TOUSERNAME = tOUSERNAME;
    }

    @Value("${wx.templatewarnid}")
    protected void setTemplateWarnId(String templatewarnid) {
        TEMPLATEWARNID = templatewarnid;
    }

    @Value("${wx.templateclosedroadid}")
    protected void setTemplateclosedroadid(String templateclosedroadid) {
        TEMPLATECLOSEDROADID = templateclosedroadid;
    }

    @Value("${wx.alarmpolicetemplateid}")
    protected void setAlarmPoliceTemplateId(String alarmpolicetemplateid) {
        ALARMPOLICETEMPLATEID = alarmpolicetemplateid;
    }

    @Value("${wx.templatewindid}")
    protected void setTemplateWindId(String templatewindid) {
        TEMPLATEWINDID = templatewindid;
    }

    @Value("${wx.templateabnid}")
    protected void setTemplateAbnId(String templateabnid) {
        TEMPLATEABNID = templateabnid;
    }

    @Value("${wx.policenoticetemplateid}")
    protected void setPoliceNoticeTemplateId(String policenoticetemplateid) {
        POLICENOTICETEMPLATEID = policenoticetemplateid;
    }

    @Value("${wx.relievepolicenoticetemplateid}")
    protected void setRelievePoliceNoticeTemplateId(String relievepolicenoticetemplateid) {
        RELIEVEPOLICENOTICETEMPLATEID = relievepolicenoticetemplateid;
    }

    // 获取用户信息
    protected final static String getWxUserUrl = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
    // 获取用户列表
    protected final static String getWxUsersUrl = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN&next_openid=NEXT_OPENID";
    // 新建标签
    protected final static String createTagUrl = "https://api.weixin.qq.com/cgi-bin/tags/create?access_token=ACCESS_TOKEN";
    // 获取标签列表
    protected final static String getTagsUrl = "https://api.weixin.qq.com/cgi-bin/tags/get?access_token=ACCESS_TOKEN";
    // 批量取消标签
    protected final static String batchuntagUrl = "https://api.weixin.qq.com/cgi-bin/tags/members/batchuntagging?access_token=ACCESS_TOKEN";
    // 批量添加标签
    protected final static String batchtagUrl = "https://api.weixin.qq.com/cgi-bin/tags/members/batchtagging?access_token=ACCESS_TOKEN";

    public static final String LEVEL_CONTROL_HOME = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx6891d49ec118a9f3&redirect_uri=https%3a%2f%2fainjdjc.jchc.cn%2fwx%2fwxUser%2flevelControlhome&response_type=code&scope=snsapi_base&state=222#wechat_redirect";

    public static final String MAP_HOME = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx6891d49ec118a9f3&redirect_uri=https%3a%2f%2fainjdjc.jchc.cn%2fwx%2fmap%2fwxMap&response_type=code&scope=snsapi_base&state=222#wechat_redirect";


    /**
     * 获取小程序二维码
     *
     * @return
     */
    public static String getMiniGramCode() {
        ByteArrayOutputStream baos = null;
        try {
            String result = Rest.Get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential" + "&appid="
                    + WxUtil.MINIGRAM_APPID + "&secret=" + WxUtil.MINIGRAM_APPSECRET);
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (!StringUtils.isEmpty(jsonObject.getString("access_token"))) {
                URL url = new URL("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token="
                        + jsonObject.getString("access_token"));
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");// 提交模式
                // 发送POST请求必须设置如下两行
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                // 获取URLConnection对象对应的输出流
                PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
                // 发送请求参数
                JSONObject paramJson = new JSONObject();
                paramJson.put("scene", "1");
                paramJson.put("page", "/pages/index/index");
                paramJson.put("width", 430);
                paramJson.put("auto_color", true);
                printWriter.write(paramJson.toString());
                // flush输出流的缓冲
                printWriter.flush();
                // 开始获取数据
                BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());
                baos = new ByteArrayOutputStream();
                int len;
                byte[] arr = new byte[1024];
                while ((len = bis.read(arr)) != -1) {
                    baos.write(arr, 0, len);
                    baos.flush();
                }
                baos.close();

                byte[] bytes = baos.toByteArray();// 转换成字节
                BASE64Encoder encoder = new BASE64Encoder();
                String png_base64 = encoder.encodeBuffer(bytes).trim();// 转换成base64串
                png_base64 = png_base64.replaceAll("\n", "").replaceAll("\r", "");// 删除 \r\n

                return "data:image/jpg;base64," + png_base64;
            }
        } catch (Exception e) {
            log.error("获取小程序二维码异常", e);
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 获取openid
     * https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
     *
     * @param code
     * @return
     */
    public static String getOpenId(String code) {
        // 设置变量 url与返回值其中url使用拼接带入参数APP_ID， APPSECRET
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + WxUtil.APPID + "&secret="
                + WxUtil.APPSECRET + "&code=" + code + "&grant_type=authorization_code";
        String openid = null;
        try {
            // 设置链接
            URL urlGet = new URL(url);
            // 设置外网代理链接
//            InetSocketAddress addr = new InetSocketAddress("192.168.99.100",80);
////            Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
            // 启动链接
            HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();
            // 设置链接参数与要求
            http.setRequestMethod("GET"); // 必须是get方式请求
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);
            System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒？
            System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒？
            // 链接
            http.connect();
            // 获取返回值json字节流
            InputStream is = http.getInputStream();
            int size = is.available();
            byte[] jsonBytes = new byte[size];
            is.read(jsonBytes);
            // 转化成字符串
            String message = new String(jsonBytes, StandardCharsets.UTF_8);
            // 转化成json对象然后返回accessToken属性的值
            JSONObject demoJson = JSONObject.parseObject(message);
            openid = demoJson.getString("openid");
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(" this.getOpenId(code):", e);
        }
        return openid;
    }

    /**
     * 获取openid+session_key
     * https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code
     *
     * @param code
     * @return
     */
    public static JSONObject openIdSessionKey(String code) {
        String result = Rest.Get("https://api.weixin.qq.com/sns/jscode2session?appid=" + WxUtil.MINIGRAM_APPID
                + "&secret=" + WxUtil.MINIGRAM_APPSECRET + "&js_code=" + code + "&grant_type=authorization_code");
        log.error("微信获取用户sessionKey成功" + result);
        try {
            return JSONObject.parseObject(result);
        } catch (Exception e) {
            log.error("解析用户sessionKey异常" + result);
        }
        return new JSONObject();
    }

    /**
     * 小程序获取加密的用户数据
     *
     * @param encryptedData
     * @param sessionKey
     * @param iv
     * @return
     */
    public static JSONObject getUserInfo(String encryptedData, String sessionKey, String iv) {
        // 被加密的数据
        byte[] dataByte = Base64.decodeBase64(encryptedData);
        // 加密秘钥
        byte[] keyByte = Base64.decodeBase64(sessionKey);
        // 偏移量
        byte[] ivByte = Base64.decodeBase64(iv);
        try {
            // 如果密钥不足16位，那么就补足. 这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                String result = new String(resultByte, StandardCharsets.UTF_8);
                return JSONObject.parseObject(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param str 待转换字符串
     * @return 转换后字符串
     * @throws UnsupportedEncodingException exception
     * @Description 将字符串中的emoji表情转换成可以在utf-8字符集数据库中保存的格式（表情占4个字节，需要utf8mb4字符集）
     */
    public static String emojiConvert1(String str) {
        String patternString = "([\\x{10000}-\\x{10ffff}\ud800-\udfff])";

        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            try {
                matcher.appendReplacement(sb, "[[" + URLEncoder.encode(matcher.group(1), "UTF-8") + "]]");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 更新标签
     *
     * @param oldTagId
     * @param newTagId
     * @param openids
     */
    public static boolean updateTag(String oldTagId, String newTagId, String[] openids, String access_token) {
        boolean batchunTag = true, batchTag = true;
        if (StringUtils.isNotEmpty(oldTagId)) {
            batchunTag = batchunTag(oldTagId, openids, access_token);
        }
        if (StringUtils.isNotEmpty(newTagId)) {
            batchTag = batchTag(newTagId, openids, access_token);
        }
        return batchunTag && batchTag;
    }

    /**
     * 删除标签
     */
    protected static boolean batchunTag(String oldTagId, String[] openids, String access_token) {
        JSONObject paramjson = new JSONObject();
        paramjson.put("tagid", oldTagId);
        paramjson.put("openid_list", openids);
        String result = Rest.POST(WxUtil.batchuntagUrl.replace("ACCESS_TOKEN", access_token),
                paramjson.toJSONString());
        JSONObject jsonobj = JSONObject.parseObject(result);
        if ("0".equals(jsonobj.getString("errcode"))) {
            log.error("微信删除标签成功" + result + "----" + Arrays.toString(openids) + "----" + oldTagId);
            return true;
        } else {
            log.error("微信删除标签失败" + result + "----" + Arrays.toString(openids) + "----" + oldTagId);
            return false;
        }
    }

    /**
     * 添加标签
     */
    protected static boolean batchTag(String newTagId, String[] openids, String access_token) {
        JSONObject paramjson = new JSONObject();
        paramjson.put("tagid", newTagId);
        paramjson.put("openid_list", openids);
        String result = Rest.POST(WxUtil.batchtagUrl.replace("ACCESS_TOKEN", access_token),
                paramjson.toJSONString());
        JSONObject jsonobj = JSONObject.parseObject(result);
        if ("0".equals(jsonobj.getString("errcode"))) {
            log.error("微信添加标签成功" + result + "----" + Arrays.toString(openids) + "----" + newTagId);
            return true;
        } else {
            log.error("微信添加标签失败" + result + "----" + Arrays.toString(openids) + "----" + newTagId);
            return false;
        }
    }

    /**
     * 新建标签
     */
    public static void createTag(String tagName, String access_token) {
        JSONObject tagjson = new JSONObject();
        tagjson.put("name", tagName);
        JSONObject paramjson = new JSONObject();
        paramjson.put("tag", tagjson);
        String result = Rest.POST(WxUtil.createTagUrl.replace("ACCESS_TOKEN", access_token),
                paramjson.toJSONString());
        JSONObject jsonobj = JSONObject.parseObject(result);
        if ("0".equals(jsonobj.getString("errcode"))) {
            log.error("微信创建标签成功" + result + "----" + tagName);
        } else {
            log.error("微信创建标签失败" + result + "----" + tagName);
        }
    }
    public static void WxMenu(String access_token) {
        //		JSONObject wqyj = new JSONObject();
//		wqyj.put("name", "危情预警");
//		JSONObject njd = new JSONObject();
//		njd.put("name", "能见度");
//		njd.put("type", "view");
//		njd.put("url", "https://ainjdjc.jchc.cn:8081/pushWX/qxd/njd.html");
//		JSONObject tfwq = new JSONObject();
//		tfwq.put("name", "突发危情");
//		tfwq.put("type", "view");
//		tfwq.put("url", "https://ainjdjc.jchc.cn:8081/pushWX/wxyj/wxyj.html");
//		JSONObject fzjs = new JSONObject();
//		fzjs.put("name", "防撞警示");
//		fzjs.put("type", "view");
//		fzjs.put("url", "https://ainjdjc.jchc.cn:8081/fzjs/index.html");
//		JSONArray wqyjsub = new JSONArray();
//		wqyjsub.add(njd);
//		wqyjsub.add(tfwq);
//		wqyjsub.add(fzjs);
//		wqyj.put("sub_button", wqyjsub);

        //		JSONObject twyj = new JSONObject();
//		twyj.put("name", "团雾预警");
//		JSONObject jxgs = new JSONObject();
//		jxgs.put("name", "济徐高速");
//		jxgs.put("type", "view");
//		jxgs.put("url", "https://ainjdjc.jchc.cn:8081/pushWX/qxd/index2.html");
//		JSONObject dbgs = new JSONObject();
//		dbgs.put("name", "东部高速");
//		dbgs.put("type", "view");
//		dbgs.put("url", "https://ainjdjc.jchc.cn:8081/pushWX/qxd/tuanwu.html");
        JSONObject wxmap = new JSONObject();
        wxmap.put("name", "实时播报");
        wxmap.put("type", "miniprogram");
//		wxmap.put("type", "view");
        wxmap.put("url", MAP_HOME);
        wxmap.put("appid", MINIGRAM_APPID);
        wxmap.put("pagepath", "/pages/index/map/wxMap");
//		JSONArray twyjsub = new JSONArray();
//		twyjsub.add(jxgs);
//		twyjsub.add(dbgs);
//		twyjsub.add(wxmap);
//		twyj.put("sub_button", twyjsub);


        JSONArray butarr = new JSONArray();
//		butarr.add(wqyj);
//		butarr.add(twyj);
        butarr.add(wxmap);


        JSONObject but = new JSONObject();
        JSONObject matchrule = new JSONObject();
        String url = "";
        String result = "";

        //个性化菜单：交警
        JSONObject fjgz = new JSONObject();
        fjgz.put("name", "交管审核");
        fjgz.put("type", "miniprogram");
        fjgz.put("appid", MINIGRAM_APPID);
        fjgz.put("pagepath", "/pages/index/index");
        fjgz.put("url", LEVEL_CONTROL_HOME);


        butarr.add(fjgz);

        matchrule.put("tag_id", "102");

        but.put("button", butarr);
        but.put("matchrule", matchrule);

        url = "https://api.weixin.qq.com/cgi-bin/menu/addconditional?access_token=" + access_token;
        result = Rest.POST(url, but.toJSONString());
        log.error("微信菜单交警" + result + "---" + but.toString());

        //个性化菜单：路政路公司
        JSONObject ycfk = new JSONObject();
        ycfk.put("name", "远程防控");
//		gztz.put("type", "view");
//		gztz.put("url", "https://ainjdjc.jchc.cn/weixin/trafficPolice/levelControlNotice");

        JSONArray ycfksubs = new JSONArray();
        JSONObject ycfk_yccb = new JSONObject();
        ycfk_yccb.put("name", "远程除冰雪");
        ycfk_yccb.put("type", "view");
        ycfk_yccb.put("url", "https://ainjdjc.jchc.cn/weixin/remote/deicing");

        JSONObject ycfk_yddkz = new JSONObject();
        ycfk_yddkz.put("name", "诱导灯控制");
        ycfk_yddkz.put("type", "view");
        ycfk_yddkz.put("url", "https://ainjdjc.jchc.cn/weixin/remote/induction");

        ycfksubs.add(ycfk_yccb);
        ycfksubs.add(ycfk_yddkz);
        ycfk.put("sub_button", ycfksubs);
        butarr.remove(fjgz);
        butarr.add(ycfk);

        matchrule.put("tag_id", "103");

        but.put("button", butarr);
        but.put("matchrule", matchrule);

        url = "https://api.weixin.qq.com/cgi-bin/menu/addconditional?access_token=" + access_token;
        result = Rest.POST(url, but.toJSONString());
        log.error("微信菜单路政路公司" + result + "---" + but.toString());

        //个性化菜单：安通
        butarr.add(fjgz);
        butarr.remove(ycfk);
        butarr.add(ycfk);

        matchrule.put("tag_id", "101");

        but.put("button", butarr);
        but.put("matchrule", matchrule);

        url = "https://api.weixin.qq.com/cgi-bin/menu/addconditional?access_token=" + access_token;
        result = Rest.POST(url, but.toJSONString());
        log.error("微信菜单安通" + result + "---" + but.toString());

        //默认菜单
//		JSONObject tszbs = new JSONObject();
//		tszbs.put("name", "推送值班室");
////		gztz.put("type", "view");
////		gztz.put("url", "https://ainjdjc.jchc.cn/weixin/trafficPolice/levelControlNotice");
//
//		JSONArray tszbssubs = new JSONArray();
//		JSONObject tszbs_gzsb = new JSONObject();
//		tszbs_gzsb.put("name", "管制上报");
//		tszbs_gzsb.put("type", "miniprogram");
//		tszbs_gzsb.put("appid", wXPushUtil.MINIGRAM_APPID);
//		tszbs_gzsb.put("pagepath", "pages/shenbao/guanzhi");
//		tszbs_gzsb.put("url", WxUserController.LEVEL_CONTROL_HOME);
//
//		JSONObject ycfk_gzfk = new JSONObject();
//		ycfk_gzfk.put("name", "管制反馈");
//		ycfk_gzfk.put("type", "miniprogram");
//		ycfk_gzfk.put("appid", wXPushUtil.MINIGRAM_APPID);
//		ycfk_gzfk.put("pagepath", "pages/shenbao/fankui");
//		ycfk_gzfk.put("url", WxUserController.LEVEL_CONTROL_HOME);
//
//		tszbssubs.add(tszbs_gzsb);
//		tszbssubs.add(ycfk_gzfk);
//		tszbs.put("sub_button", tszbssubs);
//		butarr.remove(fjgz);
//		butarr.add(tszbs);
        butarr.remove(ycfk);
        butarr.add(ycfk);
        but.clear();
        but.put("button", butarr);
        url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + access_token;
        result = Rest.POST(url, but.toJSONString());
        log.error("微信菜单" + result + "---" + but.toString());

        //个性化菜单：小程序
//		JSONObject fjgzxcx = new JSONObject();
//		fjgzxcx.put("name", "交管审核");
//		fjgzxcx.put("type", "miniprogram");
//		fjgzxcx.put("url", "https://open.weixin.qq.com/sns/getexpappinfo?appid=wxaededeb771ade34d&path=pages%2Findex%2Findex.html&key=&uin=&scene=4&version=27000c36#wechat-redirect");
//		butarr.clear();
//		butarr.add(fjgzxcx);
//		matchrule.put("tag_id", "104");

//		but.put("button", butarr);
//		but.put("matchrule", matchrule);
//
//		url = "https://api.weixin.qq.com/cgi-bin/menu/addconditional?access_token=" + wXPushUtil.getAccess_token();
//		result = Rest.POST(url, but.toJSONString());
//		logger.error("微信菜单安通" + result + "---" + but.toString());
    }

    public static void oauth2(String code){
        /**
         * 第三步：通过code换取网页授权access_token
         */
        // 同意授权
        if (code != null) {
            // 拼接请求地址
            String url = "https://api.weixin.qq.com/sns/oauth2/access_token?" + "appid=" + APPID + "&secret="
                    + APPSECRET + "&code=" + code + "&grant_type=authorization_code";
//	            JSONObject json =ReadUrlUtil.readJsonFromUrl(url, "");// 拿去返回值
//	            System.out.println("返回信息:"+json);
//	            AutoWebParams autoWebParams = (AutoWebParams) JSONObject.toBean(json, AutoWebParams.class);
            /**
             * 第四步：拉取用户信息(需scope为 snsapi_userinfo)001MeAlp01IRjp1LlKkp0zPLlp0MeAl-
             */
            String url3 = "https://api.weixin.qq.com/sns/userinfo?access_token="
//	                    + autoWebParams.getAccess_token()
                    + "&openid=";
//	                    + autoWebParams.getOpenid() + "&lang=zh_CN";
//	            JSONObject json1 =ReadUrlUtil.readJsonFromUrl(url3, "");// 拿去返回值
//	            System.out.println("用户信息:"+json1);
        }
    }


}
