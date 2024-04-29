package org.lkdt.modules.system.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.lkdt.common.aspect.annotation.Idempotent;
import org.lkdt.common.util.RedisUtil;
import org.lkdt.common.util.SpringContextUtils;
import org.lkdt.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author HuangJunYao
 * @date 2021/3/16
 */
@Aspect
@Component
@Slf4j
public class IdempotentAspect {

    @Autowired
    private RedisUtil redisUtil;

    private static final String IDEMPOTENT = "idempotent.key";

    @Pointcut("@annotation(org.lkdt.common.aspect.annotation.Idempotent)")
    public void IdempotentPointCut() {

    }

    @Before("IdempotentPointCut()")
    public void IdempotentBefore(JoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Idempotent ra = method.getAnnotation(Idempotent.class);
        if (Objects.nonNull(ra)) {
            log.debug("Start doIdempotent");
            int liveTime = ra.expiredTime();
            String keyName = ra.keyName();
            String key = keyName + request.getParameter(keyName);
            log.info("Finish generateKey:[{}]", key);
            if (redisUtil.get(key) == null) {
                redisUtil.set(key, "true", liveTime);
                request.setAttribute(IDEMPOTENT, key);
            } else {
                log.info("the key exist : {}, will be expired after {} mils if not be cleared", key, liveTime);
                throw new RuntimeException("请勿重复提交");
            }
        }
    }

    //业务处理完成 删除redis中的key
    @After(value = "IdempotentPointCut()")
    public void IdempotentAfter(JoinPoint joinPoint) {
        HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
        Object obj = request.getAttribute(IDEMPOTENT);
        if (obj != null) {
            log.info("Start afterIdempotent");
            String key = obj.toString();
            if (StringUtils.isNotBlank(key) && redisUtil.del(key)) {
                log.info("afterIdempotent error Prepared to delete the key:[{}] ", key);
            }

            log.info("End afterIdempotent");
        }
    }


}
