package org.lkdt.modules.traffic.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.lkdt.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 道路事件记录
 * @Author: jeecg-boot
 * @Date:   2021-06-03
 * @Version: V1.0
 */
@Data
@TableName("zc_traffic_incident_record")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="zc_traffic_incident_record对象", description="道路事件记录")
public class TrafficIncidentRecord implements Serializable {
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
	/**数据来源 默认空：1：来源96777，2：来源江苏高速，3：来源江苏高速封路信息*/
	@Excel(name = "数据来源 默认空：1：来源96777，2：来源江苏高速，3：来源江苏高速封路信息", width = 15)
    @ApiModelProperty(value = "数据来源 默认空：1：来源96777，2：来源江苏高速，3：来源江苏高速封路信息")
    @Dict(dicCode = "source_type")
    private String sourceType;
	/**事件类型。事故-1006001；施工-1006002；道路管制-1006006；拥堵-1006008;交通事件-1006010;恶劣天气-1006009*/
	@Excel(name = "事件类型。事故-1006001；施工-1006002；道路管制-1006006；拥堵-1006008;交通事件-1006010;恶劣天气-1006009", width = 15)
    @ApiModelProperty(value = "事件类型。事故-1006001；施工-1006002；道路管制-1006006；拥堵-1006008;交通事件-1006010;恶劣天气-1006009")
    @Dict(dicCode = "eventtype")
    private String eventtype;
	/**经度*/
	@Excel(name = "经度", width = 15)
    @ApiModelProperty(value = "经度")
    private String lon;
	/**纬度*/
	@Excel(name = "纬度", width = 15)
    @ApiModelProperty(value = "纬度")
    private String lat;
	/**type*/
	@Excel(name = "type", width = 15)
    @ApiModelProperty(value = "type")
    private String type;
	/**事件内容*/
	@Excel(name = "事件内容", width = 15)
    @ApiModelProperty(value = "事件内容")
    private String reportout;
	/**开始时间*/
	@Excel(name = "开始时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "开始时间")
    private Date occtime;
	/**预计结束时间*/
	@Excel(name = "预计结束时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "预计结束时间")
    private Date planovertime;
	/**realovertime*/
	@Excel(name = "realovertime", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "realovertime")
    private Date realovertime;
	/**路段名称*/
	@Excel(name = "路段名称", width = 15)
    @ApiModelProperty(value = "路段名称")
    private String roadname;
	/**拥堵时速*/
	@Excel(name = "拥堵时速", width = 15)
    @ApiModelProperty(value = "拥堵时速")
    private String jamspeed;
	/**拥堵距离（米）*/
	@Excel(name = "拥堵距离（米）", width = 15)
    @ApiModelProperty(value = "拥堵距离（米）")
    private String jamdist;
	/**持续时间（分钟）*/
	@Excel(name = "持续时间（分钟）", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "持续时间（分钟）")
    private Date longtime;
	/**方向*/
	@Excel(name = "方向", width = 15)
    @ApiModelProperty(value = "方向")
    private String directionname;
	/**数据采集入库时间*/
	@Excel(name = "数据采集入库时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "数据采集入库时间")
    private Date inputtime;
	/**是否已经推送箩筐*/
	@Excel(name = "是否已经推送箩筐", width = 15)
    @ApiModelProperty(value = "是否已经推送箩筐")
    private String issend;
	/**【江苏高速】事件id*/
	@Excel(name = "【江苏高速】事件id", width = 15)
    @ApiModelProperty(value = "【江苏高速】事件id")
    private String feEventid;
	/**【江苏高速】事件名称*/
	@Excel(name = "【江苏高速】事件名称", width = 15)
    @ApiModelProperty(value = "【江苏高速】事件名称")
    private String feEventname;
	/**【江苏高速】类型id*/
	@Excel(name = "【江苏高速】类型id", width = 15)
    @ApiModelProperty(value = "【江苏高速】类型id")
    private String feTypeid;
	/**【江苏高速】单位id*/
	@Excel(name = "【江苏高速】单位id", width = 15)
    @ApiModelProperty(value = "【江苏高速】单位id")
    private String feUnitid;
	/**【江苏高速】单位名称*/
	@Excel(name = "【江苏高速】单位名称", width = 15)
    @ApiModelProperty(value = "【江苏高速】单位名称")
    private String feUnitname;
	/**【江苏高速】位置id*/
	@Excel(name = "【江苏高速】位置id", width = 15)
    @ApiModelProperty(value = "【江苏高速】位置id")
    private String feSectioncenterid;
	/**【江苏高速】位置名称*/
	@Excel(name = "【江苏高速】位置名称", width = 15)
    @ApiModelProperty(value = "【江苏高速】位置名称")
    private String feSectioncentername;
	/**【江苏高速】*/
	@Excel(name = "【江苏高速】", width = 15)
    @ApiModelProperty(value = "【江苏高速】")
    private String feVcCreatejopnum;
	/**【江苏高速】创建人*/
	@Excel(name = "【江苏高速】创建人", width = 15)
    @ApiModelProperty(value = "【江苏高速】创建人")
    private String feVcCreatename;
	/**【江苏高速】高速id*/
	@Excel(name = "【江苏高速】高速id", width = 15)
    @ApiModelProperty(value = "【江苏高速】高速id")
    private String feVcRoadid;
	/**【江苏高速】高速名称*/
	@Excel(name = "【江苏高速】高速名称", width = 15)
    @ApiModelProperty(value = "【江苏高速】高速名称")
    private String feVcRoadname;
	/**【江苏高速】高速方向*/
	@Excel(name = "【江苏高速】高速方向", width = 15)
    @ApiModelProperty(value = "【江苏高速】高速方向")
    private String feVcRoaddirectio;
	/**【江苏高速】电话号码*/
	@Excel(name = "【江苏高速】电话号码", width = 15)
    @ApiModelProperty(value = "【江苏高速】电话号码")
    private String feVcFromphonenum;
	/**【江苏高速】英里数*/
	@Excel(name = "【江苏高速】英里数", width = 15)
    @ApiModelProperty(value = "【江苏高速】英里数")
    private String feStrMileage;
	/**【江苏高速】位置*/
	@Excel(name = "【江苏高速】位置", width = 15)
    @ApiModelProperty(value = "【江苏高速】位置")
    private String feStrPosition;
	/**【江苏高速】经度*/
	@Excel(name = "【江苏高速】经度", width = 15)
    @ApiModelProperty(value = "【江苏高速】经度")
    private String feVcLongitude;
	/**【江苏高速】纬度*/
	@Excel(name = "【江苏高速】纬度", width = 15)
    @ApiModelProperty(value = "【江苏高速】纬度")
    private String feVcLatitude;
	/**【江苏高速】状态*/
	@Excel(name = "【江苏高速】状态", width = 15)
    @ApiModelProperty(value = "【江苏高速】状态")
    private String feState;
	/**【江苏高速】事件状态*/
	@Excel(name = "【江苏高速】事件状态", width = 15)
    @ApiModelProperty(value = "【江苏高速】事件状态")
    private String feEventstate;
	/**【江苏高速】事故描述*/
	@Excel(name = "【江苏高速】事故描述", width = 15)
    @ApiModelProperty(value = "【江苏高速】事故描述")
    private String feAccidentdesc;
	/**【江苏高速】是否删除*/
	@Excel(name = "【江苏高速】是否删除", width = 15)
    @ApiModelProperty(value = "【江苏高速】是否删除")
    private String feIsdel;
	/**【江苏高速】错误状态*/
	@Excel(name = "【江苏高速】错误状态", width = 15)
    @ApiModelProperty(value = "【江苏高速】错误状态")
    private String feCorrectstate;
	/**【江苏高速】创建时间*/
	@Excel(name = "【江苏高速】创建时间", width = 15)
    @ApiModelProperty(value = "【江苏高速】创建时间")
    private String feCreatetime;
	/**【江苏高速】更新时间*/
	@Excel(name = "【江苏高速】更新时间", width = 15)
    @ApiModelProperty(value = "【江苏高速】更新时间")
    private String feUpdatetime;
	/**【江苏高速】事件处理时间*/
	@Excel(name = "【江苏高速】事件处理时间", width = 15)
    @ApiModelProperty(value = "【江苏高速】事件处理时间")
    private String feEventChuliDate;
}
