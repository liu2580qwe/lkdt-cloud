package org.lkdt.modules.radar.supports.radarDataService.radarCamUnion.service;

import java.util.List;

/**
 * 截图服务
 * @param <T>
 */
public interface CameraService<T> {

    public void init(List<T> ts);

    public void batchSnapPic(List<T> ts);

    public void init(T t);

    public void batchDestroy(List<T> ts);

    public void destroy(T t);

}
