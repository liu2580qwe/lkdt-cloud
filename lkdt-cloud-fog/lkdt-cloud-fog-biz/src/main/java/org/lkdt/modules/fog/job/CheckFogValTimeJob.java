package org.lkdt.modules.fog.job;

import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.util.RedisUtil;
import org.lkdt.common.util.StringUtils;
import org.lkdt.modules.fog.calculator.FcFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Refer to the old system for more than 10 minutes without data timing tasks
 *
 * @Author Cai Xibei
 */
@Component
@Slf4j
public class CheckFogValTimeJob {

    @Autowired
    private FcFactory fcFactory;
    @Autowired
    private RedisUtil redisUtil;

    private final long min_10 = 10 * 60 * 1000;
    private final long min_30 = 30 * 60 * 1000;
    private final long min_60 = 60 * 60 * 1000;
    private final long min_120 = 120 * 60 * 1000;

    /**
     * Refer to the old system for more than 10 minutes without data timing tasks
     *
     * @param param 预留参数
     * @return null 不返回
     */
    @XxlJob("CheckFogValTimeHandler")
    public Result<?> execute(String param) {
        System.out.println("Task start...");
        Map<String, Long> map = fcFactory.checkFogValTime();
        List<Object> number = redisUtil.lGet("no_phones_number", 0, -1);
//        String phones="18626453870,13814028556,13813985212,15996496542,17625925627,18096493329";
        String phones = StringUtils.join(number.toArray(), ",");
        for (String a : map.keySet()) {
            if (map.get(a) > min_10 && map.get(a) <= min_10 + 1 * 60 * 1000) {
                sendSms(phones, a, "10");
            }
            if (map.get(a) > min_30 && map.get(a) <= min_30 + 1 * 60 * 1000) {
                sendSms(phones, a, "30");
            }
            if (map.get(a) > min_60 && map.get(a) <= min_60 + 1 * 60 * 1000) {
                sendSms(phones, a, "60");
            }
            if (map.get(a) > min_120 && map.get(a) <= min_120 + 1 * 60 * 1000) {
                sendSms(phones, a, "120");
            }
        }
        // 微信通知
        /*wXPushUtil.sendAbnNotice(abnSysName);*/

//        fcFactory.deleteSysTimeByKey(key);
        return null;
    }

    /**
     * Send a message
     *
     * @param phones
     * @param abnSysName
     */
    public void sendSms(String phones, String abnSysName, String time) {
        String content = "";
        try {
            String templateNumber = "SMS_194050489";
            content = "{\"time\":\"" + time + "分钟\",\"abnSysName\":\"" + abnSysName + "\"}";
            SendMessageUtil.sendSms(phones, templateNumber, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
