package org.lkdt.modules.fog.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import org.lkdt.modules.fog.entity.AlertThreepartRecord;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Description: 三方告警信息
 * @Author: jeecg-boot
 * @Date:   2021-04-27
 * @Version: V1.0
 */
@Data
@ApiModel(value="zc_alert_threepartPage对象", description="三方告警信息")
public class AlertThreepartPage {

	/**主键*/
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
	/**路段ID*/
	@Excel(name = "路段ID", width = 15)
	@ApiModelProperty(value = "路段ID")
	private Integer hwId;
	/**路段告警信息主键*/
	@Excel(name = "路段告警信息主键", width = 15)
	@ApiModelProperty(value = "路段告警信息主键")
	private String roadAlarmId;
	/**告警等级*/
	@Excel(name = "告警等级", width = 15)
	@ApiModelProperty(value = "告警等级")
	private String alarmLevel;
	/**当前能见度*/
	@Excel(name = "当前能见度", width = 15)
	@ApiModelProperty(value = "当前能见度")
	private Integer mindistanceNow;
	/**历史最低能见度*/
	@Excel(name = "历史最低能见度", width = 15)
	@ApiModelProperty(value = "历史最低能见度")
	private Integer mindistanceHis;
	/**图片时间*/
	@Excel(name = "图片时间", width = 15, format = " yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "图片时间")
	private Date imgtime;
	/**图片地址*/
	@Excel(name = "图片地址", width = 15)
	@ApiModelProperty(value = "图片地址")
	private String imgpath;
	/**消息推送时间*/
	@Excel(name = "消息推送时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "消息推送时间")
	private Date alertTime;
	/**摄像头ID*/
	@Excel(name = "摄像头ID", width = 15)
	@ApiModelProperty(value = "摄像头ID")
	private String epId;
	/**记录表数据更新时间排他用*/
	@Excel(name = "记录表数据更新时间排他用", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "记录表数据更新时间排他用")
	private Date detailUpdateTime;
	/**操作人*/
	@Excel(name = "操作人", width = 15)
	@ApiModelProperty(value = "操作人")
	private String openid;
	/**操作 1：发送微信消息；2：发送微信+短信消息*/
	@Excel(name = "操作 1：发送微信消息；2：发送微信+短信消息", width = 15)
	@ApiModelProperty(value = "操作 1：发送微信消息；2：发送微信+短信消息")
	private String operation;
	/**能见度趋势*/
	@Excel(name = "能见度趋势", width = 15)
	@ApiModelProperty(value = "能见度趋势")
	private String trend;
	
	@ExcelCollection(name="三方告警信息操作记录")
	@ApiModelProperty(value = "三方告警信息操作记录")
	private List<AlertThreepartRecord> alertThreepartRecordList;
	
}
