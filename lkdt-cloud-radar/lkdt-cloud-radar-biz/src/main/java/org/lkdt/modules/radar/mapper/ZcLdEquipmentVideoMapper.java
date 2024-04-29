package org.lkdt.modules.radar.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.lkdt.modules.radar.entity.ZcLdEquipmentVideo;

import java.util.List;

/**
 * @Description: 设备摄像头关系表
 * @Author: jeecg-boot
 * @Date:   2021-07-26
 * @Version: V1.0
 */
public interface ZcLdEquipmentVideoMapper extends BaseMapper<ZcLdEquipmentVideo> {
    public List<String> selectSingleId(String ep_id);
    public void deleteByVedioId(String vedio_id);
    public List<ZcLdEquipmentVideo> selectByVedioId(String vedio_id);
}
