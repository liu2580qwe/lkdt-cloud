package org.lkdt.modules.vo;

import lombok.Data;
import org.lkdt.modules.entity.ZcHighway;

import java.util.List;

@Data
public class HighWayDepartVO {
    private ZcHighway zcHighway;
    private List<String> deptIds;
}
