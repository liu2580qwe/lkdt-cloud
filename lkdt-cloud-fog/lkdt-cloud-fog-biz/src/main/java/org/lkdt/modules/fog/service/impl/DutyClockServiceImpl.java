package org.lkdt.modules.fog.service.impl;


import org.lkdt.modules.fog.entity.DutyClock;
import org.lkdt.modules.fog.mapper.DutyClockMapper;
import org.lkdt.modules.fog.service.IDutyClockService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 值班打卡
 * @Author: jeecg-boot
 * @Date:   2021-07-01
 * @Version: V1.0
 */
@Service
public class DutyClockServiceImpl extends ServiceImpl<DutyClockMapper, DutyClock> implements IDutyClockService {
	
	@Autowired
	private DutyClockMapper dutyClockMapper;
	
	@Override
	public List<DutyClock> selectByMainId(String mainId) {
		return dutyClockMapper.selectByMainId(mainId);
	}
}
