package org.lkdt.modules.api;

import org.lkdt.common.api.vo.Result;
import org.lkdt.common.constant.ServiceNameConstants;
import org.lkdt.common.system.vo.HighwayModel;
import org.lkdt.common.system.vo.LoginUser;
import org.lkdt.modules.api.factory.SysBaseRemoteApiFallbackFactory;
import org.lkdt.modules.system.entity.Highway;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author HuangJunYao
 * @date 2021/4/29
 */
@Component
@FeignClient(contextId = "highwayApi", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = SysBaseRemoteApiFallbackFactory.class)
public interface HighwayApi {
    /**
     * 通过组织ID查询路段
     *
     * @param deptId
     * @return
     */
    @GetMapping("/sys/zcHighwayDepart/queryHwIdsByDeptId")
    Result<List<String>> queryHwIdsByDeptId(@RequestParam(name = "deptId") String deptId);

    @GetMapping("/sys/zcHighway/queryChildNodesByHwId")
    public List<String> queryChildNodes(@RequestParam String hwId);

    @GetMapping("/sys/zcHighway/queryHighwaysByHwId")
    public Highway queryHighwaysByHwId(@RequestParam String hwId);
}
