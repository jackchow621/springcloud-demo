package com.ghost.demo.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

/**
 * @program springcloud-demo
 * @description:
 * @author: jackchow
 * @create: 2022/04/26 21:40
 */
@Slf4j
public class SpringContextUtil {

    /**
     * 全局Spring上下文
     */
    private static ApplicationContext applicationContext;

    public static ApplicationContext getContext () {
        return applicationContext;
    }

    public static void setContext(ApplicationContext applicationContext) {
        SpringContextUtil.applicationContext = applicationContext;
    }

    public static <T> T getObject(Class<T> tClass) {
        return applicationContext.getBean(tClass);
    }

    public static Object getBean(String tClass) {
        return applicationContext.getBean(tClass);
    }

    public <T> T getBean(Class<T> tClass) {
        return applicationContext.getBean(tClass);
    }

    private SpringContextUtil(){}

}
