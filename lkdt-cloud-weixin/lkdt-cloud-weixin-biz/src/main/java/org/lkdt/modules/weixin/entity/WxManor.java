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
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 微信用户路段管理
 * @Author: jeecg-boot
 * @Date:   2021-04-26
 * @Version: V1.0
 */
@Data
@TableName("zc_wx_manor")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="zc_wx_manor对象", description="微信用户路段管理")
public class WxManor implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.INPUT)
    @ApiModelProperty(value = "主键")
    private java.lang.String openid;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
	/**所属公众号*/
	@Excel(name = "所属公众号", width = 15)
    @ApiModelProperty(value = "所属公众号")
    private java.lang.String tousername;
	/**绑定路段ID*/
	@Excel(name = "绑定路段ID", width = 15)
    @ApiModelProperty(value = "绑定路段ID")
    private java.lang.String subscribeHighway;
}
