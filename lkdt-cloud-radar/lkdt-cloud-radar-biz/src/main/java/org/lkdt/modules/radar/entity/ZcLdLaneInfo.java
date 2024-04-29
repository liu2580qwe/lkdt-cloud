package org.lkdt.modules.radar.entity;

import java.io.Serializable;

import org.lkdt.common.aspect.annotation.Dict;
import org.lkdt.common.util.StringUtils;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 雷达车道与道路车道关系表
 * @Author: jeecg-boot
 * @Date:   2021-03-17
 * @Version: V1.0
 */
@Data
@TableName("zc_ld_lane_info")
@ApiModel(value="zc_ld_equipment对象", description="雷达设备表")
public class ZcLdLaneInfo implements Serializable {
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
	/**雷达设备ID*/
    @Dict(dicCode = "id",dicText = "equ_name",dictTable = "zc_ld_radar_equipment")
	@ApiModelProperty(value = "雷达设备ID")
	private java.lang.String equId;
	/**雷达车道*/
	@Excel(name = "雷达车道", width = 15)
	@ApiModelProperty(value = "雷达车道")
	private java.lang.Integer laneRadar;
	/**道路车道*/
	@Excel(name = "道路车道", width = 15)
	@ApiModelProperty(value = "道路车道")
	private java.lang.String laneRoad;
	/**车道宽度（㎝）*/
	@Excel(name = "车道宽度（㎝）", width = 15)
	@ApiModelProperty(value = "车道宽度（㎝）")
	private java.lang.Integer laneWidth;
	/**当前车道与最左侧车道的距离（cm）*/
	@Excel(name = "当前车道与最左侧车道的距离（cm）", width = 15)
	@ApiModelProperty(value = "当前车道与最左侧车道的距离（cm）")
	private java.lang.Integer leftMostWidth;
	/**多项式拟合数据*/
	@Excel(name = "多项式拟合数据", width = 15)
	@ApiModelProperty(value = "多项式拟合数据")
	private java.lang.String laneTrack;
	/**多项式拟合方程次*/
	@Excel(name = "多项式拟合方程次", width = 15)
	@ApiModelProperty(value = "多项式拟合方程次")
	private java.lang.Integer polynomialNum;
	/**多项式拟合时长（毫秒）*/
	@Excel(name = "多项式拟合时长（毫秒）", width = 15)
	@ApiModelProperty(value = "多项式拟合时长（毫秒）")
	private java.lang.Integer polynomialTime;
	/**车道中心点距离雷达的宽度（毫米MM）*/
	@Excel(name = "车道中心点距离雷达的宽度（毫米MM）", width = 15)
	@ApiModelProperty(value = "车道中心点距离雷达的宽度（毫米MM）")
	private java.lang.Integer laneMiddleWidth;
	/**车道标线方程*/
	@Excel(name = "车道标线方程", width = 15)
	@ApiModelProperty(value = "车道标线方程")
	private java.lang.String sideLineTrack;
	/**内侧车道边线距离最外侧应急车道边线的距离（CM）*/
	@Excel(name = "内侧车道边线距离最外侧应急车道边线的距离（CM）", width = 15)
	@ApiModelProperty(value = "内侧车道边线距离最外侧应急车道边线的距离（CM）")
	private java.lang.String lineOutermostWidth;
	/**是否节点雷达主路外侧车道*/
	@Excel(name = "是否节点雷达主路外侧车道", width = 15)
	@ApiModelProperty(value = "是否节点雷达主路外侧车道")
	private java.lang.String isOutsideLane;
	
	
	
	
	/**设备编号*/
	@TableField(exist = false)
    private java.lang.String equCode;
	/**设备名称*/
	@TableField(exist = false)
    private java.lang.String equName;
	/**监测方向*/
	@TableField(exist = false)
    private java.lang.String direction;
	/**监测车道数*/
    @TableField(exist = false)
    private java.lang.Integer lane;
	/**隔离带车道*/
    @TableField(exist = false)
    private java.lang.Integer medianStrip;
    /**车道名称*/
    @TableField(exist = false)
    private java.lang.String laneRoadName;
    /**车型*/
    @TableField(exist = false)
    private java.lang.String carType;
    /**拟合方程式*/
    @TableField(exist = false)
    private double[] equation;
    /**计算变道事件取雷达有效距离的的最小值*/
    @TableField(exist = false)
    private java.lang.Double eventSyMin ;
    /**计算变道事件取雷达有效距离的的最大值*/
    @TableField(exist = false)
    private java.lang.Double eventSyMax ;
    /**雷达安装位置处于雷达监测方向的哪一侧（L:左侧；R:右侧；M:绿岛-监测双向）*/
    @TableField(exist = false)
    private java.lang.String radarInstallLaneDirection;
    /**雷达类型（单元雷达：101，节点雷达102）*/
    @TableField(exist = false)
    private java.lang.String radarType;
    
    
    
    
}
