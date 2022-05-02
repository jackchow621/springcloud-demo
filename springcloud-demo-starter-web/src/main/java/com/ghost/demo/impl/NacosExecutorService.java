package com.ghost.demo.impl;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @program springcloud-demo
 * @description:
 * @author: jackchow
 * @create: 2022/04/26 21:36
 */
public class NacosExecutorService {

    final ScheduledExecutorService executorService;

    private static String baseUrl;

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static void setBaseUrl(String baseUrl) {
        NacosExecutorService.baseUrl = baseUrl;
    }

    public void execute(int period, Runnable task) {
        executorService.scheduleWithFixedDelay(task, 3, period, TimeUnit.SECONDS);
    }

    public NacosExecutorService(String name) {
        executorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName(name);
                t.setDaemon(true);
                return t;
            }
        });
    }

}
