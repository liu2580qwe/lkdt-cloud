package org.lkdt.modules.radar.service;
import org.lkdt.modules.radar.entity.ZcLdJarManage;
import com.baomidou.mybatisplus.extension.service.IService;
/**
 * @Description: JAR包升级表
 * @Author: jeecg-boot
 * @Date:   2021-07-27
 * @Version: V1.0
 */
public interface IZcLdJarManageService extends IService<ZcLdJarManage> {
    public ZcLdJarManage selectById(String id);
}
