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
public class ZcLdFlowRadarInfo implements Serializable {
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
	@Excel(name = "雷达设备ID", width = 15)
    @ApiModelProperty(value = "雷达设备ID")
    private java.lang.String equId;
    @Excel(name = "时间戳", width = 15)
    @ApiModelProperty(value = "时间戳")
    String timestamp;
    @Excel(name = "断面编号", width = 15)
    @ApiModelProperty(value = "断面编号")
    int sectionNumber;
    @Excel(name = "断面位置", width = 15)
    @ApiModelProperty(value = "断面位置")
    int sectionLocation;
    @Excel(name = "车道号", width = 15)
    @ApiModelProperty(value = "车道号")
    int lane;
    @Excel(name = "车流量", width = 15)
    @ApiModelProperty(value = "车流量")
    int flow;
    @Excel(name = "排队长度", width = 15)
    @ApiModelProperty(value = "排队长度")
    int queueLength;
    @Excel(name = "车道占有率", width = 15)
    @ApiModelProperty(value = "车道占有率")
    int occupancy;
    @Excel(name = "车道空间占有率", width = 15)
    @ApiModelProperty(value = "车道空间占有率")
    int occupancy2;
    @Excel(name = "车头时距", width = 15)
    @ApiModelProperty(value = "车头时距")
    int headWay;
    @Excel(name = "车头间距", width = 15)
    @ApiModelProperty(value = "车头间距")
    int headWay2;
    @Excel(name = "平均车速", width = 15)
    @ApiModelProperty(value = "平均车速")
    int averageSpeed;
    @Excel(name = "小型车流量", width = 15)
    @ApiModelProperty(value = "小型车流量")
    int sumMini;
    @Excel(name = "中型车流量", width = 15)
    @ApiModelProperty(value = "中型车流量")
    int sumMid;
    @Excel(name = "大型车流量", width = 15)
    @ApiModelProperty(value = "大型车流量")
    int sumLarge;
    @Excel(name = "统计周期", width = 15)
    @ApiModelProperty(value = "统计周期")
    int circle;

}
