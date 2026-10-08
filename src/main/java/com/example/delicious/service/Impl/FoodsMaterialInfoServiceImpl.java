package com.example.delicious.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.delicious.common.exception.CustomException;
import com.example.delicious.entity.FoodsMaterialInfo;
import com.example.delicious.mapper.FoodsMaterialInfoMapper;
import com.example.delicious.service.FoodsMaterialInfoService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.List;
import java.util.Random;

@Service
public class FoodsMaterialInfoServiceImpl extends ServiceImpl<FoodsMaterialInfoMapper, FoodsMaterialInfo> implements FoodsMaterialInfoService {
    /** 详情缓存 key 前缀：delicious:material:{id} */
    private static final String CACHE_KEY_PREFIX = "delicious:material:";

    @Resource
    private FoodsMaterialInfoMapper foodsMaterialInfoMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private JsonMapper jsonMapper;

    @Override
    public FoodsMaterialInfo add(FoodsMaterialInfo info) {
        Long count = foodsMaterialInfoMapper.selectCount(
                new LambdaQueryWrapper<FoodsMaterialInfo>().eq(FoodsMaterialInfo::getName, info.getName())
        );
        if (count > 0) {
            throw new CustomException("400", "食材已存在");
        }
        foodsMaterialInfoMapper.insert(info);
        return info;
    }

    @Override
    public void delete(Long id) {
        foodsMaterialInfoMapper.deleteById(id);
        // 删详情缓存
        stringRedisTemplate.delete(CACHE_KEY_PREFIX + id);
    }

    @Override
    public void update(FoodsMaterialInfo info) {
        foodsMaterialInfoMapper.updateById(info);
        // 删详情缓存
        stringRedisTemplate.delete(CACHE_KEY_PREFIX + info.getId());
    }

    @Override
    public FoodsMaterialInfo findById(Long id) {
        String key = CACHE_KEY_PREFIX + id;
        // 1. 查缓存
        String cached = stringRedisTemplate.opsForValue().get(key);
        if (cached != null) {
            // 命中的是空值缓存，说明数据库里也没有
            if (cached.isEmpty()) {
                return null;
            }
            return jsonMapper.readValue(cached, FoodsMaterialInfo.class);
        }
        // 2. 未命中查库
        FoodsMaterialInfo info = foodsMaterialInfoMapper.selectById(id);
        // 3. 回写缓存：不存在则缓存空值防穿透，存在则用随机过期时间防雪崩
        if (info != null) {
            int expire = 30 + new Random().nextInt(10);
            stringRedisTemplate.opsForValue().set(key, jsonMapper.writeValueAsString(info), Duration.ofMinutes(expire));
        } else {
            stringRedisTemplate.opsForValue().set(key, "", Duration.ofMinutes(5));
        }
        return info;
    }

    @Override
    public List<FoodsMaterialInfo> findAll() {
        return foodsMaterialInfoMapper.selectList(null);
    }

    @Override
    public FoodsMaterialInfo findByName(String name) {
        LambdaQueryWrapper<FoodsMaterialInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            wrapper.eq(FoodsMaterialInfo::getName, name);
        }
        return foodsMaterialInfoMapper.selectOne(wrapper);
    }

    @Override
    public Page<FoodsMaterialInfo> findPage(String name, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<FoodsMaterialInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name) && !name.equals("null")) {
            wrapper.like(FoodsMaterialInfo::getName, name);
        }
        return foodsMaterialInfoMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }
}
