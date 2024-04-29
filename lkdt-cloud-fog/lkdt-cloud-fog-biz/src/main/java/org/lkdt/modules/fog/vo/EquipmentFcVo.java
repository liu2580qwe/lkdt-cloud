package org.lkdt.modules.fog.vo;

import lombok.Data;
import org.lkdt.common.system.vo.HighwayModel;
import org.lkdt.modules.fog.calculator.FogCalculator;
import org.lkdt.modules.system.entity.Highway;

import java.util.List;

@Data
public class EquipmentFcVo {
    private List<FogCalculator> fogCalculatorList;
    private FogCalculator fogCalculator;
    private HighwayModel highway;
}
