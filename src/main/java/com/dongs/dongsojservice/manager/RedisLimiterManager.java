package com.dongs.dongsojservice.manager;


import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 限流器
 *
 * @author dongs
 */
@Slf4j
@Component
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;


    public boolean doRateLimit(String key){
        // 创建限流器
        RRateLimiter rRateLimiter = redissonClient.getRateLimiter(key);
        // 每秒最多访问2次
        boolean trySetRate = rRateLimiter.trySetRate(RateType.OVERALL,1,3, RateIntervalUnit.SECONDS);
        if (trySetRate){
            log.info("init rate = {}, interval = {}", rRateLimiter.getConfig().getRate(), rRateLimiter.getConfig().getRateInterval());
        }
        // 来一个请求，发放一个令牌
        return rRateLimiter.tryAcquire(1);
    }

}
