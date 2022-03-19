package com.ghost.springcloud.common;

/**
 * @program springcloud-demo
 * @description:
 * @author: jackchow
 * @create: 2022/03/14 21:09
 */
public class NeedRetryException extends Exception {

    public NeedRetryException(String message){
        super(message);
    }
}
