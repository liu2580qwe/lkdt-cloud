package org.lkdt.modules.radar.service;
import com.baomidou.mybatisplus.extension.service.IService;
import org.lkdt.modules.radar.entity.ZcLdEquipmentVideo;

import java.util.List;

/**
 * @Description: 设备摄像头关系表
 * @Author: jeecg-boot
 * @Date:   2021-07-26
 * @Version: V1.0
 */
public interface IZcLdEquipmentVideoService extends IService<ZcLdEquipmentVideo> {
    /**
     * 根据ep_id查询vedio_id
     * @param ep_id
     * @return
     */
    public List<String> selectSingleId(String ep_id);

    /**
     * 根据vedio删除
     * @param vedio_id
     * @return
     */
    public void deleteByVedioId(String vedio_id);

    /**
     * 根据vedio查询
     * @param vedio_id
     * @return
     */
    public List<ZcLdEquipmentVideo> selectByVedioId(String vedio_id);
}
