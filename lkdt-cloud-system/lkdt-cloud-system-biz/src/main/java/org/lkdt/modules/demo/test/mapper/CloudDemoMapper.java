package org.lkdt.modules.demo.test.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.lkdt.modules.demo.test.entity.CloudDemo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @Description: jeecg 测试demo
 * @Author: jeecg-boot
 * @Date:  2018-12-29
 * @Version: V1.0
 */
public interface CloudDemoMapper extends BaseMapper<CloudDemo> {

	public List<CloudDemo> getDemoByName(@Param("name") String name);
	
	/**
	 * 查询列表数据 直接传数据权限的sql进行数据过滤
	 * @param page
	 * @param permissionSql
	 * @return
	 */
	public IPage<CloudDemo> queryListWithPermission(Page<CloudDemo> page, @Param("permissionSql")String permissionSql);

}
