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
 * @Description: 交通流三态数据
 * @Author: jeecg-boot
 * @Date:   2021-08-19
 * @Version: V1.0
 */
@Data
@TableName("zc_ld_roadflow_threestatus")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="zc_ld_roadflow_threestatus对象", description="交通流三态数据")
public class ZcLdRoadflowThreestatus implements Serializable {
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
	/**交通流态势值*/
	@Excel(name = "交通流态势值", width = 15)
    @ApiModelProperty(value = "交通流态势值")
    private BigDecimal roadflowriskvalue;
	/**交通流态势标签*/
	@Excel(name = "交通流态势标签", width = 15)
    @ApiModelProperty(value = "交通流态势标签")
    private String roadflowrisklabel;
    /**交通拥堵度*/
    @Excel(name = "交通拥堵度", width = 15)
    @ApiModelProperty(value = "交通拥堵度")
	private String congestionIndex;
	/**交通流态势阈值*/
	@Excel(name = "交通流态势阈值", width = 15)
    @ApiModelProperty(value = "交通流态势阈值")
    private BigDecimal pv;
	/**能见度*/
	@Excel(name = "能见度", width = 15)
    @ApiModelProperty(value = "能见度")
    private Integer distance;
	/**超车道小车数*/
	@Excel(name = "超车道小车数", width = 15)
    @ApiModelProperty(value = "超车道小车数")
    private Integer smallnum;
	/**超车道大车数*/
	@Excel(name = "超车道大车数", width = 15)
    @ApiModelProperty(value = "超车道大车数")
    private Integer bignum;
	/**大车限速值*/
	@Excel(name = "大车限速值", width = 15)
    @ApiModelProperty(value = "大车限速值")
    private BigDecimal vlimitbig;
	/**小车限速值*/
	@Excel(name = "小车限速值", width = 15)
    @ApiModelProperty(value = "小车限速值")
    private BigDecimal vlimitsmall;
	/**数据时间*/
	@Excel(name = "数据时间", width = 15, format = "yyyy-MM-dd HH:mm:ss.SSS")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss.SSS")
    @ApiModelProperty(value = "数据时间")
    private Date datatime;
	/**单元ID*/
	@Excel(name = "单元ID", width = 15)
    @ApiModelProperty(value = "单元ID")
    private String unitid;
	/**雷达ID*/
	@Excel(name = "雷达ID", width = 15)
    @ApiModelProperty(value = "雷达ID")
    private String radarid;
}
