package org.lkdt.modules.fog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.lkdt.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 值班打卡
 * @Author: jeecg-boot
 * @Date:   2021-07-01
 * @Version: V1.0
 */
@ApiModel(value="zc_start_duty对象", description="值班记录表")
@Data
@TableName("zc_duty_clock")
public class DutyClock implements Serializable {
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
	/**本次值班ID*/
	@ApiModelProperty(value = "本次值班ID")
	private String startDutyId;
	/**打卡状态：已打卡/未打卡（预留，暂时只存已打卡）*/
	@Excel(name = "打卡状态：已打卡/未打卡（预留，暂时只存已打卡）", width = 15)
	@ApiModelProperty(value = "打卡状态：已打卡/未打卡（预留，暂时只存已打卡）")
	@Dict(dicCode = "clock_state")
	private String clockState;
}
