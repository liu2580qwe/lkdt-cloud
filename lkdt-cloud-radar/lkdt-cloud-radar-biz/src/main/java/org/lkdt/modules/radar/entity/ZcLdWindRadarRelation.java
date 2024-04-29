package org.lkdt.modules.radar.entity;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
/**
 *  大风与雷达关联关系表
 * @project org.lkdt.modules.radar.entity.ZcLdWindRadarRelation
 * @package org.lkdt.modules.radar.entity
 * @className ZcLdWindRadarRelation
 * @author Cai Xibei
 * @version 1.0.0
 * @createTime 2021/8/17 15:11
 */
@Data
@TableName("zc_ld_wind_radar_relation")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="zc_ld_wind_radar_relation对象", description="大风与雷达关联关系表")
public class ZcLdWindRadarRelation implements Serializable {
    private static final long serialVersionUID = 1L;
	/**主键*/
	@TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "主键")
    private String id;
	/**大风ID*/
	@Excel(name = "大风ID", width = 15)
    @ApiModelProperty(value = "大风ID")
    private String windId;
	/**雷达ID*/
	@Excel(name = "雷达ID", width = 15)
    @ApiModelProperty(value = "雷达ID")
    private String radarId;
}
