package org.lkdt.modules.demo.test.service;

import java.util.List;

import org.lkdt.modules.demo.test.entity.CloudOrderCustomer;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 订单客户
 * @Author: jeecg-boot
 * @Date:  2019-02-15
 * @Version: V1.0
 */
public interface ICloudOrderCustomerService extends IService<CloudOrderCustomer> {
	
	public List<CloudOrderCustomer> selectCustomersByMainId(String mainId);
}
