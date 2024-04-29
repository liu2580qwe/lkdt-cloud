package org.lkdt.modules.traffic.service.impl;

import org.lkdt.modules.traffic.entity.TrafficIncidentRecord;
import org.lkdt.modules.traffic.mapper.TrafficIncidentRecordMapper;
import org.lkdt.modules.traffic.service.ITrafficIncidentRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 道路事件记录
 * @Author: jeecg-boot
 * @Date:   2021-06-03
 * @Version: V1.0
 */
@Service
public class TrafficIncidentRecordServiceImpl extends ServiceImpl<TrafficIncidentRecordMapper, TrafficIncidentRecord> implements ITrafficIncidentRecordService {

    @Resource
    private TrafficIncidentRecordMapper trafficIncidentRecordMapper;

    @Override
    public List<String> getEventIdList() {
        return trafficIncidentRecordMapper.getEventIdList();
    }
}
