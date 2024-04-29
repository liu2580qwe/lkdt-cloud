package org.lkdt.modules.fog.service;


import com.baomidou.mybatisplus.extension.service.IService;
import org.lkdt.modules.fog.entity.AlarmRoadControlInfo;

import java.util.List;

/**
 * @Description: 路段告警与封路信息关系表
 * @Author: jeecg-boot
 * @Date:   2021-06-03
 * @Version: V1.0
 */
public interface IAlarmRoadControlInfoService extends IService<AlarmRoadControlInfo> {
    /**
     * 根据路段告警ID查询告警信息和封路信息
     * @param roadAlarmId
     * @return
     */
    List<AlarmRoadControlInfo> queryAlarmRoadControlList(String roadAlarmId);
}
