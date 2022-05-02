package com.ghost.demo.model;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @program springcloud-demo
 * @description:
 * @author: jackchow
 * @create: 2022/04/26 21:38
 */
@Component
@Data
public class RouteProperties {

    @Value("${boot.route.enable:true}")
    private boolean enable;

    @Value("${boot.route.period:3}")
    private int period;

    @Value("${boot.route.address:http://10.0.17.125:8801/list-all-instance}")
    private String address;

}
