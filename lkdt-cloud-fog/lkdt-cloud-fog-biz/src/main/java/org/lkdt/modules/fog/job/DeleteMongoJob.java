package org.lkdt.modules.fog.job;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.lkdt.common.util.DateUtils;
import org.lkdt.common.util.RedisUtil;
import org.lkdt.modules.fog.calculator.FcFactory;
import org.lkdt.modules.fog.calculator.FogCalculator;
import org.lkdt.modules.fog.entity.AlarmLog;
import org.lkdt.modules.fog.entity.AlarmRoad;
import org.lkdt.modules.fog.mongodb.MongoLogTemplate;
import org.lkdt.modules.fog.service.IAlarmRoadService;
import org.lkdt.modules.fog.service.IAlarmService;
import org.lkdt.modules.fog.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component
public class DeleteMongoJob {
    @Autowired
    FcFactory fcFactory;
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    ImageService imageService;

    @Autowired
    IAlarmRoadService alarmRoadService;

    @Autowired
    IAlarmService alarmService;

    @Autowired
    MongoLogTemplate mongoLogTemplate;
    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 删除一年之前未告警mongo数据
     */
    @XxlJob("deleteMongoJob")
    public void execute() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -11);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date dayStart = calendar.getTime();

        //一天的结束时间 yyyy:MM:dd 23:59:59
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date dayEnd = calendar.getTime();
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("beginTime", DateUtils.format(dayStart,DateUtils.DATE_TIME_PATTERN));
        paramsMap.put("endTime", DateUtils.format(dayEnd,DateUtils.DATE_TIME_PATTERN));
//        paramsMap.put("beginTime", "2021-07-15 00:00:00");
//        paramsMap.put("endTime", "2021-07-16 23:59:59");
        List<AlarmRoad> alarmRoadList = alarmRoadService.queryMongoListByDay(paramsMap);
        SimpleDateFormat sdf_ymd = new SimpleDateFormat(mongoLogTemplate.str_ymd);
        Date date = new Date();
        String collectionName = mongoLogTemplate.alarmLog_ + sdf_ymd.format(date);
//        List<List<AlarmLog>> alarmRoads = new ArrayList<>();
        for (AlarmRoad alarmRoad : alarmRoadList) {
            Set<Object> calSet = redisUtil.zSetGetAll(fcFactory.CAMERAS_CACHE_NAME + alarmRoad.getHwId());
            for (Object o : calSet) {
                String epId = (String) o;
                if (alarmRoad.getStarttime()==null || alarmRoad.getEndtime()==null){
                    mongoTemplate.remove(new Query(Criteria
                            .where("epId").is(epId)
                            .and("dateTime").gte(paramsMap.get("beginTime") + ".000").lte(paramsMap.get("endTime") + ".000")
                    ), AlarmLog.class, collectionName);
//                    if (CollectionUtils.isNotEmpty(all) && all.size()>0){
//                        alarmRoads.add(all);
//                    }
                    continue;
                }
                mongoTemplate.remove(new Query(Criteria
                        .where("epId").is(epId)
                        .and("dateTime").gte(paramsMap.get("beginTime") + ".000").lte(DateUtils.format(alarmRoad.getStarttime(), DateUtils.DATE_TIME_PATTERN) + ".000")
                ), AlarmLog.class, collectionName);
                mongoTemplate.remove(new Query(Criteria
                        .where("epId").is(epId)
                        .and("dateTime").gte(DateUtils.format(alarmRoad.getEndtime(), DateUtils.DATE_TIME_PATTERN) + ".000").lte(paramsMap.get("endTime") + ".000")
                ), AlarmLog.class, collectionName);
//                if (CollectionUtils.isNotEmpty(a)){
//                    alarmRoads.add(a);
//                }
//                if (CollectionUtils.isNotEmpty(b)){
//                    alarmRoads.add(b);
//                }
            }
        }
    }


}
