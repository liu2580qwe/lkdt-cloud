package org.lkdt.modules.radar.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.io.Serializable;

/**
 * (ZcLdRadarFrame)实体类
 *
 * @author makejava
 * @since 2021-04-16 12:00:48
 */
@Data
@TableName("zc_ld_radar_frame")
@ApiModel(value="zc_ld_radar_frame对象", description="雷达数据统计表")
public class ZcLdRadarFrame implements Serializable {
    private static final long serialVersionUID = 906860938151648347L;
    /**
     * 联合主键
     */
    @TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "联合主键")
    private String radarId;
    /**
    * 联合主键
    */
    @TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "联合主键")
    private String frameId;
    /**
     * 联合主键
     */
    @TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "联合主键")
    private String laneDirection;
    /**
    * 平均速度
    */
    @ApiModelProperty(value = "平均速度")
    private double vAvg;
    /**
    * 平均时距
    */
    @ApiModelProperty(value = "平均时距")
    private double avgTimeDistance;
    /**
    * 小型车个数
    */
    @ApiModelProperty(value = "小型车个数")
    private double smallCount;
    /**
    * 中型车个数
    */
    @ApiModelProperty(value = "中型车个数")
    private double mediumCount;
    /**
    * 大型车个数
    */
    @ApiModelProperty(value = "大型车个数")
    private double largeCount;
    /**
     * 小型车占比
     */
    @ApiModelProperty(value = "小型车占比")
    private double smallCountRatio;
    /**
     * 中型车占比
     */
    @ApiModelProperty(value = "中型车占比")
    private double mediumCountRatio;
    /**
     * 大型车占比
     */
    @ApiModelProperty(value = "大型车占比")
    private double largeCountRatio;
    /**
    * 车流密度（辆/km）
    */
    @ApiModelProperty(value = "车流密度（辆/km）")
    private double carFlowDensity;
    /**
    * 车辆速度方差
    */
    @ApiModelProperty(value = "车辆速度方差")
    private double speedDiscrete;
    /**
     * 速度离散度【用速度与平均速度差计算】
     */
    @ApiModelProperty(value = "速度离散度【用速度与平均速度差计算】")
    private double speedDiscreteAvg;
    /**
     * 车道占有率
     */
    @ApiModelProperty(value = "车道占有率")
    private double laneRatio;
    /**
    * 数据时间
    */
    @ApiModelProperty(value = "数据时间")
    private Date dateTime;


}