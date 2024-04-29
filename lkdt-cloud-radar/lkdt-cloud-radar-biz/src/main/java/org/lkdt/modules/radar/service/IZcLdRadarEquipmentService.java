package org.lkdt.modules.radar.service;
import com.baomidou.mybatisplus.extension.service.IService;
import org.lkdt.modules.radar.entity.ZcLdEquipment;
import java.util.List;
/**
 * @Description: 雷达设备表
 * @Author: jeecg-boot
 * @Date:   2021-07-23
 * @Version: V1.0
 */
public interface IZcLdRadarEquipmentService extends IService<ZcLdEquipment> {

	public List<ZcLdEquipment> selectByMainId(String mainId);
}
