package org.lkdt.modules.fog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.lkdt.modules.fog.entity.AlertThreepart;

import java.util.List;
import java.util.Map;

/**
 * @Description: 三方告警信息
 * @Author: jeecg-boot
 * @Date:   2021-04-27
 * @Version: V1.0
 */
public interface AlertThreepartMapper extends BaseMapper<AlertThreepart> {

    List<AlertThreepart> listByParams(Map<String, Object> params);
}
