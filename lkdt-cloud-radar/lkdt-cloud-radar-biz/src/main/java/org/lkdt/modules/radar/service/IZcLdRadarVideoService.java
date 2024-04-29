package org.lkdt.modules.radar.service;
import com.baomidou.mybatisplus.extension.service.IService;
import org.lkdt.modules.radar.entity.ZcLdRadarVideo;
import java.util.List;
/**
 * @Description: 雷达设备摄像头表
 * @Author: jeecg-boot
 * @Date:   2021-07-26
 * @Version: V1.0
 */
public interface IZcLdRadarVideoService extends IService<ZcLdRadarVideo> {

	public List<ZcLdRadarVideo> selectByMainId(String mainId);
	public List<ZcLdRadarVideo> selectByEpId(String epId);
	public String deleteByMainId(String mainId);
	public List<ZcLdRadarVideo> selectByUnitId(String mainId);
}
