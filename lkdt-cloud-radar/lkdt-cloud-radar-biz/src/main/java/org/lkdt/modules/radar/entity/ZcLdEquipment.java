package org.lkdt.modules.radar.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.lkdt.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Description: 雷达设备表
 * @Author: jeecg-boot
 * @Date:   2021-03-11
 * @Version: V1.0
 */
@Data
@TableName("zc_ld_radar_equipment")
@ApiModel(value="zc_ld_equipment对象", description="雷达设备表")
public class ZcLdEquipment implements Serializable {
    private static final long serialVersionUID = 1L;
	/**主键*/
	@TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
    /**IP地址*/
    @ApiModelProperty(value = "IP地址")
    private java.lang.String ip;
    /**所属单元*/
    @ApiModelProperty(value = "所属单元")
    private String unitId;
    /**端口号*/
    @ApiModelProperty(value = "端口号")
    private java.lang.Integer port;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
	/**设备编号*/
    @Excel(name = "设备编号", width = 15)
    @ApiModelProperty(value = "设备编号")
    private java.lang.String equCode;
	/**设备名称*/
    @Excel(name = "设备名称", width = 15)
    @ApiModelProperty(value = "设备名称")
    private java.lang.String equName;
    /**视频web*/
    @Excel(name = "视频web", width = 15)
    @ApiModelProperty(value = "视频web")
    private java.lang.String videoWeb;
    /**视频链接*/
    @Excel(name = "视频链接", width = 15)
    @ApiModelProperty(value = "视频链接")
    private java.lang.String videoUrl;
	/**设备位置*/
    @Excel(name = "设备位置", width = 15)
    @ApiModelProperty(value = "设备位置")
    private java.lang.String equLocation;
	/**纬度*/
    @Excel(name = "纬度", width = 15)
    @ApiModelProperty(value = "纬度")
    private java.lang.String lat;
	/**经度*/
    @Excel(name = "经度", width = 15)
    @ApiModelProperty(value = "经度")
    private java.lang.String lon;
	/**所属路段*/
    @Excel(name = "所属路段", width = 15)
    @ApiModelProperty(value = "所属路段")
    @Dict(dictTable = "zc_highway",dicCode = "id",dicText = "name")
    private java.lang.String hwId;
	/**设备类型*/
    @Excel(name = "设备类型", width = 15)
    @ApiModelProperty(value = "设备类型")
    private java.lang.String equType;
	/**监测方向*/
    @Excel(name = "监测方向", width = 15)
    @ApiModelProperty(value = "监测方向")
    private java.lang.String direction;
	/**监测车道数*/
    @Excel(name = "监测车道数", width = 15)
    @ApiModelProperty(value = "监测车道数")
    private java.lang.Integer lane;
	/**隔离带车道*/
    @Excel(name = "隔离带车道", width = 15)
    @ApiModelProperty(value = "隔离带车道")
    private java.lang.Integer medianStrip;
	/**设备属性*/
    @Excel(name = "设备属性", width = 15)
    @ApiModelProperty(value = "设备属性")
    private java.lang.String equAttribute;
    /**安装高度*/
    @Excel(name = "安装高度", width = 15)
    @ApiModelProperty(value = "安装高度")
    private java.lang.Double installationHeight ;
    /**角度修正*/
    @Excel(name = "角度修正", width = 15)
    @ApiModelProperty(value = "角度修正")
    private java.lang.Double angleCorrection ;
    /**坐标修正*/
    @Excel(name = "坐标修正", width = 15)
    @ApiModelProperty(value = "坐标修正")
    private java.lang.Double coordinateCorrection ;
    /**雷达安装位置距离最外侧应急车道边线宽度*/
    @Excel(name = "雷达安装位置距离最外侧应急车道边线宽度", width = 15)
    @ApiModelProperty(value = "雷达安装位置距离最外侧应急车道边线宽度")
    private java.lang.String radarSideLine ;
    /**雷达安装位置处于雷达监测方向的哪一侧（L:左侧；R:右侧；M:中间）*/
    @Excel(name = "雷达安装位置处于雷达监测方向的哪一侧（L:左侧；R:右侧；M:中间）", width = 15)
    @ApiModelProperty(value = "雷达安装位置处于雷达监测方向的哪一侧（L:左侧；R:右侧；M:中间）")
    private java.lang.String radarInstallLaneDirection ;
    /**雷达数据入库取值雷达有效距离的最小值*/
    @Excel(name = "雷达数据入库取值雷达有效距离的最小值", width = 15)
    @ApiModelProperty(value = "雷达数据入库取值雷达有效距离的最小值")
    private java.lang.Double dataSyMin ;
    /**雷达数据入库取值雷达有效距离的最大值*/
    @Excel(name = "雷达数据入库取值雷达有效距离的最大值", width = 15)
    @ApiModelProperty(value = "雷达数据入库取值雷达有效距离的最大值")
    private java.lang.Double dataSyMax ;
    /**计算两速三急事件取雷达有效距离的的最小值*/
    @Excel(name = "计算两速三急事件取雷达有效距离的的最小值", width = 15)
    @ApiModelProperty(value = "计算两速三急事件取雷达有效距离的的最小值")
    private java.lang.Double eventSyMin ;
    /**计算两速三急事件取雷达有效距离的的最大值*/
    @Excel(name = "计算两速三急事件取雷达有效距离的的最大值", width = 15)
    @ApiModelProperty(value = "计算两速三急事件取雷达有效距离的的最大值")
    private java.lang.Double eventSyMax ;
    /**计算车道中心线、车道标线的雷达坐标修正*/
    @Excel(name = "计算车道中心线、车道标线的雷达坐标修正", width = 15)
    @ApiModelProperty(value = "计算车道中心线、车道标线的雷达坐标修正")
    private java.lang.Double laneCoordinateCorrection ;
    /**拟合车道标线，Y值的取值范围*/
    @Excel(name = "拟合车道标线，Y值的取值范围", width = 15)
    @ApiModelProperty(value = "拟合车道标线，Y值的取值范围")
    private java.lang.String polynomialY ;
    
