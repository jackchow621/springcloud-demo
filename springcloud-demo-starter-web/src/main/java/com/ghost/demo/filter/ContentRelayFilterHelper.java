package com.ghost.demo.filter;

import com.alibaba.fastjson.JSONObject;
import com.ghost.demo.constant.CommonConstant;
import com.ghost.demo.model.GlobalContext;
import com.ghost.demo.util.ContextUtil;
import com.ghost.demo.util.SpringContextUtil;
import com.ghost.demo.util.UnsafetyIdUtil;
import org.slf4j.MDC;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * @program springcloud-demo
 * @description:
 * @author: jackchow
 * @create: 2022/04/26 21:34
 */
public class ContentRelayFilterHelper {

    static boolean isInitTraceId = true;

    public static void setIsInitTraceId(boolean isInit) {
        isInitTraceId = isInit;
    }

    public static void initGlobalContext(String contextStr, String clientIp) {
        initGlobalContext(contextStr, clientIp, null);
    }

    public static void initGlobalContext(String contextStr, String clientIp, String clientHost) {
        GlobalContext globalContext;
        if (StringUtils.isEmpty(contextStr)) {
            globalContext = new GlobalContext();
        } else {
            globalContext = JSONObject.parseObject(contextStr, GlobalContext.class);
        }
//避免假初始化
        if (globalContext == null) {
            globalContext = new GlobalContext();
        }
        if (StringUtils.isEmpty(globalContext.getClientIp())) {
            globalContext.setClientIp(clientIp);
        }

        if (StringUtils.isEmpty(globalContext.getClientHost())) {
            globalContext.setClientHost(clientHost);
        }

        if (StringUtils.isEmpty(globalContext.getEnv())) {
            ApplicationContext context = SpringContextUtil.getContext();
            if (context != null) {
                Environment env = context.getEnvironment();
                globalContext.setEnv(env.getProperty("spring.cloud.nacos.discovery.namespace", ""));
            } else {
                globalContext.setEnv("");
            }
        }
        if (StringUtils.isEmpty(globalContext.getGroup())) {
            ApplicationContext context = SpringContextUtil.getContext();
            if (context != null) {
                Environment env = context.getEnvironment();
                globalContext.setGroup(env.getProperty("spring.cloud.nacos.discovery.group", ""));
            } else {
                globalContext.setGroup(CommonConstant.DEFAULT_GROUP);
            }
        }

        ContextUtil.setCurrentContext(globalContext);
        if (StringUtils.isEmpty(globalContext.getTraceId())) {
            //如果pp有记录，则初始化
            String traceId = MDC.get(ContextUtil.TRACE_ID);
            if (traceId == null && isInitTraceId){
                traceId = UnsafetyIdUtil.genUuid();
            }
            ContextUtil.setTranceId(traceId);
        }
    }

}
