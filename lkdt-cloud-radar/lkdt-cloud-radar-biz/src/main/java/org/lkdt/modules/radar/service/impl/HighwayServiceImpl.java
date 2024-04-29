package org.lkdt.modules.radar.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lkdt.modules.radar.entity.Highway;
import org.lkdt.modules.radar.mapper.HighwayMapper;
import org.lkdt.modules.radar.service.IHighwayService;
import org.lkdt.common.util.StringUtils;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 * @Description: zc_highway
 * @Author: jeecg-boot
 * @Date:   2021-04-23
 * @Version: V1.0
 */
@Service
public class HighwayServiceImpl extends ServiceImpl<HighwayMapper, Highway> implements IHighwayService {

	@Override
	public void addZcHighway(Highway zcHighway) {
		if(StringUtils.isEmpty(zcHighway.getPid())){
			zcHighway.setPid(IHighwayService.ROOT_PID_VALUE);
		}else{
			//如果当前节点父ID不为空 则设置父节点的hasChildren 为1
			Highway parent = baseMapper.selectById(zcHighway.getPid());
			if(parent!=null && !"1".equals(parent.getHasChild())){
				parent.setHasChild("1");
				baseMapper.updateById(parent);
			}
		}
		baseMapper.insert(zcHighway);
	}
	
	@Override
	public void updateZcHighway(Highway zcHighway) throws Exception {
		Highway entity = this.getById(zcHighway.getId());
		if(entity==null) {
			throw new Exception("未找到对应实体");
		}
		String old_pid = entity.getPid();
		String new_pid = zcHighway.getPid();
		if(!old_pid.equals(new_pid)) {
			updateOldParentNode(old_pid);
			if(StringUtils.isEmpty(new_pid)){
				zcHighway.setPid(IHighwayService.ROOT_PID_VALUE);
			}
			if(!IHighwayService.ROOT_PID_VALUE.equals(zcHighway.getPid())) {
				baseMapper.updateTreeNodeStatus(zcHighway.getPid(), IHighwayService.HASCHILD);
			}
		}
		baseMapper.updateById(zcHighway);
	}
	
	@Override
	public void deleteZcHighway(String id) throws Exception {
		Highway zcHighway = this.getById(id);
		if(zcHighway==null) {
			throw new Exception("未找到对应实体");
		}
		updateOldParentNode(zcHighway.getPid());
		baseMapper.deleteById(id);
	}

	@Override
	public List selectZcHighWayList() {
		return baseMapper.selectList(new QueryWrapper<Highway>());
	}

	/**
	 * 根据hwId查询子节点
	 * @param hwId
	 * @return
	 */
	@Override
	public List<String> queryChildNodesByHwId(String hwId) {
		return baseMapper.queryChildNodesByHwId(hwId);
	}


	/**
	 * 根据所传pid查询旧的父级节点的子节点并修改相应状态值
	 * @param pid
	 */
	private void updateOldParentNode(String pid) {
		if(!IHighwayService.ROOT_PID_VALUE.equals(pid)) {
			Integer count = baseMapper.selectCount(new QueryWrapper<Highway>().eq("pid", pid));
			if(count==null || count<=1) {
				baseMapper.updateTreeNodeStatus(pid, IHighwayService.NOCHILD);
			}
		}
	}

}
