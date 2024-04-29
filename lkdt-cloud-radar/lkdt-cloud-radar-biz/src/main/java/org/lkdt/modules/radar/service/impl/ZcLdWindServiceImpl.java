package org.lkdt.modules.radar.service.impl;
import org.lkdt.modules.radar.entity.ZcLdWind;
import org.lkdt.modules.radar.mapper.ZcLdWindMapper;
import org.lkdt.modules.radar.service.IZcLdWindService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
/**
 * 大风配置表
 * @project org.lkdt.modules.radar.service.impl.ZcLdWindServiceImpl
 * @package org.lkdt.modules.radar.service.impl
 * @className ZcLdWindServiceImpl
 * @author Cai Xibei
 * @version 1.0.0
 * @createTime 2021/8/17 16:41
 */
@Service
public class ZcLdWindServiceImpl extends ServiceImpl<ZcLdWindMapper, ZcLdWind> implements IZcLdWindService {

	@Autowired
	private ZcLdWindMapper zcLdWindMapper;
	
	@Override
	public List<ZcLdWind> selectByEpId(String epId) {
		return zcLdWindMapper.selectByEpId(epId);
	}

}
