package org.lkdt;

import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.lkdt.modules.demo.mock.MockController;
import org.lkdt.modules.demo.test.entity.CloudDemo;
import org.lkdt.modules.demo.test.mapper.CloudDemoMapper;
import org.lkdt.modules.demo.test.service.ICloudDemoService;
import org.lkdt.modules.system.service.ISysDataLogService;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SampleTest {

	@Resource
	private CloudDemoMapper cloudDemoMapper;
	@Resource
	private ICloudDemoService jeecgDemoService;
	@Resource
	private ISysDataLogService sysDataLogService;
	@Resource
	private MockController mock;

	@org.junit.jupiter.api.Test
	public void testSelect() {
		System.out.println(("----- selectAll method test ------"));
		List<CloudDemo> userList = cloudDemoMapper.selectList(null);
		Assert.assertEquals(5, userList.size());
		userList.forEach(System.out::println);
	}

	@org.junit.jupiter.api.Test
	public void testXmlSql() {
		System.out.println(("----- selectAll method test ------"));
		List<CloudDemo> userList = cloudDemoMapper.getDemoByName("Sandy12");
		userList.forEach(System.out::println);
	}

	/**
	 * 测试事务
	 */
	@org.junit.jupiter.api.Test
	public void testTran() {
		jeecgDemoService.testTran();
	}
	
	//author:lvdandan-----date：20190315---for:添加数据日志测试----
	/**
	 * 测试数据日志添加
	 */
	@Test
	public void testDataLogSave() {
		System.out.println(("----- datalog test ------"));
		String tableName = "jeecg_demo";
		String dataId = "4028ef81550c1a7901550c1cd6e70001";
		String dataContent = mock.sysDataLogJson();
		sysDataLogService.addDataLog(tableName, dataId, dataContent);
	}
	//author:lvdandan-----date：20190315---for:添加数据日志测试----
}
