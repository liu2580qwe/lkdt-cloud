package org.lkdt.modules.fog.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lkdt.modules.fog.mapper.AlertThreepartRecordMapper;
import org.lkdt.modules.fog.entity.AlertThreepartRecord;
import org.lkdt.modules.fog.service.IAlertThreepartRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 三方告警信息操作记录
 * @Author: jeecg-boot
 * @Date:   2021-04-27
 * @Version: V1.0
 */
@Service
public class AlertThreepartRecordServiceImpl extends ServiceImpl<AlertThreepartRecordMapper, AlertThreepartRecord> implements IAlertThreepartRecordService {
	
	@Autowired
	private AlertThreepartRecordMapper alertThreepartRecordMapper;
	
	@Override
	public List<AlertThreepartRecord> selectByMainId(String mainId) {
		return alertThreepartRecordMapper.selectByMainId(mainId);
	}
}
