package org.lkdt.modules.radar.service;

import org.lkdt.modules.radar.entity.ZcLdLaneInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 雷达车道与道路车道关系表
 * @Author: jeecg-boot
 * @Date:   2021-03-11
 * @Version: V1.0
 */
public interface IZcLdLaneInfoService extends IService<ZcLdLaneInfo> {

	public List<ZcLdLaneInfo> selectByMainId(String mainId);
	
	public List<ZcLdLaneInfo> queryByMainId(String mainId);
}
