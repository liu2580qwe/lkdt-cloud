package org.lkdt.modules.radar.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.lkdt.modules.radar.entity.baseEntity.ZcLDBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * (ZcLdRadarFrameTargetAll)实体类
 *
 * @author makejava
 * @since 2021-04-16 12:01:34
 */
@Data
@TableName("zc_ld_radar_frame_target_all")
@ApiModel(value="zc_ld_radar_frame_target_all对象", description="雷达数据帧检测目标表")
public class ZcLdRadarFrameTargetAll implements Serializable, Comparable<ZcLdRadarFrameTargetAll>{
//    private static final long serialVersionUID = 469834231393211222L;
    /**
    * 联合主键
    */
    @TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "联合主键")
    private String carId;
    /**
     * 外键关联雷达ID
     */
    @ApiModelProperty(value = "外键关联雷达ID")
    private String radarId;
    /**
    * 目标ID
    */
    @ApiModelProperty(value = "目标ID")
    private int targetId;
    /**
    * x坐标
    */
    @ApiModelProperty(value = "x坐标")
    private double sX;
    /**
    * y坐标
    */
    @ApiModelProperty(value = "y坐标")
    private double sY;
    /**
    * x速度
    */
    @ApiModelProperty(value = "x速度")
    private double vX;
    /**
    * y速度
    */
    @ApiModelProperty(value = "y速度")
    private double vY;
    /**
    * x加速度
    */
    @ApiModelProperty(value = "x加速度")
    private double aX;
    /**
    * y加速度
    */
    @ApiModelProperty(value = "y加速度")
    private double aY;
    /**
    * 车道号
    */
    @ApiModelProperty(value = "车道号")
    private int laneNum;
    /**
    * 1 小型车，2 大型车，3 超大型车
    */
    @ApiModelProperty(value = "1 小型车，2 大型车，3 超大型车")
    private int carType;
    /**
    * 0 无事件 1 车辆停止 2 车辆反方向行驶 （逆行） 3 车辆超速行驶 5 车辆实线变道101 车辆停止消失 102 车辆反方向行 驶消失 103 车辆超速行驶 消失105 车辆实线变道 消失
    */
    @ApiModelProperty(value = "0 无事件 1 车辆停止 2 车辆反方向行驶 （逆行） 3 车辆超速行驶 5 车辆实线变道101 车辆停止消失 102 车辆反方向行 驶消失 103 车辆超速行驶 消失105 车辆实线变道 消失")
    private int event;
    /**
    * 车辆长度
    */
    @ApiModelProperty(value = "车辆长度")
    private int carLength;
    /**
     * 数据时间
     */
    @ApiModelProperty(value = "数据时间")
    private Date dateTime;

    @ApiModelProperty(value = "纳秒")
    private long nanoSecond;


    /**
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(ZcLdRadarFrameTargetAll o) {
        if(this.sY < o.sY){
            return -1;
        } else if(this.sY == o.sY){
            return 0;
        }
        return 1;
    }
}