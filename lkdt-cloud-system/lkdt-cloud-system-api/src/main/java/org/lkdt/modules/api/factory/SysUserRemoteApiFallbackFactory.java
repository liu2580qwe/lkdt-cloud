package org.lkdt.modules.api.factory;

import org.lkdt.modules.api.SysUserRemoteApi;
import org.lkdt.modules.api.fallback.SysUserRemoteApiFallbackImpl;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author scott
 * @date 2020/05/22
 */
@Component
public class SysUserRemoteApiFallbackFactory implements FallbackFactory<SysUserRemoteApi> {

	@Override
	public SysUserRemoteApiFallbackImpl create(Throwable throwable) {
		SysUserRemoteApiFallbackImpl remoteUserServiceFallback = new SysUserRemoteApiFallbackImpl();
		remoteUserServiceFallback.setCause(throwable);
		return remoteUserServiceFallback;
	}
}
