package org.lkdt.modules.fog.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import org.lkdt.modules.fog.entity.Alarm;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Description: 告警道路表
 * @Author: jeecg-boot
 * @Date:   2021-04-27
 * @Version: V1.0
 */
@Data
@ApiModel(value="zc_alarm_roadPage对象", description="告警道路表")
public class AlarmRoadPage {

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
	/**道路ID*/
	@Excel(name = "道路ID", width = 15)
	@ApiModelProperty(value = "道路ID")
	private Integer hwId;
	/**告警类型*/
	@Excel(name = "告警类型", width = 15)
	@ApiModelProperty(value = "告警类型")
	private String roadAlarmType;
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
	/**创建时间*/
	@Excel(name = "创建时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "创建时间")
	private Date starttime;
	/**结束时间*/
	@Excel(name = "结束时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "结束时间")
	private Date endtime;
	/**图片地址*/
	@Excel(name = "图片地址", width = 15)
	@ApiModelProperty(value = "图片地址")
	private String imgpath;
	/**图片时间*/
	@Excel(name = "图片时间", width = 15)
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "图片时间")
	private Date imgtime;
	
	@ExcelCollection(name="告警表")
	@ApiModelProperty(value = "告警表")
	private List<Alarm> alarmList;
	
}
