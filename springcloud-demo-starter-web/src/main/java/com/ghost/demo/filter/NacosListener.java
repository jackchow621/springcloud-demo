package com.ghost.demo.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @program springcloud-demo
 * @description:
 * @author: jackchow
 * @create: 2022/04/26 21:37
 */
@Component
@Slf4j
public class NacosListener implements ApplicationListener<ApplicationEvent> {

    volatile Environment env;

    private static boolean nacosListenerSuccess = false;

    @Override
    public synchronized void onApplicationEvent(ApplicationEvent context) {
        if (nacosListenerSuccess) {
            return;
        }
        nacosListenerSuccess = true;
        String nacosTimeout = "5000";
        System.setProperty("com.alibaba.nacos.client.naming.ctimeout", nacosTimeout);
    }

}
