package org.lkdt.modules.radar.service.impl;
import org.lkdt.modules.radar.entity.ZcLdRadarVideo;
import org.lkdt.modules.radar.mapper.ZcLdRadarVideoMapper;
import org.lkdt.modules.radar.service.IZcLdRadarVideoService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
/**
 * @Description: 雷达设备摄像头表
 * @Author: jeecg-boot
 * @Date:   2021-07-26
 * @Version: V1.0
 */
@Service
public class ZcLdRadarVideoServiceImpl extends ServiceImpl<ZcLdRadarVideoMapper, ZcLdRadarVideo> implements IZcLdRadarVideoService {
	
	@Autowired
	private ZcLdRadarVideoMapper zcLdRadarVideoMapper;
	
	@Override
	public List<ZcLdRadarVideo> selectByMainId(String mainId) {
		return zcLdRadarVideoMapper.selectByMainId(mainId);
	}
	@Override
	public List<ZcLdRadarVideo> selectByUnitId(String mainId) {
		return zcLdRadarVideoMapper.selectByUnitId(mainId);
	}

	@Override
	public List<ZcLdRadarVideo> selectByEpId(String epId) {
		return zcLdRadarVideoMapper.selectByEpId(epId);
	}

	@Override
	public String deleteByMainId(String mainId) {
		return zcLdRadarVideoMapper.deleteByMainId(mainId);
	}
}
