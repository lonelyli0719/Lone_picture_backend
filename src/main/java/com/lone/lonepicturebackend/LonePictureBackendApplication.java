package com.lone.lonepicturebackend;

import org.apache.shardingsphere.spring.boot.ShardingSphereAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

// 关闭分库分表，同时不要加载DynamicShardingManager这个配置类
@SpringBootApplication(exclude = {ShardingSphereAutoConfiguration.class})
@EnableAsync
@MapperScan("com.lone.lonepicturebackend.mapper")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class LonePictureBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LonePictureBackendApplication.class, args);
    }

}
