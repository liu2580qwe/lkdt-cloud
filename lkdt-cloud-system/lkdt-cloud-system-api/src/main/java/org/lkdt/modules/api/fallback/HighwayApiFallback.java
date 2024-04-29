package org.lkdt.modules.api.fallback;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.lkdt.common.api.vo.Result;
import org.lkdt.common.system.vo.HighwayModel;
import org.lkdt.modules.api.HighwayApi;
import org.lkdt.modules.system.entity.Highway;

import java.util.List;

@Slf4j
public class HighwayApiFallback implements HighwayApi {

    @Setter
    private Throwable cause;

    @Override
    public Result<List<String>> queryHwIdsByDeptId(String deptId) {
        log.info("--根据条件查询路段告警数据异常:"+deptId, cause);
        return null;
    }

    @Override
    public List<String> queryChildNodes(String hwId) {
        return null;
    }

    @Override
    public Highway queryHighwaysByHwId(String hwId) {
        return null;
    }

}
