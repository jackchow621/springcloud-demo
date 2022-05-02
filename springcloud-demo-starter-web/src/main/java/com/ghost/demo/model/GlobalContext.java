package com.ghost.demo.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @program springcloud-demo
 * @description:
 * @author: jackchow
 * @create: 2022/04/30 14:09
 */
@Data
@NoArgsConstructor
@ToString
public class GlobalContext {
    private String traceId;
    private String env;
    private String group;
    private String clientIp;
    private String clientHost;
    private String userId;
    private String entId;
    private String tenantId;
}
