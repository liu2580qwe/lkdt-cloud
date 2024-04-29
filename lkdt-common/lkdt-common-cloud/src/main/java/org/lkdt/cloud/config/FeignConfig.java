package org.lkdt.cloud.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.lkdt.common.constant.CommonConstant;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Feign配置
 * 使用FeignClient进行服务间调用，传递headers信息
 */
@Configuration
public class FeignConfig implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if(attributes!=null){
            HttpServletRequest request = attributes.getRequest();
            //添加token
            requestTemplate.header(CommonConstant.X_ACCESS_TOKEN, request.getHeader(CommonConstant.X_ACCESS_TOKEN));
        }
    }
}