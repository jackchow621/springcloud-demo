package com.ghost.demo.util;

import java.util.Properties;

/**
 * @program springcloud-demo
 * @description:
 * @author: jackchow
 * @create: 2022/04/30 13:46
 */
public class KafkaLogPropertiesUtil {

    public static Properties buildLogProperties(String server) {
        Properties result = new Properties();
        result.put("bootstrap.servers", server);
        //不需要响应
        result.put("acks", "0");
        result.put("retries", "0");
        //kafka挂了或者队列满了，不阻塞
        result.put("max.block.ms", "0");
        result.put("batch.size", 16384);
        result.put("linger.ms", 5);
        result.put("buffer.memory", 33554432);
        result.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        result.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return result;
    }
}
