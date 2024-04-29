package org.lkdt.modules.traffic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lkdt.modules.traffic.entity.TollStationEvent;
import org.lkdt.modules.traffic.entity.TollStationRecorder;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 收费站封路事件
 * @Author: jeecg-boot
 * @Date:   2021-06-03
 * @Version: V1.0
 */
public interface ITollStationEventService extends IService<TollStationEvent> {

    /**
     * 添加一对多
     *
     */
    public void saveMain(TollStationEvent TollStationEvent, List<TollStationRecorder> tollStationRecorderList) ;

    /**
     * 修改一对多
     *
     */
    public void updateMain(TollStationEvent TollStationEvent,List<TollStationRecorder> tollStationRecorderList);

    /**
     * 删除一对多
     */
    public void delMain (String id);

    /**
     * 批量删除一对多
     */
    public void delBatchMain (Collection<? extends Serializable> idList);
}
