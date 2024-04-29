package org.lkdt.modules.radar.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
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
 * @Description: 雷达单元态势（车道级别）
 * @Author: jeecg-boot
 * @Date:   2021-07-30
 * @Version: V1.0
 */
@Data
@TableName("zc_ld_unit_situation_lane")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="zc_ld_unit_situation_lane对象", description="雷达单元态势（车道级别）")
public class LdUnitSituationLane implements Serializable {
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
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss.SSS")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss.SSS")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**data_time*/
	@Excel(name = "data_time", width = 15, format = "yyyy-MM-dd HH:mm:ss.SSS")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss.SSS")
    @ApiModelProperty(value = "data_time")
    private Date dataTime;
	/**unit_id*/
	@Excel(name = "unit_id", width = 15)
    @ApiModelProperty(value = "unit_id")
    private String unitId;
	/**radar_id*/
	@Excel(name = "radar_id", width = 15)
    @ApiModelProperty(value = "radar_id")
    private String radarId;
	/**whole*/
	@Excel(name = "v_avg", width = 15)
    @ApiModelProperty(value = "v_avg")
    private BigDecimal vAvg;
	/**whole_xiao*/
	@Excel(name = "v_avg_car", width = 15)
    @ApiModelProperty(value = "v_avg_car")
    private BigDecimal vAvgCar;
	/**whole_da*/
	@Excel(name = "v_avg_truck", width = 15)
    @ApiModelProperty(value = "v_avg_truck")
    private BigDecimal vAvgTruck;
	/**delt_value*/
	@Excel(name = "delt_value", width = 15)
    @ApiModelProperty(value = "delt_value")
    private BigDecimal deltValue;
	/**discrete_value*/
	@Excel(name = "discrete_value", width = 15)
    @ApiModelProperty(value = "discrete_value")
    private BigDecimal discreteValue;
	/**time_headway*/
	@Excel(name = "time_distance", width = 15)
    @ApiModelProperty(value = "time_distance")
    private BigDecimal timeDistance;
	/**traffic_density*/
	@Excel(name = "car_flow_density", width = 15)
    @ApiModelProperty(value = "car_flow_density")
    private BigDecimal carFlowDensity;
	/**lane*/
	@Excel(name = "lane", width = 15)
    @ApiModelProperty(value = "lane")
    private String lane;

    @Excel(name = "car_count", width = 15)
    @ApiModelProperty(value = "car_count")
    private BigInteger carCount;

    @Excel(name = "lane_num", width = 15)
    @ApiModelProperty(value = "lane_num")
    private String laneNum;

    @Excel(name = "total_count", width = 15)
    @ApiModelProperty(value = "total_count")
    private BigInteger totalCount;

    @Excel(name = "lager_truck_count", width = 15)
    @ApiModelProperty(value = "lager_truck_count")
    private BigInteger lagerTruckCount;
}
