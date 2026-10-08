package com.example.delicious.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.delicious.common.exception.CustomException;
import com.example.delicious.entity.FoodsMenuInfo;
import com.example.delicious.mapper.FoodsMenuInfoMapper;
import com.example.delicious.service.FoodsMenuInfoService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.List;
import java.util.Random;

@Service
public class FoodsMenuInfoServiceImpl extends ServiceImpl<FoodsMenuInfoMapper, FoodsMenuInfo> implements FoodsMenuInfoService {
    @Resource
    private FoodsMenuInfoMapper FoodsMenuInfoMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private JsonMapper jsonMapper;

    @Override
    public FoodsMenuInfo add(FoodsMenuInfo info) {
        Long count = FoodsMenuInfoMapper.selectCount(
                new LambdaQueryWrapper<FoodsMenuInfo>().eq(FoodsMenuInfo::getName, info.getName())
        );
        if (count > 0) {
            throw new CustomException("400", "菜单已存在");
        }
        FoodsMenuInfoMapper.insert(info);
        return info;
    }

    @Override
    public void delete(Long id) {
        //先更新库
        FoodsMenuInfoMapper.deleteById(id);
        //删除缓存
        String key = "delicious:foods:menu:" + id;
        stringRedisTemplate.delete(key);
    }

    @Override
    public void update(FoodsMenuInfo info) {
        //先更新库
        FoodsMenuInfoMapper.updateById(info);
        //删缓存
        String key = "delicious:foods:menu:" + info.getId();
        stringRedisTemplate.delete(key);
    }

    @Override
    public FoodsMenuInfo findById(Long id) {
        //缓存Key
        String key = "delicious:foods:menu:" + id;
        //查缓存
        String cached = stringRedisTemplate.opsForValue().get(key);
        //Redis缓存命中
        if (cached != null) {
            //判断命中是否为空值
            if (cached.isEmpty()) {
                return null;
            }
            return jsonMapper.readValue(cached, FoodsMenuInfo.class);
        }
        //缓存未命中
        //要加锁
        String lockKey = "lock:foods:menu:" + id;
        Boolean locked = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "1", Duration.ofSeconds(10));
        //抢到锁了
        if (Boolean.TRUE.equals(locked)) {
            try {
                //查库
                FoodsMenuInfo info = FoodsMenuInfoMapper.selectById(id);
                if (info != null) {
                    //未穿透
                    //随机过期时间
                    int expire = 30 + new Random().nextInt(10);
                    //更新缓存
                    stringRedisTemplate.opsForValue().set(key, jsonMapper.writeValueAsString(info), Duration.ofMinutes(expire));
                } else {
                    //穿透了
                    //缓存空值
                    stringRedisTemplate.opsForValue().set(key, "", Duration.ofMinutes(5));
                }
                return info;
            } finally {
                //关闭锁
                stringRedisTemplate.delete(lockKey);
            }
        } else {
            //没抢到锁，等待
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                //重新设置中断标志
                Thread.currentThread().interrupt();
            }
            //递归，即重新启动
            return findById(id);
        }
    }

    @Override
    public List<FoodsMenuInfo> findAll() {
        return FoodsMenuInfoMapper.selectList(null);
    }

    @Override
    public FoodsMenuInfo findByName(String name) {
        LambdaQueryWrapper<FoodsMenuInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            wrapper.eq(FoodsMenuInfo::getName, name);
        }
        return FoodsMenuInfoMapper.selectOne(wrapper);
    }

    @Override
    public Page<FoodsMenuInfo> findPage(String name, Long classifyId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<FoodsMenuInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name) && !name.equals("null")) {
            wrapper.like(FoodsMenuInfo::getName, name);
        }
        if (classifyId != null) {
            wrapper.eq(FoodsMenuInfo::getClassifyId, classifyId);
        }
        return FoodsMenuInfoMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }
}
