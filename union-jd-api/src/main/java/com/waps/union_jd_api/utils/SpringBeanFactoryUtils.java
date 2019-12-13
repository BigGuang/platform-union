package com.waps.union_jd_api.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringBeanFactoryUtils implements ApplicationContextAware {

    private static ApplicationContext appCtx;

    /**
     * TODO: 此方法可以把ApplicationContext对象inject到当前类中作为一个静态成员变量。
     *
     * @Auhor: RICK
     * @Date : 2016年11月1日
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appCtx = applicationContext;
    }

    /**
     * TODO: 获取ApplicationContext
     *
     * @Auhor: RICK
     * @Date : 2016年11月1日
     */
    public static ApplicationContext getApplicationContext() {
        return appCtx;
    }

    /**
     * TODO: 这是一个便利的方法，帮助我们快速得到一个BEAN
     *
     * @Auhor: RICK
     * @Date : 2016年11月1日
     */
    public static Object getBean(String beanName) {
        return appCtx.getBean(beanName);
    }

    public static <T> T getBean(Class<T> clazz){
        return appCtx.getBean(clazz);
    }
}
