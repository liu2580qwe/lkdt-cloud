package org.lkdt.modules.api.factory;

import org.lkdt.modules.api.HighwayApi;
import org.lkdt.modules.api.fallback.HighwayApiFallback;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author scott
 * @date 2020/05/22
 */
@Component
public class HighwayApiFallbackFactory implements FallbackFactory<HighwayApi> {

	@Override
	public HighwayApiFallback create(Throwable throwable) {
		HighwayApiFallback fallback = new HighwayApiFallback();
		fallback.setCause(throwable);
		return fallback;
	}
}
