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
 * @Description: 员工周报表
 * @Author: jeecg-boot
 * @Date:   2021-05-18
 * @Version: V1.0
 */
@Data
@TableName("zc_employee_weekreport")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="zc_employee_weekreport对象", description="员工周报表")
public class ZcEmployeeWeekreport implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
    
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String creatername;
    
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
	/**本周/月工作*/
	@Excel(name = "本周/月工作", width = 15)
    @ApiModelProperty(value = "本周/月工作")
    private java.lang.String worknote;
	/**下周/月计划*/
	@Excel(name = "下周/月计划", width = 15)
    @ApiModelProperty(value = "下周/月计划")
    private java.lang.String tplan;
	/**类型*/
	@Excel(name = "类型", width = 15)
    @ApiModelProperty(value = "类型")
    private java.lang.String rtype;
	/**自评*/
	@Excel(name = "自评", width = 15)
    @ApiModelProperty(value = "自评")
    private java.lang.String selfeva;
	/**项目负责人意见*/
	@Excel(name = "项目负责人意见", width = 15)
    @ApiModelProperty(value = "项目负责人意见")
    private java.lang.String proeva;
	/**自评级别*/
	@Excel(name = "自评级别", width = 15)
    @ApiModelProperty(value = "自评级别")
    private java.lang.String selfeval;
	/**项目评级别*/
	@Excel(name = "项目评级别", width = 15)
    @ApiModelProperty(value = "项目评级别")
    private java.lang.String proeval;
	/**管理人意见*/
	@Excel(name = "管理人意见", width = 15)
    @ApiModelProperty(value = "管理人意见")
    private java.lang.String meva;
	/**管理人意见级别*/
	@Excel(name = "管理人意见级别", width = 15)
    @ApiModelProperty(value = "管理人意见级别")
    private java.lang.String meval;
	/**技术负责人意见*/
	@Excel(name = "技术负责人意见", width = 15)
    @ApiModelProperty(value = "技术负责人意见")
    private java.lang.String teva;
	/**技术意见级别*/
	@Excel(name = "技术意见级别", width = 15)
    @ApiModelProperty(value = "技术意见级别")
    private java.lang.String teval;
	/**时段*/
	@Excel(name = "时段", width = 15)
    @ApiModelProperty(value = "时段")
    private java.lang.String tarea;
}
