package org.lkdt.modules.radar.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 流量雷达数据表
 * @Author: jeecg-boot
 * @Date:   2021-03-23
 * @Version: V1.0
 */
@Data
@TableName("zc_ld_flow_radar_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="zc_ld_flow_radar_info对象", description="流量雷达数据表")
public class ZcLdFlowRadarInfo_old implements Serializable {
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
	/**雷达设备ID*/
	@Excel(name = "雷达设备ID", width = 15)
    @ApiModelProperty(value = "雷达设备ID")
    private String equId;
	/**方向*/
	@Excel(name = "方向", width = 15)
    @ApiModelProperty(value = "方向")
    private Integer direction;
	/**车型*/
	@Excel(name = "车型", width = 15)
    @ApiModelProperty(value = "车型")
    private String catType;
	/**速度（km/h）*/
	@Excel(name = "速度（km/h）", width = 15)
    @ApiModelProperty(value = "速度（km/h）")
    private BigDecimal speed;
	/**雷达车道号*/
	@Excel(name = "雷达车道号", width = 15)
    @ApiModelProperty(value = "雷达车道号")
    private Integer laneRadar;
	/**道路车道号*/
	@Excel(name = "道路车道号", width = 15)
    @ApiModelProperty(value = "道路车道号")
    private String laneRoad;
}
