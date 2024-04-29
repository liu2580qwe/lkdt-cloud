package org.lkdt.modules.fog.api.factory;

import org.lkdt.modules.fog.api.FogApi;
import org.lkdt.modules.fog.api.fallback.FogApiFallback;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author scott
 * @date 2020/05/22
 */
@Component
public class FogApiFallbackFactory implements FallbackFactory<FogApi> {

	@Override
	public FogApiFallback create(Throwable throwable) {
		FogApiFallback fallback = new FogApiFallback();
		fallback.setCause(throwable);
		return fallback;
	}
}
