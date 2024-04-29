package org.lkdt.modules.weixin.entity;

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
 * @Description: 微信用户管理
 * @Author: hjy
 * @Date:   2021-04-23
 * @Version: V1.0
 */
@Data
@TableName("zc_wx_user")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="zc_wx_user对象", description="微信用户管理")
public class WxUser implements Serializable {
    private static final long serialVersionUID = 1L;

	/**
     * TODO: 当type为以下三种类型时会自动填充：
     *  IdType.ID_WORKER_STR、
     *  IdType.UUID、
     *  IdType.ID_WORKER
     *  更改之前为IdType.INPUT
     * 主键
     * */
	@TableId(type = IdType.UUID)
    @ApiModelProperty(value = "主键")
    private String openid;
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
	/**角色*/
	@Excel(name = "角色", width = 15)
    @Dict(dicCode = "weixin_user_role")
    @ApiModelProperty(value = "角色")
    private String role;
	/**警号*/
	@Excel(name = "警号", width = 15)
    @ApiModelProperty(value = "警号")
    private String policeId;
	/**姓名*/
	@Excel(name = "姓名", width = 15)
    @ApiModelProperty(value = "姓名")
    private String name;
	/**手机号*/
	@Excel(name = "手机号", width = 15)
    @ApiModelProperty(value = "手机号")
    private String phone;
	/**昵称*/
	@Excel(name = "昵称", width = 15)
    @ApiModelProperty(value = "昵称")
    private String nickname;
	/**unionid*/
	@Excel(name = "unionid", width = 15)
    @ApiModelProperty(value = "unionid")
    private String unionid;
	/**minopenid*/
	@Excel(name = "minopenid", width = 15)
    @ApiModelProperty(value = "minopenid")
    private String minopenid;
}
