package org.lkdt.modules.radar.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lkdt.modules.radar.entity.ZcLdEquipmentVideo;
import org.lkdt.modules.radar.mapper.ZcLdEquipmentVideoMapper;
import org.lkdt.modules.radar.service.IZcLdEquipmentVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 设备摄像头关系表
 * @Author: jeecg-boot
 * @Date:   2021-07-26
 * @Version: V1.0
 */
@Service
public class ZcLdEquipmentVideoServiceImpl extends ServiceImpl<ZcLdEquipmentVideoMapper, ZcLdEquipmentVideo> implements IZcLdEquipmentVideoService {

    @Autowired
    private ZcLdEquipmentVideoMapper zcLdEquipmentVideoMapper;

    @Override
    public List<String> selectSingleId(String ep_id){
        return zcLdEquipmentVideoMapper.selectSingleId(ep_id);
    }

    @Override
    public void deleteByVedioId(String vedio_id){
        zcLdEquipmentVideoMapper.deleteByVedioId(vedio_id);
    }

    @Override
    public List<ZcLdEquipmentVideo> selectByVedioId(String vedio_id){
        return zcLdEquipmentVideoMapper.selectByVedioId(vedio_id);
    }

}
