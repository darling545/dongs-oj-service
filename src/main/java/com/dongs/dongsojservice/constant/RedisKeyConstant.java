package com.dongs.dongsojservice.constant;

public interface RedisKeyConstant {


    // 缓存键的前缀，用于区分不同分页数据的缓存
    String CACHE_KEY_PREFIX = "dataPage:";


    String CACHE_QUESTION_KEY = "question:";


    String REDIS_LIMIT_KEY_PREFIX = "commit:limit";
}
