package org.lkdt.modules.traffic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lkdt.modules.traffic.entity.TollStationRecorder;

import java.util.List;

/**
 * @Description: 收费站封路事件详细
 * @Author: jeecg-boot
 * @Date:   2021-06-03
 * @Version: V1.0
 */
public interface ITollStationRecorderService extends IService<TollStationRecorder> {

    public List<TollStationRecorder> selectByMainId(String mainId);
}
