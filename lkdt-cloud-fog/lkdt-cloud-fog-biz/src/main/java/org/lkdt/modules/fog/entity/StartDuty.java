package org.lkdt.modules.fog.entity;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
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
 * @Description: 值班记录表
 * @Author: jeecg-boot
 * @Date:   2021-06-28
 * @Version: V1.0
 */
@Data
@TableName(value = "zc_start_duty",autoResultMap = true)
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="zc_start_duty对象", description="值班记录表")
public class StartDuty implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "主键")
    private String id;
	/**值班人*/
    @ApiModelProperty(value = "值班人")
    @Dict(dictTable = "sys_user",dicText = "realname",dicCode = "id")
    private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**是否有雾*/
	@Excel(name = "是否有雾", width = 15)
    @Dict(dicCode = "yn")
    @ApiModelProperty(value = "是否有雾")
    private String isfog;
	/**雾类型：雾/团雾/霾/雨雾/其它*/
	@Excel(name = "雾类型：雾/团雾/霾/雨雾/其它", width = 15)
    @Dict(dicCode = "fogs_type")
    @ApiModelProperty(value = "雾类型：雾/团雾/霾/雨雾/其它")
    private String fogType;
	/**状态:1:一开始，未结束，2：已结束*/
	@Excel(name = "状态:1:一开始，未结束，2：已结束", width = 15)
    @ApiModelProperty(value = "状态:1:已开始，未结束，2：已结束")
//    @TableField(typeHandler = FastjsonTypeHandler.class)
    private String type;
	/**开始时间*/
	@Excel(name = "开始时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    private Date startTime;
	/**结束时间*/
	@Excel(name = "结束时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间")
    private Date endTime;
    /**天气预报*/
    @Excel(name = "天气预报", width = 15)
    @ApiModelProperty(value = "天气预报")
    private String weather;
    /**今日雾情*/
    @Excel(name = "今日雾情", width = 15)
    @Dict(dicCode = "yn")
    @ApiModelProperty(value = "今日雾情")
    private String todayIsfog;
    /**是否有雨*/
    @Excel(name = "是否有雨", width = 15)
    @Dict(dicCode = "yn")
    @ApiModelProperty(value = "是否有雨")
    private String israin;
    /**是否有冰雹/0:否1：是*/
    @Excel(name = "是否有冰雹/0:否1：是", width = 15)
    @Dict(dicCode = "yn")
    @ApiModelProperty(value = "是否有冰雹/0:否1：是")
    private String ishail;
    /**是否有雪/0:否1：是*/
    @Excel(name = "是否有雪/0:否1：是", width = 15)
    @Dict(dicCode = "yn")
    @ApiModelProperty(value = "是否有雪/0:否1：是")
    private String issnow;
    /**是否是恶劣天气/0：否1：是*/
    @Excel(name = "是否是恶劣天气/0：否1：是", width = 15)
    @ApiModelProperty(value = "是否是恶劣天气/0：否1：是")
    @Dict(dicCode = "yn")
    private String isItBadWeather;
    /**雨类型*/
    @Excel(name = "雨类型", width = 15)
    @ApiModelProperty(value = "雨类型")
    @Dict(dicCode = "rain_type")
    private String rainType;
    /**低能见度情况汇总*/
    @Excel(name = "低能见度情况汇总", width = 15)
    @ApiModelProperty(value = "低能见度情况汇总")
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONArray  summaryLowVisibility;
    /**交警管制情况汇总*/
    @Excel(name = "交警管制情况汇总", width = 15)
    @ApiModelProperty(value = "交警管制情况汇总")
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONArray summaryTrafficPoliceControl;
    /**雾情播报*/
    @Excel(name = "雾情播报", width = 15)
    @TableField(typeHandler = FastjsonTypeHandler.class)
    @ApiModelProperty(value = "雾情播报")
    private JSONArray fogBroadcast;
    /**日报总结*/
    @Excel(name = "日报总结", width = 15)
    @ApiModelProperty(value = "日报总结")
    private String summaryDaily;
    /**值班心得*/
    @Excel(name = "值班心得", width = 15)
    @ApiModelProperty(value = "值班心得")
    private String dutyTips;
}
