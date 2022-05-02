package com.ghost.demo.util;

import com.ghost.demo.model.GlobalContext;
import lombok.NoArgsConstructor;
import org.slf4j.MDC;

/**
 * @program springcloud-demo
 * @description:
 * @author: jackchow
 * @create: 2022/04/30 14:09
 */
@NoArgsConstructor
public class ContextUtil {
    public static final String TRACE_ID = "PtxId";
    public static final String REQUEST_CONTEXT = "requestContext";
    public static ThreadLocal<GlobalContext> currentThreadLocal =
            ThreadLocal.withInitial(GlobalContext::new);

    public static GlobalContext getCurrentContext() {
        return currentThreadLocal.get();
    }

    public static void setCurrentContext(GlobalContext globalContext) {
        currentThreadLocal.set(globalContext);
        String traceId = globalContext.getTraceId();
        if (traceId != null && traceId.length() > 0 && MDC.get(TRACE_ID) == null) {
            MDC.put(TRACE_ID, traceId);
        }

    }

    public static void clear() {
        MDC.clear();
        currentThreadLocal.remove();
    }

    public static void setTranceId(String traceId) {
        MDC.put(TRACE_ID, traceId);
        getCurrentContext().setTraceId(traceId);
    }

    public static String getTraceId() {
        return MDC.get(TRACE_ID);
    }

    public static String getClientHost() {
        GlobalContext context = getCurrentContext();
        return context != null ? context.getClientHost() : null;
    }

    public static String getAndCheckTenantId() {
        String tenantId = getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("tenantId can't be null");
        }
        return tenantId;
    }

    public static String getTenantId() {
        GlobalContext context = getCurrentContext();
        return context != null ? context.getTenantId() : null;
    }
}
