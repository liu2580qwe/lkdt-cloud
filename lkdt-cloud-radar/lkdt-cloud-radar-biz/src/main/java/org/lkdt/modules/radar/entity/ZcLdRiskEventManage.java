package org.lkdt.modules.radar.entity;

import java.io.Serializable;

import org.jeecgframework.poi.excel.annotation.Excel;
import org.lkdt.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 雷达事件管理
 * @Author: jeecg-boot
 * @Date:   2021-08-27
 * @Version: V1.0
 */
@Data
@TableName("zc_ld_risk_event_manage")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="zc_ld_risk_event_manage对象", description="雷达事件管理")
public class ZcLdRiskEventManage implements Serializable {
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
	/**事件时间*/
	@Excel(name = "事件时间", width = 20, format = "yyyy-MM-dd HH:mm:ss.SSS")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss.SSS")
    @ApiModelProperty(value = "事件时间")
    private java.util.Date eventTime;
	/**雷达ID*/
	@Excel(name = "雷达ID", width = 15)
    @ApiModelProperty(value = "雷达ID")
//    @Dict(dictTable = "zc_ld_radar_equipment",dicCode = "id",dicText = "equ_name")
    private java.lang.String radarId;
	/**视频地址*/
	@Excel(name = "视频地址", width = 15)
    @ApiModelProperty(value = "视频地址")
    private java.lang.String videoUrl;
	/**事件类型*/
	@Excel(name = "事件类型", width = 15)
    @ApiModelProperty(value = "事件类型")
    @Dict(dicCode = "radar_event_type")
    private java.lang.String eventType;
	/**Y坐标*/
	@Excel(name = "Y坐标", width = 15)
    @ApiModelProperty(value = "Y坐标")
    private java.lang.Double eventSy;
	/**事件车道*/
	@Excel(name = "事件车道", width = 15)
    @ApiModelProperty(value = "事件车道")
    @Dict(dicCode = "lane_type")
    private java.lang.String eventLane;
	/**是否有效*/
	@Excel(name = "是否有效", width = 15)
    @ApiModelProperty(value = "是否有效")
    @Dict(dicCode = "radar_event_status")
    private java.lang.String valid;
	
	/**事件优先级 1 2 3 4*/
	@TableField(exist = false)
	private java.lang.Integer eventPriorityLevel;
	
	@TableField(exist = false)
	private java.lang.Integer tjCount;
	
}