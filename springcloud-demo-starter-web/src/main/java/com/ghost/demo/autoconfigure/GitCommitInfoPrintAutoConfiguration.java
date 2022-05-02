package com.ghost.demo.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

/**
 * @program springcloud-demo
 * @description:
 * @author: jackchow
 * @create: 2022/04/26 21:30
 */
@Slf4j
@Configuration
public class GitCommitInfoPrintAutoConfiguration implements ApplicationListener<ApplicationReadyEvent> {

    volatile static boolean isInit = false;
    static String packageInfo = "empty";
    public static String getPackageInfo(){
        return packageInfo;
    }
    @Override
    public synchronized void onApplicationEvent(ApplicationReadyEvent event) {
        if(event.getApplicationContext().getParent() != null){
            if(isInit){
                return;
            }
            try {
                try(InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("jdh-git.commit")){
                    if(inputStream != null){
                        String gitInfo = IOUtils.toString(inputStream, "UTF-8");
                        packageInfo = gitInfo;
                        log.info("git last commit info \r\n{}", gitInfo);
                    }
                }
            } catch (IOException e) {
                log.warn("read git commit file error", e);
            }
            isInit = true;
        }
    }


}
