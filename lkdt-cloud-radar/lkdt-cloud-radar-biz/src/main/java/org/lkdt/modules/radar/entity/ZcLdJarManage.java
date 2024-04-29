package org.lkdt.modules.radar.entity;

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
 * @Description: JAR包升级表
 * @Author: jeecg-boot
 * @Date:   2021-07-27
 * @Version: V1.0
 */
@Data
@TableName("zc_ld_jar_manage")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="zc_ld_jar_manage对象", description="JAR包升级表")
public class ZcLdJarManage implements Serializable {
    private static final long serialVersionUID = 1L;
	/**主键*/
	@TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "主键")
    private String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人", width = 15)
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    @Excel(name = "创建日期", width = 15)
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    @Excel(name = "更新人", width = 15)
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    @Excel(name = "更新日期", width = 15)
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    @Excel(name = "所属部门", width = 15)
    private String sysOrgCode;
	/**版本号*/
	@Excel(name = "版本号", width = 15)
    @ApiModelProperty(value = "版本号")
    private String version;
	/**文件路径*/
	@Excel(name = "文件路径", width = 15)
    @ApiModelProperty(value = "文件路径")
    private String filePath;
	/**文件名*/
	@Excel(name = "文件名", width = 15)
    @ApiModelProperty(value = "文件名")
    private String fileName;
	/**MD5编码*/
	@Excel(name = "MD5编码", width = 15)
    @ApiModelProperty(value = "MD5编码")
    private String md5Code;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;
}
