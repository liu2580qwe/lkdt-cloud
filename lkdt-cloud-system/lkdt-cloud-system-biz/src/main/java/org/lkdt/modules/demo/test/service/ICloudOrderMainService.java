package org.lkdt.modules.demo.test.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.lkdt.modules.demo.test.entity.CloudOrderCustomer;
import org.lkdt.modules.demo.test.entity.CloudOrderMain;
import org.lkdt.modules.demo.test.entity.CloudOrderTicket;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 订单
 * @Author: jeecg-boot
 * @Date:  2019-02-15
 * @Version: V1.0
 */
public interface ICloudOrderMainService extends IService<CloudOrderMain> {

	/**
	 * 添加一对多
	 * 
	 */
	public void saveMain(CloudOrderMain jeecgOrderMain, List<CloudOrderCustomer> jeecgOrderCustomerList, List<CloudOrderTicket> jeecgOrderTicketList) ;
	
	/**
	 * 修改一对多
	 * 
	 */
	public void updateMain(CloudOrderMain jeecgOrderMain, List<CloudOrderCustomer> jeecgOrderCustomerList, List<CloudOrderTicket> jeecgOrderTicketList);
	
	/**
	 * 删除一对多
	 * @param jformOrderMain
	 */
	public void delMain (String id);
	
	/**
	 * 批量删除一对多
	 * @param jformOrderMain
	 */
	public void delBatchMain (Collection<? extends Serializable> idList);
}
