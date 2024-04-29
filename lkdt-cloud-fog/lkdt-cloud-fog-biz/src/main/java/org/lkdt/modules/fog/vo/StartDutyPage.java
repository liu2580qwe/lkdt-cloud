package org.lkdt.modules.fog.vo;

import java.util.List;

import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelEntity;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.lkdt.modules.fog.entity.DutyClock;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Description: 值班记录表
 * @Author: jeecg-boot
 * @Date:   2021-07-01
 * @Version: V1.0
 */
@Data
@ApiModel(value="zc_start_dutyPage对象", description="值班记录表")
public class StartDutyPage {

	/**主键*/
	@ApiModelProperty(value = "主键")
	private String id;
	/**值班人*/
	@ApiModelProperty(value = "值班人")
	private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "创建时间")
	private Date createTime;
	/**更新人*/
	@ApiModelProperty(value = "更新人")
	private String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "更新时间")
	private Date updateTime;
	/**所属部门*/
	@ApiModelProperty(value = "所属部门")
	private String sysOrgCode;
	/**是否有雾/0：否1：是*/
	@Excel(name = "是否有雾/0：否1：是", width = 15)
	@ApiModelProperty(value = "是否有雾/0：否1：是")
	private String isfog;
	/**雾类型：雾/团雾/霾/雨雾/其它*/
	@Excel(name = "雾类型：雾/团雾/霾/雨雾/其它", width = 15)
	@ApiModelProperty(value = "雾类型：雾/团雾/霾/雨雾/其它")
	private String fogType;
	/**状态:1:已开始，未结束，2：已结束*/
	@Excel(name = "状态:1:已开始，未结束，2：已结束", width = 15)
	@ApiModelProperty(value = "状态:1:已开始，未结束，2：已结束")
	private String type;
	/**开始时间*/
	@Excel(name = "开始时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "开始时间")
	private Date startTime;
	/**结束时间*/
	@Excel(name = "结束时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "结束时间")
	private Date endTime;
	/**天气预报*/
	@Excel(name = "天气预报", width = 15)
	@ApiModelProperty(value = "天气预报")
	private String weather;
	/**今日雾情/0：否1：是*/
	@Excel(name = "今日雾情/0：否1：是", width = 15)
	@ApiModelProperty(value = "今日雾情/0：否1：是")
	private String todayIsfog;
	/**是否有雨/0：否1：是*/
	@Excel(name = "是否有雨/0：否1：是", width = 15)
	@ApiModelProperty(value = "是否有雨/0：否1：是")
	private String israin;
	
	@ExcelCollection(name="值班打卡")
	@ApiModelProperty(value = "值班打卡")
	private List<DutyClock> dutyClockList;
	
}
