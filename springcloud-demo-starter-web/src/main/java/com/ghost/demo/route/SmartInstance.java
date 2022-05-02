package com.ghost.demo.route;

import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.Data;

/**
 * @program springcloud-demo
 * @description:
 * @author: jackchow
 * @create: 2022/04/26 21:39
 */
@Data
public class SmartInstance extends Instance {

    String groupName;

    String name;

    String namespace;

    @Override
    public String toString() {
        return "SmartInstance{" +
                "groupName='" + groupName + '\'' +
                ", name='" + name + '\'' +
                ", namespace='" + namespace + '\'' +
                ", ip='" + getIp() + '\'' +
                ", port='" + getPort() + '\'' +
                '}';
    }

}
