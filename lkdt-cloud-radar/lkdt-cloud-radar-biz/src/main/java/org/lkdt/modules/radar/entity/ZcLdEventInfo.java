package org.lkdt.modules.radar.entity;

import java.io.Serializable;

import org.jeecgframework.poi.excel.annotation.Excel;
import org.lkdt.modules.radar.entity.baseEntity.ZcLDBaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 事件信息表
 * @Author: jeecg-boot
 * @Date:   2021-03-23
 * @Version: V1.0
 */
@Data
@TableName("zc_ld_event_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="zc_ld_event_info对象", description="事件信息表")
public class ZcLdEventInfo extends ZcLDBaseEntity implements Serializable {
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
	/**事件雷达数据表ID*/
	@Excel(name = "事件雷达数据表ID", width = 15)
    @ApiModelProperty(value = "事件雷达数据表ID")
    private java.lang.String eventRadarInfoId;
	/**事件类型*/
	@Excel(name = "事件类型", width = 15)
    @ApiModelProperty(value = "事件类型")
    private java.lang.String eventType;
	/**X坐标*/
	@Excel(name = "X坐标", width = 15)
    @ApiModelProperty(value = "X坐标")
    private java.math.BigDecimal coordinateX;
	/**Y坐标*/
	@Excel(name = "Y坐标", width = 15)
    @ApiModelProperty(value = "Y坐标")
    private java.math.BigDecimal coordinateY;
	/**X速度*/
	@Excel(name = "X速度", width = 15)
    @ApiModelProperty(value = "X速度")
    private java.math.BigDecimal speedX;
	/**Y速度*/
	@Excel(name = "Y速度", width = 15)
    @ApiModelProperty(value = "Y速度")
    private java.math.BigDecimal speedY;
	/**X加速度*/
	@Excel(name = "X加速度", width = 15)
    @ApiModelProperty(value = "X加速度")
    private java.math.BigDecimal accelerationX;
	/**Y加速度*/
	@Excel(name = "Y加速度", width = 15)
    @ApiModelProperty(value = "Y加速度")
    private java.math.BigDecimal accelerationY;
	/**雷达车道号*/
	@Excel(name = "雷达车道号", width = 15)
    @ApiModelProperty(value = "雷达车道号")
    private java.lang.Integer laneRadar;
	/**道路车道号*/
	@Excel(name = "道路车道号", width = 15)
    @ApiModelProperty(value = "道路车道号")
    private java.lang.String laneRoad;
	/**车型*/
	@Excel(name = "车型", width = 15)
    @ApiModelProperty(value = "车型")
    private java.lang.String carType;
	/**车长*/
	@Excel(name = "车长", width = 15)
    @ApiModelProperty(value = "车长")
    private java.lang.Integer carLength;
	/**标志位*/
	private transient java.lang.Integer flag;
}
