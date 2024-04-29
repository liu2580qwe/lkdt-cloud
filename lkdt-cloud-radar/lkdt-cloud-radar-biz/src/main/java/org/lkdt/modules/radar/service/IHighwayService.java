package org.lkdt.modules.radar.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lkdt.modules.radar.entity.Highway;

import java.util.List;

/**
 * @Description: zc_highway
 * @Author: jeecg-boot
 * @Date:   2021-04-23
 * @Version: V1.0
 */
public interface IHighwayService extends IService<Highway> {

	/**根节点父ID的值*/
	public static final String ROOT_PID_VALUE = "0";
	
	/**树节点有子节点状态值*/
	public static final String HASCHILD = "1";
	
	/**树节点无子节点状态值*/
	public static final String NOCHILD = "0";

	/**新增节点*/
	void addZcHighway(Highway zcHighway);
	
	/**修改节点*/
	void updateZcHighway(Highway zcHighway) throws Exception;
	
	/**删除节点*/
	void deleteZcHighway(String id) throws Exception, Exception;

	/**查询道路集合*/
	List selectZcHighWayList();

	public List<String> queryChildNodesByHwId(String hwId);

}
