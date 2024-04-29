package org.lkdt.modules.fog.entity;

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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 路段告警与封路信息关系表
 * @Author: jeecg-boot
 * @Date:   2021-06-03
 * @Version: V1.0
 */
@Data
@TableName("zc_alarm_road_control_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="zc_alarm_road_control_info对象", description="路段告警与封路信息关系表")
public class AlarmRoadControlInfo implements Serializable {
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
	/**告警路段ID*/
	@Excel(name = "告警路段ID", width = 15)
    @ApiModelProperty(value = "告警路段ID")
    private String roadAlarmId;
	/**路段告警类型（1：雾，2：团雾，3：雨，4：雪，5：霾）*/
	@Excel(name = "路段告警类型（1：雾，2：团雾，3：雨，4：雪，5：霾）", width = 15)
    @ApiModelProperty(value = "路段告警类型（1：雾，2：团雾，3：雨，4：雪，5：霾）")
    private String roadAlarmType;
	/**路段管制ID*/
	@Excel(name = "路段管制ID", width = 15)
    @ApiModelProperty(value = "路段管制ID")
    private String recordeid;
	/**收费站编号*/
	@Excel(name = "收费站编号", width = 15)
    @ApiModelProperty(value = "收费站编号")
    private String tsnum;
	/**收费站名称*/
	@Excel(name = "收费站名称", width = 15)
    @ApiModelProperty(value = "收费站名称")
    private String tsname;
	/**高速编号*/
	@Excel(name = "高速编号", width = 15)
    @ApiModelProperty(value = "高速编号")
    private String roadnum;
	/**高速名称*/
	@Excel(name = "高速名称", width = 15)
    @ApiModelProperty(value = "高速名称")
    private String roadname;
	/**收费站方向*/
	@Excel(name = "收费站方向", width = 15)
    @ApiModelProperty(value = "收费站方向")
    private String direction;
	/**经度*/
	@Excel(name = "经度", width = 15)
    @ApiModelProperty(value = "经度")
    private String lon;
	/**纬度*/
	@Excel(name = "纬度", width = 15)
    @ApiModelProperty(value = "纬度")
    private String lat;
	/**0 恶劣天气， 1 交通事故， 3 诱导分流， 4 养护施工， 5 其他*/
	@Excel(name = "0 恶劣天气， 1 交通事故， 3 诱导分流， 4 养护施工， 5 其他", width = 15)
    @ApiModelProperty(value = "0 恶劣天气， 1 交通事故， 3 诱导分流， 4 养护施工， 5 其他")
    private String closereason;
	/**内容*/
	@Excel(name = "内容", width = 15)
    @ApiModelProperty(value = "内容")
    private String closereasontext;
	/**描述*/
	@Excel(name = "描述", width = 15)
    @ApiModelProperty(value = "描述")
    private String description;
	/**出入口 0：入口，1：出口*/
	@Excel(name = "出入口 0：入口，1：出口", width = 15)
    @ApiModelProperty(value = "出入口 0：入口，1：出口")
    private String io;
    /**开始时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    private Date startDate;
    /**结束时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间")
    private Date endDate;


    /**等级*/
    @Excel(name = "描述", width = 15)
    @ApiModelProperty(value = "描述")
    private String alarmlevel;
    /**等级*/
    @Excel(name = "描述", width = 15)
    @ApiModelProperty(value = "描述")
    private String mindistancenow;

    /**等级*/
    @Excel(name = "描述", width = 15)
    @ApiModelProperty(value = "描述")
    private String mindistancehis;

}
