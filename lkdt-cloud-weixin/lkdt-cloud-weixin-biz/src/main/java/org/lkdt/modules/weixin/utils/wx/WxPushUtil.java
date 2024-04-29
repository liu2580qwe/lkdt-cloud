package org.lkdt.modules.weixin.utils.wx;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.lkdt.common.util.*;
import org.lkdt.modules.fog.entity.AlertThreepartModel;
import org.lkdt.modules.weixin.entity.TemplateData;
import org.lkdt.modules.weixin.entity.WechatTemplate;
import org.lkdt.modules.weixin.entity.WxSubscribe;
import org.lkdt.modules.weixin.entity.WxUser;
import org.lkdt.modules.weixin.service.IWxSubscribeService;
import org.lkdt.modules.weixin.service.IWxUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Function;

@Slf4j
@Component
public class WxPushUtil {
    @Value("${spring.projectPath}")
    private String projectPath;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    private IWxSubscribeService wxSubscribeService;
    @Autowired
    private IWxUserService wxUserService;
    @Autowired
    private WxUtil wxUtil;

    @PostConstruct
    public void init() {
        getWXToken();
        initWxUsers();
    }

    private void initWxUsers() {
        String result = Rest
                .Get(WxUtil.getWxUsersUrl.replace("ACCESS_TOKEN", this.getAccess_token()).replace("NEXT_OPENID", ""));
        JSONObject jsonobj = JSONObject.parseObject(result);
        JSONObject data = jsonobj.getJSONObject("data");
        if (data != null) {
            JSONArray openids = data.getJSONArray("openid");
            for (int i = 0; i < openids.size(); ++i) {
                String openid = openids.getString(i);
                WxUser wxUser = wxUserService.getById(openid);
                if (wxUser == null) {
                    wxUser = new WxUser();
                    String resultuser = getUserInfo(openid);
//                    resultuser = new String(resultuser.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                    JSONObject userobj = JSONObject.parseObject(resultuser);
//                    wxUser.setNickname(WxUtil.emojiConvert1(userobj.getString("nickname")));
                    wxUser.setNickname(userobj.getString("nickname"));
                    wxUser.setUnionid(userobj.getString("unionid"));
                    wxUser.setOpenid(openid);
                    wxUser.setCreateTime(new Date());
                    wxUser.setPoliceId("123456");// TODO;
                    wxUserService.save(wxUser);
                } else if (StringUtils.isEmpty(wxUser.getUnionid())) {
                    String resultuser = getUserInfo(openid);
//                    resultuser = new String(resultuser.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                    JSONObject userobj = JSONObject.parseObject(resultuser);
                    wxUser.setNickname(WxUtil.emojiConvert1(userobj.getString("nickname")));
                    wxUser.setUnionid(userobj.getString("unionid"));
                    wxUser.setCreateTime(new Date());
                    wxUser.setPoliceId("123456");// TODO;
                    wxUserService.updateById(wxUser);
                }
            }
            log.error("微信初始化用户信息成功" + "----");
        } else {
            log.error("微信初始化用户信息失败" + "----");
        }

    }

    @Bean
    public Function<Flux<Message<String>>, Mono<Void>> checkSendNotice() {
        return flux -> flux.map(message -> {
            sendNotice();
            return message;
        }).then();
    }

    /**
     * send strong wind warning message
     */
    @Bean
    public Function<Flux<Message<JSONObject>>, Mono<Void>> windPushSend() {
        return flux -> flux.map(message -> {
            JSONObject jsonObject = message.getPayload();
            sendWind(jsonObject);
            return message;
        }).then();
    }

    @Bean
    public Function<Flux<Message<JSONObject>>, Mono<Void>> threePartPushOutPut() {
        return flux -> flux.map(message -> {
            JSONObject jsonObject = message.getPayload();
            String epName = jsonObject.getString("epName");
            sendAlarmInfoToPolice(jsonObject, epName);
            return message;
        }).then();
    }

