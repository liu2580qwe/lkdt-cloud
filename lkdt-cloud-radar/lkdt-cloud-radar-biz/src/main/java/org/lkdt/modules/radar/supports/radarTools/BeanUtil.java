package org.lkdt.modules.radar.supports.radarTools;

import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Component;

@Component
public class BeanUtil extends ApplicationObjectSupport {//implements ApplicationContextAware

    //将管理上下文的applicationContext设置成静态变量，供全局调用
//    public static ApplicationContext applicationContext;

//    //@Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        this.applicationContext = applicationContext;
//    }
//    /**
//     * 获取applicationContext
//     * @return
//     */
//    public ApplicationContext getApplicationContext() {
//        return applicationContext;
//    }

    /**
     * 通过name获取 Bean.
     * @param name
     * @return
     */
    public Object getBean(String name){
        return getApplicationContext().getBean(name);
    }

    /**
     * 通过class获取Bean.
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getBean(Class<T> clazz){
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     * @param name
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getBean(String name,Class<T> clazz){
        return getApplicationContext().getBean(name, clazz);
    }

}
