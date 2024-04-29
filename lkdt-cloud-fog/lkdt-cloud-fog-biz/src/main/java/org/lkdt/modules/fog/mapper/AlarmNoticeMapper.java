package org.lkdt.modules.fog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.lkdt.modules.fog.entity.AlarmNotice;
import org.lkdt.modules.fog.entity.AlarmNoticeModel;

import java.util.List;
import java.util.Map;

/**
 * @Description: zc_alarm_notice
 * @Author: jeecg-boot
 * @Date:   2021-04-28
 * @Version: V1.0
 */
public interface AlarmNoticeMapper extends BaseMapper<AlarmNotice> {
    String queryThePhoneNumberOfTheDutyOfficer();

    List<AlarmNoticeModel> queryAlermNoticelist(Map<String, Object> map);

}
