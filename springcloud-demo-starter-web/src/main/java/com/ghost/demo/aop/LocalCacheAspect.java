package com.ghost.demo.aop;

import com.ghost.demo.annotation.LocalCache;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @program springcloud-demo
 * @description:
 * @author: jackchow
 * @create: 2022/04/30 13:50
 */
@Aspect
@Slf4j
public class LocalCacheAspect {

    public static final String CACHE_SPLIT = "-";

    public LocalCacheAspect() {
        log.info("init LocalCacheAspect success");
    }

    private static Map<String, Cache<Object, Object>> methodMap = new ConcurrentHashMap<>();

    public static void clearCache(String key) {
        if (StringUtils.isEmpty(key)) {
            for (Cache c : methodMap.values()) {
                c.invalidateAll();
            }
        } else {
            Cache<Object, Object> cache = methodMap.get(key);
            if (cache != null) {
                cache.invalidateAll();
            }
        }
    }

    public static Map<String, String> list() {
        Map<String, String> result = Maps.newHashMapWithExpectedSize(methodMap.size());
        for (Map.Entry<String, Cache<Object, Object>> e : methodMap.entrySet()) {
            result.put(e.getKey(), e.getValue().stats().toString());
        }
        return result;
    }

    @Around("@annotation(com.jdh.boot.annotation.LocalCache)")
    public Object localCache(ProceedingJoinPoint pjp) throws Throwable {
        String methodInfo = pjp.toShortString();
        Object[] args = pjp.getArgs();
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        String methodName = getMethodName(pjp.getTarget().getClass(), method);
        Cache<Object, Object> cache = methodMap.computeIfAbsent(methodName, (m) -> {
            LocalCache localCache = method.getAnnotation(LocalCache.class);
            return CacheBuilder.newBuilder()
                    .recordStats()
                    .expireAfterWrite(localCache.expireSecond(), TimeUnit.SECONDS)
                    .build();
        });
        String tempCacheKey = "";
        if (args != null || args.length > 0) {
            tempCacheKey = Arrays.asList(args).stream().map(v -> v != null ? v.toString() : null)
                    .collect(Collectors.joining(CACHE_SPLIT));
        }
        final String cacheKey = tempCacheKey;
        Object result = cache.getIfPresent(cacheKey);
        if (result != null) {
            return result;
        } else {
            try {
                return cache.get(cacheKey, () -> {
                    try {
                        return pjp.proceed();
                    } catch (ExecutionException e) {
                        log.warn("@LocalCache get value exception {},{}", methodInfo, cacheKey, e);
                        throw e;
                    } catch (Throwable e) {
                        log.warn("@LocalCache get value error {},{}", methodInfo, cacheKey, e);
                        throw new ExecutionException(e);
                    }
                });
            } catch (CacheLoader.InvalidCacheLoadException e) {
                log.warn("@LocalCache InvalidCacheLoadException return null {},{}", methodInfo, cacheKey);
                return null;
            }
        }
    }

    private String getMethodName(Class<?> clazz, Method m) {
        StringBuilder sb = new StringBuilder();
        sb.append(clazz.getName());
        sb.append(':');
        sb.append(m.getName());
        sb.append('(');
        Class[] types = m.getParameterTypes();
        for (int j = 0; j < types.length; j++) {
            sb.append(types[j].getSimpleName());
            if (j < (types.length - 1)) {
                sb.append(",");
            }
        }
        sb.append(')');
        return sb.toString();
    }
}
