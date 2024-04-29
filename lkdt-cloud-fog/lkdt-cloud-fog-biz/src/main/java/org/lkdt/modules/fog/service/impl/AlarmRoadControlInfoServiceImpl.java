package org.lkdt.modules.fog.service.impl;


import org.lkdt.modules.fog.entity.AlarmRoadControlInfo;
import org.lkdt.modules.fog.mapper.AlarmRoadControlInfoMapper;
import org.lkdt.modules.fog.service.IAlarmRoadControlInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 路段告警与封路信息关系表
 * @Author: jeecg-boot
 * @Date:   2021-06-03
 * @Version: V1.0
 */
@Service
public class AlarmRoadControlInfoServiceImpl extends ServiceImpl<AlarmRoadControlInfoMapper, AlarmRoadControlInfo> implements IAlarmRoadControlInfoService {
    @Autowired
    private AlarmRoadControlInfoMapper alarmRoadControlInfoMapper;
    @Override
    public List<AlarmRoadControlInfo> queryAlarmRoadControlList(String roadAlarmId) {
        return alarmRoadControlInfoMapper.queryAlarmRoadControlList(roadAlarmId);
    }
}
