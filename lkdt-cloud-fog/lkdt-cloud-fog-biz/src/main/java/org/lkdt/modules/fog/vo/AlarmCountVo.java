package org.lkdt.modules.fog.vo;
import lombok.Data;
@Data
public class AlarmCountVo {
    /*name*/
    private String name;
    /*设备id*/
    private String epId;
    /*告警等级*/
    private String level;
    /*告警次数*/
    private long count;
    /*特级告警次数*/
    private long speCount;
}
