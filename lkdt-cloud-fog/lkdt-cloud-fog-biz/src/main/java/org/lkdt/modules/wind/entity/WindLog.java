package org.lkdt.modules.wind.entity;

import lombok.Data;

import java.util.Date;

@Data
public class WindLog {
    private String windId;
    private String winds;//风速
    private String time;
    private String windd;//计算参数,风向
}
