package org.lkdt.modules.radar.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@TableName("zc_ld_risk_value_records")
@ApiModel(value="zc_ld_risk_value_records对象", description="风险评估")
public class RiskValues {

    private long id;

    private String radarId;

    private Date dateTime;

    private double d;

    private double e;

    private double c;

    /**风险评估值*/
    private int riskValue;

    /**车道平均速度*/
    private double whole;

    /**小车平均速度*/
    private double wholeXiao;

    /**大车平均速度*/
    private double wholeDa;

    /**小车->大车平均速度差*/
    private double deltV;

    /**小车速度离散度*/
    private double wholeDiscreteValue;

    /**突发事件*/
    private String eventType;

    /**方向*/
    private String direction;

    /**车型*/
    private String carType;

    /**开始车道*/
    private String carStartLane;

    /**结束车道*/
    private String carEndLane;

    public RiskValues() {
        this.riskValue = 0;
        this.whole = 0;
        this.wholeXiao = 0;
        this.wholeDa = 0;
        this.deltV = 0;
        this.wholeDiscreteValue = 0;
        this.direction = null;
    }

    /**
     * 初始化
     */
    public void init() {
        this.riskValue = 0;
        this.whole = 0;
        this.wholeXiao = 0;
        this.wholeDa = 0;
        this.deltV = 0;
        this.wholeDiscreteValue = 0;
        this.direction = null;
    }

}
