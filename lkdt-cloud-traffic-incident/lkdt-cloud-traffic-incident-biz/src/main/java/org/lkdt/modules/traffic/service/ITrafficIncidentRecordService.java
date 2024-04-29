package org.lkdt.modules.traffic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lkdt.modules.traffic.entity.TrafficIncidentRecord;

import java.util.List;

/**
 * @Description: 道路事件记录
 * @Author: jeecg-boot
 * @Date:   2021-06-03
 * @Version: V1.0
 */
public interface ITrafficIncidentRecordService extends IService<TrafficIncidentRecord> {
    List<String> getEventIdList();
}
