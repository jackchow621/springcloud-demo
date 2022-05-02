package com.ghost.demo.autoconfigure;

import com.ghost.demo.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @program springcloud-demo
 * @description:
 * @author: jackchow
 * @create: 2022/04/26 21:28
 */
@Configuration
@Slf4j
public class CommonAutoConfiguration {

    @Autowired
    ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        SpringContextUtil.setContext(applicationContext);
        log.info("Nacos超时时间(毫秒):{}", System.getProperty("com.alibaba.nacos.client.naming.ctimeout"));
    }

}
