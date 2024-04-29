package org.lkdt.modules.radar.service;

import org.lkdt.modules.radar.entity.ZcLdEventInfo;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 事件信息表
 * @Author: jeecg-boot
 * @Date:   2021-03-23
 * @Version: V1.0
 */
public interface IZcLdEventInfoService extends IService<ZcLdEventInfo> {

	/**
	 * 事件占比
	 * @param equId
	 * @return
	 */
	List<Map<String,Object>> queryEventRatio(String equId);
}
