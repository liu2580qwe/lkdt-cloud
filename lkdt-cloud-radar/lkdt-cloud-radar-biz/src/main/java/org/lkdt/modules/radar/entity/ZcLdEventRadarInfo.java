package org.lkdt.modules.radar.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.math.BigDecimal;

import cn.hutool.json.JSONArray;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.lkdt.modules.radar.entity.baseEntity.ZcLDBaseEntity;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.lkdt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 事件雷达数据表
 * @Author: jeecg-boot
 * @Date:   2021-03-23
 * @Version: V1.0
 */
@Data
@TableName("zc_ld_event_radar_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="zc_ld_event_radar_info对象", description="事件雷达数据表")
public class ZcLdEventRadarInfo extends ZcLDBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
	/**雷达设备ID*/
	@Excel(name = "雷达设备ID", width = 15)
    @ApiModelProperty(value = "雷达设备ID")
    private java.lang.String equId;
    /**开始X速度*/
    @Excel(name = "开始X速度", width = 15)
    @ApiModelProperty(value = "开始X速度")
    private java.math.BigDecimal speedStartX;
    /**开始Y速度*/
    @Excel(name = "开始Y速度", width = 15)
    @ApiModelProperty(value = "开始Y速度")
    private java.math.BigDecimal speedStartY;
    /**开始雷达车道号*/
    @Excel(name = "开始雷达车道号", width = 15)
    @ApiModelProperty(value = "开始雷达车道号")
    private java.lang.Integer laneStartRadar;
    /**开始道路车道号*/
    @Excel(name = "开始道路车道号", width = 15)
    @ApiModelProperty(value = "开始道路车道号")
    private java.lang.String laneStartRoad;
	/**结束X速度*/
	@Excel(name = "结束X速度", width = 15)
    @ApiModelProperty(value = "结束X速度")
    private java.math.BigDecimal speedEndX;
	/**结束Y速度*/
	@Excel(name = "结束Y速度", width = 15)
    @ApiModelProperty(value = "结束Y速度")
    private java.math.BigDecimal speedEndY;
	/**结束雷达车道号*/
	@Excel(name = "结束雷达车道号", width = 15)
    @ApiModelProperty(value = "结束雷达车道号")
    private java.lang.Integer laneEndRadar;
	/**结束道路车道号*/
	@Excel(name = "结束道路车道号", width = 15)
    @ApiModelProperty(value = "结束道路车道号")
    private java.lang.String laneEndRoad;
	/**车型*/
	@Excel(name = "车型", width = 15)
    @ApiModelProperty(value = "车型")
    private java.lang.String carType;
	/**车长*/
	@Excel(name = "车长", width = 15)
    @ApiModelProperty(value = "车长")
    private java.lang.Integer carLength;
	/**最高行驶速度*/
	@Excel(name = "最高行驶速度", width = 15)
    @ApiModelProperty(value = "最高行驶速度")
    private java.math.BigDecimal speedMax;
	/**最低行驶速度*/
	@Excel(name = "最低行驶速度", width = 15)
    @ApiModelProperty(value = "最低行驶速度")
    private java.math.BigDecimal speedMin;
	/**平均行驶速度*/
	@Excel(name = "平均行驶速度", width = 15)
    @ApiModelProperty(value = "平均行驶速度")
    private java.math.BigDecimal speedAvg;
	/**进雷达时间*/
	@Excel(name = "进雷达时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "进雷达时间")
    private java.util.Date beginTime;
	/**出雷达时间*/
	@Excel(name = "出雷达时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "出雷达时间")
    private java.util.Date endTime;
	/**进雷达坐标X*/
	@Excel(name = "进雷达坐标X", width = 15)
    @ApiModelProperty(value = "进雷达坐标X")
    private java.math.BigDecimal beginCoordinateX;
	/**进雷达坐标Y*/
	@Excel(name = "进雷达坐标Y", width = 15)
    @ApiModelProperty(value = "进雷达坐标Y")
    private java.math.BigDecimal beginCoordinateY;
	/**出雷达坐标X*/
	@Excel(name = "出雷达坐标X", width = 15)
    @ApiModelProperty(value = "出雷达坐标X")
    private java.math.BigDecimal endCoordinateX;
	/**出雷达坐标Y*/
	@Excel(name = "出雷达坐标Y", width = 15)
    @ApiModelProperty(value = "出雷达坐标Y")
    private java.math.BigDecimal endCoordinateY;
    @Excel(name = "拟合方程参数", width = 15)
    @ApiModelProperty(value = "拟合方程参数")
    private String equationParam;
    /**目标ID*/
    private transient java.lang.String targetId;
    /**上次Y坐标*/
    private transient float lastY = -1;
    /**目标行驶轨迹*/
    private transient JSONArray jsonArray;
    /**目标行驶轨迹-源*/
    private transient JSONArray jsonArraySrc;
    /**maxVyTimeInMillis*/
    private transient long maxVyTimeInMillis;

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return "ZcLdEventRadarInfo{" +
                ", equId='" + equId + '\'' +
                ", beginCoordinateY=" + (beginCoordinateY!=null? beginCoordinateY.setScale(4, BigDecimal.ROUND_DOWN): beginCoordinateY) +
                ", endCoordinateY=" + (endCoordinateY!=null? endCoordinateY.setScale(4, BigDecimal.ROUND_DOWN): endCoordinateY) +
                ", carType='" + carType + '\'' +
                ", carLength=" + carLength +
                ", beginTime=" + (beginTime!=null? sdf.format(beginTime): beginTime) +
                ", endTime=" + (endTime!=null? sdf.format(endTime): endTime) +
                ", beginCoordinateX=" + beginCoordinateX +
                ", endCoordinateX=" + endCoordinateX +
                ", laneStartRadar=" + laneStartRadar +
                ", laneStartRoad='" + laneStartRoad + '\'' +
                ", speedStartX=" + speedStartX +
                ", speedStartY=" + speedStartY +
                ", speedEndX=" + speedEndX +
                ", speedEndY=" + speedEndY +
                ", laneEndRadar=" + laneEndRadar +
                ", laneEndRoad='" + laneEndRoad + '\'' +
                ", speedMax=" + speedMax +
                ", speedMin=" + speedMin +
                ", speedAvg=" + speedAvg +
                ", targetId='" + targetId + '\'' +
                ", lastY=" + lastY +
                '}';
    }
}
