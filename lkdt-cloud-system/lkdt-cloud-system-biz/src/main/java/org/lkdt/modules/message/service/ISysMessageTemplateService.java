package org.lkdt.modules.message.service;

import java.util.List;

import org.lkdt.common.system.base.service.CloudService;
import org.lkdt.modules.message.entity.SysMessageTemplate;

/**
 * @Description: 消息模板
 * @Author: jeecg-boot
 * @Date:  2019-04-09
 * @Version: V1.0
 */
public interface ISysMessageTemplateService extends CloudService<SysMessageTemplate> {
    List<SysMessageTemplate> selectByCode(String code);
}
