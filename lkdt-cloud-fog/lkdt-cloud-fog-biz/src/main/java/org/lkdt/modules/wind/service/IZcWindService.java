package org.lkdt.modules.wind.service;
import com.baomidou.mybatisplus.extension.service.IService;
import org.lkdt.modules.wind.domain.WindDO;
import org.lkdt.modules.wind.entity.ZcWind;
import java.util.List;
import java.util.Map;
/**
 * @Description: gale warning server interface
 * @Author: Cai Xibei
 * @Date:   2021-06-02
 * @Version: V1.0
 */
public interface IZcWindService extends IService<ZcWind> {
    List<WindDO> listWindAlarm(Map<String, Object> map);
}
