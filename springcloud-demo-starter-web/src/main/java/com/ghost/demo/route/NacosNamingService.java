package com.ghost.demo.route;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.common.utils.StringUtils;
import com.ghost.demo.constant.CommonConstant;
import com.ghost.demo.model.GlobalContext;
import com.ghost.demo.model.RouteProperties;
import com.ghost.demo.util.ContextUtil;
import com.ghost.demo.util.LocalIpUtil;
import com.ghost.demo.util.SpringContextUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program springcloud-demo
 * @description:
 * @author: jackchow
 * @create: 2022/04/30 14:01
 */
@Slf4j
public class NacosNamingService {


    private static Map<String, List<SmartInstance>> INSTANCE_MAP = Maps.newHashMap();

    @Autowired
    NamingService namingService;

    @Autowired
    private RouteProperties routeProperties;

    public Instance getInstance(String serviceName, String groupName) throws NacosException {
        Boolean routeEnable = routeProperties.isEnable();
        if (!routeEnable) {
            return namingService.selectOneHealthyInstance(serviceName, groupName);
        }

        GlobalContext globalContext = ContextUtil.getCurrentContext();
        if (globalContext == null) {
            globalContext = new GlobalContext();
        }
        String env = globalContext.getEnv();
        if (env == null) {
            ApplicationContext context = SpringContextUtil.getContext();
            if (context != null) {
                Environment environment = context.getEnvironment();
                env = environment.getProperty("spring.cloud.nacos.discovery.namespace", "");
            } else {
                env = "";
            }
        }
        String group = globalContext.getGroup();
        if (group == null) {
            ApplicationContext context = SpringContextUtil.getContext();
            if (context != null) {
                Environment environment = context.getEnvironment();
                group = environment.getProperty("spring.cloud.nacos.discovery.group", "");
            } else {
                group = CommonConstant.DEFAULT_GROUP;
            }
        }
        return getInstance(serviceName, env, group, globalContext.getClientIp());
    }

    public Instance getInstance(String serviceName, String envName, String groupName, String clientIp) throws NacosException {
        log.debug("getInstance serviceName:{}, envName:{}, groupName:{}, clientIp:{}", serviceName, envName, groupName, clientIp);
        List<SmartInstance> envAndGroupInstanceList = new ArrayList<>();
        List<SmartInstance> envDefaultGroupInstanceList = new ArrayList<>();
        List<SmartInstance> defaultInstanceList = new ArrayList<>();

        List<SmartInstance> instanceList = INSTANCE_MAP.get(serviceName);
        //非空校验
        if (instanceList == null || instanceList.size() == 0) {
            log.warn("Nacos服务列表为空!");
            return null;
        }
        //遍历搜索
        for (SmartInstance instance : instanceList) {
            //服务信息获取
            //优先本机匹配
            if (clientIp == null) {
                clientIp = LocalIpUtil.getLocalIp();
            }
            if (clientIp.equals(instance.getIp())) {
                return instance;
            }
            //按照下列规则路由: (环境,组) -> (环境,默认组) -> (默认环境,默认组)
            if (eq(instance.getNamespace(), envName) && eq(instance.getGroupName(),groupName)) {
                envAndGroupInstanceList.add(instance);
                continue;
            }
            if (eq(instance.getNamespace(),envName) && eq(instance.getGroupName(),CommonConstant.DEFAULT_GROUP)) {
                envDefaultGroupInstanceList.add(instance);
                continue;
            }
            if (eq(instance.getNamespace(),CommonConstant.Env.MASTER) && eq(instance.getGroupName(),CommonConstant.DEFAULT_GROUP)) {
                defaultInstanceList.add(instance);
            }
        }
        //输出路由结果
        log.debug("getInstance envAndGroupInstanceList:{} envDefaultGroupInstanceList:{} defaultInstanceList:{}",
                envAndGroupInstanceList, envDefaultGroupInstanceList, defaultInstanceList);

//获取最合适实例
        SmartInstance instance = getSuitableInstance(envAndGroupInstanceList);
        if (instance != null) {
            return instance;
        }
        instance = getSuitableInstance(envDefaultGroupInstanceList);
        if (instance != null) {
            return instance;
        }
        return getSuitableInstance(defaultInstanceList);
    }

    /**
     * 获取包含测试网段的实例
     * @param instanceList 实例列表
     * @return
     */
    private SmartInstance getSuitableInstance(List<SmartInstance> instanceList) {
        if (instanceList.size() > 0) {
            SmartInstance instance = instanceList.get(0);
            for (SmartInstance instanceItem : instanceList) {
                if(instanceItem.getWeight() > instance.getWeight()){
                    instance = instanceItem;
                }
            }
            return instance;
        }
        return null;
    }

    static boolean eq(String s1, String s2){
        return StringUtils.equals(s1, s2);
    }

    public static void setINSTANCE(List<SmartInstance> instances) {
        if(instances != null){
            Map<String, List<SmartInstance>> map = instances.stream()
                    .collect(Collectors.groupingBy(SmartInstance::getName));
            if(map != null){
                INSTANCE_MAP = map;
            }
        }
    }

}
