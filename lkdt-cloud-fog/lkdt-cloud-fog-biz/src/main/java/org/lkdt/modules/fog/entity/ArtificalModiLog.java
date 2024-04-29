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
import org.lkdt.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 人工数据修改日志表
 * @Author: jeecg-boot
 * @Date:   2021-05-07
 * @Version: V1.0
 */
@Data
@TableName("zc_artifical_modi_log")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="zc_artifical_modi_log对象", description="人工数据修改日志表")
public class ArtificalModiLog implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "主键")
    private String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    @Dict(dictTable = "sys_user",dicText = "realname",dicCode = "id")
    private String createBy;
	/**操作时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "操作时间")
    private Date createTime;
	/**操作用户*/
    @ApiModelProperty(value = "操作用户")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**日志类型【1.摄像头异常设置】【2.能见度设置】*/
	@Excel(name = "日志类型【1.摄像头异常设置】【2.能见度设置】", width = 15)
    @ApiModelProperty(value = "日志类型【1.摄像头异常设置】【2.能见度设置】")
    @Dict(dicCode = "modi_log_type")
    private String logType;
	/**摄像头异常类型【0:正常】【1:】,【2:已处理但未确认】9:停用3: "画面模糊",4: "信号异常",5: "摄像头污损、遮盖",7: "位置偏移",8: "夜间不合规",6: "其它",*/
	@Excel(name = "摄像头异常类型【0:正常】【1:】,【2:已处理但未确认】9:停用3: 画面模糊,4: 信号异常,5: 摄像头污损、遮盖,7: 位置偏移,8: 夜间不合规,6: 其它,", width = 15)
    @ApiModelProperty(value = "摄像头异常类型【0:正常】【1:】,【2:已处理但未确认】9:停用3: 画面模糊,4: 信号异常,5: 摄像头污损、遮盖,7: 位置偏移,8: 夜间不合规,6: 其它,")
    @Dict(dicCode="equipment_state")
    private String exceptionType;
	/**可见距离原始值*/
	@Excel(name = "可见距离原始值", width = 15)
    @ApiModelProperty(value = "可见距离原始值")
    private String artificialAlarmDistanceInit;
	/**可见距离设置*/
	@Excel(name = "可见距离设置", width = 15)
    @ApiModelProperty(value = "可见距离设置")
    private String artificialAlarmDistance;
	/**图片地址*/
	@Excel(name = "图片地址", width = 15)
    @ApiModelProperty(value = "图片地址")
    private String artificialAlarmImgUrl;
	/**用户名称*/
	@Excel(name = "用户名称", width = 15)
    @ApiModelProperty(value = "用户名称")
    private String createUserName;
}
