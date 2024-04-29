package org.lkdt.modules.radar.entity;

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
 * @Description: 交通流三态系数
 * @Author: jeecg-boot
 * @Date:   2021-09-23
 * @Version: V1.0
 */
@Data
@TableName("zc_ld_three_status_coefficient")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="zc_ld_three_status_coefficient对象", description="交通流三态系数")
public class ZcLdThreeStatusCoefficient implements Serializable {
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
    /**雷达ID*/
	@Excel(name = "雷达ID", width = 15)
    @ApiModelProperty(value = "雷达ID")
    private java.lang.String radarId;
	/**数据时间*/
	@Excel(name = "数据时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "数据时间")
    private java.util.Date dataTime;
	/**方向*/
	@Excel(name = "方向", width = 15)
    @ApiModelProperty(value = "方向")
    private java.lang.String direction;
	/**风险性车速离散度*/
	@Excel(name = "风险性车速离散度", width = 15)
    @ApiModelProperty(value = "风险性车速离散度")
    private java.lang.Double coefficientRsd;
	/**平均速度*/
	@Excel(name = "平均速度", width = 15)
    @ApiModelProperty(value = "平均速度")
    private java.lang.Double coefficientV;
	/**通行速度指数*/
	@Excel(name = "通行速度指数", width = 15)
    @ApiModelProperty(value = "通行速度指数")
    private java.lang.Double coefficientP;
	/**单元路段风险性车速变异系数*/
	@Excel(name = "单元路段风险性车速变异系数", width = 15)
    @ApiModelProperty(value = "单元路段风险性车速变异系数")
    private java.lang.Double coefficientG;
}
