package com.dongs.dongsojservice.config;


import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 创建redisClient，用来连接redis实例
 */
@Deprecated
@Configuration
@Data
@ConfigurationProperties(prefix = "spring.redis")
public class RedissonConfig {

//    @Value("${spring.redis.host}")
    private String host;

//    @Value("${spring.redis.port}")
    private String port;

//    @Value("${spring.redis.database}")
    private Integer database;

    @Bean
    public RedissonClient redissonClient(){
        // 1、创建配置
        Config config = new Config();

        // 构建redis地址
        String redisAddress = String.format("redis://%s:%s",host,port);

        // 单节点模式
        config.useSingleServer().setAddress(redisAddress).setDatabase(database);

        // 2、创建redissonClient实例
        return Redisson.create(config);
    }


}
