package com.ghost.springcloud.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program springcloud-demo
 * @description:
 * @author: jackchow
 * @create: 2022/01/03 14:21
 */
@RestController
@RequestMapping("/health")
@Slf4j
public class HealthController {

    @GetMapping("/healthCheck")
    @SentinelResource(value = "/healthCheck")
    public String healthCheck() {
        log.info("success request");
        return "success";
    }
}
