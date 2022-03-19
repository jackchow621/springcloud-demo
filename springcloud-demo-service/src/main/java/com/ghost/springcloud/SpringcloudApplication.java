package com.ghost.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringcloudApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(SpringcloudApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
