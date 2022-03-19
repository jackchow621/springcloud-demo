package com.ghost.springcloud.common.log;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @program springcloud-demo
 * @description:
 * @author: jackchow
 * @create: 2022/03/13 20:44
 */
@NoArgsConstructor
@Slf4j
public class LogProxy implements InvocationHandler {
    private Object target;

    public static Object getInstance(Object o) {
        LogProxy pm = new LogProxy();
        pm.target = o;
        Object result = Proxy.newProxyInstance(o.getClass().getClassLoader(), o
                .getClass().getInterfaces(), pm);
        return result;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.isAnnotationPresent(Log.class)) {
            Log la = method.getAnnotation(Log.class);
            Logger.info(la.value() + "--->执行的是" + method.getName() + "方法.");
        }
        return method.invoke(target, args);
    }
}
