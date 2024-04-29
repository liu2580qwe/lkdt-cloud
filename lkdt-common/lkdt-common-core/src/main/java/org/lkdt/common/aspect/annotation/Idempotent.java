package org.lkdt.common.aspect.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author HuangJunYao
 * @date 2021/3/16
 * 防止重复提交注解
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Idempotent {
    /**
     * 是否把body数据用来计算幂等key。如果没有登录信息，请设置这个值为true。主要用于第三方接入。
     *
     * @return
     */
    String keyName() default "";

    /**
     * idempotent lock失效时间，in milliseconds。一些处理时间较长或者数据重复敏感的接口，可以适当设置长点时间。默认30s
     *
     * @return
     */
    int expiredTime() default 30000;

}
