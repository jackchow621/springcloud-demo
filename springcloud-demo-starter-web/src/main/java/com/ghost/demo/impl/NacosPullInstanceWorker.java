package com.ghost.demo.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ghost.demo.route.NacosNamingService;
import com.ghost.demo.route.SmartInstance;
import com.ghost.demo.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @program springcloud-demo
 * @description:
 * @author: jackchow
 * @create: 2022/04/26 21:36
 */
@Slf4j
public class NacosPullInstanceWorker implements Runnable {

    private static final String NO_CHANCE = "NO_CHANCE";

    private static String md5;

    @Override
    public void run() {
        String content = null;
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        String url = NacosExecutorService.getBaseUrl() + "?md5=" + md5;
        Map<String, Object> resultMap = HttpUtil.doGetJson(url);
        httpClient = (CloseableHttpClient) resultMap.get(HttpUtil.HTTP_CLIENT);
        ;
        response = (CloseableHttpResponse) resultMap.get(HttpUtil.HTTP_RESPONSE);
        try {
            Header md5Header = response.getLastHeader("list-md5");
            if (md5Header != null) {
                md5 = md5Header.getValue();
            }
            log.debug("Nacos列表的md5值:{}", md5);
            String resultString = EntityUtils.toString(response.getEntity(), "utf-8");
            if (NO_CHANCE.equals(resultString)) {
                log.debug("Nacos列表保持不变!");
                return;
            }
            content = JSON.parseObject(resultString, String.class);
            log.debug("Nacos列表字符串:{}", content);
            List<SmartInstance> instances = JSONArray.parseArray(content, SmartInstance.class);
            if (instances == null) {
                log.warn("Nacos列表为空!");
                return;
            }
            log.debug("Nacos列表数量:{}", instances.size());
            NacosNamingService.setINSTANCE(instances);
        } catch (Exception e) {
            log.error("获取Nacos列表异常!异常信息:{}", e);
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                log.error("获取Nacos列表异常!异常信息:{}", e);
            }
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.error("获取Nacos列表异常!异常信息:{}", e);
            }
        }
    }

}
