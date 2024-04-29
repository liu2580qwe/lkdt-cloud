package org.lkdt.modules.radar.vo;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import org.lkdt.modules.radar.entity.ZcLdEquipment;
import lombok.Data;
import org.lkdt.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @Description: 雷达单元表
 * @Author: jeecg-boot
 * @Date:   2021-07-23
 * @Version: V1.0
 */
@Data
@ApiModel(value="zc_ld_unitPage对象", description="雷达单元表")
public class ZcLdUnitPage {
	/**主键*/
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
	@ExcelCollection(name="雷达设备表")
	@ApiModelProperty(value = "雷达设备表")
	private List<ZcLdEquipment> zcLdRadarEquipmentList;
	/**单元类型*/
	@Excel(name = "单元类型", width = 15)
	@ApiModelProperty(value = "单元类型")
	@Dict(dicCode = "unit_type")
	private String unitType;
}
