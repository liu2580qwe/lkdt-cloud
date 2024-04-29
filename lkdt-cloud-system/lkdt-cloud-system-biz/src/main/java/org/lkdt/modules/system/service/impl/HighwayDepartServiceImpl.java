package org.lkdt.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lkdt.modules.system.entity.HighwayDepart;
import org.lkdt.modules.system.mapper.HighwayDepartMapper;
import org.lkdt.modules.system.service.IHighwayDepartService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 路段组织关联表
 * @Author: jeecg-boot
 * @Date:   2021-04-28
 * @Version: V1.0
 */
@Service
public class HighwayDepartServiceImpl extends ServiceImpl<HighwayDepartMapper, HighwayDepart> implements IHighwayDepartService {

    @Resource
    private HighwayDepartMapper zcHighwayDepartMapper;
    @Override
    public List<HighwayDepart> findByHwId(String hwId) {
        List<HighwayDepart> zcHighwayDepart=baseMapper.selectList(
                new QueryWrapper<HighwayDepart>().eq("hw_id",hwId));
        return zcHighwayDepart;
    }

    /**
     * 查询部门道路ids
     * @param deptId
     * @return
     */
    @Override
    public List<String> queryHwIdsByDeptId(String deptId) {
        return zcHighwayDepartMapper.queryHwIdsByDeptId(deptId);
    }

    @Override
    public List<String> queryDeptByHwId(String hwId) {
        return zcHighwayDepartMapper.selectDeptByHwId(hwId);
    }
}
