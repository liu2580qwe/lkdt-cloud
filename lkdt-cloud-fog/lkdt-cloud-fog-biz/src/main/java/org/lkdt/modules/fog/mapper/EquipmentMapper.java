package org.lkdt.modules.fog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.lkdt.modules.fog.entity.Equipment;

import java.util.List;
import java.util.Map;

/**
 * @Description: 攝像頭設備表
 * @Author: jeecg-boot
 * @Date: 2021-04-23
 * @Version: V1.0
 */
public interface EquipmentMapper extends BaseMapper<Equipment> {

    int queryEquipmentCount();

    List<Equipment> queryEquipments(Map<String, Object> map);

    List<String> queryCameraIdsByDeptId(String department_id);
    List<Equipment> queryCameraIdsByComputeRoomId(String owned_computer_room);

}
