package com.ghost.demo.autoconfigure;

import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import com.ghost.demo.model.RouteProperties;
import com.ghost.demo.route.NacosNamingService;
import com.ghost.demo.route.NacosWeightLoadBalancerRule;
import com.netflix.loadbalancer.IRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @program springcloud-demo
 * @description:
 * @author: jackchow
 * @create: 2022/04/26 21:30
 */
@Configuration
@ConditionalOnNacosDiscoveryEnabled
public class NacosAutoConfiguration {

    @Bean
    public RouteProperties routeProperties() {
        return new RouteProperties();
    }

    @Bean
    public NacosNamingService nacosNamingService() {
        return new NacosNamingService();
    }

    @Bean
    @Scope("prototype")
    public IRule getRibbonRule() {
        return new NacosWeightLoadBalancerRule();
    }

}
