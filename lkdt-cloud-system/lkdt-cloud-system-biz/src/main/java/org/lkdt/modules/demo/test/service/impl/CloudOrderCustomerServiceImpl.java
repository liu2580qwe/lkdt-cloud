package org.lkdt.modules.demo.test.service.impl;

import java.util.List;

import org.lkdt.modules.demo.test.entity.CloudOrderCustomer;
import org.lkdt.modules.demo.test.mapper.CloudOrderCustomerMapper;
import org.lkdt.modules.demo.test.service.ICloudOrderCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 订单客户
 * @Author: jeecg-boot
 * @Date:  2019-02-15
 * @Version: V1.0
 */
@Service
public class CloudOrderCustomerServiceImpl extends ServiceImpl<CloudOrderCustomerMapper, CloudOrderCustomer> implements ICloudOrderCustomerService {

	@Autowired
	private CloudOrderCustomerMapper cloudOrderCustomerMapper;
	
	@Override
	public List<CloudOrderCustomer> selectCustomersByMainId(String mainId) {
		return cloudOrderCustomerMapper.selectCustomersByMainId(mainId);
	}

}
