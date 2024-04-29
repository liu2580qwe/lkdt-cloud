package org.lkdt.modules.demo.test.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.lkdt.modules.demo.test.entity.CloudOrderCustomer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 订单客户
 * @Author: jeecg-boot
 * @Date:  2019-02-15
 * @Version: V1.0
 */
public interface CloudOrderCustomerMapper extends BaseMapper<CloudOrderCustomer> {
	
	/**
	 *  通过主表外键批量删除客户
	 * @param mainId
	 * @return
	 */
    @Delete("DELETE FROM JEECG_ORDER_CUSTOMER WHERE ORDER_ID = #{mainId}")
	public boolean deleteCustomersByMainId(String mainId);
    
    @Select("SELECT * FROM JEECG_ORDER_CUSTOMER WHERE ORDER_ID = #{mainId}")
	public List<CloudOrderCustomer> selectCustomersByMainId(String mainId);
}
