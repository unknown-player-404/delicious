package com.example.delicious.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.delicious.common.exception.CustomException;
import com.example.delicious.entity.PraiseInfo;
import com.example.delicious.mapper.PraiseInfoMapper;
import com.example.delicious.service.PraiseInfoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
public class PraiseInfoServiceImpl extends ServiceImpl<PraiseInfoMapper, PraiseInfo> implements PraiseInfoService {
    /** 点赞数计数器 key 前缀：delicious:praise:count:{foodsId} */
    private static final String COUNT_KEY_PREFIX = "delicious:praise:count:";

    @Resource
    private PraiseInfoMapper praiseInfoMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public PraiseInfo add(PraiseInfo info) {
        if (info.getFoodsId() == null || info.getUserId() == null) {
            throw new CustomException("400", "参数错误");
        }
        Long count = praiseInfoMapper.selectCount(
                new LambdaQueryWrapper<PraiseInfo>()
                        .eq(PraiseInfo::getFoodsId, info.getFoodsId())
                        .eq(PraiseInfo::getUserId, info.getUserId())
        );
        if (count > 0) {
            throw new CustomException("400", "已点赞");
        }
        // 1. 先落库（数据库是唯一真相源）
        praiseInfoMapper.insert(info);
        // 2. 缓存命中才 +1；没命中留给 getCount 从数据库重建
        try {
            String key = COUNT_KEY_PREFIX + info.getFoodsId();
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
                stringRedisTemplate.opsForValue().increment(key);
            }
        } catch (Exception e) {
            log.warn("点赞计数缓存更新失败, foodsId={}", info.getFoodsId(), e);
        }
        return info;
    }

    @Override
    public void delete(Long id) {
        //先拿到info
        PraiseInfo info = praiseInfoMapper.selectById(id);
        praiseInfoMapper.deleteById(id);
        //判空
        if (info==null){
            return;
        }
        try {
            String key = COUNT_KEY_PREFIX + info.getFoodsId();
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
                // 计数器 -1
                Long value = stringRedisTemplate.opsForValue().decrement(key);
                // 出现负数说明缓存和库不一致，删掉让它重建
                if (value != null && value < 0) {
                    stringRedisTemplate.delete(key);
                }
            }
        } catch (Exception e) {
            log.warn("取消点赞计数器缓存更新失败,praiseId={}",id,e);
        }
    }

    @Override
    public void update(PraiseInfo info) {
        praiseInfoMapper.updateById(info);
    }

    @Override
    public Long getCount(Long foodsId) {
        String key = COUNT_KEY_PREFIX + foodsId;
        try {
            // 1. 查缓存，空串表示 0
            String cached = stringRedisTemplate.opsForValue().get(key);
            if (cached != null) {
                return cached.isEmpty() ? 0L : Long.parseLong(cached);
            }
            // 2. 未命中查库
            Long count = praiseInfoMapper.selectCount(
                    new LambdaQueryWrapper<PraiseInfo>().eq(PraiseInfo::getFoodsId, foodsId));
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
            log.warn("读取点赞数缓存失败, 回源数据库, foodsId={}", foodsId, e);
            return praiseInfoMapper.selectCount(
                    new LambdaQueryWrapper<PraiseInfo>().eq(PraiseInfo::getFoodsId, foodsId));
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
    public PraiseInfo findById(Long id) {
        return praiseInfoMapper.selectById(id);
    }

    @Override
    public List<PraiseInfo> findAll() {
        return praiseInfoMapper.selectList(null);
    }

    @Override
    public PraiseInfo findByName(String name) {
        LambdaQueryWrapper<PraiseInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            wrapper.eq(PraiseInfo::getName, name);
        }
        return praiseInfoMapper.selectOne(wrapper);
    }

    @Override
    public Page<PraiseInfo> findPage(String name, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<PraiseInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name) && !name.equals("null")) {
            wrapper.like(PraiseInfo::getName, name);
        }
        return praiseInfoMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public Boolean check(Long foodsId, Long userId) {
        return praiseInfoMapper.selectCount(
                new LambdaQueryWrapper<PraiseInfo>()
                        .eq(PraiseInfo::getFoodsId, foodsId)
                        .eq(PraiseInfo::getUserId, userId)
        ) > 0;
    }
}
