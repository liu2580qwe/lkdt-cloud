package org.lkdt.modules.fog.job;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.lkdt.common.util.DateUtils;
import org.lkdt.modules.fog.calculator.FcFactory;
import org.lkdt.modules.fog.calculator.FogCalculator;
import org.lkdt.modules.fog.entity.AlarmLog;
import org.lkdt.modules.fog.entity.AlarmRoad;
import org.lkdt.modules.fog.mongodb.MongoLogTemplate;
import org.lkdt.modules.fog.service.IAlarmRoadService;
import org.lkdt.modules.fog.service.IAlarmService;
import org.lkdt.modules.fog.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author hjy
 */
@Slf4j
@Component
public class DeleteImgJob {
    @Autowired
    FcFactory fcFactory;

    @Autowired
    ImageService imageService;

    @Autowired
    IAlarmRoadService alarmRoadService;

    @Autowired
    IAlarmService alarmService;

    @Autowired
    MongoLogTemplate mongoLogTemplate;

    /**
     * 删除一年之前未告警图片
     */
    @XxlJob("deleteImgJob")
    public void deleteImgJob() {
        SimpleDateFormat sdfmongo = new SimpleDateFormat(mongoLogTemplate.str_dateTime);
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
        paramsMap.put("beginTime", dayStart);
        paramsMap.put("endTime", dayEnd);
        List<AlarmRoad> alarmRoadList = alarmRoadService.queryListByDay(paramsMap);
        List<AlarmLog> alarmLoglist = alarmService.getFileList(sdfmongo.format(dayStart),
                sdfmongo.format(dayEnd), null);

        Map<String, List<AlarmRoad>> alarmMap = new HashMap();
        for (AlarmRoad alarmRoad : alarmRoadList) {
            List<AlarmRoad> alarmList = alarmMap.get(alarmRoad.getHwId());
            if (CollectionUtils.isEmpty(alarmList)) {
                alarmList = new ArrayList<>();
            }
            alarmList.add(alarmRoad);
            alarmMap.put(alarmRoad.getHwId(), alarmList);
        }

        for (AlarmLog alarmLog : alarmLoglist) {
            //如果已上传就跳过
            String epId = alarmLog.getEpId();
            FogCalculator cal = fcFactory.getCalculator(epId);
            List<AlarmRoad> alarmList = alarmMap.get(cal.getEquipment().getHwId());
            if (CollectionUtils.isNotEmpty(alarmList)) {
                Date alarmLogTime = null;
                try {
                    alarmLogTime = sdfmongo.parse(alarmLog.getDateTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //判断是否告警期间图片，是告警图片跳过
                boolean isAlarm = false;

                for (AlarmRoad alarmRoad : alarmList) {
                    if (alarmRoad != null && alarmLogTime.getTime() > alarmRoad.getStarttime().getTime()
                            && alarmLogTime.getTime() < alarmRoad.getEndtime().getTime()) {
                        isAlarm = true;
                    }
                }

                if (isAlarm) {
                    continue;
                }
            }
            imageService.deleteCamImg(alarmLog.getImgName());
            log.error("定时删除图片" + alarmLog.getImgName());
        }


    }


}
