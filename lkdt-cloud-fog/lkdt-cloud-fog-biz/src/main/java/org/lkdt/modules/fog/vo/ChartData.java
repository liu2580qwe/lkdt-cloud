package org.lkdt.modules.fog.vo;


import lombok.Data;

import java.util.List;

@Data
public class ChartData {
    private String sum;
    private String type;
    private List xAxisData;
    private List seriesData;
}
