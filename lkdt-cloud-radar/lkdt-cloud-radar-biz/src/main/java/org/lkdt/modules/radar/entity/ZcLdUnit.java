package org.lkdt.modules.radar.entity;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.lkdt.common.aspect.annotation.Dict;
import org.lkdt.common.system.vo.DictModel;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @author Cai Xibei
 * @version 1.0.0
 * @project zcloud-boot-parent
 * @ClassName org.lkdt.modules.radar.vo.ZcLdUnit
 * @description 雷达单元类
 * @createTime 2021年07月29日 15:19:00
 */
@ApiModel(value="zc_ld_unit对象", description="雷达单元表")
@Data
@TableName("zc_ld_unit")
public class ZcLdUnit implements Serializable {
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
	/**编码*/
	@Excel(name = "编码", width = 15)
    @ApiModelProperty(value = "编码")
    private String code;
	/**标记*/
	@Excel(name = "标记", width = 15)
    @ApiModelProperty(value = "标记")
    private String remark;
	/**所属道路*/
	@Excel(name = "所属道路", width = 15)
    @Dict(dictTable = "zc_highway",dicCode = "id",dicText = "name")
    @ApiModelProperty(value = "所属道路")
    private String hwId;
	/**VPN地址*/
	@Excel(name = "VPN地址", width = 15)
    @ApiModelProperty(value = "VPN地址")
    private String vpnIp;
	/**ssh端口号*/
	@Excel(name = "ssh端口号", width = 15)
    @ApiModelProperty(value = "ssh端口号")
    private Integer sshPort;
	/**雷达端口*/
	@Excel(name = "雷达端口", width = 15)
    @ApiModelProperty(value = "雷达端口")
    private Integer radarServerPort;
	/**ftp端口*/
	@Excel(name = "ftp端口", width = 15)
    @ApiModelProperty(value = "ftp端口")
    private String ftpPort;
	/**用户名*/
	@Excel(name = "用户名", width = 15)
    @ApiModelProperty(value = "用户名")
    private String username;
	/**密码*/
	@Excel(name = "密码", width = 15)
    @ApiModelProperty(value = "密码")
    private String password;
	/**单元类型*/
	@Excel(name = "单元类型", width = 15)
    @ApiModelProperty(value = "单元类型")
    @Dict(dicCode = "unit_type")
    private String unitType;
    /**单元雷达列表*/
    @Excel(name = "单元雷达列表", width = 15)
    @ApiModelProperty(value = "单元雷达列表")
    @TableField(exist = false)
    private List<ZcLdEquipment> equipments;
    /**单元路段信息列表*/
    @Excel(name = "单元路段信息列表", width = 15)
    @ApiModelProperty(value = "单元路段信息列表")
    @TableField(exist = false)
    private List<ZcLdLaneInfo> zcLdLaneInfos;
    /**雷达事件类型列表*/
    @Excel(name = "雷达事件类型", width = 15)
    @ApiModelProperty(value = "雷达事件类型")
    @TableField(exist = false)
    private List<DictModel> radar_event_types;
    /**车道类型列表*/
    @Excel(name = "车道类型", width = 15)
    @ApiModelProperty(value = "车道类型")
    @TableField(exist = false)
    private List<DictModel> lane_types;
}
