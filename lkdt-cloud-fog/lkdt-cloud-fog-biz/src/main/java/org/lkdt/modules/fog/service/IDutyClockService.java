package org.lkdt.modules.fog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lkdt.modules.fog.entity.DutyClock;

import java.util.List;

/**
 * @Description: 值班打卡
 * @Author: jeecg-boot
 * @Date:   2021-07-01
 * @Version: V1.0
 */
public interface IDutyClockService extends IService<DutyClock> {

	public List<DutyClock> selectByMainId(String mainId);
}
