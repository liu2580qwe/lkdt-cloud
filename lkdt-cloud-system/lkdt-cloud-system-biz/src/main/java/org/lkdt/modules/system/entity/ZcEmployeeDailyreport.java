package org.lkdt.modules.system.entity;

import java.io.Serializable;

import org.jeecgframework.poi.excel.annotation.Excel;
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
 * @Description: 员工日报表
 * @Author: jeecg-boot
 * @Date:   2021-04-30
 * @Version: V1.0
 */
@Data
@TableName("zc_employee_dailyreport")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="zc_employee_dailyreport对象", description="员工日报表")
public class ZcEmployeeDailyreport implements Serializable {
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
	/**点赞*/
	@Excel(name = "点赞", width = 15)
    @ApiModelProperty(value = "点赞")
    private java.lang.Integer great;
	/**踩*/
	@Excel(name = "踩", width = 15)
    @ApiModelProperty(value = "踩")
    private java.lang.Integer bed;
	/**工作内容*/
	@Excel(name = "工作内容", width = 15)
    @ApiModelProperty(value = "工作内容")
    private java.lang.String worknote;
	/**明天计划*/
	@Excel(name = "明天计划", width = 15)
    @ApiModelProperty(value = "明天计划")
    private java.lang.String tplan;
}
