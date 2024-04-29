package org.lkdt.modules.fog.service.impl;

import org.lkdt.modules.fog.entity.Equipment;
import org.lkdt.modules.fog.mapper.EquipmentMapper;
import org.lkdt.modules.fog.service.IEquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.Map;

/**
 * @Description: 攝像頭設備表
 * @Author: jeecg-boot
 * @Date: 2021-04-23
 * @Version: V1.0
 */
@Service
public class EquipmentServiceImpl extends ServiceImpl<EquipmentMapper, Equipment> implements IEquipmentService {
    @Autowired
    private EquipmentMapper equipmentMapper;

    @Override
    public int queryEquipmentCount() {
        return equipmentMapper.queryEquipmentCount();
    }

    @Override
    public List<Equipment> queryEquipments(Map<String, Object> map) {
        List<Equipment> equlist = equipmentMapper.queryEquipments(map);
        return equlist;
    }

    /**
     * Query camera id according to department id
     * @param deptId
     * @return
     */
    @Override
    public List<String> queryCameraIdsByDeptId(String deptId){
        return equipmentMapper.queryCameraIdsByDeptId(deptId);
    }

    @Override
    public List<Equipment> queryCameraIdsByComputeRoomId(String roomId) {
        return equipmentMapper.queryCameraIdsByComputeRoomId(roomId);
    }
}
