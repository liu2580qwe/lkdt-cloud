package org.lkdt.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.lkdt.modules.system.entity.HighwayDepart;

import java.util.List;

/**
 * @Description: 路段组织关联表
 * @Author: jeecg-boot
 * @Date:   2021-04-28
 * @Version: V1.0
 */
public interface HighwayDepartMapper extends BaseMapper<HighwayDepart> {
    List<String> queryHwIdsByDeptId(String deptId);

    /**
     * 根据道路id查询部门名
     * @param hwId
     * @return
     */
    List<String> selectDeptByHwId(String hwId);
}
