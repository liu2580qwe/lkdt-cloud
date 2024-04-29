package org.lkdt.modules.demo.test.service;

import org.lkdt.common.system.base.service.CloudService;
import org.lkdt.modules.demo.test.entity.CloudDemo;

import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * @Description: jeecg 测试demo
 * @Author: jeecg-boot
 * @Date:  2018-12-29
 * @Version: V1.0
 */
public interface ICloudDemoService extends CloudService<CloudDemo> {
	
	public void testTran();
	
	public CloudDemo getByIdCacheable(String id);
	
	/**
	 * 查询列表数据 在service中获取数据权限sql信息
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	IPage<CloudDemo> queryListWithPermission(int pageSize, int pageNo);
}
