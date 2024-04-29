package org.lkdt.modules.demo.test.service.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.lkdt.modules.demo.test.entity.CloudOrderCustomer;
import org.lkdt.modules.demo.test.entity.CloudOrderMain;
import org.lkdt.modules.demo.test.entity.CloudOrderTicket;
import org.lkdt.modules.demo.test.mapper.CloudOrderCustomerMapper;
import org.lkdt.modules.demo.test.mapper.CloudOrderMainMapper;
import org.lkdt.modules.demo.test.mapper.CloudOrderTicketMapper;
import org.lkdt.modules.demo.test.service.ICloudOrderMainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 订单
 * @Author: jeecg-boot
 * @Date:  2019-02-15
 * @Version: V1.0
 */
@Service
public class CloudOrderMainServiceImpl extends ServiceImpl<CloudOrderMainMapper, CloudOrderMain> implements ICloudOrderMainService {

    @Autowired
    private CloudOrderMainMapper cloudOrderMainMapper;
    @Autowired
    private CloudOrderCustomerMapper cloudOrderCustomerMapper;
    @Autowired
    private CloudOrderTicketMapper cloudOrderTicketMapper;

    @Override
    @Transactional
    public void saveMain(CloudOrderMain jeecgOrderMain, List<CloudOrderCustomer> jeecgOrderCustomerList, List<CloudOrderTicket> jeecgOrderTicketList) {
        cloudOrderMainMapper.insert(jeecgOrderMain);
        if (jeecgOrderCustomerList != null) {
            for (CloudOrderCustomer entity : jeecgOrderCustomerList) {
                entity.setOrderId(jeecgOrderMain.getId());
                cloudOrderCustomerMapper.insert(entity);
            }
        }
        if (jeecgOrderTicketList != null) {
            for (CloudOrderTicket entity : jeecgOrderTicketList) {
                entity.setOrderId(jeecgOrderMain.getId());
                cloudOrderTicketMapper.insert(entity);
            }
        }
    }

    @Override
    @Transactional
    public void updateMain(CloudOrderMain jeecgOrderMain, List<CloudOrderCustomer> jeecgOrderCustomerList, List<CloudOrderTicket> jeecgOrderTicketList) {
        cloudOrderMainMapper.updateById(jeecgOrderMain);

        //1.先删除子表数据
        cloudOrderTicketMapper.deleteTicketsByMainId(jeecgOrderMain.getId());
        cloudOrderCustomerMapper.deleteCustomersByMainId(jeecgOrderMain.getId());

        //2.子表数据重新插入
        if (jeecgOrderCustomerList != null) {
            for (CloudOrderCustomer entity : jeecgOrderCustomerList) {
                entity.setOrderId(jeecgOrderMain.getId());
                cloudOrderCustomerMapper.insert(entity);
            }
        }
        if (jeecgOrderTicketList != null) {
            for (CloudOrderTicket entity : jeecgOrderTicketList) {
                entity.setOrderId(jeecgOrderMain.getId());
                cloudOrderTicketMapper.insert(entity);
            }
        }
    }

	@Override
	@Transactional
	public void delMain(String id) {
		cloudOrderMainMapper.deleteById(id);
		cloudOrderTicketMapper.deleteTicketsByMainId(id);
		cloudOrderCustomerMapper.deleteCustomersByMainId(id);
	}

	@Override
	@Transactional
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			cloudOrderMainMapper.deleteById(id);
			cloudOrderTicketMapper.deleteTicketsByMainId(id.toString());
			cloudOrderCustomerMapper.deleteCustomersByMainId(id.toString());
		}
	}

}
