package com.ghost.demo.controller;

import com.ghost.demo.aop.LocalCacheAspect;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program springcloud-demo
 * @description:
 * @author: jackchow
 * @create: 2022/04/26 21:33
 */
@RestController
@RequestMapping("system-cache")
public class CacheController {
    /**
     * 获取缓存列表
     */
    @GetMapping(path = "list")
    public Object list() {
        return LocalCacheAspect.list();
    }

    /**
     * 清理
     *
     * @param key
     * @return
     */
    @GetMapping(path = "clear")
    public @ResponseBody
    String clear(String key) {
        LocalCacheAspect.clearCache(key);
        return "OK";
    }
}
