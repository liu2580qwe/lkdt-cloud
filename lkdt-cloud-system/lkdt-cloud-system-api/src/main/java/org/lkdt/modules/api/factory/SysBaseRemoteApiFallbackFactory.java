package org.lkdt.modules.api.factory;

import org.lkdt.modules.api.SysBaseRemoteApi;
import org.lkdt.modules.api.fallback.SysBaseRemoteApiFallback;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author taoyan
 * @date 2020/05/22
 */
@Component
public class SysBaseRemoteApiFallbackFactory implements FallbackFactory<SysBaseRemoteApi> {

    @Override
    public SysBaseRemoteApi create(Throwable throwable) {
        SysBaseRemoteApiFallback fallback = new SysBaseRemoteApiFallback();
        fallback.setCause(throwable);
        return fallback;
    }

}
