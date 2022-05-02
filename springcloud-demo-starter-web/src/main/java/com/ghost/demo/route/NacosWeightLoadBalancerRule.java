package com.ghost.demo.route;

import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.DynamicServerListLoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @program springcloud-demo
 * @description:
 * @author: jackchow
 * @create: 2022/04/26 21:39
 */
@Slf4j
public class NacosWeightLoadBalancerRule extends AbstractLoadBalancerRule {

    @Autowired
    private NacosNamingService nacosNamingService;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Override
    public Server choose(Object o) {
        String groupName = applicationContext.getEnvironment().getProperty("spring.cloud.nacos.config.group");
        if (groupName == null) {
            groupName = "DEFAULT_GROUP";
        }
        DynamicServerListLoadBalancer loadBalancer = (DynamicServerListLoadBalancer) getLoadBalancer();
        String name = loadBalancer.getName();
        try {
            Instance instance = nacosNamingService.getInstance(name, groupName);
            log.info("请求服务:{}", instance != null ? instance.getInstanceId() : null);
            return new NacosServer(instance);
        } catch (NacosException ee) {
            log.error("请求服务异常!异常信息:{}", ee);
        } catch (Exception e) {
            log.error("请求服务异常!异常信息:{}", e);
        }
        return null;
    }

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {
    }

}