    /**
     * 待确认告警消息列表
     */
    public void sendNotice() {
        try {
            WechatTemplate wechatTemplate = getTemplateWarn(WxUtil.TEMPLATEWARNID);
            Set<Object> openids = getOpenids();
            if (openids == null || openids.size() == 0) {
                JSONObject paramjson = new JSONObject();
                paramjson.put("tagid", "101");
                String resultopenid = Rest.POST(
                        "https://api.weixin.qq.com/cgi-bin/user/tag/get?access_token=" + getAccess_token(),
                        paramjson.toString());
                JSONObject jsonobj = JSONObject.parseObject(resultopenid);
                if (jsonobj.get("data") != null) {
                    setOpenids(jsonobj.getJSONObject("data").getJSONArray("openid").toArray());
                } else {
                    log.error("微信获取粉丝列表失败" + resultopenid);
                }
            }
            for (int i = 0; i < openids.size(); ++i) {
                String openid = (String) openids.toArray()[i];// "o2LBM1ErvMBXFuXNaLAXk50N5loo";
                wechatTemplate.setUrl(projectPath + "/weixin/alarm/confirmList/" + openid);
                wechatTemplate.setTouser(openid);
                String json = JSONObject.toJSON(wechatTemplate).toString();
                boolean result = sendTemplateForUser(json);
                log.error("微信发送通知" + openid + "----" + wechatTemplate.toString() + "-----" + result);
            }
        } catch (Exception e) {
            log.error("微信通知失败" + e);
            e.printStackTrace();
        }
    }

    /**
     * 系统异常信息
     */
    public void sendAbnNotice(String abnSysName) {
        try {
            WechatTemplate wechatTemplate = getTemplateAbn(WxUtil.TEMPLATEABNID, abnSysName);
            String[] abnOpenids = {"o2LBM1AkllRWPBS4Qu2CQam1gtn0", "o2LBM1AZDaEM6eNS-pzFmHvZqUyM",
                    "o2LBM1CHIuSFDhf5Ux_MISM0THo4", "o2LBM1ErvMBXFuXNaLAXk50N5loo", "o2LBM1L2BQCPgF39imrcwis3nj-U",
                    "o2LBM1P-r-5LR7aTAClETBRQ4_lI",};
            // "o2LBM1ErvMBXFuXNaLAXk50N5loo";
            for (String openid : abnOpenids) {
                wechatTemplate.setTouser(openid);
                String json = JSONObject.toJSON(wechatTemplate).toString();
                boolean result = sendTemplateForUser(json);
                log.error("微信发送通知" + openid + "----" + wechatTemplate.toString() + "-----" + result);
            }
        } catch (Exception e) {
            log.error("微信通知失败" + e);
            e.printStackTrace();
        }
    }

    /**
     * Send push
     *
     * @param content
     * @return
     */
    public boolean sendTemplateForUser(String content) {
		/*String wxstr = "{\"touser\": \"" + openid + "\",\"text\":{\"content\":\"" + content
				+ "\"},\"msgtype\":\"text\"}";*/
        String result = Rest
                .POST("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + getAccess_token(), content);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if ("0".equals(jsonObject.getString("errcode"))) {
            return true;
        }
        log.error(jsonObject.toJSONString());
        return false;
    }

    public void sendWind(JSONObject jsonObject) {
        try {
            WechatTemplate wechatTemplate = getTemplateWind(WxUtil.TEMPLATEWINDID,
                    projectPath + "/weixin/alarm/confirmWind?epId=" + jsonObject.getString("epId"), jsonObject);
            List<String> openids = wxSubscribeService.getOpenidsByWindId(jsonObject.getString("epId"));
            for (String openid : openids) {
                wechatTemplate.setTouser(openid);
                String json = JSONObject.toJSON(wechatTemplate).toString();
                boolean result = sendTemplateForUser(json);
                log.error("微信发送通知" + openid + "----" + wechatTemplate.toString() + "-----" + result);
            }
        } catch (Exception e) {
            log.error("微信通知失败" + e);
            e.printStackTrace();
        }
    }

