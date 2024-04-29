package org.lkdt.modules.demo.test.service;

import java.util.List;

import org.lkdt.modules.demo.test.entity.CloudOrderTicket;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 订单机票
 * @Author: jeecg-boot
 * @Date:  2019-02-15
 * @Version: V1.0
 */
public interface ICloudOrderTicketService extends IService<CloudOrderTicket> {
	
	public List<CloudOrderTicket> selectTicketsByMainId(String mainId);
}
