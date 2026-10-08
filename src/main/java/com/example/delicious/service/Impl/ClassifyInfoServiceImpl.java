package com.example.delicious.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.delicious.common.exception.CustomException;
import com.example.delicious.entity.ClassifyInfo;
import com.example.delicious.mapper.ClassifyInfoMapper;
import com.example.delicious.service.ClassifyInfoService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.List;
import java.util.Random;

@Service
public class ClassifyInfoServiceImpl extends ServiceImpl<ClassifyInfoMapper, ClassifyInfo> implements ClassifyInfoService {
    /** 详情缓存 key 前缀：delicious:classify:{id} */
    private static final String CACHE_KEY_PREFIX = "delicious:classify:";

    @Resource
    private ClassifyInfoMapper classifyInfoMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private JsonMapper jsonMapper;

    @Override
    public ClassifyInfo add(ClassifyInfo info) {
        Long count = classifyInfoMapper.selectCount(
                new LambdaQueryWrapper<ClassifyInfo>().eq(ClassifyInfo::getName, info.getName())
        );
        if (count > 0) {
            throw new CustomException("400", "菜品分类已存在");
        }
        classifyInfoMapper.insert(info);
        return info;
    }

    @Override
    public void delete(Long id) {
        classifyInfoMapper.deleteById(id);
        // 删详情缓存
        stringRedisTemplate.delete(CACHE_KEY_PREFIX + id);
    }

    @Override
    public void update(ClassifyInfo info) {
        classifyInfoMapper.updateById(info);
        // 删详情缓存
        stringRedisTemplate.delete(CACHE_KEY_PREFIX + info.getId());
    }

    @Override
    public ClassifyInfo findById(Long id) {
        String key = CACHE_KEY_PREFIX + id;
        // 1. 查缓存
        String cached = stringRedisTemplate.opsForValue().get(key);
        if (cached != null) {
            // 命中的是空值缓存，说明数据库里也没有
            if (cached.isEmpty()) {
                return null;
            }
            return jsonMapper.readValue(cached, ClassifyInfo.class);
        }
        // 2. 未命中查库
        ClassifyInfo info = classifyInfoMapper.selectById(id);
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
    public List<ClassifyInfo> findAll() {
        return classifyInfoMapper.selectList(null);
    }

    @Override
    public ClassifyInfo findByName(String name) {
        LambdaQueryWrapper<ClassifyInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            wrapper.eq(ClassifyInfo::getName, name);
        }
        return classifyInfoMapper.selectOne(wrapper);
    }

    @Override
    public Page<ClassifyInfo> findPage(String name, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<ClassifyInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name) && !name.equals("null")) {
            wrapper.like(ClassifyInfo::getName, name);
        }
        return classifyInfoMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }
}
