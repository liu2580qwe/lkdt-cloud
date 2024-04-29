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
 * @Description: 三方告警信息操作记录
 * @Author: jeecg-boot
 * @Date:   2021-04-27
 * @Version: V1.0
 */
@ApiModel(value="zc_alert_threepart对象", description="三方告警信息")
@Data
@TableName("zc_alert_threepart_record")
public class AlertThreepartRecord implements Serializable {
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
	/**告警消息主键*/
	@ApiModelProperty(value = "告警消息主键")
	private String alertThreepartId;
	/**交警操作【0：无操作，1：上报中心，2：管制通报，3：无效警情】*/
	@Excel(name = "交警操作【0：无操作，1：上报中心，2：管制通报，3：无效警情】", width = 15)
	@ApiModelProperty(value = "交警操作【0：无操作，1：上报中心，2：管制通报，3：无效警情】")
	@Dict(dicCode = "operation_record")
	private String operation;
}