    /**雷达类型（单元雷达：101，节点匝道雷达102，节点主路雷达103）*/
    @Excel(name = "雷达类型（单元雷达：101，节点匝道雷达102，节点主路雷达103）", width = 15)
    @ApiModelProperty(value = "雷达类型（单元雷达：101，节点匝道雷达102，节点主路雷达103）")
    private java.lang.String radarType ;
    /**节点雷达ID，同一个节点的主路雷达和匝道雷达绑定同一个节点ID*/
    @Excel(name = "节点雷达ID，同一个节点的主路雷达和匝道雷达绑定同一个节点ID", width = 15)
    @ApiModelProperty(value = "节点雷达ID，同一个节点的主路雷达和匝道雷达绑定同一个节点ID")
    private java.lang.String nodeId ;
    
    /**三态系数是否入库开关*/
    @Excel(name = "三态系数是否入库开关。O(open)：打开；C(close)：关闭", width = 15)
    @ApiModelProperty(value = "三态系数是否入库开关。O(open)：打开；C(close)：关闭")
    private java.lang.String coefficientIsSave ;
    /**三态系数入库时间开始*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "三态系数入库时间开始")
    private java.util.Date saveTimeStart;
    /**三态系数入库时间结束*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "三态系数入库时间结束")
    private java.util.Date saveTimeEnd;
    /**三态系数是否入库开关*/
    @Excel(name = "是否计算三态风险开关。O(open)：打开；C(close)：关闭", width = 15)
    @ApiModelProperty(value = "是否计算三态风险开关。O(open)：打开；C(close)：关闭")
    private java.lang.String isCalculate ;
    
    
    
    
    /**摄像头列表*/
    @Excel(name = "摄像头列表", width = 15)
    @ApiModelProperty(value = "摄像头列表")
    @TableField(exist = false)
    private List<ZcLdRadarVideo> videos;
    /**大风列表*/
    @TableField(exist = false)
    private List<ZcLdWind> winds;
}
