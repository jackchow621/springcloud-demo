package com.ghost.demo.autoconfigure;

import com.ghost.demo.annotation.ConditionalOnRouteEnabled;
import com.ghost.demo.impl.NacosExecutorService;
import com.ghost.demo.impl.NacosPullInstanceWorker;
import com.ghost.demo.model.RouteProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @program springcloud-demo
 * @description:
 * @author: jackchow
 * @create: 2022/04/26 21:31
 */
@Slf4j
@Configuration
@ConditionalOnRouteEnabled
public class RouteAutoConfiguration {

    @Autowired(required = false)
    private RouteProperties routeProperties;

    @Value("${spring.cloud.nacos.discovery.namespace:}")
    private String namespace;

    @PostConstruct
    public void init() {
        log.info("初始化智能路由!");
        NacosExecutorService.setBaseUrl(routeProperties.getAddress());

        NacosPullInstanceWorker worker = new NacosPullInstanceWorker();
        //需要优先保存拉取一次路由信息
        worker.run();

        int period = routeProperties.getPeriod();
        NacosExecutorService nacosExecutorService = new NacosExecutorService("nacos-pull-instance");
        nacosExecutorService.execute(period, worker);
    }

}
