package com.ghost.demo.autoconfigure;

import com.alibaba.fastjson.JSON;
import com.ghost.demo.model.GlobalContext;
import com.ghost.demo.util.ContextUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program springcloud-demo
 * @description:
 * @author: jackchow
 * @create: 2022/04/26 21:29
 */
@Configuration
public class FeignAutoConfiguration {

    @Bean
    public RequestInterceptor headerInterceptor() {
        return requestTemplate -> {
            setRequestContext(requestTemplate);
        };
    }

    private void setRequestContext(RequestTemplate requestTemplate) {
        GlobalContext context = ContextUtil.getCurrentContext();
        if (context != null) {
            requestTemplate.header(ContextUtil.REQUEST_CONTEXT, JSON.toJSONString(ContextUtil.getCurrentContext()));
        }
    }

}
