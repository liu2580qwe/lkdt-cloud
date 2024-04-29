package org.lkdt.modules.system.util;

import lombok.extern.slf4j.Slf4j;
import org.lkdt.common.util.oConvertUtils;
import org.lkdt.modules.api.SysBaseRemoteApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class DictUtil {
    @Autowired
    private SysBaseRemoteApi sysBaseRemoteApi;
    @Autowired
    public RedisTemplate redisTemplate;

    /**
     * 翻译字典文本
     *
     * @param code
     * @param text
     * @param table
     * @param key
     * @return
     */
    public String translateDictValue(String code, String text, String table, String key) {
        if (oConvertUtils.isEmpty(key)) {
            return null;
        }
        StringBuffer textValue = new StringBuffer();
        String[] keys = key.split(",");
        for (String k : keys) {
            String tmpValue = null;
            log.debug(" 字典 key : " + k);
            if (k.trim().length() == 0) {
                continue; //跳过循环
            }
            //update-begin--Author:scott -- Date:20210531 ----for： !56 优化微服务应用下存在表字段需要字典翻译时加载缓慢问题-----
            if (!StringUtils.isEmpty(table)) {
                log.info("--DictAspect------dicTable=" + table + " ,dicText= " + text + " ,dicCode=" + code);
                String keyString = String.format("sys:cache:dictTable::SimpleKey [%s,%s,%s,%s]", table, text, code, k.trim());
                if (redisTemplate.hasKey(keyString)) {
                    try {
                        tmpValue = oConvertUtils.getString(redisTemplate.opsForValue().get(keyString));
                    } catch (Exception e) {
                        log.warn(e.getMessage());
                    }
                } else {
                    tmpValue = sysBaseRemoteApi.queryTableDictTextByKey(table, text, code, k.trim());
                }
            } else {
                String keyString = String.format("sys:cache:dict::%s:%s", code, k.trim());
                if (redisTemplate.hasKey(keyString)) {
                    try {
                        tmpValue = oConvertUtils.getString(redisTemplate.opsForValue().get(keyString));
                    } catch (Exception e) {
                        log.warn(e.getMessage());
                    }
                } else {
                    tmpValue = sysBaseRemoteApi.queryDictTextByKey(code, k.trim());
                }
            }
            //update-end--Author:scott -- Date:20210531 ----for： !56 优化微服务应用下存在表字段需要字典翻译时加载缓慢问题-----

            if (tmpValue != null) {
                if (!"".equals(textValue.toString())) {
                    textValue.append(",");
                }
                textValue.append(tmpValue);
            }

        }
        return textValue.toString();
    }
}
