package com.ghost.demo.controller;

import com.ghost.demo.autoconfigure.GitCommitInfoPrintAutoConfiguration;
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
@RequestMapping("system-package-info")
public class AppPackageInfoController {
    /**
     * 获取打包信息
     */
    @GetMapping(path = "get")
    public @ResponseBody
    String get(){
        return GitCommitInfoPrintAutoConfiguration.getPackageInfo();
    }
}
