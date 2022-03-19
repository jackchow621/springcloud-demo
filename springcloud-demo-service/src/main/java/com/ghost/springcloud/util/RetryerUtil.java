package com.ghost.springcloud.util;

import com.ghost.springcloud.common.NeedRetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.base.Predicates;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @program springcloud-demo
 * @description:
 * @author: jackchow
 * @create: 2022/03/14 21:11
 */
@Data
@Component
@RefreshScope
public class RetryerUtil {

    @Value("${retry.fiexedWait}")
    private long fixedWait;

    @Value("${retry.stopAfterAttempt}")
    private int stopAfterAttempt;


    private static Retryer<Object> instance;

    private static Retryer<Boolean> connectivityInstence;

    public static Retryer<Object> getInstance() {
        RetryerUtil retryerUtil = (RetryerUtil)CommonUtil.getApplicationContext().getBean("retryerUtil");
        if (instance == null) {
            instance = RetryerBuilder
                    .newBuilder()
                    .retryIfExceptionOfType(NeedRetryException.class)
                    .withWaitStrategy(WaitStrategies.fixedWait(retryerUtil.getFixedWait(), TimeUnit.SECONDS))
                    .withStopStrategy(StopStrategies.stopAfterAttempt(retryerUtil.getStopAfterAttempt()))
//                    .withRetryListener(new RetryLogListener())
                    .build();
            return instance;
        }
        return instance;
    }

    public static Retryer<Boolean> getConnectivityInstance () {
        RetryerUtil retryerUtil = (RetryerUtil)CommonUtil.getApplicationContext().getBean("retryerUtil");
        if(connectivityInstence == null) {
            connectivityInstence = RetryerBuilder
                    .<Boolean>newBuilder()
                    .retryIfException()
                    .retryIfResult(Predicates.equalTo(false))
                    .withWaitStrategy(WaitStrategies.fixedWait(retryerUtil.getFixedWait(), TimeUnit.SECONDS))
                    .withStopStrategy(StopStrategies.stopAfterAttempt(retryerUtil.getStopAfterAttempt()))
                    .build();

            return connectivityInstence;
        }
        return connectivityInstence;
    }

}
