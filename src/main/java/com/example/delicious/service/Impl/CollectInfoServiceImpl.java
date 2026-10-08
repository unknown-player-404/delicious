package com.example.delicious.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.delicious.common.exception.CustomException;
import com.example.delicious.entity.CollectInfo;
import com.example.delicious.mapper.CollectInfoMapper;
import com.example.delicious.service.CollectInfoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
public class CollectInfoServiceImpl extends ServiceImpl<CollectInfoMapper, CollectInfo> implements CollectInfoService {
    /** 收藏详情缓存 key 前缀：delicious:collect:{id} */
    private static final String CACHE_KEY_PREFIX = "delicious:collect:";
    /** 收藏数计数器 key 前缀：delicious:collect:count:{foodsId} */
    private static final String COUNT_KEY_PREFIX = "delicious:collect:count:";

    @Resource
    private CollectInfoMapper collectInfoMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private JsonMapper jsonMapper;

    @Override
    public CollectInfo add(CollectInfo info) {
        if (info.getFoodsId() == null || info.getUserId() == null) {
            throw new CustomException("400", "参数错误");
        }
        Long count = collectInfoMapper.selectCount(
                new LambdaQueryWrapper<CollectInfo>()
                        .eq(CollectInfo::getFoodsId, info.getFoodsId())
                        .eq(CollectInfo::getUserId, info.getUserId())
        );
        if (count > 0) {
            throw new CustomException("400", "已收藏");
        }
        // 1. 先落库（数据库是唯一真相源）
        collectInfoMapper.insert(info);
        // 2. 缓存命中才 +1；没命中留给 getCount 从数据库重建
        try {
            String key = COUNT_KEY_PREFIX + info.getFoodsId();
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
                stringRedisTemplate.opsForValue().increment(key);
            }
        } catch (Exception e) {
            log.warn("收藏计数缓存更新失败, foodsId={}", info.getFoodsId(), e);
        }
        return info;
    }

    @Override
    public void delete(Long id) {
        // 先查出记录，拿到 foodsId
        CollectInfo info = collectInfoMapper.selectById(id);
        collectInfoMapper.deleteById(id);
        if (info == null) {
            return;
        }
        try {
            // 删详情缓存
            stringRedisTemplate.delete(CACHE_KEY_PREFIX + id);
            // 计数器 -1，出现负数说明缓存和库不一致，删掉让它重建
            String key = COUNT_KEY_PREFIX + info.getFoodsId();
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
                Long value = stringRedisTemplate.opsForValue().decrement(key);
                if (value != null && value < 0) {
                    stringRedisTemplate.delete(key);
                }
            }
        } catch (Exception e) {
            log.warn("取消收藏缓存更新失败, collectId={}", id, e);
        }
    }

    @Override
    public void update(CollectInfo info) {
        collectInfoMapper.updateById(info);
        // 删详情缓存
        stringRedisTemplate.delete(CACHE_KEY_PREFIX + info.getId());
    }

    @Override
    public CollectInfo findById(Long id) {
        String key = CACHE_KEY_PREFIX + id;
        // 1. 查缓存
        String cached = stringRedisTemplate.opsForValue().get(key);
        if (cached != null) {
            // 命中的是空值缓存，说明数据库里也没有
            if (cached.isEmpty()) {
                return null;
            }
            return jsonMapper.readValue(cached, CollectInfo.class);
        }
        // 2. 未命中查库
        CollectInfo info = collectInfoMapper.selectById(id);
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
    public Long getCount(Long foodsId) {
        String key = COUNT_KEY_PREFIX + foodsId;
        try {
            // 1. 查缓存，空串表示 0
            String cached = stringRedisTemplate.opsForValue().get(key);
            if (cached != null) {
                return cached.isEmpty() ? 0L : Long.valueOf(cached);
            }
            // 2. 未命中查库
            Long count = collectInfoMapper.selectCount(
                    new LambdaQueryWrapper<CollectInfo>().eq(CollectInfo::getFoodsId, foodsId));
            if (count == null) {
                count = 0L;
            }
            // 3. 回写缓存
            if (count != 0) {
                int expire = 30 + new Random().nextInt(10);
                stringRedisTemplate.opsForValue().set(key, count.toString(), Duration.ofMinutes(expire));
            } else {
                stringRedisTemplate.opsForValue().set(key, "", Duration.ofMinutes(5));
            }
            return count;
        } catch (Exception e) {
            // Redis 挂了就回源数据库，缓存不能拖垮主流程
            log.warn("读取收藏数缓存失败, 回源数据库, foodsId={}", foodsId, e);
            return collectInfoMapper.selectCount(
                    new LambdaQueryWrapper<CollectInfo>().eq(CollectInfo::getFoodsId, foodsId));
        }
    }

    @Override
    public Map<Long, Long> getCounts(List<Long> foodsIds) {
        Map<Long, Long> result = new LinkedHashMap<>();
        if (foodsIds == null || foodsIds.isEmpty()) {
            return result;
        }
        for (Long foodsId : foodsIds) {
            result.put(foodsId, getCount(foodsId));
        }
        return result;
    }

    @Override
    public List<CollectInfo> findAll() {
        return collectInfoMapper.selectList(null);
    }

    @Override
    public CollectInfo findByName(String name) {
        LambdaQueryWrapper<CollectInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            wrapper.eq(CollectInfo::getName, name);
        }
        return collectInfoMapper.selectOne(wrapper);
    }

    @Override
    public Page<CollectInfo> findPage(String name, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<CollectInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name) && !name.equals("null")) {
            wrapper.like(CollectInfo::getName, name);
        }
        return collectInfoMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public boolean check(Long foodsId, Long userId) {
        return collectInfoMapper.selectCount(
                new LambdaQueryWrapper<CollectInfo>()
                        .eq(CollectInfo::getFoodsId, foodsId)
                        .eq(CollectInfo::getUserId, userId)
        ) > 0;
    }
}
