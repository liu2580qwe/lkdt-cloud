package org.lkdt.modules.traffic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.lkdt.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 收费站封路事件详细
 * @Author: jeecg-boot
 * @Date:   2021-06-03
 * @Version: V1.0
 */
@Data
@TableName("zc_toll_station_recorder")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="zc_toll_station_recorder对象", description="收费站封路事件详细")
public class TollStationRecorder implements Serializable {
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
	/**收费站编号*/
	@Excel(name = "收费站编号", width = 15)
    @ApiModelProperty(value = "收费站编号")
    private String tsnum;
	/**收费站名称*/
	@Excel(name = "收费站名称", width = 15)
    @ApiModelProperty(value = "收费站名称")
    private String tsname;
	/**高速名称*/
	@Excel(name = "高速名称", width = 15)
    @ApiModelProperty(value = "高速名称")
    private String roadname;
	/**高速编号*/
	@Excel(name = "高速编号", width = 15)
    @ApiModelProperty(value = "高速编号")
    private String roadnum;
	/**收费站方向*/
	@Excel(name = "收费站方向", width = 15)
    @ApiModelProperty(value = "收费站方向")
    private String direction;
	/**出入口 1：入口，0：出口*/
	@Excel(name = "出入口 1：入口，0：出口", width = 15)
    @ApiModelProperty(value = "出入口 1：入口，0：出口")
    @Dict(dicCode = "io")
    private String io;
	/**纬度*/
	@Excel(name = "纬度", width = 15)
    @ApiModelProperty(value = "纬度")
    private String lat;
	/**经度*/
	@Excel(name = "经度", width = 15)
    @ApiModelProperty(value = "经度")
    private String lon;
	/**0 恶劣天气， 1 交通事故， 3 诱导分流， 4 养护施工， 5 其他*/
	@Excel(name = "0 恶劣天气， 1 交通事故， 3 诱导分流， 4 养护施工， 5 其他", width = 15)
    @ApiModelProperty(value = "0 恶劣天气， 1 交通事故， 3 诱导分流， 4 养护施工， 5 其他")
    @Dict(dicCode = "closereason")
    private String closereason;
	/**内容*/
	@Excel(name = "内容", width = 15)
    @ApiModelProperty(value = "内容")
    private String closereasontext;
	/**关闭时间*/
	@Excel(name = "关闭时间", width = 15)
    @ApiModelProperty(value = "关闭时间")
    private String closetime;
	/**croadcid*/
	@Excel(name = "croadcid", width = 15)
    @ApiModelProperty(value = "croadcid")
    private String croadcid;
	/**cunitid*/
	@Excel(name = "cunitid", width = 15)
    @ApiModelProperty(value = "cunitid")
    private String cunitid;
	/**数据类型*/
	@Excel(name = "数据类型", width = 15)
    @ApiModelProperty(value = "数据类型")
    private String datatype;
	/**预计开放时间*/
	@Excel(name = "预计开放时间", width = 15)
    @ApiModelProperty(value = "预计开放时间")
    private String estimatopen;
	/**事件id*/
	@Excel(name = "事件id", width = 15)
    @ApiModelProperty(value = "事件id")
    private String eventid;
	/**事件名*/
	@Excel(name = "事件名", width = 15)
    @ApiModelProperty(value = "事件名")
    private String eventname;
	/**执行时间*/
	@Excel(name = "执行时间", width = 15)
    @ApiModelProperty(value = "执行时间")
    private String exetime;
	/**信息类型*/
	@Excel(name = "信息类型", width = 15)
    @ApiModelProperty(value = "信息类型")
    private String infotype;
	/**通知时间*/
	@Excel(name = "通知时间", width = 15)
    @ApiModelProperty(value = "通知时间")
    private String noticetime;
	/**旧id*/
	@Excel(name = "旧id", width = 15)
    @ApiModelProperty(value = "旧id")
    private String oldid;
	/**多个车站名*/
	@Excel(name = "多个车站名", width = 15)
    @ApiModelProperty(value = "多个车站名")
    private String stationnames;
	/**车站名*/
	@Excel(name = "车站名", width = 15)
    @ApiModelProperty(value = "车站名")
    private String stationname;
	/**收费站id（入口和出口）*/
	@Excel(name = "收费站id（入口和出口）", width = 15)
    @ApiModelProperty(value = "收费站id（入口和出口）")
    private String tollstationid;
	/**1:通行 0:禁行*/
	@Excel(name = "1:通行 0:禁行", width = 15)
    @ApiModelProperty(value = "1:通行 0:禁行")
    @Dict(dicCode = "allow")
    private String allow;
	/**禁行时间*/
	@Excel(name = "禁行时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "禁行时间")
    private Date forbidtime;
	/**解封时间*/
	@Excel(name = "解封时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "解封时间")
    private Date allowtime;
    /**持续时间*/
    @TableField(exist = false)
    private String continuousTime;
    /**管制等级*/
    @TableField(exist = false)
    private String controlLevel;
}
