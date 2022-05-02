package com.ghost.demo.autoconfigure;

import com.ghost.demo.aop.LocalCacheAspect;
import com.ghost.demo.controller.CacheController;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program springcloud-demo
 * @description:
 * @author: jackchow
 * @create: 2022/04/26 21:30
 */
@Configuration
@ConditionalOnProperty(name = "boot.cache.enable", matchIfMissing = true, havingValue = "true")
public class LocalCacheAutoConfigure {

    @Bean
    LocalCacheAspect getLocalCacheAspect(){
        return new LocalCacheAspect();
    }
    @Bean
    @ConditionalOnMissingBean(value = CacheController.class)
    CacheController registerCacheController(){
        return new CacheController();
    }
}
