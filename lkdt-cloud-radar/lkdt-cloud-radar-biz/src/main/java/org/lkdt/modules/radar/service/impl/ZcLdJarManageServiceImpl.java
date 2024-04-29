package org.lkdt.modules.radar.service.impl;
import org.lkdt.modules.radar.entity.ZcLdJarManage;
import org.lkdt.modules.radar.mapper.ZcLdJarManageMapper;
import org.lkdt.modules.radar.service.IZcLdJarManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
/**
 * @Description: JAR包升级表
 * @Author: jeecg-boot
 * @Date:   2021-07-27
 * @Version: V1.0
 */
@Service
public class ZcLdJarManageServiceImpl extends ServiceImpl<ZcLdJarManageMapper, ZcLdJarManage> implements IZcLdJarManageService {
    @Autowired
    private ZcLdJarManageMapper zcLdJarManageMapper;

    @Override
    public ZcLdJarManage selectById(String id){
        return zcLdJarManageMapper.selectById(id);
    }

}
