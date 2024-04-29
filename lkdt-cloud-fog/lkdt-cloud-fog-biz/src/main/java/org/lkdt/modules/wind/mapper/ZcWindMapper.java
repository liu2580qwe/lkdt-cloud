package org.lkdt.modules.wind.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.lkdt.modules.wind.domain.WindDO;
import org.lkdt.modules.wind.entity.ZcWind;
import java.util.List;
import java.util.Map;
/**
 * @Description: gale warning persistence layer interface
 * @Author: Cai Xibei
 * @Date:   2021-06-02
 * @Version: V1.0
 */
public interface ZcWindMapper extends BaseMapper<ZcWind> {
    List<WindDO> listWindAlarm(Map<String,Object> map);
    WindDO get(String windId);
}
