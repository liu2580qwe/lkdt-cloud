package org.lkdt.modules.system.vo;

import lombok.Data;
import org.lkdt.modules.system.entity.Highway;

import java.util.List;

@Data
public class HighWayDepartVO {
    private Highway zcHighway;
    private List<String> deptIds;
}
