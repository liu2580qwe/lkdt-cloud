package org.lkdt.common.system.base.service.impl;

import org.lkdt.common.system.base.entity.CloudEntity;
import org.lkdt.common.system.base.service.CloudService;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: ServiceImpl基类
 * @Author: dangzhenghui@163.com
 * @Date: 2019-4-21 8:13
 * @Version: 1.0
 */
@Slf4j
public class CloudServiceImpl<M extends BaseMapper<T>, T extends CloudEntity> extends ServiceImpl<M, T> implements CloudService<T> {

}
