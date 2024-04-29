package org.lkdt.modules.wind.entity;

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
 * @Description: gale warning entity
 * @Author: Cai Xibei
 * @Date:   2021-06-02
 * @Version: V1.0
 */
@Data
@TableName("zc_wind")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="zc_wind对象", description="大风")
public class ZcWind implements Serializable {
    private static final long serialVersionUID = 1L;

	/**primary key*/
	@TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "主键")
    private String id;
	/**gale code*/
	@Excel(name = "大风编号", width = 15)
    @ApiModelProperty(value = "大风编号")
    private String windCode;
	/**gale name*/
	@Excel(name = "大风名称", width = 20)
    @ApiModelProperty(value = "大风名称")
    private String windName;
	/**address*/
	@Excel(name = "地址", width = 15)
    @ApiModelProperty(value = "地址")
    private String windLocation;
	/**longitude*/
	@Excel(name = "经度", width = 20)
    @ApiModelProperty(value = "经度")
    private String lat;
	/**dimension*/
	@Excel(name = "维度", width = 15)
    @ApiModelProperty(value = "维度")
    private String lon;
	/**road section*/
	@Excel(name = "路段", width = 15)
    @ApiModelProperty(value = "路段")
    private String hwId;
	/**creation time*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**change time*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更改时间")
    private Date updateTime;
	/**founder*/
	@Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
    private String creator;
	/**modifier*/
	@Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
    private String updater;
	/**state*/
	@Excel(name = "状态", width = 15)
    @ApiModelProperty(value = "状态")
    private String state;
	/**adjoin_epid*/
	@Excel(name = "adjoin_epid", width = 15)
    @ApiModelProperty(value = "adjoin_epid")
    private String adjoinEpid;
}
