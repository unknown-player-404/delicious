package com.example.delicious;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

@SpringBootTest
class DeliciousApplicationTests {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 测试Redis
     */
    @Test
    void redisBasic(){
        stringRedisTemplate.opsForValue().set("demo:name","delicious", Duration.ofMinutes(5));
        //key的名字
        System.out.println(stringRedisTemplate.opsForValue().get("demo:name"));
        //key的过期时间
        System.out.println(stringRedisTemplate.getExpire("demo:name"));
    }

}
