package org.lkdt.modules.radar.service;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lkdt.modules.radar.entity.ZcLdWind;
/**
 * 大风配置表
 * @project org.lkdt.modules.radar.service.IZcLdWindService
 * @package org.lkdt.modules.radar.service
 * @className IZcLdWindService
 * @author Cai Xibei
 * @version 1.0.0
 * @createTime 2021/8/17 16:42
 */
public interface IZcLdWindService extends IService<ZcLdWind> {

	public List<ZcLdWind> selectByEpId(String epId);
}
