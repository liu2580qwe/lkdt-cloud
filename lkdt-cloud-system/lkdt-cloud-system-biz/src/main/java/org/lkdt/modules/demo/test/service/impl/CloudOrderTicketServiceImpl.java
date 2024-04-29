package org.lkdt.modules.demo.test.service.impl;

import java.util.List;

import org.lkdt.modules.demo.test.entity.CloudOrderTicket;
import org.lkdt.modules.demo.test.mapper.CloudOrderTicketMapper;
import org.lkdt.modules.demo.test.service.ICloudOrderTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 订单机票
 * @Author: jeecg-boot
 * @Date:  2019-02-15
 * @Version: V1.0
 */
@Service
public class CloudOrderTicketServiceImpl extends ServiceImpl<CloudOrderTicketMapper, CloudOrderTicket> implements ICloudOrderTicketService {
	@Autowired
	private CloudOrderTicketMapper cloudOrderTicketMapper;
	
	@Override
	public List<CloudOrderTicket> selectTicketsByMainId(String mainId) {
		return cloudOrderTicketMapper.selectTicketsByMainId(mainId);
	}

}