    public WechatTemplate getTemplateWind(String templateid, String url, JSONObject jsonObject) {
        try {
            WechatTemplate wechatTemplate = new WechatTemplate();
            wechatTemplate.setTemplate_id(templateid);
            // Here is the user's OpenId
            /*wechatTemplate.setTouser(openid);*/
            wechatTemplate.setUrl(url);
            Map<String, TemplateData> m = new HashMap<>();
            TemplateData first = new TemplateData();
            first.setColor("#2261ef");
            first.setValue(jsonObject.getString("address") + "处监测到大风，当前风速：" + jsonObject.getString("winds") + "m/s,建议实施大风"
                    + AlarmLevelUtil.getWindDescByDist(jsonObject.getFloat("winds")));
            m.put("first", first);
            TemplateData keyword1 = new TemplateData();
            keyword1.setColor("#2261ef");
            keyword1.setValue("中交信科集团海德科技");
            m.put("keyword1", keyword1);
            TemplateData keyword2 = new TemplateData();
            keyword2.setColor("#2261ef");
            keyword2.setValue("大风");
            m.put("keyword2", keyword2);
            TemplateData keyword3 = new TemplateData();
            keyword3.setColor("#2261ef");
            keyword3.setValue(AlarmLevelUtil.getWindDescByDist(jsonObject.getFloat("winds")));
            m.put("keyword3", keyword3);
            TemplateData keyword4 = new TemplateData();
            keyword4.setColor("#2261ef");
            keyword4.setValue(DateUtils.format(new Date(), "yyyy-MM-dd HH:mm"));
            m.put("keyword4", keyword4);
            TemplateData remark = new TemplateData();
            remark.setColor("#f63333");
            remark.setValue("请做好防范工作");
            m.put("remark", remark);
            wechatTemplate.setData(m);
            return wechatTemplate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取用户信息
     *
     * @param openid
     * @return
     */
    public String getUserInfo(String openid) {
        String result = Rest.Get("https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + getAccess_token() + "&openid="
                + openid + "&lang=zh_CN");
        log.error("微信获取用户信息成功" + result);
        return result;
    }

    public void getWXToken() {

        log.error("微信获取token开始");
        String param = "grant_type=client_credential&appid=" + WxUtil.APPID + "&secret=" + WxUtil.APPSECRET;
        log.error(param);
        String result = Rest.Get("https://api.weixin.qq.com/cgi-bin/token?" + param);
        System.out.println(result);
        JSONObject jsonobj = JSONObject.parseObject(result);
        if (jsonobj.get("access_token") != null) {
            setAccess_token(jsonobj.getString("access_token"));
            log.error("微信获取token成功" + jsonobj.getString("access_token"));
            String jsresult = Rest.Get("https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="
                    + jsonobj.getString("access_token") + "&type=jsapi");
            JSONObject jsobj = JSONObject.parseObject(jsresult);
            if (jsobj.get("ticket") != null) {
                setJsapi_ticket(jsobj.get("ticket").toString());
                log.error("微信获取ticket成功" + jsobj.getString("ticket"));
                /*getAllUser();*/
            } else {
                log.error("微信获取ticket失败" + result);
            }
            /*getAllUser();*/
        } else {
            log.error("微信获取token失败" + result);
        }
    }

    public void getAllUser() {
        log.error("微信获取所有关注人");
        String result = Rest
                .Get("https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN&next_openid=NEXT_OPENID"
                        .replace("ACCESS_TOKEN", getAccess_token()).replace("NEXT_OPENID", ""));
        System.out.println(result);
        JSONObject jsonobj = JSONObject.parseObject(result).getJSONObject("data");
        if (jsonobj.get("openid") != null) {
            JSONArray users = jsonobj.getJSONArray("openid");
            List<WxSubscribe> wxSubscribes = wxSubscribeService.list();
            List<String> openidList = new ArrayList<>();
            for (WxSubscribe wxSubscribe : wxSubscribes) {
                openidList.add(wxSubscribe.getSubscribeHighway());
            }
            for (int i = 0; i < users.size(); i++) {
                String openid = users.getString(i);
                if (!openidList.contains(openid)) {
                    WxSubscribe wxSubscribe = new WxSubscribe();
                    wxSubscribe.setOpenid(openid);
                    wxSubscribe.setTousername(WxUtil.TOUSERNAME);
                    wxSubscribe.setCreateTime(new Date());
                    wxSubscribeService.save(wxSubscribe);
                }
            }
            log.error("微信获取所有关注人成功" + jsonobj.get("openid"));
        } else {
            log.error("微信获取所有关注人失败" + result);
        }
    }

    public WechatTemplate getTemplateWarn(String templateid) {
        try {
            WechatTemplate wechatTemplate = new WechatTemplate();
            wechatTemplate.setTemplate_id(templateid);
            /*wechatTemplate.setTouser(openid);// 此处是用户的OpenId*/
            Map<String, TemplateData> m = new HashMap<>();
            TemplateData first = new TemplateData();
            first.setColor("#2261ef");
            first.setValue("有未确认告警信息超过10分钟未确认或累计数量大于5条，请及时确认。");
            m.put("first", first);
            TemplateData keyword1 = new TemplateData();
            keyword1.setColor("#2261ef");
            /*keyword1.setValue("江苏省东部高速");*/
            keyword1.setValue("");
            m.put("keyword1", keyword1);
            TemplateData keyword2 = new TemplateData();
            keyword2.setColor("#2261ef");
            keyword2.setValue("");
            m.put("keyword2", keyword2);
            TemplateData keyword3 = new TemplateData();
            keyword3.setColor("#2261ef");
            keyword3.setValue("");
            m.put("keyword3", keyword3);
            TemplateData keyword4 = new TemplateData();
            keyword4.setColor("#2261ef");
            keyword4.setValue("");
            m.put("keyword4", keyword4);
            TemplateData keyword5 = new TemplateData();
            keyword5.setColor("#2261ef");
            keyword5.setValue(DateUtils.format(new Date(), "yyyy-MM-dd HH:mm"));
            m.put("keyword5", keyword5);
            TemplateData remark = new TemplateData();
            remark.setColor("#2261ef");
            remark.setValue("有未确认告警信息超过10分钟未确认或累计未确认数量大于5条，请及时确认。");
            m.put("remark", remark);
            wechatTemplate.setData(m);
            return wechatTemplate;
        } catch (Exception e) {
            log.info("异常" + e);
        }
        return null;
    }

    public WechatTemplate getTemplateAbn(String templateid, String abnSysName) {
        try {
            WechatTemplate wechatTemplate = new WechatTemplate();
            wechatTemplate.setTemplate_id(templateid);
            Map<String, TemplateData> m = new HashMap<>();
            TemplateData first = new TemplateData();
            first.setColor("#2261ef");
            first.setValue(abnSysName + "服务器出现异常，已连续10分钟无数据，请注意");
            m.put("first", first);
            TemplateData keyword1 = new TemplateData();
            keyword1.setColor("#2261ef");
            /*keyword1.setValue("江苏省东部高速");*/
            keyword1.setValue("数据异常");
            m.put("keyword1", keyword1);
            TemplateData keyword2 = new TemplateData();
            keyword2.setColor("#2261ef");
            keyword2.setValue(DateUtils.format(new Date(), "yyyy-MM-dd HH:mm"));
            m.put("keyword2", keyword2);
            TemplateData keyword3 = new TemplateData();
            keyword3.setColor("#2261ef");
            keyword3.setValue("智高速");
            m.put("keyword3", keyword3);
            wechatTemplate.setData(m);
            return wechatTemplate;
        } catch (Exception e) {
            log.info("异常" + e);
        }
        return null;
    }

    public String getAccess_token() {
        return String.valueOf(redisUtil.get("access_token"));
    }

    public void setAccess_token(String access_token) {
        redisUtil.set("access_token", access_token);
    }

    public String getJsapi_ticket() {
        return String.valueOf(redisUtil.get("jsapi_ticket"));
    }

    public void setJsapi_ticket(String jsapi_ticket) {
        redisUtil.set("jsapi_ticket", jsapi_ticket);
    }

    public Set<Object> getOpenids() {
        return redisUtil.sGet("openids");
    }

    public void setOpenids(Object[] openids) {
        redisUtil.sSet("openids", openids);
    }

    public void delOpenids() {
        redisUtil.del("openids");
    }

    public void main(String[] args) {
		/*String[] s = { "111", "222" };
		List<String> openids = Arrays.asList(s);
		WXPushTask runner = new WXPushTask(openids, null);
		Thread thread = new Thread(runner);
		thread.start();*/
    }

    /**
     * 给交警发送告警消息
     *
     * @param alertThreepartJson
     */
    public void sendAlarmInfoToPolice(JSONObject alertThreepartJson, String epName) {
        try {
            AlertThreepartModel alertThreepart = alertThreepartJson.toJavaObject(AlertThreepartModel.class);
//			param.put("roles","('101','102','103')");
//			List<String> openids = service.getOpenidsByRole(param);//交警
            List<String> openids = wxSubscribeService.getOpenidsByHwId(alertThreepart.getHwId());// 关注辖区人
            log.error("openids:" + openids);
            for (int i = 0; i < openids.size(); ++i) {
                try {
                    String openid = openids.get(i);
//					WechatTemplate wechatTemplate = getAlarmPoliceTemplate("QFtpOhgUAa6BDQSEHzBWCGdUr69uHXhk4NLIqXXyW9c",
//					projectPath + "/weixin/alertThreepart/confirmDetails?" +
//						"alertThreepartId="+alertThreepartDO.getAlertThreepartId()+"&openid="+openid,alertThreepartDO);
                    WechatTemplate wechatTemplate = getAlarmPoliceTemplate(WxUtil.ALARMPOLICETEMPLATEID,
                            projectPath + "/weixin/alarm/alarmRoadDetails?" + "alertThreepartId="
                                    + alertThreepart.getId() + "&openid=" + openid,
                            alertThreepart, false, epName);
                    wechatTemplate.setTouser(openid);
                    String json = JSONObject.toJSON(wechatTemplate).toString();
                    boolean result = sendTemplateForUser(json);
                    log.error("给交警发送告警消息---微信通知" + openid + "----" + wechatTemplate.toString() + "-----" + result);

                    // {"errcode":0,"errmsg":"ok","msgid":200228332}
                    if (result) {// 成功
                        log.info("给交警发送告警消息---微信通知：OK");
                    } else { // 失败重发
                        sendTemplateForUser(json);
                    }
                } catch (Exception e) {
                    log.error("给交警发送告警消息---异常" + e);
                }
            }
        } catch (Exception e) {
            log.error("给交警发送告警消息---微信通知失败" + e);
            e.printStackTrace();
        }
    }

//	/**
//	 * 巡逻交警给值班室发告警消息
//	 * @param alertThreepartJson
//	 * @return
//	 */
//	public boolean sendAlarmInfoToPolice2(JSONObject alertThreepartJson) {
//		try {
//			AlertThreepart alertThreepart = JSON.parseObject(alertThreepartJson.toJSONString(), AlertThreepart.class);
//			Map<String, Object> param = new HashMap<>();
//			param.put("role","102");	//交警
//			param.put("hwId", alertThreepart.getHwId());	//告警路段
//			List<String> openids = wxSubscribeService.getOpenidsByHwIdAndRole(param);// 辖区内的交警
//			for (String s : openids) {
//				try {
//					String openid = s;
////					WechatTemplate wechatTemplate = getAlarmPoliceTemplate("QFtpOhgUAa6BDQSEHzBWCGdUr69uHXhk4NLIqXXyW9c",
////					projectPath + "/weixin/alertThreepart/confirmDetails?" +
////						"alertThreepartId="+alertThreepartDO.getAlertThreepartId()+"&openid="+openid,alertThreepartDO);
//					WechatTemplate wechatTemplate = getAlarmPoliceTemplate(ALARMPOLICETEMPLATEID,
//							projectPath + "/weixin/alertThreepart/confirmDetails?" + "alertThreepartId="
//									+ alertThreepart.getAlertThreepartId() + "&openid=" + openid,
//							alertThreepart, true);
//					wechatTemplate.setTouser(openid);
//					String json = JSONObject.toJSON(wechatTemplate).toString();
//					boolean result = sendTemplateForUser(json);
//					log.error("给交警发送告警消息---微信通知" + openid + "----" + wechatTemplate.toString() + "-----" + result);
//
//					// {"errcode":0,"errmsg":"ok","msgid":200228332}
//					if (result) {// 成功
//						log.info("给交警发送告警消息---微信通知：OK");
//					} else { // 失败重发
//						sendTemplateForUser(json);
//					}
//				} catch (Exception e) {
//					log.error("给交警发送告警消息---异常" + e);
//				}
//			}
//			return true;
//		} catch (Exception e) {
//			log.error("给交警发送告警消息---微信通知失败" + e);
//			e.printStackTrace();
//			return false;
//		}
//	}


    /**
     * 给交警发送告警消息---模板
     *
     * @param templateid       模板id
     * @param url              链接
     * @param alertThreepartDO 实体消息
     * @return
     */
    private WechatTemplate getAlarmPoliceTemplate(String templateid, String url,
                                                  AlertThreepartModel alertThreepartDO, boolean isPolice, String epName) {
        try {
            String color = "#FF0101";
            String jjsb = "";
            String type = "1";//1:告警 2：消散 3：回升 4：交警上报
            if (isPolice) {
                jjsb = "交警上报：";
                type = "4";
            }
            String firststr = "“" + alertThreepartDO.getName() + "”路段发生"
                    + AlarmLevelUtil.getFogDesc(alertThreepartDO.getMindistanceNow());
            if ("0".equals(alertThreepartDO.getAlarmLevel())) {
                type = "2";
                color = "#1aac19";
                firststr = "“" + alertThreepartDO.getName() + "”路段大雾消散";
            } else if (AlarmLevelUtil.getLevelByDistInt(alertThreepartDO.getMindistanceHis()) > AlarmLevelUtil
                    .getLevelByDistInt(alertThreepartDO.getMindistanceNow())) {
                type = "3";
                firststr = "“" + alertThreepartDO.getName() + "”路段能见度回升";
            }
            log.error("回升" + AlarmLevelUtil.getLevelByDistInt(alertThreepartDO.getMindistanceHis()) + "---"
                    + AlarmLevelUtil.getLevelByDistInt(alertThreepartDO.getMindistanceNow()));
            url += "&type=" + type;
//			String epName = "";
//			try {
//				epName = fcFactory.getCalculator(alertThreepartDO.getEpId()).getEquipment().getEquName();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
            WechatTemplate wechatTemplate = new WechatTemplate();
            Map<String, TemplateData> m = new HashMap<>();
            TemplateData first = new TemplateData();
            wechatTemplate.setUrl(url);
            wechatTemplate.setTemplate_id(templateid);
            first.setColor(color);// 红色
            first.setValue(jjsb + firststr);
            m.put("first", first);
            TemplateData keyword1 = new TemplateData();
            keyword1.setColor(color);// 红色
//			keyword1.setValue("低能见度告警");
            keyword1.setValue(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
            m.put("keyword1", keyword1);
            TemplateData keyword2 = new TemplateData();
            keyword2.setColor(color);// 红色
//			keyword2.setValue(DateUtils.format(new Date()));
            keyword2.setValue(alertThreepartDO.getName() + "【" + epName + "】");
            m.put("keyword2", keyword2);
            TemplateData keyword3 = new TemplateData();
            keyword3.setColor(color);// 红色
//			keyword3.setValue(alertThreepartDO.getName()+"【"+alertThreepartDO.getEpId()+"】");
            if ("0".equals(alertThreepartDO.getAlarmLevel()) && alertThreepartDO.getMindistanceNow() > 200) {
                keyword3.setValue("大于500米");
            } else {
                keyword3.setValue(alertThreepartDO.getMindistanceNow() + "米");
            }

            m.put("keyword3", keyword3);
//			TemplateData keyword4 = new TemplateData();
//			keyword4.setColor(color);//红色
//			keyword4.setValue(alertThreepartDO.getMindistanceNow() + "米");
//			m.put("keyword4", keyword4);
            TemplateData remark = new TemplateData();
            remark.setValue("请点击查看详情");
            remark.setColor(color);
            m.put("remark", remark);
            wechatTemplate.setData(m);
            return wechatTemplate;
        } catch (Exception e) {
            log.info("给交警发送告警消息异常" + e);
        }
        return null;
    }

//	/**
//	 * 交警发送管制通知
//	 */
//	public  WechatTemplate getTemplatePoliceNotice(PoliceNoticeDO policeNotice) {
//		try {
//
//			int distance = Integer.parseInt(policeNotice.getMinDistance());
//			String level = "";
//			String foglevel = "";
//			String remarkData = "";
//			if (distance <= 200) {
//				level = "三";
//				foglevel = "大雾";
//				remarkData = "能见度在100米以上200米以下时，实行三级管制。管制路段临时限速80公里/小时；通行车辆必须开启雾灯、示廓灯和前后位灯，保持车间距不小于80米。";
//			}
//			if (distance <= 100) {
//				level = "二";
//				foglevel = "浓雾";
//				remarkData = "能见度在50米以上100米以下时，实行二级管制。管制路段禁止危险品运输车辆、“三超”车辆及重载大型货车驶入高速公路，管制路段临时限速60公里/小时；通行车辆必须开启雾灯和近光灯、示廓灯、前后位灯，保持车间距不小于50米。";
//			}
//			if (distance <= 50) {
//				level = "一";
//				foglevel = "特浓雾";
//				remarkData = "能见度在30米以上50米以下时，实行一级管制。管制路段禁止危险品运输车辆、“三超”车辆、大型客货车辆和后雾灯不亮的小型车辆驶入高速公路，管制路段临时限速40公里/小时、禁止超车；通行车辆必须开启雾灯和近光灯、示廓灯、前后位灯、危险报警闪光灯，保持车间距不小于30米。";
//			}
//			if (distance <= 30) {
//				level = "特";
//				foglevel = "特大浓雾";
//				remarkData = "能见度不足30米时，实行特级管制。除重要领导特别紧急公务、紧急抢险救护等特殊车辆在警车带道下通行外，管制路段禁止其他各类车辆驶入高速公路，已经驶入高速公路的车辆须开启雾灯、近光灯、示廓灯、前后位灯及危险报警闪光灯，并以不超过20公里/小时的速度就近驶离高速公路或进入服务区休息。";
//			}
//			WechatTemplate wechatTemplate = new WechatTemplate();
//
//			Map<String, TemplateData> m = new HashMap<>();
//			TemplateData first = new TemplateData();
//			first.setColor("#111111");
//			first.setValue("因" + foglevel + "天气影响，" + policeNotice.getHwName() + "实行交通管制。");
//			m.put("first", first);
//			TemplateData keyword1 = new TemplateData();
////			keyword1.setColor("#000000");
//			keyword1.setValue(policeNotice.getHwName());
//			m.put("keyword1", keyword1);
//			TemplateData keyword2 = new TemplateData();
////			keyword2.setColor("#000000");
//			keyword2.setValue("因" + foglevel + "天气影响");
//			m.put("keyword2", keyword2);
//			TemplateData keyword3 = new TemplateData();
////			keyword3.setColor("#000000");
//			keyword3.setValue(DateUtils.format(policeNotice.getControlTime(), "yyyy-MM-dd HH:mm"));
//			m.put("keyword3", keyword3);
//			TemplateData keyword4 = new TemplateData();
////			keyword4.setColor("#000000");
//			keyword4.setValue(level + "级管制");
//			m.put("keyword4", keyword4);
//			TemplateData keyword5 = new TemplateData();
////			keyword5.setColor("#000000");
//			keyword5.setValue(policeNotice.getMinDistance());
//			m.put("keyword5", keyword5);
//			TemplateData remark = new TemplateData();
//			remark.setValue(remarkData);
//			m.put("remark", remark);
//			wechatTemplate.setData(m);
//			return wechatTemplate;
//		} catch (Exception e) {
//			log.info("异常" + e);
//		}
//		return null;
//	}

//	/**
//	 * 交警发送解除管制
//	 *
//	 * @param policeNotice
//	 */
//	public  WechatTemplate getTemplateRelievePoliceNotice(PoliceNoticeDO policeNotice) {
//		try {
//
//			WechatTemplate wechatTemplate = new WechatTemplate();
//
//			Map<String, TemplateData> m = new HashMap<>();
//			TemplateData first = new TemplateData();
//			first.setColor("#111111");
//			first.setValue(policeNotice.getHwName() + "，当前公路交通能见度持续大于200米0.5小时，依据交通安全管理规范，解除交通管制。");
//			m.put("first", first);
//			TemplateData keyword1 = new TemplateData();
////			keyword1.setColor("#000000");
//			keyword1.setValue(policeNotice.getHwName());
//			m.put("keyword1", keyword1);
//			TemplateData keyword2 = new TemplateData();
////			keyword2.setColor("#000000");
//			keyword2.setValue(DateUtils.format(policeNotice.getControlTime(), "yyyy-MM-dd HH:mm"));
//			m.put("keyword2", keyword2);
//			TemplateData keyword3 = new TemplateData();
////			keyword3.setColor("#000000");
//			keyword3.setValue(policeNotice.getMinDistance() + "米");
//			m.put("keyword3", keyword3);
//
//			TemplateData remark = new TemplateData();
//			remark.setValue(policeNotice.getHwName() + "，当前公路交通能见度（道路可视距离）回升至" + policeNotice.getMinDistance()
//					+ "米，能见度持续大于200米0.5小时，依据交通安全管理规范，实行解除管制。");
//
//			m.put("remark", remark);
//			wechatTemplate.setData(m);
//			return wechatTemplate;
//		} catch (Exception e) {
//			log.error("异常" + e);
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	public  String getOpenid_Code(String openid) {
//		return openid_Code.get(openid);
//	}
//	public  void putOpenid_Code(String openid, String code) {
//		openid_Code.put(openid, code);
//	}
//	public  void removeOpenid_Code(String openid) {
//		openid_Code.remove(openid);
//	}
//
}

//class WXPushTask implements Runnable {
//	private List<String> openids;
//	private WechatTemplate wechatTemplate;
//
//	public WXPushTask(List<String> openids, WechatTemplate wechatTemplate) {
//		super();
//		this.openids = openids;
//		this.wechatTemplate = wechatTemplate;
//	}
//
//	public void run() {
//		try {
//
//			int j = 1;
//			int max = 5;
//			while (true) {
//				log.error("每10分钟一次提醒" + "===" + openids + "===");
//				// "o2LBM1ErvMBXFuXNaLAXk50N5loo";
//				for (String openid : openids) {
//
//					log.error(openid + "===========");
//					wechatTemplate.setTouser(openid);
//					String json = JSONObject.toJSON(wechatTemplate).toString();
//					log.error("===" + json);
//					boolean result =  this.sendTemplateForUser(json);
//					log.error("微信通知" + openid + "----" + wechatTemplate.toString() + "-----" + result);
//				}
//				j++;
//				if (j > max) {
//					break;
//				}
//
//				Thread.sleep(10 * 60 * 1000);// 每次间隔10分钟。
//
//			}
//		} catch (Exception e) {
//			log.error(e.toString());
//			e.printStackTrace();
//
//		}
//	}
//
//
//
//
//}
