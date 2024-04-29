package org.lkdt.modules.fog.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.lkdt.common.util.RedisUtil;
import org.lkdt.modules.fog.calculator.FcFactory;
import org.lkdt.modules.fog.calculator.FogCalculator;
import org.lkdt.modules.fog.channel.FogChannelUtil;
import org.lkdt.modules.fog.service.IAlarmNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
@Component
@Slf4j
public class CheckSendNoticeJob{

    private final static String LOCK_KEY = "LOCK_KEY_CHECK_SEND_NOTICE_JOB";

    private final static long TIMEOUT = 100;

    @Autowired
    private IAlarmNoticeService alarmNoticeService;

    @Autowired
    private FcFactory fcFactory;

    @Autowired
    private FogChannelUtil fogChannelUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Value(value = "${external-service.service-path}")
    private String externalpServicePath;

    /**
     * 验证是否发送通知
     */
    @XxlJob("checkSendNotice")
    public void checkSendNotice() {
        try {
                Set<FogCalculator> mapFogCalculator = fcFactory.needConfigByMan(false);
                Set<FogCalculator> allFogCalculator = fcFactory.needConfigByMan(true);
                boolean jiechu = true;
                long nowTime = System.currentTimeMillis();

                if (mapFogCalculator != null && mapFogCalculator.size() > 0) {
                    for (FogCalculator cal : allFogCalculator) {
                        if (cal.getCameraType() != 5) {
                            jiechu = false;
                        }
                    }
                    if (jiechu) {
                        if (nowTime - fcFactory.sendTime > 10 * 60 * 1000) {
//                            wXPushUtil.sendNotice();
                            fogChannelUtil.checkSendNotice();
                            fcFactory.sendTime = nowTime;
                        }
                    } else {
//                        wXPushUtil.sendNotice();
                        fogChannelUtil.checkSendNotice();
                        fcFactory.sendTime = nowTime;
                    }
                }

                log.error("验证是否发送通知");
                FogCalculator calculator = fcFactory.needCall();
                if (calculator != null) {

                    Map<String, Object> paramMap = new HashMap<String, Object>();
                    paramMap.put("CalledShowNumber", "");
                    paramMap.put("calledNumber",alarmNoticeService.queryThePhoneNumberOfTheDutyOfficer());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("destance", calculator.getDistance());
                    //告警模板
                    paramMap.put("ttsCode", "TTS_209837650");
                    Integer[] ints = FogCalculator.parseEquName(calculator.getEquipment().getEquName());
                    jsonObject.put("first", calculator.getEquipment().getHwName() + "，桩号：K" + ints[0]);
                    paramMap.put("ttsParam", jsonObject.toJSONString());

                    try {
                        //打电话接口
                        HttpHeaders headers = new HttpHeaders();
                        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
                        headers.setContentType(type);
                        HttpEntity<String> entity = new HttpEntity<String>(JSON.toJSONString(paramMap), headers);

                        String url = externalpServicePath + "ext/aliyun/sendCallByTts";
                        RestTemplate restTemplate = new RestTemplate();
                        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, entity, String.class);
                        String pResponse = responseEntity.getBody();
                        JSONObject json = JSONObject.parseObject(pResponse);
                        log.info("告警拨打电话结果：" + json);
                    } catch (Exception e) {
                        log.error("告警拨打电话结果", e);
                    }
                }

        } catch (Exception e) {
            log.error("验证是否发送通知失败", e);
        } finally {
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


}
