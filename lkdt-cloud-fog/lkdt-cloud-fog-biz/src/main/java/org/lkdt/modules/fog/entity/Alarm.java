package org.lkdt.modules.fog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.lkdt.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 告警表
 * @Author: jeecg-boot
 * @Date:   2021-04-27
 * @Version: V1.0
 */
@ApiModel(value="zc_alarm_road对象", description="告警道路表")
@Data
@TableName("zc_alarm")
public class Alarm implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ID_WORKER_STR)
	@ApiModelProperty(value = "主键")
	private String id;
	/**创建人*/
	@ApiModelProperty(value = "创建人")
	private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "创建日期")
	private Date createTime;
	/**更新人*/
	@ApiModelProperty(value = "更新人")
	private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "更新日期")
	private Date updateTime;
	/**所属部门*/
	@ApiModelProperty(value = "所属部门")
	private String sysOrgCode;
	/**图片时间*/
	@Excel(name = "图片时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "图片时间")
	private Date imgtime;
	/**图片地址*/
	@Excel(name = "图片地址", width = 15)
	@ApiModelProperty(value = "图片地址")
	private String imgpath;
	/**摄像头ID*/
	@Excel(name = "摄像头ID", width = 15)
	@ApiModelProperty(value = "摄像头ID")
	@Dict(dictTable ="zc_equipment",dicText = "equ_name",dicCode = "id")
	private String epId;
	/**是否有效*/
	@Excel(name = "是否有效", width = 15)
	@ApiModelProperty(value = "是否有效")
	@Dict(dicCode = "iseffective")
	private String iseffective;
	/**确认人*/
	@Excel(name = "确认人", width = 15)
	@ApiModelProperty(value = "确认人")
	@Dict(dictTable = "sys_user",dicText = "realname",dicCode = "id")
	private String confirmor;
	/**确认时间*/
	@Excel(name = "确认时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "确认时间")
	private Date confirmtime;
	/**可见距离*/
	@Excel(name = "可见距离", width = 15)
	@ApiModelProperty(value = "可见距离")
	private Float distance;
	/**团雾类型*/
	@Excel(name = "团雾类型", width = 15)
	@ApiModelProperty(value = "团雾类型")
	@Dict(dicCode = "section_warning_type")
	private String fogType;
	/**告警等级*/
	@Excel(name = "告警等级", width = 15)
	@ApiModelProperty(value = "告警等级")
	@Dict(dicCode = "alarm_level")
	private String level;
	/**开始时间*/
	@Excel(name = "开始时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "开始时间")
	private Date begintime;
	/**结束时间*/
	@Excel(name = "结束时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "结束时间")
	private Date endtime;
	/**摄像头地址*/
	@Excel(name = "摄像头地址", width = 15)
	@ApiModelProperty(value = "摄像头地址")
	private String address;
	/**告警道路ID*/
	@ApiModelProperty(value = "告警道路ID")
	private String roadAlarmId;

	/**摄像头名称*/
	@ApiModelProperty(value = "摄像头名称")
	@TableField(exist = false)
	private String equName;

	/**type*/
	@ApiModelProperty(value = "type")
	@TableField(exist = false)
	private Integer type;

	/**原始值*/
	@ApiModelProperty(value = "calx")
	@TableField(exist = false)
	private Integer calx;

	/**路段+桩号*/
	@TableField(exist = false)
	@Dict(dictTable = "zc_highway",dicText = "name",dicCode = "id")
	private String hwId;
	/**持续时间*/
	@TableField(exist = false)
	private String continuousTime;
}
