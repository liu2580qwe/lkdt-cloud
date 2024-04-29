package org.lkdt.modules.radar.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.lkdt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 雷达单元态势（来/去向级别）
 * @Author: jeecg-boot
 * @Date:   2021-07-30
 * @Version: V1.0
 */
@Data
@TableName("zc_ld_unit_situation_direction")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="zc_ld_unit_situation_direction对象", description="雷达单元态势（来/去向级别）")
public class LdUnitSituationDirection implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "主键")
    private String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**数据时间*/
	@Excel(name = "数据时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "数据时间")
    private Date dataTime;
	/**所属单元*/
	@Excel(name = "所属单元", width = 15)
    @ApiModelProperty(value = "所属单元")
    private String unitId;
	/**所属雷达*/
	@Excel(name = "所属雷达", width = 15)
    @ApiModelProperty(value = "所属雷达")
    private String radarId;
	/**cvalue*/
	@Excel(name = "cvalue", width = 15)
    @ApiModelProperty(value = "cvalue")
    private BigDecimal cvalue;
	/**dvalue*/
	@Excel(name = "dvalue", width = 15)
    @ApiModelProperty(value = "dvalue")
    private BigDecimal dvalue;
	/**evalue*/
	@Excel(name = "evalue", width = 15)
    @ApiModelProperty(value = "evalue")
    private BigDecimal evalue;
	/**risk_value*/
	@Excel(name = "risk_value", width = 15)
    @ApiModelProperty(value = "risk_value")
    private Integer riskValue;
	/**situation_value*/
	@Excel(name = "situation_value", width = 15)
    @ApiModelProperty(value = "situation_value")
    private String situationValue;
	/**car_flow*/
	@Excel(name = "car_flow", width = 15)
    @ApiModelProperty(value = "car_flow")
    private Integer carFlow;
	/**whole*/
	@Excel(name = "whole", width = 15)
    @ApiModelProperty(value = "whole")
    private BigDecimal whole;
	/**whole_xiao*/
	@Excel(name = "whole_xiao", width = 15)
    @ApiModelProperty(value = "whole_xiao")
    private BigDecimal wholeXiao;
	/**whole_da*/
	@Excel(name = "whole_da", width = 15)
    @ApiModelProperty(value = "whole_da")
    private BigDecimal wholeDa;
	/**delt_value*/
	@Excel(name = "delt_value", width = 15)
    @ApiModelProperty(value = "delt_value")
    private BigDecimal deltValue;
	/**discrete_value*/
	@Excel(name = "discrete_value", width = 15)
    @ApiModelProperty(value = "discrete_value")
    private BigDecimal discreteValue;
	/**direction*/
	@Excel(name = "direction", width = 15)
    @ApiModelProperty(value = "direction")
    private String direction;
}
