package org.lkdt.modules.fog.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.lkdt.common.aspect.annotation.Dict;
import org.lkdt.common.util.HighWayUtil;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Description: 告警道路表
 * @Author: jeecg-boot
 * @Date:   2021-04-27
 * @Version: V1.0
 */
@ApiModel(value="zc_alarm_road对象", description="告警道路表")
@Data
@TableName("zc_alarm_road")
public class AlarmRoad implements Serializable {
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
	/**道路ID*/
	@Excel(name = "道路ID", width = 15)
    @ApiModelProperty(value = "道路ID")
    @Dict(dictTable ="zc_highway",dicText = "name",dicCode = "id")
    private String hwId;
	/**告警类型*/
	@Excel(name = "告警类型", width = 15)
    @ApiModelProperty(value = "告警类型")
    @Dict(dicCode = "section_warning_type")
    private String roadAlarmType;
	/**告警等级*/
	@Excel(name = "告警等级", width = 15)
    @ApiModelProperty(value = "告警等级")
    @Dict(dicCode = "alarm_level")
    private String alarmLevel;
	/**当前能见度*/
	@Excel(name = "当前能见度", width = 15)
    @ApiModelProperty(value = "当前能见度")
    private Float mindistanceNow;
	/**历史最低能见度*/
	@Excel(name = "历史最低能见度", width = 15)
    @ApiModelProperty(value = "历史最低能见度")
    private Float mindistanceHis;
	/**创建时间*/
	@Excel(name = "创建时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date starttime;
	/**结束时间*/
	@Excel(name = "结束时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间")
    private Date endtime;
	/**图片地址*/
	@Excel(name = "图片地址", width = 15)
    @ApiModelProperty(value = "图片地址")
    private String imgpath;
	/**图片时间*/
	@Excel(name = "图片时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "图片时间")
    private Date imgtime;

    @Excel(name = "影响里程", width = 15)
    @ApiModelProperty(value = "影响里程")
	private Double effectedMile;

    /**统计时间（用于报表）**/
    @TableField(exist = false)
    private String statisticsDate;

    /**统计值（用于报表）***/
    @TableField(exist = false)
    private String statisticsNum;

    @TableField(exist = false)
    private String hwName;

	/**统计个数**/
    @ApiModelProperty(value = "统计个数")
    @TableField(exist = false)
	private Integer count;
}
