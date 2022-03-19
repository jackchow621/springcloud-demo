package com.ghost.springcloud.common.log;

import java.util.Date;

/**
 * @program springcloud-demo
 * @description:
 * @author: jackchow
 * @create: 2022/03/13 20:46
 */
public class Logger {
    public static void info(String message){
        System.out.println(new Date()+"--------->"+message);
    }

}
