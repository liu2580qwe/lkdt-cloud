package org.lkdt.modules.message.service.impl;

import org.lkdt.common.system.base.service.impl.CloudServiceImpl;
import org.lkdt.modules.message.entity.SysMessage;
import org.lkdt.modules.message.mapper.SysMessageMapper;
import org.lkdt.modules.message.service.ISysMessageService;
import org.springframework.stereotype.Service;

/**
 * @Description: 消息
 * @Author: jeecg-boot
 * @Date:  2019-04-09
 * @Version: V1.0
 */
@Service
public class SysMessageServiceImpl extends CloudServiceImpl<SysMessageMapper, SysMessage> implements ISysMessageService {

}
