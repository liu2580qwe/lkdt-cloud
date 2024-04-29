package org.lkdt.modules.traffic.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 收费站封路事件
 * @Author: jeecg-boot
 * @Date:   2021-06-03
 * @Version: V1.0
 */
@Data
@TableName("zc_toll_station_event")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="zc_toll_station_event对象", description="收费站封路事件")
public class TollStationEvent implements Serializable {
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
	/**事件名称*/
	@Excel(name = "事件名称", width = 15)
    @ApiModelProperty(value = "事件名称")
    private String eventname;
	/**事件类型*/
	@Excel(name = "事件类型", width = 15)
    @ApiModelProperty(value = "事件类型")
    private String typeid;
	/**单元id*/
	@Excel(name = "单元id", width = 15)
    @ApiModelProperty(value = "单元id")
    private String unitid;
	/**单元名称*/
	@Excel(name = "单元名称", width = 15)
    @ApiModelProperty(value = "单元名称")
    private String unitname;
	/**部分中心id*/
	@Excel(name = "部分中心id", width = 15)
    @ApiModelProperty(value = "部分中心id")
    private String sectioncenterid;
	/**部分中心名称*/
	@Excel(name = "部分中心名称", width = 15)
    @ApiModelProperty(value = "部分中心名称")
    private String sectioncentername;
	/**创建人id*/
	@Excel(name = "创建人id", width = 15)
    @ApiModelProperty(value = "创建人id")
    private String createjopnum;
	/**创建人名称*/
	@Excel(name = "创建人名称", width = 15)
    @ApiModelProperty(value = "创建人名称")
    private String createname;
	/**路段id*/
	@Excel(name = "路段id", width = 15)
    @ApiModelProperty(value = "路段id")
    private String roadid;
	/**路段名称*/
	@Excel(name = "路段名称", width = 15)
    @ApiModelProperty(value = "路段名称")
    private String roadname;
	/**事件状态*/
	@Excel(name = "事件状态", width = 15)
    @ApiModelProperty(value = "事件状态")
    private String eventstate;
	/**事件描述*/
	@Excel(name = "事件描述", width = 15)
    @ApiModelProperty(value = "事件描述")
    private String accidentdesc;
	/**是否删除 0:未删除 1:已删除*/
	@Excel(name = "是否删除 0:未删除 1:已删除", width = 15)
    @ApiModelProperty(value = "是否删除 0:未删除 1:已删除")
    private String isdel;
	/**影响路段*/
	@Excel(name = "影响路段", width = 15)
    @ApiModelProperty(value = "影响路段")
    private String effectroad;
	/**总点*/
	@Excel(name = "总点", width = 15)
    @ApiModelProperty(value = "总点")
    private String totalpoints;
	/**入库时间*/
	@Excel(name = "入库时间", width = 15)
    @ApiModelProperty(value = "入库时间")
    private String putawaytime;
	/**无效时间*/
	@Excel(name = "无效时间", width = 15)
    @ApiModelProperty(value = "无效时间")
    private String deltime;
}
