package com.ghost.demo.autoconfigure;

import com.ghost.demo.controller.AppPackageInfoController;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program springcloud-demo
 * @description:
 * @author: jackchow
 * @create: 2022/04/26 21:27
 */
@Configuration
@ConditionalOnProperty(name = "boot.app-package-info.enable", matchIfMissing = true, havingValue = "true")
public class AppPackageInfoAutoConfigure {

    @Bean
    @ConditionalOnMissingBean(value = AppPackageInfoController.class)
    AppPackageInfoController registerAppPackageInfoController(){
        return new AppPackageInfoController();
    }

}
