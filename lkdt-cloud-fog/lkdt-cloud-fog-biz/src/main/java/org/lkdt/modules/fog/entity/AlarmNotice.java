package org.lkdt.modules.fog.entity;

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
import org.jeecgframework.poi.excel.annotation.Excel;
import org.lkdt.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: zc_alarm_notice
 * @Author: jeecg-boot
 * @Date:   2021-04-28
 * @Version: V1.0
 */
@Data
@TableName("zc_alarm_notice")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="zc_alarm_notice对象", description="zc_alarm_notice")
public class AlarmNotice implements Serializable {
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
	/**设备id*/
	@Excel(name = "设备id", width = 15)
    @ApiModelProperty(value = "设备id")
    @Dict(dictTable ="zc_equipment",dicText = "equ_name",dicCode = "id")
    private String epId;
	/**告警id*/
	@Excel(name = "告警id", width = 15)
    @ApiModelProperty(value = "告警id")
    /*@Dict(dictTable ="zc_alarm",dicText = "equ_name",dicCode = "id")*/
    private String alarmId;
	/**发送时间*/
	@Excel(name = "发送时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "发送时间")
    private Date sendtime;
	/**发送人*/
	@Excel(name = "发送人", width = 15)
    @ApiModelProperty(value = "发送人")
    private String sender;
	/**类型 1:发生告警 2:告警等级提高 4:告警解除*/
    @Dict(dicCode="notice_type")
	@Excel(name = "类型 1:发生告警 2:告警等级提高 4:告警解除", width = 15)
    @ApiModelProperty(value = "类型 1:发生告警 2:告警等级提高 4:告警解除")
    private String type;
	/**发送文本*/
	@Excel(name = "发送文本", width = 15)
    @ApiModelProperty(value = "发送文本")
    private String sendtext;
	/**操作人*/
	@Excel(name = "操作人", width = 15)
    @ApiModelProperty(value = "操作人")
    @Dict(dictTable = "sys_user",dicText = "realname",dicCode = "id")
    private String handler;
	/**操作时间*/
	@Excel(name = "操作时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "操作时间")
    private Date handletime;
	/**1:已确认有效 0:已确认无效 9:未确认*/
	@Dict(dicCode="iseffective")
	@Excel(name = "1:已确认有效 0:已确认无效 9:未确认", width = 15)
    @ApiModelProperty(value = "1:已确认有效 0:已确认无效 9:未确认")
    private String iseffective;
	/**确认能见度*/
	@Excel(name = "确认能见度", width = 15)
    @ApiModelProperty(value = "确认能见度")
    private String distance;

	@TableField(exist = false)
    private String imgpath;
	@TableField(exist = false)
    @Excel(name = "操作时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date imgtime;
}
