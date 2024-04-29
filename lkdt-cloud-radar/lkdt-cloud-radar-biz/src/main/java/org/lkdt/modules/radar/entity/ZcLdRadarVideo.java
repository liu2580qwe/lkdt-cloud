package org.lkdt.modules.radar.entity;
import java.io.Serializable;
import java.util.Date;

import org.jeecgframework.poi.excel.annotation.Excel;
import org.lkdt.common.aspect.annotation.Dict;
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
 * @Description: 雷达设备摄像头表
 * @Author: jeecg-boot
 * @Date:   2021-07-26
 * @Version: V1.0
 */
@Data
@TableName("zc_ld_radar_video")
@ApiModel(value="zc_ld_radar_equipment对象", description="雷达设备表")
public class ZcLdRadarVideo implements Serializable {
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
	/**IP地址*/
	@Excel(name = "IP地址", width = 15)
	@ApiModelProperty(value = "IP地址")
	private String ip;
	/**端口号*/
	@Excel(name = "端口号", width = 15)
	@ApiModelProperty(value = "端口号")
	private Integer port;
	/**用户名*/
	@Excel(name = "用户名", width = 15)
	@ApiModelProperty(value = "用户名")
	private String username;
	/**登录密码*/
	@Excel(name = "登录密码", width = 15)
	@ApiModelProperty(value = "登录密码")
	private String password;
	/**视频链接*/
	@Excel(name = "视频链接", width = 15)
	@ApiModelProperty(value = "视频链接")
	private String url;
	/**视频链接*/
	@Excel(name = "区域划分", width = 15)
	@ApiModelProperty(value = "区域划分")
	private String areaDivision;
	/**所属单元*/
	@Excel(name = "所属单元", width = 15)
	@ApiModelProperty(value = "所属单元")
	@Dict(dictTable = "zc_ld_unit",dicCode = "id",dicText = "remark")
	private String unitId;
	/**是否关联雷达*/
	@Excel(name = "是否关联雷达", width = 15)
	@ApiModelProperty(value = "是否关联雷达")
	@TableField(exist = false)
	private Boolean isItRelated;
}
