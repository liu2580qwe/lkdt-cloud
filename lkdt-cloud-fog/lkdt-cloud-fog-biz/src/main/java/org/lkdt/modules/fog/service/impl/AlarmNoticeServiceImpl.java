package org.lkdt.modules.fog.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lkdt.modules.fog.entity.AlarmNoticeModel;
import org.lkdt.modules.fog.mapper.AlarmNoticeMapper;
import org.lkdt.modules.fog.entity.AlarmNotice;
import org.lkdt.modules.fog.service.IAlarmNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Description: zc_alarm_notice
 * @Author: jeecg-boot
 * @Date:   2021-04-28
 * @Version: V1.0
 */
@Service
public class AlarmNoticeServiceImpl extends ServiceImpl<AlarmNoticeMapper, AlarmNotice> implements IAlarmNoticeService {
    @Autowired
    private AlarmNoticeMapper alarmNoticeMapper;

    @Override
    public String queryThePhoneNumberOfTheDutyOfficer(){
        return alarmNoticeMapper.queryThePhoneNumberOfTheDutyOfficer();
    }

    @Override
    public List<AlarmNoticeModel> queryAlermNoticelist(Map<String, Object> map) {
        return alarmNoticeMapper.queryAlermNoticelist(map);
    }
}
