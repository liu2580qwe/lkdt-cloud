package org.lkdt.modules.wind.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lkdt.modules.wind.domain.WindDO;
import org.lkdt.modules.wind.entity.ZcWind;
import org.lkdt.modules.wind.mapper.ZcWindMapper;
import org.lkdt.modules.wind.service.IZcWindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
/**
 * @Description: gale warning service layer realization
 * @Author: Cai Xibei
 * @Date:   2021-06-02
 * @Version: V1.0
 */
@Service
public class ZcWindServiceImpl extends ServiceImpl<ZcWindMapper, ZcWind> implements IZcWindService {
    @Autowired
    private ZcWindMapper zcWindMapper;

    @Override
    public List<WindDO> listWindAlarm(Map<String, Object> map){
        return zcWindMapper.listWindAlarm(map);
    }
}
