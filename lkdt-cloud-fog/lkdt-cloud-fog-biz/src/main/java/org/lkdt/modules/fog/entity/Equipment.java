package org.lkdt.modules.fog.entity;

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
 * @Description: 攝像頭設備表
 * @Author: jeecg-boot
 * @Date:   2021-04-23
 * @Version: V1.0
 */
@Data
@TableName("zc_equipment")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="zc_equipment对象", description="攝像頭設備表")
public class Equipment implements Serializable{
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
	/**设备编码*/
	@Excel(name = "设备编码", width = 15)
    @ApiModelProperty(value = "设备编码")
    private String equCode;
	/**设备名称*/
	@Excel(name = "设备名称", width = 15)
    @ApiModelProperty(value = "设备名称")
    private String equName;
	/**设备位置*/
	@Excel(name = "设备位置", width = 15)
    @ApiModelProperty(value = "设备位置")
    private String equLocation;
	/**纬度*/
	@Excel(name = "纬度", width = 15)
    @ApiModelProperty(value = "纬度")
    private String lat;
	/**经度*/
	@Excel(name = "经度", width = 15)
    @ApiModelProperty(value = "经度")
    private String lon;
	/**所属公路*/
	@Excel(name = "所属公路", width = 15)
    @ApiModelProperty(value = "所属公路")
    @Dict(dictTable = "zc_highway",dicText = "name",dicCode = "id")
    private String hwId;
	/**最大值*/
	@Excel(name = "最大值", width = 15)
    @ApiModelProperty(value = "最大值")
    private Integer maxVal;
	/**最小值*/
	@Excel(name = "最小值", width = 15)
    @ApiModelProperty(value = "最小值")
    private Integer minVal;
	/**max_dist*/
	@Excel(name = "max_dist", width = 15)
    @ApiModelProperty(value = "max_dist")
    private Integer maxDist;
	/**是否高危路段*/
	@Excel(name = "是否高危路段", width = 15)
    @ApiModelProperty(value = "是否高危路段")
    @Dict(dicCode = "equipment_isdanger")
    private String isdanger;
	/**夜晚最大值*/
	@Excel(name = "夜晚最大值", width = 15)
    @ApiModelProperty(value = "夜晚最大值")
    private Integer maxValNight;
	/**调整后维度*/
	@Excel(name = "调整后维度", width = 15)
    @ApiModelProperty(value = "调整后维度")
    private String latAfterAdjust;
	/**调整后经度*/
	@Excel(name = "调整后经度", width = 15)
    @ApiModelProperty(value = "调整后经度")
    private String lonAfterAdjust;
	/**state*/
	@Excel(name = "state", width = 15)
    @ApiModelProperty(value = "state")
    @Dict(dicCode = "equipment_state")
    private String state;
	/**感动科技感动科技cameraNum*/
	@Excel(name = "感动科技感动科技cameraNum", width = 15)
    @ApiModelProperty(value = "感动科技感动科技cameraNum")
    private String cameraNum;
	/**save*/
	@Excel(name = "save", width = 15)
    @ApiModelProperty(value = "save")
    @Dict(dicCode = "equipment_save")
    private String save;
	/**清晰度*/
	@Excel(name = "清晰度", width = 15)
    @ApiModelProperty(value = "清晰度")
    private Double clarity;
	/**gd_hw_id*/
	@Excel(name = "gd_hw_id", width = 15)
    @ApiModelProperty(value = "gd_hw_id")
    private String gdHwId;
    /**ownedComputerRoom*/
    @Excel(name = "所属机房", width = 15)
    @ApiModelProperty(value = "ownedComputerRoom")
    private String ownedComputerRoom;
    /**departmentId*/
    @Excel(name = "所属部门", width = 15)
    @ApiModelProperty(value = "departmentId")
    @Dict(dicCode = "id", dictTable = "sys_depart",dicText = "depart_name")
    private String departmentId;

    @TableField(exist = false)
    private String epId;

    @TableField(exist = false)
    private String a;

    @TableField(exist = false)
    private String b;

    @TableField(exist = false)
    private String c;
}
