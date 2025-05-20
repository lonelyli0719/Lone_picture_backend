package com.lone.lonepicturebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.lone.lonepicturebackend.mapper")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class LonePictureBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LonePictureBackendApplication.class, args);
    }

}
