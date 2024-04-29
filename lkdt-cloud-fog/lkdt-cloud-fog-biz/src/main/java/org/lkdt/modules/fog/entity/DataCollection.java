package org.lkdt.modules.fog.entity;
import java.io.Serializable;
import java.util.Date;
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
 * @Description:  数据采集
 * @Author: jeecg-boot
 * @Date:   2021-07-01
 * @Version: V1.0
 */
@Data
@TableName("zc_data_collection")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="zc_data_collection对象", description=" 数据采集")
public class DataCollection implements Serializable {
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
	/**填写日期*/
	@Excel(name = "填写日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "填写日期")
    private Date writeDate;
	/**调查日期*/
	@Excel(name = "调查日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "调查日期")
    private Date surveyDate;
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
	/**开始桩号*/
	@Excel(name = "开始桩号", width = 15)
    @ApiModelProperty(value = "开始桩号")
    private String startStake;
	/**结束桩号*/
	@Excel(name = "结束桩号", width = 15)
    @ApiModelProperty(value = "结束桩号")
    private String endStake;
	/**所属路段*/
	@Excel(name = "所属路段", width = 15)
    @ApiModelProperty(value = "所属路段")
    @Dict(dictTable = "zc_highway",dicText = "name",dicCode = "id")
    private String hwId;
	/**天气情况*/
	@Excel(name = "天气情况", width = 15)
    @ApiModelProperty(value = "天气情况")
    private String weather;
	/**事故（事件）状况*/
	@Excel(name = "事故（事件）状况", width = 15)
    @ApiModelProperty(value = "事故（事件）状况")
    private String accident;
	/**填写人*/
	@Excel(name = "填写人", width = 15)
    @ApiModelProperty(value = "填写人")
    private String writtenBy;
	/**算法分析时间*/
	@Excel(name = "算法分析时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "算法分析时间")
    private Date arithmeticTime;
	/**算法分析人*/
	@Excel(name = "算法分析人", width = 15)
    @ApiModelProperty(value = "算法分析人")
    private String arithmeticBy;
}
