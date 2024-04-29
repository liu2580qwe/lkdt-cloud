package org.lkdt.modules.fog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lkdt.modules.fog.entity.Equipment;

import java.util.List;
import java.util.Map;

/**
 * @Description: 攝像頭設備表
 * @Author: jeecg-boot
 * @Date:   2021-04-23
 * @Version: V1.0
 */
public interface IEquipmentService extends IService<Equipment> {

    int queryEquipmentCount();

    List<Equipment> queryEquipments(Map<String, Object> map);

    List<String> queryCameraIdsByDeptId(String deptId);

    List<Equipment> queryCameraIdsByComputeRoomId(String roomId);
}
