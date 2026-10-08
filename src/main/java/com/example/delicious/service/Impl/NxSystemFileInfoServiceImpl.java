package com.example.delicious.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.delicious.entity.NxSystemFileInfo;
import com.example.delicious.mapper.NxSystemFileInfoMapper;
import com.example.delicious.service.NxSystemFileInfoService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.List;
import java.util.Random;

@Service
public class NxSystemFileInfoServiceImpl extends ServiceImpl<NxSystemFileInfoMapper, NxSystemFileInfo> implements NxSystemFileInfoService {
    /** 文件信息缓存 key 前缀：delicious:file:{id} */
    private static final String CACHE_KEY_PREFIX = "delicious:file:";

    @Resource
    private NxSystemFileInfoMapper nxSystemFileInfoMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private JsonMapper jsonMapper;

    @Override
    public NxSystemFileInfo add(NxSystemFileInfo info) {
        nxSystemFileInfoMapper.insert(info);
        return info;
    }

    @Override
    public void delete(Long id) {
        nxSystemFileInfoMapper.deleteById(id);
        // 删文件信息缓存
        stringRedisTemplate.delete(CACHE_KEY_PREFIX + id);
    }

    @Override
    public NxSystemFileInfo findById(Long id) {
        String key = CACHE_KEY_PREFIX + id;
        // 1. 查缓存
        String cached = stringRedisTemplate.opsForValue().get(key);
        if (cached != null) {
            // 命中的是空值缓存，说明数据库里也没有
            if (cached.isEmpty()) {
                return null;
            }
            return jsonMapper.readValue(cached, NxSystemFileInfo.class);
        }
        // 2. 未命中查库
        NxSystemFileInfo info = nxSystemFileInfoMapper.selectById(id);
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
    public List<NxSystemFileInfo> findAll() {
        return nxSystemFileInfoMapper.selectList(null);
    }

    @Override
    public Page<NxSystemFileInfo> findPage(Integer pageNum, Integer pageSize) {
        return nxSystemFileInfoMapper.selectPage(new Page<>(pageNum, pageSize),null);
    }
}
