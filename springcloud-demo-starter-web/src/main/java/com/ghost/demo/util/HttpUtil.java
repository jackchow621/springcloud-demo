package com.ghost.demo.util;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;

/**
 * @program springcloud-demo
 * @description:
 * @author: jackchow
 * @create: 2022/04/30 14:18
 */
@Slf4j
@NoArgsConstructor
public class HttpUtil {

    /**
     * HTTP响应对象
     */
    public static final String HTTP_RESPONSE = "HttpResponse";

    /**
     * HTTP客户端
     */
    public static final String HTTP_CLIENT = "HttpClient";

    /**
     * HTTPS协议
     */
    private static final String HTTPS = "https://";

    /**
     * HTTP协议
     */
    private static final String HTTP = "http://";

    /**
     * 连接超时时间,毫秒
     */
    private static int connectionTimeout = 30000;

    /**
     * 请求超时时间,毫秒
     */
    private static int connectionRequestTimeout = 30000;

    /**
     * 响应超时时间,毫秒
     */
    private static int socketTimeout = 30000;

    /**
     * 超时设置
     */
    private static RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectionTimeout).setConnectionRequestTimeout(connectionRequestTimeout).setSocketTimeout(socketTimeout).build();

    /**
     * get请求
     *
     * @param url 访问路径
     * @return Map<String, Object>
     */
    public static Map<String, Object> doGetJson(String url) {
        Map<String, Object> resultMap = Maps.newHashMap();
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            httpClient = HttpClients.createDefault();
            String result = null;
            //针对host特殊处理
            if (getUrlProtocol(url) == null) {
                url = HTTP + url;
            }
            URIBuilder uri = new URIBuilder(url);
            HttpGet httpGet = new HttpGet(uri.build());
            httpGet.setConfig(requestConfig);
            response = httpClient.execute(httpGet);
            resultMap.put(HTTP_RESPONSE, response);
            resultMap.put(HTTP_CLIENT, httpClient);
            return resultMap;
        } catch (Exception e) {
            log.error("GET请求报错!报错信息:{}", e);
        }
        return resultMap;
    }

    /**
     * delete请求
     *
     * @param url 访问路径
     * @return Map<String, Object>
     */
    public static Map<String, Object> doDeleteJson(String url) {
        Map<String, Object> resultMap = Maps.newHashMap();
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            httpClient = HttpClients.createDefault();
            String result = null;
            //针对host特殊处理
            if (getUrlProtocol(url) == null) {
                url = HTTP + url;
            }
            URIBuilder uri = new URIBuilder(url);
            HttpDelete httpDelete = new HttpDelete(uri.build());
            httpDelete.setConfig(requestConfig);
            response = httpClient.execute(httpDelete);
            resultMap.put(HTTP_RESPONSE, response);
            resultMap.put(HTTP_CLIENT, httpClient);
            return resultMap;
        } catch (Exception e) {
            log.error("DELETE请求报错!报错信息:{}", e);
        }
        return resultMap;
    }

    /**
     * get请求
     *
     * @param url         访问路径
     * @param returnClass 返回类型
     * @return T
     */
    public static <T> T doGetJson(String url, Class<? extends T> returnClass) {
        Map<String, Object> resultMap = doGetJson(url);
        return dealResult(returnClass, resultMap);
    }

    /**
     * delete请求
     *
     * @param url         访问路径
     * @param returnClass 返回类型
     * @return T
     */
    public static <T> T doDeleteJson(String url, Class<? extends T> returnClass) {
        Map<String, Object> resultMap = doDeleteJson(url);
        return dealResult(returnClass, resultMap);
    }

    private static <T> T dealResult(Class<? extends T> returnClass, Map<String, Object> resultMap) {
        CloseableHttpClient httpClient = (CloseableHttpClient) resultMap.get(HTTP_CLIENT);
        ;
        CloseableHttpResponse response = (CloseableHttpResponse) resultMap.get(HTTP_RESPONSE);
        String resultString = "";
        try {
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (IOException e) {
            log.error("HTTP请求报错!报错信息:{}", e);
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                log.error("HTTP请求报错!报错信息:{}", e);
            }
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.error("HTTP请求报错!报错信息:{}", e);
            }
        }
        return JSON.parseObject(resultString, returnClass);
    }

    /**
     * POST请求
     *
     * @param url         访问路径
     * @param returnClass 返回类型
     * @return
     */
    public static <T> T doPostJson(String url, Class<? extends T> returnClass) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            httpClient = HttpClients.createDefault();
            String result = null;
            //针对host特殊处理
            if (getUrlProtocol(url) == null) {
                url = HTTP + url;
            }
            URIBuilder uri = new URIBuilder(url);
            HttpPost httpPost = new HttpPost(uri.build());
            httpPost.setConfig(requestConfig);
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            log.error("POST请求报错!报错信息:{}", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.error("GET请求报错!报错信息:{}", e);
            }
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                log.error("POST请求报错!报错信息:{}", e);
            }
        }
        return JSON.parseObject(resultString, returnClass);
    }

    /**
     * 获取URL的协议
     *
     * @param url
     * @return
     */
    public static String getUrlProtocol(String url) {
        if (url == null) {
            return null;
        }
        String httpsProtocolStr = url.substring(0, 8);
        if (HTTPS.equals(httpsProtocolStr)) {
            return HTTPS;
        }
        String httpProtocolStr = url.substring(0, 7);
        if (HTTP.equals(httpProtocolStr)) {
            return HTTP;
        }
        return null;
    }

}
