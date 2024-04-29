package org.lkdt.modules.system.entity;

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
 * @Description: zc_highway
 * @Author: jeecg-boot
 * @Date:   2021-04-23
 * @Version: V1.0
 */
@Data
@TableName("zc_highway")
@ApiModel(value="zc_highway对象", description="zc_highway")
public class Highway implements Serializable {
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
	/**父级节点*/
	@Excel(name = "父级节点", width = 15)
	@ApiModelProperty(value = "父级节点")
	private String pid;
	/**是否有子节点*/
	@Excel(name = "是否有子节点", width = 15)
	@ApiModelProperty(value = "是否有子节点")
	private String hasChild;
	/**编码*/
	@Excel(name = "编码", width = 15)
	@ApiModelProperty(value = "编码")
	private String name;
	/**路段详情*/
	@Excel(name = "路段详情", width = 15)
	@ApiModelProperty(value = "路段详情")
	private String detail;
	/**1:国家公路；2：省级高速；3：市级高速*/
	@Excel(name = "1:国家公路；2：省级高速；3：市级高速", width = 15)
	@ApiModelProperty(value = "1:国家公路；2：省级高速；3：市级高速")
	private String waytype;
	/**1：高危路段；0不是高危路段*/
	@Excel(name = "1：高危路段；0不是高危路段", width = 15)
	@ApiModelProperty(value = "1：高危路段；0不是高危路段")
	private String isdanger;
	/**所在区域*/
	@Excel(name = "所在区域", width = 15, dicCode = "yn")
	@ApiModelProperty(value = "所在区域")
	private String areaid;
	/**区域path*/
	@Excel(name = "区域path", width = 15)
	@ApiModelProperty(value = "区域path")
	private String areapath;
	/**公路路径*/
	@Excel(name = "公路路径", width = 15)
	@ApiModelProperty(value = "公路路径")
	private String idpath;
}
