package org.lkdt.modules.fog.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.lkdt.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 路段告警电话通知日志表
 * @Author: jeecg-boot
 * @Date:   2021-05-07
 * @Version: V1.0
 */
@Data
@TableName("zc_alarm_road_call_log")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="zc_alarm_road_call_log对象", description="路段告警电话通知日志表")
public class AlarmRoadCallLog implements Serializable {
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
	/**ZC_ALARM_ROAD表主键*/
	@Excel(name = "ZC_ALARM_ROAD表主键", width = 15)
    @ApiModelProperty(value = "ZC_ALARM_ROAD表主键")
    private String roadAlarmId;
	/**路段ID*/
	@Excel(name = "路段ID", width = 15)
    @ApiModelProperty(value = "路段ID")
    @Dict(dictTable ="zc_highway",dicText = "name",dicCode = "id")
    private Integer hwId;
	/**告警等级*/
	@Excel(name = "告警等级", width = 15)
    @ApiModelProperty(value = "告警等级")
    private String alarmLevel;
	/**能见度*/
	@Excel(name = "能见度", width = 15)
    @ApiModelProperty(value = "能见度")
    private Integer distance;
	/**告警时间*/
	@Excel(name = "告警时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "告警时间")
    private Date alarmTime;
	/**电话呼叫接口ID*/
	@Excel(name = "电话呼叫接口ID", width = 15)
    @ApiModelProperty(value = "电话呼叫接口ID")
    private String callId;
	/**电话呼叫时间*/
	@Excel(name = "电话呼叫时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "电话呼叫时间")
    private Date callTime;
	/**电话呼叫内容*/
	@Excel(name = "电话呼叫内容", width = 15)
    @ApiModelProperty(value = "电话呼叫内容")
    private String callContent;
	/**被呼叫的电话号码*/
	@Excel(name = "被呼叫的电话号码", width = 15)
    @ApiModelProperty(value = "被呼叫的电话号码")
    private String callNumber;
	/**呼叫结果*/
	@Excel(name = "呼叫结果", width = 15)
    @ApiModelProperty(value = "呼叫结果")
    private String callResultCode;
	/**呼叫结果*/
	@Excel(name = "呼叫结果", width = 15)
    @ApiModelProperty(value = "呼叫结果")
    private String callResultMessage;
	/**操作人*/
	@Excel(name = "操作人", width = 15)
    @ApiModelProperty(value = "操作人")
    private String openid;
}
