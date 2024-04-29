package org.lkdt.modules.fog.service;


import com.baomidou.mybatisplus.extension.service.IService;
import org.lkdt.modules.fog.entity.AlertThreepartRecord;

import java.util.List;

/**
 * @Description: 三方告警信息操作记录
 * @Author: jeecg-boot
 * @Date:   2021-04-27
 * @Version: V1.0
 */
public interface IAlertThreepartRecordService extends IService<AlertThreepartRecord> {

	public List<AlertThreepartRecord> selectByMainId(String mainId);
}
